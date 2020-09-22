/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.vendor;


import com.exigen.istf.data.TestData;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.agencyvendor.IAgencyVendor;
import com.exigen.ren.admin.modules.agencyvendor.vendor.actions.VendorCreateAction;
import com.exigen.ren.admin.modules.agencyvendor.vendor.actions.VendorUpdateAction;
import com.exigen.ren.admin.modules.agencyvendor.vendor.pages.VendorPage;
import com.exigen.ren.admin.modules.agencyvendor.vendor.tabs.ContactInfoTab;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;

public class Vendor implements IAgencyVendor {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/agencyvendor/vendor");

    @Override
    public void initiate() {
        navigate();
        VendorPage.buttonAddVendor.click();
    }

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(VendorCreateAction.class).getWorkspace();
    }

    @Override
    public void search(TestData td) {
        VendorPage.search(td);
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultWorkspace().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.VENDOR);
        ContactInfoTab.buttonOk.click();
        LOGGER.info("Created Vendor: " + entity);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.AGENCY_VENDOR);
        NavigationPage.toLeftMenuTab(NavigationEnum.AgencyVendorLeftMenu.VENDOR);
    }

    @Override
    public VendorUpdateAction update() {
        return AutomationContext.getAction(VendorUpdateAction.class);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
