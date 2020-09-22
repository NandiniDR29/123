package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.master.PremiumSummaryBindActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PremiumSummaryIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.PlanDefinitionIssueActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.*;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PremiumSummaryTabMetaData.ASO_FEE_BASIS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAttributesVerificationOnPremiumSummaryTab extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {
    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23381"}, component = POLICY_GROUPBENEFITS)
    public void testAttributesVerificationOnPremiumSummaryTab() {

        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());

        LOGGER.info("REN-23381 Step#1 verification");
        quoteInitiateAndFillUpToTab(getDefaultVSMasterPolicyData(), PremiumSummaryTab.class, false);
        assertThat(PremiumSummaryTab.premiumSummaryASOFeeTable).isAbsent();
        assertThat(premiumSummaryTab.getAssetList().getAsset(ASO_FEE_BASIS.getLabel())).isAbsent();

        LOGGER.info("REN-23381 Steps#3, 4, 5, 8 verification");
        policyInformationTab.navigateToTab();
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.ASO_PLAN.getLabel()), VALUE_YES)
                .adjust(tdSpecific().getTestData("TestDataForASOPlan").resolveLinks()).resolveLinks(), PremiumSummaryTab.class, false);
        assertThat(PremiumSummaryTab.premiumSummaryASOFeeTable).isPresent();
        assertThat(premiumSummaryTab.getAssetList().getAsset(ASO_FEE_BASIS))
                .isPresent().isRequired().isEnabled().hasValue("Per Employee Per Month").hasOptions("Per Employee Per Month");

        LOGGER.info("REN-23381 Step#9 verification");
        Tab.buttonSaveAndExit.click();
        groupVisionMasterPolicy.declineByCompanyQuote().perform((groupVisionMasterPolicy.getDefaultTestData(TestDataKey.DECLINE_BY_COMPANY, TestDataKey.DEFAULT_TEST_DATA_KEY)));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(COMPANY_DECLINED);

        LOGGER.info("REN-23381 Step#10, 11 verification");
        asoFeeBasesVerificationAfterQuoteDecline("50");

        LOGGER.info("REN-23381 Step#12 verification");
        Tab.buttonSaveAndExit.click();
        groupVisionMasterPolicy.declineByCustomerQuote().perform((groupVisionMasterPolicy.getDefaultTestData(TestDataKey.DECLINE_BY_CUSTOMER, TestDataKey.DEFAULT_TEST_DATA_KEY)));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(CUSTOMER_DECLINED);

        LOGGER.info("REN-23381 Step#13, 14 verification");
        asoFeeBasesVerificationAfterQuoteDecline("51");

        LOGGER.info("REN-23381 Step#18 verification");
        premiumSummaryTab.submitTab();

        groupVisionMasterPolicy.propose().perform(getDefaultVSMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestDataPropose").resolveLinks()).resolveLinks());
        groupVisionMasterPolicy.acceptContract().start();
        groupVisionMasterPolicy.acceptContract().getWorkspace().getTab(PremiumSummaryBindActionTab.class).navigateToTab();
        assertThat(groupVisionMasterPolicy.acceptContract().getWorkspace().getTab(PremiumSummaryBindActionTab.class).getAssetList()
                .getAsset(PremiumSummaryBindActionTabMetaData.ASO_FEE_BASIS)).isPresent().isDisabled();

        LOGGER.info("REN-23381 Step#22 verification");
        premiumSummaryTab.navigateToTab();
        PremiumSummaryBindActionTab.buttonNext.click();

        groupVisionMasterPolicy.issue().start();
        AbstractContainer<?, ?> issuePolicyInfoAssetList = groupVisionMasterPolicy.issue().getWorkspace().getTab(PremiumSummaryIssueActionTab.class).getAssetList();
        groupVisionMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(PlanDefinitionIssueActionTab.class.getSimpleName(), "PlanKey"), "ASO A La Carte-ASO A La Carte").resolveLinks(), PremiumSummaryIssueActionTab.class);
        assertThat(issuePolicyInfoAssetList.getAsset(PremiumSummaryIssueActionTabMetaData.ASO_FEE_BASIS)).isPresent().isDisabled();

        LOGGER.info("REN-23381 Step#23 verification");
        groupVisionMasterPolicy.issue().getWorkspace().fillFrom(getDefaultVSMasterPolicyData(), PremiumSummaryIssueActionTab.class);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
    }

    private void asoFeeBasesVerificationAfterQuoteDecline(String totalNumberValue) {
        groupVisionMasterPolicy.dataGather().start();
        premiumSummaryTab.navigateToTab();
        assertThat(premiumSummaryTab.getAssetList().getAsset(ASO_FEE_BASIS.getLabel())).isPresent().isEnabled();

        policyInformationTab.navigateToTab();
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.POLICY_TOTAL_NUMBER_ELIGIBLE_LIVES).setValue(totalNumberValue);
        assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.DATA_GATHERING);
        premiumSummaryTab.navigateToTab();
        assertThat(premiumSummaryTab.getAssetList().getAsset(ASO_FEE_BASIS.getLabel())).isPresent().isEnabled();
    }
}
