/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.billing.account.metadata.RefundActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.RefundActionTab;

public class RefundAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(RefundActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Refund";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    public Action perform(TestData td, String amount) {
        td.adjust(TestData.makeKeyPath(RefundActionTab.class.getSimpleName(), RefundActionTabMetaData.AMOUNT.getLabel()), amount);
        start();
        getWorkspace().fill(td);
        return submit();
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, String amount) instead.");
    }

    @Override
    public Action submit() {
        RefundActionTab.buttonOk.click();
        return this;
    }

}
