/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.paymentsmaintenance.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class SearchSuspenseActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> SUSPENSE_REFERENCE = declare("Suspense Reference #", TextBox.class);
    public static final AssetDescriptor<ComboBox> PAYMENT_DESIGNATION = declare("Payment Designation", ComboBox.class);
    public static final AssetDescriptor<TextBox> BILLING_ACCOUNT = declare("Billing Account", TextBox.class);
}
