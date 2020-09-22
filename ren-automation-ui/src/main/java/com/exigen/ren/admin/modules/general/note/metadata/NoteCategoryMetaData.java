/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.note.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class NoteCategoryMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> STATUS = declare("Status", RadioGroup.class);
    public static final AssetDescriptor<TextBox> NEW_CATEGORY = declare("New Category", TextBox.class);
    public static final AssetDescriptor<TextBox> TITLE = declare("Title", TextBox.class);
    public static final AssetDescriptor<TextBox> QUICK_NOTE = declare("Quick Note", TextBox.class,
            By.name("quickNoteForm:quickNoteInfoPanel_userText_input"));
}
