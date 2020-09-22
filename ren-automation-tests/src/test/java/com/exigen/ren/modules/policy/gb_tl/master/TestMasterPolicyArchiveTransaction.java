package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestMasterPolicyArchiveTransaction;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestMasterPolicyArchiveTransaction extends ScenarioTestMasterPolicyArchiveTransaction implements TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-28148", component = POLICY_GROUPBENEFITS)
    public void testPolicyArchiveTransaction() {
        super.testPolicyArchiveTransaction(GroupBenefitsMasterPolicyType.GB_TL, getDefaultTLMasterPolicyData()
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY)));
    }
}
