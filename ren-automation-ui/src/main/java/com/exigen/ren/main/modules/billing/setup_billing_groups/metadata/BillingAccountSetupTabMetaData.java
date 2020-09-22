/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.setup_billing_groups.metadata;

import com.exigen.istf.webdriver.controls.CheckBox;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class BillingAccountSetupTabMetaData extends MetaData {

    public static final AssetDescriptor<CheckBox> GENERATE_ADDITIONAL_SPREADSHEET_FORMAT_BILL = declare("Generate Additional Spreadsheet Format Bill", CheckBox.class);
    public static final AssetDescriptor<TextBox> OVERRIDE_GRACE_PERIOD_TO_X_DAYS_AFTER_INVOICE_DUE_DATE = declare("Override grace period to X days after invoice due date", TextBox.class);
    public static final AssetDescriptor<RadioGroup> SELECT_ACTION = declare("Select Action", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> BILL_UNDER_ACCOUNT = declare("Bill Under Account", ComboBox.class);

}