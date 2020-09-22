package com.exigen.ren.modules.policy.gb_eap.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.EmployeeAssistanceProgramMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestMultiQuoteProposalEditability_BigDecimal;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestMultiQuoteProposalEditability_BigDecimal extends ScenarioTestMultiQuoteProposalEditability_BigDecimal implements EmployeeAssistanceProgramMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_EAP, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37556", component = POLICY_GROUPBENEFITS)
    public void testMultiQuoteProposalEditability_BigDecimal() {
        super.testMultiQuoteProposalEditability_BigDecimal(employeeAssistanceProgramMasterPolicy.getType(), getDefaultEAPMasterPolicyData());
    }
}
