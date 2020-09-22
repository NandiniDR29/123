/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.moratorium.pages;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import org.openqa.selenium.By;

public class MoratoriumPage {
    public static AssetList assetListSearch = new AssetList(By.id("searchForm"), MoratoriumSearchTab.class);

    public static Button buttonAddMoratorium = new Button(By.id("searchForm:addMoratoriumBtn"));
    public static Button buttonSearch = new Button(By.id("searchForm:searchBtn"));

    public static Table tableSearchResult = new Table(By.id("searchResultForm:resultInfoTable")).applyConfiguration("MoratoriumSearchResult");

    public static void search(TestData testData) {
        assetListSearch.fill(testData);
    }

    public static class MoratoriumSearchTab extends MetaData {
        public static final AssetDescriptor<TextBox> MORATORIUM_NAME = declare("Moratorium Name", TextBox.class);
        public static final AssetDescriptor<ComboBox> LOB = declare("LOB", ComboBox.class);
        public static final AssetDescriptor<AdvancedSelector> PRODUCT = declare("Product", AdvancedSelector.class);
        public static final AssetDescriptor<ComboBox> BRAND = declare("Brand", ComboBox.class);
        public static final AssetDescriptor<ComboBox> MORATORIUM_TYPE = declare("Moratorium Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> STATUS = declare("Status", ComboBox.class);
        public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
        public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class);
        public static final AssetDescriptor<ComboBox> CITY = declare("City", ComboBox.class);
        public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
    }
}
