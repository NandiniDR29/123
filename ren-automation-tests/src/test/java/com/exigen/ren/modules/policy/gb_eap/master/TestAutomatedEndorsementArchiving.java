package com.exigen.ren.modules.policy.gb_eap.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.EmployeeAssistanceProgramMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestAutomatedEndorsementArchiving;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAutomatedEndorsementArchiving extends ScenarioTestAutomatedEndorsementArchiving implements EmployeeAssistanceProgramMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_EAP, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-30355", component = POLICY_GROUPBENEFITS)
    public void testAutomatedEndorsementArchiving() {
        super.testAutomatedEndorsementArchiving(GroupBenefitsMasterPolicyType.GB_EAP, getDefaultEAPMasterPolicyData()
                .adjust(employeeAssistanceProgramMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(employeeAssistanceProgramMasterPolicy.getDefaultTestData(TestDataKey.DECLINE_BY_COMPANY, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(employeeAssistanceProgramMasterPolicy.getDefaultTestData(TestDataKey.CANCELLATION, TestDataKey.DEFAULT_TEST_DATA_KEY)));
    }
}
