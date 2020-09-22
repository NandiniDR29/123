package com.exigen.ren.main.modules.mywork.metadata;

import com.exigen.ipb.eisa.controls.RichTextBox;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class CompleteTaskDentalActionTabMetaData extends MetaData {

    public static final AssetDescriptor<StaticElement> TASK_ID = declare("Task ID", StaticElement.class);
    public static final AssetDescriptor<StaticElement> REFERENCE_ID = declare("Reference ID", StaticElement.class);
    public static final AssetDescriptor<StaticElement> PRIORITY = declare("Priority", StaticElement.class);
    public static final AssetDescriptor<ComboBox> CHOOSE_TO_COMPLETE = declare("Choose to Complete", ComboBox.class);
    public static final AssetDescriptor<RichTextBox> TASK_NOTE = declare("Task Note", RichTextBox.class);
    public static final AssetDescriptor<StaticElement> PROCESS_ID = declare("Process ID", StaticElement.class);
    public static final AssetDescriptor<StaticElement> TASK_NAME = declare("Task Name", StaticElement.class);
    public static final AssetDescriptor<TextBox> CLAIM_NUM = declare("Claim #", TextBox.class);
    public static final AssetDescriptor<StaticElement> TYPE = declare("Type", StaticElement.class);
    public static final AssetDescriptor<StaticElement> CUSTOMER = declare("Customer", StaticElement.class);

}
