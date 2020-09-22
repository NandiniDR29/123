package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class DisapprovePaymentActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> DETAILS = declare("Details", TextBox.class);
}
