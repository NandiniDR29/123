/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class UpdatePaymentPlanActionTabMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> PAYMENT_PLAN = declare("Payment Plan:", ComboBox.class);
    public static final AssetDescriptor<TextBox> CASH_AMOUNT = declare("Cash Amount",
            TextBox.class,By.id("billingActionForm:downpaymentComponent_PaymentDetailsTable:0:downpaymentComponent_amount"));
}
