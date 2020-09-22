package com.exigen.ren.modules.policy.gb_pfl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioTestChangeAgencyReasonListValues;
import org.testng.annotations.Test;

import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.AGENT_SUB_PRODUCER;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestChangeAgencyReasonListValues extends ScenarioTestChangeAgencyReasonListValues implements PaidFamilyLeaveMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_PFL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-31957"}, component = POLICY_GROUPBENEFITS)
    public void testChangeAgencyReasonListValues() {

        TestData tdMasterPolicy = getDefaultPFLMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS.getLabel()), VALUE_NO).resolveLinks()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), AGENT_SUB_PRODUCER.getLabel()), "index=1").resolveLinks();

        //In next method the last two parameters will not be used and needed only for method signature
        super.testChangeAgencyReasonListValues(GroupBenefitsMasterPolicyType.GB_PFL, tdMasterPolicy, GroupBenefitsCertificatePolicyType.GB_DN, new SimpleDataProvider());
    }
}
