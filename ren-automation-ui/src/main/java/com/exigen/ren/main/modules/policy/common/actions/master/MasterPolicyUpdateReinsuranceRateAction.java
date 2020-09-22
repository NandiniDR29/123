package com.exigen.ren.main.modules.policy.common.actions.master;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.policy.common.tabs.master.UpdateReinsuranceRateActionTab;

public class MasterPolicyUpdateReinsuranceRateAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(UpdateReinsuranceRateActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Update Reinsurance Rate";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action submit() {
        Tab.buttonOk.click();
        Page.dialogConfirmation.confirm();
        return this;
    }
}
