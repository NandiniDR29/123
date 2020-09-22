/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cem.campaigns.tabs;

import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.admin.modules.cem.campaigns.metadata.SuspendCampaignMetaData;
import com.exigen.ren.common.ActionTab;
import org.openqa.selenium.By;

public class SuspendCampaignTab extends ActionTab {
    public SuspendCampaignTab() {
        super(SuspendCampaignMetaData.class);
        assetList = new AssetList(By.id("suspendCampaignModalPanel_container"), metaDataClass);
    }
}
