/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.productstrategy.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class AutomatedProcessingStrategyMetaData extends MetaData {
    public static final AssetDescriptor<RadioGroup> STRATEGY_TYPE = declare("Strategy Type", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> RENEWAL_TYPE = declare("Renewal Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> STRATEGY_ID = declare("Strategy ID", TextBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
    public static final AssetDescriptor<AdvancedSelector> LINE_OF_BUSINESS = declare("Line of Business", AdvancedSelector.class);
    public static final AssetDescriptor<AdvancedSelector> PRODUCT = declare("Product", AdvancedSelector.class);
    public static final AssetDescriptor<AdvancedSelector> POLICY_FORM = declare("Policy Form", AdvancedSelector.class);
    public static final AssetDescriptor<AdvancedSelector> RISK_STATE = declare("Risk State", AdvancedSelector.class);
    public static final AssetDescriptor<AdvancedSelector> UNDEWRITING_COMPANY = declare("Underwriting Company", AdvancedSelector.class);
    public static final AssetDescriptor<RadioGroup> APPLICABLE_FOR_TERM = declare("Applicable for Term", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> PROCESS_TYPE = declare("Process Type", RadioGroup.class);
    public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
    public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> TRIGGER_TYPE = declare("Trigger Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> BASIS_FOR_TIME_BASED_CALCULATION = declare("Basis for Time-based Calculation", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> PENDED_TRANSACTION_HANDLING = declare("Pended Transaction Handling", RadioGroup.class);
    public static final AssetDescriptor<TextBox> OF_DAYS_ALLOWED_FOR_RENEWAL_LAPSE = declare("# of Days allowed for Renewal Lapse", TextBox.class);
    public static final AssetDescriptor<TextBox> DO_NOT_PROCESS_OF_DAYS_BEFORE_TERM_EXPIRES = declare("Do not process # of Days before term expires", TextBox.class);
    public static final AssetDescriptor<RadioGroup> RATE_POLICY = declare("Rate Policy?", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> GENERATE_DOCS = declare("Generate Docs?", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> GENERATE_TASKS = declare("Generate Tasks?", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> POLICIES_FLAGGED_FOR_MANUAL_RENEW = declare("Policies flagged for Manual Renew", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> POLICIES_FLAGGED_AS_DO_NOT_RENEW = declare("Policies flagged as Do Not Renew", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> POLICIES_FLAGGED_FOR_CANCEL_NOTICE = declare("Policies flagged for Cancel Notice", RadioGroup.class);
    public static final AssetDescriptor<AdvancedSelector> ADDITIONAL_POLICY_SELECTION_RULES_ACTION = declare("Additional Policy Selection Rules Action", AdvancedSelector.class);
    public static final AssetDescriptor<TextBox> INITIATE_QUOTE = declare("Initiate Quote", TextBox.class);
    public static final AssetDescriptor<TextBox> RUN_REPORTS_SERVICES = declare("Run Reports/Services", TextBox.class);
    public static final AssetDescriptor<TextBox> RATE_QUOTE = declare("Rate Quote", TextBox.class);
    public static final AssetDescriptor<TextBox> OFFER_ISSUE_QUOTE = declare("Offer/Issue Quote", TextBox.class);
}
