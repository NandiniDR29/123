/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class PaymentActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> PAYMENT_NUMBER = declare("Payment Number", TextBox.class);
    public static final AssetDescriptor<TextBox> REFERENCE = declare("Reference #", TextBox.class);
    public static final AssetDescriptor<TextBox> GROSS_AMOUNT = declare("Gross Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> NET_AMOUNT = declare("Net Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> PAYMENT_TO = declare("Payment To", TextBox.class);
    public static final AssetDescriptor<TextBox> PAYMENT_ISSUE_DATE = declare("Payment Issue Date", TextBox.class);
    public static final AssetDescriptor<TextBox> PERFORMER = declare("Performer", TextBox.class);
    public static final AssetDescriptor<TextBox> PAYMENT_COMMENT = declare("Payment comment", TextBox.class);
    public static final AssetDescriptor<RadioGroup> FINAL_PAYMENT = declare("Final Payment", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> PAYMENT_METHOD = declare("Payment Method", ComboBox.class);
    public static final AssetDescriptor<TextBox> CHECK_NUMBER = declare("Check #", TextBox.class);
    public static final AssetDescriptor<RadioGroup> PAYMENT_SPECIAL_HANDLING = declare("Payment Special Handling", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> EX_GRATIA_PAYMENT = declare("Ex Gratia Payment", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> REASON_FOR_EX_GRATIA_REQUEST = declare("Reason for Ex Gratia Request", ComboBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION_OF_EX_GRATIA_REQUEST = declare("Description of Ex Gratia Request", TextBox.class);
    public static final AssetDescriptor<TextBox> ALLOCATIONS_AMOUNT = declare("Allocations Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> ATTENTION = declare("Attention", TextBox.class);
    public static final AssetDescriptor<ComboBox> DELIVERY_METHOD = declare("Delivery Method", ComboBox.class);
    public static final AssetDescriptor<TextBox> PAYMENT_DELIVERY_ADDRESS = declare("Payment Delivery Address", TextBox.class);
    public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
    public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
    public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
    public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class);
    public static final AssetDescriptor<TextBox> DAMAGE = declare("Damage", TextBox.class);
    public static final AssetDescriptor<TextBox> RESERVE_TYPE = declare("Reserve Type", TextBox.class);
    public static final AssetDescriptor<TextBox> ALLOCATION_AMOUNT = declare("Allocation Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> ALLOCATION_TYPE = declare("Allocation Type", TextBox.class);
    public static final AssetDescriptor<TextBox> NOTE = declare("Note", TextBox.class);
    public static final AssetDescriptor<TextBox> AMOUNT = declare("Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> PAYEE = declare("Payee", TextBox.class);
    public static final AssetDescriptor<TextBox> PAYMENT_STATUS_DATE = declare("Payment Status Date", TextBox.class);
    public static final AssetDescriptor<TextBox> ACTION = declare("Action", TextBox.class);
    public static final AssetDescriptor<TextBox> STATUS = declare("Status", TextBox.class);
    public static final AssetDescriptor<TextBox> DETAILS = declare("Details", TextBox.class);
    public static final AssetDescriptor<TextBox> REASON = declare("Reason", TextBox.class);
}
