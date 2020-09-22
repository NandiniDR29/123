/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.catastrophe.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class CatastropheMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> CATASTROPHE_ID = declare("Catastrophe ID", TextBox.class);
    public static final AssetDescriptor<TextBox> CATASTROPHE_NAME = declare("Catastrophe Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> CATASTROPHE_TYPE = declare("Catastrophe Type", ComboBox.class);
    public static final AssetDescriptor<AdvancedSelector> IMPACTED_COUNTRIES = declare("Impacted Countries", AdvancedSelector.class);
    public static final AssetDescriptor<AdvancedSelector> IMPACTED_STATES = declare("Impacted States", AdvancedSelector.class);
    public static final AssetDescriptor<AdvancedSelector> IMPACTED_CITIES = declare("Impacted Cities", AdvancedSelector.class);
    public static final AssetDescriptor<AdvancedSelector> IMPACTED_ZIP_CODES = declare("Impacted Zip Codes", AdvancedSelector.class);
    public static final AssetDescriptor<TextBox> EVENT_START_DATE = declare("Event Start Date", TextBox.class);
    public static final AssetDescriptor<TextBox> EVENT_END_DATE = declare("Event End Date", TextBox.class);
}
