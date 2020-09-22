/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.billing.rules.write_off.benefits.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class WriteOffBenefitsSearchMetaData extends MetaData {

    public static final AssetDescriptor<TextBox> WRITE_OFF_DESCRIPTION = declare("Write-Off Description", TextBox.class);
    public static final AssetDescriptor<ComboBox> WRITE_OFF_TYPE = declare("Write-Off Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
}