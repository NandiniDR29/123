/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.account.tabs;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.AccountConstants;
import com.exigen.ren.main.modules.account.metadata.SelectContactTabMetaData;
import org.openqa.selenium.By;

public class SelectContactTab extends ActionTab {

    public static Button buttonSelectContact = new Button(By.xpath("//input[@value= 'Select Contact']"));
    public static Button buttonContactSearch = new Button(By.id("contactSearchPopupForm:search"));
    public static Table tableContactSeachResults = new Table(By.id("contactSearchPopupForm:resultsTable"));

    public SelectContactTab() {
        super(SelectContactTabMetaData.class);
        assetList = new AssetList(By.id("contactSearchPopup_container"), metaDataClass);
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab fillTab(TestData td) {
        buttonSelectContact.click();
        ((AssetList) assetList).fill(td);
        buttonContactSearch.click();
        tableContactSeachResults.getColumn(AccountConstants.AccountContactSeachResultsTable.NAME).getCell(1).controls.links.getFirst().click();
        return this;
    }
}
