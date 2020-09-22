/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.agency;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.waiters.Waiter;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.agencyvendor.IAgencyVendor;
import com.exigen.ren.admin.modules.agencyvendor.agency.actions.AgencyCreateAction;
import com.exigen.ren.admin.modules.agencyvendor.agency.actions.AgencyUpdateAction;
import com.exigen.ren.admin.modules.agencyvendor.agency.tabs.SupportTeamTab;
import com.exigen.ren.admin.modules.agencyvendor.pages.AgencyVendorSummaryPage;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import org.openqa.selenium.By;

public class Agency implements IAgencyVendor {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/agencyvendor/agency");

    private Workspace defaultView = AutomationContext.getAction(AgencyCreateAction.class).getWorkspace();

    @Override
    public Workspace getDefaultWorkspace() {
        return defaultView;
    }

    public String createAgency(TestData td) {
        initiate();
        getDefaultWorkspace().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.AGENCY_NAME);
        new Button(By.xpath("//input[contains(@id,'saveButton_footer')]")).click();
        LOGGER.info("Created Agency with Name:" + entity);

        return entity;
    }

    @Override
    public void initiate() {
        navigate();
        AgencyVendorSummaryPage.buttonAddNewAgency.click();
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.AGENCY_VENDOR);
        NavigationPage.toLeftMenuTab(NavigationEnum.AgencyVendorLeftMenu.AGENCY);
        Waiters.SLEEP(5000).go();
    }

    @Override
    public AgencyUpdateAction update() {
        return AutomationContext.getAction(AgencyUpdateAction.class);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
