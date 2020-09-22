/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.commission.commissionreferral.metadata;

import com.exigen.ipb.eisa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class CommissionReferralMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> PRODUCT_CODE_NAME = declare("Product Code-Name", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COMMISSION_PAYMENT_TRIGGER = declare("Commission Payment Trigger", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ROUNDING = declare("Rounding", ComboBox.class);
    public static final AssetDescriptor<ComboBox> TRANSACTION_DATES = declare("Payment Frequency", ComboBox.class);
    public static final AssetDescriptor<TextBox> DATE_FROM = declare("Effective Date", TextBox.class);
    public static final AssetDescriptor<TextBox> DATE_TO = declare("Expiration Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> SALES_CHANNEL = declare("Sales Channel", ComboBox.class);
    public static final AssetDescriptor<DialogSingleSelector> SALES_CHANNEL_DIALOG = declare("Sales Channel Select", DialogSingleSelector.class, CommissionReferralMetaData.SelectSalesChannel.class);

    public static final class SelectSalesChannel extends MetaData {
        public static final AssetDescriptor<RadioGroup> REFFERAL_PAYER = declare("Refferal Payer", RadioGroup.class, By.id("selectPopupForm:modeSelector"));
        public static final AssetDescriptor<TextBox> DIRECT = declare("Direct", TextBox.class);
        public static final AssetDescriptor<TextBox> AGENCY = declare("Agency", TextBox.class);

        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false,
                By.id("referralTopForm:selectChannelPopupBtn"));
    }
}
