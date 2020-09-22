package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.claim.common.tabs.CoveragesActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentCalculatorTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantInformationTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.modules.claim.common.metadata.BenefitReservesActionTabMetaData.INDEMNITY_RESERVE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.COST_OF_LIVING_ADJUSTMENT_BENEFIT;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static java.time.temporal.ChronoUnit.DAYS;

public class TestClaimNotSpanCOLAIncreaseInPayment extends ClaimGroupBenefitsLTDBaseTest {

    private static final String COVERED_EARNINGS_VALUE = "1000";
    private static final String INDEMNITY_RESERVE_VALUE = "2000";

    private PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
    private PaymentPaymentPaymentAllocationTab paymentPaymentPaymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-18510", "REN-18512"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimNotSpanCOLAIncreaseInPayment() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        TestData defaultTestData = getDefaultLTDMasterPolicyData();
        List<TestData> planDefinitionTestData = defaultTestData.getTestDataList(planDefinitionTab.getMetaKey());
        planDefinitionTestData.get(1).adjust(TestData.makeKeyPath(OPTIONS.getLabel(), COST_OF_LIVING_ADJUSTMENT_BENEFIT.getLabel()), "3%");

        longTermDisabilityMasterPolicy.createPolicy(defaultTestData.adjust(planDefinitionTab.getMetaKey(), planDefinitionTestData));
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(),
                        COVERED_EARNINGS.getLabel()), COVERED_EARNINGS_VALUE));
        claim.claimOpen().perform();

        String allocationAmount = getAllocationAmount();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD")
                .adjust(TestData.makeKeyPath(CoveragesActionTab.class.getSimpleName(), INDEMNITY_RESERVE.getLabel()),
                        INDEMNITY_RESERVE_VALUE), 1);

        claim.updateMaximumBenefitPeriodAction()
                .perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_UpdateMaximumBenefitPeriod"), 1);

        TestData addPaymentTestData = tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment");
        LocalDate paymentFromDate = LocalDate.parse(addPaymentTestData.getValue(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                PAYMENT_FROM_DATE.getLabel()), DateTimeUtils.MM_DD_YYYY).with(TemporalAdjusters.firstDayOfNextMonth());

        LOGGER.info("TEST: Payment 1");
        postPayment(paymentFromDate, paymentFromDate.plusMonths(8).minusDays(1), addPaymentTestData);

        LOGGER.info("TEST: Payment 2");
        postPayment(paymentFromDate.plusMonths(8), paymentFromDate.plusMonths(9).minusDays(1), addPaymentTestData);

        LOGGER.info("TEST: Payment 3");
        postPayment(paymentFromDate.plusMonths(9), paymentFromDate.plusMonths(10).minusDays(1), addPaymentTestData);

        LOGGER.info("TEST: Payment 4");
        postPayment(paymentFromDate.plusMonths(10), paymentFromDate.plusMonths(11).minusDays(1), addPaymentTestData);

        LOGGER.info("TEST: Payment 5");
        postPayment(paymentFromDate.plusMonths(11), paymentFromDate.plusMonths(11).plusDays(14), addPaymentTestData);

        LOGGER.info("TEST: Step #1");
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fill(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_FROM_DATE.getLabel()), paymentFromDate.plusMonths(11).plusDays(15).format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_THROUGH_DATE.getLabel()), paymentFromDate.plusMonths(11).plusDays(19).format(DateTimeUtils.MM_DD_YYYY)));

        assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE)).isAbsent();

        LOGGER.info("TEST: Step #5");
        paymentPaymentPaymentAllocationTab.navigateToTab();
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE)
                .setValue(paymentFromDate.plusMonths(11).plusDays(15).format(DateTimeUtils.MM_DD_YYYY));
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE)
                .setValue(paymentFromDate.plusYears(1).minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        claim.addPayment().submit();

        LOGGER.info("TEST: Step #7");
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fill(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_FROM_DATE.getLabel()), paymentFromDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_THROUGH_DATE.getLabel()), paymentFromDate.plusYears(1).plusMonths(1).minusDays(1).format(DateTimeUtils.MM_DD_YYYY)));

        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(COLA_PERCENT)).isDisabled().hasValue(new Currency(3).toPlainString());
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(COLA_AMOUNT)).isDisabled()
                    .hasValue(calculateCOLAAmount(paymentFromDate.plusYears(1),
                            paymentFromDate.plusYears(1).plusMonths(1).minusDays(1),
                            new Currency(allocationAmount),
                            new Currency(3)));
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE)).isDisabled()
                    .hasValue(paymentFromDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE)).isDisabled()
                    .hasValue(paymentFromDate.plusYears(1).plusMonths(1).minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        });

        LOGGER.info("TEST: Step #10");
        paymentPaymentPaymentAllocationTab.navigateToTab();
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE)
                .setValue(paymentFromDate.plusMonths(11).plusDays(15).format(DateTimeUtils.MM_DD_YYYY));
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE)
                .setValue(paymentFromDate.plusYears(1).plusDays(3).format(DateTimeUtils.MM_DD_YYYY));

        paymentCalculatorTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(COLA_PERCENT)).isDisabled().hasValue(new Currency(3).toPlainString());
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(COLA_AMOUNT)).isDisabled()
                    .hasValue(calculateCOLAAmount(paymentFromDate.plusYears(1),
                            paymentFromDate.plusYears(1).plusDays(3),
                            new Currency(allocationAmount),
                            new Currency(3)));
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE)).isDisabled()
                    .hasValue(paymentFromDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE)).isDisabled()
                    .hasValue(paymentFromDate.plusYears(1).plusDays(3).format(DateTimeUtils.MM_DD_YYYY));
        });

        LOGGER.info("TEST: Step #11");
        paymentPaymentPaymentAllocationTab.navigateToTab();
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE)
                .setValue(paymentFromDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY));
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE)
                .setValue(paymentFromDate.plusYears(1).plusYears(1).minusDays(1).format(DateTimeUtils.MM_DD_YYYY));

        paymentCalculatorTab.navigateToTab();
        assertThat(paymentCalculatorTab.getAssetList().getAsset(COLA_AMOUNT))
                .hasValue(calculateCOLAAmount(paymentFromDate.plusYears(1),
                        paymentFromDate.plusYears(1).plusYears(1).minusDays(1),
                        new Currency(allocationAmount),
                        new Currency(3)));
    }

    private void postPayment(LocalDate paymentFromDate, LocalDate paymentThroughDate, TestData addPaymentTestData) {
        claim.addPayment().perform(addPaymentTestData
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_FROM_DATE.getLabel()), paymentFromDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_THROUGH_DATE.getLabel()), paymentThroughDate.format(DateTimeUtils.MM_DD_YYYY)));
    }

    private String getAllocationAmount() {
        claim.claimInquiry().start();
        policyInformationParticipantParticipantCoverageTab.navigateToTab();
        return new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(BENEFIT_AMOUNT.getLabel())).getValue();
    }

    /*
     * Method return calculation COLA Amount according to the formula
     * COLA Amount = (# of days /total days) * (Allocation Amount) * (COLA%) / 100
     */
    private String calculateCOLAAmount(LocalDate beginningDate, LocalDate throughDate, Currency allocationAmount, Currency colaPercent) {
        return new Currency(getDaysBetweenDates(beginningDate, throughDate)).multiply(allocationAmount).multiply(colaPercent).divide(100).divide(30).toString();
    }

    /*
     * Method return count of days between two dates
     * If the throughDate falls within the first month, (e.g beginningDate = 11/05/2019, throughDate = 12/03/2019 < beginningDate + 1 month)
     * method return count of days between two dates(e.g 29 days).
     * If the throughDate doesn't falls within the first month, (e.g beginningDate = 11/05/2019, throughDate = 12/10/2019 > beginningDate + 1 month)
     * method return 30(full month(11/05/2019 and 12/04/2019)) + days between two dates(6 days(12/05/2019 and  12/10/2019))
     */
    private static long getDaysBetweenDates(LocalDate beginningDate, LocalDate throughDate) {
        long daysBetweenDates;
        if (beginningDate.plusMonths(1).minusDays(1).isAfter(throughDate)) {
            return DAYS.between(beginningDate.minusDays(1), throughDate);
        }
        daysBetweenDates = 30 + getDaysBetweenDates(beginningDate.plusMonths(1), throughDate);

        return daysBetweenDates;
    }
}
