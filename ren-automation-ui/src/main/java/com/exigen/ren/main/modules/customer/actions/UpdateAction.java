/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.actions;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.customer.tabs.*;

import static com.exigen.ren.common.Tab.buttonDone;

public class UpdateAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(GeneralTab.class)
            .registerTab(BusinessEntityTab.class)
            .registerTab(DivisionsTab.class)
            .registerTab(RelationshipTab.class)
            .registerTab(EmployeeInfoTab.class)
            .registerTab(ProviderTab.class)
            .build();

    @Override
    public String getName() {
        return "Update";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action submit() {
        if(buttonDone.isPresent()) {
            buttonDone.click();
        }
        return this;
    }
}
