package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class TaxActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> ID = declare("ID", TextBox.class);
    public static final AssetDescriptor<ComboBox> TAX_TYPE = declare("Tax Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> BEGINNING_DATE = declare("Beginning Date", TextBox.class);
    public static final AssetDescriptor<TextBox> TAX_AMOUNT = declare("Tax Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> THROUGH_DATE = declare("Through Date", TextBox.class);
}
