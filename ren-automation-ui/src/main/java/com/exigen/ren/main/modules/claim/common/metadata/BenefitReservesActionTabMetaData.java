/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class BenefitReservesActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> OLD_INDEMNITY_RESERVE = declare("Old Indemnity Reserve", TextBox.class);
    public static final AssetDescriptor<TextBox> INDEMNITY_RESERVE = declare("Indemnity Reserve", TextBox.class);
    public static final AssetDescriptor<TextBox> OLD_EXPENSE_RESERVE = declare("Old Expense Reserve", TextBox.class);
    public static final AssetDescriptor<TextBox> EXPENSE_RESERVE = declare("Expense Reserve", TextBox.class);
    public static final AssetDescriptor<TextBox> OLD_RECOVERY_RESERVE = declare("Old Recovery Reserve", TextBox.class);
    public static final AssetDescriptor<TextBox> RECOVERY_RESERVE = declare("Recovery Reserve", TextBox.class);
}
