/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class ClaimHandlingSpecialHandlingActionTabMetaData extends MetaData {
    public static final AssetDescriptor<RadioGroup> CATASTROPHE = declare("Catastrophe", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> CATASTROPHE_CODE = declare("Catastrophe Code", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> FAST_TRACK_CLAIM = declare("Fast Track Claim?", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> EMPLOYEE = declare("Employee", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> VIP = declare("VIP", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> LARGE_LOSS_DEDUCTIBLE = declare("Large Loss Deductible", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> SECURE_CLAIM = declare("Secure Claim", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> LITIGATION = declare("Litigation", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> SALVAGE = declare("Salvage", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> TOTAL_LOSS = declare("Total Loss", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> COMPLAINT = declare("Complaint", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> DATE_RECEIVED = declare("Date Received", TextBox.class);
}
