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

public class LossContextTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> CUSTOMER = declare("Customer #", TextBox.class);
    public static final AssetDescriptor<DialogSingleSelector> POLICY = declare("Policy #", DialogSingleSelector.class, SelectPolicy.class);
    public static final AssetDescriptor<DialogSingleSelector> NO_POLICY = declare("No Policy", DialogSingleSelector.class, SelectNoPolicy.class);
    public static final AssetDescriptor<TextBox> POLICY_STATUS = declare("Policy Status", TextBox.class);
    public static final AssetDescriptor<ComboBox> POLICY_BLOB = declare("Policy BLOB", ComboBox.class);
    public static final AssetDescriptor<ComboBox> POLICY_LOB = declare("Policy LOB", ComboBox.class);
    public static final AssetDescriptor<ComboBox> POLICY_PRODUCT = declare("Policy Product", ComboBox.class);
    public static final AssetDescriptor<ComboBox> TYPE_OF_CLAIM = declare("Type of Claim", ComboBox.class);
    public static final AssetDescriptor<ComboBox> TYPE_OF_LOSS = declare("Type of Loss", ComboBox.class);
    public static final AssetDescriptor<ComboBox> DAMAGED_PROPERTY = declare("Damaged Property", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ASSOCIATED_RISK_ITEM = declare("Associated Risk Item", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COVERAGE = declare("Coverage", ComboBox.class);
    public static final AssetDescriptor<ComboBox> REPORTED_RISK_ITEM_TYPE = declare("Reported Risk Item Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> REPORTED_RISK_ITEM_NAME = declare("Reported Risk Item Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> SAVE_LOSS_AS = declare("Save Loss As", ComboBox.class);

    public static final class SelectPolicy extends MetaData {
        public static final AssetDescriptor<RadioGroup> SELECT_POLICY = declare("Policy Search", RadioGroup.class, Waiters.DEFAULT, false,
                By.id("policySearchForm:policySearch"));
        public static final AssetDescriptor<TextBox> POLICY = declare("Policy #", TextBox.class, Waiters.DEFAULT, false,
                By.id("policySearchForm:policySearchResults:policyNumberColumn:filter"));
        public static final AssetDescriptor<ComboBox> STATUS = declare("Status", ComboBox.class, Waiters.DEFAULT, false,
                By.id("policySearchForm:policySearchResults:policyStatusColumn:filter"));
        public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class, Waiters.DEFAULT, false,
                By.id("policySearchForm:policySearchResults:policyEffectiveDateColumn:filter"));
        public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class, Waiters.DEFAULT, false,
                By.id("policySearchForm:policySearchResults:policyExpirationDateColumn:filter"));
        public static final AssetDescriptor<ComboBox> PRODUCT = declare("Product", ComboBox.class, Waiters.DEFAULT, false,
                By.id("policySearchForm:policySearchResults:policyProductColumn:filter"));
        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false,
                By.id("policyDataGatherForm:policySearchLink"));
        public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.getDefaultPopupSubmitterName(), Button.class, Waiters.DEFAULT, false,
                By.id("policySearchForm:selectPolicyAction"));
    }

    public static final class SelectNoPolicy extends MetaData {
        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false,
                By.id("policyDataGatherForm:policySearchLink"));
        public static final AssetDescriptor<Button> NO_POLICY = declare("No Policy", Button.class, Waiters.DEFAULT, false,
                By.id("policySearchForm:noPolicyLink"));
    }
}
