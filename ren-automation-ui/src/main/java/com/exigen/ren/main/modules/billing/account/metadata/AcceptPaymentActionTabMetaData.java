/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.metadata;

import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class AcceptPaymentActionTabMetaData extends MetaData {
    public static final AssetDescriptor<AssetList> MANAGE_PAYMENT_METHODS = declare("Manage Payment Methods", AssetList.class, ManagePaymentMethods.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<ComboBox> PAYMENT_METHOD = declare("Payment Method", ComboBox.class);
    public static final AssetDescriptor<TextBox> AMOUNT = declare("Amount", TextBox.class);
    public static final AssetDescriptor<RadioGroup> SUSPEND_REMAINING = declare("Suspend Remaining?", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> INVOICE = declare("Invoice", ComboBox.class);
    public static final AssetDescriptor<TextBox> CHECK_NUMBER = declare("Check Number", TextBox.class);
    public static final AssetDescriptor<TextBox> PAYEE_NAME = declare("Payee Name", TextBox.class);
    public static final AssetDescriptor<TextBox> ALLOCATIONS = declare("Allocations", TextBox.class);
    public static final AssetDescriptor<TextBox> CHECK_DATE = declare("Check Date", TextBox.class);
    public static final AssetDescriptor<TextBox> REFERENCE = declare("Reference #", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDITIONAL_INFORMATION = declare("Additional Information", TextBox.class);
    public static final AssetDescriptor<RadioButton> ALLOCATE_EXISTING_SUSPENSE =
            declare("Allocate existing Suspense", RadioButton.class, By.id("paymentForm:createNewPayment_radio:1"));
    public static final AssetDescriptor<RadioButton> UPDATE_LIVES_VOLUME_NO =
            declare("Update Lives/Volume? NO", RadioButton.class, By.id("paymentForm:updateLivesVolume_radio:1"));
    public static final AssetDescriptor<TextBox> POLICY_ONE = declare("Policy One", TextBox.class, By.id("paymentForm:amount_0"));
    public static final AssetDescriptor<TextBox> POLICY_TWO = declare("Policy Two", TextBox.class, By.id("paymentForm:amount_1"));
    public static final AssetDescriptor<TextBox> POLICY_THREE = declare("Policy Three", TextBox.class, By.id("paymentForm:amount_2"));
    public static final AssetDescriptor<Link> ADVANCED_ALLOCATION = declare("Advanced Allocation", Link.class, By.id("paymentForm:openAdvAllocationLnk"));
    public static final AssetDescriptor<TextBox> TAX3 = declare("TAX1/3", TextBox.class);
    public static final AssetDescriptor<TextBox> TAX4 = declare("TAX1/4", TextBox.class);
    public static final AssetDescriptor<TextBox> TAX6 = declare("TAX1/6", TextBox.class);
    public static final AssetDescriptor<TextBox> TAX7 = declare("TAX1/7", TextBox.class);
    public static final AssetDescriptor<TextBox> NET_PREMIUM = declare("Net Premium", TextBox.class);
    public static final AssetDescriptor<RadioGroup> SELECT_ACTION = declare("Select Action", RadioGroup.class);
    public static final AssetDescriptor<TextBox> ROUTING_TRANSIT_NUMBER = declare("Routing#/Transit #", TextBox.class);
    public static final AssetDescriptor<TextBox> ACCOUNT_NUMBER = declare("Account #", TextBox.class);
    public static final AssetDescriptor<TextBox> BANK_NAME = declare("Bank Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> BANK_ACCOUNT_TYPE = declare("Bank Account Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> NAME_TYPE = declare("Name Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
    public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
    public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
    public static final AssetDescriptor<Button> ADDRESS_VALIDATED = declare("Address validated?", Button.class);
    public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> NUMBER = declare("Number", TextBox.class);
    public static final AssetDescriptor<TextBox> CARD_HOLDER_NAME = declare("Card Holder Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> CARD_EXPIRATION_MONTH = declare("Card Expiration Month", ComboBox.class, By.id("paymentForm:expMonth"));
    public static final AssetDescriptor<ComboBox> CARD_EXPIRATION_YEAR = declare("Card Expiration Year", ComboBox.class, By.id("paymentForm:expYear"));


    public static final class ManagePaymentMethods extends MetaData {
        public static final AssetDescriptor<Link> ADD_PAYMENT_METHOD = declare("Add Payment Method", Link.class, By.id("paymentForm:addPaymentMethodButton"));
        public static final AssetDescriptor<ComboBox> PAYMENT_METHOD = declare("Payment Method", ComboBox.class);
        public static final AssetDescriptor<AssetList> CARD_BILLING_ADDRESS = declare("Card Billing Address", AssetList.class, CardBillingAddress.class, false, By.xpath("//span[@id='paymentMethodForm:paymentMethodDetailsAddressForm']"));
        public static final AssetDescriptor<AssetList> GENERAL = declare("General", AssetList.class, General.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
        public static final AssetDescriptor<ComboBox> BANK_ACCOUNT_TYPE = declare("Bank Account Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> TRANSIT_NUMBER = declare("Routing#/Transit #", TextBox.class);
        public static final AssetDescriptor<TextBox> ACCOUNT_NUMBER = declare("Account #", TextBox.class);
        public static final AssetDescriptor<TextBox> BANK_NAME = declare("Bank Name", TextBox.class);
        public static final AssetDescriptor<TextBox> PAYMENT_METHOD_EFFECTIVE_DATE = declare("Payment Method Effective Date", TextBox.class);
        public static final AssetDescriptor<TextBox> PAYMENT_METHOD_EXPIRATION_DATE = declare("Payment Method Expiration Date", TextBox.class);
        public static final AssetDescriptor<ComboBox> NAME_TYPE = declare("Name Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class);
        public static final AssetDescriptor<ComboBox> PREFIX = declare("Prefix", ComboBox.class);
        public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
        public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
        public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
        public static final AssetDescriptor<RadioGroup> DOES_THE_BANK_ACCOUNT_BELONG_TO_THE_INSURED = declare("Does the Bank Account belong to the Insured?", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> DOES_THE_CREDIT_CARD_BELONG_TO_THE_INSURED = declare("Does the Credit Card belong to the Insured?", RadioGroup.class);
        public static final AssetDescriptor<AssetList> BILLING_MAILING_ADDRESS = declare("Billing Mailing Address", AssetList.class, BillingMailingAddress.class,false, By.xpath("//span[contains(@id,'billingMailingAddressForm')]"));
        public static final AssetDescriptor<ComboBox> PAYORS_NAME_TYPE = declare("Payor's Name Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> PAYORS_NAME = declare("Payor's Name", TextBox.class);
        public static final AssetDescriptor<ComboBox> PAYORS_PREFIX = declare("Payor's Prefix", ComboBox.class);
        public static final AssetDescriptor<TextBox> PAYORS_FIRST_NAME = declare("Payor's First Name", TextBox.class);
        public static final AssetDescriptor<TextBox> PAYORS_MIDDLE_NAME = declare("Payor's Middle Name", TextBox.class);
        public static final AssetDescriptor<TextBox> PAYORS_LAST_NAME = declare("Payor's Last Name", TextBox.class);
        public static final AssetDescriptor<Button> ADD_UPDATE = declare("Add/Update", Button.class, By.xpath("//input[@value='Add/Update']"));
        public static final AssetDescriptor<Button> BACK = declare("Back", Button.class, By.id("primaryButtonsForm:backButton_footer"));
    }

    public static final class CardBillingAddress extends MetaData {
        public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
        public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
        public static final AssetDescriptor<Button> ADDRESS_VALIDATED = declare("Address validated?", Button.class);
    }

    public static final class General extends MetaData {
        public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> NUMBER = declare("Number", TextBox.class);
        public static final AssetDescriptor<TextBox> CARD_HOLDER_NAME = declare("Card Holder Name", TextBox.class);
        public static final AssetDescriptor<ComboBox> CARD_EXPIRATION_MONTH = declare("Card Expiration Month", ComboBox.class, By.id("paymentMethodForm:expMonth"));
        public static final AssetDescriptor<ComboBox> CARD_EXPIRATION_YEAR = declare("Card Expiration Year", ComboBox.class, By.id("paymentMethodForm:expYear"));
    }

    public static final class BillingMailingAddress extends MetaData {
        public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
        public static final AssetDescriptor<TextBox> STATE_PROVINCE = declare("State / Province", TextBox.class);
        public static final AssetDescriptor<ComboBox> STATE_PROVINCE_COMBOBOX = declare("State / Province ComboBox", ComboBox.class, By.xpath(".//select[contains(@id, 'billingMailingAddress_stateProvCd')]"));
        public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
    }

}
