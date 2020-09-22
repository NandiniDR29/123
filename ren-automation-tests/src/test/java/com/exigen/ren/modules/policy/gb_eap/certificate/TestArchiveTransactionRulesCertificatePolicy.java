package com.exigen.ren.modules.policy.gb_eap.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.EmployeeAssistanceProgramCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.EmployeeAssistanceProgramMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.certificate.ScenarioTestArchiveTransactionRulesCertificatePolicy;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestArchiveTransactionRulesCertificatePolicy extends ScenarioTestArchiveTransactionRulesCertificatePolicy implements EmployeeAssistanceProgramMasterPolicyContext, EmployeeAssistanceProgramCertificatePolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_EAP, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-28391", component = POLICY_GROUPBENEFITS)
    public void testArchiveTransactionRulesCertificatePolicy() {
        super.testArchiveTransactionRulesCertificatePolicy(
                GroupBenefitsMasterPolicyType.GB_EAP, getDefaultEAPMasterPolicyData(),
                GroupBenefitsCertificatePolicyType.GB_EAP, getDefaultEAPCertificatePolicyDataGatherData());
    }
}
