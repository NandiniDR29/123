/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.profile.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class BankingDetailsMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> SETTLEMENT_METHOD = declare("Settlement Method", ComboBox.class);
    public static final AssetDescriptor<TextBox> ACCOUNT_HOLDER_NAME = declare("Account Holder Name", TextBox.class);
    public static final AssetDescriptor<TextBox> ACCOUNT_NUMBER = declare("Account #", TextBox.class);
    public static final AssetDescriptor<TextBox> ABA_TRANSIT_NUMBER = declare("ABA Transit #", TextBox.class);
    public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
    public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> BANK_NAME = declare("Bank Name", ComboBox.class);
}
