/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.ipb.eisa.controls.AutoCompleteBox;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class EventInformationLossEventActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> REPORTED_DATE_OF_LOSS = declare("Reported Date of Loss", TextBox.class);
    public static final AssetDescriptor<TextBox> REPORTED_CAUSE_OF_LOSS = declare("Reported Cause of Loss", TextBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION_OF_LOSS = declare("Description of Loss", TextBox.class);
    public static final AssetDescriptor<TextBox> PROOF_OF_LOSS_RECEIVED = declare("Proof of Loss Received", TextBox.class);
    public static final AssetDescriptor<AutoCompleteBox> ICD_CODE = declare("ICD Code", AutoCompleteBox.class);
    public static final AssetDescriptor<TextBox> REPORTED_UNDERLYING_CONDITIONS = declare("Reported Underlying Conditions", TextBox.class);

    public static final AssetDescriptor<TextBox> DATE_OF_LOSS = declare("Date of Loss", TextBox.class);
    public static final AssetDescriptor<TextBox> TIME_OF_LOSS = declare("Time of Loss", TextBox.class);
    public static final AssetDescriptor<ComboBox> LOSS_LOCATION = declare("Loss Location", ComboBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION_OF_LOSS_LOCATION = declare("Description of Loss Location", TextBox.class);
    public static final AssetDescriptor<ComboBox> CAUSE_OF_LOSS = declare("Cause of Loss", ComboBox.class);
    public static final AssetDescriptor<TextBox> SECONDARY_CAUSE_OF_LOSS = declare("Secondary Cause of Loss", TextBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION_OF_LOSS_EVENT = declare("Description of Loss Event", TextBox.class);
    public static final AssetDescriptor<TextBox> ROAD_CONDITION = declare("Road Condition", TextBox.class);
    public static final AssetDescriptor<TextBox> WEATHER_CONDITION = declare("Weather Condition", TextBox.class);
    public static final AssetDescriptor<TextBox> GENERAL_COMMENTS = declare("General Comments", TextBox.class);
    public static final AssetDescriptor<ComboBox> CLAIM_TYPE = declare("Claim Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> CONTRIBUTING_FACTOR = declare("Contributing Factor", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
    public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
    public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
    public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class);
}
