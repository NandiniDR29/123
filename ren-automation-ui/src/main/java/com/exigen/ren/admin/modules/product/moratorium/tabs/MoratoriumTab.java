/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.moratorium.tabs;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.admin.modules.product.moratorium.metadata.MoratoriumMetaData;
import com.exigen.ren.common.Tab;
import org.openqa.selenium.By;

public class MoratoriumTab extends Tab {
    public static Button buttonAddRule = new Button(By.id("moratoriumRegistryForm:addRuleBtn_footer"));
    public static Button buttonAddAdditionalExposureCap = new Button(By.id("moratoriumRegistryForm:exposureCapAddBtn"));
    public static Button buttonSaveRule = new Button(By.id("moratoriumRegistryForm:okBtnRule"));
    public static Table tableCatastropheDetails = new Table(By.id("moratoriumRegistryForm:body_catastropheTable"));

    public static Link linkDelete = new Link(By.xpath("//a[.='Delete']"));

    public MoratoriumTab() {
        super(MoratoriumMetaData.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        if (td.containsKey(MoratoriumTab.class.getSimpleName()) && td.getTestData(MoratoriumTab.class.getSimpleName()).getKeys().contains(MoratoriumMetaData.ADD_RULE_SECTION.getLabel())) {
            if (td.getTestData(MoratoriumTab.class.getSimpleName(), MoratoriumMetaData.AddRuleSection.class.getSimpleName()).getKeys().contains("Delete Rule")) {
                linkDelete.click();
            }
            buttonAddRule.click();
            if (td.getTestData(MoratoriumTab.class.getSimpleName()).containsKey(MoratoriumMetaData.AddRuleSection.class.getSimpleName())
                    && td.getTestData(MoratoriumTab.class.getSimpleName(), MoratoriumMetaData.AddRuleSection.class.getSimpleName()).getKeys().contains(MoratoriumMetaData.AddRuleSection.ADD_ADDITIONAL_EXPOSURE_CAP_SECTION.getLabel())) {
                buttonAddAdditionalExposureCap.click();
            }
            assetList.fill(td);
            buttonSaveRule.click();
            return this;
        }
        assetList.fill(td);
        return this;
    }
}
