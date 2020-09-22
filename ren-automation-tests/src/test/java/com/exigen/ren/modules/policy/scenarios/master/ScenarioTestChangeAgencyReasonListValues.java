package com.exigen.ren.modules.policy.scenarios.master;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicy;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicy;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.common.metadata.master.ChangeAgencyTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.ChangeAgencyTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType.GB_PFL;
import static com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType.GB_ST;

public class ScenarioTestChangeAgencyReasonListValues extends BaseTest implements CustomerContext, CaseProfileContext {

    public void testChangeAgencyReasonListValues(GroupBenefitsMasterPolicyType policyType, TestData tdPolicy,
                                                 GroupBenefitsCertificatePolicyType certificatePolicyType, TestData tdCertificatePolicy) {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(policyType);
        GroupBenefitsMasterPolicy policy = policyType.get();
        policy.createPolicy(tdPolicy);

        if (!policyType.equals(GB_ST) && !policyType.equals(GB_PFL)) {
            String masterPolicyID = PolicySummaryPage.labelPolicyNumber.getValue();
            GroupBenefitsCertificatePolicy certificatePolicy = certificatePolicyType.get();
            certificatePolicy.createPolicy(tdCertificatePolicy);
            MainPage.QuickSearch.search(masterPolicyID);
        }

        LOGGER.info("Step 1");
        policy.changeAgency().start();
        assertThat(policy.changeAgency().getWorkspace().getTab(ChangeAgencyTab.class).getAssetList().getAsset(ChangeAgencyTabMetaData.REASON))
                .hasOptions(ImmutableList.of("", "Broker Retired", "New Broker Hired", "Broker out of Business", "Broker License Expired", "Acquisition", "Other"));
    }
}
