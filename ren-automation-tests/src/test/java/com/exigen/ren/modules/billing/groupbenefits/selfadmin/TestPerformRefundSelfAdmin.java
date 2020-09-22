/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.*;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.tabs.RefundActionTab;
import com.exigen.ren.main.modules.mywork.actions.CompleteTaskAction;
import com.exigen.ren.main.modules.mywork.tabs.CreateTaskActionTab;
import com.exigen.ren.main.pages.summary.MyWorkSummaryPage;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ActionConstants.BillingAction.REFUND;
import static com.exigen.ren.main.enums.ActionConstants.BillingPendingTransactionAction.*;
import static com.exigen.ren.main.modules.mywork.MyWorkContext.myWork;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPerformRefundSelfAdmin extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-30393", component = BILLING_GROUPBENEFITS)
    public void testPerformRefundSelfAdmin() {

        mainApp().open();
        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicySelfAdmin();

        navigateToBillingAccount(masterPolicyNumber.get());

        assertThat(BillingSummaryPage.comboBoxTakeAction).doesNotContainOption(REFUND);

        LOGGER.info("TEST: Accept payment");
        // In according with new feature REN-12088 if suspense <= 100, refund approved automatically
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash_Suspend_Remaining"), new Currency(1000).toString());
        assertThat(BillingSummaryPage.comboBoxTakeAction).containsOption(REFUND);

        LOGGER.info("TEST: Perform refund with Check payment method");
        billingAccount.refund().perform(billingAccount.getDefaultTestData("Refund", "TestData_Check"), new Currency(101).toString());
        verifyRefundTransaction();

        verifyTaskGenerated();

        LOGGER.info("TEST: Perform refund with EFT payment method");
        billingAccount.refund().perform(billingAccount.getDefaultTestData("Refund", "TestData_EFT"), new Currency(150).toString());
        verifyRefundTransaction();

        verifyTaskGenerated();

        LOGGER.info("TEST: Perform refund with Credit card payment method");
        billingAccount.refund().perform(billingAccount.getDefaultTestData("Refund", "TestData_CreditCard"), new Currency(180).toString());
        verifyRefundTransaction();

        verifyTaskGenerated();

        LOGGER.info("TEST: Perform refund change action");
        BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getSelf().getRow(BillingConstants.BillingPaymentsAndAdjustmentsTable.AMOUNT,
                new Currency(150).toString()).getCell(BillingConstants.BillingPaymentsAndAdjustmentsTable.ACTION).controls.links.get(ActionConstants.CHANGE).click();

        new RefundActionTab().fillTab(billingAccount.getDefaultTestData("Refund", "TestData_Refund_Misapplied"));
        RefundActionTab.buttonSave.click();

        verifyTaskGenerated();

        LOGGER.info("TEST: Perform refund inquiry action");
        BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getSelf().getRow(1)
                .getCell(BillingConstants.BillingBillsAndStatmentsTable.STATUS).controls.links.getFirst().click();
        RefundActionTab.buttonBack.click();

        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow("Description",
                String.format(BamConstants.INQUIRY_REFUND, new Currency(120).toString()))).isPresent();

        LOGGER.info("TEST: Approve refund transaction");
        approveTask(1);

        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, BillingConstants.PaymentsAndAdjustmentsStatusGB.APPROVED)
                .with(TableConstants.BillingPaymentsAndAdjustmentsGB.ACTION, "IssueVoid")
                .containsMatchingRow(3);

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.collapse();
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow("Description",
                String.format(BamConstants.REFUND_APPROVED, new Currency(101).toString()))).isPresent();

        LOGGER.info("TEST: Reject refund transaction");
        CreateTaskActionTab.buttonTasks.click();
        myWork.completeTask().perform(1, myWork.getDefaultTestData().getTestData(CompleteTaskAction.class.getSimpleName(), "TestData_Reject"));
        navigateToBillingAccount(masterPolicyNumber.get());

        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, BillingConstants.PaymentsAndAdjustmentsStatusGB.DISAPPROVED)
                .containsMatchingRow(2);

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.collapse();
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow("Description",
                String.format(BamConstants.REFUND_DISAPPROVED, new Currency(180).toString()))).isPresent();

        LOGGER.info("TEST: Issue refund transaction");
        performAction(3, ISSUE);

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.collapse();
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow("Description",
                String.format(BamConstants.REFUND_ISSUED, new Currency(101).toString()))).isPresent();

        BillingSummaryPage.buttonTasks.click();
        assertThat(MyWorkSummaryPage.tableTasks.getRow(2))
                .hasCellWithValue(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), MyWorkConstants.MyWorkTaskName.CLEAR_REFUND);
        MyWorkSummaryPage.buttonCancel.click();
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(3).getCell("Description"))
                .valueContains("Task Created Clear Refund, Decline or Stop");

        LOGGER.info("TEST: Generate new refund transaction, approve and void it");
        billingAccount.refund().perform(billingAccount.getDefaultTestData("Refund", "TestData_Check"), new Currency(110).toString());

        approveTask(3);
        performAction(1, VOID);

        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, BillingConstants.PaymentsAndAdjustmentsStatusGB.VOIDED)
                .containsMatchingRow(1);
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow("Description",
                String.format(BamConstants.REFUND_VOIDED, new Currency(110).toString()))).isPresent();
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.collapse();

        LOGGER.info("TEST: Clear issued refund transaction");
        performAction(4, CLEAR);
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow("Description",
                String.format(BamConstants.REFUND_CLEARED, new Currency(101).toString()))).isPresent();

        LOGGER.info("TEST: Generate new refund transaction, approve, issue and decline it");
        billingAccount.refund().perform(billingAccount.getDefaultTestData("Refund", "TestData_EFT"), new Currency(500).toString());
        approveTask(2);
        performAction(1, ISSUE);
        performAction(1, DECLINE);

        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, BillingConstants.PaymentsAndAdjustmentsStatusGB.DECLINED)
                .containsMatchingRow(1);
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow("Description",
                String.format(BamConstants.REFUND_DECLINED, new Currency(500).toString()))).isPresent();

        LOGGER.info("TEST: Generate new refund transaction, approve, issue and stop it");
        billingAccount.refund().perform(billingAccount.getDefaultTestData("Refund", "TestData_Check"), new Currency(200).toString());
        approveTask(2);
        performAction(1, ISSUE);
        performAction(1, STOP);

        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, BillingConstants.PaymentsAndAdjustmentsStatusGB.STOP_REQUESTED)
                .containsMatchingRow(1);
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow("Description",
                String.format(BamConstants.REFUND_STOP_REQUESTED, new Currency(200).toString()))).isPresent();
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.collapse();

        LOGGER.info("TEST: Confirm stopped refund transaction");
        performAction(1, CONFIRM);

        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, BillingConstants.PaymentsAndAdjustmentsStatusGB.STOPPED)
                .containsMatchingRow(1);
        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions)
                .with(TableConstants.BillingPaymentsAndTransactionsGB.TYPE, BillingConstants.PaymentsAndOtherTransactionTypeGB.REFUND_DECLINED)
                .containsMatchingRow(1);
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow("Description",
                String.format(BamConstants.REFUND_STOP_CHECK_CONFIRMED, new Currency(200).toString()))).isPresent();

        LOGGER.info("TEST: Generate new refund transaction with check payment method");
        billingAccount.refund().perform(billingAccount.getDefaultTestData("Refund", "TestData_Check"), new Currency(250).toString());
        approveTask(2);
        performAction(1, ISSUE);
        performAction(1, STOP);
        performAction(1, CLEAR);

        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, BillingConstants.PaymentsAndAdjustmentsStatusGB.CLEARED)
                .containsMatchingRow(1);
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow("Description",
                String.format(BamConstants.REFUND_CLEARED, new Currency(250).toString()))).isPresent();
    }

    private void performAction(int rowNumber, String action) {
        BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getSelf().getRow(rowNumber)
                .getCell(BillingConstants.BillingPaymentsAndAdjustmentsTable.ACTION).controls.links.get(action).click();
        Page.dialogConfirmation.confirm();
    }

    private void verifyTaskGenerated() {
        BillingSummaryPage.buttonTasks.click();
        assertThat(MyWorkSummaryPage.tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), MyWorkConstants.MyWorkTaskName.REFUND_APPROVAL)).isPresent();
        MyWorkSummaryPage.buttonCancel.click();
    }

    private void verifyRefundTransaction() {
        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                .with(TableConstants.BillingPaymentsAndAdjustmentsGB.SUBTYPE, BillingConstants.PaymentsAndAdjustmentsSubtypeGB.MANUAL_REFUND)
                .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, BillingConstants.PaymentsAndAdjustmentsStatusGB.PENDING)
                .containsMatchingRow(1);
    }

    private void approveTask(int index) {
        CreateTaskActionTab.buttonTasks.click();
        myWork.completeTask().perform(index, myWork.getDefaultTestData().getTestData(CompleteTaskAction.class.getSimpleName(), "TestData_Approve"));
        navigateToBillingAccount(masterPolicyNumber.get());
    }
}
