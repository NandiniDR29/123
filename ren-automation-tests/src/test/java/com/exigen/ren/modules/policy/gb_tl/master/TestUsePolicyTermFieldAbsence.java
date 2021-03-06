package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestUsePolicyTermFieldAbsence;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUsePolicyTermFieldAbsence extends ScenarioTestUsePolicyTermFieldAbsence implements TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-39885", component = POLICY_GROUPBENEFITS)
    public void testUsePolicyTermFieldAbsence() {
        super.testUsePolicyTermFieldAbsence(termLifeInsuranceMasterPolicy.getType(), getDefaultTLMasterPolicyData(), policyInformationTab);
    }
}
