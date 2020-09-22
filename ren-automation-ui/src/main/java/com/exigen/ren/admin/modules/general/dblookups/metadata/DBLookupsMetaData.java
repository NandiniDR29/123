/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.dblookups.metadata;

import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class DBLookupsMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> CODE = declare("code", TextBox.class);
    public static final AssetDescriptor<TextBox> VALUE_DEFAULT = declare("value (default)", TextBox.class);
    public static final AssetDescriptor<TextBox> VALUE_EN = declare("value_EN_US_", TextBox.class);
    public static final AssetDescriptor<TextBox> SENDER_ID = declare("senderId", TextBox.class);
    public static final AssetDescriptor<TextBox> CUSTOMER_ID = declare("customerId", TextBox.class);
}
