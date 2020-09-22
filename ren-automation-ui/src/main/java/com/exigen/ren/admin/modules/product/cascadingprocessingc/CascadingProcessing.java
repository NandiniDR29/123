/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.cascadingprocessingc;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.product.IProduct;
import com.exigen.ren.admin.modules.product.cascadingprocessingc.pages.CascadingProcessingPage;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;

public class CascadingProcessing implements IProduct {

    public void search(TestData td) {
        navigate();
        CascadingProcessingPage.search(td);
        CascadingProcessingPage.buttonSearch.click();
    }

    public void initiate() {
        throw new UnsupportedOperationException("Method is not realized");
    }

    public void submit() {
        throw new UnsupportedOperationException("Method is not realized");
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.PRODUCT);
        NavigationPage.toLeftMenuTab(NavigationEnum.ProductLeftMenu.CASCADING_PROCESSING);
    }

    @Override
    public Workspace getDefaultWorkspace() {
        throw new UnsupportedOperationException("Method is not realized");
    }

    @Override
    public void create(TestData td) {
        throw new UnsupportedOperationException("Method is not realized");
    }

    @Override
    public TestData defaultTestData() {
        throw new UnsupportedOperationException("Method is not realized");
    }
}
