/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.modules.billing.account.tabs.WaiveFeeActionTab;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;

public class WaiveFeeAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(WaiveFeeActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Waive Fee";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    //FIXME(vmarkouski): magic number
    @Override
    public Action start() {
        return start(1);
    }

    public Action start(int rowNumber) {
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(rowNumber).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(
                ActionConstants.BillingPaymentsAndOtherTransactionAction.WAIVE).click();
        return this;
    }

    public Action perform(TestData td, int rowNumber) {
        start(rowNumber);
        getWorkspace().fill(td);
        return submit();
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, List<String> policies) instead.");
    }

    @Override
    public Action submit() {
        Page.dialogConfirmation.confirm();
        return this;
    }

}
