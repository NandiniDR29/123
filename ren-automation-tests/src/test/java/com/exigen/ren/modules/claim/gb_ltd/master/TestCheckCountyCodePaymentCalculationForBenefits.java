package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants;
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
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.common.Tab.cancelClickAndCloseDialog;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.PAYMENTS;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentSeriesTable.SERIES_NUMBER;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_SCHEDULE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.NUMBER_OF_ADJUSTMENTS;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EXPIRATION_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.claim.common.tabs.PaymentCalculatorTab.tableListOfPaymentAddition;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageDeterminationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.main.modules.mywork.tabs.MyWorkTab.MyWorkTasks.TASK_ID;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.SPONSOR_PAYMENT_MODE;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckCountyCodePaymentCalculationForBenefits extends ClaimGroupBenefitsLTDBaseTest implements MyWorkContext {
    private PaymentPaymentPaymentAllocationTab paymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);
    private PaymentSeriesCalculatorActionTab paymentSeriesCalculatorActionTab = claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesCalculatorActionTab.class);
    private PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-32192", component = CLAIMS_GROUPBENEFITS)
    public void testCheckCountyCodePaymentCalculation1() {
        createPolicyFromPrecondition("$1 500");

        LOGGER.info("TEST REN-32192: Step 1");
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "5000"));
        disabilityClaim.claimOpen().perform();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        LOGGER.info("TEST REN-32192: Step 2");
        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_IndemnityPaymentWithAdditionParty1")
                        .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusDays(29).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[1]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[1]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[2]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[2]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[3]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[3]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY)),
                paymentCalculatorTab.getClass(), true);
        assertThat(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT)).hasValue(new Currency(5000).toString());

        claim.addPayment().submit();
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-32195", component = CLAIMS_GROUPBENEFITS)
    public void testCheckCountyCodePaymentCalculation2() {
        createPolicyFromPrecondition("$1 500");

        LOGGER.info("TEST REN-32195: Step 1");
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "5000"));
        disabilityClaim.claimOpen().perform();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        LOGGER.info("TEST REN-32195: Step 2");
        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_IndemnityPaymentWithAdditionParty2")
                        .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusDays(29).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[1]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[1]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[2]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[2]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[3]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[3]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[4]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[4]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY)),
                paymentCalculatorTab.getClass(), true);

        paymentAllocationTab.navigate();
        PaymentPaymentPaymentAllocationTab.buttonPostPayment.click();
        assertThat(Page.dialogConfirmation.labelMessage)
                .valueContains("Indemnity Payment amount per occurrence (COLA is not applied) of <$5,200> on Payment # <Empty> exceeds Total Benefit Cap per occurrence of <$5,000> by <$200>.");
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-32203", component = CLAIMS_GROUPBENEFITS)
    public void testCheckCountyCodePaymentCalculation3() {
        createPolicyFromPrecondition("$2 000");

        LOGGER.info("TEST REN-32203: Step 1");
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "5000")
                .adjust(makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().plusYears(10).format(MM_DD_YYYY)));
        disabilityClaim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        LOGGER.info("TEST REN-32203: Step 2");
        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_IndemnityPaymentWithAdditionParty3")
                        .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[1]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[1]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[2]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[2]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[3]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[3]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY)),
                paymentCalculatorTab.getClass(), true);

        paymentAllocationTab.navigate();
        PaymentPaymentPaymentAllocationTab.buttonPostPayment.click();
        assertThat(Page.dialogConfirmation.labelMessage)
                .valueContains("Indemnity Payment amount per occurrence (COLA is not applied) of <$5,800> on Payment # <Empty> exceeds Total Benefit Cap per occurrence of <$5,500> by <$300>");
        Page.dialogConfirmation.buttonCancel.click();
        cancelClickAndCloseDialog();

        LOGGER.info("TEST REN-32203: Step 3");
        claim.createPaymentSeries().start();
        claim.createPaymentSeries().getWorkspace().fillUpTo(tdSpecific().getTestData("TestData_CreatePaymentSeries")
                        .adjust(makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EFFECTIVE_DATE.getLabel()), bpStartDate.plusMonths(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EXPIRATION_DATE.getLabel()), bpStartDate.plusMonths(2).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentSeriesCalculatorActionTab.getMetaKey(), PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION.getLabel() + "[0]", BEGINNING_DATE.getLabel()), bpStartDate.plusMonths(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentSeriesCalculatorActionTab.getMetaKey(), PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION.getLabel() + "[0]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(2).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentSeriesCalculatorActionTab.getMetaKey(), PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION.getLabel() + "[1]", BEGINNING_DATE.getLabel()), bpStartDate.plusMonths(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentSeriesCalculatorActionTab.getMetaKey(), PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION.getLabel() + "[1]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(2).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentSeriesCalculatorActionTab.getMetaKey(), PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION.getLabel() + "[2]", BEGINNING_DATE.getLabel()), bpStartDate.plusMonths(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentSeriesCalculatorActionTab.getMetaKey(), PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION.getLabel() + "[2]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(2).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentSeriesCalculatorActionTab.getMetaKey(), PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION.getLabel() + "[3]", BEGINNING_DATE.getLabel()), bpStartDate.plusMonths(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentSeriesCalculatorActionTab.getMetaKey(), PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION.getLabel() + "[3]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(2).minusDays(1).format(MM_DD_YYYY)),
                paymentSeriesCalculatorActionTab.getClass(), true);

        claim.createPaymentSeries().submit();
        String paymentSeries = tableSummaryOfClaimPaymentSeries.getRow(1).getCell(TableConstants.ClaimSummaryOfPaymentSeriesTableExtended.SERIES_NUMBER.getName()).getValue();

        tableSummaryOfClaimPaymentSeries.getRow(1).getCell(SERIES_NUMBER).controls.links.getFirst().click();
        String firstPaymentScheduleDate = tableScheduledPaymentsOfSeries.getRow(1).getCell(PAYMENT_SCHEDULE_DATE.getName()).getValue();
        LocalDateTime nextPhaseDate = LocalDateTime.parse(firstPaymentScheduleDate, DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a"));

        LOGGER.info("TEST REN-32203: Step 3");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(nextPhaseDate, true);
        mainApp().reopen();
        search(claimNumber);

        toSubTab(PAYMENTS);
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(0);

        myWork.navigate();
        myWork.filterTask().start();
        myWork.filterTask().getWorkspace().getTab(FilterTaskActionTab.class).getAssetList().getAsset(FilterTaskActionTabMetaData.REFERENCE_ID).setValue(claimNumber);
        myWork.filterTask().getWorkspace().getTab(FilterTaskActionTab.class).getAssetList().getAsset(FilterTaskActionTabMetaData.TASK_NAME).setValue("Review Pending Payment Series");
        myWork.filterTask().submit();

        MyWorkTab.tableMyWorkTasks.getRow(1).getCell(TASK_ID.getName()).controls.links.getFirst().click();
        assertThat(TaskDetailsSummaryPage.taskDescription)
                .valueContains(String.format("Payment Series # %s. Claim # %s. Scheduled Payment posting has failed. Indemnity Payment amount per occurrence (COLA is not applied)", paymentSeries, claimNumber));
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-32214", component = CLAIMS_GROUPBENEFITS)
    public void testCheckCountyCodePaymentCalculation4() {
        createPolicyFromPrecondition("$2 000");

        LOGGER.info("TEST REN-32214: Step 1");
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "5000")
                .adjust(makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().plusYears(10).format(MM_DD_YYYY)));
        disabilityClaim.claimOpen().perform();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        LOGGER.info("TEST REN-32214: Step 2");
        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_IndemnityPaymentWithAdditionParty4")
                        .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusDays(60).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", THROUGH_DATE.getLabel()), bpStartDate.plusDays(29).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[1]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[1]", THROUGH_DATE.getLabel()), bpStartDate.plusDays(29).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[2]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[2]", THROUGH_DATE.getLabel()), bpStartDate.plusDays(29).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[3]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[3]", THROUGH_DATE.getLabel()), bpStartDate.plusDays(29).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[4]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[4]", THROUGH_DATE.getLabel()), bpStartDate.plusDays(29).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[5]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[5]", THROUGH_DATE.getLabel()), bpStartDate.plusDays(29).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[6]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[6]", THROUGH_DATE.getLabel()), bpStartDate.plusDays(29).format(MM_DD_YYYY)),
                paymentCalculatorTab.getClass(), true);
        assertThat(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT)).hasValue(new Currency(9000).toString());

        claim.addPayment().submit();
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-32327", component = CLAIMS_GROUPBENEFITS)
    public void testCheckCountyCodePaymentCalculation5() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()))
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Voluntary")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), CATASTROPHIC_DISABILITY_BENEFIT.getLabel()), "10%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), COBRA_PREMIUM_REIMB_AMOUNT.getLabel()), "500")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), CHILD_EDUCATION_BENEFIT.getLabel()), "Included")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FAMILY_CARE_BENEFIT.getLabel()), "Included")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FOUR_HUNDRED_ONE_K_CONTRIBUTION_DURING_DISABILITY.getLabel()), "1%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), STUDENT_LOAN_REPAYMENT_AMOUNT.getLabel()), "$1 500")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), COST_OF_LIVING_ADJUSTMENT_BENEFIT.getLabel()), "3%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), NUMBER_OF_ADJUSTMENTS.getLabel()), "5 Years"));

        LOGGER.info("TEST REN-32327: Step 1");
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "5000"));
        disabilityClaim.claimOpen().perform();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusYears(1).minusDays(1).format(MM_DD_YYYY)));

        LOGGER.info("TEST REN-32327: Step 2");
        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_IndemnityPaymentWithAdditionParty5")
                        .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.plusYears(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusYears(1).plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", BEGINNING_DATE.getLabel()), bpStartDate.plusYears(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", THROUGH_DATE.getLabel()), bpStartDate.plusYears(1).plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[1]", BEGINNING_DATE.getLabel()), bpStartDate.plusYears(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[1]", THROUGH_DATE.getLabel()), bpStartDate.plusYears(1).plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[2]", BEGINNING_DATE.getLabel()), bpStartDate.plusYears(1).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[2]", THROUGH_DATE.getLabel()), bpStartDate.plusYears(1).plusMonths(1).minusDays(1).format(MM_DD_YYYY)),
                paymentCalculatorTab.getClass(), true);
        assertThat(tableListOfPaymentAddition.getRow(PaymentCalculatorTab.PaymentCalculatorPaymentAddition.ADDITION_TYPE.getName(), "COLA"))
                .hasCellWithValue(PaymentCalculatorTab.PaymentCalculatorPaymentAddition.AMOUNT.getName(), new Currency(90).toString());

        claim.addPayment().submit();
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(2);
    }

    private void createPolicyFromPrecondition(String studentAmount) {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()))
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Voluntary")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), CATASTROPHIC_DISABILITY_BENEFIT.getLabel()), "10%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), COBRA_PREMIUM_REIMB_AMOUNT.getLabel()), "500")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), CHILD_EDUCATION_BENEFIT.getLabel()), "Included")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FAMILY_CARE_BENEFIT.getLabel()), "Included")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FOUR_HUNDRED_ONE_K_CONTRIBUTION_DURING_DISABILITY.getLabel()), "1%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), STUDENT_LOAN_REPAYMENT_AMOUNT.getLabel()), studentAmount)
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), REHABILITATION_INCENTIVE_BENEFIT.getLabel()), "5%"));
    }
}
