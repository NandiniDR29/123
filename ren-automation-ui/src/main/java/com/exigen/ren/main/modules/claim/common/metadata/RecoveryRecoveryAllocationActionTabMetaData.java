/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantInformationTab;
import com.exigen.ren.main.modules.claim.common.tabs.RecoveryRecoveryAllocationActionTab;
import org.openqa.selenium.By;

public class RecoveryRecoveryAllocationActionTabMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> DAMAGE = declare("Damage", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COVERAGE = declare("Coverage", ComboBox.class);
    public static final AssetDescriptor<TextBox> ASSOCIATED_INSURABLE_RISK = declare("Associated Insurable Risk", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATUS = declare("Status", ComboBox.class);
    public static final AssetDescriptor<TextBox> FEATURE_NUMBER = declare("Feature Number", TextBox.class);
    public static final AssetDescriptor<TextBox> ALLOCATION_AMOUNT = declare("Allocation Amount", TextBox.class);

    public static final AssetDescriptor<RecoveryRecoveryAllocationActionTab.RecoveryTaxMultiAssetList> RECOVERY_TAX =
            declare("Recovery Tax", RecoveryRecoveryAllocationActionTab.RecoveryTaxMultiAssetList.class, RecoveryTaxMetaData.class, By.id("policyDataGatherForm:componentView_RenClaimsRecoveryCalculatorTax"));

    public static final class RecoveryTaxMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> TAX_COMMENTS = declare("Tax Comments", TextBox.class);
        public static final AssetDescriptor<ComboBox> TAX_TYPE = declare("Tax Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> FICA_SOCIAL_SECURITY_TAX_AMOUNT = declare("FICA Social Security Tax Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> FICA_MEDICARE_TAX_AMOUNT = declare("FICA Medicare Tax Amount", TextBox.class);
    }
}
