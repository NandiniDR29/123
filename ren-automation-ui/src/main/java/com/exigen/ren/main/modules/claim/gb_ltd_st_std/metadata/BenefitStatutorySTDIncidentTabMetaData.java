/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata;

import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class BenefitStatutorySTDIncidentTabMetaData extends MetaData {
    public static final AssetDescriptor<RadioGroup> ACCIDENT = declare("Accident?", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> HOSPITAL = declare("Hospital", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> SURGERY = declare("Surgery", RadioGroup.class);
    public static final AssetDescriptor<TextBox> DATE_OF_HIRE = declare("Date of Hire", TextBox.class);
}
