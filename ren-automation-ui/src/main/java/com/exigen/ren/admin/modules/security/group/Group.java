/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.group;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.security.ISecurity;
import com.exigen.ren.admin.modules.security.group.actions.GroupCreateAction;
import com.exigen.ren.admin.modules.security.group.actions.GroupUpdateAction;
import com.exigen.ren.admin.modules.security.group.metadata.GroupMetaData;
import com.exigen.ren.admin.modules.security.group.tabs.GroupTab;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.AdminConstants;
import com.google.common.collect.ImmutableList;

public class Group implements ISecurity {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/security/group");

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(GroupCreateAction.class).getWorkspace();
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultWorkspace().fill(td);
        GroupTab.buttonSave.click();
    }

    @Override
    public void navigate() {
        initiate();
    }

    @Override
    public void initiate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.SECURITY);
        NavigationPage.toLeftMenuTab(NavigationEnum.SecurityLeftMenu.GROUP);
    }

    public void update(TestData td, String groupName) {
        navigate();
        update().perform(td, groupName);
    }

    @Override
    public GroupUpdateAction update() {
        return AutomationContext.getAction(GroupUpdateAction.class);
    }


    public void delete(int rowIndex, String groupName) {
        GroupTab.tableGroups.getRow(AdminConstants.AdminGroup.GROUP_NAME, groupName).
                getCell(rowIndex).controls.links.get(AdminConstants.AdminGroup.DELETE).click();
        Page.dialogConfirmation.confirm();
    }

    public void deleteUsersFromGroup(String groupName, ImmutableList<String> users) {
        this.navigate();
        this.update().start(groupName);
        AdvancedSelector groupUsers = this.update().getWorkspace().getTab(GroupTab.class).getAssetList().getAsset(GroupMetaData.GROUP_USERS);
        groupUsers.buttonOpenPopup.click();
        groupUsers.removeValue(users);
        groupUsers.buttonSave.click();
        this.update().submit();

    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
