/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.scheme.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class SchemeInfoMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> SCHEME_NAME = declare("Scheme Name", TextBox.class);
    public static final AssetDescriptor<TextBox> ACCOUNTING_SCHEME_CODE = declare("Accounting Scheme Code", TextBox.class);
    public static final AssetDescriptor<TextBox> SCHEME_MARKETING_NAME = declare("Scheme Marketing Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> COMPANY_NAME = declare("Company Name", ComboBox.class);
    public static final AssetDescriptor<ComboBox> DIVISION_NAME = declare("Division Name", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PARTNER_NAME = declare("Partner Name", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> PROFIT_SHARE = declare("Profit Share", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> SCHEME_TEMPLATE_COMPLETE = declare("Scheme Template Complete", RadioGroup.class);
}
