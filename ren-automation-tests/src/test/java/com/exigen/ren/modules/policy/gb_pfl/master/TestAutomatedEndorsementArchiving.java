package com.exigen.ren.modules.policy.gb_pfl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestAutomatedEndorsementArchiving;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAutomatedEndorsementArchiving extends ScenarioTestAutomatedEndorsementArchiving implements PaidFamilyLeaveMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_PFL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-30355", component = POLICY_GROUPBENEFITS)
    public void testAutomatedEndorsementArchiving() {
        super.testAutomatedEndorsementArchiving(GroupBenefitsMasterPolicyType.GB_PFL, getDefaultPFLMasterPolicyData()
                .adjust(paidFamilyLeaveMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(paidFamilyLeaveMasterPolicy.getDefaultTestData(TestDataKey.DECLINE_BY_COMPANY, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(paidFamilyLeaveMasterPolicy.getDefaultTestData(TestDataKey.CANCELLATION, TestDataKey.DEFAULT_TEST_DATA_KEY)));
    }
}
