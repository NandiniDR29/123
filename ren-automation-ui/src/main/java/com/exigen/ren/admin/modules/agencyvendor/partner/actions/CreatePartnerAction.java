/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.partner.actions;

import com.exigen.ren.admin.modules.agencyvendor.partner.tabs.PartnerTab;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;

public class CreatePartnerAction implements Action {
    private Workspace workspace = new Workspace.Builder()
            .registerTab(PartnerTab.class)
            .build();

    @Override
    public String getName() {
        return "Create Partner Action";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }
}
