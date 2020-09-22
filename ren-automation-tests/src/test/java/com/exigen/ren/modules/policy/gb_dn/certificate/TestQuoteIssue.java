/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_dn.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteIssue extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalCertificatePolicyContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"IPBQA-24448", "REN-11061", "REN-11062"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteIssue() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        createDefaultGroupDentalMasterPolicy();

        assertSoftly(softly -> {

            // Asserts for REN-11061/#5
            softly.assertThat(PolicySummaryPage.getPolicySeries()).isEqualTo("MP");
            softly.assertThat(PolicySummaryPage.getPolicyNumber()).isBetween(1L, 9999999999L);

            groupDentalCertificatePolicy.createQuote(groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
            // Asserts for REN-11062/#11
            softly.assertThat(PolicySummaryPage.getPolicySeries()).isEqualTo("CP");
            softly.assertThat(PolicySummaryPage.getPolicyNumber()).isBetween(1L, 9999999999L);
        });

        LOGGER.info("TEST: Issue Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        groupDentalCertificatePolicy.issue().perform(groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
        
        LOGGER.info("------------------------------------------------------------------------");
        LOGGER.info("TEST: Group Dental Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        LOGGER.info("------------------------------------------------------------------------");
    }
}
