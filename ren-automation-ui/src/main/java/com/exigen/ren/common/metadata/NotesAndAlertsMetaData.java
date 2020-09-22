/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.common.metadata;

import com.exigen.ipb.eisa.controls.RichTextBox;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class NotesAndAlertsMetaData {

    public static final class NotesAndAlertsTab extends MetaData {
        public static final AssetDescriptor<RadioGroup> NOTE_ALERT = declare("Note/Alert", RadioGroup.class,
                By.xpath("//table[@id='createNote_form:createNote_noteAlert']"));
        public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> CATEGORY = declare("Category", ComboBox.class);
        public static final AssetDescriptor<RadioGroup> CONFID = declare("Confid.", RadioGroup.class);
        public static final AssetDescriptor<TextBox> TITLE = declare("Title", TextBox.class);
        public static final AssetDescriptor<RichTextBox> NOTE = declare("Note", RichTextBox.class);
        public static final AssetDescriptor<RichTextBox> ALERT = declare("Alert", RichTextBox.class);

        public static Button buttonUpdate = new Button(By.xpath("(//input[(@value = 'Update' or @value = 'UPDATE') and not(@class = 'hidden') and not(contains(@style,'none'))])[last()]"));

        public static RadioButton alertRadioButton = new RadioButton(By.xpath("//table[@id='createNote_form:createNote_noteAlert']//input[@value='alert']"));
        public static StaticElement errorLabel = new StaticElement(By.xpath("//span[@class='ui-messages-error-summary']"));
        public static StaticElement labelAlert = new StaticElement(By.id("alertArchiveForm:alerts:0:arhiveAlert_id"));
        public static RadioButton noteRadioButton = new RadioButton(By.xpath("//table[@id='createNote_form:createNote_noteAlert']//input[@value='note']"));
    }

    public static final class ActivitiesAndUserNotesTab extends MetaData{
        public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class, By.id("activityCommandsForm:activityFilterTableForm:0:noteType"));
        public static final AssetDescriptor<ComboBox> CATEGORY = declare("Category", ComboBox.class);
        public static final AssetDescriptor<ComboBox> STATUS = declare("Status", ComboBox.class);
        public static final AssetDescriptor<TextBox> DATE_RANGE_START = declare("Date Range Start", TextBox.class, By.id("activityCommandsForm:activityFilterTableForm:0:startedTimeInputDate"));
        public static final AssetDescriptor<TextBox> DATE_RANGE_END = declare("Date Range End", TextBox.class, By.id("activityCommandsForm:activityFilterTableForm:0:endedTimeInputDate"));
        public static final AssetDescriptor<CheckBox> RETRIEVE_ARCHIVED = declare("Also retrieve archived records", CheckBox.class, By.id("activityCommandsForm:activityFilterTableForm:0:showHistory"));
    }
}
