/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.commission.bulkadjustment.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class BulkAdjustmentRuleMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> SALES_CHANNEL = declare("Sales Channel", ComboBox.class);
    public static final AssetDescriptor<ComboBox> GEOGRAPHY_SELECTION = declare("Geography Selection", ComboBox.class);
    public static final AssetDescriptor<TextBox> NEW_BUSINESS_COMMISSION_RATE = declare("New Business Target Rate", TextBox.class);
    public static final AssetDescriptor<TextBox> ENDORSEMENT_COMMISSION_RATE = declare("Endorsement Target Rate", TextBox.class);
    public static final AssetDescriptor<TextBox> RENEWAL_COMMISSION_RATE = declare("Renewal Target Rate", TextBox.class, By.id("adjustmentTopForm:subseqRateAmountPercent_5"));
    public static final AssetDescriptor<TextBox> REINSTATEMENT_COMMISSION_RATE = declare("Reinstatement Target Rate", TextBox.class);
    public static final AssetDescriptor<TextBox> SPIN_COMMISSION_RATE = declare("Spin Target Rate", TextBox.class);
    public static final AssetDescriptor<TextBox> SPLIT_COMMISSION_RATE = declare("Split Target Rate", TextBox.class);
}
