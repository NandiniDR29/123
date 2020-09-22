package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.EndorsementReason.EMPTY;
import static com.exigen.ren.main.enums.PolicyConstants.PlanLTD.LTD_NC;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.MAX_MONTHLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.GUARANTEED_ISSUE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.GuaranteedIssueMetaData.GUARANTEED_ISSUE_AMT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab.OVERRIDE_RULES_LIST_KEY;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimAddNewAttributesInCoverageLimitDetails extends ClaimGroupBenefitsLTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33716", component = CLAIMS_GROUPBENEFITS)
    public void testClaimAddNewAttributesInCoverageLimitDetails() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(PremiumSummaryTab.class.getSimpleName()), longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER).getTestData("PremiumSummaryTab_OverrideRules")
                        .adjust(TestData.makeKeyPath(OVERRIDE_RULES_LIST_KEY), "Benefit Amount over $6,000 requires Underwriter approval"))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), MAX_MONTHLY_BENEFIT_AMOUNT.getLabel()), "10000") // for step 4: Benefit Maximum Amount=$10000
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", GUARANTEED_ISSUE.getLabel()),
                        new SimpleDataProvider().adjust(GUARANTEED_ISSUE_AMT.getLabel(), "6000")).resolveLinks());

        LOGGER.info("TEST: Steps 1-3");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .adjust(TestData.makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000"),
                policyInformationParticipantParticipantCoverageTab.getClass(), false);

        policyInformationParticipantParticipantCoverageTab.addCoverage(LTD_NC, "LTD Core - NC");
        policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(BENEFIT_PERCENTAGE).setValue("60");

        assertSoftly(softly -> {
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.GUARANTEED_ISSUE_AMOUNT)).isPresent().isDisabled().hasValue(new Currency(6000).toString());
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.EOI_REQUIRED)).isPresent().hasValue(VALUE_NO);
        });

        LOGGER.info("TEST: Steps 4, 5");
        policyInformationParticipantParticipantInformationTab.navigateToTab();

        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(COVERED_EARNINGS).setValue("20000");
        policyInformationParticipantParticipantCoverageTab.navigateToTab();

        assertSoftly(softly -> {
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.EOI_REQUIRED)).hasValue(VALUE_YES);
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.EOI_STATUS)).hasOptions(EMPTY, "Pending", "Approved", "Declined", "Closed");
        });

        LOGGER.info("TEST: Step 6");
        policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.EOI_STATUS).setValue("Approved");

        assertSoftly(softly -> {
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.APPROVAL_DATE)).isPresent();
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_AMOUNT)).hasValue(new Currency(10000).toString());
        });

        LOGGER.info("TEST: Step 7");
        policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.EOI_STATUS).setValue("Pending");

        assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_AMOUNT)).hasValue(new Currency(6000).toString());

        LOGGER.info("TEST: Step 8");
        policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.EOI_REQUIRED).setValue(VALUE_NO);

        assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_AMOUNT)).hasValue(new Currency(10000).toString());
    }
}