/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.commission.bulkadjustment.metadata;

import com.exigen.ipb.eisa.controls.dialog.DialogSingleSelector;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class BulkAdjustmentMetaData extends MetaData {
    public static final AssetDescriptor<DialogSingleSelector> PRODUCT_NAME = declare("Product Name", DialogSingleSelector.class, BulkAdjustmentMetaData.AddProduct.class);
    public static final AssetDescriptor<ComboBox> PRODUCT_CODE_NAME = declare("Product Code-Name", ComboBox.class);
    public static final AssetDescriptor<TextBox> ADJUSTMENT_START_DATE = declare("Adjustment Start Date", TextBox.class);
    public static final AssetDescriptor<TextBox> ADJUSTMENT_END_DATE = declare("Adjustment End Date", TextBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
    public static final AssetDescriptor<RadioGroup> SPECIFY_POLICIES = declare("Specify Policies", RadioGroup.class);

    public static final class AddProduct extends MetaData {
        public static final AssetDescriptor<TextBox> PRODUCT_NAME = declare("Product Name", TextBox.class);
    }
}
