/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata;

import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab;
import com.exigen.ren.common.controls.TripleTextBox;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class GBCommissionStrategyMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> PRODUCT_CODE_NAME = declare("Product Code-Name", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ROUNDING = declare("Rounding", ComboBox.class);
    public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
    public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
    public static final AssetDescriptor<RadioGroup> DETERMINE_COMMISSION_RATE_BY = declare("Determine Commission Rate By", RadioGroup.class);
    public static final AssetDescriptor<CheckBox> AVAILABLE_FOR_OVERRIDE = declare("Available for Override", CheckBox.class);
    public static final AssetDescriptor<RadioGroup> POLICY_LEVEL_COMMISSION = declare("Policy Level Commission", RadioGroup.class);

    public static final AssetDescriptor<GBCommissionStrategyTab.CommissionOverrideAssetList> COMMISSION_OVERRIDES = declare("Commission Overrides",
            GBCommissionStrategyTab.CommissionOverrideAssetList.class, AddCommissionOverrideOptionsMetaData.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

    public static final class AddCommissionOverrideOptionsMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> COMMISSION_TYPE_OVERRIDE = declare("Commission Type", ComboBox.class, By.id("strategyTopForm:overrideRuleStructure"));
        public static final AssetDescriptor<DialogAssetList> ADD_COMMISSION_OVERRIDE_OPTIONS = declare("Add Commission Override Options", DialogAssetList.class, CommissionOverrideOptions.class);
    }

    public static final class CommissionOverrideOptions extends MetaData {
        public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class);
        public static final AssetDescriptor<RadioGroup> USE_TEMPLATE = declare("Use Template", RadioGroup.class, Waiters.DEFAULT, false);
        public static final AssetDescriptor<TripleTextBox> RATE_1 = declare("Rate 1", TripleTextBox.class, Waiters.DEFAULT);
        public static final AssetDescriptor<TripleTextBox> RATE_2 = declare("Rate 2", TripleTextBox.class, Waiters.DEFAULT);
        public static final AssetDescriptor<TripleTextBox> RATE_3 = declare("Rate 3", TripleTextBox.class, Waiters.DEFAULT);
        public static final AssetDescriptor<TripleTextBox> RATE_4 = declare("Rate 4", TripleTextBox.class, Waiters.DEFAULT);
        public static final AssetDescriptor<TripleTextBox> TIER_1 = declare("Tier 1", TripleTextBox.class, Waiters.DEFAULT);
        public static final AssetDescriptor<TripleTextBox> TIER_2 = declare("Tier 2", TripleTextBox.class, Waiters.DEFAULT);
        public static final AssetDescriptor<TripleTextBox> TIER_3 = declare("Tier 3", TripleTextBox.class, Waiters.DEFAULT);
        public static final AssetDescriptor<TripleTextBox> TIER_4 = declare("Tier 4", TripleTextBox.class, Waiters.DEFAULT);
        public static final AssetDescriptor<TextBox> OVERRIDE_RANGE_MIN_PERCENT = declare("Override Range Min Percent", TextBox.class, By.id("addOverrideOptionDialogForm_groupBenefitsFlat:overrideMinPercent"));
        public static final AssetDescriptor<TextBox> OVERRIDE_RANGE_MAX_PERCENT = declare("Override Range Max Percent", TextBox.class, By.id("addOverrideOptionDialogForm_groupBenefitsFlat:overrideMaxPercent"));
        public static final AssetDescriptor<TextBox> OVERRIDE_RANGE_MIN = declare("Override Range Min", TextBox.class, By.id("addOverrideOptionDialogForm_renSubscriberCountFlat:overrideMinValue"));
        public static final AssetDescriptor<TextBox> OVERRIDE_RANGE_MAX = declare("Override Range Max", TextBox.class, By.id("addOverrideOptionDialogForm_renSubscriberCountFlat:overrideMaxValue"));

        public static final AssetDescriptor<Button> ADD_TIER = declare("Add Tier", Button.class, Waiters.DEFAULT, false, By.xpath("//button[@id='addOverrideOptionDialogForm_renTiered:addBandBtn']"));
        public static final AssetDescriptor<Button> ADD_RATE = declare("Add Rate", Button.class, Waiters.DEFAULT, false, By.xpath("//button[@id='addOverrideOptionDialogForm_renSubscriberCount:addBandBtn']"));
        public static final AssetDescriptor<Button> BUTTON_REMOVE = declare("Remove", Button.class, By.xpath("//button[contains(@id, 'addOverrideOptionDialogForm_ren') and contains(@id, 'removeBandBtn')]"));
        public static final AssetDescriptor<Button> BUTTON_CLEAR = declare("Clear", Button.class, By.xpath("//button[contains(@id, 'addOverrideOptionDialogForm_') and contains(@id, 'clearRuleBtn')]"));
        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false,
                By.id("strategyTopForm:showAddOverrideRulePopup"));
        public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.getDefaultPopupSubmitterName(), Button.class, Waiters.DEFAULT, false,
                By.xpath("//input[contains(@id, 'addOverrideOptionDialogForm_') and @value = 'OK']"));
        public static final AssetDescriptor<Button> BUTTON_CLOSE_POPUP = declare(AbstractDialog.getDefaultPopupCloserName(), Button.class, Waiters.DEFAULT, false,
                By.xpath("//button[contains(@id, 'addOverrideOptionDialogForm_') and contains(@id, 'cancelBtn')]"));

    }
}
