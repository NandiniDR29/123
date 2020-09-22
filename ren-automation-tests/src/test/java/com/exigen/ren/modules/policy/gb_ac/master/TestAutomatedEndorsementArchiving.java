package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestAutomatedEndorsementArchiving;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAutomatedEndorsementArchiving extends ScenarioTestAutomatedEndorsementArchiving implements GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-30355", component = POLICY_GROUPBENEFITS)
    public void testAutomatedEndorsementArchiving() {
        super.testAutomatedEndorsementArchiving(GroupBenefitsMasterPolicyType.GB_AC, getDefaultACMasterPolicyData()
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DECLINE_BY_COMPANY, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.CANCELLATION, TestDataKey.DEFAULT_TEST_DATA_KEY)));
    }
}
