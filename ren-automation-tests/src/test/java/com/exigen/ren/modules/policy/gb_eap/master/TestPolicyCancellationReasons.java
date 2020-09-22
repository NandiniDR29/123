package com.exigen.ren.modules.policy.gb_eap.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.EmployeeAssistanceProgramMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestPolicyCancellationReasons;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyCancellationReasons extends ScenarioTestPolicyCancellationReasons implements EmployeeAssistanceProgramMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_EAP, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-31754", component = POLICY_GROUPBENEFITS)
    public void testPolicyCancellationReasons() {
        super.testPolicyCancellationReasons(GroupBenefitsMasterPolicyType.GB_EAP, getDefaultEAPMasterPolicyData());
    }
}
