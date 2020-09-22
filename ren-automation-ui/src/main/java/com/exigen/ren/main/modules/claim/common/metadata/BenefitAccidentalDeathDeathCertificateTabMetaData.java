/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class BenefitAccidentalDeathDeathCertificateTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> DATE_DEATH_CERTIFICATE_WAS_RECEIVED = declare("Date Death Certificate was Received", TextBox.class);
    public static final AssetDescriptor<TextBox> OFFICIAL_DATE_OF_DEATH = declare("Official Date of Death", TextBox.class);
    public static final AssetDescriptor<TextBox> OFFICIAL_TIME_OF_DEATH = declare("Official Time of Death", TextBox.class);
    public static final AssetDescriptor<ComboBox> OFFICIAL_MANNER_OF_DEATH = declare("Official Manner of Death", ComboBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION_FOR_OFFICIAL_MANNER_OF_DEATH = declare("Description for Official Manner of Death", TextBox.class);
    public static final AssetDescriptor<TextBox> OFFICIAL_CAUSE_OF_DEATH = declare("Official Cause of Death", TextBox.class);
    public static final AssetDescriptor<TextBox> UNDERLYING_CONDITIONS = declare("Underlying Conditions", TextBox.class);
    public static final AssetDescriptor<ComboBox> COUNTRY_OF_DEATH = declare("Country of Death", ComboBox.class);
    public static final AssetDescriptor<ComboBox> STATE_OF_DEATH = declare("State of Death", ComboBox.class);
    public static final AssetDescriptor<ComboBox> DECEDENT_NAME = declare("Decedent Name", ComboBox.class);
    public static final AssetDescriptor<ComboBox> DATE_OF_BIRTH = declare("Date of Birth", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SOCIAL_SECURITY_NUMBER = declare("Social Security Number", ComboBox.class);
    public static final AssetDescriptor<ComboBox> DECEDENT_ADDRESS = declare("Decedent Address", ComboBox.class);
}
