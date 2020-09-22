/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.exigen.ren.main.modules.policy.gb_ac.masterpolicy;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.*;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;

public interface GroupAccidentMasterPolicyContext {

    GroupAccidentMasterPolicy groupAccidentMasterPolicy = AutomationContext.getService(GroupAccidentMasterPolicy.class);
    BasicBenefitsTab basicBenefitsTab = groupAccidentMasterPolicy.getDefaultWorkspace().getTab(BasicBenefitsTab.class);
    EnhancedBenefitsAtoCTab enhancedBenefitsAtoCTab = groupAccidentMasterPolicy.getDefaultWorkspace().getTab(EnhancedBenefitsAtoCTab.class);
    EnhancedBenefitsDtoFTab enhancedBenefitsDtoFTab = groupAccidentMasterPolicy.getDefaultWorkspace().getTab(EnhancedBenefitsDtoFTab.class);
    EnhancedBenefitsHtoLTab enhancedBenefitsHtoLTab = groupAccidentMasterPolicy.getDefaultWorkspace().getTab(EnhancedBenefitsHtoLTab.class);
    EnhancedBenefitsMtoTTab enhancedBenefitsMtoTTab = groupAccidentMasterPolicy.getDefaultWorkspace().getTab(EnhancedBenefitsMtoTTab.class);
    ClassificationManagementTab classificationManagementMPTab = groupAccidentMasterPolicy.getDefaultWorkspace().getTab(ClassificationManagementTab.class);
    EnrollmentTab enrollmentTab = groupAccidentMasterPolicy.getDefaultWorkspace().getTab(EnrollmentTab.class);
    OptionalBenefitTab optionalBenefitTab = groupAccidentMasterPolicy.getDefaultWorkspace().getTab(OptionalBenefitTab.class);
    PlanDefinitionTab planDefinitionTab = groupAccidentMasterPolicy.getDefaultWorkspace().getTab(PlanDefinitionTab.class);
    PolicyInformationTab policyInformationTab = groupAccidentMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class);
    PremiumSummaryTab premiumSummaryTab = groupAccidentMasterPolicy.getDefaultWorkspace().getTab(PremiumSummaryTab.class);

    default void createDefaultGroupAccidentMasterPolicy() {
        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicyData());
    }

    default void createDefaultGroupAccidentMasterPolicySelfAdmin() {
        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicySelfAdminData());
    }

    default TestData getDefaultACMasterPolicyData() {
        return groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));
    }
    default TestData getDefaultACMasterPolicyDataWithBasicBenefits() {
        return groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestDataWithBasicBenefitsTab")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

    default TestData getDefaultACMasterPolicySelfAdminData() {
        return groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER_SELF_ADMIN, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY));
    }

    default void proposeAcceptContractIssueACMasterPolicyWithDefaultTestData() {
        groupAccidentMasterPolicy.propose().perform(getDefaultACMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        groupAccidentMasterPolicy.acceptContract().perform(getDefaultACMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        groupAccidentMasterPolicy.issue().perform(getDefaultACMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    default void initiatGACQuoteAndFillUpToTab(TestData testData, Class<? extends Tab> tabClass, boolean isFillTab) {
        groupAccidentMasterPolicy.initiate(testData);
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(testData, tabClass, isFillTab);
    }
}
