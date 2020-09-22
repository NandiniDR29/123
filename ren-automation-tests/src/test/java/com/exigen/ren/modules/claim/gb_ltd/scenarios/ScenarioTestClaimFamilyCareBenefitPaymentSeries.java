package com.exigen.ren.modules.claim.gb_ltd.scenarios;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentSeriesCalculatorActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentSeriesPaymentSeriesProfileActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.UpdateMaximumBenefitPeriodActionTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentSeriesTableExtended.SERIES_NUMBER;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData.BUTTON_ADD_PAYMENT_ADDITION;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION;
import static com.exigen.ren.main.modules.claim.common.metadata.UpdateMaximumBenefitPeriodActionTabMetaData.MAXIMUM_BENEFIT_PERIOD_THROUGH_DATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableScheduledPaymentsOfSeries;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentSeries;
import static java.time.temporal.TemporalAdjusters.*;

public class ScenarioTestClaimFamilyCareBenefitPaymentSeries extends ClaimGroupBenefitsLTDBaseTest {

    private static final String FAMILY_CARE_BENEFIT_NAME = "Family Care Benefit";
    private double reMaxForAll = 2000;
    private double amtPerMember = 350;

    public void testClaimFamilyCareBenefitPaymentSeries() {
        PaymentSeriesCalculatorActionTab paymentSeriesCalculatorActionTab = claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesCalculatorActionTab.class);

        claim.claimOpen().perform();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        String currentDatePlus10Y = TimeSetterUtil.getInstance().getCurrentTime().plusYears(10).format(MM_DD_YYYY);

        claim.updateMaximumBenefitPeriodAction().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_UpdateMaximumBenefitPeriod")
                .adjust(TestData.makeKeyPath(UpdateMaximumBenefitPeriodActionTab.class.getSimpleName(), MAXIMUM_BENEFIT_PERIOD_THROUGH_DATE.getLabel()), currentDatePlus10Y)
                .resolveLinks(), 1);

        LOGGER.info("Test. Step 1");
        TestData td = tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries")
                .adjust(TestData.makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), PaymentSeriesPaymentSeriesProfileActionTabMetaData.EFFECTIVE_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), PaymentSeriesPaymentSeriesProfileActionTabMetaData.EXPIRATION_DATE.getLabel()), bpStartDate.plusYears(1).minusDays(1).format(MM_DD_YYYY))
                .resolveLinks();

        disabilityClaim.createPaymentSeries().start();
        disabilityClaim.createPaymentSeries().getWorkspace().fillUpTo(td, paymentSeriesCalculatorActionTab.getClass());

        paymentSeriesCalculatorActionTab.getAssetList().getAsset(BUTTON_ADD_PAYMENT_ADDITION).click();

        MultiAssetList paymentAdditionAssetList = paymentSeriesCalculatorActionTab.getAssetList().getAsset(PAYMENT_ADDITION);

        paymentAdditionAssetList.getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.ADDITION_TYPE).setValue(FAMILY_CARE_BENEFIT_NAME);
        paymentAdditionAssetList.getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.NUMBER_OF_FAMILY_MEMBERS).setValue("1");
        paymentAdditionAssetList.getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.BEGINNING_DATE).setValue(bpStartDate.format(MM_DD_YYYY));
        paymentAdditionAssetList.getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.THROUGH_DATE).setValue(bpStartDate.plusYears(1).minusDays(1).format(MM_DD_YYYY));

        assertThat(paymentAdditionAssetList.getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.REMAINING_BENEFIT_MAXIMUM)).hasValue(new Currency(2000).toString());

        Currency totalBenefitAmount = calculateTotalBenefitAmount(bpStartDate, bpStartDate.plusYears(1).minusDays(1));

        assertThat(paymentAdditionAssetList.getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.TOTAL_BENEFIT_AMOUNT)).hasValue(totalBenefitAmount.toString());

        LOGGER.info("Test. Step 2");
        PaymentSeriesCalculatorActionTab.buttonCreatePaymentSeries.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("Test. Step 3");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.PAYMENTS.get());
        tableSummaryOfClaimPaymentSeries.getRow(1).getCell(SERIES_NUMBER.getName()).controls.links.getFirst().click();

        String paymentFromThroughDateSeries1 = String.format("%s - %s", bpStartDate.format(MM_DD_YYYY), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY));
        String paymentFromThroughDateSeries2 = String.format("%s - %s", bpStartDate.plusMonths(1).format(MM_DD_YYYY), bpStartDate.plusMonths(2).minusDays(1).format(MM_DD_YYYY));
        String paymentFromThroughDateSeries3 = String.format("%s - %s", bpStartDate.plusMonths(2).format(MM_DD_YYYY), bpStartDate.plusMonths(3).minusDays(1).format(MM_DD_YYYY));
        String paymentFromThroughDateSeries4 = String.format("%s - %s", bpStartDate.plusMonths(3).format(MM_DD_YYYY), bpStartDate.plusMonths(4).minusDays(1).format(MM_DD_YYYY));
        String paymentFromThroughDateSeries5 = String.format("%s - %s", bpStartDate.plusMonths(4).format(MM_DD_YYYY), bpStartDate.plusMonths(5).minusDays(1).format(MM_DD_YYYY));
        String paymentFromThroughDateSeries6 = String.format("%s - %s", bpStartDate.plusMonths(5).format(MM_DD_YYYY), bpStartDate.plusMonths(6).minusDays(1).format(MM_DD_YYYY));
        String paymentFromThroughDateSeries7 = String.format("%s - %s", bpStartDate.plusMonths(6).format(MM_DD_YYYY), bpStartDate.plusMonths(7).minusDays(1).format(MM_DD_YYYY));
        String paymentFromThroughDateSeries8 = String.format("%s - %s", bpStartDate.plusMonths(7).format(MM_DD_YYYY), bpStartDate.plusMonths(8).minusDays(1).format(MM_DD_YYYY));
        String paymentFromThroughDateSeries9 = String.format("%s - %s", bpStartDate.plusMonths(8).format(MM_DD_YYYY), bpStartDate.plusMonths(9).minusDays(1).format(MM_DD_YYYY));
        String paymentFromThroughDateSeries10 = String.format("%s - %s", bpStartDate.plusMonths(9).format(MM_DD_YYYY), bpStartDate.plusMonths(10).minusDays(1).format(MM_DD_YYYY));
        String paymentFromThroughDateSeries11 = String.format("%s - %s", bpStartDate.plusMonths(10).format(MM_DD_YYYY), bpStartDate.plusMonths(11).minusDays(1).format(MM_DD_YYYY));
        String paymentFromThroughDateSeries12 = String.format("%s - %s", bpStartDate.plusMonths(11).format(MM_DD_YYYY), bpStartDate.plusMonths(12).minusDays(1).format(MM_DD_YYYY));

        assertSoftly(softly -> {
            softly.assertThat(tableScheduledPaymentsOfSeries.findRow(PAYMENT_FROM_THROUGH_DATE.getName(), paymentFromThroughDateSeries1))
                    .hasCellWithValue(GROSS_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(ADJUSTED_GROSS_BENEFIT_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(PAYMENT_AMOUNT.getName(), calculatePaymentAmount(bpStartDate, bpStartDate.plusMonths(1).minusDays(1)).toString());

            softly.assertThat(tableScheduledPaymentsOfSeries.findRow(PAYMENT_FROM_THROUGH_DATE.getName(), paymentFromThroughDateSeries2))
                    .hasCellWithValue(GROSS_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(ADJUSTED_GROSS_BENEFIT_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(PAYMENT_AMOUNT.getName(), calculatePaymentAmount(bpStartDate.plusMonths(1), bpStartDate.plusMonths(2).minusDays(1)).toString());

            softly.assertThat(tableScheduledPaymentsOfSeries.findRow(PAYMENT_FROM_THROUGH_DATE.getName(), paymentFromThroughDateSeries3))
                    .hasCellWithValue(GROSS_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(ADJUSTED_GROSS_BENEFIT_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(PAYMENT_AMOUNT.getName(), calculatePaymentAmount(bpStartDate.plusMonths(2), bpStartDate.plusMonths(3).minusDays(1)).toString());

            softly.assertThat(tableScheduledPaymentsOfSeries.findRow(PAYMENT_FROM_THROUGH_DATE.getName(), paymentFromThroughDateSeries4))
                    .hasCellWithValue(GROSS_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(ADJUSTED_GROSS_BENEFIT_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(PAYMENT_AMOUNT.getName(), calculatePaymentAmount(bpStartDate.plusMonths(3), bpStartDate.plusMonths(4).minusDays(1)).toString());

            softly.assertThat(tableScheduledPaymentsOfSeries.findRow(PAYMENT_FROM_THROUGH_DATE.getName(), paymentFromThroughDateSeries5))
                    .hasCellWithValue(GROSS_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(ADJUSTED_GROSS_BENEFIT_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(PAYMENT_AMOUNT.getName(), calculatePaymentAmount(bpStartDate.plusMonths(4), bpStartDate.plusMonths(5).minusDays(1)).toString());

            softly.assertThat(tableScheduledPaymentsOfSeries.findRow(PAYMENT_FROM_THROUGH_DATE.getName(), paymentFromThroughDateSeries6))
                    .hasCellWithValue(GROSS_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(ADJUSTED_GROSS_BENEFIT_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(PAYMENT_AMOUNT.getName(), calculatePaymentAmount(bpStartDate.plusMonths(5), bpStartDate.plusMonths(6).minusDays(1)).toString());

            softly.assertThat(tableScheduledPaymentsOfSeries.findRow(PAYMENT_FROM_THROUGH_DATE.getName(), paymentFromThroughDateSeries7))
                    .hasCellWithValue(GROSS_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(ADJUSTED_GROSS_BENEFIT_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(PAYMENT_AMOUNT.getName(), calculatePaymentAmount(bpStartDate.plusMonths(6), bpStartDate.plusMonths(7).minusDays(1)).toString());

            softly.assertThat(tableScheduledPaymentsOfSeries.findRow(PAYMENT_FROM_THROUGH_DATE.getName(), paymentFromThroughDateSeries8))
                    .hasCellWithValue(GROSS_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(ADJUSTED_GROSS_BENEFIT_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(PAYMENT_AMOUNT.getName(), calculatePaymentAmount(bpStartDate.plusMonths(7), bpStartDate.plusMonths(8).minusDays(1)).toString());

            softly.assertThat(tableScheduledPaymentsOfSeries.findRow(PAYMENT_FROM_THROUGH_DATE.getName(), paymentFromThroughDateSeries9))
                    .hasCellWithValue(GROSS_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(ADJUSTED_GROSS_BENEFIT_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(PAYMENT_AMOUNT.getName(), calculatePaymentAmount(bpStartDate.plusMonths(8), bpStartDate.plusMonths(9).minusDays(1)).toString());

            softly.assertThat(tableScheduledPaymentsOfSeries.findRow(PAYMENT_FROM_THROUGH_DATE.getName(), paymentFromThroughDateSeries10))
                    .hasCellWithValue(GROSS_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(ADJUSTED_GROSS_BENEFIT_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(PAYMENT_AMOUNT.getName(), calculatePaymentAmount(bpStartDate.plusMonths(9), bpStartDate.plusMonths(10).minusDays(1)).toString());

            softly.assertThat(tableScheduledPaymentsOfSeries.findRow(PAYMENT_FROM_THROUGH_DATE.getName(), paymentFromThroughDateSeries11))
                    .hasCellWithValue(GROSS_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(ADJUSTED_GROSS_BENEFIT_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(PAYMENT_AMOUNT.getName(), calculatePaymentAmount(bpStartDate.plusMonths(10), bpStartDate.plusMonths(11).minusDays(1)).toString());

            softly.assertThat(tableScheduledPaymentsOfSeries.findRow(PAYMENT_FROM_THROUGH_DATE.getName(), paymentFromThroughDateSeries12))
                    .hasCellWithValue(GROSS_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(ADJUSTED_GROSS_BENEFIT_AMOUNT.getName(), new Currency(6000).toString())
                    .hasCellWithValue(PAYMENT_AMOUNT.getName(), calculatePaymentAmount(bpStartDate.plusMonths(11), bpStartDate.plusMonths(12).minusDays(1)).toString());
        });

        LOGGER.info("Test. Step 4");
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        claim.updatePaymentSeries().start(1);
        paymentSeriesCalculatorActionTab.navigate();

        paymentAdditionAssetList.getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.BEGINNING_DATE).setValue(bpStartDate.plusMonths(3).format(MM_DD_YYYY));
        paymentAdditionAssetList.getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.THROUGH_DATE).setValue(bpStartDate.plusMonths(6).minusDays(1).format(MM_DD_YYYY));

        assertThat(paymentAdditionAssetList.getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.REMAINING_BENEFIT_MAXIMUM)).hasValue(new Currency(2000).toString());

        Currency totalBenefitAmountUpdate = calculateTotalBenefitAmount(bpStartDate.plusMonths(3), bpStartDate.plusMonths(6).minusDays(1));
        assertThat(paymentAdditionAssetList.getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.TOTAL_BENEFIT_AMOUNT)).hasValue(totalBenefitAmountUpdate.toString());
    }

    private Currency calculateTotalBenefitAmount(LocalDateTime beginningDate, LocalDateTime throughDate) {
        double reMaxForAll = 2000;

        if (!(beginningDate.isBefore(beginningDate.with(lastDayOfYear())) && throughDate.isAfter(beginningDate.with(firstDayOfNextYear())))) {  // beginningDate and throughDate in the same year
            return new Currency(amtPerMember * 3); //  prorated (AmtPerMember * NoOfMember * OverlapMonth) = $350*1*3 = $1050
        }

        Period periodBeforeNewYear = Period.between(beginningDate.toLocalDate(), beginningDate.with(firstDayOfNextYear()).toLocalDate());   // calculating period before New Year
        int monthsInCurrentYear = periodBeforeNewYear.getMonths();
        int daysInCurrentYear = periodBeforeNewYear.getDays();

        double totalAmountForCurrentYear = (amtPerMember * monthsInCurrentYear) + (amtPerMember * daysInCurrentYear / 30);  // prorated (AmtPerMember * NoOfMember * OverlapMonth) eg  $350*1*(1 month 29 days) = $350*1*1 + $350*1*29/30
        totalAmountForCurrentYear = Double.parseDouble(new DecimalFormat("##.##").format(totalAmountForCurrentYear));

        Period periodAfterNewYear = Period.between(throughDate.with(firstDayOfYear()).minusDays(1).toLocalDate(), throughDate.toLocalDate());  // calculating period after New Year
        int monthsInNextYear = periodAfterNewYear.getMonths();
        int daysInNextYear = periodAfterNewYear.getDays();

        double totalAmountForNextYear = (amtPerMember * monthsInNextYear) + (amtPerMember * daysInNextYear / 30);  // prorated (AmtPerMember * NoOfMember * OverlapMonth) eg  $350*1*(1 month 29 days) = $350*1*1 + $350*1*29/30
        totalAmountForNextYear = Double.parseDouble(new DecimalFormat("##.##").format(totalAmountForNextYear));

        if (totalAmountForCurrentYear > reMaxForAll && totalAmountForNextYear > reMaxForAll) {     // IF AmtPerMember > ReMaxForAll THEN Family Care Benefit Amount for current month = ReMaxForAll  (prorate as need)
            return new Currency(reMaxForAll + reMaxForAll);     // sum of reMaxForAll for current year and reMaxForAll for next year
        } else if (reMaxForAll < totalAmountForCurrentYear) {
            return new Currency(totalAmountForNextYear + reMaxForAll, RoundingMode.HALF_DOWN);
        } else if (totalAmountForNextYear > reMaxForAll) {
            return new Currency(totalAmountForCurrentYear + reMaxForAll, RoundingMode.HALF_DOWN);
        } else {
            return new Currency(totalAmountForCurrentYear + totalAmountForNextYear, RoundingMode.HALF_DOWN);  // totalAmountForCurrentYear < reMaxForAll and totalAmountForNextYear < reMaxForAll
        }
    }

    private Currency calculatePaymentAmount(LocalDateTime paymentFromDate, LocalDateTime paymentThroughDate) {
        double paymentAmount;

        if (paymentFromDate.isBefore(paymentFromDate.with(lastDayOfYear())) && paymentThroughDate.isAfter(paymentFromDate.with(firstDayOfNextYear()))) {    // beginningDate and throughDate in the different years

            Period periodBeforeNewYear = Period.between(paymentFromDate.toLocalDate(), paymentFromDate.with(firstDayOfNextYear()).toLocalDate());   // calculating period before New Year
            int monthsInCurrentYear = periodBeforeNewYear.getMonths();
            int daysInCurrentYear = periodBeforeNewYear.getDays();

            double totalAmountForCurrentYear = (amtPerMember * monthsInCurrentYear) + (amtPerMember * daysInCurrentYear / 30);   // prorated (AmtPerMember * NoOfMember * OverlapMonth)  for Current Year
            totalAmountForCurrentYear = Double.parseDouble(new DecimalFormat("##.##").format(totalAmountForCurrentYear));

            Period periodAfterNewYear = Period.between(paymentThroughDate.with(firstDayOfYear()).minusDays(1).toLocalDate(), paymentThroughDate.toLocalDate());     // calculating period after New Year
            int monthsInNextYear = periodAfterNewYear.getMonths();
            int daysInNextYear = periodAfterNewYear.getDays();

            double totalAmountForNextYear = (amtPerMember * monthsInNextYear) + (amtPerMember * daysInNextYear / 30);       // prorated (AmtPerMember * NoOfMember * OverlapMonth)  for Next Year
            totalAmountForNextYear = Double.parseDouble(new DecimalFormat("##.##").format(totalAmountForNextYear));

            if (amtPerMember > reMaxForAll) {       // IF AmtPerMember * NoOfMember > ReMaxForAll THEN Family Care Benefit Amount for current month = ReMaxForAll  (prorate as need)
                reMaxForAll = 2000;
                return new Currency(totalAmountForNextYear + 6000, RoundingMode.DOWN);
            } else {
                reMaxForAll = 2000;
                return new Currency(totalAmountForCurrentYear + totalAmountForNextYear + 6000, RoundingMode.DOWN);  // sum of total Amount For Current Year, total Amount For Next Year and Allocation Amount
            }
        } else if (amtPerMember > reMaxForAll) {     // IF AmtPerMember * NoOfMember > ReMaxForAll THEN Family Care Benefit Amount for current month = ReMaxForAll  (prorate as need)
            paymentAmount = reMaxForAll + 6000;
            reMaxForAll = 0;
            return new Currency(paymentAmount);
        } else {
            reMaxForAll = reMaxForAll - amtPerMember;  // After each monthly benefit amount calculation, ReMaxForAll will be changed
            return new Currency(amtPerMember + 6000, RoundingMode.DOWN);
        }
    }
}