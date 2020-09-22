/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class BenefitsDiagnosisAndTreatmentIncidentTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> REPORTED_DATE_OF_ACCIDENT = declare("Reported Date of Accident", TextBox.class);
    public static final AssetDescriptor<TextBox> FIRST_DATE_OF_TREATMENT = declare("First Date of Treatment", TextBox.class);
    public static final AssetDescriptor<TextBox> NUMBER_OF_DAYS_HOSPITALIZED = declare("Number of Days Hospitalized", TextBox.class);
    public static final AssetDescriptor<TextBox> REPORTED_LOCATION_OF_ACCIDENT = declare("Reported Location of Accident", TextBox.class);
    public static final AssetDescriptor<ComboBox> AMBULANCE = declare("Ambulance", ComboBox.class);
    public static final AssetDescriptor<ComboBox> EMERGENCY_ROOM = declare("Emergency Room", ComboBox.class);
    public static final AssetDescriptor<TextBox> NUMBER_OF_DAYS_IN_ICU = declare("Number of Days in ICU", TextBox.class);
}
