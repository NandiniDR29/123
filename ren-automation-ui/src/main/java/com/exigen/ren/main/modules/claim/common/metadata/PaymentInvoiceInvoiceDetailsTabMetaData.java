/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class PaymentInvoiceInvoiceDetailsTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> REFERENCE = declare("Reference #", TextBox.class);
    public static final AssetDescriptor<TextBox> AMOUNT = declare("Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> INVOICE_DATE = declare("Invoice Date", TextBox.class);
    public static final AssetDescriptor<TextBox> PAYMENT_DUE_DATE = declare("Payment Due Date", TextBox.class);
    public static final AssetDescriptor<TextBox> INVOICE_MEMO = declare("Invoice Memo", TextBox.class);
}
