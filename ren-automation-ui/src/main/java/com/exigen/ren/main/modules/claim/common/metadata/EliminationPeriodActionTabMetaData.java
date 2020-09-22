package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class EliminationPeriodActionTabMetaData extends MetaData {

    public static final AssetDescriptor<TextBox> ELIMINATION_PERIOD_START_DATE = declare("Elimination Period Start Date", TextBox.class);
    public static final AssetDescriptor<TextBox> ELIMINATION_PERIOD_END_DATE = declare("Elimination Period End Date", TextBox.class);
    public static final AssetDescriptor<TextBox> OVERRIDE_REASON = declare("Override Reason", TextBox.class);
}