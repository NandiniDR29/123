/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_tl.metadata;

import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class BenefitsAcceleratedDeathIncidentTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> LIFE_EXPECTANCY_MONTHS = declare("Life Expectancy (months)", TextBox.class);
    public static final AssetDescriptor<TextBox> FIRST_DATE_OF_TREATMENT = declare("First Date of Treatment", TextBox.class);
    public static final AssetDescriptor<TextBox> BENEFIT_AMOUNT_REQUESTED = declare("Benefit Amount Requested", TextBox.class);
    public static final AssetDescriptor<TextBox> REPORTED_DATE_OF_DIAGNOSIS = declare("Reported Date of Diagnosis", TextBox.class);
    public static final AssetDescriptor<TextBox> DATE_LIFE_EXPECTANCY_WAS_PRESCRIBED = declare("Date Life Expectancy was Prescribed", TextBox.class);
}
