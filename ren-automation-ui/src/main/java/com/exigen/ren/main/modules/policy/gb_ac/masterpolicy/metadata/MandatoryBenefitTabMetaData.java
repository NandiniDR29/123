/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class MandatoryBenefitTabMetaData extends MetaData {

    public static final AssetDescriptor<ComboBox> INCOMPLETE_DISLOCATION = declare("Incomplete Dislocation or Without Anesthesia", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PARTIAL_DISLOCATION_BENEFIT_AMOUNT = declare("Partial Dislocation Benefit Amount", ComboBox.class);
}
