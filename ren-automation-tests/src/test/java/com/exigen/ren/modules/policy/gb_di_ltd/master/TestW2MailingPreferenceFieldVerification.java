package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioW2MailingPreferenceVerification;
import org.testng.annotations.Test;

import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestW2MailingPreferenceFieldVerification extends ScenarioW2MailingPreferenceVerification implements LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-39051", component = POLICY_GROUPBENEFITS)
    public void testW2MailingPreferenceFieldVerification_LTD() {

        TestData policyTestData = getDefaultLTDMasterPolicyData()
                .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks()).resolveLinks();

        scenarioW2MailingPreferenceVerification(GroupBenefitsMasterPolicyType.GB_DI_LTD, policyTestData);
    }
}
