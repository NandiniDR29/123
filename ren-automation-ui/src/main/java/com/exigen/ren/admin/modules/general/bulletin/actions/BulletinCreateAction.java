/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.bulletin.actions;

import com.exigen.ren.admin.modules.general.bulletin.tabs.BulletinTab;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;

public class BulletinCreateAction implements Action {
    private Workspace workspace = new Workspace.Builder()
            .registerTab(BulletinTab.class)
            .build();

    @Override
    public String getName() {
        return "Create Bulletin Action";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }
}
