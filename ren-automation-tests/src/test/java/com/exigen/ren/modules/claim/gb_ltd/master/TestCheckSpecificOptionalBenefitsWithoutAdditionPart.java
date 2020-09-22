package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentReductionMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.PAYMENTS;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.MM_DD_YYYY_H_MM_A;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentSeriesTable.SERIES_NUMBER;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_SCHEDULE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.DeductionsActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EXPIRATION_DATE;
import static com.exigen.ren.main.modules.claim.common.tabs.BalanceActionTab.ClaimUnprocessedBalanceTableExtended.BALANCE_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.tabs.BalanceActionTab.ClaimUnprocessedBalanceTableExtended.PAYMENT_NUMBER;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageDeterminationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.EmployerBenefitsMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.SPONSOR_PAYMENT_MODE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckSpecificOptionalBenefitsWithoutAdditionPart extends ClaimGroupBenefitsLTDBaseTest {
    private PaymentPaymentPaymentAllocationTab paymentPaymentPaymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);
    private PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
    private DeductionsActionTab deductionsActionTab = claim.calculateSingleBenefitAmount().getWorkspace().getTab(DeductionsActionTab.class);
    private Currency amountOneHundred = new Currency(100);
    private Currency amountSixHundreds = new Currency(600);

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-38164", component = CLAIMS_GROUPBENEFITS)
    public void testCheckSpecificOptionalBenefits401KContribution() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()))
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Voluntary")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FOUR_HUNDRED_ONE_K_CONTRIBUTION_DURING_DISABILITY.getLabel()), "10%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FOUR_HUNDRED_ONE_K_CONTRIBUTION_MONTHLY_MAXIMUM_AMOUNT.getLabel()), "1000"));

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000")
                .adjust(TestData.makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), DateTimeUtils.getCurrentDateTime().plusYears(12).format(MM_DD_YYYY)));
        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().start(1);
        claim.calculateSingleBenefitAmount().getWorkspace().fillUpTo(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), OtherIncomeBenefitActionTab.class);
        OtherIncomeBenefitActionTab.buttonSaveAndExit.click();
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        TestData tdCalculateBenefit = tdClaim.getTestData("CalculateASingleBenefitAmount", "TestDataWithDeductions")
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), TYPE_OF_DEDUCTION.getLabel()), "contains=401K")
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), REQUIRED_MONTHLY_401K_CONTRIBUTION_AMOUNT.getLabel()), "1000");
        fillDeductionTab(bpStartDate, tdCalculateBenefit);
        createPaymentFromPrecondition(bpStartDate);

        LOGGER.info("TEST REN-38164: Step 1");
        claim.recalculateSingleBenefitAmount().start(1);
        deductionsActionTab.navigate();
        deductionsActionTab.getAssetList().getAsset(REQUIRED_MONTHLY_401K_CONTRIBUTION_AMOUNT).setValue("50");
        DeductionsActionTab.buttonSaveAndExit.click();

        checkPaymentAmount(2, amountSixHundreds, amountOneHundred);
        checkPaymentAmount(4, amountSixHundreds, new Currency(50));

        LOGGER.info("TEST REN-38164: Step 2");
        claim.recalculateSingleBenefitAmount().start(1);
        deductionsActionTab.navigate();
        deductionsActionTab.getAssetList().getAsset(THROUGH_DATE).setValue(bpStartDate.plusMonths(1).format(MM_DD_YYYY));
        DeductionsActionTab.buttonSaveAndExit.click();

        checkPaymentAmount(2, amountSixHundreds, amountOneHundred);
        checkPaymentAmount(4, amountSixHundreds, new Currency(1.67));

        LOGGER.info("TEST REN-38164: Step 3");
        removeDeduction();
        checkPaymentAmount(2, amountSixHundreds, amountOneHundred);
        claim.paymentInquiry().start(4);
        paymentPaymentPaymentAllocationTab.navigateToTab();
        assertThat(new StaticElement(ALLOCATION_AMOUNT.getLocator())).hasValue(amountSixHundreds.toString());
        paymentCalculatorTab.navigateToTab();
        assertThat(new StaticElement(PaymentReductionMetaData.DEDUCTION_AMOUNT.getLocator())).isAbsent();
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();

        LOGGER.info("TEST REN-38164: Step 4");
        tableSummaryOfClaimPaymentSeries.getRow(1).getCell(SERIES_NUMBER).controls.links.getFirst().click();
        String firstPaymentScheduleDate = tableScheduledPaymentsOfSeries.getRow(1).getCell(PAYMENT_SCHEDULE_DATE.getName()).getValue();
        LocalDateTime nextPhaseDate = LocalDateTime.parse(firstPaymentScheduleDate, MM_DD_YYYY_H_MM_A).plusMinutes(1);

        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(nextPhaseDate, true);
        mainApp().reopen();
        search(claimNumber);

        LOGGER.info("TEST REN-38164: Step 5");
        toSubTab(PAYMENTS.get());
        claim.paymentInquiry().start(5);
        paymentCalculatorTab.navigateToTab();
        assertThat(new StaticElement(PaymentReductionMetaData.DEDUCTION_AMOUNT.getLocator())).isAbsent();
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();

        LOGGER.info("TEST REN-38164: Step 6");
        fillDeductionTab(bpStartDate, tdCalculateBenefit);

        checkPaymentAmount(2, amountSixHundreds, amountOneHundred);
        checkPaymentAmount(4, amountSixHundreds, amountOneHundred);
        checkPaymentAmount(5, amountSixHundreds, amountOneHundred);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-38165", component = CLAIMS_GROUPBENEFITS)
    public void testCheckSpecificOptionalBenefitRevenueProtection() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "AL")
                .mask(makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel()))
                .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()))
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Voluntary")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", EMPLOYER_BENEFITS.getLabel(), NONE.getLabel()), "false")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", EMPLOYER_BENEFITS.getLabel(), REVENUE_PROTECTION_BENEFIT_PERCENT.getLabel()), "10%"));

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000")
                .adjust(TestData.makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), DateTimeUtils.getCurrentDateTime().plusYears(12).format(MM_DD_YYYY)));
        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().start(1);
        claim.calculateSingleBenefitAmount().getWorkspace().fillUpTo(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), OtherIncomeBenefitActionTab.class);
        OtherIncomeBenefitActionTab.buttonSaveAndExit.click();
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        TestData tdCalculateBenefit = tdClaim.getTestData("CalculateASingleBenefitAmount", "TestDataWithDeductions")
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), TYPE_OF_DEDUCTION.getLabel()), "contains=Revenue");
        fillDeductionTab(bpStartDate, tdCalculateBenefit);
        createPaymentFromPrecondition(bpStartDate);

        LOGGER.info("TEST REN-38165: Step 1");
        claim.recalculateSingleBenefitAmount().start(1);
        deductionsActionTab.navigate();
        deductionsActionTab.getAssetList().getAsset(THROUGH_DATE).setValue(bpStartDate.plusMonths(1).format(MM_DD_YYYY));
        DeductionsActionTab.buttonSaveAndExit.click();

        checkPaymentAmount(2, amountSixHundreds, amountOneHundred);
        checkPaymentAmount(4, amountSixHundreds, new Currency(3.33));

        LOGGER.info("TEST REN-38165: Step 2");
        toSubTab(OVERVIEW);
        disabilityClaim.claimUpdate().perform(tdClaim.getTestData("TestClaimUpdate", "TestData_Update")
                .adjust(makeKeyPath(PolicyInformationParticipantParticipantInformationActionTab.class.getSimpleName(), COVERED_EARNINGS.getLabel()), "2000"));

        claim.recalculateSingleBenefitAmount().start(1);
        deductionsActionTab.navigate();
        DeductionsActionTab.buttonSaveAndExit.click();

        checkPaymentAmount(2, amountSixHundreds, amountOneHundred);
        checkPaymentAmount(4, new Currency(1200), new Currency(6.67));
        checkBalanceAmount(2, amountSixHundreds.negate());

        LOGGER.info("TEST REN-38165: Step 3");
        removeDeduction();
        checkPaymentAmount(2, amountSixHundreds, amountOneHundred);
        checkBalanceAmount(2, amountSixHundreds.negate());

        claim.paymentInquiry().start(4);
        paymentPaymentPaymentAllocationTab.navigateToTab();
        assertThat(new StaticElement(ALLOCATION_AMOUNT.getLocator())).hasValue(new Currency(1200).toString());
        paymentCalculatorTab.navigateToTab();
        assertThat(new StaticElement(PaymentReductionMetaData.DEDUCTION_AMOUNT.getLocator())).isAbsent();
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();

        LOGGER.info("TEST REN-38165: Step 4");
        tableSummaryOfClaimPaymentSeries.getRow(1).getCell(SERIES_NUMBER).controls.links.getFirst().click();
        String firstPaymentScheduleDate = tableScheduledPaymentsOfSeries.getRow(1).getCell(PAYMENT_SCHEDULE_DATE.getName()).getValue();
        LocalDateTime nextPhaseDate = LocalDateTime.parse(firstPaymentScheduleDate, MM_DD_YYYY_H_MM_A).plusMinutes(1);

        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(nextPhaseDate, true);
        mainApp().reopen();
        search(claimNumber);

        LOGGER.info("TEST REN-38165: Step 5");
        toSubTab(PAYMENTS.get());
        claim.paymentInquiry().start(5);
        paymentCalculatorTab.navigateToTab();
        assertThat(new StaticElement(PaymentReductionMetaData.DEDUCTION_AMOUNT.getLocator())).isAbsent();
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();

        LOGGER.info("TEST REN-38165: Step 6");
        fillDeductionTab(bpStartDate, tdCalculateBenefit);

        checkPaymentAmount(2, amountSixHundreds, amountOneHundred);
        checkPaymentAmount(4, new Currency(1200), new Currency(200));
        checkPaymentAmount(5, new Currency(1200), new Currency(200));
        checkBalanceAmount(2, amountSixHundreds.negate());
    }

    private void createPaymentFromPrecondition(LocalDateTime bpStartDate) {
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY)));
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 2);

        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.plusMonths(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(2).minusDays(1).format(MM_DD_YYYY)));

        claim.createPaymentSeries().perform(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries")
                .adjust(TestData.makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EFFECTIVE_DATE.getLabel()), bpStartDate.plusMonths(2).format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EXPIRATION_DATE.getLabel()), bpStartDate.plusMonths(3).minusDays(1).format(MM_DD_YYYY)));
    }

    private void fillDeductionTab(LocalDateTime bpStartDate, TestData testData) {
        claim.recalculateSingleBenefitAmount().start(1);
        deductionsActionTab.navigate();
        deductionsActionTab.fillTab(testData
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), THROUGH_DATE.getLabel()), bpStartDate.plusYears(10).format(MM_DD_YYYY))
                .mask(makeKeyPath(deductionsActionTab.getMetaKey(), DEDUCTION_AMOUNT.getLabel())));
        DeductionsActionTab.buttonSaveAndExit.click();
    }

    private void removeDeduction() {
        claim.recalculateSingleBenefitAmount().start(1);
        deductionsActionTab.navigate();
        deductionsActionTab.removeDeduction();
        DeductionsActionTab.buttonSaveAndExit.click();

        checkPaymentAmount(2, amountSixHundreds, new Currency(100));

        claim.paymentInquiry().start(4);
        paymentCalculatorTab.navigateToTab();
        assertThat(new StaticElement(PaymentReductionMetaData.DEDUCTION_AMOUNT.getLocator())).isAbsent();
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
    }

    private void checkPaymentAmount(int rowPayment, Currency allocationAmount, Currency deductionAmount) {
        claim.paymentInquiry().start(rowPayment);
        paymentPaymentPaymentAllocationTab.navigateToTab();
        assertThat(new StaticElement(ALLOCATION_AMOUNT.getLocator())).hasValue(allocationAmount.toString());

        paymentCalculatorTab.navigateToTab();
        assertThat(new StaticElement(PaymentReductionMetaData.DEDUCTION_AMOUNT.getLocator())).hasValue(deductionAmount.toString());
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
    }

    private void checkBalanceAmount(int rowPayment, Currency balanceAmount) {
        String paymentNumber1 = ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(rowPayment).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).getValue();
        claim.checkBalance().start();
        assertSoftly(softly -> {
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries.getRow(PAYMENT_NUMBER.getName(), paymentNumber1))
                    .hasCellWithValue(BALANCE_AMOUNT.getName(), balanceAmount.toString());
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
        });
        BalanceActionTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
    }
}
