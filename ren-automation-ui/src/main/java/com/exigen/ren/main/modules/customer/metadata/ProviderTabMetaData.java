/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.metadata;

import com.exigen.ipb.eisa.controls.dialog.DialogSingleSelector;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class ProviderTabMetaData extends MetaData {
    public static final AssetDescriptor<DialogSingleSelector> SEARCH_PROVIDER = declare("Search Provider", DialogSingleSelector.class, AddProviderMetaData.class);
    public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
    public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
    public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
    public static final AssetDescriptor<StaticElement> PROVIDER_ID = declare("Provider ID", StaticElement.class);
    public static final AssetDescriptor<StaticElement> PROVIDER_NAME = declare("Provider Name", StaticElement.class);
    public static final AssetDescriptor<StaticElement> SPECIALTY = declare("Specialty", StaticElement.class);
    public static final AssetDescriptor<StaticElement> PRACTICE_ID = declare("Practice ID", StaticElement.class);
    public static final AssetDescriptor<StaticElement> PRACTICE_NAME = declare("Practice Name", StaticElement.class);
    public static final AssetDescriptor<StaticElement> ADDRESS = declare("Address", StaticElement.class);
    public static final AssetDescriptor<StaticElement> PHONE = declare("Phone", StaticElement.class);
}
