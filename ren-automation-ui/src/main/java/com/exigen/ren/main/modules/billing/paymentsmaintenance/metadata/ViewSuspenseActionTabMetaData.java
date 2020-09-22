/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.paymentsmaintenance.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class ViewSuspenseActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> REFERENCE = declare("Reference #:", TextBox.class);
    public static final AssetDescriptor<TextBox> CREATION_DATE_RANGE_FROM = declare("Creation Date Range From:", TextBox.class, By.id("suspenseListForm:dateFromInputDate"));
    public static final AssetDescriptor<TextBox> CREATION_DATE_RANGE_TO = declare("Creation Date Range To:", TextBox.class, By.id("suspenseListForm:dateToInputDate"));
    public static final AssetDescriptor<TextBox> PAYMENT_DESIGNATION_FROM = declare("Amount Range From:", TextBox.class, By.id("suspenseListForm:amountFrom"));
    public static final AssetDescriptor<TextBox> PAYMENT_DESIGNATION_TO = declare("Amount Range To:", TextBox.class, By.id("suspenseListForm:amountTo"));
    public static final AssetDescriptor<ComboBox> SUSPENSE_TYPE = declare("Suspense Type:", ComboBox.class);
    public static final AssetDescriptor<ComboBox> STATUS = declare("Status:", ComboBox.class);
}
