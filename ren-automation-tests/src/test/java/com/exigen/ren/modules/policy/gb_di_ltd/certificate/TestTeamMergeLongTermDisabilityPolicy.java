/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.policy.gb_di_ltd.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.LongTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestTeamMergeLongTermDisabilityPolicy extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityCertificatePolicyContext, LongTermDisabilityMasterPolicyContext {


    @Test(groups = {GB, GB_PRECONFIGURED, TEAM_MERGE, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23733", component = POLICY_GROUPBENEFITS)
    public void testTeamMergeLongTermDisabilityPolicy() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        LOGGER.info("TEST: Master Policy Creation");
        longTermDisabilityMasterPolicy.createPolicy(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_AllPlans")
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE,TestDataKey.DEFAULT_TEST_DATA_KEY)));

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("TEST: Certificate Policy Creation");
        createDefaultLongTermDisabilityCertificatePolicy();

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
