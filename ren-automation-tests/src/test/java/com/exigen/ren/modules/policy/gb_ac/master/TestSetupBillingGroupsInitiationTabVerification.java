package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestSetupBillingGroupsInitiationTab;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestSetupBillingGroupsInitiationTabVerification extends ScenarioTestSetupBillingGroupsInitiationTab implements GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-27411"}, component = POLICY_GROUPBENEFITS)
    public void testSetupBillingGroupsInitiationTabVerification() {

        testSetupBillingGroupsInitiationTab(GroupBenefitsMasterPolicyType.GB_AC, getDefaultACMasterPolicyData());
    }
}