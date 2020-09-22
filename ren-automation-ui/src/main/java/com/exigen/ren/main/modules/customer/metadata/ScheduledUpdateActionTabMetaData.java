/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.metadata;

import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class ScheduledUpdateActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> UPDATE_EFFECTIVE_DATE = declare("Update Effective Date", TextBox.class, Waiters.DEFAULT, false,
            By.id("customerScheduledUpdatePopupForm:customerScheduledUpdate_scheduledUpdateDateInputDate"));
    public static final AssetDescriptor<TextBox> RESCHEDULE_UPDATE_EFFECTIVE_DATE = declare("Reschedule Update Effective Date", TextBox.class, Waiters.DEFAULT, false,
            By.id("reschedulePendingUpdatePopupForm:reschedulePendingUpdate_scheduledUpdateDateInputDate"));
}
