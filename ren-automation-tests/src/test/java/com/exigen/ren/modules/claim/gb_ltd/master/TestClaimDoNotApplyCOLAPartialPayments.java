package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.claim.common.metadata.BenefitReservesActionTabMetaData.INDEMNITY_RESERVE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PAYMENT_ADDITION;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.ADDITION_TYPE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.COST_OF_LIVING_ADJUSTMENT_BENEFIT;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimDoNotApplyCOLAPartialPayments extends ClaimGroupBenefitsLTDBaseTest {

    private static final String COLA_PERCENT_VALUE = "3%";
    private static final String ALLOCATION_AMOUNT_VALUE = "600";
    private static final String COVERED_EARNINGS_VALUE = "1000";
    private static final String BENEFIT_PERCENTAGE_VALUE = "60";
    private static final String INDEMNITY_RESERVE_VALUE = "2000";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-18517", "REN-18518"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimDoNotApplyCOLAPartialPayments() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        TestData defaultTestData = getDefaultLTDMasterPolicyData();
        List<TestData> planDefinitionTestData = defaultTestData.getTestDataList(planDefinitionTab.getMetaKey());
        planDefinitionTestData.get(1).adjust(TestData.makeKeyPath(OPTIONS.getLabel(), COST_OF_LIVING_ADJUSTMENT_BENEFIT.getLabel()), COLA_PERCENT_VALUE);

        longTermDisabilityMasterPolicy.createPolicy(defaultTestData.adjust(planDefinitionTab.getMetaKey(), planDefinitionTestData));

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(),
                        COVERED_EARNINGS.getLabel()), COVERED_EARNINGS_VALUE)
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantCoverageTab.class.getSimpleName(),
                        BENEFIT_PERCENTAGE.getLabel()), BENEFIT_PERCENTAGE_VALUE));
        claim.claimOpen().perform();

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

        LOGGER.info("TEST: Step #2-3");
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_FROM_DATE.getLabel()), paymentFromDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_THROUGH_DATE.getLabel()),
                        paymentFromDate.plusYears(1).plusMonths(1).minusDays(1).format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        IN_LIEU_BENEFIT.getLabel()), "Partial Disability Benefit")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        CURRENT_EARNINGS.getLabel()), "200")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        IN_APPROVED_REHABILITATION_PROGRAM.getLabel()), "Yes"), PaymentPaymentPaymentAllocationTab.class, true);

        PaymentPaymentPaymentAllocationTab paymentPaymentPaymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);
        PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);

        assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(new Currency(ALLOCATION_AMOUNT_VALUE).toString());

        paymentCalculatorTab.navigateToTab();
        assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE)).isPresent();

        LOGGER.info("TEST: Step #4");
        paymentPaymentPaymentAllocationTab.navigateToTab();
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(CURRENT_EARNINGS).setValue("180");

        paymentCalculatorTab.navigateToTab();
        assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE)).isPresent();

        LOGGER.info("TEST: Step #5");
        paymentPaymentPaymentAllocationTab.navigateToTab();
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(CURRENT_EARNINGS).setValue("799");

        paymentCalculatorTab.navigateToTab();
        assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE)).isAbsent();
    }
}