package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestUsePolicyTermFieldAbsence;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUsePolicyTermFieldAbsence extends ScenarioTestUsePolicyTermFieldAbsence implements GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-39883", component = POLICY_GROUPBENEFITS)
    public void testUsePolicyTermFieldAbsence() {
        super.testUsePolicyTermFieldAbsence(groupDentalMasterPolicy.getType(), getDefaultDNMasterPolicyData(), policyInformationTab);
    }
}
