package com.exigen.ren.common.module.efolder.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class ReindexFileTabMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> FOLDER = declare("Folder", ComboBox.class);
}
