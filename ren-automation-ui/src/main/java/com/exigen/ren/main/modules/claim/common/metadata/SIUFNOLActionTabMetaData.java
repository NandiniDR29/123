/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class SIUFNOLActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> LOSS_OCCURS_BEFORE_EXPIRATION_OF_THE_POLICY_DAYS =
            declare("Loss occurs before expiration of the policy (days)", TextBox.class);
    public static final AssetDescriptor<TextBox> CURRENT_POLICY_EFFECTIVE_DAYS = declare("Current policy effective (days)", TextBox.class);
    public static final AssetDescriptor<TextBox> FNOL_DELAY_OVER_DAYS = declare("FNOL delay over (days)", TextBox.class);
    public static final AssetDescriptor<RadioGroup> COVERAGE_WAS_RECENTLY_INCREASED = declare("Coverage was recently increased", RadioGroup.class);
}
