/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.main.modules.billing.account.metadata;

import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class AllocateSearchMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> BILLING_ACCOUNT = declare("Billing Account #", TextBox.class, Waiters.SLEEP(2000));
    public static final AssetDescriptor<TextBox> POLICY = declare("Policy #", TextBox.class, Waiters.SLEEP(2000));
    public static final AssetDescriptor<Button> BUTTON_CLOSE_POPUP = declare(AbstractDialog.getDefaultPopupCloserName(), Button.class, Waiters.DEFAULT, false,
            By.id("personalAccountSearch:selectBtn"));
    public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.SLEEP(1000), false,
            By.xpath("//span[.='Search for Allocation']"));
    public static final AssetDescriptor<Button> POPUP_SUBMITTER_NAME = declare(AbstractDialog.getDefaultPopupSubmitterName(), Button.class, Waiters.DEFAULT, false,
            By.xpath("//*[(self::button or self::input) and contains(@id, 'selectBtn') and not (@disabled)]"));
}
