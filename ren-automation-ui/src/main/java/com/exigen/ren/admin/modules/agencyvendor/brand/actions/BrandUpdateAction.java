/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.brand.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.agencyvendor.brand.metadata.BrandMetaData;
import com.exigen.ren.admin.modules.agencyvendor.brand.pages.BrandPage;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.enums.AdminConstants;

public class BrandUpdateAction implements Action {
    @Override
    public String getName() {
        return "Update Brand Action";
    }

    @Override
    public Workspace getWorkspace() {
        return AutomationContext.getAction(BrandCreateAction.class).getWorkspace();
    }


    public Action start(int rowNumber) {
        BrandPage.tableBrands.getRow(rowNumber).getCell(AdminConstants.AdminBrandsTable.ACTION).controls.links.getFirst().click();
        return this;
    }

    public Action start(String brandName) {
        BrandPage.tableBrands.getRow(AdminConstants.AdminBrandsTable.NAME, brandName).getCell(AdminConstants.AdminBrandsTable.ACTION).controls.links.getFirst().click();
        return this;
    }

    @Override
    public Action start() {
        throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
    }

    @Override
    public Action submit() {
        BrandPage.assetListAddBrand.getAsset(BrandMetaData.AddBrandDialog.BUTTON_UPDATE).getWebElement().click();
        return this;
    }

    public Action perform(TestData td, int rowNumber) {
        start(rowNumber);
        getWorkspace().fill(td);
        return submit();
    }

    public Action perform(TestData td, String brandName) {
        start(brandName);
        getWorkspace().fill(td);
        return submit();
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
    }
}
