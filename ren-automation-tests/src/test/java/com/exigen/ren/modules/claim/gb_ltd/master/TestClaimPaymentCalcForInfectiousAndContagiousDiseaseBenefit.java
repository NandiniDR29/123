package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.OtherIncomeBenefitActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantCoverageTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantInformationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.joda.time.Days;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.pages.Page.dialogConfirmation;
import static com.exigen.ren.main.enums.ValueConstants.NONE;
import static com.exigen.ren.main.modules.claim.common.metadata.OtherIncomeBenefitActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab.buttonPostPayment;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.INFECTIOUS_DISEASE;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static java.time.temporal.ChronoUnit.DAYS;

public class TestClaimPaymentCalcForInfectiousAndContagiousDiseaseBenefit extends ClaimGroupBenefitsLTDBaseTest {

    private static final String INFECTIOUS_AND_CONTAGIOUS_DISEASE_BENEFIT = "Infectious and Contagious Disease Benefit";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-25776", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPaymentCalcForInfectiousAndContagiousBenefitInfectiousDiseaseNone() {
        PaymentPaymentPaymentAllocationTab paymentPaymentPaymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), INFECTIOUS_DISEASE.getLabel()), NONE));

        claim.create(tdClaim.getTestData(TestDataKey.DATA_GATHER, "TestData_Without_Benefits"));

        LOGGER.info("TEST: Step 1");
        claim.claimOpen().perform();

        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_LTD"));

        LOGGER.info("TEST: Step 2");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment"), PaymentPaymentPaymentAllocationTab.class);

        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(RESERVE_TYPE).setValue("Indemnity");
        assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(IN_LIEU_BENEFIT).getAllValues()).doesNotContain(INFECTIOUS_AND_CONTAGIOUS_DISEASE_BENEFIT);
    }


    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-25776", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPaymentCalcForInfectiousAndContagiousBenefitInfectiousDiseaseIncluded() {
        PaymentPaymentPaymentAllocationTab paymentPaymentPaymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), INFECTIOUS_DISEASE.getLabel()), "6 Months"));

        TestData tdClaim2 = disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_Without_Benefits")
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS.getLabel()), "5000")
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantCoverageTab.class.getSimpleName(), PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_PERCENTAGE.getLabel()), "60");

        LOGGER.info("TEST: Step 3");
        disabilityClaim.create(tdClaim2);
        claim.claimOpen().perform();

        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_LTD"));
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime BPStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment"), PaymentPaymentPaymentAllocationTab.class);
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(RESERVE_TYPE).setValue("Indemnity");

        LOGGER.info("TEST: Step 4");
        assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(IN_LIEU_BENEFIT)).hasValue(ValueConstants.EMPTY).containsOption(INFECTIOUS_AND_CONTAGIOUS_DISEASE_BENEFIT);

        LOGGER.info("TEST: Step 5");
        claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class).fillTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), IN_LIEU_BENEFIT.getLabel()), INFECTIOUS_AND_CONTAGIOUS_DISEASE_BENEFIT));

        buttonPostPayment.click();
        Page.dialogConfirmation.confirm();

        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)
                .getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.NOTE).applyConfiguration("ClaimPaymentSummaryAndRecoveries")
                .getHintValue()).contains(INFECTIOUS_AND_CONTAGIOUS_DISEASE_BENEFIT);

        LOGGER.info("TEST: Step 6");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD")
                .adjust(TestData.makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName()), new SimpleDataProvider().adjust(TYPE_OF_OFFSET.getLabel(), "index=1"))
                .adjust(TestData.makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), PAYMENT_AMOUNT.getLabel()), "100"), 1);

        verifyAllocationAmount(
                BPStartDate,
                BPStartDate.plusMonths(1).minusDays(1),
                "3000",
                "1-2");

        LOGGER.info("TEST: Step 7");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD")
                .adjust(TestData.makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName()), new SimpleDataProvider().adjust(TYPE_OF_OFFSET.getLabel(), "index=1"))
                .adjust(TestData.makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), PAYMENT_AMOUNT.getLabel()), "1500")
                .adjust(TestData.makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), BEGINNING_DATE.getLabel()), BPStartDate.plusDays(10).minusMonths(2).format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), THROUGH_DATE.getLabel()), BPStartDate.plusDays(10).format(MM_DD_YYYY)), 1);

        LocalDateTime paymentThroughDate;

        if((DAYS.between(BPStartDate, BPStartDate.plusMonths(1))==31)){
            paymentThroughDate=BPStartDate.plusMonths(1).minusDays(1); // (if it is 31 days)
        }else {
            paymentThroughDate=BPStartDate.plusMonths(1);
        }

        verifyAllocationAmount(
                BPStartDate,
                paymentThroughDate,
                "2770",
                "1-3");

        LOGGER.info("TEST: Step 8");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD")
                .adjust(TestData.makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName()), new SimpleDataProvider().adjust(TYPE_OF_OFFSET.getLabel(), "index=1"))
                .adjust(TestData.makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), PAYMENT_AMOUNT.getLabel()), "1500")
                .adjust(TestData.makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), BEGINNING_DATE.getLabel()), BPStartDate.plusDays(10).minusMonths(2).format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), THROUGH_DATE.getLabel()), BPStartDate.plusDays(10).format(MM_DD_YYYY)), 1);

        verifyAllocationAmount(
                BPStartDate.plusMonths(1),
                BPStartDate.plusMonths(2).minusDays(1),
                "3000",
                "1-4");
    }

    private void verifyAllocationAmount(LocalDateTime paymentFromDate, LocalDateTime paymentThroughDate, String allocationAmountExpected, String singleBenefitNumber) {
        PaymentPaymentPaymentAllocationTab paymentPaymentPaymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);

        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment"), PaymentPaymentPaymentAllocationTab.class);

        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(RESERVE_TYPE).setValue("Indemnity");
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(IN_LIEU_BENEFIT).setValue(INFECTIOUS_AND_CONTAGIOUS_DISEASE_BENEFIT);
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(paymentFromDate.format(MM_DD_YYYY));
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE).setValue(paymentThroughDate.format(MM_DD_YYYY));
        paymentPaymentPaymentAllocationTab.setSingleBenefitCalculationNumber(singleBenefitNumber);

        assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(new Currency(allocationAmountExpected).toString());
        Tab.buttonCancel.click();
        dialogConfirmation.confirm();
    }
}