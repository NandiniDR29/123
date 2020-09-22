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

public class PaymentSeriesPaymentAllocationActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> DAMAGE = declare("Damage", TextBox.class);
    public static final AssetDescriptor<TextBox> COVERAGE = declare("Coverage", TextBox.class);
    public static final AssetDescriptor<TextBox> ASSOCIATED_INSURABLE_RISK = declare("Associated Insurable Risk", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATUS = declare("Status", ComboBox.class);
    public static final AssetDescriptor<TextBox> FEATURE_NUMBER = declare("Feature Number", TextBox.class);
    public static final AssetDescriptor<TextBox> TYPE_OF_PAYMENT = declare("Type of Payment", TextBox.class);
    public static final AssetDescriptor<RadioGroup> FINAL_PAYMENT = declare("Final Payment", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> SUPPLEMENTAL_PAYMENT = declare("Supplemental Payment", RadioGroup.class);
    public static final AssetDescriptor<TextBox> ALLOCATION_AMOUNT = declare("Allocation Amount", TextBox.class);
    public static final AssetDescriptor<RadioGroup> EX_GRATIA_PAYMENT = declare("Ex Gratia Payment", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> REASON_FOR_EX_GRATIA_REQUEST = declare("Reason for Ex Gratia Request", ComboBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION_OF_EX_GRATIA_REQUEST = declare("Description of Ex Gratia Request", TextBox.class);
    public static final AssetDescriptor<RadioGroup> ADJUST_RESERVE = declare("Adjust reserve", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> ALLOCATION_TYPE = declare("Allocation Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> TYPE_DESCRIPTION = declare("Type Description", TextBox.class);
    public static final AssetDescriptor<ComboBox> IN_LIEU_BENEFIT = declare("In Lieu Benefit", ComboBox.class);
}
