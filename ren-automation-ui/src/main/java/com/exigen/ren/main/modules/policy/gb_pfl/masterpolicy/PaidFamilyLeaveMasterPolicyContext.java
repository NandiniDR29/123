/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.*;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;

import static com.exigen.istf.verification.CustomAssertions.assertThat;

public interface PaidFamilyLeaveMasterPolicyContext {

    PaidFamilyLeaveMasterPolicy paidFamilyLeaveMasterPolicy = AutomationContext.getService(PaidFamilyLeaveMasterPolicy.class);

    PolicyInformationTab policyInformationTab = paidFamilyLeaveMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class);
    EnrollmentTab enrollmentTab = paidFamilyLeaveMasterPolicy.getDefaultWorkspace().getTab(EnrollmentTab.class);
    PlanDefinitionTab planDefinitionTab = paidFamilyLeaveMasterPolicy.getDefaultWorkspace().getTab(PlanDefinitionTab.class);
    ClassificationManagementTab classificationManagementTab = paidFamilyLeaveMasterPolicy.getDefaultWorkspace().getTab(ClassificationManagementTab.class);
    PremiumSummaryTab premiumSummaryTab = paidFamilyLeaveMasterPolicy.getDefaultWorkspace().getTab(PremiumSummaryTab.class);

    default TestData getDefaultPFLMasterPolicyData() {
        return paidFamilyLeaveMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(paidFamilyLeaveMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(paidFamilyLeaveMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(paidFamilyLeaveMasterPolicy.getDefaultTestData(TestDataKey.ISSUE,TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

    default void createDefaultPaidFamilyLeaveMasterPolicy(){
        paidFamilyLeaveMasterPolicy.createPolicy(getDefaultPFLMasterPolicyData());
    }

    default void initiatePFLQuoteAndFillToTab(TestData testData, Class<? extends Tab> tabClass, boolean isFillTab) {
        paidFamilyLeaveMasterPolicy.initiate(testData);
        paidFamilyLeaveMasterPolicy.getDefaultWorkspace().fillUpTo(testData, tabClass, isFillTab);
    }

    default void proposeAcceptContractIssuePFLMasterPolicyWithDefaultTestData() {
        paidFamilyLeaveMasterPolicy.propose().perform(getDefaultPFLMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        paidFamilyLeaveMasterPolicy.acceptContract().perform(getDefaultPFLMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        paidFamilyLeaveMasterPolicy.issue().perform(getDefaultPFLMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
