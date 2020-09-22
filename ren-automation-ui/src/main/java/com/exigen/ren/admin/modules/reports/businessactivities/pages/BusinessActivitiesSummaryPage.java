/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.reports.businessactivities.pages;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class BusinessActivitiesSummaryPage {
    public static Table tableActivityReportsForSelectedCriteria = new Table(By.id("activitySummaryForm:activitySummaryTable"));
    public static Table tableActivities = new Table(By.xpath("//form[@id='bamTreeForm']/table"));

    public static Button buttonChangeCriteria = new Button(By.id("bamReportsTopHeaderForm:changeCriteriaBtn"));
    public static Button buttonSubmitQuery = new Button(By.id("bamReportsHiddenForm:resetSaveReportDialogState"));
    public static Button buttonSaveReport = new Button(By.id("activitySummaryForm:saveReportBtn"));
    public static Button buttonCancel = new Button(By.id("topCancelLink"), Waiters.SLEEP(10000));

    public static Link linkGraph = new Link(By.id("activitySummaryForm:actSumGraphBtn"));

    public static StaticElement imgCountOfActivities = new StaticElement(By.id("AUTOGENBOOKMARK_2"));
    public static StaticElement imgDurationOfActivities = new StaticElement(By.id("AUTOGENBOOKMARK_3"));


}
