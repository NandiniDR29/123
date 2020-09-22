package com.exigen.ren.main.modules.billing.account.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.billing.account.tabs.ConsolidatedStatementDetailsActionTab;
import com.exigen.ren.main.modules.billing.account.tabs.ManageConsolidatedStatementsActionTab;

public class ManageConsolidatedStatementsAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(ManageConsolidatedStatementsActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Manage Consolidated Statements";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action submit() {
        Tab.buttonBack.click();
        return this;
    }

    public Action perform(TestData td) {
        start();
        getWorkspace().fill(td);
        return submit();
    }
}