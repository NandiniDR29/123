/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class PolicyInformationPolicyActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> POLICY = declare("Policy #", TextBox.class);
    public static final AssetDescriptor<TextBox> REPORTED_POLICY = declare("Reported Policy #", TextBox.class);
    public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
    public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
    public static final AssetDescriptor<TextBox> ORIGINAL_EFFECTIVE_DATE = declare("Original Effective Date", TextBox.class);
    public static final AssetDescriptor<TextBox> CUSTOMER = declare("Customer #", TextBox.class);
    public static final AssetDescriptor<TextBox> LOB = declare("LOB", TextBox.class);
    public static final AssetDescriptor<TextBox> PRODUCT = declare("Product", TextBox.class);
    public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
    public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
}
