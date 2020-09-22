/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.brand;

import com.exigen.istf.data.TestData;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.agencyvendor.IAgencyVendor;
import com.exigen.ren.admin.modules.agencyvendor.brand.actions.BrandCreateAction;
import com.exigen.ren.admin.modules.agencyvendor.brand.actions.BrandUpdateTypeAction;
import com.exigen.ren.admin.modules.agencyvendor.brand.pages.BrandPage;
import com.exigen.ren.admin.modules.agencyvendor.brand.tabs.BrandTab;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;

public class BrandType implements IAgencyVendor {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/agencyvendor/brand");

    @Override
    public void initiate() {
        navigate();
        BrandPage.expandBrandType();
        BrandPage.buttonAddBrandType.click();
    }

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(BrandCreateAction.class).getWorkspace();
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultWorkspace().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.BRAND_TYPE);
        BrandTab.buttonSave.click();
        LOGGER.info("Created Brand Type " + entity);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.AGENCY_VENDOR);
        NavigationPage.toLeftMenuTab(NavigationEnum.AgencyVendorLeftMenu.BRAND);
    }

    @Override
    public BrandUpdateTypeAction update() {
        return AutomationContext.getAction(BrandUpdateTypeAction.class);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
