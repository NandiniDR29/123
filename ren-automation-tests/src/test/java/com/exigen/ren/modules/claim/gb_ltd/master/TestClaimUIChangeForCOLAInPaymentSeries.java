package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData.INDEMNITY_RESERVE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EXPIRATION_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.COST_OF_LIVING_ADJUSTMENT_BENEFIT;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentSeries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimUIChangeForCOLAInPaymentSeries extends ClaimGroupBenefitsLTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-18501", component = CLAIMS_GROUPBENEFITS)
    public void testClaimUIChangeForCOLAInPaymentSeries() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        TestData defaultTestData = getDefaultLTDMasterPolicyData();
        List<TestData> planDefinitionTestData = defaultTestData.getTestDataList(planDefinitionTab.getMetaKey());
        planDefinitionTestData.get(1).adjust(TestData.makeKeyPath(OPTIONS.getLabel(), COST_OF_LIVING_ADJUSTMENT_BENEFIT.getLabel()), "3%");


        longTermDisabilityMasterPolicy.createPolicy(defaultTestData.adjust(planDefinitionTab.getMetaKey(), planDefinitionTestData));
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(),
                        COVERED_EARNINGS.getLabel()), "1000"));
        claim.claimOpen().perform();

        String allocationAmount = getAllocationAmount();

        claim.updateBenefit().perform(tdClaim.getTestData("TestClaimUpdateAndInquiryBenefit", "TestData_UpdateCoverageEvaluation"), 1);

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD")
                .adjust(TestData.makeKeyPath(CoveragesActionTab.class.getSimpleName(), INDEMNITY_RESERVE.getLabel()),
                        "2000"), 1);

        claim.updateMaximumBenefitPeriodAction()
                .perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_UpdateMaximumBenefitPeriod"), 1);

        TestData addPaymentTestData = tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment");
        LocalDate paymentFromDate = LocalDate.parse(addPaymentTestData.getValue(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                PAYMENT_FROM_DATE.getLabel()), DateTimeUtils.MM_DD_YYYY).with(TemporalAdjusters.firstDayOfNextMonth());

        claim.addPayment().perform(addPaymentTestData
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_FROM_DATE.getLabel()), paymentFromDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_THROUGH_DATE.getLabel()), paymentFromDate.plusMonths(4).minusDays(1).format(DateTimeUtils.MM_DD_YYYY)));

        claim.addPayment().perform(addPaymentTestData
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_FROM_DATE.getLabel()), paymentFromDate.plusMonths(4).format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_THROUGH_DATE.getLabel()), paymentFromDate.plusMonths(11).minusDays(1).format(DateTimeUtils.MM_DD_YYYY)));

        claim.addPayment().perform(addPaymentTestData
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_FROM_DATE.getLabel()), paymentFromDate.plusMonths(11).format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_THROUGH_DATE.getLabel()), paymentFromDate.plusYears(1).minusDays(1).format(DateTimeUtils.MM_DD_YYYY)));

        LOGGER.info("TEST: Step #1-4");
        claim.createPaymentSeries().start();
        claim.createPaymentSeries().getWorkspace().fill(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries")
                .adjust(TestData.makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(),
                        EFFECTIVE_DATE.getLabel()), paymentFromDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(),
                        EXPIRATION_DATE.getLabel()), paymentFromDate.plusYears(2).minusDays(1).format(DateTimeUtils.MM_DD_YYYY)));

        PaymentSeriesCalculatorActionTab paymentSeriesCalculatorActionTab =
                claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesCalculatorActionTab.class);

        assertSoftly(softly -> {
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(COLA_AMOUNT))
                    .hasValue(new Currency(3).multiply(new Currency(allocationAmount)).divide(100).toString());
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE))
                    .hasValue(paymentFromDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE))
                    .hasValue(paymentFromDate.plusYears(2).minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE)).hasValue("COLA");
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(COLA_PERCENT))
                    .hasValue(new Currency(3).toPlainString());
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(NUMBER_OF_ADJUSTMENTS)).hasValue("5 Years");
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(BUTTON_REMOVE)).isAbsent();
        });

        LOGGER.info("TEST: Step #5");
        claim.createPaymentSeries().submit();
        assertThat(tableSummaryOfClaimPaymentSeries).hasRows(1);
    }

    private String getAllocationAmount() {
        claim.claimInquiry().start();
        policyInformationParticipantParticipantCoverageTab.navigateToTab();
        return new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(BENEFIT_AMOUNT.getLabel())).getValue();
    }
}
