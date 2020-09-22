/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.commission.commissionbonus;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.commission.ICommission;
import com.exigen.ren.admin.modules.commission.commissionbonus.actions.CommissionBonusCreateAction;
import com.exigen.ren.admin.modules.commission.commissionbonus.actions.CommissionBonusDeleteAction;
import com.exigen.ren.admin.modules.commission.commissionbonus.actions.CommissionBonusEditAction;
import com.exigen.ren.admin.modules.commission.commissionbonus.actions.CommissionBonusExpireAction;
import com.exigen.ren.admin.modules.commission.commissiontrategy.pages.CommissionPage;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;

public class CommissionBonus implements ICommission {


    @Override
    public void submit() {

    }

    @Override
    public boolean searched(TestData td) {
        throw new UnsupportedOperationException("Not Supported");
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.COMMISSION);
        NavigationPage.toLeftMenuTab(NavigationEnum.CommissionLeftMenu.COMMISSION_BONUS);
    }

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(CommissionBonusCreateAction.class).getWorkspace();
    }

    @Override
    public void create(TestData td) {
        navigate();
        initiate();
        getDefaultWorkspace().fill(td);
    }

    @Override
    public void initiate() {
        CommissionPage.buttonAddCommissionBonus.click();
    }

    @Override
    public CommissionBonusEditAction edit() {
        return AutomationContext.getAction(CommissionBonusEditAction.class);
    }

    @Override
    public CommissionBonusExpireAction expire() {
        return AutomationContext.getAction(CommissionBonusExpireAction.class);
    }

    @Override
    public Action copy() {
        return null;
    }

    @Override
    public CommissionBonusDeleteAction delete() {
        return AutomationContext.getAction(CommissionBonusDeleteAction.class);
    }

    @Override
    public TestData defaultTestData() {
        return TestDataProvider.getDefaultTestDataProvider().get("modules/commission/commissionbonus");
    }
}
