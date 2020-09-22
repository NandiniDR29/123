package com.exigen.ren.main.modules.claim.gb_dn.metadata;

import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class PaymentInquiryTabMetaData extends MetaData {

    public static final AssetDescriptor<AssetList> PAYMENT_DETAILS = declare("Payment Details",
            AssetList.class, PaymentDetailsSection.class, By.id("policyDataGatherForm:formGrid_ClaimsPayment"));
    public static final AssetDescriptor<AssetList> PAYMENT_DELIVERY_ADDRESS = declare("Payment Delivery Address",
            AssetList.class, PaymentDeliveryAddressSection.class, By.id("policyDataGatherForm:componentView_ClaimsPaymentAddressContact"));

    public static final class PaymentDetailsSection extends MetaData {
        public static final AssetDescriptor<StaticElement> PAYMENT_POST_DATE = declare("Payment Post Date", StaticElement.class);
        public static final AssetDescriptor<StaticElement> PAYMENT_MEMO = declare("Payment Memo", StaticElement.class);
        public static final AssetDescriptor<StaticElement> EXTERNAL_PAYMENT_METHOD = declare("External Payment Method", StaticElement.class);
        public static final AssetDescriptor<StaticElement> EXTERNAL_PAYMENT_NUM = declare("External Payment #", StaticElement.class);
        public static final AssetDescriptor<StaticElement> ISSUE_DATE = declare("Issue Date", StaticElement.class);
        public static final AssetDescriptor<StaticElement> ORIGINAL_PAYEE = declare("Original Payee", StaticElement.class);
        public static final AssetDescriptor<StaticElement> BENEFIT_AMOUNT = declare("Benefit Amount", StaticElement.class);
    }

    public static final class PaymentDeliveryAddressSection extends MetaData {
        public static final AssetDescriptor<StaticElement> ADDRESS_LINE_1 = declare("Address Line 1", StaticElement.class);
        public static final AssetDescriptor<StaticElement> ADDRESS_LINE_2 = declare("Address Line 2", StaticElement.class);
        public static final AssetDescriptor<StaticElement> ADDRESS_LINE_3 = declare("Address Line 3", StaticElement.class);
        public static final AssetDescriptor<StaticElement> CITY = declare("City", StaticElement.class);
        public static final AssetDescriptor<StaticElement> ZIP_POSTAL_CODE = declare("Zip / Postal Code", StaticElement.class);
    }

}
