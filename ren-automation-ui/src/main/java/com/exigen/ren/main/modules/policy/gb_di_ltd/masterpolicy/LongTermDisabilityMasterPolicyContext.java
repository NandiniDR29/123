/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.*;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;

public interface LongTermDisabilityMasterPolicyContext {

    LongTermDisabilityMasterPolicy longTermDisabilityMasterPolicy = AutomationContext.getService(LongTermDisabilityMasterPolicy.class);
    ClassificationManagementTab classificationManagementMpTab = longTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(ClassificationManagementTab.class);
    EnrollmentTab enrollmentTab = longTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(EnrollmentTab.class);
    PlanDefinitionTab planDefinitionTab = longTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(PlanDefinitionTab.class);
    PolicyInformationTab policyInformationTab = longTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class);
    PremiumSummaryTab premiumSummaryTab = longTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(PremiumSummaryTab.class);

    default TestData getDefaultLTDMasterPolicyData() {
        return longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

    default TestData getDefaultLTDMasterPolicySelfAdminData() {
        return longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER_SELF_ADMIN, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY));
    }

    default void createDefaultLongTermDisabilityMasterPolicy() {
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData());
    }

    default void initiateQuoteAndFillToTab(TestData testData,  Class<? extends Tab> tabClass, boolean isFillTab) {
        longTermDisabilityMasterPolicy.initiate(testData);
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(testData, tabClass, isFillTab);
    }

    default void initiateQuoteAndSelectDefaultPlan(TestData testData) {
        initiateQuoteAndFillToTab(testData, planDefinitionTab.getClass(), false);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).fill(testData.getTestDataList(planDefinitionTab.getClass().getSimpleName()).get(0));
    }

    default void proposeAcceptContractIssueWithDefaultTestData() {
        longTermDisabilityMasterPolicy.propose().perform(getDefaultLTDMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        longTermDisabilityMasterPolicy.acceptContract().perform(getDefaultLTDMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        longTermDisabilityMasterPolicy.issue().perform(getDefaultLTDMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
