/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.commission.commissiontemplate;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.commission.ICommission;
import com.exigen.ren.admin.modules.commission.commissiontemplate.actions.CommissionTemplateCreateAction;
import com.exigen.ren.admin.modules.commission.commissiontemplate.actions.CommissionTemplateDeleteAction;
import com.exigen.ren.admin.modules.commission.commissiontemplate.actions.CommissionTemplateEditAction;
import com.exigen.ren.admin.modules.commission.commissiontemplate.pages.CommissionTemplatePage;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;

public class CommissionTemplate implements ICommission {
    private String type;

    public CommissionTemplate(String type) {
        this.type = type;
    }

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(CommissionTemplateCreateAction.class).getWorkspace();
    }

    @Override
    public void initiate() {
        CommissionTemplatePage.comboboxCommissionTemplateType.setValue(type);
        CommissionTemplatePage.buttonAddNewCommissionTemplate.click();
    }

    @Override
    public void create(TestData td) {
        navigate();
        initiate();
        getDefaultWorkspace().fill(td);
        CommissionTemplatePage.buttonOK.click();
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.COMMISSION);
        NavigationPage.toLeftMenuTab(NavigationEnum.CommissionLeftMenu.COMMISSION_TEMPLATE);
    }


    @Override
    public void submit() {

    }

    @Override
    public boolean searched(TestData td) {
        return false;
    }

    @Override
    public Action expire() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Action copy() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public CommissionTemplateEditAction edit() {
        return AutomationContext.getAction(CommissionTemplateEditAction.class);
    }

    @Override
    public CommissionTemplateDeleteAction delete() {
        return AutomationContext.getAction(CommissionTemplateDeleteAction.class);
    }

    @Override
    public TestData defaultTestData() {
        return TestDataProvider.getDefaultTestDataProvider().get("modules/commission/commissiontemplate/heap");
    }
}
