/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.asynctasks.pages;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.admin.modules.common.pages.AdminPage;
import org.openqa.selenium.By;

public class GeneralAsyncTasksPage extends AdminPage {

    public static Table tableFailedTasks = new Table(By.xpath("//div[@id='failedAsyncTasksForm:failedAsyncTasksTable']//table"));
    public static Button buttonRefreshFailedTasks = new Button(By.id("failedAsyncTasksForm:refreshFailedTasks"));
    public static Button buttonStartManagers = new Button(By.id("asyncTaskSummaryForm:startManagers"));
    public static Button buttonStopManagers = new Button(By.id("asyncTaskSummaryForm:stopManagers"));
    public static Button buttonRefreshManagers = new Button(By.id("asyncTaskSummaryForm:refreshManagerStatus"));
    public static Button buttonStartManager = new Button(By.id("asyncTaskSummaryForm:startManager"));
    public static Button buttonStopManagement = new Button(By.id("asyncTaskSummaryForm:stopManager"));
    public static Table tableTaskProcessingManagement = new Table(By.xpath("//div[@id='asyncTaskSummaryForm:managersList']//table"));
    public static Button buttonUnlockAll = new Button(By.id("failedAsyncTasksForm:unlockAllTasks"));
    public static Button buttonRemoveAll = new Button(By.id("failedAsyncTasksForm:removeAllTasks"));
    public static StaticElement labelStatusRunning = new StaticElement(By.xpath("//tbody[@id='asyncTaskSummaryForm:managersList_data']//span[.='Running']"));
    public static StaticElement labelStatusStopped = new StaticElement(By.xpath("//tbody[@id='asyncTaskSummaryForm:managersList_data']//span[.='Stopped']"));
    public static Link linkStartManager = new Link(By.id("asyncTaskSummaryForm:startManager"));
    public static Link linkStopManager = new Link(By.id("asyncTaskSummaryForm:stopManager"));

}
