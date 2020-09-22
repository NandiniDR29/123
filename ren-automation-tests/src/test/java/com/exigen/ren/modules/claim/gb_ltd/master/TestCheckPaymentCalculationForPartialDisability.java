package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.tabs.*;

import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PlanDefinitionTab;

import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.EARNING_TEST;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.WIB_DURATION;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.OWN_OCCUPATION_EARNINGS_TEST;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.REHABILITATION_INCENTIVE_BENEFIT;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckPaymentCalculationForPartialDisability extends ClaimGroupBenefitsLTDBaseTest {
    private PaymentPaymentPaymentAllocationTab paymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-29799", "REN-29812", "REN-29816", "REN-29819"}, component = CLAIMS_GROUPBENEFITS)
    public void testCheckPaymentCalculationForPartialDisability() {
        final String EARNING_PERCENT = "80%";
        final String DURATION = "12 Months";
        TestData tdPolicyCON = longTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_CON")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), OWN_OCCUPATION_EARNINGS_TEST.getLabel()), EARNING_PERCENT)
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), WIB_DURATION.getLabel()), DURATION)
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), REHABILITATION_INCENTIVE_BENEFIT.getLabel()), "5%");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        initiateQuoteAndFillToTab(tdPolicyCON, PlanDefinitionTab.class, true);
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(OWN_OCCUPATION_EARNINGS_TEST)).containsAllOptions("60%", "80%", "99%");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(WIB_DURATION)).containsAllOptions("None", "12 Months", "24 Months");

            PlanDefinitionTab.buttonNext.click();
            longTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(tdPolicyCON, ClassificationManagementTab.class, PremiumSummaryTab.class, true);
            LongTermDisabilityMasterPolicyContext.premiumSummaryTab.submitTab();
            longTermDisabilityMasterPolicy.propose().perform(getDefaultLTDMasterPolicyData());
            longTermDisabilityMasterPolicy.acceptContract().perform(getDefaultLTDMasterPolicyData());
            longTermDisabilityMasterPolicy.issue().perform(getDefaultLTDMasterPolicyData());

            LOGGER.info("TEST REN-29799, REN-29812, REN-29816: Step 1-2");
            initiateClaimWithPolicyAndFillToTab(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY), PolicyInformationParticipantParticipantCoverageTab.class, true);
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(EARNING_TEST)).hasValue(EARNING_PERCENT);
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(WIB_DURATION)).hasValue(DURATION);
        });

        PolicyInformationParticipantParticipantCoverageTab.buttonNext.click();
        disabilityClaim.getDefaultWorkspace()
                .fillFromTo(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY), PolicyInformationSponsorTab.class, CompleteNotificationTab.class, true);
        CompleteNotificationTab.buttonOpenClaim.click();

        LOGGER.info("TEST REN-29819: Step 3-6");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment"), paymentAllocationTab.getClass(), true);
        assertThat(paymentAllocationTab.getAssetList().getAsset(IN_APPROVED_REHABILITATION_PROGRAM)).isAbsent();

        paymentAllocationTab.getAssetList().getAsset(IN_LIEU_BENEFIT).setValue("Partial Disability Benefit");
        assertThat(paymentAllocationTab.getAssetList().getAsset(IN_APPROVED_REHABILITATION_PROGRAM)).isPresent();

        PaymentPaymentPaymentAllocationTab.buttonNext.click();
        assertThat(paymentAllocationTab.getAssetList().getAsset(IN_APPROVED_REHABILITATION_PROGRAM)).hasWarningWithText("'In Approved Rehabilitation Program' is required");

        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
        Page.dialogConfirmation.buttonYes.click();

        LOGGER.info("TEST REN-29819: Step 12");
        claim.createPaymentSeries().start();
        claim.createPaymentSeries().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries"), PaymentSeriesPaymentAllocationActionTab.class, true);
        assertThat(claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesPaymentAllocationActionTab.class).getAssetList().getAsset(IN_LIEU_BENEFIT)).doesNotContainOption("Partial Disability Benefit");
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-29819", component = CLAIMS_GROUPBENEFITS)
    public void testCheckPaymentCalculationForBenefitNone() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), REHABILITATION_INCENTIVE_BENEFIT.getLabel()), "None"));

        LOGGER.info("TEST REN-29819: Step 17-18");
        createDefaultLongTermDisabilityClaimForMasterPolicy();
        claim.claimOpen().perform();

        LOGGER.info("TEST REN-29819: Step 19");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment"), paymentAllocationTab.getClass());
        assertThat(paymentAllocationTab.navigate().getAssetList().getAsset(IN_APPROVED_REHABILITATION_PROGRAM)).isAbsent();
    }
}
