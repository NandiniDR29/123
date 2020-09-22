package com.exigen.ren.modules.policy.gb_di_ltd.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.LongTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.certificate.ScenarioTestArchiveTransactionRulesCertificatePolicy;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestArchiveTransactionRulesCertificatePolicy extends ScenarioTestArchiveTransactionRulesCertificatePolicy implements LongTermDisabilityMasterPolicyContext, LongTermDisabilityCertificatePolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-28391", component = POLICY_GROUPBENEFITS)
    public void testArchiveTransactionRulesCertificatePolicy() {
        super.testArchiveTransactionRulesCertificatePolicy(
                GroupBenefitsMasterPolicyType.GB_DI_LTD, getDefaultLTDMasterPolicyData(),
                GroupBenefitsCertificatePolicyType.GB_DI_LTD, longTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
    }
}
