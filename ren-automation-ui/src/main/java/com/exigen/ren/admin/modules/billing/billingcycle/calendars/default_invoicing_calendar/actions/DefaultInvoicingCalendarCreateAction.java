/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.exigen.ren.admin.modules.billing.billingcycle.calendars.default_invoicing_calendar.actions;

import com.exigen.ren.admin.modules.billing.billingcycle.calendars.default_invoicing_calendar.tabs.DefaultInvoicingCalendarTab;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;

public class DefaultInvoicingCalendarCreateAction implements Action {
    private Workspace workspace = new Workspace.Builder()
            .registerTab(DefaultInvoicingCalendarTab.class)
            .build();

    @Override
    public String getName() {
        return "Default Invoicing Calendar Create Action";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }
}
