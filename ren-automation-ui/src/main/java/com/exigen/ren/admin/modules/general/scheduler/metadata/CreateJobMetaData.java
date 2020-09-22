/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.scheduler.metadata;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.CheckBox;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class CreateJobMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> ONE = declare("1.", ComboBox.class, By.id("jobsForm:job_0_class"));
    public static final AssetDescriptor<ComboBox> TWO = declare("2.", ComboBox.class, By.id("jobsForm:job_1_class"));
    public static final AssetDescriptor<ComboBox> THREE = declare("3.", ComboBox.class, Waiters.AJAX, By.id("jobsForm:job_2_class"));
    public static final AssetDescriptor<CheckBox> ENABLED = declare("Enabled", CheckBox.class, Waiters.AJAX, By.id("jobsForm:enableGroup"));
    public static final AssetDescriptor<TextBox> JOB_PARAMETERS = declare("Job Parameters", TextBox.class);
    public static final AssetDescriptor<TextBox> START_DATE = declare("Start Date", TextBox.class, By.id("jobsForm:startDateInputDate"));
    public static final AssetDescriptor<TextBox> START_TIME = declare("Start Time", TextBox.class, By.id("jobsForm:startTime"));
    public static final AssetDescriptor<CheckBox> EXECUTE_ETERNALLY = declare("Execute Eternally", CheckBox.class);
    public static final AssetDescriptor<Button> ADD_TRIGGER = declare("Add Trigger", Button.class, By.id("jobsForm:addTrigger"));
}
