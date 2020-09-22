package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationBindActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationBindActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.*;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationBindActionTabMetaData.AMOUNT_RECEIVED_WITH_APPLICATION;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.InternalTeamMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteInternalTeamSection extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-14620", "REN-14621"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteInternalTeamSection() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        customerNonIndividual.setSalesRep(USER_10001_FIRST_NAME, USER_10001_LAST_NAME);
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        AbstractContainer<?, ?> policyAssetList = policyInformationTab.getAssetList();
        AssetList internalTeamAssetList = policyAssetList.getAsset(INTERNAL_TEAM);
        assertSoftly(softly -> {
            LOGGER.info("REN-14620: Steps: 1-5");
            ImmutableList.of(SALES_REPRESENTATIVE, UNDERWRITER).forEach(control ->
                    softly.assertThat(internalTeamAssetList.getAsset(control)).isPresent().isEnabled().isRequired());
            softly.assertThat(internalTeamAssetList.getAsset(SALES_SUPPORT_ASSOCIATE)).isPresent().isEnabled();

            softly.assertThat(internalTeamAssetList.getAsset(SALES_REPRESENTATIVE)).hasValue(USER_10001).containsAllOptions(SALES_REPRESENTATIVE_USERS);
            softly.assertThat(internalTeamAssetList.getAsset(SALES_SUPPORT_ASSOCIATE)).hasValue("").containsAllOptions(SALES_SUPPORT_ASSOCIATE_USERS);
            softly.assertThat(internalTeamAssetList.getAsset(UNDERWRITER)).hasValue("").containsAllOptions(UNDERWRITER_USERS);
            softly.assertThat(policyAssetList.getAsset(UNDEWRITING_COMPANY)).isPresent().isEnabled().isRequired();

            LOGGER.info("REN-14621: Steps: 1-6");
            policyAssetList.getAsset(SITUS_STATE).setValue("NY");
            verifyInternalTeamSection(internalTeamAssetList, softly);

            LOGGER.info("REN-14621: Steps: 7-8");
            softly.assertThat(policyAssetList.getAsset(UNDEWRITING_COMPANY)).isEnabled()
                    .hasValue("Renaissance Life & Health Insurance Company of New York");

            LOGGER.info("REN-14621: Steps: 9-11");
            policyAssetList.getAsset(SITUS_STATE).setValue("NJ");
            softly.assertThat(policyAssetList.getAsset(UNDEWRITING_COMPANY))
                    .hasValue("Renaissance Life & Health Insurance Company of America");
            verifyInternalTeamSection(internalTeamAssetList, softly);
            policyInformationTab.getAssetList().fill(getDefaultSTMasterPolicyData()
                    .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), INTERNAL_TEAM.getLabel())));
            PolicyInformationTab.buttonSaveAndExit.click();
           navigateToCustomer();
            customerNonIndividual.setSalesRep(USER_10004_FIRST_NAME, USER_10004_LAST_NAME);
            statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
            softly.assertThat(internalTeamAssetList.getAsset(SALES_REPRESENTATIVE)).hasValue("");
            softly.assertThat(internalTeamAssetList.getAsset(SALES_SUPPORT_ASSOCIATE)).hasValue("");
            softly.assertThat(internalTeamAssetList.getAsset(UNDERWRITER)).hasValue("");

            LOGGER.info("REN-14621: Steps: 12");
            statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultSTMasterPolicyData(),
                    policyInformationTab.getClass(), premiumSummaryTab.getClass(), true);
            premiumSummaryTab.submitTab();
            statutoryDisabilityInsuranceMasterPolicy.propose().perform(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY));
            statutoryDisabilityInsuranceMasterPolicy.acceptContract().start();
            AbstractContainer<?, ?> policyInfBindActionAssetList = statutoryDisabilityInsuranceMasterPolicy.acceptContract().getWorkspace().getTab(PolicyInformationBindActionTab.class).getAssetList();
            assertThat(policyInfBindActionAssetList.getAsset(PolicyInformationBindActionTabMetaData.UNDEWRITING_COMPANY)).isDisabled();
            assertThat(policyInfBindActionAssetList.getAsset(AMOUNT_RECEIVED_WITH_APPLICATION)).isPresent().isOptional().hasValue(EMPTY);
            statutoryDisabilityInsuranceMasterPolicy.acceptContract().getWorkspace()
                    .fill(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY));
            statutoryDisabilityInsuranceMasterPolicy.acceptContract().submit();
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

            statutoryDisabilityInsuranceMasterPolicy.issue().start();
            assertThat(statutoryDisabilityInsuranceMasterPolicy.issue().getWorkspace().getTab(PolicyInformationIssueActionTab.class)
                    .getAssetList().getAsset(PolicyInformationIssueActionTabMetaData.UNDEWRITING_COMPANY)).isDisabled();
        });
    }

    private void verifyInternalTeamSection(AssetList internalTeamAssetList, ETCSCoreSoftAssertions softly) {
        ImmutableList.of(SALES_REPRESENTATIVE, UNDERWRITER).forEach(control ->
                softly.assertThat(internalTeamAssetList.getAsset(control)).isPresent().isEnabled().isRequired());
        softly.assertThat(internalTeamAssetList.getAsset(SALES_SUPPORT_ASSOCIATE)).isPresent().isEnabled();

        softly.assertThat(internalTeamAssetList.getAsset(SALES_REPRESENTATIVE)).hasValue(USER_10001).containsAllOptions(SALES_REPRESENTATIVE_USERS);
        softly.assertThat(internalTeamAssetList.getAsset(SALES_SUPPORT_ASSOCIATE)).hasValue("").containsAllOptions(SALES_SUPPORT_ASSOCIATE_USERS);
        softly.assertThat(internalTeamAssetList.getAsset(UNDERWRITER)).hasValue("").containsAllOptions(UNDERWRITER_USERS);
        checkInternalSectionDropdownValues(internalTeamAssetList, softly);
    }

    private void checkInternalSectionDropdownValues(AssetList internalTeamAssetList, ETCSCoreSoftAssertions softly) {
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
    }
}
