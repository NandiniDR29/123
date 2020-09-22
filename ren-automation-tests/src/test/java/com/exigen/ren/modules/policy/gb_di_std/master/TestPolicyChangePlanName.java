package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyChangePlanName extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-5990", "REN-13342"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyChangePlanName() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.createQuote(getDefaultSTDMasterPolicyData());

        shortTermDisabilityMasterPolicy.dataGather().start();
        shortTermDisabilityMasterPolicy.dataGather().getWorkspace().fillUpTo(tdSpecific().getTestData("TestDataUpdatePlanName"), premiumSummaryTab.getClass());
        Tab.buttonSaveAndExit.click();
        // Assert for REN-5990/#4
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        shortTermDisabilityMasterPolicy.propose().perform(getDefaultSTDMasterPolicyData());

        // Assert for REN-13342/#1
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        shortTermDisabilityMasterPolicy.acceptContract().perform(getDefaultSTDMasterPolicyData());

        // Assert for REN-13342/#2
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        shortTermDisabilityMasterPolicy.issue().perform(getDefaultSTDMasterPolicyData());
        shortTermDisabilityMasterPolicy.createEndorsement(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestDataUpdatePlanName").resolveLinks())
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.EXISTENT_BILLING_ACCOUNT).resolveLinks()));
        PolicySummaryPage.TransactionHistory.selectTransactionsToCompare("Endorsement", "Issue");

        PolicySummaryPage.TransactionHistoryDifferences.expandDifferencesTable();
        // Assert for REN-5990/#5
        assertThat(PolicySummaryPage.TransactionHistoryDifferences.tableDifferences)
                .hasRowsThatContain(0, ImmutableMap.of("Description", PlanDefinitionTabMetaData.PLAN_NAME.getLabel()));
    }
}
