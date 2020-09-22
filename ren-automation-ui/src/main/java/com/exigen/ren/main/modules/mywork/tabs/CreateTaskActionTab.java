/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.mywork.tabs;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.mywork.MyWorkContext;
import com.exigen.ren.main.modules.mywork.metadata.CreateTaskActionTabMetaData;
import org.openqa.selenium.By;

public class CreateTaskActionTab extends ActionTab {

    public static Button buttonCreate = new Button(By.id("taskCreateCommandForm:startTask_footer"));
    public static Button buttonCancel = new Button(By.id("taskCreateCommandForm:cancel_footer"));
    public static Button buttonCreateTask = new Button(By.xpath("//a[contains(@id,'createTask') and text()='Create Task']"));
    public static Button buttonTasks = new Button(By.xpath("//*[contains(@id,'tasksList') and text()='Tasks']"));
    public static Button buttonAssignTask = new Button(By.id("taskCreateForm:assignTask"));


    @Override
    public Tab fillTab(TestData td) {
        super.fillTab(td);
        if (td.containsKey(getMetaKey()) && !td.getTestData(getMetaKey()).getKeys().isEmpty()) {
            if (td.getTestData(getMetaKey()).containsKey(AssignTaskToActionTab.class.getSimpleName())
                    && !td.getTestData(getMetaKey(), AssignTaskToActionTab.class.getSimpleName()).getKeys().isEmpty()) {
                buttonAssignTask.click();
                MyWorkContext.myWork.assignTaskTo().getWorkspace().getTab(AssignTaskToActionTab.class).fillTab(td.getTestData(getMetaKey()));
                AssignTaskToActionTab.buttonAssign.click();
            }
        }
        return this;
    }

    public CreateTaskActionTab() {
        super(CreateTaskActionTabMetaData.class);
    }
}
