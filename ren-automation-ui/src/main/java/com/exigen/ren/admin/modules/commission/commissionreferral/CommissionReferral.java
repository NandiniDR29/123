/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.commission.commissionreferral;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.commission.ICommission;
import com.exigen.ren.admin.modules.commission.commissionreferral.actions.CommissionReferralCreateAction;
import com.exigen.ren.admin.modules.commission.commissionreferral.actions.CommissionReferralDeleteAction;
import com.exigen.ren.admin.modules.commission.commissionreferral.actions.CommissionReferralEditAction;
import com.exigen.ren.admin.modules.commission.commissionreferral.actions.CommissionReferralExpireAction;
import com.exigen.ren.admin.modules.commission.commissiontrategy.pages.CommissionPage;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;

public class CommissionReferral implements ICommission {


    @Override
    public void submit() {

    }

    @Override
    public boolean searched(TestData td) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.COMMISSION);
        NavigationPage.toLeftMenuTab(NavigationEnum.CommissionLeftMenu.COMMISSION_REFERRAL);
    }

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(CommissionReferralCreateAction.class).getWorkspace();
    }

    @Override
    public void create(TestData td) {
        navigate();
        initiate();
        getDefaultWorkspace().fill(td);
    }

    @Override
    public void initiate() {
        CommissionPage.buttonAddCommissionReferral.click();
    }

    @Override
    public CommissionReferralEditAction edit() {
        return AutomationContext.getAction(CommissionReferralEditAction.class);
    }

    @Override
    public CommissionReferralExpireAction expire() {
        return AutomationContext.getAction(CommissionReferralExpireAction.class);
    }

    @Override
    public Action copy() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public CommissionReferralDeleteAction delete() {
        return AutomationContext.getAction(CommissionReferralDeleteAction.class);
    }

    @Override
    public TestData defaultTestData() {
        return TestDataProvider.getDefaultTestDataProvider().get("modules/commission/commissionreferral");
    }
}
