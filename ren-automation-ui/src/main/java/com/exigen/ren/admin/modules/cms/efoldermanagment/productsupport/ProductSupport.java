/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cms.efoldermanagment.productsupport;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.cms.efoldermanagment.IEfolderManagement;
import com.exigen.ren.admin.modules.cms.efoldermanagment.pages.EfolderManagmentPage;
import com.exigen.ren.admin.modules.cms.efoldermanagment.productsupport.actions.ProductSupportUploadAction;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;

public class ProductSupport implements IEfolderManagement {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/cms/efoldermanagment/productsupport");

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(ProductSupportUploadAction.class).getWorkspace();
    }

    @Override
    public void create(TestData td) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void initiate() {
        navigate();
        NavigationPage.toSubTab(NavigationEnum.CMSEfolderMgmtSubTab.PRODUCT_SUPPORT);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.CMS);
        NavigationPage.toLeftMenuTab(NavigationEnum.CMSLeftMenu.EFOLDER_MGMT);
    }

    public void upload(TestData testData) {
        initiate();
        EfolderManagmentPage.buttonSupportUpload.click();
        getDefaultWorkspace().fill(testData);
    }

    public void uploadNewVrsion(TestData testData, String fileName) {
        EfolderManagmentPage.tableSupportFileList.getRow("File Name", fileName).getCell(7).controls.links.get("Upload new version").click();
        getDefaultWorkspace().fill(testData);
    }

    public void filterDocument(TestData testData) {
        getDefaultWorkspace().fill(testData);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
