package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.billing.setup_billing_groups.tabs.BillingAccountSetupTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.IssueActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationIssueActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.SELECT_ACTION;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestSTATIssueAction extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23432"}, component = POLICY_GROUPBENEFITS)
    public void testValidateEIN() {
        LOGGER.info("REN-23432 TC1 Preconditions");

        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData().adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), GeneralTabMetaData.EIN.getLabel()), StringUtils.EMPTY));
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        statutoryDisabilityInsuranceMasterPolicy.createQuote(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.propose().perform(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.acceptContract().perform(getDefaultSTMasterPolicyData());

        LOGGER.info("REN-23432 TC1 Step 3");

        NavigationPage.setActionAndGo("Issue");
        assertThat(Page.dialogConfirmation.labelMessage).hasValue("EIN is mandatory for the Group Sponsor customer record. Please complete EIN before continuing with Master Quote issue.");
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23436", "REN-23438"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureIssueActionCountyCode() {
        LOGGER.info("REN-23436 TC1 Preconditions");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());

        assertSoftly(softly -> {

            LOGGER.info("REN-23438 TC1 Step 1 Step 2");

            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.ZIP_CODE)).isRequired().hasValue("90210");

            LOGGER.info("REN-23436 TC1 Step 2 and TC1 Step 3");

            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("NY");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(COUNTY_CODE)).isPresent().isOptional().hasValue(StringUtils.EMPTY).isEnabled();

            LOGGER.info("REN-23436 TC1 Step 4");

            policyInformationTab.getAssetList().getAsset(COUNTY_CODE).setValue("003 - Allegany County");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(COUNTY_CODE)).hasNoWarning();

            LOGGER.info("Test REN-23436 TC1 Step 6");

            statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultSTMasterPolicyData().adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), COUNTY_CODE.getLabel()), StringUtils.EMPTY), policyInformationTab.getClass(), premiumSummaryTab.getClass(), true);
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            LOGGER.info("Test REN-23436 TC1 Step 9");

            statutoryDisabilityInsuranceMasterPolicy.propose().perform(getDefaultSTMasterPolicyData());
            statutoryDisabilityInsuranceMasterPolicy.acceptContract().start();
            softly.assertThat(statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class).getAssetList().getAsset(COUNTY_CODE)).isOptional().hasValue(StringUtils.EMPTY);

            LOGGER.info("Test REN-23436 TC1 Step 11");

            statutoryDisabilityInsuranceMasterPolicy.acceptContract().getWorkspace().fill(getDefaultSTMasterPolicyData());
            PremiumSummaryBindActionTab.buttonNext.click();
            statutoryDisabilityInsuranceMasterPolicy.issue().start();
            softly.assertThat(statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class).getAssetList().getAsset(COUNTY_CODE)).isRequired().hasValue(StringUtils.EMPTY);

            LOGGER.info("Test REN-23436 Step TC1 14");
            policyInformationTab.navigateToTab();
            statutoryDisabilityInsuranceMasterPolicy.issue().getWorkspace().fillFromTo(getDefaultSTMasterPolicyData()
                    .adjust(TestData.makeKeyPath(PolicyInformationIssueActionTab.class.getSimpleName(), PolicyInformationIssueActionTabMetaData.COUNTY_CODE.getLabel()), "005 - Bronx County"), PolicyInformationIssueActionTab.class, IssueActionTab.class, true);
            statutoryDisabilityInsuranceMasterPolicy.issue().getWorkspace().getTab(IssueActionTab.class).submitTab();
            billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(SELECT_ACTION).setValue("Create New Account");
            BillingAccountSetupTab.saveTab();
            BillingAccountSetupTab.buttonFinish.click();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        });
    }
}
