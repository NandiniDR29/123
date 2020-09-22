/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.scheme.actions;

import com.exigen.ren.admin.modules.product.scheme.pages.SchemePage;
import com.exigen.ren.admin.modules.product.scheme.tabs.ProductsTab;
import com.exigen.ren.admin.modules.product.scheme.tabs.SchemeInfoTab;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;

public class SchemeDataGatherAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(SchemeInfoTab.class)
            .registerTab(ProductsTab.class)
            .build();

    @Override
    public String getName() {
        return "Data Gather";
    }

    public Action submit() {
        SchemePage.buttonSaveAndExit.click();
        return this;
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }
}
