/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.scheme.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class CloseSchemeMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> CLOSE_EFFECTIVE_DATE = declare("Close Effective date", TextBox.class);
    public static final AssetDescriptor<ComboBox> SCHEME_CLOSE_REASON = declare("Scheme Close Reason", ComboBox.class);
}
