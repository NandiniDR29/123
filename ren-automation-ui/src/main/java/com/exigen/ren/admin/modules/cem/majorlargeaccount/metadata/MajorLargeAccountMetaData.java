/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cem.majorlargeaccount.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class MajorLargeAccountMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> ACCOUNT_DESIGNATION_TYPE = declare("Account Designation Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> MAJOR_LARGE_ACCOUNT_ID = declare("Major/Large Account ID", TextBox.class);
    public static final AssetDescriptor<TextBox> ACCOUNT_NAME = declare("Account Name", TextBox.class);
    public static final AssetDescriptor<TextBox> SERVICE_LEVEL_CLASSIFICATION = declare("Service Level Classification", TextBox.class);
}
