package com.exigen.ren.modules.claim.gb_st.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentSeriesPaymentSeriesProfileActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantCoverageTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantInformationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTBaseTest;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.Tab.buttonCancel;
import static com.exigen.ren.common.pages.Page.dialogConfirmation;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.FREQUENCY;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.ParticipantPriorEarningsMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageDeterminationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableScheduledPaymentsOfSeries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimBiWeeklyPaymentPaymentSeriesCreated extends ClaimGroupBenefitsSTBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-32201", "REN-32401"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimBiWeeklyPaymentPaymentSeriesNJ() {
        LocalDateTime currentDate = DateTimeUtils.getCurrentDateTime();
        String weeklyAmount = "100";

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        statutoryDisabilityInsuranceMasterPolicy.createPolicy(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NJ")
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));

        disabilityClaim.create(disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), WORK_STATE.getLabel()), "NJ")
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), COVERED_EARNINGS.getLabel()), weeklyAmount)
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), PARTICIPANT_PRIOR_EARNINGS.getLabel(), WEEK_NO.getLabel()), "1")
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), PARTICIPANT_PRIOR_EARNINGS.getLabel(), NUMBER_OF_DAYS_WORKED.getLabel()), "5")
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), PARTICIPANT_PRIOR_EARNINGS.getLabel(), GROSS_AMOUNT_PAID.getLabel()), weeklyAmount)
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantCoverageTab.class.getSimpleName(), BENEFIT_PERCENTAGE.getLabel()), "100")
                .adjust(TestData.makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), currentDate.plusYears(10).format(DateTimeUtils.MM_DD_YYYY)));

        claim.claimOpen().perform();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_ST"), 1);

        Currency grossAmountLastSeriesOncePerWeek = new Currency(28.57); // Weekly amount = $100.00. Net amount = $100*2/7 = $28.57
        Currency grossAmountLastSeriesOncePer2Weeks = new Currency(128.57);  // Weekly amount = $100.00. Net amount #1 = $100*7/7 = $100.00. Net amount #2 = $100*2/7 = $28.57. Net amount for bi-weekly payment = $128.57

        commonSteps(grossAmountLastSeriesOncePerWeek, grossAmountLastSeriesOncePer2Weeks);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-32201", "REN-32401"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimBiWeeklyPaymentPaymentSeriesNY() {
        LocalDateTime currentDate = DateTimeUtils.getCurrentDateTime();
        String weeklyAmount = "100";

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        statutoryDisabilityInsuranceMasterPolicy.createPolicy(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));

        disabilityClaim.create(disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), COVERED_EARNINGS.getLabel()), weeklyAmount)
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), PARTICIPANT_PRIOR_EARNINGS.getLabel(), WEEK_NO.getLabel()), "1")
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), PARTICIPANT_PRIOR_EARNINGS.getLabel(), NUMBER_OF_DAYS_WORKED.getLabel()), "5")
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), PARTICIPANT_PRIOR_EARNINGS.getLabel(), GROSS_AMOUNT_PAID.getLabel()), weeklyAmount)
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantCoverageTab.class.getSimpleName(), BENEFIT_PERCENTAGE.getLabel()), "100")
                .adjust(TestData.makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), currentDate.plusYears(10).format(DateTimeUtils.MM_DD_YYYY)));

        claim.claimOpen().perform();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_ST"), 1);

        Currency grossAmountSecondWeek = calculateGrossAmountForSecondWeek();
        Currency grossAmountLastSeriesOncePer2Weeks = new Currency(weeklyAmount).add(grossAmountSecondWeek);  // Weekly amount = $100.00. Net amount #1 = $100*5/5 = $100.00. Net amount #2 = $100*2/5 = $40.00. Net amount for bi-weekly payment = $140.00
        Currency grossAmountLastSeriesOncePerWeek = new Currency(grossAmountSecondWeek); // Weekly amount = $100.00. Net amount = $100*2/5 = $40.00

        commonSteps(grossAmountLastSeriesOncePerWeek, grossAmountLastSeriesOncePer2Weeks);
    }

    private Currency calculateGrossAmountForSecondWeek() {
        LocalDateTime expirationDate = DateTimeUtils.getCurrentDateTime().plusWeeks(8).plusDays(1);  // REN-32201 step 4: expiration Date = Current Date + 8 weeks + 1 day

        switch (expirationDate.getDayOfWeek()) {
            case SATURDAY:
                return new Currency(20);   // one day of second week is weekend (SATURDAY)  =  100 * 1/5 = 20 (Used prorate)
            case SUNDAY:
                return new Currency(0); //  two days of second week are weekends
            default:
                return new Currency(40); //  two days of second week are business days 100 * 2/5 = 40 (Used prorate)
        }
    }

    private void commonSteps(Currency grossAmountOncePerWeek, Currency grossAmountOncePer2Weeks) {
        PaymentSeriesPaymentSeriesProfileActionTab paymentSeriesPaymentSeriesProfileActionTab = claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesPaymentSeriesProfileActionTab.class);
        LocalDateTime currentDate = DateTimeUtils.getCurrentDateTime();
        String effectiveDate = currentDate.plusDays(7).format(DateTimeUtils.MM_DD_YYYY);
        LocalDateTime expirationDate = currentDate.plusWeeks(8).plusDays(1);

        LOGGER.info("REN-32201: Steps 1-2");
        claim.createPaymentSeries().start();
        claim.createPaymentSeries().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries"), PaymentSeriesPaymentSeriesProfileActionTab.class);

        assertThat(paymentSeriesPaymentSeriesProfileActionTab.getAssetList().getAsset(FREQUENCY)).isEnabled().hasValue(EMPTY).hasOptions(EMPTY, "Once per Week", "Once per 2 Weeks");

        LOGGER.info("REN-32201: Step 3");
        paymentSeriesPaymentSeriesProfileActionTab.getAssetList().getAsset(FREQUENCY).setValue("Once per 2 Weeks");

        LOGGER.info("REN-32201: Step 4");
        paymentSeriesPaymentSeriesProfileActionTab.getAssetList().getAsset(PaymentSeriesPaymentSeriesProfileActionTabMetaData.EFFECTIVE_DATE).setValue(effectiveDate);
        paymentSeriesPaymentSeriesProfileActionTab.getAssetList().getAsset(PaymentSeriesPaymentSeriesProfileActionTabMetaData.EXPIRATION_DATE).setValue(expirationDate.format(DateTimeUtils.MM_DD_YYYY));

        paymentSeriesPaymentSeriesProfileActionTab.submitTab();
        claim.createPaymentSeries().submit();

        String paymentFromThroughDateSeries_1_1 = String.format("%s - %s", effectiveDate, currentDate.plusWeeks(2).plusDays(6).format(DateTimeUtils.MM_DD_YYYY));
        String paymentFromThroughDateSeries_1_2 = String.format("%s - %s", currentDate.plusWeeks(2).plusDays(7).format(DateTimeUtils.MM_DD_YYYY), currentDate.plusWeeks(4).plusDays(6).format(DateTimeUtils.MM_DD_YYYY));
        String paymentFromThroughDateSeries_1_3 = String.format("%s - %s", currentDate.plusWeeks(4).plusDays(7).format(DateTimeUtils.MM_DD_YYYY), currentDate.plusWeeks(6).plusDays(6).format(DateTimeUtils.MM_DD_YYYY));
        String paymentFromThroughDateSeries_1_4 = String.format("%s - %s", currentDate.plusWeeks(6).plusDays(7).format(DateTimeUtils.MM_DD_YYYY), expirationDate.format(DateTimeUtils.MM_DD_YYYY));

        LOGGER.info("REN-32201: Step 5");
        ClaimSummaryPage.tableSummaryOfClaimPaymentSeries.getRow(1).getCell(1).controls.links.getFirst().click();

        assertSoftly(softly -> {
            softly.assertThat(tableScheduledPaymentsOfSeries).hasRows(4);
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, getPaymentScheduleDate(currentDate.plusWeeks(2).plusDays(6)))
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.GROSS_AMOUNT, new Currency("200").toString())
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, paymentFromThroughDateSeries_1_1).hasMatchingRows(1);
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, getPaymentScheduleDate(currentDate.plusWeeks(4).plusDays(6)))
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.GROSS_AMOUNT, new Currency("200").toString())
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, paymentFromThroughDateSeries_1_2).hasMatchingRows(1);
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, getPaymentScheduleDate(currentDate.plusWeeks(6).plusDays(6)))
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.GROSS_AMOUNT, new Currency("200").toString())
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, paymentFromThroughDateSeries_1_3).hasMatchingRows(1);
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, getPaymentScheduleDate(expirationDate))
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.GROSS_AMOUNT, new Currency(grossAmountOncePer2Weeks).toString())
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, paymentFromThroughDateSeries_1_4).hasMatchingRows(1);
        });

        buttonCancel.click();
        dialogConfirmation.confirm();

        LOGGER.info("REN-32401: Step 1");
        claim.updatePaymentSeries().start(1);
        paymentSeriesPaymentSeriesProfileActionTab.navigateToTab();
        assertThat(paymentSeriesPaymentSeriesProfileActionTab.getAssetList().getAsset(FREQUENCY)).hasOptions("Once per Week", "Once per 2 Weeks");

        LOGGER.info("REN-32401: Step 2");
        paymentSeriesPaymentSeriesProfileActionTab.getAssetList().getAsset(FREQUENCY).setValue("Once per Week");
        claim.updatePaymentSeries().submit();

        LOGGER.info("REN-32401: Step 3");
        String paymentFromThroughDateSeries_2_1 = String.format("%s - %s", effectiveDate, currentDate.plusWeeks(1).plusDays(6).format(DateTimeUtils.MM_DD_YYYY));
        String paymentFromThroughDateSeries_2_2 = String.format("%s - %s", currentDate.plusWeeks(1).plusDays(7).format(DateTimeUtils.MM_DD_YYYY), currentDate.plusWeeks(2).plusDays(6).format(DateTimeUtils.MM_DD_YYYY));
        String paymentFromThroughDateSeries_2_3 = String.format("%s - %s", currentDate.plusWeeks(2).plusDays(7).format(DateTimeUtils.MM_DD_YYYY), currentDate.plusWeeks(3).plusDays(6).format(DateTimeUtils.MM_DD_YYYY));
        String paymentFromThroughDateSeries_2_4 = String.format("%s - %s", currentDate.plusWeeks(3).plusDays(7).format(DateTimeUtils.MM_DD_YYYY), currentDate.plusWeeks(4).plusDays(6).format(DateTimeUtils.MM_DD_YYYY));
        String paymentFromThroughDateSeries_2_5 = String.format("%s - %s", currentDate.plusWeeks(4).plusDays(7).format(DateTimeUtils.MM_DD_YYYY), currentDate.plusWeeks(5).plusDays(6).format(DateTimeUtils.MM_DD_YYYY));
        String paymentFromThroughDateSeries_2_6 = String.format("%s - %s", currentDate.plusWeeks(5).plusDays(7).format(DateTimeUtils.MM_DD_YYYY), currentDate.plusWeeks(6).plusDays(6).format(DateTimeUtils.MM_DD_YYYY));
        String paymentFromThroughDateSeries_2_7 = String.format("%s - %s", currentDate.plusWeeks(6).plusDays(7).format(DateTimeUtils.MM_DD_YYYY), currentDate.plusWeeks(7).plusDays(6).format(DateTimeUtils.MM_DD_YYYY));
        String paymentFromThroughDateSeries_2_8 = String.format("%s - %s", currentDate.plusWeeks(7).plusDays(7).format(DateTimeUtils.MM_DD_YYYY), expirationDate.format(DateTimeUtils.MM_DD_YYYY));

        ClaimSummaryPage.tableSummaryOfClaimPaymentSeries.getRow(1).getCell(1).controls.links.getFirst().click();
        assertSoftly(softly -> {
            softly.assertThat(tableScheduledPaymentsOfSeries).hasRows(8);
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, getPaymentScheduleDate(currentDate.plusWeeks(1).plusDays(6)))
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.GROSS_AMOUNT, new Currency("100").toString())
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, paymentFromThroughDateSeries_2_1).hasMatchingRows(1);
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, getPaymentScheduleDate(currentDate.plusWeeks(2).plusDays(6)))
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.GROSS_AMOUNT, new Currency("100").toString())
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, paymentFromThroughDateSeries_2_2).hasMatchingRows(1);
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, getPaymentScheduleDate(currentDate.plusWeeks(3).plusDays(6)))
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.GROSS_AMOUNT, new Currency("100").toString())
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, paymentFromThroughDateSeries_2_3).hasMatchingRows(1);
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, getPaymentScheduleDate(currentDate.plusWeeks(4).plusDays(6)))
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.GROSS_AMOUNT, new Currency("100").toString())
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, paymentFromThroughDateSeries_2_4).hasMatchingRows(1);
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, getPaymentScheduleDate(currentDate.plusWeeks(5).plusDays(6)))
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.GROSS_AMOUNT, new Currency("100").toString())
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, paymentFromThroughDateSeries_2_5).hasMatchingRows(1);
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, getPaymentScheduleDate(currentDate.plusWeeks(6).plusDays(6)))
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.GROSS_AMOUNT, new Currency("100").toString())
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, paymentFromThroughDateSeries_2_6).hasMatchingRows(1);
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, getPaymentScheduleDate(currentDate.plusWeeks(7).plusDays(6)))
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.GROSS_AMOUNT, new Currency("100").toString())
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, paymentFromThroughDateSeries_2_7).hasMatchingRows(1);
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, getPaymentScheduleDate(expirationDate))
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.GROSS_AMOUNT, new Currency(grossAmountOncePerWeek).toString())
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, paymentFromThroughDateSeries_2_8).hasMatchingRows(1);
        });

        buttonCancel.click();
        dialogConfirmation.confirm();

        LOGGER.info("REN-32401: Step 4");
        claim.updatePaymentSeries().start(1);
        paymentSeriesPaymentSeriesProfileActionTab.navigateToTab();
        assertThat(paymentSeriesPaymentSeriesProfileActionTab.getAssetList().getAsset(FREQUENCY)).hasOptions("Once per Week", "Once per 2 Weeks");

        paymentSeriesPaymentSeriesProfileActionTab.getAssetList().getAsset(FREQUENCY).setValue("Once per 2 Weeks");
        claim.updatePaymentSeries().submit();

        LOGGER.info("REN-32401: Step 5");
        ClaimSummaryPage.tableSummaryOfClaimPaymentSeries.getRow(1).getCell(1).controls.links.getFirst().click();

        assertSoftly(softly -> {
            softly.assertThat(tableScheduledPaymentsOfSeries).hasRows(4);
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, getPaymentScheduleDate(currentDate.plusWeeks(2).plusDays(6)))
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.GROSS_AMOUNT, new Currency("200").toString())
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, paymentFromThroughDateSeries_1_1).hasMatchingRows(1);
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, getPaymentScheduleDate(currentDate.plusWeeks(4).plusDays(6)))
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.GROSS_AMOUNT, new Currency("200").toString())
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, paymentFromThroughDateSeries_1_2).hasMatchingRows(1);
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, getPaymentScheduleDate(currentDate.plusWeeks(6).plusDays(6)))
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.GROSS_AMOUNT, new Currency("200").toString())
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, paymentFromThroughDateSeries_1_3).hasMatchingRows(1);
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, getPaymentScheduleDate(expirationDate))
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.GROSS_AMOUNT, new Currency(grossAmountOncePer2Weeks).toString())
                    .with(TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE, paymentFromThroughDateSeries_1_4).hasMatchingRows(1);
        });
    }

    private String getPaymentScheduleDate(LocalDateTime paymentScheduleDate) {
//         If the day is not a business day then will display the day before
        if (paymentScheduleDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            return paymentScheduleDate.minusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        }
        return paymentScheduleDate.format(DateTimeUtils.MM_DD_YYYY);
    }
}