/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cem.cemconfiguration.pages;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.CheckBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.webstudio.pages.AdminPage;
import org.openqa.selenium.By;

public class FieldsConfigurationPage extends AdminPage {

    public static AssetList assetListFiledConfiguration = new AssetList(By.id("contentWrapper"), FieldsConfigurationTab.class);
    public static Button buttonNext = new Button(By.xpath("//input[@value = 'Next' and not(@class = 'hidden') and not(contains(@style,'none'))]"));
    public static Button buttonSave = new Button(By.xpath("//input[@value = 'Save' and not(@class = 'hidden') and not(contains(@style,'none'))]"));

    public static class FieldsConfigurationTab extends MetaData {
        public static final AssetDescriptor<CheckBox> ADDRESS_LINE_3_HIDDEN = declare("Address Line 3", CheckBox.class, By.id("configForm:fieldsConfigurationTable:5:fieldHidden"));
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_2_DEFAULT_VALUE = declare("Address Line 2", TextBox.class, By.id("configForm:fieldsConfigurationTable:4:defaultSTRING"));
        public static final AssetDescriptor<TextBox> ATTENTION_ORDER = declare("Attention", TextBox.class, By.id("configForm:fieldsConfigurationTable:7:fieldOrder"));
    }

    public static void createConfiguration(TestData td) {
        assetListFiledConfiguration.fill(td);
        if (buttonSave.isPresent()) {
            buttonSave.click();
        } else {
            buttonNext.click();
        }
    }
}
