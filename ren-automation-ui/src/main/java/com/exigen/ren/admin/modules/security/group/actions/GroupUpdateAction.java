/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.group.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.security.group.tabs.GroupTab;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.enums.AdminConstants;

public class GroupUpdateAction implements Action {
    private Workspace workspace = new Workspace.Builder()
            .registerTab(GroupTab.class)
            .build();

    @Override
    public String getName() {
        return "Update Group Action";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action submit() {
        GroupTab.buttonUpdate.click();
        return this;
    }

    public Action start(String groupName) {
        GroupTab.buttonSearch.click();
        GroupTab.tableGroups.findRow(AdminConstants.AdminGroup.GROUP_NAME, groupName).
                getCell(3).controls.links.get(AdminConstants.AdminGroup.CHANGE).click();
        return this;
    }

    public Action perform(TestData td, String groupName) {
        start(groupName);
        getWorkspace().fill(td);
        return submit();
    }
}
