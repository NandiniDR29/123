/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.exigen.ren.main.modules.policy.common.metadata.master;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class PremiumSummaryBindActionTabMetaData extends MetaData {

    public static final AssetDescriptor<TextBox> RATE_REQUEST_DATE = declare("Rate Request Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> BROCHURE_RATE_PROGRAM = declare("Brochure Rate Program", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ASO_FEE_BASIS = declare("ASO Fee Basis", ComboBox.class);
    public static final AssetDescriptor<TextBox> RATING_FORMULA = declare("Rating Formula", TextBox.class);
}
