/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.role.actions;

import com.exigen.ren.admin.modules.security.role.pages.RolePage;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.enums.ActionConstants;

public class RoleUpdateAction implements Action {
    @Override
    public String getName() {
        return "Update Role Action";
    }

    @Override
    public Workspace getWorkspace() {
        return AutomationContext.getAction(RoleCreateAction.class).getWorkspace();
    }

    @Override
    public Action start() {
        RolePage.tableRolesSearchResult.getRow(1).getCell(3).controls.links.get(ActionConstants.CHANGE).click();
        return this;
    }

    @Override
    public Action submit() {
        GeneralRoleTab.buttonUpdate.click();
        return this;
    }
}
