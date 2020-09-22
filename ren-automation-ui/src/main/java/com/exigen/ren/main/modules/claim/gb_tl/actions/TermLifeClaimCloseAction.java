/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_tl.actions;

import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.claim.common.actions.ClaimCloseAction;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.ResolutionDetailsActionTab;

public class TermLifeClaimCloseAction extends ClaimCloseAction {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(ResolutionDetailsActionTab.class)
            .build();

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }
}
