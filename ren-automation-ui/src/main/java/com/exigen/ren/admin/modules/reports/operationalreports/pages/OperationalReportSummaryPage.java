/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.reports.operationalreports.pages;

import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import org.openqa.selenium.By;

public class OperationalReportSummaryPage {

    public static StaticElement labelCaptionMain = new StaticElement(By.xpath("//table[@id='workbook']//table[@id='Policy Status']/tbody/tr[1]/td[1]"));
    public static StaticElement labelCaptionPeriod = new StaticElement(By.xpath("//table[@id='workbook']//table[@id='Policy Status']/tbody/tr[2]/td[1]"));
    public static StaticElement labelCaptionRun = new StaticElement(By.xpath("//table[@id='workbook']//table[@id='Policy Status']/tbody/tr[3]/td[1]"));

    public static Table tableWorkBook = new Table(By.xpath("//table[@id='workbook']"));
    public static Table tableReportData = new Table(By.xpath("//table[@id='workbook']//table//table"));
    public static Table tableTriggers = new Table(By.id("triggersForm:triggersTable"));
}
