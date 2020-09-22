package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestSetupBillingGroupsInitiationTab;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestSetupBillingGroupsInitiationTabVerification extends ScenarioTestSetupBillingGroupsInitiationTab implements GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-27411"}, component = POLICY_GROUPBENEFITS)
    public void testSetupBillingGroupsInitiationTabVerification() {

        testSetupBillingGroupsInitiationTab(GroupBenefitsMasterPolicyType.GB_VS, getDefaultVSMasterPolicyData());
    }
}
