/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cms.templatemanagment.doctemplates;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.cms.templatemanagment.ITemplateManagement;
import com.exigen.ren.admin.modules.cms.templatemanagment.doctemplates.actions.DocTemplateCreateAction;
import com.exigen.ren.admin.modules.cms.templatemanagment.doctemplates.actions.DocTemplateRefreshAction;
import com.exigen.ren.admin.modules.cms.templatemanagment.doctemplates.pages.DocTemplatesPage;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;

public class DocTemplates implements ITemplateManagement {

    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/cms/templatemanagment/doctemplates");

    @Override
    public void initiate() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(DocTemplateCreateAction.class).getWorkspace();
    }

    public void initiate(TestData testData) {
        DocTemplatesPage.dialogAddTemplate.fill(testData);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.CMS);
        NavigationPage.toLeftMenuTab(NavigationEnum.CMSLeftMenu.TEMPLATE_MGMT);
    }

    @Override
    public void refresh(TestData td) {
        navigate();
        refresh().getWorkspace().fill(td);
        DocTemplatesPage.buttonRefresh.click();
    }

    public DocTemplateRefreshAction refresh() {
        return AutomationContext.getAction(DocTemplateRefreshAction.class);
    }

    @Override
    public void create(TestData testData) {
        navigate();
        initiate(testData);
        getDefaultWorkspace().fill(testData);
        Tab.buttonSave.click();
    }

    public void delete(int rowNumber) {
        DocTemplatesPage.tableTemplateSearch.getRow(rowNumber).getCell(9).controls.links.getFirst().click();
        Page.dialogConfirmation.confirm();
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
