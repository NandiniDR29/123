package com.exigen.ren.main.modules.mywork.actions;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.mywork.actiontabs.AddAttachmentForTaskActionTab;
import com.exigen.ren.main.pages.summary.TaskDetailsSummaryPage;

public class AddAttachmentForTaskAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(AddAttachmentForTaskActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Add Attachment";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        TaskDetailsSummaryPage.buttonAddAttachment.click();
        return this;
    }

    @Override
    public Action submit() {
        return this;
    }

}
