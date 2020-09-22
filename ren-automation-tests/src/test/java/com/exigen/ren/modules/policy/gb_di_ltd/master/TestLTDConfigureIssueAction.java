package com.exigen.ren.modules.policy.gb_di_ltd.master;

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
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.SELECT_ACTION;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestLTDConfigureIssueAction extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21918"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureIssueActionCountyCode() {

        LOGGER.info("REN-21918 TC1 Preconditions");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), policyInformationTab.getClass());

        assertSoftly(softly -> {

            LOGGER.info("REN-21918 TC1 Step 1 Step 3");

            softly.assertThat(policyInformationTab.getAssetList().getAsset(COUNTY_CODE)).isPresent().isOptional().hasValue(StringUtils.EMPTY).isEnabled();

            LOGGER.info("REN-21918 TC1 Step 1 Step 4");

            policyInformationTab.getAssetList().getAsset(COUNTY_CODE).setValue("003 - Allegany County");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(COUNTY_CODE)).hasNoWarning();

            LOGGER.info("REN-21918 TC1 Step 6");

            longTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultLTDMasterPolicyData().adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), COUNTY_CODE.getLabel()), StringUtils.EMPTY), policyInformationTab.getClass(), premiumSummaryTab.getClass(), true);
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            LOGGER.info("REN-21918 TC1 Step 9");

            longTermDisabilityMasterPolicy.propose().perform(getDefaultLTDMasterPolicyData());
            longTermDisabilityMasterPolicy.acceptContract().start();
            softly.assertThat(longTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class).getAssetList().getAsset(COUNTY_CODE)).isOptional().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-21918 TC1 Step 11");

            longTermDisabilityMasterPolicy.acceptContract().getWorkspace().fill(getDefaultLTDMasterPolicyData());
            PremiumSummaryBindActionTab.buttonNext.click();
            longTermDisabilityMasterPolicy.issue().start();
            softly.assertThat(longTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class).getAssetList().getAsset(COUNTY_CODE)).isRequired().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-21918 TC1 Step 14");

            policyInformationTab.getAssetList().getAsset(COUNTY_CODE).setValue("005 - Bronx County");
            longTermDisabilityMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), IssueActionTab.class, true);
            IssueActionTab.buttonPurchase.click();
            Page.dialogConfirmation.confirm();
            billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(SELECT_ACTION).setValue("Create New Account");
            BillingAccountSetupTab.saveTab();
            BillingAccountSetupTab.buttonFinish.click();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-22985"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureIssueActionEIN() {

        LOGGER.info("REN-22985 TC1 Preconditions");

        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData().adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), GeneralTabMetaData.EIN.getLabel()), StringUtils.EMPTY));
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.createQuote(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.propose().perform(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.acceptContract().perform(getDefaultLTDMasterPolicyData());

        LOGGER.info("REN-22985 TC1 Step 3");

        NavigationPage.setActionAndGo("Issue");
        assertThat(Page.dialogConfirmation.labelMessage).hasValue("EIN is mandatory for the Group Sponsor customer record. Please complete EIN before continuing with Master Quote issue.");
        Page.dialogConfirmation.reject();
    }
}
