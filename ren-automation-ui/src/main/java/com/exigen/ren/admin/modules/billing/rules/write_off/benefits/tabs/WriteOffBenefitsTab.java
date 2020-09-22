/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.exigen.ren.admin.modules.billing.rules.write_off.benefits.tabs;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.admin.modules.billing.rules.write_off.benefits.metadata.WriteOffBenefitsMetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

import static com.exigen.ren.admin.modules.billing.rules.write_off.benefits.pages.WriteOffBenefitsPage.buttonAddWriteOff;

public class WriteOffBenefitsTab extends DefaultTab {

    public static final Button buttonAddUpdate = new Button(By.id("writeOffEditForm:addUpdate_footer"));

    public WriteOffBenefitsTab()
    {
        super(WriteOffBenefitsMetaData.class);
        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {
            @Override
            protected void addSection(int index, int size) {
                buttonAddWriteOff.click();
            }

            @Override
            protected void selectSection(int index) {
            }

            @Override
            protected void setSectionValue(int index, TestData value) {
                super.setSectionValue(index, value);
                buttonAddUpdate.click();
            }
        };
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab submitTab() { return this;}
}