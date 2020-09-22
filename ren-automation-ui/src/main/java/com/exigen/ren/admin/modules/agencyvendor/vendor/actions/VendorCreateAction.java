/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.vendor.actions;

import com.exigen.ren.admin.modules.agencyvendor.vendor.tabs.ContactInfoTab;
import com.exigen.ren.admin.modules.agencyvendor.vendor.tabs.VendorInfoTab;
import com.exigen.ren.admin.modules.agencyvendor.vendor.tabs.VendorServiceTab;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;

public class VendorCreateAction implements Action {
    private Workspace workspace = new Workspace.Builder()
            .registerTab(VendorServiceTab.class)
            .registerTab(VendorInfoTab.class)
            .registerTab(ContactInfoTab.class)
            .build();

    @Override
    public String getName() {
        return "Create Vendor Action";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }
}
