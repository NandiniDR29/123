package com.exigen.ren.main.modules.claim.gb_dn.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class FinancialPaymentPaymentDetailsActionTabMetaData extends MetaData {

    public static final AssetDescriptor<AssetList> PAYMENT_DETAILS = declare("Payment Details", AssetList.class, PaymentDetailsMetaData.class, By.id("policyDataGatherForm:componentView_ClaimsPayment"));
    public static final AssetDescriptor<AssetList> PAYMENT_DELIVERY_ADDRESS = declare("Payment Delivery Address", AssetList.class, PaymentDeliveryAddressMetaData.class, By.id("policyDataGatherForm:componentView_ClaimsPaymentAddressContact"));
    public static final AssetDescriptor<TextBox> REDUCTION_AMOUNT = declare("Reduction Amount", TextBox.class);
    public static final AssetDescriptor<AssetList> PAYMENT_INTEREST = declare("Payment Interest", AssetList.class, PaymentInterestMetaData.class, By.id("policyDataGatherForm:componentView_ClaimsPaymentCalculatorInterest"));

    public static final class PaymentDetailsMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> BENEFIT_AMOUNT = declare("Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> NET_AMOUNT = declare("Net Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> PAYEE = declare("Payee", ComboBox.class);
        public static final AssetDescriptor<TextBox> ORIGINAL_PAYEE = declare("Original Payee", TextBox.class);
        public static final AssetDescriptor<TextBox> PAYMENT_POST_DATE = declare("Payment Post Date", TextBox.class);
        public static final AssetDescriptor<TextBox> PAYMENT_MEMO = declare("Payment Memo", TextBox.class);
        public static final AssetDescriptor<ComboBox> EXTERNAL_PAYMENT_METHOD = declare("External Payment Method", ComboBox.class);
        public static final AssetDescriptor<TextBox> EXTERNAL_PAYMENT_NUM = declare("External Payment #", TextBox.class);
        public static final AssetDescriptor<TextBox> ISSUE_DATE = declare("Issue Date", TextBox.class);
        public static final AssetDescriptor<TextBox> CONSOLIDATED_PAYMENT = declare("Consolidated Payment #", TextBox.class);
    }

    public static final class PaymentDeliveryAddressMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> ATTENTION = declare("Attention", TextBox.class);
        public static final AssetDescriptor<ComboBox> DELIVERY_METHOD = declare("Delivery Method", ComboBox.class);
        public static final AssetDescriptor<ComboBox> PAYMENT_DELIVERY_ADDRESS = declare("Payment Delivery Address", ComboBox.class);
        public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
        public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
        public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class);
    }

    public static final class PaymentInterestMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class);
        public static final AssetDescriptor<TextBox> INTEREST_AMOUNT = declare("Interest Amount", TextBox.class, By.id("policyDataGatherForm:sedit_ClaimsPaymentCalculatorInterest_additionAmount"));
        public static final AssetDescriptor<TextBox> NUMBER_OF_DELAYED_DAYS = declare("Number of Delayed Days", TextBox.class);
    }

}