package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestMultiQuoteProposalEditability_BigDecimal;
import org.testng.annotations.Test;

import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.PER_10_TOTAL_WEEKLY_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.RATE_BASIS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.GB;
import static com.exigen.ren.utils.groups.Groups.GB_DI_STD;
import static com.exigen.ren.utils.groups.Groups.GB_PRECONFIGURED;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestMultiQuoteProposalEditability_BigDecimal extends ScenarioTestMultiQuoteProposalEditability_BigDecimal implements ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37556", component = POLICY_GROUPBENEFITS)
    public void testMultiQuoteProposalEditability_BigDecimal() {

        TestData tdQuote = getDefaultSTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", RATE_BASIS.getLabel()), PER_10_TOTAL_WEEKLY_BENEFIT).resolveLinks();

        super.testMultiQuoteProposalEditability_BigDecimal(shortTermDisabilityMasterPolicy.getType(), tdQuote);
    }
}
