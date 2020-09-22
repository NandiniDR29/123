/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class EnrollmentTabMetaData extends MetaData {

    public static final AssetDescriptor<AdvancedSelector> PLANNED_ENROLLMENT_COMMUNICATION_CHANNELS = declare("Planned Enrollment Communication Channels", AdvancedSelector.class);
    public static final AssetDescriptor<TextBox> ENROLLMENT_CONSULTANT_MANAGER = declare("Enrollment Consultant/Manager", TextBox.class);
    public static final AssetDescriptor<AdvancedSelector> REQUIRED_ENROLLMENT_RESPONSE = declare("Required Enrollment Response", AdvancedSelector.class);
    public static final AssetDescriptor<TextBox> ENROLLMENT_RESPONSE_REQUIRED_DATE = declare("Enrollment Response Required Date", TextBox.class);
    public static final AssetDescriptor<TextBox> COMPETING_INFLUENCES = declare("Competing Influences", TextBox.class);
}
