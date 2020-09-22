/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.setup_billing_groups.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class BillingGroupsEffectiveDateTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> BILLING_GROUPS_EFFECTIVE_DATE = declare("Billing Groups Effective Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
}