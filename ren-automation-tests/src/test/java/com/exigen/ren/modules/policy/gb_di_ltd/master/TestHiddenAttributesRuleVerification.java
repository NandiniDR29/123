package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestHiddenAttributesRuleVerification extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-35948"}, component = POLICY_GROUPBENEFITS)
    public void testHiddenAttributesRuleVerification_LTD() {
        LOGGER.info("General Preconditions");
        String planName = longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "SelectPlan").getValue(PlanDefinitionTabMetaData.PLAN.getLabel());

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), PremiumSummaryTab.class, true);

        LOGGER.info("Step#2.1 verification");
        assertThat(PremiumSummaryTab.buttonMaintainInForceRates).isAbsent();

        LOGGER.info("Step#2.2 verification");
        premiumSummaryTab.rate();
        assertThat(PremiumSummaryTab.rateDetailsButton(planName)).isPresent().isEnabled();

        LOGGER.info("Step#2.3 verification");
        premiumSummaryTab.openViewRateDetailsByPlanName(planName);
        assertThat(RateDialogs.ViewRateDetailsDialog.tableRateDetails.getHeader().getValue()).doesNotContain(TableConstants.RateDetailsTable.IN_FORCE_RATE.getName());
        RateDialogs.ViewRateDetailsDialog.close();

        LOGGER.info("Step#4 verification");
        PremiumSummaryTab.buttonSaveAndExit.click();
        longTermDisabilityMasterPolicy.propose().perform(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.acceptContract().start();
        longTermDisabilityMasterPolicy.acceptContract().getWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), PremiumSummaryBindActionTab.class);

        assertThat(PremiumSummaryBindActionTab.buttonMaintainInForceRates).isAbsent();
        PremiumSummaryBindActionTab.buttonViewRateHistory.click();
        assertThat(RateDialogs.ViewRateHistoryDialog.tableRateHistory.getHeader().getValue()).doesNotContain(TableConstants.RateDetailsTable.IN_FORCE_RATE.getName());
        RateDialogs.ViewRateHistoryDialog.close();
        PremiumSummaryBindActionTab.buttonNext.click();

        LOGGER.info("Step#5 verification");
        longTermDisabilityMasterPolicy.issue().start();
        longTermDisabilityMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), PremiumSummaryIssueActionTab.class);
        assertThat(PremiumSummaryIssueActionTab.buttonMaintainInForceRates).isAbsent();

        PremiumSummaryIssueActionTab.buttonViewRateHistory.click();
        assertThat(RateDialogs.ViewRateHistoryDialog.tableRateHistory.getHeader().getValue()).doesNotContain(TableConstants.RateDetailsTable.IN_FORCE_RATE.getName());
        RateDialogs.ViewRateHistoryDialog.close();
        longTermDisabilityMasterPolicy.issue().getWorkspace().fillFrom(getDefaultLTDMasterPolicyData(), PremiumSummaryIssueActionTab.class);

        LOGGER.info("Steps#7, 8 verification");
        longTermDisabilityMasterPolicy.endorse().perform(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        PolicySummaryPage.buttonPendedEndorsement.click();
        longTermDisabilityMasterPolicy.issue().start();
        longTermDisabilityMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), PremiumSummaryIssueActionTab.class);

        PremiumSummaryIssueActionTab.openViewRateDetailsByPlanName(planName);
        assertThat(RateDialogs.ViewRateDetailsDialog.tableRateDetails.getHeader().getValue()).doesNotContain(TableConstants.RateDetailsTable.IN_FORCE_RATE.getName());
    }
}
