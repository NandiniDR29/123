/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.reports.businessactivities.tabs;

import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.admin.modules.reports.businessactivities.metadata.BusinessActivitiesMetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import org.openqa.selenium.By;

public class BusinessActivitiesTab extends DefaultTab {

    public static Table tableActivities = new Table(By.xpath("//form[@id='bamTreeForm']/table"));

    public static Button buttonChangeCriteria = new Button(By.id("bamReportsTopHeaderForm:changeCriteriaBtn"));
    public static Button buttonOk = new Button(By.id("advancedCriteriaForm:okBtn"));

    public static Link linkReportOverview = new Link(By.id("criteriaTopHeaderForm:reportOverview"));

    public static StaticElement frame = new StaticElement(By.xpath("//form[@id='urlForm']/iframe"));
    public static StaticElement imageGraph = new StaticElement(By.id("AUTOGENBOOKMARK_2"));

    public BusinessActivitiesTab() {
        super(BusinessActivitiesMetaData.class);
    }

    public void switchToFrame() {
        frame.waitForAccessible(2000);
        BrowserController.get().driver().switchTo().frame(frame.getWebElement());
    }

    @Override
    public Tab submitTab() {
        buttonOk.click();
        buttonChangeCriteria.waitForAccessible(3000, buttonChangeCriteria.isEnabled());
        return this;
    }
}
