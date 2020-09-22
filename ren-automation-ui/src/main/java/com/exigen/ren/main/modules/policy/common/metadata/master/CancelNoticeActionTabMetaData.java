/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.common.metadata.master;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class CancelNoticeActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> MAILING_DATE = declare("Mailing Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation Reason", ComboBox.class);
    public static final AssetDescriptor<TextBox> SUPPORTING_DATA = declare("Supporting Data", TextBox.class);
    public static final AssetDescriptor<TextBox> DAYS_OF_NOTICE = declare("Days Of Notice", TextBox.class);
    public static final AssetDescriptor<RadioGroup> PRINT_NOTICE = declare("Print Notice", RadioGroup.class);
}
