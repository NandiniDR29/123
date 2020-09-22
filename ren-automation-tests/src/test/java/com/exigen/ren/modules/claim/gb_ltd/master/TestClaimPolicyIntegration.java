/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.PlanDefinitionIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanLTD.LTD_CON;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantCoverageTab.buttonAddCoverage;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionIssueActionTabMetaData.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimPolicyIntegration extends ClaimGroupBenefitsLTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-18891", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPolicyIntegrationSpecifiedAmountSingle() {
        mainApp().open();

        createPolicyAndInitiateClaim(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(PlanDefinitionTab.class.getSimpleName(),
                        tdSpecific().getTestDataList("TestDataPercentageOfMonthlySalarySingleValue_AdjustPlanDefinitionTab", PlanDefinitionTab.class.getSimpleName())));

        policyInformationParticipantParticipantInformationTab.navigateToTab();
        policyInformationParticipantParticipantInformationTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS).setValue("1000");
        policyInformationParticipantParticipantCoverageTab.navigateToTab();
        buttonAddCoverage.click();

        policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.PLAN).setValue("TestUpdate");
        policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.COVERAGE_NAME).setValue("LTD Core - TestUpdate");

        assertSoftly(softly -> {
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.ERISA_INDICATOR)).hasValue(VALUE_YES);
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_TYPE)).hasValue("Percentage of Monthly Salary - Single Value");
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_AMOUNT)).hasValue(new Currency(600).toString());
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.PARTIAL_DISABILITY_CALCULATION)).hasValue("Proportionate Loss");
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.EARNING_TEST)).hasValue("80%");
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-19094", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPolicyIntegrationPercentageAmount() {
        mainApp().open();

        createPolicyAndInitiateClaim(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(PlanDefinitionTab.class.getSimpleName(),
                        tdSpecific().getTestDataList("TestDataPercentageAmount_AdjustPlanDefinitionTab", PlanDefinitionTab.class.getSimpleName())));

        policyInformationParticipantParticipantInformationTab.navigateToTab();
        policyInformationParticipantParticipantInformationTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS).setValue(new Currency(6000).toString());

        policyInformationParticipantParticipantCoverageTab.navigateToTab();
        buttonAddCoverage.click();

        policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.PLAN).setValue(LTD_CON);
        policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.COVERAGE_NAME).setValue("LTD Core - CON");

        assertSoftly(softly -> {
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.ERISA_INDICATOR)).hasValue(VALUE_YES);
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_TYPE)).hasValue("Percentage of Monthly Salary - Single Value");
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_PERCENTAGE)).hasValue(new Currency(60).toPlainString());
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_AMOUNT)).hasValue(new Currency(3600).toString());
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_MAXIMUM_AMOUNT)).hasValue(new Currency(6000).toString());
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_MINIMUM_AMOUNT)).hasValue(new Currency(100).toString());
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.PARTIAL_DISABILITY_CALCULATION)).hasValue("Proportionate Loss");
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.EARNING_TEST)).hasValue("80%");
        });
    }

    private void createPolicyAndInitiateClaim(TestData policyTestData) {
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.createPolicy(policyTestData
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .adjust(TestData.makeKeyPath(PlanDefinitionIssueActionTab.class.getSimpleName(), MINIMUM_HOURLY_REQUIREMENT.getLabel()), "15")
                        .adjust(TestData.makeKeyPath(PlanDefinitionIssueActionTab.class.getSimpleName(), ELIGIBILITY_WAITING_PERIOD_DEFINITION.getLabel()), "First of the month following (amount and mode)")
                        .adjust(TestData.makeKeyPath(PlanDefinitionIssueActionTab.class.getSimpleName(), WAITING_PERIOD_MODE.getLabel()), "Days")
                        .adjust(TestData.makeKeyPath(PlanDefinitionIssueActionTab.class.getSimpleName(), WAITING_PERIOD.getLabel()), "30")
                ));
        disabilityClaim.initiate(disabilityClaim.getSTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
    }
}
