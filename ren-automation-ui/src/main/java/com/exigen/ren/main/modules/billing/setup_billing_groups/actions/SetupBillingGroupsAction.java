/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.setup_billing_groups.actions;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.billing.setup_billing_groups.tabs.BillingAccountSetupTab;
import com.exigen.ren.main.modules.billing.setup_billing_groups.tabs.BillingGroupsEffectiveDateTab;
import com.exigen.ren.main.modules.billing.setup_billing_groups.tabs.BillingGroupsTab;
import com.exigen.ren.main.modules.billing.setup_billing_groups.tabs.BillingLocationsMappingTab;
import org.openqa.selenium.By;


public class SetupBillingGroupsAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(BillingGroupsEffectiveDateTab.class)
            .registerTab(BillingLocationsMappingTab.class)
            .registerTab(BillingGroupsTab.class)
            .registerTab(BillingAccountSetupTab.class)
            .registerTab(BillingAccountTab.class)
            .build();

    @Override
    public String getName() {
        return "Setup Billing Groups";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action perform(TestData td) {
        start();
        getWorkspace().fill(td);
        return this;
    }

    @Override
    public Action start() {
        Action.super.start();
        Link link = new Link(By.id("billingInfoForm:policiesForAccountTable:0:setupBillingGroupsLink"));
        if (link.isPresent()) {
            link.click();
        }
        return this;
    }

    public Action start(String policyNumber) {
        Action.super.start();
        Link link = new Link(By.xpath(String.format("//a[text()='%s' and contains(@id, 'setupBillingGroupsLink') and @class!='hidden']", policyNumber)));
        if (link.isPresent()) {
            link.click();
        }
        return this;
    }
}