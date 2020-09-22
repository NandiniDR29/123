package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;

import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentSeriesTableExtended;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentCalculatorTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentSeriesCalculatorActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentSeriesPaymentSeriesProfileActionTab;
import com.exigen.ren.main.modules.mywork.MyWorkContext;
import com.exigen.ren.main.modules.mywork.metadata.FilterTaskActionTabMetaData;
import com.exigen.ren.main.modules.mywork.tabs.FilterTaskActionTab;
import com.exigen.ren.main.modules.mywork.tabs.MyWorkTab;
import com.exigen.ren.main.pages.summary.TaskDetailsSummaryPage;
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
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.PAYMENTS;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TOTAL_PAYMENT_AMOUNT;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_NUMBER;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesActionTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesActionTabMetaData.EXPIRATION_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.REQUIRED_MONTHLY_COBRA_PREMIUM_AMOUNT;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData.PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData.ParticipantIndexedPreDisabilityEarningsMetaData.*;
import static com.exigen.ren.main.modules.mywork.tabs.MyWorkTab.MyWorkTasks.TASK_ID;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.NUMBER_OF_ADJUSTMENTS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.SPONSOR_PAYMENT_MODE;
import static com.exigen.ren.main.pages.summary.SummaryPage.activitiesAndUserNotes;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckUpdateCalculationForTotalBenefitCap extends ClaimGroupBenefitsLTDBaseTest implements MyWorkContext {
    private static final String CHILD_EDUCATION_BENEFIT_VALUE = "Child Education Benefit";
    private static final String COBRA_PREMIUM_REIMBURSEMENT_BENEFIT = "COBRA Premium Reimbursement Benefit";
    private static final String REHABILITATION_BENEFIT = "Rehabilitation Benefit";
    private static final String CATASTROPHIC_DISABILITY_BENEFIT_VALUE = "Catastrophic Disability Benefit";
    private static final String STUDENT_LOAN_REPAYMENT_BENEFIT = "Student Loan Repayment Benefit";
    private static final String FAMILY_CARE_BENEFIT_VALUE = "Family Care Benefit";
    private static final String CONTRIBUTION_DURING_DISABILITY_BENEFIT = "401K Contribution During Disability Benefit";
    private static final String ERROR_MESSAGE = "Indemnity Payment amount per occurrence (COLA is not applied) of <%s> on Payment #";
    private static final String TASK_NAME = "Review Pending Payment Series";
    private static final String TASK_DESCRIPTION = "Payment Series # %s. Claim # %s. Scheduled Payment posting has failed. Payee for main payment or deduction payment is additional party";
    private static final String WARNING_MASSAGE = "Current changes will impact existing Active&Completed payments. Completed Payments will be recalculated and generate the Claim balance. " +
            "Please review Active payments to check if they need to be updated or voided.";

    private PaymentPaymentPaymentAllocationTab paymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);
    private PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
    private PaymentSeriesCalculatorActionTab paymentSeriesCalculatorActionTab = claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesCalculatorActionTab.class);

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-36937", component = CLAIMS_GROUPBENEFITS)
    public void testCheckUpdateCalculationForTotalBenefitCap1() {
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime day1 = currentDate.plusMonths(6);
        LocalDateTime day2 = currentDate.plusYears(1).plusDays(1);
        LocalDateTime day3 = currentDate.plusYears(2).plusDays(1);
        Currency allocationAmount = new Currency(1050);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), CHILD_EDUCATION_BENEFIT.getLabel()), "Included")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), BENEFIT_PERCENTAGE.getLabel()), "60%"));
        createDefaultLongTermDisabilityCertificatePolicy();

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()));
        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        LOGGER.info("TEST REN-36937: Step 1");
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), day1.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), day1.plusMonths(1).minusDays(1).format(MM_DD_YYYY)), paymentCalculatorTab.getClass(), false);

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), CHILD_EDUCATION_BENEFIT_VALUE);

        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_CHILDREN).setValue("3");
        Currency amount1 = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue());
        assertThat(new Currency(paymentCalculatorTab.getAssetList().getAsset(TOTAL_TAX_AMOUNT).getValue()).add(amount1)).isEqualTo(allocationAmount);

        paymentAllocationTab.navigate();
        PaymentPaymentPaymentAllocationTab.buttonPostPayment.click();
        assertThat(Page.dialogConfirmation.labelMessage).valueContains(String.format(ERROR_MESSAGE, amount1.toString()));
        Page.dialogConfirmation.buttonCancel.click();

        LOGGER.info("TEST REN-36937: Step 2");
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(day2.format(MM_DD_YYYY));
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE).setValue(day2.plusMonths(1).minusDays(1).format(MM_DD_YYYY));

        addPaymentAddition(day2, day2.plusMonths(1).minusDays(1), CHILD_EDUCATION_BENEFIT_VALUE);

        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_CHILDREN).setValue("3");
        Currency amount2 = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue());
        assertThat(new Currency(paymentCalculatorTab.getAssetList().getAsset(TOTAL_TAX_AMOUNT).getValue()).add(amount2)).isEqualTo(allocationAmount);

        LOGGER.info("TEST REN-36937: Step 3");
        claim.addPayment().submit();
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        LOGGER.info("TEST REN-36937: Step 4");
        claim.updatePayment().start(1);
        paymentAllocationTab.navigate();
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(day1.format(MM_DD_YYYY));
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE).setValue(day1.plusMonths(1).minusDays(1).format(MM_DD_YYYY));

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), CHILD_EDUCATION_BENEFIT_VALUE);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_CHILDREN).setValue("3");
        Currency amount3 = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue());
        assertThat(new Currency(paymentCalculatorTab.getAssetList().getAsset(TOTAL_TAX_AMOUNT).getValue()).add(amount3)).isEqualTo(allocationAmount);

        LOGGER.info("TEST REN-36937: Step 5");
        paymentAllocationTab.navigate();
        PaymentPaymentPaymentAllocationTab.buttonUpdatePayment.click();
        assertThat(Page.dialogConfirmation.labelMessage).valueContains(String.format(ERROR_MESSAGE, amount3.toString()));
        Page.dialogConfirmation.buttonCancel.click();

        LOGGER.info("TEST REN-36937: Step 6");
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(day3.format(MM_DD_YYYY));
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE).setValue(day3.plusMonths(1).minusDays(1).format(MM_DD_YYYY));

        addPaymentAddition(day3, day3.plusMonths(1).minusDays(1), CHILD_EDUCATION_BENEFIT_VALUE);

        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_CHILDREN).setValue("3");
        Currency amount4 = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue());
        assertThat(new Currency(paymentCalculatorTab.getAssetList().getAsset(TOTAL_TAX_AMOUNT).getValue()).add(amount4)).isEqualTo(allocationAmount);

        LOGGER.info("TEST REN-36937: Step 7");
        claim.updatePayment().submit();
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        LOGGER.info("TEST REN-36937: Step 8");
        claim.createPaymentSeries().start();
        claim.createPaymentSeries().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries")
                .adjust(makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EFFECTIVE_DATE.getLabel()), day1.plusMonths(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EXPIRATION_DATE.getLabel()), day1.plusMonths(3).format(MM_DD_YYYY)), paymentSeriesCalculatorActionTab.getClass());

        addPaymentSeriesAddition(day1.plusMonths(1), day1.plusMonths(3), CHILD_EDUCATION_BENEFIT_VALUE);
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentAdditionMetaData.NUMBER_OF_CHILDREN).setValue("3");
        claim.createPaymentSeries().submit();
        String paymentSeries = tableSummaryOfClaimPaymentSeries.getRow(1).getCell(ClaimSummaryOfPaymentSeriesTableExtended.SERIES_NUMBER.getName()).getValue();

        LOGGER.info("TEST REN-36937: Step 9");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(day1.plusMonths(2));
        mainApp().reopen();
        search(claimNumber);

        toSubTab(PAYMENTS);
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        checkTask(claimNumber, paymentSeries);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-37024", component = CLAIMS_GROUPBENEFITS)
    public void testCheckUpdateCalculationForTotalBenefitCap2() {
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime day1 = currentDate.plusMonths(6);
        LocalDateTime day2 = currentDate.plusYears(1).plusDays(1);
        LocalDateTime day3 = currentDate.plusYears(2).plusDays(1);
        Currency allocationAmount = new Currency(2000);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), CHILD_EDUCATION_BENEFIT.getLabel()), "Included")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FAMILY_CARE_BENEFIT.getLabel()), "Included")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), CATASTROPHIC_DISABILITY_BENEFIT.getLabel()), "10%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), COBRA_PREMIUM_REIMB_AMOUNT.getLabel()), "1000")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FOUR_HUNDRED_ONE_K_CONTRIBUTION_DURING_DISABILITY.getLabel()), "1%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), STUDENT_LOAN_REPAYMENT_AMOUNT.getLabel()), "$1 500")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), BENEFIT_PERCENTAGE.getLabel()), "60%"));
        createDefaultLongTermDisabilityCertificatePolicy();

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()));
        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        LOGGER.info("TEST REN-37024: Step 1");
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), day1.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), day1.plusDays(29).format(MM_DD_YYYY)), paymentCalculatorTab.getClass(), false);

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), CHILD_EDUCATION_BENEFIT_VALUE);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_CHILDREN).setValue("3");

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM).setValue("362.5");

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), CATASTROPHIC_DISABILITY_BENEFIT_VALUE);

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), CONTRIBUTION_DURING_DISABILITY_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_401K_CONTRIBUTION_AMOUNT).setValue("12.5");

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), STUDENT_LOAN_REPAYMENT_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_STUDENT_LOAN_AMOUNT).setValue("100");

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), FAMILY_CARE_BENEFIT_VALUE);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_FAMILY_MEMBERS).setValue("1");
        Currency amount1 = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue());
        assertThat(new Currency(paymentCalculatorTab.getAssetList().getAsset(TOTAL_TAX_AMOUNT).getValue()).add(amount1)).isEqualTo(allocationAmount);

        LOGGER.info("TEST REN-37024: Step 2");
        paymentAllocationTab.navigate();
        PaymentPaymentPaymentAllocationTab.buttonPostPayment.click();
        assertThat(Page.dialogConfirmation.labelMessage).valueContains(String.format(ERROR_MESSAGE, amount1.toString()));
        Page.dialogConfirmation.buttonCancel.click();

        LOGGER.info("TEST REN-37024: Step 3");
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(day2.format(MM_DD_YYYY));
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE).setValue(day2.plusDays(29).format(MM_DD_YYYY));

        addPaymentAddition(day2, day2.plusMonths(1).minusDays(1), CHILD_EDUCATION_BENEFIT_VALUE);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_CHILDREN).setValue("3");

        addPaymentAddition(day2, day2.plusMonths(1).minusDays(1), COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM).setValue("362.5");

        addPaymentAddition(day2, day2.plusMonths(1).minusDays(1), CATASTROPHIC_DISABILITY_BENEFIT_VALUE);
        addPaymentAddition(day2, day2.plusMonths(1).minusDays(1), CONTRIBUTION_DURING_DISABILITY_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_401K_CONTRIBUTION_AMOUNT).setValue("12.5");

        addPaymentAddition(day2, day2.plusMonths(1).minusDays(1), STUDENT_LOAN_REPAYMENT_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_STUDENT_LOAN_AMOUNT).setValue("100");

        addPaymentAddition(day2, day2.plusMonths(1).minusDays(1), FAMILY_CARE_BENEFIT_VALUE);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_FAMILY_MEMBERS).setValue("1");
        Currency amount2 = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue());
        assertThat(new Currency(paymentCalculatorTab.getAssetList().getAsset(TOTAL_TAX_AMOUNT).getValue()).add(amount2)).isEqualTo(allocationAmount);

        claim.addPayment().submit();
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        LOGGER.info("TEST REN-37024: Step 4");
        claim.updatePayment().start(1);
        paymentAllocationTab.navigate();
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(day1.format(MM_DD_YYYY));
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE).setValue(day1.plusDays(29).format(MM_DD_YYYY));

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), CHILD_EDUCATION_BENEFIT_VALUE);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_CHILDREN).setValue("3");

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM).setValue("362.5");

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), CATASTROPHIC_DISABILITY_BENEFIT_VALUE);
        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), CONTRIBUTION_DURING_DISABILITY_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_401K_CONTRIBUTION_AMOUNT).setValue("12.5");

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), STUDENT_LOAN_REPAYMENT_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_STUDENT_LOAN_AMOUNT).setValue("100");

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), FAMILY_CARE_BENEFIT_VALUE);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_FAMILY_MEMBERS).setValue("1");
        Currency amount3 = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue());
        assertThat(new Currency(paymentCalculatorTab.getAssetList().getAsset(TOTAL_TAX_AMOUNT).getValue()).add(amount3)).isEqualTo(allocationAmount);

        LOGGER.info("TEST REN-37024: Step 5");
        paymentAllocationTab.navigate();
        PaymentPaymentPaymentAllocationTab.buttonUpdatePayment.click();
        assertThat(Page.dialogConfirmation.labelMessage).valueContains(String.format(ERROR_MESSAGE, amount3.toString()));
        Page.dialogConfirmation.buttonCancel.click();

        LOGGER.info("TEST REN-37024: Step 6");
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(day3.format(MM_DD_YYYY));
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE).setValue(day3.plusDays(29).format(MM_DD_YYYY));

        addPaymentAddition(day3, day3.plusMonths(1).minusDays(1), CHILD_EDUCATION_BENEFIT_VALUE);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_CHILDREN).setValue("3");

        addPaymentAddition(day3, day3.plusMonths(1).minusDays(1), COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM).setValue("362.5");

        addPaymentAddition(day3, day3.plusMonths(1).minusDays(1), CATASTROPHIC_DISABILITY_BENEFIT_VALUE);
        addPaymentAddition(day3, day3.plusMonths(1).minusDays(1), CONTRIBUTION_DURING_DISABILITY_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_401K_CONTRIBUTION_AMOUNT).setValue("12.5");

        addPaymentAddition(day3, day3.plusMonths(1).minusDays(1), STUDENT_LOAN_REPAYMENT_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_STUDENT_LOAN_AMOUNT).setValue("100");

        addPaymentAddition(day3, day3.plusMonths(1).minusDays(1), FAMILY_CARE_BENEFIT_VALUE);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_FAMILY_MEMBERS).setValue("1");
        Currency amount4 = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue());
        assertThat(new Currency(paymentCalculatorTab.getAssetList().getAsset(TOTAL_TAX_AMOUNT).getValue()).add(amount4)).isEqualTo(allocationAmount);

        LOGGER.info("TEST REN-37024: Step 7");
        paymentAllocationTab.navigate();
        PaymentPaymentPaymentAllocationTab.buttonUpdatePayment.click();
        assertThat(Page.dialogConfirmation.labelMessage).valueContains(String.format(ERROR_MESSAGE, amount4.toString()));
        Page.dialogConfirmation.buttonCancel.click();
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
        Page.dialogConfirmation.buttonYes.click();

        LOGGER.info("TEST REN-37024: Step 8");
        claim.createPaymentSeries().start();
        claim.createPaymentSeries().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries")
                .adjust(makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EFFECTIVE_DATE.getLabel()), day1.plusMonths(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EXPIRATION_DATE.getLabel()), day1.plusMonths(3).format(MM_DD_YYYY)), paymentSeriesCalculatorActionTab.getClass());

        addPaymentSeriesAddition(day1.plusMonths(1), day1.plusMonths(3), CHILD_EDUCATION_BENEFIT_VALUE);
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(NUMBER_OF_CHILDREN).setValue("3");

        addPaymentSeriesAddition(day1.plusMonths(1), day1.plusMonths(3), COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM_AMOUNT).setValue("362.5");

        addPaymentSeriesAddition(day1.plusMonths(1), day1.plusMonths(3), CATASTROPHIC_DISABILITY_BENEFIT_VALUE);
        addPaymentSeriesAddition(day1.plusMonths(1), day1.plusMonths(3), CONTRIBUTION_DURING_DISABILITY_BENEFIT);
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_401K_CONTRIBUTION_AMOUNT).setValue("12.5");

        addPaymentSeriesAddition(day1.plusMonths(1), day1.plusMonths(3), STUDENT_LOAN_REPAYMENT_BENEFIT);
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_STUDENT_LOAN_AMOUNT).setValue("100");

        addPaymentSeriesAddition(day1.plusMonths(1), day1.plusMonths(3), FAMILY_CARE_BENEFIT_VALUE);
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(NUMBER_OF_FAMILY_MEMBERS).setValue("100");

        claim.createPaymentSeries().submit();
        String paymentSeries = tableSummaryOfClaimPaymentSeries.getRow(1).getCell(ClaimSummaryOfPaymentSeriesTableExtended.SERIES_NUMBER.getName()).getValue();

        LOGGER.info("TEST REN-37024: Step 9");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(day1.plusMonths(2));
        mainApp().reopen();
        search(claimNumber);

        toSubTab(PAYMENTS);
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        checkTask(claimNumber, paymentSeries);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-37026", component = CLAIMS_GROUPBENEFITS)
    public void testCheckUpdateCalculationForTotalBenefitCap3() {
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime day1 = currentDate.plusMonths(6);
        LocalDateTime day2 = currentDate.plusYears(1).plusDays(1);
        LocalDateTime day3 = currentDate.plusYears(2).plusDays(1);
        Currency allocationAmount = new Currency(1125);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), COBRA_PREMIUM_REIMB_AMOUNT.getLabel()), "1000")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), BENEFIT_PERCENTAGE.getLabel()), "60%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), REHABILITATION_INCENTIVE_BENEFIT.getLabel()), "5%"));
        createDefaultLongTermDisabilityCertificatePolicy();

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()));
        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        LOGGER.info("TEST REN-37026: Step 1");
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), day1.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), day1.plusMonths(1).minusDays(1).format(MM_DD_YYYY)), paymentCalculatorTab.getClass(), false);

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM).setValue("337.5");

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), REHABILITATION_BENEFIT);
        Currency amount1 = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue());
        assertThat(new Currency(paymentCalculatorTab.getAssetList().getAsset(TOTAL_TAX_AMOUNT).getValue()).add(amount1)).isEqualTo(allocationAmount);

        paymentAllocationTab.navigate();
        PaymentPaymentPaymentAllocationTab.buttonPostPayment.click();
        assertThat(Page.dialogConfirmation.labelMessage).valueContains(String.format(ERROR_MESSAGE, amount1.toString()));
        Page.dialogConfirmation.buttonCancel.click();

        LOGGER.info("TEST REN-37026: Step 2");
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(day2.format(MM_DD_YYYY));
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE).setValue(day2.plusMonths(1).minusDays(1).format(MM_DD_YYYY));

        addPaymentAddition(day2, day2.plusMonths(1).minusDays(1), COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM).setValue("337.5");

        addPaymentAddition(day2, day2.plusMonths(1).minusDays(1), REHABILITATION_BENEFIT);
        Currency amount2 = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue());
        assertThat(new Currency(paymentCalculatorTab.getAssetList().getAsset(TOTAL_TAX_AMOUNT).getValue()).add(amount2)).isEqualTo(allocationAmount);

        LOGGER.info("TEST REN-37026: Step 3");
        claim.addPayment().submit();
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        LOGGER.info("TEST REN-37026: Step 4");
        claim.updatePayment().start(1);
        paymentAllocationTab.navigate();
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(day1.format(MM_DD_YYYY));
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE).setValue(day1.plusMonths(1).minusDays(1).format(MM_DD_YYYY));

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM).setValue("337.5");

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), REHABILITATION_BENEFIT);
        Currency amount3 = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue());
        assertThat(new Currency(paymentCalculatorTab.getAssetList().getAsset(TOTAL_TAX_AMOUNT).getValue()).add(amount3)).isEqualTo(allocationAmount);

        LOGGER.info("TEST REN-37026: Step 5");
        paymentAllocationTab.navigate();
        PaymentPaymentPaymentAllocationTab.buttonUpdatePayment.click();
        assertThat(Page.dialogConfirmation.labelMessage).valueContains(String.format(ERROR_MESSAGE, amount3.toString()));
        Page.dialogConfirmation.buttonCancel.click();

        LOGGER.info("TEST REN-37026: Step 6");
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(day3.format(MM_DD_YYYY));
        paymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE).setValue(day3.plusMonths(1).minusDays(1).format(MM_DD_YYYY));

        addPaymentAddition(day3, day3.plusMonths(1).minusDays(1), COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM).setValue("337.5");

        addPaymentAddition(day3, day3.plusMonths(1).minusDays(1), REHABILITATION_BENEFIT);
        Currency amount4 = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue());
        assertThat(new Currency(paymentCalculatorTab.getAssetList().getAsset(TOTAL_TAX_AMOUNT).getValue()).add(amount4)).isEqualTo(allocationAmount);

        LOGGER.info("TEST REN-37026: Step 7");
        claim.updatePayment().submit();
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        LOGGER.info("TEST REN-37026: Step 8");
        claim.createPaymentSeries().start();
        claim.createPaymentSeries().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries")
                .adjust(makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EFFECTIVE_DATE.getLabel()), day1.plusMonths(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EXPIRATION_DATE.getLabel()), day1.plusMonths(3).format(MM_DD_YYYY)), paymentSeriesCalculatorActionTab.getClass());

        addPaymentSeriesAddition(day1.plusMonths(1), day1.plusMonths(3), COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM_AMOUNT).setValue("337.5");

        addPaymentSeriesAddition(day1.plusMonths(1), day1.plusMonths(3), REHABILITATION_BENEFIT);
        claim.createPaymentSeries().submit();
        String paymentSeries = tableSummaryOfClaimPaymentSeries.getRow(1).getCell(ClaimSummaryOfPaymentSeriesTableExtended.SERIES_NUMBER.getName()).getValue();

        LOGGER.info("TEST REN-37026: Step 9");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(day1.plusMonths(2));
        mainApp().reopen();
        search(claimNumber);

        toSubTab(PAYMENTS);
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        checkTask(claimNumber, paymentSeries);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37028", component = CLAIMS_GROUPBENEFITS)
    public void testCheckUpdateCalculationForTotalBenefitCap4() {
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime day1 = currentDate.plusMonths(19);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), COBRA_PREMIUM_REIMB_AMOUNT.getLabel()), "1000")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), CHILD_EDUCATION_BENEFIT.getLabel()), "Included")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), BENEFIT_PERCENTAGE.getLabel()), "60%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), COST_OF_LIVING_ADJUSTMENT_BENEFIT.getLabel()), "3%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), NUMBER_OF_ADJUSTMENTS.getLabel()), "5 Years"));
        createDefaultLongTermDisabilityCertificatePolicy();

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", "TestData_WithOneBenefit")
                .adjust(tdSpecific().getTestData("TestData_Claim2").resolveLinks()));
        claim.claimOpen().perform();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), day1.minusMonths(12).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), day1.minusDays(1).format(MM_DD_YYYY)));

        LOGGER.info("TEST REN-37028: Step 1");
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), day1.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), day1.plusMonths(1).minusDays(1).format(MM_DD_YYYY)), paymentCalculatorTab.getClass(), false);

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM).setValue("200");

        addPaymentAddition(day1, day1.plusMonths(1).minusDays(1), CHILD_EDUCATION_BENEFIT_VALUE);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_CHILDREN).setValue("3");

        Currency amount = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue());
        assertSoftly(softly -> {
            softly.assertThat(new Currency(paymentCalculatorTab.getAssetList().getAsset(TOTAL_TAX_AMOUNT).getValue()).add(amount)).isEqualTo(new Currency(1272.50));
            softly.assertThat(PaymentCalculatorTab.tableListOfPaymentAddition.getRow(PaymentCalculatorTab.PaymentCalculatorPaymentAddition.ADDITION_TYPE.getName(), "COLA"))
                    .hasCellWithValue(PaymentCalculatorTab.PaymentCalculatorPaymentAddition.AMOUNT.getName(), new Currency(22.50).toString());
        });

        LOGGER.info("TEST REN-37028: Step 2");
        claim.addPayment().submit();
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(2);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37034", component = CLAIMS_GROUPBENEFITS)
    public void testCheckUpdateCalculationForTotalBenefitCap5() {
        String taskDescription1 = "Payment # %1$s. Claim # %2$s. Indemnity Payment amount per occurrence (COLA is not applied) " +
                "of <$2,600> on Payment # <%1$s> exceeds Total Benefit Cap per occurrence of <$2,000> by <$600>..";
        String taskDescription2 = "Payment # %1$s. Claim # %2$s. Indemnity Payment amount per occurrence (COLA is not applied) " +
                "of <$2,600> on Payment # <%1$s> exceeds Total Benefit Cap per occurrence of <$2,500> by <$100>..";
        Currency allocationAmount = new Currency(2600);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()))
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Voluntary")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), COBRA_PREMIUM_REIMB_AMOUNT.getLabel()), "1100")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), CHILD_EDUCATION_BENEFIT.getLabel()), "Included")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), BENEFIT_PERCENTAGE.getLabel()), "60%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), COST_OF_LIVING_ADJUSTMENT_BENEFIT.getLabel()), "3%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), NUMBER_OF_ADJUSTMENTS.getLabel()), "5 Years"));
        createDefaultLongTermDisabilityCertificatePolicy();

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", "TestData_WithOneBenefit")
                .adjust(tdSpecific().getTestData("TestData_Claim3").resolveLinks()));
        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime day1 = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1).getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        claim.updateBenefit().start(1);
        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE).setValue(day1.format(MM_DD_YYYY));
        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE).setValue(day1.plusMonths(6).format(MM_DD_YYYY));
        claim.updateBenefit().submit();

        LOGGER.info("TEST REN-37034: create 1 payment from precondition");
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), day1.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), day1.plusMonths(1).minusDays(1).format(MM_DD_YYYY)), paymentCalculatorTab.getClass(), false);

        addPaymentAddition(day1.minusYears(1), day1.plusYears(1), COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM).setValue("1000");

        addPaymentAddition(day1.minusYears(1), day1.plusYears(1), CHILD_EDUCATION_BENEFIT_VALUE);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_CHILDREN).setValue("1");
        assertThat(new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue())).isEqualTo(allocationAmount);

        claim.addPayment().submit();
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);

        LOGGER.info("TEST REN-37034: create 2 payment from precondition");
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), day1.plusMonths(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), day1.plusMonths(2).minusDays(1).format(MM_DD_YYYY)), paymentCalculatorTab.getClass(), false);

        addPaymentAddition(day1.minusYears(1), day1.plusYears(1), COBRA_PREMIUM_REIMBURSEMENT_BENEFIT);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM).setValue("1000");

        addPaymentAddition(day1.minusYears(1), day1.plusYears(1), CHILD_EDUCATION_BENEFIT_VALUE);
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_CHILDREN).setValue("1");

        assertThat(new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue())).isEqualTo(allocationAmount);
        claim.addPayment().submit();

        LOGGER.info("TEST REN-37034: Step 1");
        claim.updateBenefit().start(1);
        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(INDEXED_PRE_DISABILITY_EARNINGS).setValue("2000");
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(INDEXED_PRE_DISABILITY_EARNINGS))
                .hasWarningWithText(WARNING_MASSAGE);
        claim.updateBenefit().submit();

        toSubTab(PAYMENTS);
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(TOTAL_PAYMENT_AMOUNT)).hasValue(allocationAmount.toString());
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(TOTAL_PAYMENT_AMOUNT)).hasValue(allocationAmount.toString());
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(TRANSACTION_STATUS)).hasValue("Pending");
        });
        String paymentNumber = tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(PAYMENT_NUMBER.getName()).getValue();

        LOGGER.info("TEST REN-37034: Step 2");
        myWork.navigate();
        myWork.filterTask().perform(claimNumber, "Active", "Approve Payment");
        MyWorkTab.tableMyWorkTasks.getRow(1).getCell(TASK_ID.getName()).controls.links.getFirst().click();
        assertThat(TaskDetailsSummaryPage.taskDescription).hasValue(String.format(taskDescription1, paymentNumber, claimNumber));

        LOGGER.info("TEST REN-37034: Step 3");
        mainApp().reopen(approvalUsername, approvalPassword);
        search(claimNumber);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 2);
        assertThat(ErrorPage.tableError.getRow(1).getCell(ErrorPage.TableError.MESSAGE.getName()).getValue()).
                contains(String.format("Indemnity Payment amount per occurrence (COLA is not applied) of <$2,600> on Payment # <%s> exceeds Total Benefit Cap per occurrence of <$2,000> by <$600>", paymentNumber));
        Tab.buttonCancel.click();
        Tab.buttonCancel.click();

        LOGGER.info("TEST REN-37034: Step 4");
        claim.updateBenefit().start(1);
        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(INDEXED_PRE_DISABILITY_EARNINGS).setValue("5000");
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(INDEXED_PRE_DISABILITY_EARNINGS))
                .hasWarningWithText(WARNING_MASSAGE);
        claim.updateBenefit().submit();

        toSubTab(PAYMENTS);
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(TOTAL_PAYMENT_AMOUNT)).hasValue(allocationAmount.toString());
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(TOTAL_PAYMENT_AMOUNT)).hasValue(allocationAmount.toString());
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(TRANSACTION_STATUS)).hasValue("Approved");
        });

        LOGGER.info("TEST REN-37034: Step 5");
        claim.updateBenefit().start(1);
        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE).setValue(day1.minusDays(1).format(MM_DD_YYYY));
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE)).hasWarningWithText(WARNING_MASSAGE);

        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE).setValue(day1.minusDays(1).format(MM_DD_YYYY));
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE)).hasWarningWithText(WARNING_MASSAGE);
        claim.updateBenefit().submit();

        toSubTab(PAYMENTS);
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(TOTAL_PAYMENT_AMOUNT)).hasValue(allocationAmount.toString());
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(TOTAL_PAYMENT_AMOUNT)).hasValue(allocationAmount.toString());
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(TRANSACTION_STATUS)).hasValue("Pending");
            softly.assertThat(activitiesAndUserNotes.getRow(3).getCell(DESCRIPTION).getValue()).contains("Task Created Approve Payment");
        });

        LOGGER.info("TEST REN-37034: Step 6");
        myWork.navigate();
        myWork.filterTask().perform(claimNumber, "Active", "Approve Payment");
        MyWorkTab.tableMyWorkTasks.getRow(1).getCell(TASK_ID.getName()).controls.links.getFirst().click();
        assertThat(TaskDetailsSummaryPage.taskDescription).hasValue(String.format(taskDescription2, paymentNumber, claimNumber));

        search(claimNumber);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 2);
        assertThat(ErrorPage.tableError.getRow(1).getCell(ErrorPage.TableError.MESSAGE.getName()).getValue()).
                contains(String.format("Indemnity Payment amount per occurrence (COLA is not applied) of <$2,600> on Payment # <%s> exceeds Total Benefit Cap per occurrence of <$2,500> by <$100>", paymentNumber));
        Tab.buttonCancel.click();
        Tab.buttonCancel.click();

        LOGGER.info("TEST REN-37034: Step 7");
        claim.updateBenefit().start(1);
        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE).setValue(day1.plusMonths(6).format(MM_DD_YYYY));
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE)).hasWarningWithText(WARNING_MASSAGE);

        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE).setValue(day1.format(MM_DD_YYYY));
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE)).hasWarningWithText(WARNING_MASSAGE);
        claim.updateBenefit().submit();

        toSubTab(PAYMENTS);
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(TOTAL_PAYMENT_AMOUNT)).hasValue(allocationAmount.toString());
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(TOTAL_PAYMENT_AMOUNT)).hasValue(allocationAmount.toString());
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(TRANSACTION_STATUS)).hasValue("Approved");
        });
    }

    private void addPaymentAddition(LocalDateTime beginningDate, LocalDateTime throughDate, String additionType) {
        paymentCalculatorTab.navigate();
        paymentCalculatorTab.getAssetList().getAsset(BUTTON_ADD_PAYMENT_ADDITION).click();
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE).setValue(additionType);

        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE).setValue(beginningDate.format(MM_DD_YYYY));
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE).setValue(throughDate.format(MM_DD_YYYY));
    }

    private void addPaymentSeriesAddition(LocalDateTime beginningDate, LocalDateTime throughDate, String additionType) {
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.BUTTON_ADD_PAYMENT_ADDITION).click();

        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(ADDITION_TYPE).setValue(additionType);
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(BEGINNING_DATE).setValue(beginningDate.format(MM_DD_YYYY));
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(THROUGH_DATE).setValue(throughDate.format(MM_DD_YYYY));
    }

    private void checkTask(String claimNumber, String paymentNumber) {
        myWork.navigate();
        myWork.filterTask().start();
        myWork.filterTask().getWorkspace().getTab(FilterTaskActionTab.class).getAssetList().getAsset(FilterTaskActionTabMetaData.REFERENCE_ID).setValue(claimNumber);
        myWork.filterTask().getWorkspace().getTab(FilterTaskActionTab.class).getAssetList().getAsset(FilterTaskActionTabMetaData.TASK_NAME).setValue(TASK_NAME);
        myWork.filterTask().submit();

        MyWorkTab.tableMyWorkTasks.getRow(1).getCell(TASK_ID.getName()).controls.links.getFirst().click();
        assertThat(TaskDetailsSummaryPage.taskDescription).hasValue(String.format(TASK_DESCRIPTION, paymentNumber, claimNumber));
    }
}
