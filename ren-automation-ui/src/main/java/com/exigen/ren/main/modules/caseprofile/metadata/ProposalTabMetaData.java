/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.caseprofile.metadata;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class ProposalTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> ASSUMPTIONS = declare("Assumptions", TextBox.class);
    public static final AssetDescriptor<TextBox> PROPOSED_ASO_FEE = declare("Proposed ASO Fee", TextBox.class, By.id("proposalForm:proposalsQuotesSelectionTable:0:proposalASOFeetable:0:proposedAsoFeeMoney"));
    public static final AssetDescriptor<ComboBox> FEE_UPDATE_REASON = declare("Fee Update Reason", ComboBox.class, By.id("proposalForm:proposalsQuotesSelectionTable:0:proposalASOFeetable:0:feeUpdateReason"));
    public static final AssetDescriptor<TextBox> ASO_FEE_ADJUSTMENT = declare("ASO Fee Adjustment % ", TextBox.class, By.id("proposalForm:proposalsQuotesSelectionTable:0:asoFeeAdjustment"));
    public static final AssetDescriptor<Button> ASO_FEE_ADJUSTMENT_APPLY = declare("Apply", Button.class, By.id("proposalForm:proposalsQuotesSelectionTable:0:asoFeeAdjustmentApply"));
    public static final AssetDescriptor<TextBox> RATE_ADJUSTMENT = declare("Rate Adjustment %  - ", TextBox.class, By.id("proposalForm:proposalsQuotesSelectionTable:0:coverageInfoTable:0:discountPct"));
    public static final AssetDescriptor<ComboBox> RATE_ADJUSTMENT_DRP_DWN = declare("Rate Adjustment %", ComboBox.class, By.id("proposalForm:proposalsQuotesSelectionTable:0:coverageInfoTable:0:discountReasonCd"));
    public static final AssetDescriptor<Button> RATE_ADJUSTMENT_APPLY = declare("Rate Adjustment Apply", Button.class, By.id("proposalForm:proposalsQuotesSelectionTable:0:coverageInfoTable:0:rateAdjustmentApplyButton"));
    public static final AssetDescriptor<ComboBox> RATE_UPDATE_REASON = declare("Rate Update Reason", ComboBox.class, By.id("proposalForm:proposalsQuotesSelectionTable:0:coverageInfoTable:0:classTabId:0:rateUpdateReason"));
    public static final AssetDescriptor<Button> GENERATE_PRE_PROPOSAL = declare("Generate Pre-Proposal", Button.class, By.id("proposalForm:generatePreProposalBtn_footer"));
    public static final AssetDescriptor<Button> GENERATE_PROPOSAL = declare("Generate Proposal", Button.class, By.id("proposalForm:generateProposalBtn_footer"));
    public static final AssetDescriptor<Button> OVERRIDE_RULES = declare("Override Rules", Button.class, By.id("proposalForm:overrideRulesBtn"));
    public static final AssetDescriptor<TextBox> PROPOSED_TERM_RATE = declare("Proposed Term Rate", TextBox.class, By.id("proposalForm:proposalsQuotesSelectionTable:0:coverageInfoTable:0:classTabId:0:proposedTermRateMoney"));

}
