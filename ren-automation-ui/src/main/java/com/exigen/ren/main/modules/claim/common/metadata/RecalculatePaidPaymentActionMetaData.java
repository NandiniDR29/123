package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.RadioButton;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class RecalculatePaidPaymentActionMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> PAYMENT_THROUGH_DATE = declare("Payment Through Date", TextBox.class);
}
