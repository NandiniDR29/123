package com.exigen.ren.modules.policy.gb_di_std.master;

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
import com.exigen.ren.main.modules.policy.common.tabs.master.IssueActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.SELECT_ACTION;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestSTDConfigureIssueAction extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21826"}, component = POLICY_GROUPBENEFITS)
    public void testIssueActionEIN() {

        LOGGER.info("REN-21826 TC1 Preconditions");

        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData().adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), GeneralTabMetaData.EIN.getLabel()), StringUtils.EMPTY));
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        shortTermDisabilityMasterPolicy.createQuote(getDefaultSTDMasterPolicyData());
        shortTermDisabilityMasterPolicy.propose().perform(getDefaultSTDMasterPolicyData());
        shortTermDisabilityMasterPolicy.acceptContract().perform(getDefaultSTDMasterPolicyData());

        LOGGER.info("REN-21826 TC1 Step 3");

        NavigationPage.setActionAndGo("Issue");
        assertThat(Page.dialogConfirmation.labelMessage).hasValue("EIN is mandatory for the Group Sponsor customer record. Please complete EIN before continuing with Master Quote issue.");
        Page.dialogConfirmation.reject();
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21913"}, component = POLICY_GROUPBENEFITS)
    public void testIssueActionCountyCode() {
        LOGGER.info("REN-21913 TC1 Preconditions");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.initiate(getDefaultSTDMasterPolicyData());
        shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTDMasterPolicyData(), policyInformationTab.getClass());

        assertSoftly(softly -> {

            LOGGER.info("REN-21913 TC1 Step 1 Step 3");

            softly.assertThat(policyInformationTab.getAssetList().getAsset(COUNTY_CODE)).isPresent().isOptional().hasValue(StringUtils.EMPTY).isEnabled();

            LOGGER.info("REN-21913 TC1 Step 4");

            policyInformationTab.getAssetList().getAsset(COUNTY_CODE).setValue("003 - Allegany County");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(COUNTY_CODE)).hasNoWarning();

            LOGGER.info("REN-21913 TC1 Step 6");

            shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultSTDMasterPolicyData().adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), COUNTY_CODE.getLabel()), StringUtils.EMPTY), policyInformationTab.getClass(), premiumSummaryTab.getClass(), true);
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            LOGGER.info("REN-21913 TC1 Step 9");

            shortTermDisabilityMasterPolicy.propose().perform(getDefaultSTDMasterPolicyData());
            shortTermDisabilityMasterPolicy.acceptContract().start();
            softly.assertThat(shortTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class).getAssetList().getAsset(COUNTY_CODE)).isOptional().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-21913 TC1 Step 11");

            shortTermDisabilityMasterPolicy.acceptContract().getWorkspace().fill(getDefaultSTDMasterPolicyData());
            PremiumSummaryBindActionTab.buttonNext.click();
            shortTermDisabilityMasterPolicy.issue().start();
            softly.assertThat(shortTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class).getAssetList().getAsset(COUNTY_CODE)).isRequired().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-21913 TC1 Step 14");

            policyInformationTab.getAssetList().getAsset(COUNTY_CODE).setValue("005 - Bronx County");
            shortTermDisabilityMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultSTDMasterPolicyData(), IssueActionTab.class, true);
            IssueActionTab.buttonPurchase.click();
            Page.dialogConfirmation.confirm();
            billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(SELECT_ACTION).setValue("Create New Account");
            BillingAccountSetupTab.saveTab();
            BillingAccountSetupTab.buttonFinish.click();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        });
    }
}
