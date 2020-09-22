package com.exigen.ren.main.modules.mywork.actions;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.mywork.actiontabs.CompleteTaskDentalActionTab;
import com.exigen.ren.main.pages.summary.TaskDetailsSummaryPage;

public class CompleteTaskDentalAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(CompleteTaskDentalActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Complete Task (dental)";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        TaskDetailsSummaryPage.buttonComplete.click();
        return this;
    }

    @Override
    public Action submit() {
        return this;
    }

}
