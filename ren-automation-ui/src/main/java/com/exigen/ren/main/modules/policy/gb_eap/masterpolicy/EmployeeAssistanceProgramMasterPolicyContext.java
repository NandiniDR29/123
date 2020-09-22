/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.exigen.ren.main.modules.policy.gb_eap.masterpolicy;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.tabs.*;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;

public interface EmployeeAssistanceProgramMasterPolicyContext {
    EmployeeAssistanceProgramMasterPolicy employeeAssistanceProgramMasterPolicy = AutomationContext.getService(EmployeeAssistanceProgramMasterPolicy.class);
    ClassificationManagementTab classificationManagementMpTab = employeeAssistanceProgramMasterPolicy.getDefaultWorkspace().getTab(ClassificationManagementTab.class);
    EnrollmentTab enrollmentTab = employeeAssistanceProgramMasterPolicy.getDefaultWorkspace().getTab(EnrollmentTab.class);
    PlanDefinitionTab planDefinitionTab = employeeAssistanceProgramMasterPolicy.getDefaultWorkspace().getTab(PlanDefinitionTab.class);
    PolicyInformationTab policyInformationTab = employeeAssistanceProgramMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class);
    PremiumSummaryTab premiumSummaryTab = employeeAssistanceProgramMasterPolicy.getDefaultWorkspace().getTab(PremiumSummaryTab.class);

    default void createDefaultEmployeeAssistanceProgramMasterPolicy() {
        employeeAssistanceProgramMasterPolicy.createPolicy(getDefaultEAPMasterPolicyData());
    }

    default void initiateEAPQuoteAndFillToTab(TestData testData, Class<? extends Tab> tabClass, boolean isFillTab) {
        employeeAssistanceProgramMasterPolicy.initiate(testData);
        employeeAssistanceProgramMasterPolicy.getDefaultWorkspace().fillUpTo(testData, tabClass, isFillTab);
    }

    default TestData getDefaultEAPMasterPolicyData() {
        return employeeAssistanceProgramMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(employeeAssistanceProgramMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(employeeAssistanceProgramMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(employeeAssistanceProgramMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

    default TestData getDefaultEAPMasterPolicySelfAdminData() {
        return employeeAssistanceProgramMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER_SELF_ADMIN, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(employeeAssistanceProgramMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(employeeAssistanceProgramMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(employeeAssistanceProgramMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY));
    }

    default void proposeAcceptContractIssueEAPMasterPolicyWithDefaultTestData() {
        employeeAssistanceProgramMasterPolicy.propose().perform(getDefaultEAPMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        employeeAssistanceProgramMasterPolicy.acceptContract().perform(getDefaultEAPMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        employeeAssistanceProgramMasterPolicy.issue().perform(getDefaultEAPMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
