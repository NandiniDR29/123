/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class ChangeStatusActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> FEATURE_NUMBER = declare("Feature Number", TextBox.class);
    public static final AssetDescriptor<TextBox> FEATURE_ID = declare("Feature ID", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATUS = declare("Status", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SUB_STATUS = declare("Sub-Status", ComboBox.class);
    public static final AssetDescriptor<ComboBox> CLAIMANT = declare("Claimant", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ASSOCIATED_INSURABLE_RISK = declare("Associated Insurable Risk", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COVERAGE = declare("Coverage", ComboBox.class);
    public static final AssetDescriptor<TextBox> LIMIT = declare("Limit", TextBox.class);
    public static final AssetDescriptor<ComboBox> COVERAGE_LETTER_ISSUED = declare("Coverage Letter Issued", ComboBox.class);
    public static final AssetDescriptor<TextBox> DATE_COVERAGE_LETTER_WAS_ISSUED = declare("Date Coverage Letter was Issued", TextBox.class);
    public static final AssetDescriptor<TextBox> COVERAGE_DETERMINATION_NOTES = declare("Coverage Determination Notes", TextBox.class);
    public static final AssetDescriptor<RadioGroup> COVERAGE_DETERMINATION_DISPUTED = declare("Coverage Determination Disputed", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> ESTIMATED_EVALUATION_OF_LIABILITY = declare("Estimated Evaluation of Liability", ComboBox.class);
    public static final AssetDescriptor<ComboBox> FINAL_DETERMINATION_OF_LIABILITY = declare("Final Determination of Liability", ComboBox.class);
    public static final AssetDescriptor<TextBox> EXPLANATION_FOR_EVALUATION_OF_LIABILITY = declare("Explanation for Evaluation of Liability", TextBox.class);
    public static final AssetDescriptor<TextBox> INDEMNITY_RESERVE_OLD = declare("Indemnity Reserve Old", TextBox.class);
    public static final AssetDescriptor<TextBox> INDEMNITY_RESERVE = declare("Indemnity Reserve", TextBox.class);
    public static final AssetDescriptor<TextBox> INDEMNITY_RESERVE_COMMENTS = declare("Indemnity Reserve Comments", TextBox.class);
    public static final AssetDescriptor<TextBox> EXPENSE_RESERVE_OLD = declare("Expense Reserve Old", TextBox.class);
    public static final AssetDescriptor<TextBox> EXPENSE_RESERVE = declare("Expense Reserve", TextBox.class);
    public static final AssetDescriptor<TextBox> EXPENSE_RESERVE_COMMENTS = declare("Expense Reserve Comments", TextBox.class);
    public static final AssetDescriptor<TextBox> RECOVERY_RESERVE_OLD = declare("Recovery Reserve Old", TextBox.class);
    public static final AssetDescriptor<TextBox> RECOVERY_RESERVE = declare("Recovery Reserve", TextBox.class);
    public static final AssetDescriptor<TextBox> RECOVERY_RESERVE_COMMENTS = declare("Recovery Reserve Comments", TextBox.class);
    public static final AssetDescriptor<TextBox> FEATURE_OWNER = declare("Feature Owner", TextBox.class);
    public static final AssetDescriptor<ComboBox> ACTION = declare("Action", ComboBox.class);
    public static final AssetDescriptor<ComboBox> REASON_FOR_DAMAGE_CLOSURE = declare("Reason for Damage Closure", ComboBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION_OF_REASON_FOR_DAMAGE_CLOSURE = declare("Description of Reason for Damage Closure", TextBox.class);
}
