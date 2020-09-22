/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.main.modules.policy.gb_vs.masterpolicy;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.*;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;

public interface GroupVisionMasterPolicyContext {

    GroupVisionMasterPolicy groupVisionMasterPolicy = AutomationContext.getService(GroupVisionMasterPolicy.class);

    PolicyInformationTab policyInformationTab = groupVisionMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class);
    EnrollmentTab enrollmentTab = groupVisionMasterPolicy.getDefaultWorkspace().getTab(EnrollmentTab.class);
    PlanDefinitionTab planDefinitionTab = groupVisionMasterPolicy.getDefaultWorkspace().getTab(PlanDefinitionTab.class);
    ClassificationManagementTab classificationManagementMpTab = groupVisionMasterPolicy.getDefaultWorkspace().getTab(ClassificationManagementTab.class);
    PremiumSummaryTab premiumSummaryTab = groupVisionMasterPolicy.getDefaultWorkspace().getTab(PremiumSummaryTab.class);
    CaseInstallationTab caseInstallationTab = groupVisionMasterPolicy.getDefaultWorkspace().getTab(CaseInstallationTab.class);

    default TestData getDefaultVSMasterPolicyData() {
        return groupVisionMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

    default void createDefaultGroupVisionMasterPolicy() {
        groupVisionMasterPolicy.createPolicy(getDefaultVSMasterPolicyData());
    }

    default void proposeAcceptContractIssueVSMasterPolicyWithDefaultTestData() {
        groupVisionMasterPolicy.propose().perform(getDefaultVSMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        groupVisionMasterPolicy.acceptContract().perform(getDefaultVSMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        groupVisionMasterPolicy.issue().perform(getDefaultVSMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    default void quoteInitiateAndFillUpToTab(TestData testData, Class<? extends Tab> tabClass, boolean isFillTab) {
        groupVisionMasterPolicy.initiate(testData);
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(testData, tabClass, isFillTab);
    }
}
