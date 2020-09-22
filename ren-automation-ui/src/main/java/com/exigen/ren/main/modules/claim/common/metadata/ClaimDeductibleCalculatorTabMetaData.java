/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class ClaimDeductibleCalculatorTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> TOTAL_ADDED_DEDUCTIBLE_AMOUNT = declare("Total Added Deductible Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> TOTAL_WAIVED_DEDUCTIBLE_AMOUNT = declare("Total Waived Deductible Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> CALCULATED_DEDUCTIBLE_AMOUNT = declare("Calculated Deductible Amount", TextBox.class);
}
