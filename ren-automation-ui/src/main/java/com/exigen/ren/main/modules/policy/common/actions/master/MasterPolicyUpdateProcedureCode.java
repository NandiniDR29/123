package com.exigen.ren.main.modules.policy.common.actions.master;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.policy.common.tabs.master.UpdateProcedureCodeActionTab;

public class MasterPolicyUpdateProcedureCode implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(UpdateProcedureCodeActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Update Procedure Code";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action submit() {
        Tab.buttonOk.click();
        Tab.buttonOk.click();
        return this;
    }
}
