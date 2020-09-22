/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.*;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;

public interface ShortTermDisabilityMasterPolicyContext {
    ShortTermDisabilityMasterPolicy shortTermDisabilityMasterPolicy = AutomationContext.getService(ShortTermDisabilityMasterPolicy.class);
    ClassificationManagementTab classificationManagementMpTab = shortTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(ClassificationManagementTab.class);
    EnrollmentTab enrollmentTab = shortTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(EnrollmentTab.class);
    PlanDefinitionTab planDefinitionTab = shortTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(PlanDefinitionTab.class);
    PolicyInformationTab policyInformationTab = shortTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class);
    PremiumSummaryTab premiumSummaryTab = shortTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(PremiumSummaryTab.class);

    default void createDefaultShortTermDisabilityMasterPolicy() {
        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicyData());
    }

    default void createDefaulSTDMasterPolicySelfAdminMasterPolicy() {
        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicySelfAdminData());
    }

    default void initiateSTDQuoteAndFillToTab(TestData testData, Class<? extends Tab> tabClass, boolean isFillTab) {
        shortTermDisabilityMasterPolicy.initiate(testData);
        shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(testData, tabClass, isFillTab);
    }

    default TestData getDefaultSTDMasterPolicyData() {
        return shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

    default TestData getDefaultSTDMasterPolicySelfAdminData() {
        return shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER_SELF_ADMIN, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY));
    }

    default void proposeAcceptContractIssueSTDMasterPolicyWithDefaultTestData() {
        shortTermDisabilityMasterPolicy.propose().perform(getDefaultSTDMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        shortTermDisabilityMasterPolicy.acceptContract().perform(getDefaultSTDMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        shortTermDisabilityMasterPolicy.issue().perform(getDefaultSTDMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
