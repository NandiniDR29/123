/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cem.groupsinformation;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.cem.common.pages.CemPage;
import com.exigen.ren.admin.modules.cem.groupsinformation.actions.GroupInformationCreateAction;
import com.exigen.ren.admin.modules.cem.groupsinformation.actions.GroupsInformationUpdateAction;
import com.exigen.ren.admin.modules.cem.groupsinformation.pages.GroupInformationPage;
import com.exigen.ren.admin.modules.cem.groupsinformation.tabs.GroupsInformationTab;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.enums.NavigationEnum.AdminAppMainTabs;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.CEMConstants;

public class GroupsInformation implements IGroupInformation {

    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/cem/cem/groupsinformation");

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(GroupInformationCreateAction.class).getWorkspace();
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultWorkspace().fill(td);
        GroupsInformationTab.buttonSave.click();
    }

    @Override
    public void initiate() {
        navigate();
        GroupInformationPage.buttonCreateNewGroup.click();
    }

    public void search(TestData td) {
        CemPage.search(td);
    }

    public void navigate() {
        NavigationPage.toMainTab(AdminAppMainTabs.CEM);
        NavigationPage.toLeftMenuTab(NavigationEnum.CEMLeftMenu.GROUPS_INFORMATION);
    }

    public GroupsInformationUpdateAction editGroupsInformation() {
        return AutomationContext.getAction(GroupsInformationUpdateAction.class);
    }

    public void delete(int rowNumber) {
        GroupInformationPage.tableGroupsInformation.getRow(rowNumber).getCell(CEMConstants.CEMGroupsInformationTable.ACTIONS).controls.buttons.get("Delete").click();
        Page.dialogConfirmation.confirm();
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
