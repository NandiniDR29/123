/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class PolicyInformationScheduledItemTabMetaData extends MetaData {

    public static final AssetDescriptor<ComboBox> ASSOCIATED_POLICY_PARTY = declare("Associated Policy Party", ComboBox.class);
    public static final AssetDescriptor<TextBox> SCHEDULED_ITEM = declare("Scheduled Item", TextBox.class);
    public static final AssetDescriptor<TextBox> COVERAGE = declare("Coverage", TextBox.class);
    public static final AssetDescriptor<ComboBox> LIMIT_LEVEL = declare("Limit Level", ComboBox.class);
    public static final AssetDescriptor<TextBox> LIMIT_AMOUNT = declare("Limit Amount", TextBox.class);
}
