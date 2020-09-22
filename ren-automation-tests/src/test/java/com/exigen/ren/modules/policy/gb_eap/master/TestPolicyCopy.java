/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.policy.gb_eap.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.EmployeeAssistanceProgramMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyCopy extends BaseTest implements CustomerContext, CaseProfileContext, EmployeeAssistanceProgramMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_EAP, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24537", component = POLICY_GROUPBENEFITS)
    public void testPolicyCopy() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(employeeAssistanceProgramMasterPolicy.getType());
        createDefaultEmployeeAssistanceProgramMasterPolicy();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TEST: Copy Policy #" + policyNumber);
        employeeAssistanceProgramMasterPolicy.policyCopy().perform(employeeAssistanceProgramMasterPolicy.getDefaultTestData("CopyFromPolicy", TestDataKey.DEFAULT_TEST_DATA_KEY));
        employeeAssistanceProgramMasterPolicy.dataGather().perform(employeeAssistanceProgramMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestDataUpdateSomething"));
        employeeAssistanceProgramMasterPolicy.calculatePremium();
        employeeAssistanceProgramMasterPolicy.propose().perform(getDefaultEAPMasterPolicyData());
        employeeAssistanceProgramMasterPolicy.acceptContract().perform(getDefaultEAPMasterPolicyData());
        employeeAssistanceProgramMasterPolicy.issue().perform(getDefaultEAPMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        String policyNumberCopied = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Copied Policy #" + policyNumberCopied);

        assertThat(policyNumber).isNotEqualTo(policyNumberCopied)
                .as("Copied policy %s number is the same as initial policy number %s",
                        policyNumberCopied, policyNumber);
    }
}
