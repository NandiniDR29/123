/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.tabs;

import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.customer.metadata.SelectGroupRelationshipTypeActionTabMetaData;
import org.openqa.selenium.By;

public class SelectGroupRelationshipTypeActionTab extends ActionTab {
    public SelectGroupRelationshipTypeActionTab() {
        super(SelectGroupRelationshipTypeActionTabMetaData.class);
        assetList = new AssetList(By.xpath("//*"), metaDataClass);
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab submitTab() {
        Page.dialogConfirmation.buttonOk.click();
        return this;
    }
}
