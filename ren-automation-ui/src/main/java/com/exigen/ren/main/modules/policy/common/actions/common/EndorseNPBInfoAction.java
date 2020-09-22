package com.exigen.ren.main.modules.policy.common.actions.common;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.policy.common.tabs.common.StartEndorsementNPBInfoActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.CaseInstallationEndorseNPDINfoActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PlanDefinitionEndorseNPBInfoActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInfoEndorseNPBInfoActionTab;

public class EndorseNPBInfoAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(StartEndorsementNPBInfoActionTab.class)
            .registerTab(PolicyInfoEndorseNPBInfoActionTab.class)
            .registerTab(CaseInstallationEndorseNPDINfoActionTab.class)
            .registerTab(PlanDefinitionEndorseNPBInfoActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Endorse NPB Info";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }
}
