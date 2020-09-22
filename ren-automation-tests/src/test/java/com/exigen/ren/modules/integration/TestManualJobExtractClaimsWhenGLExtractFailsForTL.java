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
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYYMMDD;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimTAAvailableBenefits.PREMIUM_WAIVER;
import static com.exigen.ren.main.enums.ClaimConstants.PaymentsAndRecoveriesTransactionStatus.ISSUED;
import static com.exigen.ren.main.enums.ClaimConstants.PaymentsAndRecoveriesTransactionStatus.PENDING;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_NUMBER;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.TRANSACTION_STATUS;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.*;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestManualJobExtractClaimsWhenGLExtractFailsForTL extends ClaimGroupBenefitsTLBaseTest implements JobContext, MyWorkContext {

    private static final String TASK_NAME = "Claim Payment Reversal";

    private TestData paymentData = tdClaim.getTestData("ClaimPayment", "TestData_PartialPayment");
    private String reverseType = paymentData.getValue(
            PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.RESERVE_TYPE.getLabel());
    private Currency reverseAmount = new Currency(tdClaim.getTestData("BenefitReserves", TestDataKey.DEFAULT_TEST_DATA_KEY).getValue(
            BenefitReservesActionTab.class.getSimpleName(), reverseType + " Reserve"));

    @Test(groups = {INTEGRATION, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-16591", component = INTEGRATION)
    public void testManualJobExtractClaimsWhenGLExtractFailsForTL() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        createDefaultSelfAdminTermLifeInsuranceMasterPolicy();

        termLifeClaim.create(termLifeClaim.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_PremiumWaiverBenefit"));
        claim.claimOpen().perform();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_PremiumWaiver"), PREMIUM_WAIVER);
        claim.addBenefitReserves().perform(tdClaim.getTestData("BenefitReserves", "TestData"), PREMIUM_WAIVER);

        LOGGER.info("TEST: Post Payment for Claim #" + claimNumber);
        LocalDateTime effDate = TimeSetterUtil.getInstance().getCurrentTime();
        Currency paymentAmount = reverseAmount.divide(4).subtract(new Currency("0.1"));

        LOGGER.info("TEST: Payment with Payment Issue Date = X-1");
        //d) Payment D with payment status 'Issued' and Payment Issue Date=X-1;
        String paymentD = postPayment(paymentAmount);

        LOGGER.info("TEST: Issue Payment with Payment Issue Date = X-1");
        changePaymentStatusToIssue(paymentD, claimNumber);

        LOGGER.info("TEST: Payment with Payment Issue Date = X");
        mainApp().close();

        TimeSetterUtil.getInstance().nextPhase(effDate.plusDays(1));
        mainApp().reopen();
        MainPage.QuickSearch.search(claimNumber);

        //b) Payment B with payment status other than "Issued";
        String paymentB = postPayment(paymentAmount);
        //a) Payment A with payment status 'Issued' and Payment Issue Date=X;
        String paymentA = postPayment(paymentAmount);

        LOGGER.info("TEST: Issue Payment with Payment Issue Date = X");
        changePaymentStatusToIssue(paymentA, claimNumber);

        LOGGER.info("TEST: Payment with Payment Issue Date = X+1");
        mainApp().close();

        TimeSetterUtil.getInstance().nextPhase(effDate.plusDays(2));
        mainApp().reopen();
        MainPage.QuickSearch.search(claimNumber);

        //c) Payment C with payment status 'Issued' and Payment Issue Date=X+1;
        String paymentC = postPayment(paymentAmount);

        LOGGER.info("TEST: Issue Payment with Payment Issue Date = X+1");
        changePaymentStatusToIssue(paymentC, claimNumber);

        assertSoftly(softly -> {
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries).with(TRANSACTION_STATUS, ISSUED).hasMatchingRows(3);
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
        myWork.filterTask().perform(paymentD);
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), TASK_NAME)).isAbsent();

        myWork.filterTask().perform(paymentB);
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), TASK_NAME)).isAbsent();

        myWork.filterTask().perform(paymentC);
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

    private String postPayment(Currency paymentAmount) {
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_PartialPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PaymentPaymentPaymentDetailsTabMetaData.GROSS_AMOUNT.getLabel()),
                        paymentAmount.toPlainString())
                .resolveLinks()
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.ALLOCATION_AMOUNT.getLabel()),
                        paymentAmount.toPlainString())
                .resolveLinks());
        String num = tableSummaryOfClaimPaymentsAndRecoveries.getRows().get(tableSummaryOfClaimPaymentsAndRecoveries.getRows().size() - 1).getCell(PAYMENT_NUMBER.getName()).getValue();
        LOGGER.info("Payment created: " + num);
        return num;
    }

    private void changePaymentStatusToIssue(String paymentNumber, String claimNumber) {
        mainApp().reopen(approvalUsername, approvalPassword);
        MainPage.QuickSearch.search(claimNumber);

        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), paymentNumber);
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), paymentNumber);
    }
}