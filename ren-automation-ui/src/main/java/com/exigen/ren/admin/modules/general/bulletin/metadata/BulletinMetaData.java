/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.bulletin.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.ipb.eisa.controls.RichTextBox;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class BulletinMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> BULLETIN_EFFECTIVE_DATE = declare("Bulletin Effective Date", TextBox.class);
    public static final AssetDescriptor<TextBox> BULLETIN_EXPIRATION_DATE = declare("Bulletin Expiration Date", TextBox.class);
    public static final AssetDescriptor<TextBox> BULLETIN_NAME = declare("Bulletin Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> PRIORITY = declare("Priority", ComboBox.class);
    public static final AssetDescriptor<AdvancedSelector> CHANNEL = declare("Channel", AdvancedSelector.class);
    public static final AssetDescriptor<AdvancedSelector> LOCATION_TYPE = declare("Location Type", AdvancedSelector.class);
    public static final AssetDescriptor<AdvancedSelector> LOCATION_NAME = declare("Location Name", AdvancedSelector.class);
    public static final AssetDescriptor<RichTextBox> BULLETIN_TEXT = declare("Bulletin Text", RichTextBox.class);
}
