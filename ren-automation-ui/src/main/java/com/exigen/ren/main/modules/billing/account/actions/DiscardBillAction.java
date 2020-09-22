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
import com.exigen.ren.main.modules.billing.account.tabs.DiscardBillActionTab;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;

public class DiscardBillAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(DiscardBillActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Discard Bill";
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
        if (BillingSummaryPage.tableBillsAndStatements.isPresent()) {
            BillingSummaryPage.tableBillsAndStatements.getRow(rowNumber).getCell(BillingConstants.BillingGBBillsAndStatmentsTable.ACTION).controls.links.get(
                    ActionConstants.BillingBillsAndStatementsAction.DISCARD).click();
        }
        return this;
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
