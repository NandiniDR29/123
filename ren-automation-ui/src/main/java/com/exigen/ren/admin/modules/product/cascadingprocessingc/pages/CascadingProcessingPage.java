/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.cascadingprocessingc.pages;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.admin.modules.common.pages.AdminPage;
import org.openqa.selenium.By;

public class CascadingProcessingPage extends AdminPage {
    public static AssetList assetListSearch = new AssetList(By.id("searchForm"), CascadingProcessingSearchTab.class);
    public static Table tableSearch = new Table(By.xpath("//div[@id='searchResultForm:resultTable']//table")).applyConfiguration("NoRecordsFound");
    public static Table tableSearchLinkPolicy = new Table(By.xpath("//div[@id='viewForm:policiesTable']//table"));
    public static Button buttonCancel = new Button(By.id("viewForm:cancel"));

    public static void search(TestData td) {
        assetListSearch.fill(td);
        buttonSearch.click();
    }

    public static class CascadingProcessingSearchTab extends MetaData {
        public static final AssetDescriptor<TextBox> MASTER_POLICY_NUMBER = declare("Master Policy Number", TextBox.class);

    }
}
