/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.main.modules.mywork.metadata;

import com.exigen.ipb.eisa.controls.ClickComboBox;
import com.exigen.ipb.eisa.controls.RichTextBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;

public class UpdateTaskActionTabMetaData extends MetaData {
    public static final AssetDescriptor<ClickComboBox> PRIORITY = declare("Priority", ClickComboBox.class, Waiters.DEFAULT);
    public static final AssetDescriptor<TextBox> WARNING_DATE_TIME = declare("Warning Date/Time", TextBox.class);
    public static final AssetDescriptor<TextBox> DUE_DATE_TIME = declare("Due Date/Time", TextBox.class);
    public static final AssetDescriptor<RichTextBox> DESCRIPTION = declare("Task Description", RichTextBox.class);
    public static final AssetDescriptor<RadioGroup> SUSPEND = declare("Suspend?", RadioGroup.class);
    public static final AssetDescriptor<ClickComboBox> SUSPENSE_REASON = declare("Suspense Reason", ClickComboBox.class);
    public static final AssetDescriptor<TextBox> TASK_SUSPENSE_END_DATE = declare("Task Suspense End Date", TextBox.class);
    public static final AssetDescriptor<RichTextBox> NOTE = declare("Task Note", RichTextBox.class);
}
