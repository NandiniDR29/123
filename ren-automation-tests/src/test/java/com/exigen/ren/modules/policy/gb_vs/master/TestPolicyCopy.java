/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyCopy extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24537", component = POLICY_GROUPBENEFITS)
    public void testPolicyCopy() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupVisionMasterPolicy.getType());

        createDefaultGroupVisionMasterPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TEST: Copy Policy #" + policyNumber);
        groupVisionMasterPolicy.policyCopy().perform(groupVisionMasterPolicy.getDefaultTestData("CopyFromPolicy", TestDataKey.DEFAULT_TEST_DATA_KEY));
        groupVisionMasterPolicy.dataGather().perform(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_CopyFromPolicy"));
        groupVisionMasterPolicy.calculatePremium();
        groupVisionMasterPolicy.propose().perform(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.acceptContract().perform(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.issue().perform(getDefaultVSMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        String policyNumberCopied = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Copied Policy #" + policyNumberCopied);

        assertThat(policyNumber).isNotEqualTo(policyNumberCopied)
                .as("Copied policy %s number is the same as initial policy number %s",
                        policyNumberCopied, policyNumber);
    }
}
