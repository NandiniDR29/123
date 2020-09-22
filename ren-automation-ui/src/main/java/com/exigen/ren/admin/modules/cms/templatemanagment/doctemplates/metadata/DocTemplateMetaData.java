/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cms.templatemanagment.doctemplates.metadata;

import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.CheckBox;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class DocTemplateMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> SUBSYSTEM = declare("Subsystem", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PRODUCT = declare("Product", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ENTITY = declare("Entity", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ENGINE = declare("Engine", ComboBox.class);
    public static final AssetDescriptor<ComboBox> INDEXING_ROOT = declare("Indexing Root", ComboBox.class);
    public static final AssetDescriptor<ComboBox> FOLDER = declare("Folder", ComboBox.class);
    public static final AssetDescriptor<ComboBox> DOCUMENT_TYPE = declare("Document Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PAPER_TYPE = declare("Paper Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> TEMPLATE_URI = declare("Template Uri", TextBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
    public static final AssetDescriptor<TextBox> GENERATED_DOCUMENT_NAME = declare("Generated Document Name", TextBox.class);
    public static final AssetDescriptor<CheckBox> IS_ACTIVE = declare("Is Active", CheckBox.class);
    public static final AssetDescriptor<CheckBox> CAN_GENERATE_ON_DEMAND = declare("Can Generate On Demand", CheckBox.class);
    public static final AssetDescriptor<CheckBox> MAY_DELIVER_AS_PRINT_TO_BATCH = declare("Print to Batch", CheckBox.class);
    public static final AssetDescriptor<CheckBox> MAY_DELIVER_AS_FAX = declare("Fax", CheckBox.class);
    public static final AssetDescriptor<CheckBox> MAY_DELIVER_AS_EMAIL = declare("EMail", CheckBox.class);
    public static final AssetDescriptor<CheckBox> MAY_DELIVER_AS_SMS = declare("SMS", CheckBox.class);

    public static class NewTamplateId extends MetaData{
        public static final AssetDescriptor<TextBox> TEMPLATE_ID = declare("Template ID(Name)", TextBox.class, Waiters.DEFAULT, false);

        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false,
                By.id("selectionForm:addTemplate"));
        public static final AssetDescriptor<Button> DEFAULT_POPUP_SUBMITTER_NAME = declare(AbstractDialog.getDefaultPopupSubmitterName(), Button.class, Waiters.DEFAULT, false,
                By.id("addTemplatePanelForm:createNewTemplate"));
    }
}
