/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class ResolutionDetailsActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> TYPE_OF_RESOLUTION = declare("Type of Resolution", ComboBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION_OF_RESOLUTION = declare("Description of Resolution", TextBox.class);
    public static final AssetDescriptor<TextBox> NUMBER_OF_OPEN_FEATURES = declare("Number of Open Features", TextBox.class);
    public static final AssetDescriptor<TextBox> NUMBER_OF_ACTIVE_PROCESSES = declare("Number of Active Processes", TextBox.class);
    public static final AssetDescriptor<TextBox> NUMBER_OF_ACTIVE_PAYMENTS = declare("Number of Active Payments", TextBox.class);
    public static final AssetDescriptor<TextBox> NUMBER_OF_COMPLETED_PAYMENTS = declare("Number of Completed Payments", TextBox.class);
    public static final AssetDescriptor<RadioGroup> AUTOMATICALLY_CLOSE_OPEN_ITEMS = declare("Automatically Close Open Items", RadioGroup.class);
}
