/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.paymentsmaintenance.metadata;

import com.exigen.ipb.eisa.controls.dialog.DialogMultiSelector;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.main.metadata.PaymentsMaintenanceMetaData;
import org.openqa.selenium.By;

public class ClearSuspenseActionTabMetaData extends MetaData {
    public static final AssetDescriptor<DialogMultiSelector> ALLOCATION = declare("Allocation", DialogMultiSelector.class,
            PaymentsMaintenanceMetaData.ClearSuspenseActionTab.AllocationMultiSelector.class);

    public static final class AllocationMultiSelector extends MetaData {
        public static final AssetDescriptor<TextBox> BILLING_ACCOUNT_NUMBER = declare("Billing Account #", TextBox.class);
        public static final AssetDescriptor<TextBox> POLICY_NUMBER = declare("Policy #", TextBox.class);
        public static final AssetDescriptor<ComboBox> BILLING_ACCOUNT_NAME_TYPE = declare("Billing Account Name Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
        public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
        public static final AssetDescriptor<TextBox> MORTGAGEE = declare("Mortgagee", TextBox.class);
        public static final AssetDescriptor<TextBox> AGENCY = declare("Agency", TextBox.class);
        public static final AssetDescriptor<TextBox> AGENCY_CODE = declare("Agency Code", TextBox.class);

        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class,
                Waiters.SLEEP(3000), false, By.id("suspenseForm:showPolicySearchPopupLnk"));
    }
}
