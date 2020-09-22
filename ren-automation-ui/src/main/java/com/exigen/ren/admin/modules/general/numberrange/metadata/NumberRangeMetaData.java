/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.numberrange.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class NumberRangeMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> RANGE_TYPE = declare("Range Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> START_NUMBER = declare("Start #", TextBox.class);
    public static final AssetDescriptor<TextBox> END_NUMBER = declare("End #", TextBox.class);
}
