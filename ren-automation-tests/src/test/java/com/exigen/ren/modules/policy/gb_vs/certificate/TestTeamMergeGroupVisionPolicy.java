/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_vs.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestTeamMergeGroupVisionPolicy extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext, GroupVisionCertificatePolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, TEAM_MERGE, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23733", component = POLICY_GROUPBENEFITS)
    public void testTeamMergeGroupVisionPolicy() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupVisionMasterPolicy.getType());

        LOGGER.info("TEST: Master Policy Creation");
        groupVisionMasterPolicy.createPolicy(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_AllPlans")
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestData_AllPlans")));

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("TEST: Certificate Policy Creation");
        createDefaultGroupVisionCertificatePolicy();

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
