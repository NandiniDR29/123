package com.exigen.ren.main.modules.claim.common.actions;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.claim.common.tabs.ClaimSuspendActionTab;

import static com.exigen.ren.main.enums.ActionConstants.ClaimAction.SUSPEND_CLAIM;
import static com.exigen.ren.main.modules.claim.common.tabs.ClaimSuspendActionTab.buttonSuspendClaim;

public class ClaimSuspendAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(ClaimSuspendActionTab.class)
            .build();

    @Override
    public String getName() {
        return SUSPEND_CLAIM;
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action submit() {
        buttonSuspendClaim.click();
        return this;
    }
}
