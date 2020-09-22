/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cms.efoldermanagment.documenttype;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.cms.efoldermanagment.IEfolderManagement;
import com.exigen.ren.admin.modules.cms.efoldermanagment.documenttype.action.DocumentTypeCreateAction;
import com.exigen.ren.admin.modules.cms.efoldermanagment.pages.EfolderManagmentPage;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;

public class DocumentType implements IEfolderManagement {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/cms/efoldermanagment/documenttype");

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(DocumentTypeCreateAction.class).getWorkspace();
    }

    @Override
    public void create(TestData td) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void initiate() {
        navigate();
        NavigationPage.toSubTab(NavigationEnum.CMSEfolderMgmtSubTab.DOCUMENT_TYPES);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.CMS);
        NavigationPage.toLeftMenuTab(NavigationEnum.CMSLeftMenu.EFOLDER_MGMT);
    }

    public void addDocumentType(TestData testData, String code) {
        navigate();
        initiate();
        EfolderManagmentPage.tableDocumentTypeEntity.getRow("Code", code).getCell(ActionConstants.ACTIONS).controls.links.getFirst().click();
        EfolderManagmentPage.buttonAddNew.click();
        getDefaultWorkspace().fill(testData);
    }

    public void changeDocType(TestData testData, String code) {
        EfolderManagmentPage.tableAvailableTypes.getRow("Document Type Code", code).getCell(ActionConstants.ACTIONS).controls.links.getFirst().click();
        getDefaultWorkspace().fill(testData);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
