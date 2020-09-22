/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.main.modules.mywork.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.mywork.tabs.UpdateTaskActionTab;
import com.exigen.ren.main.pages.summary.MyWorkSummaryPage;
import com.exigen.ren.main.pages.summary.TaskDetailsSummaryPage;

public class UpdateTaskAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(UpdateTaskActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Update Task";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
    }

    public Action start(int rowNumber) {
        MyWorkSummaryPage.openTaskDetails(rowNumber);
        TaskDetailsSummaryPage.buttonUpdate.click();
        return this;
    }

    @Override
    public Action submit() {
        UpdateTaskActionTab.buttonUpdate.click();
        TaskDetailsSummaryPage.buttonCancel.click();
        return this;
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported for this action. Use perform(int rowNumber, TestData td) instead.");
    }

    public Action perform(int rowNumber, TestData td) {
        start(rowNumber);
        getWorkspace().fill(td);
        return submit();
    }
}
