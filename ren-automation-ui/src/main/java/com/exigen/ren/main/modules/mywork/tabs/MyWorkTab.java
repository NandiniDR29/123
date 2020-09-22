/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.mywork.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.mywork.metadata.MyWorkTabMetaData;
import org.openqa.selenium.By;

public class MyWorkTab extends DefaultTab {

    public static Button buttonAssign = new Button(By.id("taskListForm:bulkTaskAssign"));
    public static Button buttonComplete = new Button(By.id("taskListForm:bulkTaskComplete"));
    public static Button buttonAssignExpandedTask = new Button(By.xpath("//button[contains(@id, 'assignTask')]"));
    public static Button buttonUpdateExpandedTask = new Button(By.xpath("//button[contains(@id, 'updateTask')]"));
    public static Button buttonCompleteExpandedTask = new Button(By.xpath("//button[contains(@id, 'completeTask')]"));

    public static TableExtended<MyWorkTasks> tableMyWorkTasks = new TableExtended<>("My Work Tasks",
            By.xpath("//div[@id='taskListForm:customTaskTable:workTasks']//table"), By.xpath("//div[@id='taskListForm:customTaskTable:workTasks_paginator_bottom']"));


    public MyWorkTab() {
        super(MyWorkTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }

    public enum MyWorkTasks implements Named {
        TASK_ID("Task ID"),
        TASK_NAME("Task Name"),
        WARNING_DATE("Warning Date"),
        DUE_DATE("Due Date"),
        PRIORITY("Priority"),
        REFERENCE_ID("Reference ID"),
        CUSTOMER("Customer"),
        AGENCY_LOCATION_ID("Agency / Location ID"),
        AGENCY_LOCATION_NAME("Agency / Location Name"),
        LAST_PERFORMER("Last Performer"),
        ASSIGNEE("Assignee"),
        QUEUE("Queue");

        private String name;

        MyWorkTasks(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

}
