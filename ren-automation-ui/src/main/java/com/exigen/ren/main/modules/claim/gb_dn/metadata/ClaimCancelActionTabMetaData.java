package com.exigen.ren.main.modules.claim.gb_dn.metadata;

import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class ClaimCancelActionTabMetaData extends MetaData {

    public static final AssetDescriptor<TextBox> DETAILS = declare("Details", TextBox.class);
}
