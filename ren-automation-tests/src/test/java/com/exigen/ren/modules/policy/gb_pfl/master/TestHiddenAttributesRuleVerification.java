package com.exigen.ren.modules.policy.gb_pfl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.PlanPFL.FLB;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestHiddenAttributesRuleVerification extends BaseTest implements CustomerContext, CaseProfileContext, PaidFamilyLeaveMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_PFL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-35948"}, component = POLICY_GROUPBENEFITS)
    public void testHiddenAttributesRuleVerification_PFL() {
        LOGGER.info("General Preconditions");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(paidFamilyLeaveMasterPolicy.getType());
        initiatePFLQuoteAndFillToTab(getDefaultPFLMasterPolicyData(), PremiumSummaryTab.class, true);

        LOGGER.info("Step#2.1 verification");
        assertThat(PremiumSummaryTab.buttonMaintainInForceRates).isAbsent();

        LOGGER.info("Step#2.2 verification");
        premiumSummaryTab.rate();
        assertThat(PremiumSummaryTab.rateDetailsButton(FLB)).isPresent().isEnabled();

        LOGGER.info("Step#2.3 verification");
        premiumSummaryTab.openViewRateDetailsByPlanName(FLB);
        assertThat(RateDialogs.ViewRateDetailsDialog.tableRateDetails.getHeader().getValue()).doesNotContain(TableConstants.RateDetailsTable.IN_FORCE_RATE.getName());
        RateDialogs.ViewRateDetailsDialog.close();

        LOGGER.info("Step#4 verification");
        PremiumSummaryTab.buttonSaveAndExit.click();
        paidFamilyLeaveMasterPolicy.propose().perform(getDefaultPFLMasterPolicyData());
        paidFamilyLeaveMasterPolicy.acceptContract().start();
        paidFamilyLeaveMasterPolicy.acceptContract().getWorkspace().fillUpTo(getDefaultPFLMasterPolicyData(), PremiumSummaryBindActionTab.class);

        assertThat(PremiumSummaryBindActionTab.buttonMaintainInForceRates).isAbsent();
        PremiumSummaryBindActionTab.buttonViewRateHistory.click();
        assertThat(RateDialogs.ViewRateHistoryDialog.tableRateHistory.getHeader().getValue()).doesNotContain(TableConstants.RateDetailsTable.IN_FORCE_RATE.getName());
        RateDialogs.ViewRateHistoryDialog.close();
        PremiumSummaryBindActionTab.buttonNext.click();

        LOGGER.info("Step#5 verification");
        paidFamilyLeaveMasterPolicy.issue().start();
        paidFamilyLeaveMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultPFLMasterPolicyData(), PremiumSummaryIssueActionTab.class);
        assertThat(PremiumSummaryIssueActionTab.buttonMaintainInForceRates).isAbsent();

        PremiumSummaryIssueActionTab.buttonViewRateHistory.click();
        assertThat(RateDialogs.ViewRateHistoryDialog.tableRateHistory.getHeader().getValue()).doesNotContain(TableConstants.RateDetailsTable.IN_FORCE_RATE.getName());
        RateDialogs.ViewRateHistoryDialog.close();
        paidFamilyLeaveMasterPolicy.issue().getWorkspace().fillFrom(getDefaultPFLMasterPolicyData(), PremiumSummaryIssueActionTab.class);

        LOGGER.info("Steps#7, 8 verification");
        paidFamilyLeaveMasterPolicy.endorse().perform(paidFamilyLeaveMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        PolicySummaryPage.buttonPendedEndorsement.click();
        paidFamilyLeaveMasterPolicy.issue().start();
        paidFamilyLeaveMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultPFLMasterPolicyData(), PremiumSummaryIssueActionTab.class);

        PremiumSummaryIssueActionTab.openViewRateDetailsByPlanName(FLB);
        assertThat(RateDialogs.ViewRateDetailsDialog.tableRateDetails.getHeader().getValue()).doesNotContain(TableConstants.RateDetailsTable.IN_FORCE_RATE.getName());
    }
}
