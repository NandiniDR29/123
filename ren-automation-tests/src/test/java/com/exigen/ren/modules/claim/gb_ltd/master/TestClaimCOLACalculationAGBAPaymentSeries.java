package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.DataProviderFactory;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.PAYMENTS;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentSeriesTableExtended.SERIES_NUMBER;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.OFFSET_AMOUNT;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_FROM_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData.INDEMNITY_RESERVE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.BEGINNING_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData.COLA_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EXPIRATION_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.MAX_MONTHLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.MINIMUM_MONTHLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.COST_OF_LIVING_ADJUSTMENT_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.NUMBER_OF_ADJUSTMENTS;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimCOLACalculationAGBAPaymentSeries extends ClaimGroupBenefitsLTDBaseTest {

    private static final String COLA_PERCENT_VALUE = "3%";
    private static final String MAX_MONTHLY_BENEFIT_AMOUNT_VALUE = "1000";
    private static final String COVERED_EARNINGS_VALUE = "600";
    private static final String INDEMNITY_RESERVE_VALUE = "2000";

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = {"REN-18503", "REN-18515"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimCOLACalculationAGBAPaymentSeries() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), COST_OF_LIVING_ADJUSTMENT_BENEFIT.getLabel()), COLA_PERCENT_VALUE)
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), NUMBER_OF_ADJUSTMENTS.getLabel()), "5 Years")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel()), DataProviderFactory.dataOf(
                        MINIMUM_MONTHLY_BENEFIT_AMOUNT.getLabel(), "$100",
                        MAX_MONTHLY_BENEFIT_AMOUNT.getLabel(), MAX_MONTHLY_BENEFIT_AMOUNT_VALUE)));
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(),
                        COVERED_EARNINGS.getLabel()), COVERED_EARNINGS_VALUE));
        claim.claimOpen().perform();
        String claimNumber = getClaimNumber();
        claim.updateBenefit().perform(tdClaim.getTestData("TestClaimUpdateAndInquiryBenefit", "TestData_UpdateCoverageEvaluation"), 1);

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD")
                .adjust(TestData.makeKeyPath(CoveragesActionTab.class.getSimpleName(), INDEMNITY_RESERVE.getLabel()),
                        INDEMNITY_RESERVE_VALUE)
                .adjust(OtherIncomeBenefitActionTab.class.getSimpleName(),
                        tdSpecific().getTestDataList(DEFAULT_TEST_DATA_KEY, OtherIncomeBenefitActionTab.class.getSimpleName())), 1);

        claim.updateMaximumBenefitPeriodAction()
                .perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_UpdateMaximumBenefitPeriod"), 1);

        TestData addPaymentTestData = tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment");
        LocalDate paymentFromDate = LocalDate.parse(addPaymentTestData.getValue(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                PAYMENT_FROM_DATE.getLabel()), DateTimeUtils.MM_DD_YYYY);

        claim.addPayment().perform(addPaymentTestData
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_FROM_DATE.getLabel()), paymentFromDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_THROUGH_DATE.getLabel()), paymentFromDate.plusYears(1).minusDays(1).format(DateTimeUtils.MM_DD_YYYY)));

        LOGGER.info("TEST: Step #1 REN-18503, REN-18515");
        claim.createPaymentSeries().start();
        claim.createPaymentSeries().getWorkspace().fill(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries")
                .adjust(TestData.makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(),
                        EFFECTIVE_DATE.getLabel()), paymentFromDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(),
                        EXPIRATION_DATE.getLabel()), paymentFromDate.plusYears(2).plusMonths(3).plusDays(20).format(DateTimeUtils.MM_DD_YYYY)));

        PaymentSeriesCalculatorActionTab paymentSeriesCalculatorActionTab =
                claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesCalculatorActionTab.class);

        assertSoftly(softly -> {
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(COLA_AMOUNT))
                    .hasValue(new Currency(COLA_PERCENT_VALUE.replace("%", StringUtils.EMPTY))
                            .multiply(new Currency(100)).divide(100).toString());
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE))
                    .hasValue(paymentFromDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY));
        });

        claim.createPaymentSeries().submit();

        LOGGER.info("TEST: Step #3, REN-18515");
        tableSummaryOfClaimPaymentSeries.getRow(1).getCell(SERIES_NUMBER.getName()).controls.links.getFirst().click();

        assertSoftly(softly -> {
            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(OFFSET_AMOUNT, new Currency("100").toString())
                    .with(PAYMENT_FROM_THROUGH_DATE, String.format("%s - %s",
                            paymentFromDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY),
                            paymentFromDate.plusYears(1).plusMonths(1).minusDays(1).format(DateTimeUtils.MM_DD_YYYY)))
                    .hasMatchingRows(1);

            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(OFFSET_AMOUNT, new Currency("550").toString())
                    .with(PAYMENT_FROM_THROUGH_DATE, String.format("%s - %s",
                            paymentFromDate.plusYears(1).plusMonths(1).format(DateTimeUtils.MM_DD_YYYY),
                            paymentFromDate.plusYears(1).plusMonths(2).minusDays(1).format(DateTimeUtils.MM_DD_YYYY)))
                    .hasMatchingRows(1);

            softly.assertThat(tableScheduledPaymentsOfSeries)
                    .with(OFFSET_AMOUNT, new Currency("70").toString())
                    .with(PAYMENT_FROM_THROUGH_DATE, String.format("%s - %s",
                            paymentFromDate.plusYears(1).plusMonths(2).format(DateTimeUtils.MM_DD_YYYY),
                            paymentFromDate.plusYears(1).plusMonths(3).minusDays(1).format(DateTimeUtils.MM_DD_YYYY)))
                    .hasMatchingRows(1);
        });

        LOGGER.info("TEST: Step #4, REN-18515");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(paymentFromDate.plusYears(1).plusMonths(2).plusDays(10).atStartOfDay());

        mainApp().open();
        search(claimNumber);
        toSubTab(PAYMENTS.get());
        tableSummaryOfClaimPaymentSeries.getRow(1).getCell(SERIES_NUMBER.getName()).controls.links.getFirst().click();

        assertThat(tablePostedPaymentsOfSeries).hasRows(2);
    }
}
