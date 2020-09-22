/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.tabs.ReverseWriteOffActionTab;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;

public class ReverseWriteOffAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(ReverseWriteOffActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Reverse Write-Off";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.ACTION.getName())
                .controls.links.get(ActionConstants.BillingPendingTransactionAction.REVERSE).click();
        return this;
    }

    @Override
    public Action submit() {
        ReverseWriteOffActionTab.buttonOk.click();
        return this;
    }

    @Override
    public Action perform(TestData td) {
        start();
        getWorkspace().fill(td);
        return submit();
    }

}
