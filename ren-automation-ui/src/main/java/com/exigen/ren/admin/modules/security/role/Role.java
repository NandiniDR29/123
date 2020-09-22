/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.role;

import com.exigen.istf.data.TestData;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.admin.modules.security.ISecurity;
import com.exigen.ren.admin.modules.security.role.actions.RoleCreateAction;
import com.exigen.ren.admin.modules.security.role.actions.RoleUpdateAction;
import com.exigen.ren.admin.modules.security.role.pages.RolePage;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.enums.NavigationEnum.AdminAppMainTabs;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.AdminConstants;

public abstract class Role implements ISecurity {

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(RoleCreateAction.class).getWorkspace();
    }


    public void initiate(RoleType channelType) {
        navigate();
        RolePage.assetListSearchForm.getAsset(RolePage.RoleSearch.BUSINESS_DOMAIN).setValue(channelType.get());
        GeneralRoleTab.buttonAddNewRole.click();
    }

    @Override
    public RoleUpdateAction update() {
        return AutomationContext.getAction(RoleUpdateAction.class);
    }

    public void search(TestData td) {
        RolePage.search(td);
    }


    @Override
    public void navigate() {
        NavigationPage.toMainTab(AdminAppMainTabs.SECURITY);
        NavigationPage.toLeftMenuTab(NavigationEnum.SecurityLeftMenu.ROLE);
    }

    @Override
    public void initiate() {
        throw new UnsupportedOperationException("Role not supported initiate() method");
    }


    @Override
    public void create(TestData td) {
        initiate(RoleType.valueOf(td.getValue("Channel")));
        getDefaultWorkspace().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.ROLE);
        GeneralRoleTab.buttonSave.click();
        LOGGER.info("Created Role " + entity);
    }

    public void update(TestData td) {
        update().perform(td);
    }

    public void delete(TestData td) {
        search(td);
        RolePage.tableRolesSearchResult.getRow(1).getCell(AdminConstants.AdminRolesSearchResultTable.ACTION).controls.links.get(ActionConstants.DELETE).click();
        RolePage.dialogRemoveRoleConfirmation.buttonYes.click();
    }

    public void change(TestData td) {
        search(td);
        RolePage.tableRolesSearchResult.getRow(1).getCell(AdminConstants.AdminRolesSearchResultTable.ACTION).controls.links.get(ActionConstants.CHANGE).click();
    }

    public void inquiry(TestData td) {
        search(td);
        RolePage.tableRolesSearchResult.getRow(1).getCell(AdminConstants.AdminRolesSearchResultTable.ROLE_NAME).controls.links.getFirst().click();
    }

    @Override
    public TestData defaultTestData() {
        throw new UnsupportedOperationException("default Test Data absent for entity Role. Please, specify role type");
    }
}
