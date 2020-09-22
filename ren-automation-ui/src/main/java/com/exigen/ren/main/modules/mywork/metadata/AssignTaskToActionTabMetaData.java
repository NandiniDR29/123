/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.main.modules.mywork.metadata;

import com.exigen.ipb.eisa.controls.RichTextBox;
import com.exigen.istf.webdriver.controls.CheckBox;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class AssignTaskToActionTabMetaData extends MetaData {
    public static final AssetDescriptor<CheckBox> STAY_IN_ORIGINAL_QUEUE = declare("Stay in Original Queue", CheckBox.class);
    public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
    public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> WORK_STATUS = declare("Work Status", ComboBox.class);
    public static final AssetDescriptor<ComboBox> AGENCY = declare("Agency", ComboBox.class);
    public static final AssetDescriptor<ComboBox> QUEUE = declare("Queue", ComboBox.class);
    public static final AssetDescriptor<RichTextBox> NOTE = declare("Task Note", RichTextBox.class, By.xpath("//div[@id='taskAssignForm:userNote']//iframe"));
}
