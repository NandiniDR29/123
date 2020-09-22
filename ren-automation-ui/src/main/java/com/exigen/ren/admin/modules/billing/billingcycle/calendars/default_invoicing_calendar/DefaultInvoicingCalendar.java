/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.exigen.ren.admin.modules.billing.billingcycle.calendars.default_invoicing_calendar;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.billing.billingcycle.IBillingCycle;
import com.exigen.ren.admin.modules.billing.billingcycle.calendars.default_invoicing_calendar.actions.DefaultInvoicingCalendarCreateAction;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;

public class DefaultInvoicingCalendar implements IBillingCycle {
    private TestData defaultTesData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/billing/billingcycle/default_invoicing_calendar");

      @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.BILLING);
        NavigationPage.toLeftMenuTab(NavigationEnum.BillingLeftMenu.BILLING_CYCLE);
        NavigationPage.toSubTab(NavigationEnum.BillingBillingCycleSubTab.CALENDARS);
        NavigationPage.toSubTab(NavigationEnum.BillingBillingCycleCalendarsSubTab.DEFAULT_INVOICING_CALENDAR);
    }

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(DefaultInvoicingCalendarCreateAction.class).getWorkspace();
    }

    @Override
    public void create(TestData td) {
        navigate();
        getDefaultWorkspace().fill(td);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTesData;
    }
}