package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestDeclineReasons;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDeclineReasons extends ScenarioTestDeclineReasons implements GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-27998", component = POLICY_GROUPBENEFITS)
    public void testDeclineReasons() {
        super.testDeclineReasons(GroupBenefitsMasterPolicyType.GB_AC, getDefaultACMasterPolicyData());
    }
}
