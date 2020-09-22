/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.integration;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.general.scheduler.JobContext;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.MyWorkConstants;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentDetailsTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitReservesActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentDetailsTab;
import com.exigen.ren.main.modules.mywork.MyWorkContext;
import com.exigen.ren.main.pages.summary.TaskDetailsSummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsACBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYYMMDD;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimGAAvailableBenefits.ACCIDENTAL_DEATH;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimGAAvailableBenefits.CRITICAL_ILLNESS;
import static com.exigen.ren.main.enums.ClaimConstants.PaymentsAndRecoveriesTransactionStatus.ISSUED;
import static com.exigen.ren.main.enums.ClaimConstants.PaymentsAndRecoveriesTransactionStatus.PENDING;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_NUMBER;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.TRANSACTION_STATUS;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.*;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage.tableAllSingleBenefitCalculations;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestManualJobExtractClaimsWhenGLExtractFailsForGA extends ClaimGroupBenefitsACBaseTest implements JobContext, MyWorkContext {

    private static final String TASK_NAME = "Claim Payment Reversal";

    private TestData paymentData = tdClaim.getTestData("ClaimPayment", "TestData_PartialPayment");
    private String reverseType = paymentData.getValue(
            PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.RESERVE_TYPE.getLabel());
    private Currency reverseAmount = new Currency(tdClaim.getTestData("BenefitReserves", TestDataKey.DEFAULT_TEST_DATA_KEY).getValue(
            BenefitReservesActionTab.class.getSimpleName(), reverseType + " Reserve"));

    @Test(groups = {INTEGRATION, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-16597", component = INTEGRATION)
    public void testManualJobExtractClaimsWhenGLExtractFailsForGA() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createDefaultGroupAccidentMasterPolicy();

        createDefaultGroupAccidentClaimForMasterPolicy();

        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_AccidentalDeath"), ACCIDENTAL_DEATH);
        claim.addBenefitReserves().perform(tdClaim.getTestData("BenefitReserves", "TestData"), ACCIDENTAL_DEATH);

        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_CriticalIllness"));
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_CriticalIllness"), CRITICAL_ILLNESS);

        String claimantAccidentalDeath =
                tableAllSingleBenefitCalculations.getRow(1).getCell(ClaimAdjudicationBenefitPage.AllSingleBenefitCalculations.CLAIMANT.getName()).getValue()
                        .split(" ")[0];
        String claimantCriticalIllness =
                tableAllSingleBenefitCalculations.getRow(2).getCell(ClaimAdjudicationBenefitPage.AllSingleBenefitCalculations.CLAIMANT.getName()).getValue()
                        .split(" ")[0];

        claim.addBenefitReserves().perform(tdClaim.getTestData("BenefitReserves", "TestData"), CRITICAL_ILLNESS);

        LOGGER.info("TEST: Post Payment for Claim #" + claimNumber);
        LocalDateTime effDate = TimeSetterUtil.getInstance().getCurrentTime();
        Currency paymentAmount = reverseAmount.divide(5).subtract(new Currency("0.1"));

        LOGGER.info("TEST: Payment with Payment Issue Date = X-1");
        //e) Benefit Type is Accidental Death: Payment E with payment status 'Issued' and Payment Issue Date=X-1;
        String paymentE = postPayment(paymentAmount, claimantAccidentalDeath);

        LOGGER.info("TEST: Issue Payment with Payment Issue Date = X-1");
        changePaymentStatusToIssue(claimNumber, paymentE);

        LOGGER.info("TEST: Payment with Payment Issue Date = X");
        mainApp().close();

        TimeSetterUtil.getInstance().nextPhase(effDate.plusDays(1));
        mainApp().reopen();
        MainPage.QuickSearch.search(claimNumber);

        //b) Benefit Type is Accidental Death: Payment B with payment status other than "Issued";
        String paymentB = postPayment(paymentAmount, claimantAccidentalDeath);
        //a) Benefit Type is Accidental Death: Payment A with payment status 'Issued' and Payment Issue Date=X;
        String paymentA = postPayment(paymentAmount, claimantAccidentalDeath);
        //c) Benefit Type is other than Accidental Death: Payment C with payment status 'Issued' and Payment Issue Date=X;
        String paymentC = postPayment(paymentAmount, claimantCriticalIllness);

        LOGGER.info("TEST: Issue Payment with Payment Issue Date = X");
        changePaymentStatusToIssue(claimNumber, paymentA, paymentC);

        LOGGER.info("TEST: Payment with Payment Issue Date = X+1");
        mainApp().close();

        TimeSetterUtil.getInstance().nextPhase(effDate.plusDays(2));
        mainApp().reopen();
        MainPage.QuickSearch.search(claimNumber);

        //d) Benefit Type is Accidental Death: Payment D with payment status 'Issued' and Payment Issue Date=X+1;
        String paymentD = postPayment(paymentAmount, claimantAccidentalDeath);

        LOGGER.info("TEST: Issue Payment with Payment Issue Date = X+1");
        changePaymentStatusToIssue(claimNumber, paymentD);

        assertSoftly(softly -> {
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries).with(TRANSACTION_STATUS, ISSUED).hasMatchingRows(4);
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries).with(TRANSACTION_STATUS, PENDING).hasMatchingRows(1);
        });

        LOGGER.info("TEST: Create and run " + GeneralSchedulerPage.LEDGER_TRANSFER_JOB.getGroupName());
        JobRunner.executeJob(GeneralSchedulerPage.LEDGER_TRANSFER_JOB);

        LOGGER.info("TEST: Create and run " + GeneralSchedulerPage.CLAIMS_PAYMENT_REVERSAL_JOB.getGroupName());
        Job job = new Job(GeneralSchedulerPage.CLAIMS_PAYMENT_REVERSAL_JOB.getGroupName())
                .setJobParameters(ImmutableMap.of("JOB_UI_PARAMS", "Accounting Date="+effDate.plusDays(1).toLocalDate().format(YYYYMMDD)));
        JobRunner.executeJob(new JobGroup(GeneralSchedulerPage.CLAIMS_PAYMENT_REVERSAL_JOB.getGroupName(), job));

        mainApp().reopen();
        linkAllQueues.click();
        myWork.filterTask().perform(paymentE);
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), TASK_NAME)).isAbsent();

        myWork.filterTask().perform(paymentB);
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), TASK_NAME)).isAbsent();

        myWork.filterTask().perform(paymentC);
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), TASK_NAME)).isAbsent();

        myWork.filterTask().perform(paymentD);
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), TASK_NAME)).isAbsent();

        myWork.filterTask().perform(paymentA);
        openTaskDetails(TASK_NAME);
        assertSoftly(softly -> {
            softly.assertThat(TaskDetailsSummaryPage.taskName).hasValue(TASK_NAME);
            softly.assertThat(TaskDetailsSummaryPage.taskDescription).hasValue(String.format("%s for %s Life/AD&D/Dental", TASK_NAME, claimNumber));
            softly.assertThat(TaskDetailsSummaryPage.type).hasValue("Claims Payment");
            softly.assertThat(TaskDetailsSummaryPage.priority).hasValue("1");
            softly.assertThat(TaskDetailsSummaryPage.queueName).hasValue("Ledger Transfer");
            softly.assertThat(TaskDetailsSummaryPage.warningDate.getValue()).contains(effDate.plusDays(2).toLocalDate().format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(TaskDetailsSummaryPage.dueDate.getValue())
                    .contains(calculateDueDateTime(effDate, 2).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(TaskDetailsSummaryPage.assignedTo.getValue()).isEmpty();
        });
    }

    private String postPayment(Currency paymentAmount, String paymentTo) {
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_PartialPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(),
                        PaymentPaymentPaymentDetailsTabMetaData.PAYMENT_TO.getLabel()), String.format("contains=%s", paymentTo))
                .resolveLinks()
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PaymentPaymentPaymentDetailsTabMetaData.GROSS_AMOUNT.getLabel()),
                        paymentAmount.toPlainString())
                .resolveLinks()
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.ALLOCATION_AMOUNT.getLabel()),
                        paymentAmount.toPlainString())
                .resolveLinks());
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasSomeRows();
        String num = tableSummaryOfClaimPaymentsAndRecoveries.getRows().get(tableSummaryOfClaimPaymentsAndRecoveries.getRows().size() - 1).getCell(PAYMENT_NUMBER.getName()).getValue();
        LOGGER.info("Payment created: " + num);
        return num;
    }

    private void changePaymentStatusToIssue(String claimNumber, String... paymentNumbers) {
        mainApp().reopen(approvalUsername, approvalPassword);
        MainPage.QuickSearch.search(claimNumber);

        Arrays.asList(paymentNumbers).forEach(paymentNumber -> {
            claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), paymentNumber);
            claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), paymentNumber);
        });
    }
}
