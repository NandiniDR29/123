package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class PaymentInquiryPaymentDetailsTabMetaData extends MetaData {
    public static final AssetDescriptor<StaticElement> COUNTRY_VALUE = declare("Country", StaticElement.class);
    public static final AssetDescriptor<StaticElement> ZIP_POSTAL_CODE_VALUE = declare("Zip / Postal Code", StaticElement.class);
    public static final AssetDescriptor<StaticElement> ADDRESS_LINE_1_VALUE = declare("Address Line 1", StaticElement.class);
    public static final AssetDescriptor<StaticElement> ADDRESS_LINE_2_VALUE = declare("Address Line 2", StaticElement.class);
    public static final AssetDescriptor<StaticElement> ADDRESS_LINE_3_VALUE = declare("Address Line 3", StaticElement.class);
    public static final AssetDescriptor<StaticElement> CITY_VALUE = declare("City", StaticElement.class);
}
