/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.catastrophe;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.general.IGeneral;
import com.exigen.ren.admin.modules.general.catastrophe.action.CatastropheCreateAction;
import com.exigen.ren.admin.modules.general.catastrophe.action.CatastropheUpdateAction;
import com.exigen.ren.admin.modules.general.catastrophe.pages.CatastrophePage;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;

public class Catastrophe implements IGeneral {

    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/general/catastrophe");

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(CatastropheCreateAction.class).getWorkspace();
    }

    @Override
    public void initiate() {

    }

    @Override
    public void create(TestData td) {
        navigate();
        create().perform(td);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.GENERAL);
        NavigationPage.toLeftMenuTab(NavigationEnum.GeneralTabLeftMenu.CATASTROPHE_REGISTRY);
    }

    public void search(TestData td) {
        CatastrophePage.search(td);
    }

    public CatastropheCreateAction create() {
        return AutomationContext.getAction(CatastropheCreateAction.class);
    }

    public CatastropheUpdateAction update() {
        return AutomationContext.getAction(CatastropheUpdateAction.class);
    }

    public void update(String updatedElementName, TestData td) {
        update().perform(updatedElementName, td);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
