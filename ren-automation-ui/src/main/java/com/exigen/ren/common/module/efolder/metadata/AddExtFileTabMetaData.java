package com.exigen.ren.common.module.efolder.metadata;

import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class AddExtFileTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> FILE_URL = declare("File URL", TextBox.class);
    public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class);
}
