package com.exigen.ren.admin.modules.commission.commissioncorrection.gbcommissioncorrection.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class ApplyManualCommissionCorrectionMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> SALES_CHANNEL = declare("Sales Channel", ComboBox.class);
}
