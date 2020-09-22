/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.main.modules.mywork.metadata;

import com.exigen.ipb.eisa.controls.ClickComboBox;
import com.exigen.ipb.eisa.controls.RichTextBox;
import com.exigen.ipb.eisa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class CreateTaskActionTabMetaData extends MetaData {
    public static final AssetDescriptor<ClickComboBox> TYPE = declare("Type", ClickComboBox.class, Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
    public static final AssetDescriptor<ClickComboBox> TASK_NAME = declare("Task Name", ClickComboBox.class, Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
    public static final AssetDescriptor<TextBox> REFERENCE_ID = declare("Reference ID", TextBox.class, Waiters.DEFAULT);
    public static final AssetDescriptor<ClickComboBox> PRIORITY = declare("Priority", ClickComboBox.class);
    public static final AssetDescriptor<StaticElement> CUSTOMER = declare("Customer", StaticElement.class);
    public static final AssetDescriptor<TextBox> WARNING_DATE_TIME = declare("Warning Date/Time", TextBox.class);
    public static final AssetDescriptor<TextBox> DUE_DATE_TIME = declare("Due Date/Time", TextBox.class);
    public static final AssetDescriptor<RichTextBox> DESCRIPTION = declare("Task Description", RichTextBox.class);
    public static final AssetDescriptor<RadioGroup> SUSPEND = declare("Suspend?", RadioGroup.class);
    public static final AssetDescriptor<StaticElement> QUEUE = declare("Queue", StaticElement.class);
    public static final AssetDescriptor<TextBox> TASK_SUSPENSE_END_DATE = declare("Task Suspense End Date", TextBox.class);
    public static final AssetDescriptor<ClickComboBox> SUSPENSE_REASON = declare("Suspense Reason", ClickComboBox.class);
    public static final AssetDescriptor<RichTextBox> NOTE = declare("Task Note", RichTextBox.class);
    public static final AssetDescriptor<DialogSingleSelector> ASSIGNED_TO = declare("Assigned To", DialogSingleSelector.class, AssignTo.class, false, By.xpath("//div[@id='taskAssignForm:taskAssignmentDialog']"));

    public static final class AssignTo extends MetaData {
        public static final AssetDescriptor<CheckBox> STAY_IN_ORIGINAL_QUEUE = declare("Stay in Original Queue", CheckBox.class);
        public static final AssetDescriptor<ClickComboBox> QUEUE = declare("Queue", ClickComboBox.class, Waiters.DEFAULT);
        public static final AssetDescriptor<TextBox> GROUP_NAME = declare("Group Name", TextBox.class);
        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false,
                By.xpath(".//*[contains(@id, 'Form:assignTask')]"));
    }
}
