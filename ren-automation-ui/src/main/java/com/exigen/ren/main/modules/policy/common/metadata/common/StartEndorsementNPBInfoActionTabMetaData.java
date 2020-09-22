package com.exigen.ren.main.modules.policy.common.metadata.common;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class StartEndorsementNPBInfoActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> ENDORSEMENT_DATE = declare("Endorsement Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> ENDORSEMENT_REASON = declare("Endorsement Reason", ComboBox.class);
    public static final AssetDescriptor<TextBox> OTHER_REASON = declare("Other Reason", TextBox.class);
}
