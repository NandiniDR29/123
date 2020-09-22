/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;

public class RemoveRelationshipContactAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(RelationshipTab.class)
            .build();

    @Override
    public String getName() {
        return "Remove Relationship Contact";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        CustomerSummaryPage.buttonEditRelationship.click();
        CustomerSummaryPage.linkRemoveThisRelationship.click();
        Page.dialogConfirmation.confirm();
        Page.dialogConfirmation.buttonYes.click();
        return this;
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
        RelationshipTab.buttonDone.click();
        return this;
    }
}
