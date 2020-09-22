/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.reports.operationalreports.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.admin.modules.reports.operationalreports.metadata.OperationalReportsMetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class OperationalReportsTab extends DefaultTab {
    public static Button buttonGenerateReport = new Button(By.id("jobsForm:generateReport"));
    public static Button buttonSchedule = new Button(By.id("jobsForm:schedule"));
    public static Button buttonReport = new Button(By.id("jobsForm:generateReport"));

    public OperationalReportsTab() {
        super(OperationalReportsMetaData.class);
        assetList = new AssetList(By.xpath(Page.OPERATIONAL_REPORTS_ASSETLIST_CONTAINER), metaDataClass);
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
