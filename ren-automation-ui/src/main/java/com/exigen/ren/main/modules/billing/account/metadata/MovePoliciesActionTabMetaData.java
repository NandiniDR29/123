/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.metadata;

import com.exigen.istf.webdriver.controls.CheckBox;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class MovePoliciesActionTabMetaData extends MetaData {
    public static final AssetDescriptor<CheckBox> POLICIES = declare("Policy", CheckBox.class);
    public static final AssetDescriptor<CheckBox> CREATE_NEW_ACCOUNT = declare("Create New Account", CheckBox.class);
    public static final AssetDescriptor<TextBox> BILLING_ACCOUNT_NAME = declare("Billing Account Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> BILL_TYPE = declare("Bill Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> BILL_UNDER_ACCOUNT = declare("Bill Under Account", ComboBox.class);
    public static final AssetDescriptor<TextBox> BILLING_ACCOUNT_DUE_DAY = declare("Billing Account Due Day", TextBox.class);
    public static final AssetDescriptor<ComboBox> BILLING_ACCOUNT_NAME_TYPE = declare("Billing Account Name Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> BILLING_CONTACT_PREFIX = declare("Billing Contact Prefix", ComboBox.class);
    public static final AssetDescriptor<TextBox> BILLING_CONTACT_FIRST_NAME = declare("Billing Contact First Name", TextBox.class);
    public static final AssetDescriptor<TextBox> BILLING_CONTACT_MIDDLE_NAME = declare("Billing Contact Middle Name", TextBox.class);
    public static final AssetDescriptor<TextBox> BILLING_CONTACT_LAST_NAME = declare("Billing Contact Last Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
    public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
    public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
    public static final AssetDescriptor<TextBox> PHONE = declare("Phone #", TextBox.class);
    public static final AssetDescriptor<ComboBox> INVOICING_CALENDAR = declare("Invoicing Calendar", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PAYMENT_PLAN = declare("Payment Plan", ComboBox.class);
    public static final AssetDescriptor<CheckBox> CHANGE_DOWN_PAYMENT = declare("Change Down Payment", CheckBox.class);
    public static final AssetDescriptor<TextBox> MINIMUM_REQUIRED_AMOUNT = declare("Minimum Required Amount", TextBox.class);
    public static final AssetDescriptor<ComboBox> REASON_FOR_REDUCING = declare("Reason for Reducing", ComboBox.class);
    public static final AssetDescriptor<TextBox> CASH_AMOUNT = declare("Cash Amount", TextBox.class,
            By.id("policyMoveForm:downpaymentComponent_0_PaymentDetailsTable:0:downpaymentComponent_0_amount"));
}
