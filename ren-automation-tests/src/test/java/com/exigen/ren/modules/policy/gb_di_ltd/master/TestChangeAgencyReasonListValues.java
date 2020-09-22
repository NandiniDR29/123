package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.LongTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestChangeAgencyReasonListValues;
import org.testng.annotations.Test;

import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.AGENT_SUB_PRODUCER;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestChangeAgencyReasonListValues extends ScenarioTestChangeAgencyReasonListValues implements LongTermDisabilityCertificatePolicyContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-31957"}, component = POLICY_GROUPBENEFITS)
    public void testChangeAgencyReasonListValues() {

        TestData tdMasterPolicy = getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS.getLabel()), VALUE_NO)
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), AGENT_SUB_PRODUCER.getLabel()), "index=1");
        TestData tdCertificatePolicy = getDefaultLTDCertificatePolicyDataGatherData()
                .adjust(longTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));

        super.testChangeAgencyReasonListValues(GroupBenefitsMasterPolicyType.GB_DI_LTD, tdMasterPolicy, GroupBenefitsCertificatePolicyType.GB_DI_LTD, tdCertificatePolicy);
    }
}
