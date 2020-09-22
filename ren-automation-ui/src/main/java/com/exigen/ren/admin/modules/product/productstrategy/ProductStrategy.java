/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.productstrategy;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.product.IProduct;
import com.exigen.ren.admin.modules.product.productstrategy.action.ProductProcessingCreateAction;
import com.exigen.ren.admin.modules.product.productstrategy.pages.AutomatedProcessingPage;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;

public class ProductStrategy implements IProduct {

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(ProductProcessingCreateAction.class).getWorkspace();
    }

    public void initiate() {
        navigate();
        AutomatedProcessingPage.buttonAddNewStrategy.click();
    }

    public void search(TestData td) {
        AutomatedProcessingPage.search(td);
        AutomatedProcessingPage.buttonSearch.click();
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.PRODUCT);
        NavigationPage.toLeftMenuTab(NavigationEnum.ProductLeftMenu.AUTOMATED_PROCESSING);
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultWorkspace().fill(td);
    }

    @Override
    public TestData defaultTestData() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
