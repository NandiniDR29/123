/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

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

public class ClaimHandlingClaimFileOwnerActionTabMetaData extends MetaData {
    public static final AssetDescriptor<DialogSingleSelector> CLAIM_FILE_OWNER = declare("Claim File Owner", DialogSingleSelector.class,
            ClaimFileOwnerMetaData.class, By.id("fileOwnerSearchPopup_Claims_container"));

    public static final class ClaimFileOwnerMetaData extends MetaData {
        public static final AssetDescriptor<RadioGroup> ASSIGN_ADJUSTER = declare("Assign Adjuster", RadioGroup.class);
        public static final AssetDescriptor<TextBox> ROLE = declare("Role", TextBox.class);
        public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
        public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
        public static final AssetDescriptor<TextBox> OFFICE = declare("Office", TextBox.class);
        public static final AssetDescriptor<ComboBox> WORK_QUEUE = declare("Work queue", ComboBox.class);

        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false,
                By.id("policyDataGatherForm:claimFileOwnerSearchLink"));
    }
}
