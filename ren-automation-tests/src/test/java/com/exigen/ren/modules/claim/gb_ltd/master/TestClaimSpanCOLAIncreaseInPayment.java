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
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.modules.claim.common.metadata.BenefitReservesActionTabMetaData.INDEMNITY_RESERVE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.COLA_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.claim.common.tabs.PaymentCalculatorTab.tableListOfPaymentAddition;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.COST_OF_LIVING_ADJUSTMENT_BENEFIT;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static java.time.temporal.ChronoUnit.DAYS;

public class TestClaimSpanCOLAIncreaseInPayment extends ClaimGroupBenefitsLTDBaseTest {

    private static final String COVERED_EARNINGS_VALUE = "1000";
    private static final String INDEMNITY_RESERVE_VALUE = "2000";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-18511", "REN-18514"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimSpanCOLAIncreaseInPayment() {
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

        claim.addPayment().perform(addPaymentTestData
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_FROM_DATE.getLabel()), paymentFromDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_THROUGH_DATE.getLabel()), paymentFromDate.plusYears(1).minusDays(1).format(DateTimeUtils.MM_DD_YYYY)));

        PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
        PaymentPaymentPaymentAllocationTab paymentPaymentPaymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);

        LOGGER.info("TEST: Step #1");
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fill(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_FROM_DATE.getLabel()), paymentFromDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_THROUGH_DATE.getLabel()), paymentFromDate.plusYears(1).plusMonths(1).plusDays(5).format(DateTimeUtils.MM_DD_YYYY)));

        assertThat(calculateCOLAAmount(paymentFromDate.plusYears(1),
                paymentFromDate.plusYears(1).plusMonths(1).plusDays(5),
                new Currency(allocationAmount),
                new Currency(3)))
                .isEqualTo(paymentCalculatorTab.getAssetList().getAsset(COLA_AMOUNT).getValue());

        LOGGER.info("TEST: Step #3");
        paymentPaymentPaymentAllocationTab.navigateToTab();
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE)
                .setValue(paymentFromDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY));
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE)
                .setValue(paymentFromDate.plusYears(1).plusDays(10).format(DateTimeUtils.MM_DD_YYYY));

        paymentCalculatorTab.navigateToTab();
        assertThat(calculateCOLAAmount(paymentFromDate.plusYears(1),
                paymentFromDate.plusYears(1).plusDays(10),
                new Currency(allocationAmount),
                new Currency(3)))
                .isEqualTo(paymentCalculatorTab.getAssetList().getAsset(COLA_AMOUNT).getValue());

        LOGGER.info("TEST: Step #4");
        paymentPaymentPaymentAllocationTab.navigateToTab();
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE)
                .setValue(paymentFromDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY));
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE)
                .setValue(paymentFromDate.plusYears(1).plusMonths(4).plusDays(10).format(DateTimeUtils.MM_DD_YYYY));

        paymentCalculatorTab.navigateToTab();
        assertThat(calculateCOLAAmount(paymentFromDate.plusYears(1),
                paymentFromDate.plusYears(1).plusMonths(4).plusDays(10),
                new Currency(allocationAmount),
                new Currency(3)))
                .isEqualTo(paymentCalculatorTab.getAssetList().getAsset(COLA_AMOUNT).getValue());

        LOGGER.info("TEST: Step #6");
        paymentPaymentPaymentAllocationTab.navigateToTab();
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE)
                .setValue(paymentFromDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY));
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE)
                .setValue(paymentFromDate.plusYears(2).plusDays(10).format(DateTimeUtils.MM_DD_YYYY));

        paymentCalculatorTab.navigateToTab();
        LOGGER.info("TEST: Step #6, COLA section first");
        Currency colaAmountFirstByFormula = new Currency(calculateCOLAAmount(paymentFromDate.plusYears(1),
                paymentFromDate.plusYears(2).minusDays(1),
                new Currency(allocationAmount),
                new Currency(3)));
        Currency colaAmountFirstActual = new Currency(paymentCalculatorTab.getAssetList().getAsset(COLA_AMOUNT).getValue());

        assertThat(colaAmountFirstByFormula.lessThan(colaAmountFirstActual))
                .withFailMessage("Incorrect COLA Amount").isTrue();

        LOGGER.info("TEST: Step #6, COLA section second");
        tableListOfPaymentAddition.getRow(2).getCell(4).controls.links.getFirst().click();
        Currency colaAmountSecondByFormula = new Currency(calculateCOLAAmount(paymentFromDate.plusYears(2),
                paymentFromDate.plusYears(2).plusDays(10),
                new Currency(allocationAmount),
                new Currency(3)));
        Currency colaAmountSecondMonthActual = new Currency(paymentCalculatorTab.getAssetList().getAsset(COLA_AMOUNT).getValue());

        assertThat(colaAmountSecondByFormula.lessThan(colaAmountSecondMonthActual))
                .withFailMessage("Incorrect COLA Amount").isTrue();
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
    private long getDaysBetweenDates(LocalDate beginningDate, LocalDate throughDate) {
        long daysBetweenDates;
        if (beginningDate.plusMonths(1).minusDays(1).isAfter(throughDate)) {
            return DAYS.between(beginningDate.minusDays(1), throughDate);
        }
        daysBetweenDates = 30 + getDaysBetweenDates(beginningDate.plusMonths(1), throughDate);

        return daysBetweenDates;
    }

    private String getAllocationAmount() {
        claim.claimInquiry().start();
        policyInformationParticipantParticipantCoverageTab.navigateToTab();
        return new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(BENEFIT_AMOUNT.getLabel())).getValue();
    }
}
