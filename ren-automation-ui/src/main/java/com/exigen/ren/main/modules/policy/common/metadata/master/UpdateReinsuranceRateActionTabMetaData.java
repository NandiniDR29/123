package com.exigen.ren.main.modules.policy.common.metadata.master;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class UpdateReinsuranceRateActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> SIC_CODE = declare("SIC Code", TextBox.class);
    public static final AssetDescriptor<TextBox> EFFECTIVE_DATE_CHANGE = declare("Effective Date of Change", TextBox.class);
    public static final AssetDescriptor<TextBox> MANUAL_REINSURANCE_RATE = declare("Manual Reinsurance Rate", TextBox.class);
    public static final AssetDescriptor<ComboBox> REINSURER = declare("Reinsurer", ComboBox.class);
    public static final AssetDescriptor<TextBox> FINAL_REINSURANCE_RATE = declare("Final Reinsurance Rate", TextBox.class);
    public static final AssetDescriptor<TextBox> COMMENTS = declare("Comments", TextBox.class);
}
