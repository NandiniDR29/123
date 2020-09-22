/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.fulladmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.MyWorkConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.mywork.MyWorkContext;
import com.exigen.ren.main.modules.mywork.actions.CompleteTaskAction;
import com.exigen.ren.main.modules.mywork.tabs.CreateTaskActionTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.MyWorkSummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION;
import static com.exigen.ren.common.enums.ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.STATUS;
import static com.exigen.ren.main.enums.ActionConstants.BillingAction.REFUND;
import static com.exigen.ren.main.enums.BamConstants.*;
import static com.exigen.ren.main.enums.BillingConstants.PaymentsAndAdjustmentsStatusGB.*;
import static com.exigen.ren.main.enums.BillingConstants.PaymentsAndAdjustmentsSubtypeGB.MANUAL_REFUND;
import static com.exigen.ren.main.enums.MyWorkConstants.MyWorkTaskName.REFUND_APPROVAL;
import static com.exigen.ren.main.enums.MyWorkConstants.MyWorkTaskName.REFUND_ISSUE;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestBillingRefundApprovalRules extends GroupBenefitsBillingBaseTest implements BillingAccountContext, GroupVisionMasterPolicyContext, MyWorkContext {

    private static String taskRefundID;
    private static String taskRefundApprovalID;
    private static String taskRefundApprovalID2;
    private static String taskRefundApprovalID3;
    private static String taskRefundApprovalID4;
    private static String taskRefundApprovalID5;
    private static String taskRefundApprovalID6;


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-30765"}, component = BILLING_GROUPBENEFITS)
    public void testBillingRefundApprovalRules() {

        mainApp().open();
        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicyFullAdmin();

        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();
        Currency suspenseAmount =  new Currency(2202);
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("SuspendePayment", "TestData_Cash_Suspense"), suspenseAmount.toString());
        assertSoftly(softly -> {
            LOGGER.info("---=={Step 1}==---");
            Currency refundAmount = new Currency(100);
            billingAccount.refund().perform(billingAccount.getDefaultTestData(REFUND, "TestData_Check"), refundAmount.toString());
            softly.assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB).with(TableConstants.BillingPaymentsAndAdjustmentsGB.TYPE, REFUND)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, APPROVED).isPresent();
            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions)
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.TYPE, BillingConstants.PaymentsAndOtherTransactionTypeGB.REFUND_APPROVED)
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.AMOUNT, refundAmount.toString()).hasMatchingRows(1);
            BillingAccountsListPage.verifyBamActivities(String.format(REFUND_INITIATED, refundAmount.toString()), FINISHED);
            BillingAccountsListPage.verifyBamActivities(String.format(REFUND_APPROVED_AUTOMATICALLY, refundAmount.toString()), FINISHED);
            CreateTaskActionTab.buttonTasks.click();
            String taskID = MyWorkSummaryPage.tableTasks.getRow(1).getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID.getName()).getValue();
            navigateToBillingAccount(masterPolicyNumber.get());
            softly.assertThat(BillingSummaryPage.activitiesAndUserNotes.getRows().stream().filter(row -> row.getCell(DESCRIPTION).getValue()
                    .equals(String.format(REFUND_CREATED_TASK, taskID))).findFirst().orElseThrow(() -> new IstfException("Message in BAM not found")).getCell(STATUS)).hasValue(FINISHED);

            LOGGER.info("---=={Step 1.7}==---");
            softly.assertThat(BillingSummaryPage.tableBillingGeneralInformation)
                    .with(TableConstants.BillingGeneralInformationGB.SUSPENSE_AMOUNT, suspenseAmount.subtract(refundAmount))
                    .containsMatchingRow(1);

            LOGGER.info("---=={Step 1.8}==---");
            BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.ACTION.getName())
                    .controls.links.get(ActionConstants.BillingPendingTransactionAction.ISSUE).click();
            Page.dialogConfirmation.confirm();

            softly.assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, BillingConstants.PaymentsAndAdjustmentsTypeGB.ISSUED).isPresent();
            CreateTaskActionTab.buttonTasks.click();
            taskRefundID = MyWorkSummaryPage.tableTasks.getRow(1).getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID.getName()).getValue();
            navigateToBillingAccount(masterPolicyNumber.get());

            softly.assertThat(BillingSummaryPage.activitiesAndUserNotes.getRows().stream().filter(row -> row.getCell(DESCRIPTION).getValue()
                    .equals(String.format(REFUND_TASK_CREATED, taskRefundID))).findFirst().orElseThrow(() -> new IstfException("Message in BAM not found")).getCell(STATUS)).hasValue(FINISHED);
            softly.assertThat(BillingSummaryPage.activitiesAndUserNotes.getRows().stream().filter(row -> row.getCell(DESCRIPTION).getValue()
                    .equals(String.format(REFUND_TASK_COMPLETE_ISSUE, taskID))).findFirst().orElseThrow(() -> new IstfException("Message in BAM not found")).getCell(STATUS)).hasValue(FINISHED);
            BillingAccountsListPage.verifyBamActivities(String.format(REFUND_ISSUED, refundAmount.toString()), FINISHED);

            LOGGER.info("---=={Step 2}==---");
            Currency refundAmount2 = new Currency(101);
            billingAccount.refund().perform(billingAccount.getDefaultTestData(REFUND, "TestData_Check"), refundAmount2.toString());
            softly.assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.SUBTYPE, MANUAL_REFUND)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, PENDING)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.AMOUNT, refundAmount2.toString()).hasMatchingRows(1);

            LOGGER.info("---=={Step 3}==---");
            CreateTaskActionTab.buttonTasks.click();
            taskRefundApprovalID = MyWorkSummaryPage.tableTasks.getRow(ImmutableMap.of(
                    MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), REFUND_APPROVAL,
                    MyWorkConstants.MyWorkTasksTable.QUEUE.getName(), "RefundApprovalLevel1")).getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID.getName()).getValue();
            navigateToBillingAccount(masterPolicyNumber.get());
            BillingAccountsListPage.verifyBamActivities(String.format(REFUND_INITIATED, refundAmount2.toString()), FINISHED);
            softly.assertThat(BillingSummaryPage.activitiesAndUserNotes.getRows().stream().filter(row -> row.getCell(DESCRIPTION).getValue()
                    .equals(String.format(REFUND_TASK_APPROVAL, taskRefundApprovalID))).findFirst().orElseThrow(() -> new IstfException("Message in BAM not found")).getCell(STATUS)).hasValue(FINISHED);

        LOGGER.info("---=={Step 4}==---");
            CreateTaskActionTab.buttonTasks.click();
            myWork.completeTask().perform(ImmutableMap.of(
                    MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), REFUND_APPROVAL, MyWorkConstants.MyWorkTasksTable.QUEUE.getName(),"RefundApprovalLevel1"),
                    myWork.getDefaultTestData().getTestData(CompleteTaskAction.class.getSimpleName(), "TestData_Reject"));
            navigateToBillingAccount(masterPolicyNumber.get());

            softly.assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.SUBTYPE, MANUAL_REFUND)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, DISAPPROVED)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.AMOUNT, refundAmount2.toString()).hasMatchingRows(1);

            BillingAccountsListPage.verifyBamActivities(String.format(REFUND_DISAPPROVED, refundAmount2.toString()), FINISHED);
            softly.assertThat(BillingSummaryPage.activitiesAndUserNotes.getRows().stream().filter(row -> row.getCell(DESCRIPTION).getValue()
                    .equals(String.format(REFUND_TASK_COMPLETE_APPROVAL, taskRefundApprovalID))).findFirst().orElseThrow(() -> new IstfException("Message in BAM not found")).getCell(STATUS)).hasValue(FINISHED);
        });

        assertSoftly(softly -> {
            LOGGER.info("---=={Step 5}==---");
            Currency refundAmount3 = new Currency(1000);
            billingAccount.refund().perform(billingAccount.getDefaultTestData(REFUND, "TestData_Check"), refundAmount3.toString());
            softly.assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.SUBTYPE, MANUAL_REFUND)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, PENDING)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.AMOUNT, refundAmount3.toString()).hasMatchingRows(1);

            LOGGER.info("---=={Step 6}==---");
            CreateTaskActionTab.buttonTasks.click();
            taskRefundApprovalID2 = MyWorkSummaryPage.tableTasks.getRow(ImmutableMap.of(
                    MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), REFUND_APPROVAL,
                    MyWorkConstants.MyWorkTasksTable.QUEUE.getName(), "RefundApprovalLevel1")).getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID.getName()).getValue();
            navigateToBillingAccount(masterPolicyNumber.get());
            BillingAccountsListPage.verifyBamActivities(String.format(REFUND_INITIATED, refundAmount3.toString()), FINISHED);
            softly.assertThat(BillingSummaryPage.activitiesAndUserNotes.getRows().stream().filter(row -> row.getCell(DESCRIPTION).getValue()
                    .equals(String.format(REFUND_TASK_APPROVAL, taskRefundApprovalID2))).findFirst().orElseThrow(() -> new IstfException("Message in BAM not found")).getCell(STATUS)).hasValue(FINISHED);

            LOGGER.info("---=={Step 7}==---");
            CreateTaskActionTab.buttonTasks.click();
            myWork.completeTask().perform(ImmutableMap.of(
                    MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), REFUND_APPROVAL, MyWorkConstants.MyWorkTasksTable.QUEUE.getName(),"RefundApprovalLevel1"),
                    myWork.getDefaultTestData().getTestData(CompleteTaskAction.class.getSimpleName(), "TestData_Approve"));

            taskRefundApprovalID3 = MyWorkSummaryPage.tableTasks.getRow(ImmutableMap.of(
                    MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), REFUND_ISSUE)).getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID.getName()).getValue();
            navigateToBillingAccount(masterPolicyNumber.get());

            softly.assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.SUBTYPE, MANUAL_REFUND)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, APPROVED)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.AMOUNT, refundAmount3.toString()).hasMatchingRows(1);

            LOGGER.info("---=={Step 7.5}==---");
            BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(TableConstants.BillingPaymentsAndAdjustmentsGB.AMOUNT.getName(), refundAmount3.toString())
                    .getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.ACTION.getName())
                    .controls.links.get(ActionConstants.BillingPendingTransactionAction.ISSUE).click();
            Page.dialogConfirmation.confirm();

            CreateTaskActionTab.buttonTasks.click();
            taskRefundApprovalID4 = MyWorkSummaryPage.tableTasks.getRow(2).getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID.getName()).getValue();
            navigateToBillingAccount(masterPolicyNumber.get());

            softly.assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.SUBTYPE, MANUAL_REFUND)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, ISSUED)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.AMOUNT, refundAmount3.toString()).hasMatchingRows(1);

            LOGGER.info("---=={Step 7.6}==---");
            BillingAccountsListPage.verifyBamActivities(String.format(REFUND_APPROVED, refundAmount3.toString()), FINISHED);
            BillingAccountsListPage.verifyBamActivities(String.format(REFUND_ISSUED, refundAmount3.toString()), FINISHED);

            softly.assertThat(BillingSummaryPage.activitiesAndUserNotes.getRows().stream().filter(row -> row.getCell(DESCRIPTION).getValue()
                    .equals(String.format(REFUND_TASK_COMPLETE_APPROVAL, taskRefundApprovalID2))).findFirst().orElseThrow(() -> new IstfException("Message in BAM not found")).getCell(STATUS)).hasValue(FINISHED);

            softly.assertThat(BillingSummaryPage.activitiesAndUserNotes.getRows().stream().filter(row -> row.getCell(DESCRIPTION).getValue()
                    .equals(String.format(REFUND_TASK_COMPLETE_ISSUE, taskRefundApprovalID3))).findFirst().orElseThrow(() -> new IstfException("Message in BAM not found")).getCell(STATUS)).hasValue(FINISHED);

            softly.assertThat(BillingSummaryPage.activitiesAndUserNotes.getRows().stream().filter(row -> row.getCell(DESCRIPTION).getValue()
                    .equals(String.format(REFUND_CREATED_TASK, taskRefundApprovalID3))).findFirst().orElseThrow(() -> new IstfException("Message in BAM not found")).getCell(STATUS)).hasValue(FINISHED);

            softly.assertThat(BillingSummaryPage.activitiesAndUserNotes.getRows().stream().filter(row -> row.getCell(DESCRIPTION).getValue()
                    .equals(String.format(REFUND_TASK_CREATED, taskRefundApprovalID4))).findFirst().orElseThrow(() -> new IstfException("Message in BAM not found")).getCell(STATUS)).hasValue(FINISHED);
        });

        assertSoftly(softly -> {
            LOGGER.info("---=={Step 8}==---");
            Currency refundAmount4 = new Currency(1001);
            billingAccount.refund().perform(billingAccount.getDefaultTestData(REFUND, "TestData_Check"), refundAmount4.toString());
            softly.assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.SUBTYPE, MANUAL_REFUND)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, PENDING)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.AMOUNT, refundAmount4.toString()).hasMatchingRows(1);

            LOGGER.info("---=={Step 9}==---");
            BillingAccountsListPage.verifyBamActivities(String.format(REFUND_INITIATED, refundAmount4.toString()), FINISHED);

            CreateTaskActionTab.buttonTasks.click();
            taskRefundApprovalID5 = MyWorkSummaryPage.tableTasks.getRow(ImmutableMap.of(
                    MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), REFUND_APPROVAL,
                    MyWorkConstants.MyWorkTasksTable.QUEUE.getName(), "RefundApprovalLevel2")).getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID.getName()).getValue();
            navigateToBillingAccount(masterPolicyNumber.get());

            BillingAccountsListPage.verifyBamActivities(String.format(REFUND_INITIATED, refundAmount4.toString()), FINISHED);
            softly.assertThat(BillingSummaryPage.activitiesAndUserNotes.getRows().stream().filter(row -> row.getCell(DESCRIPTION).getValue()
                    .equals(String.format(REFUND_TASK_APPROVAL, taskRefundApprovalID5))).findFirst().orElseThrow(() -> new IstfException("Message in BAM not found")).getCell(STATUS)).hasValue(FINISHED);

            LOGGER.info("---=={Step 10}==---");
            CreateTaskActionTab.buttonTasks.click();
            myWork.completeTask().perform(ImmutableMap.of(
                    MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), REFUND_APPROVAL, MyWorkConstants.MyWorkTasksTable.QUEUE.getName(),"RefundApprovalLevel2"),
                    myWork.getDefaultTestData().getTestData(CompleteTaskAction.class.getSimpleName(), "TestData_Approve"));

            taskRefundApprovalID6 = MyWorkSummaryPage.tableTasks.getRow(ImmutableMap.of(
                    MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), REFUND_ISSUE)).getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID.getName()).getValue();
            navigateToBillingAccount(masterPolicyNumber.get());

            softly.assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.SUBTYPE, MANUAL_REFUND)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, APPROVED)
                    .with(TableConstants.BillingPaymentsAndAdjustmentsGB.AMOUNT, refundAmount4.toString()).hasMatchingRows(1);

            LOGGER.info("---=={Step 10.5-10.6}==---");
            BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(TableConstants.BillingPaymentsAndAdjustmentsGB.AMOUNT.getName(), refundAmount4.toString())
                    .getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.ACTION.getName())
                    .controls.links.get(ActionConstants.BillingPendingTransactionAction.VOID).click();
            Page.dialogConfirmation.confirm();
            navigateToBillingAccount(masterPolicyNumber.get());
            BillingAccountsListPage.verifyBamActivities(String.format(REFUND_APPROVED, refundAmount4.toString()), FINISHED);
            BillingAccountsListPage.verifyBamActivities(String.format(REFUND_VOIDED, refundAmount4.toString()), FINISHED);
            softly.assertThat(BillingSummaryPage.activitiesAndUserNotes.getRows().stream().filter(row -> row.getCell(DESCRIPTION).getValue()
                    .equals(String.format(REFUND_TASK_COMPLETE_APPROVAL, taskRefundApprovalID5))).findFirst().orElseThrow(() -> new IstfException("Message in BAM not found")).getCell(STATUS)).hasValue(FINISHED);

            softly.assertThat(BillingSummaryPage.activitiesAndUserNotes.getRows().stream().filter(row -> row.getCell(DESCRIPTION).getValue()
                    .equals(String.format(REFUND_CREATED_TASK, taskRefundApprovalID6))).findFirst().orElseThrow(() -> new IstfException("Message in BAM not found")).getCell(STATUS)).hasValue(FINISHED);

            softly.assertThat(BillingSummaryPage.activitiesAndUserNotes.getRows().stream().filter(row -> row.getCell(DESCRIPTION).getValue()
                    .equals(String.format(REFUND_TASK_COMPLETE_ISSUE, taskRefundApprovalID6))).findFirst().orElseThrow(() -> new IstfException("Message in BAM not found")).getCell(STATUS)).hasValue(FINISHED);
        });
    }
}