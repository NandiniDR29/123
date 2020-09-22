/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.tabs;

import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.customer.metadata.CustomerTypeMetaData;
import org.openqa.selenium.By;

public class CustomerTypeTab extends DefaultTab {

    public CustomerTypeTab() {
        super(CustomerTypeMetaData.class);
        assetList = new AssetList(By.xpath("//div[@id='searchForm:customerTypePopup_container' or @id='customerTypePopup_container']"), metaDataClass);
        assetList.applyConfiguration("CustomerTypeTab");
        assetList.setName("CustomerType");
    }

    @Override
    public Tab submitTab() {
        Page.dialogConfirmation.confirm();
        return this;
    }
}
