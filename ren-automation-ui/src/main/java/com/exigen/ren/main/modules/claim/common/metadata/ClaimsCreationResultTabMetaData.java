/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class ClaimsCreationResultTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> CLAIM_SEQ = declare("Claim Seq. #", TextBox.class);
    public static final AssetDescriptor<TextBox> CLAIM = declare("Claim #", TextBox.class);
    public static final AssetDescriptor<TextBox> CREATION_STATUS = declare("Creation Status", TextBox.class);
}
