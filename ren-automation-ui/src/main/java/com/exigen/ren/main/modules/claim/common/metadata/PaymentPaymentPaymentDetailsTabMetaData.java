/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class PaymentPaymentPaymentDetailsTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> REFERENCE = declare("Reference #", TextBox.class);
    public static final AssetDescriptor<TextBox> GROSS_AMOUNT = declare("Gross Amount", TextBox.class);
    public static final AssetDescriptor<ComboBox> PAYMENT_TO = declare("Payment To", ComboBox.class);
    public static final AssetDescriptor<TextBox> CHECK_EFT_NUMBER = declare("Check # / EFT #", TextBox.class);

    public static final AssetDescriptor<ComboBox> PAYMENT_TYPE = declare("Non Provider Payment Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> TOTAL_AMOUNT = declare("Total Combined Check/EFT Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> CHECK_DATE = declare("Check Printed Date", TextBox.class);
    public static final AssetDescriptor<TextBox> COMBINED_NUMBER = declare("# of Combined Checks/EFT", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATE = declare("State Services Provided In", ComboBox.class);
    public static final AssetDescriptor<TextBox> ENCHANCED_PAYMENT_TYPE = declare("Enchanced Payment Type", TextBox.class);
    public static final AssetDescriptor<TextBox> NET_AMOUNT = declare("Net Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> PAYEE_NAME = declare("Payee Name", TextBox.class);
    public static final AssetDescriptor<TextBox> PAYMENT_POST_DATE = declare("Payment Post Date", TextBox.class);
    public static final AssetDescriptor<TextBox> REISSUE_DATE = declare("Reissue Date", TextBox.class,  By.id("policyDataGatherForm:sedit_ClaimsPayment_claimsBasePayment_reissueDateInputDate"));
    public static final AssetDescriptor<TextBox> PAYMENT_POST_TIME = declare("Payment Post Time", TextBox.class);
    public static final AssetDescriptor<ComboBox> PAYMENT_METHOD = declare("Payment Method", ComboBox.class);
    public static final AssetDescriptor<ComboBox> BANK_NAME = declare("Bank Name", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> MANUAL_CHECK_PAYMENT = declare("Manual Check Payment", RadioGroup.class);
    public static final AssetDescriptor<TextBox> CHECK = declare("Check #", TextBox.class);
    public static final AssetDescriptor<ComboBox> BANK_ACCOUNT_INFORMATION = declare("Bank Account Information", ComboBox.class);
    public static final AssetDescriptor<TextBox> BANK_ACCOUNT_NUMBER = declare("Bank Account Number", TextBox.class);
    public static final AssetDescriptor<TextBox> BANK_TRANSIT_ROUTING_NUMBER = declare("Bank Transit / Routing Number", TextBox.class);
    public static final AssetDescriptor<TextBox> BANK_CODE = declare("Bank Code", TextBox.class);
    public static final AssetDescriptor<TextBox> BANK_BRANCH = declare("Bank Branch", TextBox.class);
    public static final AssetDescriptor<TextBox> PAYMENT_MEMO = declare("Payment Memo", TextBox.class);
    public static final AssetDescriptor<RadioGroup> PAYMENT_SPECIAL_HANDLING = declare("Payment Special Handling", RadioGroup.class);
    public static final AssetDescriptor<TextBox> TOTAL_PAYMENT_ALLOCATION = declare("Total Payment Allocation", TextBox.class);
    public static final AssetDescriptor<ComboBox> TAXATION_TYPE = declare("Taxation Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ACCOUNT_TYPE = declare("Account Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> GL_ACCOUNT = declare("GL Account", TextBox.class);
    public static final AssetDescriptor<ComboBox> ESTIMATE_CODE = declare("Estimate Code", ComboBox.class);
    public static final AssetDescriptor<TextBox> TITLE_NUMBER = declare("Title Number", TextBox.class);
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
    public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
    public static final AssetDescriptor<TextBox> REASON = declare("Reason", TextBox.class);
    public static final AssetDescriptor<TextBox> DETAILS = declare("Details", TextBox.class);
}
