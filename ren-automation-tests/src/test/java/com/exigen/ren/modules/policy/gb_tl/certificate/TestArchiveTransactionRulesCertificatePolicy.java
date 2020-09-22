package com.exigen.ren.modules.policy.gb_tl.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.certificate.ScenarioTestArchiveTransactionRulesCertificatePolicy;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestArchiveTransactionRulesCertificatePolicy extends ScenarioTestArchiveTransactionRulesCertificatePolicy implements TermLifeInsuranceCertificatePolicyContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-28391", component = POLICY_GROUPBENEFITS)
    public void testArchiveTransactionRulesCertificatePolicy() {
        super.testArchiveTransactionRulesCertificatePolicy(
                GroupBenefitsMasterPolicyType.GB_TL, getDefaultTLMasterPolicyData(),
                GroupBenefitsCertificatePolicyType.GB_TL, termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
    }
}
