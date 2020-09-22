package com.exigen.ren.modules.policy.gb_pfl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestMultiQuoteProposalEditability_BigDecimal;
import org.testng.annotations.Test;

import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.PERCENT_OF_TAXABLE_WAGE;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetaData.RATE_BASIS;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.GB;
import static com.exigen.ren.utils.groups.Groups.GB_PFL;
import static com.exigen.ren.utils.groups.Groups.GB_PRECONFIGURED;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestMultiQuoteProposalEditability_BigDecimal extends ScenarioTestMultiQuoteProposalEditability_BigDecimal implements PaidFamilyLeaveMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_PFL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37556", component = POLICY_GROUPBENEFITS)
    public void testMultiQuoteProposalEditability_BigDecimal() {

        TestData tdQuote = getDefaultPFLMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NJ")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey(), RATING.getLabel()), new SimpleDataProvider().adjust(RATE_BASIS.getLabel(), PERCENT_OF_TAXABLE_WAGE))
                .resolveLinks();

        super.testMultiQuoteProposalEditability_BigDecimal(paidFamilyLeaveMasterPolicy.getType(), tdQuote);
    }
}
