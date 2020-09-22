/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.common.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiter;
import com.exigen.istf.webdriver.controls.waiters.Waiters;

public class AgencyVendorSearchMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> STATUS = declare("Status", ComboBox.class, Waiters.DEFAULT);
    public static final AssetDescriptor<TextBox> AGENCY_CODE = declare("Agency Code", TextBox.class, Waiters.DEFAULT);
    public static final AssetDescriptor<TextBox> AGENCY_NAME = declare("Agency Name", TextBox.class);
    public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
    public static final AssetDescriptor<TextBox> STATE = declare("State", TextBox.class);
    public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
    public static final AssetDescriptor<TextBox> TAX_ID = declare("Tax Id", TextBox.class);
    public static final AssetDescriptor<ComboBox> PRIMARY_AGENCY = declare("Primary Agency", ComboBox.class);
    public static final AssetDescriptor<TextBox> VENDOR_NAME = declare("Vendor Name", TextBox.class);

    public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
    public static final AssetDescriptor<TextBox> STATE_PROVINCE = declare("State / Province", TextBox.class);
}
