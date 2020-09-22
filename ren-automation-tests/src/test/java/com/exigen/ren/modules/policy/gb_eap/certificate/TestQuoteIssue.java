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
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteIssue extends BaseTest implements CustomerContext, CaseProfileContext, EmployeeAssistanceProgramMasterPolicyContext, EmployeeAssistanceProgramCertificatePolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_EAP, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"IPBQA-24448", "REN-11061", "REN-11062"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteIssue() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(employeeAssistanceProgramMasterPolicy.getType());
        createDefaultEmployeeAssistanceProgramMasterPolicy();

        assertSoftly(softly -> {

            // Asserts for REN-11061/#8
            softly.assertThat(PolicySummaryPage.getPolicySeries()).isEqualTo("MP");
            softly.assertThat(PolicySummaryPage.getPolicyNumber()).isBetween(1L, 9999999999L);

            employeeAssistanceProgramCertificatePolicy.createQuote(getDefaultEAPCertificatePolicyDataGatherData());
            // Asserts for REN-11062/#14
            softly.assertThat(PolicySummaryPage.getPolicySeries()).isEqualTo("CP");
            softly.assertThat(PolicySummaryPage.getPolicyNumber()).isBetween(1L, 9999999999L);
        });

        LOGGER.info("TEST: Issue Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        employeeAssistanceProgramCertificatePolicy.issue().perform(employeeAssistanceProgramCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
