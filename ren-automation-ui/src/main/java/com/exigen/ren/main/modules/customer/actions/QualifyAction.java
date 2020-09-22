/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.customer.tabs.QualifyActionTab;

public class QualifyAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(QualifyActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Qualify";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    public Action perform() {
        start();
        return submit();
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform() instead.");
    }

    @Override
    public Action submit() {
        Page.dialogConfirmation.confirm();
        return this;
    }
}
