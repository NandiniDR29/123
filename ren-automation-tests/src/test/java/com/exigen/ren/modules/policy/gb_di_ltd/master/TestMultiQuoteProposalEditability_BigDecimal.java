package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestMultiQuoteProposalEditability_BigDecimal;
import org.testng.annotations.Test;

import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.PER_100_MONTHLY_COVERED_PAYROLL;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetaData.RATE_BASIS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.GB;
import static com.exigen.ren.utils.groups.Groups.GB_DI_LTD;
import static com.exigen.ren.utils.groups.Groups.GB_PRECONFIGURED;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestMultiQuoteProposalEditability_BigDecimal extends ScenarioTestMultiQuoteProposalEditability_BigDecimal implements LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37556", component = POLICY_GROUPBENEFITS)
    public void testMultiQuoteProposalEditability_BigDecimal() {

        TestData tdQuote = getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", RATING.getLabel()), new SimpleDataProvider().adjust(RATE_BASIS.getLabel(), PER_100_MONTHLY_COVERED_PAYROLL)).resolveLinks();

        super.testMultiQuoteProposalEditability_BigDecimal(longTermDisabilityMasterPolicy.getType(), tdQuote);
    }
}
