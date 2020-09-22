/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class CommunicationActionTabMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> COMMUNICATION_CHANNEL = declare("Communication Channel", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ENTITY_TYPE = declare("Entity Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COMMUNICATION_DIRECTION = declare("Communication Direction", ComboBox.class);
    public static final AssetDescriptor<TextBox> ENTITY_REFERENCE_ID = declare("Entity Reference ID", TextBox.class);
    public static final AssetDescriptor<TextBox> SUBJECT = declare("Subject", TextBox.class);
    public static final AssetDescriptor<TextBox> COMMUNICATION_DESCRIPTION = declare("Communication Description", TextBox.class);
    public static final AssetDescriptor<TextBox> COMMUNICATION_OUTCOME = declare("Communication Outcome", TextBox.class);
    public static final AssetDescriptor<ComboBox> CATEGORY = declare("Category", ComboBox.class);
    public static final AssetDescriptor<ComboBox> STATUS = declare("Status", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SUB_CATEGORY = declare("Sub-Category", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SOURCE = declare("Source", ComboBox.class);
    public static final AssetDescriptor<ComboBox> LANGUAGE = declare("Language", ComboBox.class);
    public static final AssetDescriptor<ComboBox> INTERNAL_CALLER_ID = declare("Internal Caller ID", ComboBox.class);
}