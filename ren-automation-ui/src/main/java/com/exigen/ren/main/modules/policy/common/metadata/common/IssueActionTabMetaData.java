/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.common.metadata.common;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class IssueActionTabMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> METHOD_OF_DELIVERY = declare("Method Of Delivery", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SEND_TO = declare("Send To", ComboBox.class);
    public static final AssetDescriptor<TextBox> CORPORATE_SPONSOR_EMAIL = declare("Corporate Sponsor Email", TextBox.class);
    public static final AssetDescriptor<TextBox> ISSUE_DATE = declare("Issue Date", TextBox.class);
    public static final AssetDescriptor<TextBox> BROKER_EMAIL = declare("Broker Email", TextBox.class);
    public static final AssetDescriptor<TextBox> INSURED_EMAIL = declare("Insured Email", TextBox.class);
    public static final AssetDescriptor<TextBox> ATTENTION_OF = declare("Attention of", TextBox.class);
    public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
    public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip/Postal Code", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
    public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
    public static final AssetDescriptor<TextBox> NOTES = declare("Notes", TextBox.class);
}
