package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.DataProviderFactory;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentSeriesCalculatorActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentSeriesPaymentSeriesProfileActionTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitCoverageEvaluationTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.IntStream;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.PAYMENTS;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS;
import static com.exigen.ren.main.enums.ClaimConstants.PaymentsAndRecoveriesTransactionStatus.APPROVED;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentSeriesTableExtended.SERIES_NUMBER;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageEvaluationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.MAX_MONTHLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.MINIMUM_MONTHLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.COST_OF_LIVING_ADJUSTMENT_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.NUMBER_OF_ADJUSTMENTS;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableScheduledPaymentsOfSeries;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentSeries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimCalculateCOLAPaymentSeries extends ClaimGroupBenefitsLTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-30558"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimCalculateColaSinglePayment() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.OPTIONS.getLabel(), COST_OF_LIVING_ADJUSTMENT_BENEFIT.getLabel()), "3%")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.OPTIONS.getLabel(), NUMBER_OF_ADJUSTMENTS.getLabel()), "5 Years")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel()), DataProviderFactory.dataOf(
                        MINIMUM_MONTHLY_BENEFIT_AMOUNT.getLabel(), "$100",
                        // TODO (ybandarenka) https://jira.exigeninsurance.com/browse/REN-30558?focusedCommentId=4844977&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#comment-4844977
                        MAX_MONTHLY_BENEFIT_AMOUNT.getLabel(), "6000")));
        LocalDateTime currentTime = TimeSetterUtil.getInstance().getCurrentTime();

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_Without_Benefits_Without_AdditionalParties")
                .adjust(TestData.makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000")
                .adjust(TestData.makeKeyPath(policyInformationParticipantParticipantCoverageTab.getMetaKey(), BENEFIT_PERCENTAGE.getLabel()), "60"));
        claim.claimOpen().perform();
        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_LTD")
                .adjust(TestData.makeKeyPath(BenefitCoverageEvaluationTab.class.getSimpleName(), APPROVED_THROUGH_DATE.getLabel()), currentTime.plusYears(10).format(DateTimeUtils.MM_DD_YYYY)));
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        TestData addPaymentTestData = tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment");

        LocalDateTime theLastDayOfYear = TimeSetterUtil.getInstance().getCurrentTime().with(TemporalAdjusters.lastDayOfYear());
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
        LocalDateTime theLastDayOfCurrentYearMinusTenDays = paymentThroughDate.with(TemporalAdjusters.lastDayOfYear()).minusDays(10);
        PaymentSeriesCalculatorActionTab paymentSeriesCalculatorActionTab = claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesCalculatorActionTab.class);
        claim.createPaymentSeries().start();

        String effectiveDate = theLastDayOfCurrentYearMinusTenDays.format(DateTimeUtils.MM_DD_YYYY);
        String expirationDate = theLastDayOfCurrentYearMinusTenDays.plusYears(1).plusDays(19).format(DateTimeUtils.MM_DD_YYYY);

        claim.createPaymentSeries().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries")
                .adjust(TestData.makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(),
                        PaymentSeriesPaymentSeriesProfileActionTabMetaData.EFFECTIVE_DATE.getLabel()), effectiveDate)
                .adjust(TestData.makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(),
                        PaymentSeriesPaymentSeriesProfileActionTabMetaData.EXPIRATION_DATE.getLabel()), expirationDate), paymentSeriesCalculatorActionTab.getClass());

        assertSoftly(softly -> {
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.COLA_PERCENT)).isDisabled().hasValue(new Currency(3).toPlainString());
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.COLA_AMOUNT)).isDisabled().hasValue(new Currency(600).multiply(0.03).toString());
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.BEGINNING_DATE)).isDisabled().hasValue(effectiveDate);
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.THROUGH_DATE)).isDisabled().hasValue(expirationDate);
        });

        LOGGER.info("Step 2");
        claim.createPaymentSeries().getWorkspace().fillFrom(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries"), paymentSeriesCalculatorActionTab.getClass());
        claim.createPaymentSeries().submit();

        toSubTab(PAYMENTS);

        assertSoftly(softly -> {
            softly.assertThat(tableSummaryOfClaimPaymentSeries).hasMatchingRows(1, ImmutableMap.of(
                    TableConstants.ClaimSummaryOfPaymentSeriesTableExtended.EFFECTIVE_DATE.getName(), effectiveDate,
                    TableConstants.ClaimSummaryOfPaymentSeriesTableExtended.EXPIRATION_DATE.getName(), expirationDate));
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(TRANSACTION_STATUS)).hasValue(APPROVED);
        });

        LOGGER.info("Step 3");
        tableSummaryOfClaimPaymentSeries.getRow(1).getCell(SERIES_NUMBER.getName()).controls.links.getFirst().click();
        List<TestData> tableScheduledPaymentsOfSeriesContinuousValue = tableScheduledPaymentsOfSeries.getContinuousValue();

        assertSoftly(softly -> {
            // From the first row to the last -1
            IntStream.range(0, tableScheduledPaymentsOfSeriesContinuousValue.size() - 1).forEach(index -> {
                softly.assertThat(tableScheduledPaymentsOfSeriesContinuousValue.get(index).getValue(GROSS_AMOUNT.getName())).isEqualTo(new Currency(600).toString());
                softly.assertThat(tableScheduledPaymentsOfSeriesContinuousValue.get(index).getValue(PAYMENT_AMOUNT.getName()))
                        .isEqualTo(new Currency(600).add(new Currency(600).multiply(0.03)).toString());
                softly.assertThat(tableScheduledPaymentsOfSeriesContinuousValue.get(index).getValue(PAYMENT_FROM_THROUGH_DATE.getName()))
                        .isEqualTo(String.format("%s - %s",
                                theLastDayOfCurrentYearMinusTenDays.plusMonths(index).format(DateTimeUtils.MM_DD_YYYY),
                                theLastDayOfCurrentYearMinusTenDays.plusMonths(index + 1).minusDays(1).format(DateTimeUtils.MM_DD_YYYY)));
            });

            // The last row
            softly.assertThat(tableScheduledPaymentsOfSeriesContinuousValue.get(12).getValue(GROSS_AMOUNT.getName())).isEqualTo(new Currency(400).toString());
            softly.assertThat(tableScheduledPaymentsOfSeriesContinuousValue.get(12).getValue(PAYMENT_AMOUNT.getName()))
                    .isEqualTo(new Currency(400).add(new Currency(600).multiply(20).divide(30).multiply(0.03 * (1 + 0.03) + 0.03)).toString());
            softly.assertThat(tableScheduledPaymentsOfSeriesContinuousValue.get(12).getValue(PAYMENT_FROM_THROUGH_DATE.getName()))
                    .isEqualTo(String.format("%s - %s",
                            theLastDayOfCurrentYearMinusTenDays.plusMonths(12).format(DateTimeUtils.MM_DD_YYYY), expirationDate));
        });
    }
}