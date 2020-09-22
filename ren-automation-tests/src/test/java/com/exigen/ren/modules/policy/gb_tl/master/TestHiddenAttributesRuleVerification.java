package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.SCHEDULE_OF_CONTINUATION_PROVISION;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.ScheduleOfContinuationProvisionMetaData.SABBATICAL_DURATION;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestHiddenAttributesRuleVerification extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-35948"}, component = POLICY_GROUPBENEFITS)
    public void testHiddenAttributesRuleVerification_TL() {
        LOGGER.info("General Preconditions");
        String planName = termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "SelectPlan").getValue(PlanDefinitionTabMetaData.PLAN.getLabel());

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        initiateTLQuoteAndFillToTab(getDefaultTLMasterPolicyData()
                        .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SCHEDULE_OF_CONTINUATION_PROVISION.getLabel(), SABBATICAL_DURATION.getLabel()), "2"),
                PremiumSummaryTab.class, true);

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
        termLifeInsuranceMasterPolicy.propose().perform(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.acceptContract().start();
        termLifeInsuranceMasterPolicy.acceptContract().getWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PremiumSummaryBindActionTab.class);

        assertThat(PremiumSummaryBindActionTab.buttonMaintainInForceRates).isAbsent();
        PremiumSummaryBindActionTab.buttonViewRateHistory.click();
        assertThat(RateDialogs.ViewRateHistoryDialog.tableRateHistory.getHeader().getValue()).doesNotContain(TableConstants.RateDetailsTable.IN_FORCE_RATE.getName());
        RateDialogs.ViewRateHistoryDialog.close();
        PremiumSummaryBindActionTab.buttonNext.click();

        LOGGER.info("Step#5 verification");
        termLifeInsuranceMasterPolicy.issue().start();
        termLifeInsuranceMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PremiumSummaryIssueActionTab.class);
        assertThat(PremiumSummaryIssueActionTab.buttonMaintainInForceRates).isAbsent();

        PremiumSummaryIssueActionTab.buttonViewRateHistory.click();
        assertThat(RateDialogs.ViewRateHistoryDialog.tableRateHistory.getHeader().getValue()).doesNotContain(TableConstants.RateDetailsTable.IN_FORCE_RATE.getName());
        RateDialogs.ViewRateHistoryDialog.close();
        termLifeInsuranceMasterPolicy.issue().getWorkspace().fillFrom(getDefaultTLMasterPolicyData(), PremiumSummaryIssueActionTab.class);

        LOGGER.info("Steps#7, 8 verification");
        termLifeInsuranceMasterPolicy.endorse().perform(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        PolicySummaryPage.buttonPendedEndorsement.click();
        termLifeInsuranceMasterPolicy.issue().start();
        termLifeInsuranceMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PremiumSummaryIssueActionTab.class);

        PremiumSummaryIssueActionTab.openViewRateDetailsByPlanName(planName);
        assertThat(RateDialogs.ViewRateDetailsDialog.tableRateDetails.getHeader().getValue()).doesNotContain(TableConstants.RateDetailsTable.IN_FORCE_RATE.getName());
    }
}
