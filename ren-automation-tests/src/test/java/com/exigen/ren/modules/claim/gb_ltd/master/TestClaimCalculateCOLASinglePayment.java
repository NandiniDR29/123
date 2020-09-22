package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.DataProviderFactory;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentCalculatorTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.common.tabs.UpdateMaximumBenefitPeriodActionTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS;
import static com.exigen.ren.main.enums.ClaimConstants.PaymentsAndRecoveriesTransactionStatus.APPROVED;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.claim.common.metadata.UpdateMaximumBenefitPeriodActionTabMetaData.MAXIMUM_BENEFIT_PERIOD_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.tabs.PaymentCalculatorTab.tableListOfPaymentAddition;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.MAX_MONTHLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.MINIMUM_MONTHLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.COST_OF_LIVING_ADJUSTMENT_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.NUMBER_OF_ADJUSTMENTS;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimCalculateCOLASinglePayment extends ClaimGroupBenefitsLTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-30557"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimCalculateColaSinglePayment() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.OPTIONS.getLabel(), COST_OF_LIVING_ADJUSTMENT_BENEFIT.getLabel()), "3%")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.OPTIONS.getLabel(), NUMBER_OF_ADJUSTMENTS.getLabel()), "5 Years")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel()), DataProviderFactory.dataOf(
                        MINIMUM_MONTHLY_BENEFIT_AMOUNT.getLabel(), "$100",
                        MAX_MONTHLY_BENEFIT_AMOUNT.getLabel(), "6000")));
        LocalDateTime currentTime = TimeSetterUtil.getInstance().getCurrentTime();

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_Without_Benefits_Without_AdditionalParties")
                .adjust(TestData.makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000")
                .adjust(TestData.makeKeyPath(policyInformationParticipantParticipantCoverageTab.getMetaKey(), BENEFIT_PERCENTAGE.getLabel()), "60"));
        claim.claimOpen().perform();
        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_LTD"));
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.updateMaximumBenefitPeriodAction()
                .perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_UpdateMaximumBenefitPeriod")
                        .adjust(TestData.makeKeyPath(UpdateMaximumBenefitPeriodActionTab.class.getSimpleName(), MAXIMUM_BENEFIT_PERIOD_THROUGH_DATE.getLabel()),
                                currentTime.plusYears(10).format(DateTimeUtils.MM_DD_YYYY)), 1);

        TestData addPaymentTestData = tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment");

        // Two cola parts should be applied to our post payment.
        // First part is from currentTime.plusDays(180) to the last day of the year.
        // Second part is from the first day of next year to currentTime.plusDays(180).plusMonths(12).minusDays(1)
        // If we have currentTime.plusDays(180) > theLastDayOfYear.minusDays(11) we add one month to the currentTime.plusDays(180).
        // We need to get ability to do post payment from 12/21/XXXX(theLastDayOfYear.minusDays(10)) to 01/20/XXXX
        LocalDateTime theLastDayOfYear = TimeSetterUtil.getInstance().getCurrentTime().with(TemporalAdjusters.lastDayOfYear());

        // If we have currentTime.plusDays(180) > theLastDayOfYear.minusDays(11) we add one month to the currentTime.plusDays(180).
        LocalDateTime fromDate = currentTime.plusDays(180).isAfter(theLastDayOfYear.minusDays(11)) ? currentTime.plusDays(180).plusMonths(1) : currentTime.plusDays(180);
        LocalDateTime paymentThroughDate = fromDate.plusMonths(12).minusDays(1);

        claim.addPayment().perform(addPaymentTestData
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_FROM_DATE.getLabel()), fromDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_THROUGH_DATE.getLabel()), paymentThroughDate.format(DateTimeUtils.MM_DD_YYYY)));
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(TRANSACTION_STATUS)).hasValue(APPROVED);
        });

        LOGGER.info("Step 1");
        PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
        claim.addPayment().start();
        LocalDateTime theLastDayOfCurrentYearMinusTenDays = paymentThroughDate.with(TemporalAdjusters.lastDayOfYear()).minusDays(10);
        String fromDateSecondPayment = theLastDayOfCurrentYearMinusTenDays.format(DateTimeUtils.MM_DD_YYYY);
        LocalDateTime throughDateSecondPayment = theLastDayOfCurrentYearMinusTenDays.plusMonths(1).minusDays(1);
        claim.addPayment().getWorkspace().fillUpTo(addPaymentTestData
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_FROM_DATE.getLabel()), theLastDayOfCurrentYearMinusTenDays.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_THROUGH_DATE.getLabel()), throughDateSecondPayment.format(DateTimeUtils.MM_DD_YYYY)), paymentCalculatorTab.getClass());

        tableListOfPaymentAddition.getRow(1).getCell(4).controls.links.get(ActionConstants.CHANGE).click();
        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(COLA_PERCENT)).isDisabled().hasValue(new Currency(3).toPlainString());
            Float colaValue = Float.valueOf(paymentCalculatorTab.getAssetList().getAsset(COLA_PERCENT).getValue());
            Currency allocationAmountValue = new Currency(paymentCalculatorTab.getAssetList().getAsset(PaymentCalculatorActionTabMetaData.ALLOCATION_AMOUNT).getValue(), RoundingMode.HALF_UP);
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(COLA_AMOUNT)).isDisabled().hasValue(allocationAmountValue.multiply(11).divide(31).multiply(colaValue / 100).toString());

            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE)).hasValue(fromDateSecondPayment);
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE)).hasValue(theLastDayOfCurrentYearMinusTenDays.with(TemporalAdjusters.lastDayOfYear()).format(DateTimeUtils.MM_DD_YYYY));
        });

        tableListOfPaymentAddition.getRow(2).getCell(4).controls.links.get(ActionConstants.CHANGE).click();
        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(COLA_PERCENT)).isDisabled().hasValue(new Currency(3).toPlainString());

            Currency allocationAmountValue = new Currency(paymentCalculatorTab.getAssetList().getAsset(PaymentCalculatorActionTabMetaData.ALLOCATION_AMOUNT).getValue(), RoundingMode.HALF_UP);
            Float colaValue = Float.valueOf(paymentCalculatorTab.getAssetList().getAsset(COLA_PERCENT).getValue());
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(COLA_AMOUNT)).isDisabled().hasValue(allocationAmountValue.multiply(20).divide(31).multiply(colaValue / 100).toString());

            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE)).hasValue(throughDateSecondPayment.with(TemporalAdjusters.firstDayOfYear()).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE)).hasValue(throughDateSecondPayment.format(DateTimeUtils.MM_DD_YYYY));
        });

        LOGGER.info("Step 2");
        claim.addPayment().getWorkspace().fillFrom(addPaymentTestData, paymentCalculatorTab.getClass());
        claim.addPayment().submit();
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(2);
        });
    }
}