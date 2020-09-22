package com.exigen.ren.modules.policy.gb_dn.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.certificate.ScenarioTestAutomatedEndorsementArchiving;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAutomatedEndorsementArchiving extends ScenarioTestAutomatedEndorsementArchiving implements GroupDentalCertificatePolicyContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-30355", component = POLICY_GROUPBENEFITS)
    public void testAutomatedEndorsementArchiving() {
        super.testAutomatedEndorsementArchiving(
                GroupBenefitsMasterPolicyType.GB_DN, getDefaultDNMasterPolicyData(),
                GroupBenefitsCertificatePolicyType.GB_DN,
                groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .adjust(groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                        .adjust(groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY))
        );
    }
}
