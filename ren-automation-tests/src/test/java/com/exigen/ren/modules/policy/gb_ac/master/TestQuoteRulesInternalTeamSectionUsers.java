package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.security.group.GroupContext;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.EXP_USER_2;
import static com.exigen.ren.common.enums.UsersConsts.EXP_USER_3;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.INTERNAL_TEAM;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.InternalTeamMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteRulesInternalTeamSectionUsers extends BaseTest implements ProfileContext, GroupContext, CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    private final static String SALES_REPRESENTATIVE_IS_REQUIRED_ERROR_MESSAGE = "'Sales Representative' is required";


    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-14614", component = POLICY_GROUPBENEFITS)
    public void testQuoteRulesVerificationForUnderwritingCompany() {
        adminApp().open();

        TestData tdSpecific = tdSpecific().getTestData("TestData");

        // Active EIS user – User 1 is part of "Underwriter" group and 'Sales Support Associate' group
        profileCorporate.create(tdSpecific);

        // Active Non-EIS user
        profileVendor.createNonEis(profileVendor.defaultTestData().getTestData("DataGather", "TestData_Non_EIS"));
        String eisUserLogin = tdSpecific.getTestData("GeneralProfileTab").getValue("User Login");
        String eisUserPassw = tdSpecific.getTestData("GeneralProfileTab").getValue("Password");

        mainApp().reopen(eisUserLogin, eisUserPassw);

        createDefaultNonIndividualCustomer();
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        AssetList internalAssetList = policyInformationTab.getAssetList().getAsset(INTERNAL_TEAM);
        String eisUserFirstName = tdSpecific.getTestData("GeneralProfileTab").getValue("First Name");
        String eisUserLastName = tdSpecific.getTestData("GeneralProfileTab").getValue("Last Name");
        String nonEisUserFirstName = profileVendor.defaultTestData().getTestData("DataGather", "TestData_Non_EIS").getTestData("GeneralProfileTab").getValue("First Name");
        String nonEisUserLastName = profileVendor.defaultTestData().getTestData("DataGather", "TestData_Non_EIS").getTestData("GeneralProfileTab").getValue("Last Name");

        String eisFName_LName = String.format("%s %s", eisUserFirstName, eisUserLastName);

        assertSoftly(softly -> {
            LOGGER.info("Step 3");
            softly.assertThat(internalAssetList.getAsset(UNDERWRITER)).hasValue(eisFName_LName);

            LOGGER.info("Step 4");
            softly.assertThat(internalAssetList.getAsset(SALES_SUPPORT_ASSOCIATE))
            // Contains Expired EIS user with id=11002 created through liquibase
            .containsOption(EXP_USER_2)
            // Contains Active EIS user
            .containsOption(eisFName_LName)
            // Does not contain Active Non-EIS user
            .doesNotContainOption(String.format("%s %s", nonEisUserFirstName, nonEisUserLastName));
        });

        LOGGER.info("Step 5");
        fillAndRateQuote(getDefaultACMasterPolicyData());
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        adminApp().reopen();

        // Delete user from 'Policy Sales & Service -Advanced (Underwriting)' group
        group.deleteUsersFromGroup("Policy Sales & Service -Advanced (Underwriting)", ImmutableList.of(String.format("%s, %s", eisUserLastName, eisUserFirstName)));

        LOGGER.info("Step 6");
        mainApp().reopen(eisUserLogin, eisUserPassw);
        MainPage.QuickSearch.search(quoteNumber);
        groupAccidentMasterPolicy.dataGather().start();
        assertSoftly(softly -> {
            softly.assertThat(internalAssetList.getAsset(UNDERWRITER)).hasValue(eisFName_LName);
            softly.assertThat(internalAssetList.getAsset(UNDERWRITER)).containsOption(EXP_USER_2);
        });

        LOGGER.info("Step 7");
        mainApp().reopen(eisUserLogin, eisUserPassw);
        MainPage.QuickSearch.search(customerNumber);
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        assertThat(internalAssetList.getAsset(UNDERWRITER)).hasValue(eisFName_LName);

        LOGGER.info("Step 10");
        internalAssetList.getAsset(UNDERWRITER).setValue(EXP_USER_2);
        fillAndRateQuote(getDefaultACMasterPolicyData());

        LOGGER.info("Step 15");
        initEndorseDataGatherFromRatedQuote();
        assertThat(internalAssetList.getAsset(UNDERWRITER)).hasValue(EXP_USER_2);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-14613", component = POLICY_GROUPBENEFITS)
    public void testQuoteRulesVerificationForSalesRepresentative() {
        adminApp().open();

        TestData tdSpecific = tdSpecific().getTestData("TestData_SalesRepresentatives");

        // Active EIS user – User 1 is part of "Policy Sales & Service - Basic (Sales Reps)" group
        profileCorporate.create(tdSpecific);

        // Active Non-EIS user
        profileVendor.createNonEis(profileVendor.defaultTestData().getTestData("DataGather", "TestData_Non_EIS"));
        String eisUserLogin = tdSpecific.getTestData("GeneralProfileTab").getValue("User Login");
        String eisUserPassw = tdSpecific.getTestData("GeneralProfileTab").getValue("Password");
        String eisUserFirstName = tdSpecific.getTestData("GeneralProfileTab").getValue("First Name");
        String eisUserLastName = tdSpecific.getTestData("GeneralProfileTab").getValue("Last Name");

        mainApp().reopen(eisUserLogin, eisUserPassw);

        createDefaultNonIndividualCustomer();
        customerNonIndividual.setSalesRep(eisUserFirstName, eisUserLastName);
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        AssetList internalAssetList = policyInformationTab.getAssetList().getAsset(INTERNAL_TEAM);
        String nonEisUserFirstName = profileVendor.defaultTestData().getTestData("DataGather", "TestData_Non_EIS").getTestData("GeneralProfileTab").getValue("First Name");
        String nonEisUserLastName = profileVendor.defaultTestData().getTestData("DataGather", "TestData_Non_EIS").getTestData("GeneralProfileTab").getValue("Last Name");

        String eisFName_LName = String.format("%s %s", eisUserFirstName, eisUserLastName);
        assertSoftly(softly -> {
            LOGGER.info("Step 3");
            softly.assertThat(internalAssetList.getAsset(SALES_REPRESENTATIVE)).hasValue(eisFName_LName);

            LOGGER.info("Step 4");
            softly.assertThat(internalAssetList.getAsset(SALES_REPRESENTATIVE))
                    // Contains Expired EIS user with id=11003 created through liquibase
                    .containsOption(EXP_USER_3)
                    // Contains Active EIS user
                    .containsOption(eisFName_LName)
                    // Does not contain Active Non-EIS user
                    .doesNotContainOption(String.format("%s %s", nonEisUserFirstName, nonEisUserLastName));
        });

        LOGGER.info("Step 5");
        fillAndRateQuote(getDefaultACMasterPolicyData().adjust(TestData.makeKeyPath(
                policyInformationTab.getMetaKey(),
                INTERNAL_TEAM.getLabel(),
                SALES_REPRESENTATIVE.getLabel()),
                eisFName_LName));
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        adminApp().reopen();

        // Delete user from 'Policy Sales & Service - Basic (Sales Reps)' group
        group.deleteUsersFromGroup("Policy Sales & Service - Basic (Sales Reps)", ImmutableList.of(String.format("%s, %s", eisUserLastName, eisUserFirstName)));

        LOGGER.info("Step 6");
        mainApp().reopen(eisUserLogin, eisUserPassw);
        MainPage.QuickSearch.search(quoteNumber);
        groupAccidentMasterPolicy.dataGather().start();
        assertSoftly(softly -> {
            softly.assertThat(internalAssetList.getAsset(SALES_REPRESENTATIVE)).hasValue(eisFName_LName);
            softly.assertThat(internalAssetList.getAsset(SALES_REPRESENTATIVE)).containsOption(EXP_USER_3);
        });

        LOGGER.info("Step 7");
        internalAssetList.getAsset(SALES_REPRESENTATIVE).setValue(EXP_USER_3);
        Tab.buttonTopSave.click();
        assertThat(internalAssetList.getAsset(SALES_REPRESENTATIVE)).doesNotContainOption(eisFName_LName);

        LOGGER.info("Step 8");
        mainApp().reopen(eisUserLogin, eisUserPassw);
        MainPage.QuickSearch.search(customerNumber);
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        assertThat(internalAssetList.getAsset(SALES_REPRESENTATIVE)).hasValue("");

        LOGGER.info("Step 9");
        policyInformationTab.getAssetList().fill(getDefaultACMasterPolicyData()
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), INTERNAL_TEAM.getLabel(), SALES_REPRESENTATIVE.getLabel())));
        Tab.buttonNext.click();
        assertThat(internalAssetList.getAsset(SALES_REPRESENTATIVE)).hasWarningWithText(SALES_REPRESENTATIVE_IS_REQUIRED_ERROR_MESSAGE);

        LOGGER.info("Step 10");
        premiumSummaryTab.navigate();
        premiumSummaryTab.rate();
        Row errorRow = ErrorPage.tableError.getRow(ErrorPage.TableError.MESSAGE.getName(), SALES_REPRESENTATIVE_IS_REQUIRED_ERROR_MESSAGE);
        assertThat(errorRow).exists();
        errorRow.getCell(ErrorPage.TableError.CODE.getName()).controls.links.getFirst().click();

        LOGGER.info("Step 11");
        fillAndRateQuote(getDefaultACMasterPolicyData().adjust(TestData.makeKeyPath(
                policyInformationTab.getMetaKey(),
                INTERNAL_TEAM.getLabel(),
                SALES_REPRESENTATIVE.getLabel()),
                EXP_USER_3));

        LOGGER.info("Step 12");
        initEndorseDataGatherFromRatedQuote();
        assertThat(internalAssetList.getAsset(SALES_REPRESENTATIVE)).hasValue(EXP_USER_3);
    }

    private void fillAndRateQuote(TestData testData) {
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(testData, PremiumSummaryTab.class, true);
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
    }

    private void initEndorseDataGatherFromRatedQuote() {
        groupAccidentMasterPolicy.propose().perform(getDefaultACMasterPolicyData().adjust(TestData.makeKeyPath(QuotesSelectionActionTab.class.getSimpleName(), QuotesSelectionActionTab.SELECT_QUOTE_BY_ROW_NUMBER_KEY), "2"));
        groupAccidentMasterPolicy.acceptContract().perform(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.issue().perform(getDefaultACMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        groupAccidentMasterPolicy.endorse().perform(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupAccidentMasterPolicy.dataGather().start();
    }
}
