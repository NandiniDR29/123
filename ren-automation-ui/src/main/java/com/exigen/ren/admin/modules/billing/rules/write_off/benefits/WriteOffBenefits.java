/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.exigen.ren.admin.modules.billing.rules.write_off.benefits;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.billing.rules.IRules;
import com.exigen.ren.admin.modules.billing.rules.write_off.benefits.actions.WriteOffBenefitCreateAction;
import com.exigen.ren.admin.modules.billing.rules.write_off.benefits.actions.WriteOffBenefitSearchAction;
import com.exigen.ren.admin.modules.billing.rules.write_off.benefits.pages.WriteOffBenefitsPage;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;

import static com.exigen.ren.common.enums.NavigationEnum.BillingRulesSubTab.WRITE_OFF;
import static com.exigen.ren.main.enums.CaseProfileConstants.ErrorMessages.NO_RECORD_FOUND;

public class WriteOffBenefits implements IRules {
    private TestData defaultTesData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/billing/rules/write_off/benefits");

    @Override
    public TestData defaultTestData() {
        return defaultTesData;
    }

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(WriteOffBenefitCreateAction.class).getWorkspace();
    }

    public Workspace getSearchDefaultWorkspace() {
        return AutomationContext.getAction(WriteOffBenefitSearchAction.class).getWorkspace();
    }

    @Override
    public void create(TestData td) {
        navigate();
        if(isRecordNotFound(td)){
            getDefaultWorkspace().fill(td);
        }
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.BILLING);
        NavigationPage.toLeftMenuTab(NavigationEnum.BillingLeftMenu.RULES);
        NavigationPage.toSubTab(WRITE_OFF);
        NavigationPage.toSubTab(NavigationEnum.BillingRulesWriteOffSubTab.BENEFITS);
    }

    public void search(TestData td) {
        getSearchDefaultWorkspace().fill(td);
    }

    private boolean isRecordNotFound(TestData td) {
        search(td);
        return WriteOffBenefitsPage.tableWriteOffReasonsPage.getRow(1).getValue().contains(NO_RECORD_FOUND);
    }
}
