/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.policy.gb_eap.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.EmployeeAssistanceProgramCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.EmployeeAssistanceProgramMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyEndorsementMidTerm extends BaseTest implements CustomerContext, CaseProfileContext, EmployeeAssistanceProgramMasterPolicyContext, EmployeeAssistanceProgramCertificatePolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_EAP, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24524", component = POLICY_GROUPBENEFITS)
    public void testPolicyEndorsementMidTerm() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(employeeAssistanceProgramMasterPolicy.getType());
        employeeAssistanceProgramMasterPolicy.createPolicy(getDefaultEAPMasterPolicyData()
                .adjust(employeeAssistanceProgramMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "Adjustment_BackDated").resolveLinks()));

        employeeAssistanceProgramCertificatePolicy.createPolicy(getDefaultEAPCertificatePolicyDataGatherData()
                .adjust(employeeAssistanceProgramCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "Adjustment_BackDated"))
                .adjust(employeeAssistanceProgramCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()));

        LOGGER.info("TEST: Flat Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        employeeAssistanceProgramCertificatePolicy.endorse().perform(employeeAssistanceProgramCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        PolicySummaryPage.buttonPendedEndorsement.click();
        employeeAssistanceProgramCertificatePolicy.dataGather().perform(tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY));
        PolicySummaryPage.buttonPendedEndorsement.click();
        employeeAssistanceProgramCertificatePolicy.issue().perform(employeeAssistanceProgramCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));

        assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        PolicySummaryPage.buttonTransactionHistory.click();
        assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(
                "Type")).hasValue(ProductConstants.TransactionHistoryType.ENDORSEMENT);
    }
}
