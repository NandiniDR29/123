package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsLTDIncidentTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsLTDInjuryPartyInformationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.PAYMENTS;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentSeriesTable.SERIES_NUMBER;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_SCHEDULE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData.ASSOCIATED_INSURABLE_RISK;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentAllocationActionTabMetaData.ALLOCATION_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EXPIRATION_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.ANNUAL_BASE_SALARY;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageDeterminationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData.ASSOCIATED_SCHEDULED_ITEM;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.*;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableScheduledPaymentsOfSeries;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentSeries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckPaymentCalculationForCOBRAPremiumReimbursementBenefit extends ClaimGroupBenefitsLTDBaseTest {
    private PaymentPaymentPaymentAllocationTab paymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);
    private PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
    private PaymentSeriesCalculatorActionTab paymentSeriesCalculatorActionTab = claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesCalculatorActionTab.class);
    private PaymentSeriesPaymentSeriesProfileActionTab paymentSeriesProfileActionTab = claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesPaymentSeriesProfileActionTab.class);

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-23479", component = CLAIMS_GROUPBENEFITS)
    public void testCheckCOBRAPremiumReimbursementBenefit() {
        final String COBRA_PREMIUM_REIMBURSEMENT_BENEFIT = "COBRA Premium Reimbursement Benefit";
        preconditionCreatePolicy();

        LOGGER.info("TEST REN-23479: Step 11");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY), BenefitsLTDInjuryPartyInformationTab.class, true);
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM)).doesNotContainOption(COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);

        BenefitsLTDInjuryPartyInformationTab.buttonNext.click();
        disabilityClaim.getDefaultWorkspace()
                .fillFromTo(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_WithOneBenefit")
                                .adjust(makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), DateTimeUtils.getCurrentDateTime().plusYears(10).format(MM_DD_YYYY)),
                        BenefitsLTDIncidentTab.class, CompleteNotificationTab.class, true);
        CompleteNotificationTab.buttonOpenClaim.click();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("TEST REN-23479: Step 14");
        LocalDateTime bpStartDate = getBpStartDate(COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);

        LOGGER.info("TEST REN-23479: Step 15");
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY)), paymentCalculatorTab.getClass());
        paymentCalculatorTab.getAssetList().getAsset(BUTTON_ADD_PAYMENT_ADDITION).click();
        assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE)).containsOption(COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);

        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE).setValue(COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);
        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE)).isRequired();
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE)).isRequired();
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM)).isRequired();
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(MONTHLY_BENEFIT_AMOUNT)).isRequired();
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_COMMENTS)).isOptional();
        });

        LOGGER.info("TEST REN-23479: Step 16");
        Currency net1 = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue());
        Currency amount = new Currency(900);

        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM).setValue(amount.toString());
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE).setValue(bpStartDate.format(MM_DD_YYYY));
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE).setValue(bpStartDate.format(MM_DD_YYYY));
        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(MONTHLY_BENEFIT_AMOUNT)).hasValue(amount.toString());
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT)).hasValue(net1.add(amount.divide(30)).toString());
        });
        PaymentCalculatorTab.buttonCancel.click();
        Page.dialogConfirmation.buttonYes.click();

        LOGGER.info("TEST REN-23479: Step 17");
        claim.createPaymentSeries().start();
        claim.createPaymentSeries().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries"), PaymentSeriesPaymentAllocationActionTab.class, true);

        Currency net1ValuePaymentSeries = new Currency(claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesPaymentAllocationActionTab.class).getAssetList().getAsset(ALLOCATION_AMOUNT).getValue());

        paymentAllocationTab.submitTab();
        claim.createPaymentSeries().getWorkspace().fillFromTo(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries")
                        .adjust(makeKeyPath(paymentSeriesProfileActionTab.getMetaKey(), EFFECTIVE_DATE.getLabel()), bpStartDate.plusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentSeriesProfileActionTab.getMetaKey(), EXPIRATION_DATE.getLabel()), bpStartDate.plusDays(32).format(MM_DD_YYYY)),
                PaymentSeriesAdditionalPayeeTab.class, PaymentSeriesCalculatorActionTab.class, true);

        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.BUTTON_ADD_PAYMENT_ADDITION).click();
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(ADDITION_TYPE).setValue(COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM).setValue(new Currency(102).toString());
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(BEGINNING_DATE).setValue(bpStartDate.plusDays(1).format(MM_DD_YYYY));
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(THROUGH_DATE).setValue(bpStartDate.plusDays(25).format(MM_DD_YYYY));
        claim.createPaymentSeries().submit();

        Currency benefitAmount = new Currency(102);
        tableSummaryOfClaimPaymentSeries.getRow(1).getCell(SERIES_NUMBER).controls.links.getFirst().click();
        String firstPaymentScheduleDate = tableScheduledPaymentsOfSeries.getRow(2).getCell(PAYMENT_SCHEDULE_DATE.getName()).getValue();
        LocalDateTime nextPhaseDate = LocalDateTime.parse(firstPaymentScheduleDate, DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a"));

        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(nextPhaseDate, true);
        mainApp().reopen();
        search(claimNumber);

        claim.updatePayment().start(1);
        paymentCalculatorTab.navigateToTab();
        Currency allocationNetAmount = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue())
                .add(new Currency(paymentCalculatorTab.getAssetList().getAsset(TOTAL_TAX_AMOUNT).getValue()));
        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(MONTHLY_BENEFIT_AMOUNT)).hasValue(benefitAmount.toString());
            softly.assertThat(allocationNetAmount).isEqualTo(net1ValuePaymentSeries.add(benefitAmount.divide(30).multiply(25))); // NET2 = NET1+x/30*26
        });
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        claim.updatePayment().start(2);
        paymentCalculatorTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(MONTHLY_BENEFIT_AMOUNT)).hasValue(benefitAmount.toString());
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT))
                    .hasValue(claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesPaymentAllocationActionTab.class).getAssetList().getAsset(ALLOCATION_AMOUNT).getValue());
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-23480", component = CLAIMS_GROUPBENEFITS)
    public void testCheckStudentLoanRepaymentBenefit() {
        final String STUDENT_LOAN_REPAYMENT_BENEFIT = "Student Loan Repayment Benefit";
        preconditionCreatePolicy();

        LOGGER.info("TEST REN-23480: Step 6");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY), BenefitsLTDInjuryPartyInformationTab.class, true);
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM)).doesNotContainOption(STUDENT_LOAN_REPAYMENT_BENEFIT);

        policyInformationParticipantParticipantInformationTab.navigate();
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(ANNUAL_BASE_SALARY).setValue("24000");
        benefitsLTDIncidentTab.navigate();

        disabilityClaim.getDefaultWorkspace().fillFromTo(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                        .adjust(makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), DateTimeUtils.getCurrentDateTime().plusYears(10).format(MM_DD_YYYY)),
                BenefitsLTDIncidentTab.class, CompleteNotificationTab.class, true);
        CompleteNotificationTab.buttonOpenClaim.click();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("TEST REN-23480: Step 9");
        LocalDateTime bpStartDate = getBpStartDate(STUDENT_LOAN_REPAYMENT_BENEFIT);

        LOGGER.info("TEST REN-23480: Step 10");
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).format(MM_DD_YYYY)), paymentCalculatorTab.getClass());
        paymentCalculatorTab.getAssetList().getAsset(BUTTON_ADD_PAYMENT_ADDITION).click();
        assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE)).containsOption(STUDENT_LOAN_REPAYMENT_BENEFIT);

        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE).setValue(STUDENT_LOAN_REPAYMENT_BENEFIT);
        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE)).isRequired();
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE)).isRequired();
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_STUDENT_LOAN_AMOUNT)).isRequired();
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(MONTHLY_BENEFIT_AMOUNT)).isRequired();
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_COMMENTS)).isOptional();
        });

        LOGGER.info("TEST REN-23480: Step 11");
        Currency net1 = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue());
        Currency amount = new Currency(300);

        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_STUDENT_LOAN_AMOUNT).setValue(new Currency(900).toString());
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE).setValue(bpStartDate.format(MM_DD_YYYY));
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE).setValue(bpStartDate.plusDays(26).format(MM_DD_YYYY));
        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(MONTHLY_BENEFIT_AMOUNT)).hasValue(amount.toString());
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT)).hasValue(net1.add(amount.divide(30).multiply(27)).toString());
        });

        LOGGER.info("TEST REN-23480: Step 12");
        Currency newAmount = new Currency(120);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_STUDENT_LOAN_AMOUNT).setValue(newAmount.toString());
        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(MONTHLY_BENEFIT_AMOUNT)).hasValue(newAmount.toString());
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT)).hasValue(net1.add(newAmount.divide(30).multiply(27)).toString());
        });
        PaymentCalculatorTab.buttonCancel.click();
        Page.dialogConfirmation.buttonYes.click();

        LOGGER.info("TEST REN-23480: Step 13");
        claim.createPaymentSeries().start();
        claim.createPaymentSeries().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries"), PaymentSeriesPaymentAllocationActionTab.class, true);

        Currency net1ValuePaymentSeries = new Currency(claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesPaymentAllocationActionTab.class).getAssetList().getAsset(ALLOCATION_AMOUNT).getValue());

        paymentAllocationTab.submitTab();
        claim.createPaymentSeries().getWorkspace().fillFromTo(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries")
                        .adjust(makeKeyPath(paymentSeriesProfileActionTab.getMetaKey(), EFFECTIVE_DATE.getLabel()), bpStartDate.plusMonths(2).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentSeriesProfileActionTab.getMetaKey(), EXPIRATION_DATE.getLabel()), bpStartDate.plusMonths(3).format(MM_DD_YYYY)),
                PaymentSeriesAdditionalPayeeTab.class, PaymentSeriesCalculatorActionTab.class, true);

        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.BUTTON_ADD_PAYMENT_ADDITION).click();
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(ADDITION_TYPE).setValue(STUDENT_LOAN_REPAYMENT_BENEFIT);
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_STUDENT_LOAN_AMOUNT).setValue(amount.toString());
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(BEGINNING_DATE).setValue(bpStartDate.plusMonths(2).format(MM_DD_YYYY));
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(THROUGH_DATE).setValue(bpStartDate.plusMonths(2).plusDays(25).format(MM_DD_YYYY));
        claim.createPaymentSeries().submit();

        checkPaymentSeries(claimNumber, net1ValuePaymentSeries, new Currency(300));
        claim.updatePayment().start(2);
        paymentCalculatorTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(MONTHLY_BENEFIT_AMOUNT)).hasValue(new Currency(300).toString());
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT)).hasValue(new Currency(40).toString());
        });
    }

    private void preconditionCreatePolicy() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), COBRA_PREMIUM_REIMB_DURATION.getLabel()), "18 Months")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), COBRA_PREMIUM_REIMB_AMOUNT.getLabel()), "1000")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), STUDENT_LOAN_REPAYMENT_AMOUNT.getLabel()), "$1 500")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), STUDENT_LOAN_REPAYMENT_DURATION.getLabel()), "12 Months"));
    }

    private LocalDateTime getBpStartDate(String associatedInsurableRisk) {
        claim.calculateSingleBenefitAmount().start(1);
        assertThat(claim.calculateSingleBenefitAmount().getWorkspace().getTab(CoveragesActionTab.class).getAssetList().getAsset(ASSOCIATED_INSURABLE_RISK))
                .doesNotContainOption(associatedInsurableRisk);
        claim.calculateSingleBenefitAmount().getWorkspace().fillUpTo(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), OtherIncomeBenefitActionTab.class);
        OtherIncomeBenefitActionTab.buttonSaveAndExit.click();

        claim.viewSingleBenefitCalculation().perform(1);
        return LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1).getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();
    }

    private void checkPaymentSeries(String claimNumber, Currency net1ValuePaymentSeries, Currency benefitAmount) {
        tableSummaryOfClaimPaymentSeries.getRow(1).getCell(SERIES_NUMBER).controls.links.getFirst().click();
        String firstPaymentScheduleDate = tableScheduledPaymentsOfSeries.getRow(2).getCell(PAYMENT_SCHEDULE_DATE.getName()).getValue();
        LocalDateTime nextPhaseDate = LocalDateTime.parse(firstPaymentScheduleDate, DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a"));

        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(nextPhaseDate, true);
        mainApp().reopen();
        search(claimNumber);

        claim.updatePaymentSeries().start(1);
        claim.updatePaymentSeries().submit();
        RetryService.run(predicate -> ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).isPresent(),
                () -> {
                    toSubTab(PAYMENTS);
                    return null;
                },
                StopStrategies.stopAfterAttempt(5),
                WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        claim.updatePayment().start(1);
        paymentCalculatorTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(MONTHLY_BENEFIT_AMOUNT)).hasValue(benefitAmount.toString());
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT)).hasValue(net1ValuePaymentSeries.add(benefitAmount.divide(30).multiply(26)).toString()); // NET2 = NET1+x/30*26
        });
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
    }
}
