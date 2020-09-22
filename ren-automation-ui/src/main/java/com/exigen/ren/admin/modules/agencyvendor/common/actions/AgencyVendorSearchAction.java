/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.common.actions;

import com.exigen.ren.admin.modules.agencyvendor.common.tabs.AgencyVendorSearchTab;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;

public class AgencyVendorSearchAction implements Action {
    private Workspace workspace = new Workspace.Builder()
            .registerTab(AgencyVendorSearchTab.class)
            .build();

    @Override
    public Action start() {
        return this;
    }

    @Override
    public String getName() {
        return "Agency Vendor Search Action";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action submit() {
        AgencyVendorSearchTab.buttonSearch.click();
        return this;
    }
}
