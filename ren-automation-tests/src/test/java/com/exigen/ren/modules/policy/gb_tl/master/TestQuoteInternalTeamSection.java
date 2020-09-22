package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.admin.modules.security.group.GroupContext;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.*;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.INTERNAL_TEAM;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.InternalTeamMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteInternalTeamSection extends BaseTest implements ProfileContext, GroupContext, CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    private final static ImmutableList<String> SALES_REPRESENTATIVE_USERS = ImmutableList.of(USER_10001, USER_2, USER_3);
    private final static ImmutableList<String> SALES_SUPPORT_ASSOCIATE_USERS = ImmutableList.of(USER_10004, USER_5, USER_6);
    private final static ImmutableList<String> UNDERWRITER_USERS = ImmutableList.of(USER_7, USER_8, USER_9, USER_13);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-14356", "REN-14792"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteInternalTeamSection() {
        TestData tdSpecificDefault = tdSpecific().getTestData("TestData_User_With_Three_Groups");
        adminApp().open();

        // Active EIS user with 'Policy Sales & Service - Basic (Sales Reps)', 'Sales Support Associate', 'Underwriter' groups
        profileCorporate.create(tdSpecific().getTestData("TestData_User_With_Three_Groups"));
        String eisUserFirstName = tdSpecificDefault.getTestData("GeneralProfileTab").getValue("First Name");
        String eisUserLastName = tdSpecificDefault.getTestData("GeneralProfileTab").getValue("Last Name");
        String eisUserLogin = tdSpecificDefault.getTestData("GeneralProfileTab").getValue("User Login");
        String eisUserPassword = tdSpecificDefault.getTestData("GeneralProfileTab").getValue("Password");
        String eisFName_LName = String.format("%s %s", eisUserFirstName, eisUserLastName);


        // User with 'Policy Sales & Service - Basic (Sales Reps)', 'Sales Support Associate', 'Underwriter' groups
        mainApp().reopen(eisUserLogin, eisUserPassword);
        createDefaultNonIndividualCustomer();
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        customerNonIndividual.setSalesRep(eisUserFirstName, eisUserLastName);
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        AssetList internalTeamAssetList = policyInformationTab.getAssetList().getAsset(INTERNAL_TEAM);

        assertSoftly(softly -> {
            LOGGER.info("REN-14356");
            ImmutableList.of(SALES_REPRESENTATIVE, SALES_SUPPORT_ASSOCIATE, UNDERWRITER).forEach(control ->
                    softly.assertThat(internalTeamAssetList.getAsset(control)).isPresent().isEnabled().isRequired().containsOption(eisFName_LName));
            SALES_SUPPORT_ASSOCIATE_USERS.forEach(salesSupportUser -> {
                softly.assertThat(internalTeamAssetList.getAsset(SALES_REPRESENTATIVE)).doesNotContainOption(salesSupportUser);
                softly.assertThat(internalTeamAssetList.getAsset(UNDERWRITER)).doesNotContainOption(salesSupportUser);
            });
            SALES_REPRESENTATIVE_USERS.forEach(salesRepUser -> {
                softly.assertThat(internalTeamAssetList.getAsset(SALES_SUPPORT_ASSOCIATE)).doesNotContainOption(salesRepUser);
                softly.assertThat(internalTeamAssetList.getAsset(UNDERWRITER)).doesNotContainOption(salesRepUser);
            });
            UNDERWRITER_USERS.forEach(underwriterUser -> {
                softly.assertThat(internalTeamAssetList.getAsset(SALES_REPRESENTATIVE)).doesNotContainOption(underwriterUser);
                softly.assertThat(internalTeamAssetList.getAsset(SALES_SUPPORT_ASSOCIATE)).doesNotContainOption(underwriterUser);
            });

        });
        LOGGER.info("REN-14792: Step 1");
        assertThat(internalTeamAssetList.getAsset(SALES_REPRESENTATIVE)).hasValue(eisFName_LName)
                .containsAllOptions(USER_QA_QA, eisFName_LName);
        policyInformationTab.getAssetList().fill(getDefaultTLMasterPolicyData()
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), INTERNAL_TEAM.getLabel())));
        Tab.buttonSaveAndExit.click();
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        adminApp().reopen();
        group.deleteUsersFromGroup("Policy Sales & Service - Basic (Sales Reps)", ImmutableList.of(String.format("%s, %s", eisUserLastName, eisUserFirstName)));
        mainApp().reopen(eisUserLogin, eisUserPassword);
        MainPage.QuickSearch.search(quoteNumber);
        termLifeInsuranceMasterPolicy.dataGather().start();
        assertThat(internalTeamAssetList.getAsset(SALES_REPRESENTATIVE)).hasValue(eisFName_LName)
                .containsAllOptions(USER_QA_QA, eisFName_LName);
        termLifeInsuranceMasterPolicy.dataGather().submit();

        LOGGER.info("REN-14792: Step 2");
        MainPage.QuickSearch.search(customerNumber);
        customerNonIndividual.setSalesRep(USER_QA_QA_FIRST_NAME, USER_QA_QA_LAST_NAME);
        mainApp().reopen(eisUserLogin, eisUserPassword);
        MainPage.QuickSearch.search(quoteNumber);
        termLifeInsuranceMasterPolicy.dataGather().start();
        internalTeamAssetList.getAsset(SALES_REPRESENTATIVE).setValue(USER_QA_QA);
        Tab.buttonTopSave.click();
        assertThat(internalTeamAssetList.getAsset(SALES_REPRESENTATIVE)).doesNotContainOption(eisFName_LName);

        LOGGER.info("REN-14792: Step 3");
        assertThat(internalTeamAssetList.getAsset(UNDERWRITER)).hasValue(eisFName_LName)
                .containsAllOptions(USER_QA_QA, eisFName_LName);

        LOGGER.info("REN-14792: Step 4");
        mainApp().reopen();
        MainPage.QuickSearch.search(customerNumber);
        customerNonIndividual.setSalesRep(USER_ISBA_FIRST_NAME, USER_ISBA_LAST_NAME);

        LOGGER.info("REN-14792: Step 5");
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        assertThat(internalTeamAssetList.getAsset(SALES_REPRESENTATIVE)).hasValue("")
                .containsAllOptions(USER_QA_QA);

        LOGGER.info("REN-14792: Step 6");
        assertThat(internalTeamAssetList.getAsset(UNDERWRITER)).hasValue(USER_ISBA);
        internalTeamAssetList.getAsset(UNDERWRITER).setValue(USER_ISBA);

        LOGGER.info("REN-14792: Step 7");
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), premiumSummaryTab.getClass(), true);
        premiumSummaryTab.submitTab();
        termLifeInsuranceMasterPolicy.propose().perform(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.acceptContract().perform(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.issue().perform(getDefaultTLMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
