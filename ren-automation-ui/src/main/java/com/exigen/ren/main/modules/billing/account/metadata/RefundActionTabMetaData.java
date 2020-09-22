/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.metadata;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class RefundActionTabMetaData extends MetaData {
    public static final AssetDescriptor<AssetList> MANAGE_PAYMENT_METHODS = declare("Manage Payment Methods", AssetList.class, RefundActionTabMetaData.ManagePaymentMethods.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<ComboBox> PAYMENT_METHOD = declare("Payment Method", ComboBox.class);
    public static final AssetDescriptor<TextBox> CHECK_NUMBER = declare("Check Number", TextBox.class);
    public static final AssetDescriptor<TextBox> CHECK_DATE = declare("Check Date", TextBox.class);
    public static final AssetDescriptor<TextBox> PAYEE_NAME = declare("Payee Name", TextBox.class);
    public static final AssetDescriptor<TextBox> REFERENCE = declare("Reference #", TextBox.class);
    public static final AssetDescriptor<TextBox> AMOUNT = declare("Amount", TextBox.class);
    public static final AssetDescriptor<ComboBox> REASON_FOR_REFUND = declare("Reason for Refund", ComboBox.class);
    public static final AssetDescriptor<TextBox> ADDITIONAL_INFORMATION = declare("Additional Information", TextBox.class);

    public static final class ManagePaymentMethods extends MetaData {
        public static final AssetDescriptor<Link> ADD_PAYMENT_METHOD = declare("Add Payment Method", Link.class, By.id("fullAdminManualRefundForm:addPaymentMethodButton"));
        public static final AssetDescriptor<ComboBox> PAYMENT_METHOD = declare("Payment Method", ComboBox.class);
        public static final AssetDescriptor<TextBox> TRANSIT_NUMBER = declare("Routing#/Transit #", TextBox.class);
        public static final AssetDescriptor<TextBox> ACCOUNT_NUMBER = declare("Account #", TextBox.class);
        public static final AssetDescriptor<TextBox> BANK_NAME = declare("Bank Name", TextBox.class);
        public static final AssetDescriptor<ComboBox> BANK_ACCOUNT_TYPE = declare("Bank Account Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> NAME_TYPE = declare("Name Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class);
        public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
        public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> NUMBER = declare("Number", TextBox.class);
        public static final AssetDescriptor<TextBox> CARD_HOLDER_NAME = declare("Card Holder Name", TextBox.class);
        public static final AssetDescriptor<ComboBox> CARD_EXPIRATION_MONTH = declare("Card Expiration Month", ComboBox.class, By.id("paymentMethodForm:expMonth"));
        public static final AssetDescriptor<ComboBox> CARD_EXPIRATION_YEAR = declare("Card Expiration Year", ComboBox.class, By.id("paymentMethodForm:expYear"));
        public static final AssetDescriptor<Button> ADD_UPDATE = declare("Add/Update", Button.class, By.xpath("//input[@id='paymentMethodEFTForm:saveBtn' or @id='paymentMethodForm:saveBtn']"));
        public static final AssetDescriptor<Button> BACK = declare("Back", Button.class, By.id("primaryButtonsForm:backButton_footer"));
    }
}
