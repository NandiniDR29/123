/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cms.efoldermanagment.efoldertree;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.cms.efoldermanagment.IEfolderManagement;
import com.exigen.ren.admin.modules.cms.efoldermanagment.efoldertree.actions.EfolderTreeAddSubfolderAction;
import com.exigen.ren.admin.modules.cms.efoldermanagment.pages.EfolderManagmentPage;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.EfolderManagmentConstants;
import org.openqa.selenium.By;

public class EfolderTree implements IEfolderManagement {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/cms/efoldermanagment/efoldertree");

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(EfolderTreeAddSubfolderAction.class).getWorkspace();
    }

    @Override
    public void create(TestData td) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void initiate() {
        throw new UnsupportedOperationException("Not supported");
    }

    public void initiate(String code) {
        EfolderManagmentPage.tableDocumentTypeEntity.getRow(EfolderManagmentConstants.ProductSupportTable.CODE, code).getCell(ActionConstants.ACTIONS).controls.links.getFirst().click();
        EfolderManagmentPage.buttonEfolderTreeEntityRootFolder.click();
        new Link(By.xpath("//span[.='Invoices and Bills' and @class='text8pt']")).click();
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.CMS);
        NavigationPage.toLeftMenuTab(NavigationEnum.CMSLeftMenu.EFOLDER_MGMT);
    }

    public void addSubfolder(TestData testData, String code) {
        navigate();
        initiate(code);
        EfolderManagmentPage.buttonAddSubfolder.click();
        getDefaultWorkspace().fill(testData);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
