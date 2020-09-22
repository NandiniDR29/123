package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestArchiveTransactionRulesMasterPolicy;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestArchiveTransactionRulesMasterPolicy extends ScenarioTestArchiveTransactionRulesMasterPolicy implements GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-28310", component = POLICY_GROUPBENEFITS)
    public void testArchiveTransactionRulesForMp() {
        super.testArchiveTransactionRulesForMP(GroupBenefitsMasterPolicyType.GB_DN, getDefaultDNMasterPolicyData());
    }
}