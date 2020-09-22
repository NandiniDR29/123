/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.caseprofile.tabs;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.caseprofile.metadata.ProductAndPlanManagementTabMetaData;
import org.openqa.selenium.By;

public class ProductAndPlanManagementTab extends DefaultTab {

    public static Link linkNext = new Link(By.xpath("//a[.='Next']"));
    public static Button buttonSaveProduct = new Button(By.id("addProductForm:saveProductBtn"));

    public ProductAndPlanManagementTab() {
        super(ProductAndPlanManagementTabMetaData.class);

        assetList = new MultiAssetList(By.xpath("//div[@id='addProductPopup_container']"), metaDataClass) {
            @Override
            protected void setSectionValue(int index, TestData value) {
                super.setSectionValue(index, value);
                buttonSaveProduct.click();
            }

            @Override
            protected void addSection(int index, int size) {
                new Button(By.id("caseForm:addProductBtn")).click();
            }

            @Override
            protected void selectSection(int index) {}
        };
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab fillTab(TestData td) {
        if (td.containsKey(getMetaKey()) && !td.getTestDataList(getMetaKey()).get(0).getKeys().isEmpty()) {
            super.getAssetList().fill(td);
        }

        return this;
    }

    @Override
    public Tab submitTab() {
        linkNext.click();
        return this;
    }
}
