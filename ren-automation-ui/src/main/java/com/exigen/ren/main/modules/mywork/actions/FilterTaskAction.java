/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.main.modules.mywork.actions;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.mywork.metadata.FilterTaskActionTabMetaData;
import com.exigen.ren.main.modules.mywork.tabs.FilterTaskActionTab;
import com.exigen.ren.main.pages.summary.MyWorkSummaryPage;

public class FilterTaskAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(FilterTaskActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Filter Task";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        MyWorkSummaryPage.showFilter();
        return this;
    }

    @Override
    public Action submit() {
        MyWorkSummaryPage.submitFilter();
        return this;
    }

    public Action perform(String referenceId) {
        start();
        getWorkspace().getTab(FilterTaskActionTab.class).getAssetList().getAsset(
                FilterTaskActionTabMetaData.REFERENCE_ID).setValue(referenceId);
        return submit();
    }

    public Action perform(String referenceId, String status, String taskName) {
        start();
        getWorkspace().getTab(FilterTaskActionTab.class).getAssetList().getAsset(FilterTaskActionTabMetaData.REFERENCE_ID).setValue(referenceId);
        getWorkspace().getTab(FilterTaskActionTab.class).getAssetList().getAsset(FilterTaskActionTabMetaData.STATUS).setValue(status);
        getWorkspace().getTab(FilterTaskActionTab.class).getAssetList().getAsset(FilterTaskActionTabMetaData.TASK_NAME).setValue(taskName);
        submit();
        MyWorkSummaryPage.openAllQueuesSection();
        return this;
    }
}
