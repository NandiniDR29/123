package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestMultiQuoteProposalEditability_BigDecimal;
import org.testng.annotations.Test;

import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.RATE_BASIS;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.GB;
import static com.exigen.ren.utils.groups.Groups.GB_AC;
import static com.exigen.ren.utils.groups.Groups.GB_PRECONFIGURED;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestMultiQuoteProposalEditability_Money extends ScenarioTestMultiQuoteProposalEditability_BigDecimal implements GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37792", component = POLICY_GROUPBENEFITS)
    public void testMultiQuoteProposalEditability_Money() {

        TestData tdQuote = getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "GA")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), RATE_BASIS.getLabel()), "Per Month")
                .resolveLinks();

        super.testMultiQuoteProposalEditability_BigDecimal(groupAccidentMasterPolicy.getType(), tdQuote);
    }
}
