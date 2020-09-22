/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.bulletin;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.general.IGeneral;
import com.exigen.ren.admin.modules.general.bulletin.actions.BulletinCreateAction;
import com.exigen.ren.admin.modules.general.bulletin.actions.BulletinUpdateAction;
import com.exigen.ren.admin.modules.general.bulletin.pages.BulletinPage;
import com.exigen.ren.admin.modules.general.bulletin.tabs.BulletinTab;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.BulletinConstants;

public class Bulletin implements IGeneral {

    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/general/bulletin");

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(BulletinCreateAction.class).getWorkspace();
    }

    @Override
    public void initiate() {
        navigate();
        BulletinPage.buttonAddBulletin.click();
    }

    @Override
    public void create(TestData td) {
        throw new UnsupportedOperationException("Method is not supported, use create(TestData td, boolean isSubmeted) instaead");
    }

    public void search(TestData td) {
        navigate();
        BulletinPage.search(td);
    }

    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.GENERAL);
        NavigationPage.toLeftMenuTab(NavigationEnum.GeneralTabLeftMenu.BULLETIN);
    }

    public void create(TestData td, boolean isSubmeted) {
        initiate();
        getDefaultWorkspace().fill(td);
        submit(isSubmeted);
    }

    public void delete(String bulletinName) {
        BulletinPage.tableSearchResults.getRow(BulletinConstants.BulletinDeailsTable.BULLETIN_NAME, bulletinName).getCell(BulletinPage.tableSearchResults.getColumnsCount()).controls.links.get(ActionConstants.DELETE).click();
        Page.dialogConfirmation.confirm();
    }

    public void expire(String bulletinName) {
        BulletinPage.tableSearchResults.getRow(BulletinConstants.BulletinDeailsTable.BULLETIN_NAME, bulletinName).getCell(1).controls.checkBoxes.getFirst().setValue(true);
        BulletinPage.buttonExpireBulletin.click();
        Page.dialogConfirmation.confirm();
    }


    public BulletinUpdateAction update() {
        return AutomationContext.getAction(BulletinUpdateAction.class);
    }

    /**
     * If 'isSubmeted' is true, clicks button 'Submit', else clicks button 'Save'
     */
    private void submit(boolean isSubmeted) {
        if (isSubmeted) {
            BulletinTab.buttonSubmit.click();
        } else {
            BulletinTab.buttonSave.click();
        }
        Page.dialogConfirmation.confirm();
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
