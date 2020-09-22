/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.modules.billing.account.tabs.UnallocatePaymentActionTab;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;

public class UnallocatePaymentAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(UnallocatePaymentActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Unallocate";
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
            BillingSummaryPage.tablePaymentsOtherTransactions.getRow(rowNumber).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.buttons.get("Unallocate")
                    .click();
        }
        return submit();
    }

    public Action perform(TestData td, int rowNumber) {
        start(rowNumber);
        getWorkspace().fill(td);
        return submit();
    }

    @Override
    public Action submit() {
        Page.dialogConfirmation.confirm();
        return this;
    }

}
