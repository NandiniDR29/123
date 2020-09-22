/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.main.modules.mywork.actions;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.mywork.tabs.UpdateMyProfileActionTab;
import com.exigen.ren.main.pages.summary.MyWorkSummaryPage;

public class UpdateMyProfileAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(UpdateMyProfileActionTab.class)
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
        MyWorkSummaryPage.buttonMyProfile.click();
        return this;
    }

    @Override
    public Action submit() {
        UpdateMyProfileActionTab.buttonSave.click();
        return this;
    }
}
