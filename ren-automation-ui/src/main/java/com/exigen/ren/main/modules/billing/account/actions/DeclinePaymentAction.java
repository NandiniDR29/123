/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.actions;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.tabs.DeclinePaymentActionTab;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.By;

import static com.exigen.ren.main.enums.BillingConstants.PaymentsAndOtherTransactionTypeGB.PAYMENT;

public class DeclinePaymentAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(DeclinePaymentActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Decline Payment";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        return start(1);
    }

    public Action start(int rowNumber) {
        if (BillingSummaryPage.tablePaymentsOtherTransactions.isPresent()) {
            BillingSummaryPage.tablePaymentsOtherTransactions.getRows(ImmutableMap.of(TableConstants.BillingPaymentsAndTransactionsGB.TYPE.getName(), PAYMENT)).get(rowNumber-1)
                    .getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.buttons.get( ActionConstants.BillingPaymentsAndOtherTransactionAction.DECLINE).click();
        } else {
            BillingSummaryPage.tablePaymentsOtherTransactions.getRow(rowNumber).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(
                    ActionConstants.BillingPaymentsAndOtherTransactionAction.DECLINE).click();
        }
        return this;
    }

    public Action perform(TestData td, int rowNumber) {
        start(rowNumber);
        getWorkspace().fill(td);
        return submit();
    }

    //Decline
    public Action perform(TestData td, int rowNumber, String referenceNumber) {
        start(rowNumber);
        Table suspenseForDecline = new Table(By.xpath("//div[@id = 'billingDetailedForm:suspenseItemList']//table"));
        if (suspenseForDecline.isPresent()) {
            suspenseForDecline.getRowContains(BillingConstants.BillingSuspenseForDeclineTable.REFERENCE_NUMBER, referenceNumber).getCell(BillingConstants.BillingSuspenseForDeclineTable.ACTION).controls.links
                    .get("Decline").click();
        }
        getWorkspace().fill(td);
        return submit();
    }

    //Click Cancel button in Decline Action PopUp
    public void perform(int rowNumber) {
        start(rowNumber);
        Table suspenseForDecline = new Table(By.xpath("//div[@id = 'billingDetailedForm:suspenseItemList']//table"));
        if (suspenseForDecline.isPresent()) {
            new Button(By.id("billingDetailedForm:multipleSuspenseDeclineCancelBtn")).click();
        }
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
    }

    @Override
    public Action submit() {
        if (DeclinePaymentActionTab.buttonOk.isPresent()) {
            DeclinePaymentActionTab.buttonOk.click();
        } else {
            new Button(By.id("declinePaymentReasonForm:okBtnPopup_footer")).click();
        }

        if (Page.dialogConfirmation.isPresent()) {
            Page.dialogConfirmation.confirm();
        }
        return this;
    }

}
