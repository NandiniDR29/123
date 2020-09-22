package com.exigen.ren.modules.claim.gb_std.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.WIB_DURATION;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.EARNING_TEST;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.PARTIAL_DISABILITY;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.REHABILITATION_INCENTIVE_BENEFIT;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckPaymentCalculationForPartialDisability extends ClaimGroupBenefitsSTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-29812", "REN-29816", "REN-29819"}, component = CLAIMS_GROUPBENEFITS)
    public void testCheckPaymentCalculationForPartialDisability() {
        final String EARNING_PERCENT = "80%";
        final String DISABILITY = "Work Incentive Benefit";
        TestData tdPolicyCON = shortTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_CON")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), EARNING_TEST.getLabel()), EARNING_PERCENT)
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), PARTIAL_DISABILITY.getLabel()), DISABILITY)
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), REHABILITATION_INCENTIVE_BENEFIT.getLabel()), "5%");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        initiateSTDQuoteAndFillToTab(tdPolicyCON, PlanDefinitionTab.class, true);
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(EARNING_TEST)).containsAllOptions("40%", "50%", "60%", "70%", "80%");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(PARTIAL_DISABILITY)).containsAllOptions(DISABILITY, "None");

            PlanDefinitionTab.buttonNext.click();
            shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(tdPolicyCON, ClassificationManagementTab.class, PremiumSummaryTab.class, true);
            ShortTermDisabilityMasterPolicyContext.premiumSummaryTab.submitTab();
            shortTermDisabilityMasterPolicy.propose().perform(getDefaultSTDMasterPolicyData());
            shortTermDisabilityMasterPolicy.acceptContract().perform(getDefaultSTDMasterPolicyData());
            shortTermDisabilityMasterPolicy.issue().perform(getDefaultSTDMasterPolicyData());

            LOGGER.info("TEST REN-29812, REN-29816: Step 7-8, 5-6");
            initiateClaimWithPolicyAndFillToTab(disabilityClaim.getSTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY), PolicyInformationParticipantParticipantCoverageTab.class, true);
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.EARNING_TEST)).hasValue(EARNING_PERCENT);
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(WIB_DURATION)).isAbsent();
        });

        PolicyInformationParticipantParticipantCoverageTab.buttonNext.click();
        disabilityClaim.getDefaultWorkspace()
                .fillFromTo(disabilityClaim.getSTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY), PolicyInformationSponsorTab.class, CompleteNotificationTab.class, true);
        CompleteNotificationTab.buttonOpenClaim.click();

        LOGGER.info("TEST REN-29819: Step 7-11");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_STD"), 1);
        claim.addPayment().start();
        PaymentPaymentPaymentAllocationTab paymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment"), paymentAllocationTab.getClass(), true);
        assertThat(paymentAllocationTab.getAssetList().getAsset(IN_APPROVED_REHABILITATION_PROGRAM)).isAbsent();

        paymentAllocationTab.getAssetList().getAsset(IN_LIEU_BENEFIT).setValue("Partial Disability Benefit");
        assertThat(paymentAllocationTab.getAssetList().getAsset(IN_APPROVED_REHABILITATION_PROGRAM)).isPresent();

        PaymentPaymentPaymentAllocationTab.buttonNext.click();
        assertThat(paymentAllocationTab.getAssetList().getAsset(IN_APPROVED_REHABILITATION_PROGRAM)).hasWarningWithText("'In Approved Rehabilitation Program' is required");
    }
}
