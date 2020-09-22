/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.claim.gb_std.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantCoverageTab.buttonAddCoverage;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimPolicyIntegration extends ClaimGroupBenefitsSTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-18890", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPolicyIntegrationSpecifiedAmountSingle() {
        mainApp().open();

        createPolicyAndInitiateClaim(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(PlanDefinitionTab.class.getSimpleName(),
                        tdSpecific().getTestDataList("TestDataSpecifiedAmountSingle_AdjustPlanDefinitionTab", PlanDefinitionTab.class.getSimpleName())));

        policyInformationParticipantParticipantCoverageTab.navigateToTab();
        buttonAddCoverage.click();

        policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.PLAN).setValue("TestUpdate");
        policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.COVERAGE_NAME).setValue("STD Core - TestUpdate");

        assertSoftly(softly -> {
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.ERISA_INDICATOR)).hasValue(VALUE_YES);
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.MAX_BENEFIT_PERIOD_WEEKS)).hasValue("26");
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_TYPE)).hasValue("Specified Weekly Benefit Amount - Single Value");
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_AMOUNT)).hasValue(new Currency(500).toString());
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.PARTIAL_DISABILITY_CALCULATION)).hasValue("Work Incentive Benefit");
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.EARNING_TEST)).hasValue("50%");
        });
    }


    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-19051", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPolicyIntegrationPercentageAmount() {
        mainApp().open();

        createPolicyAndInitiateClaim(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(PlanDefinitionTab.class.getSimpleName(),
                        tdSpecific().getTestDataList("TestDataPercentageAmount_AdjustPlanDefinitionTab", PlanDefinitionTab.class.getSimpleName())));

        policyInformationParticipantParticipantInformationTab.navigateToTab();
        policyInformationParticipantParticipantInformationTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS).setValue(new Currency(500).toString());

        policyInformationParticipantParticipantCoverageTab.navigateToTab();
        buttonAddCoverage.click();

        policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.PLAN).setValue("NC");
        policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.COVERAGE_NAME).setValue("STD Core - NC");

        assertSoftly(softly -> {
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.ERISA_INDICATOR)).hasValue(VALUE_NO);
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.MAX_BENEFIT_PERIOD_WEEKS)).hasValue("26");
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_TYPE)).hasValue("Percentage of Weekly Salary - Single Value");
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_PERCENTAGE)).hasValue(new Currency(60).toPlainString());
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_AMOUNT)).hasValue(new Currency(300).toString());
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_MAXIMUM_AMOUNT)).hasValue(new Currency(1000).toString());
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_MINIMUM_AMOUNT)).hasValue(new Currency(25).toString());
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.PARTIAL_DISABILITY_CALCULATION)).hasValue("Work Incentive Benefit");
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.EARNING_TEST)).hasValue("80%");
        });
    }

    private void createPolicyAndInitiateClaim(TestData policyTestData) {
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        shortTermDisabilityMasterPolicy.createPolicy(policyTestData
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));

        disabilityClaim.initiate(disabilityClaim.getSTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
    }
}
