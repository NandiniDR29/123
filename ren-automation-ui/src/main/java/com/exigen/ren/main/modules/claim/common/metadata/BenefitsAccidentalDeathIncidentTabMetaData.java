/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class BenefitsAccidentalDeathIncidentTabMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> REPORTED_MANNER_OF_DEATH = declare("Reported Manner of Death", ComboBox.class);
    public static final AssetDescriptor<TextBox> REPORTED_CAUSE_OF_DEATH = declare("Reported Cause of Death", TextBox.class);
    public static final AssetDescriptor<TextBox> FIRST_DATE_OF_TREATMENT = declare("First Date of Treatment", TextBox.class);
    public static final AssetDescriptor<ComboBox> REPORTED_COUNTRY_OF_DEATH = declare("Reported Country of Death", ComboBox.class);
    public static final AssetDescriptor<ComboBox> REPORTED_STATE_OF_DEATH = declare("Reported State of Death", ComboBox.class);
    public static final AssetDescriptor<ComboBox> REPORTED_LOCATION_OF_DEATH = declare("Reported Location of Death", ComboBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION_OF_LOCATION_OF_DEATH = declare("Description of Location of Death", TextBox.class);
    public static final AssetDescriptor<TextBox> REPORTED_DATE_OF_ACCIDENT = declare("Reported Date of Accident", TextBox.class);
    public static final AssetDescriptor<ComboBox> AMBULANCE = declare("Ambulance", ComboBox.class);
    public static final AssetDescriptor<ComboBox> EMERGENCY_ROOM = declare("Emergency Room", ComboBox.class);
    public static final AssetDescriptor<ComboBox> HOSPITALIZED_OVERNIGHT = declare("Hospitalized Overnight", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SEAT_BELT = declare("Seat Belt", ComboBox.class);
    public static final AssetDescriptor<ComboBox> AIRBAG_DEPLOYED = declare("Airbag Deployed", ComboBox.class);
}
