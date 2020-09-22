package com.exigen.ren.modules.policy.scenarios.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.helpers.policy.groupbenefits.PolicyHelper;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicy;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicy;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.DATA_GATHERING;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_CANCELLED;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.Endorsements.STATUS;

public class ScenarioTestAutomatedEndorsementArchiving extends BaseTest implements CustomerContext, CaseProfileContext {

    protected void testAutomatedEndorsementArchiving(GroupBenefitsMasterPolicyType masterPolicyType, TestData tdMasterPolicy,
                                                  GroupBenefitsCertificatePolicyType certificatePolicyType, TestData tdCertificatePolicy) {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(masterPolicyType);
        GroupBenefitsMasterPolicy masterPolicy = masterPolicyType.get();
        masterPolicy.createPolicy(tdMasterPolicy);
        String mpNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        GroupBenefitsCertificatePolicy certificatePolicy = certificatePolicyType.get();
        certificatePolicy.createPolicy(tdCertificatePolicy);
        String cpNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        certificatePolicy.endorse().perform(tdCertificatePolicy);
        PolicySummaryPage.buttonPendedEndorsement.click();
        certificatePolicy.dataGather().perform(certificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_Endorsement")
                .mask("PremiumSummaryTab"));
        Tab.buttonSaveAndExit.click();
        PolicySummaryPage.buttonPendedEndorsement.click();
        assertThat(PolicySummaryPage.tableEndorsements.getRow(1).getCell(STATUS.getName())).hasValue(DATA_GATHERING);
        Tab.buttonBack.click();

        LOGGER.info("Step 3");
        search(mpNumber);
        masterPolicy.cancel().perform(masterPolicy.getDefaultTestData(TestDataKey.CANCELLATION, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_CANCELLED);
        assertThat(PolicySummaryPage.labelCascadingTransaction).isPresent();

        LOGGER.info("Step 4");
        PolicyHelper.executeCascadingTransactionJobs(mpNumber);

        LOGGER.info("Step 5");
        search(cpNumber);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_CANCELLED);
    }
}