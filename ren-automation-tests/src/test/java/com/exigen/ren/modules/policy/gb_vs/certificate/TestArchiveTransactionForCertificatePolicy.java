package com.exigen.ren.modules.policy.gb_vs.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.certificate.ScenarioTestArchiveTransactionForCertificatePolicy;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestArchiveTransactionForCertificatePolicy extends ScenarioTestArchiveTransactionForCertificatePolicy implements GroupVisionCertificatePolicyContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-28390", component = POLICY_GROUPBENEFITS)
    public void testArchiveTransactionForCertificatePolicy() {
        super.testArchiveTransactionForCertificatePolicy(
                GroupBenefitsMasterPolicyType.GB_VS,
                getDefaultVSMasterPolicyData().adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY)),
                GroupBenefitsCertificatePolicyType.GB_VS,
                groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .adjust(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                        .adjust(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY))
        );
    }
}