/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.ipb.eisa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioButton;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class ClaimCopyActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> DATE_OF_LOSS = declare("Date of Loss", TextBox.class);
    public static final AssetDescriptor<TextBox> TIME_OF_LOSS = declare("Time of Loss", TextBox.class);
    public static final AssetDescriptor<TextBox> DETAILS = declare("Details", TextBox.class);
    public static final AssetDescriptor<DialogSingleSelector> POLICY = declare("Policy #", DialogSingleSelector.class, SelectPolicy.class);
    public static final AssetDescriptor<DialogSingleSelector> NO_POLICY = declare("No Policy", DialogSingleSelector.class, SelectNoPolicy.class);

    public static final class SelectPolicy extends MetaData {
        public static final AssetDescriptor<RadioButton> POLICY = declare("Policy #", RadioButton.class, Waiters.AJAX.then(Waiters.SLEEP(5000)), false,
                By.xpath("//div[contains(@class, 'ui-radiobutton-box')]"));
        public static final AssetDescriptor<ComboBox> STATUS = declare("Status", ComboBox.class, Waiters.DEFAULT, false,
                By.id("policySearchForm:policySearchResults:policyStatusColumn:filter"));
        public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class, Waiters.DEFAULT, false,
                By.id("policySearchForm:policySearchResults:policyEffectiveDateColumn:filter"));
        public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class, Waiters.DEFAULT, false,
                By.id("policySearchForm:policySearchResults:policyExpirationDateColumn:filter"));
        public static final AssetDescriptor<ComboBox> PRODUCT = declare("Product", ComboBox.class, Waiters.DEFAULT, false,
                By.id("policySearchForm:policySearchResults:policyProductColumn:filter"));
        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.AJAX.then(Waiters.SLEEP(5000)), false,
                By.id("policyDataGatherForm:policySearchLink"));
        public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.getDefaultPopupSubmitterName(), Button.class, Waiters.AJAX.then(Waiters.SLEEP(5000)), false,
                By.id("policySearchForm:selectPolicyAction"));
    }

    public static final class SelectNoPolicy extends MetaData {
        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false,
                By.id("policyDataGatherForm:policySearchLink"));
        public static final AssetDescriptor<Button> NO_POLICY = declare("No Policy", Button.class, Waiters.DEFAULT, false,
                By.id("policySearchForm:noPolicyLink"));
    }
}
