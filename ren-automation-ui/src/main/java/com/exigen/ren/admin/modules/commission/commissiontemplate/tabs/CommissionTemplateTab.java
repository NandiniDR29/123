/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.commission.commissiontemplate.tabs;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.admin.modules.commission.commissiontemplate.CommissionTemplateDefaultTab;
import com.exigen.ren.admin.modules.commission.commissiontemplate.metadata.CommissionTemplateMetaData;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class CommissionTemplateTab extends CommissionTemplateDefaultTab {
    public static final Button buttonAdd = new Button(By.xpath("//button[contains(@id, 'addBandBtn')]"));

    public CommissionTemplateTab() {
        super(CommissionTemplateMetaData.class);
        assetList = new AssetList(By.xpath(Page.DEFAULT_DIALOG_LOCATOR), metaDataClass);
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab fillTab(TestData td) {
        super.fillTab(td);

        MultiAssetList multiAssetList = new MultiAssetList(By.xpath(Page.DEFAULT_DIALOG_LOCATOR), CommissionTemplateMetaData.AddCommissionTemplate.class) {
            @Override
            protected void addSection(int index, int size) {
                if (index > 0) {
                    buttonAdd.click();
                }
            }

            @Override
            protected void selectSection(int index) {
            }
        };
        multiAssetList.setName(CommissionTemplateMetaData.AddCommissionTemplate.class.getSimpleName());
        multiAssetList.fill(td.getTestData(this.getClass().getSimpleName()));
        return this;
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
