/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class PolicyInformationDependentDependentCoverageActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> DEPENDENT_NAME = declare("Dependent Name", TextBox.class);
    public static final AssetDescriptor<TextBox> COVERAGE_EFFECTIVE_DATE = declare("Coverage Effective Date", TextBox.class);
    public static final AssetDescriptor<AdvancedSelector> COVERAGE_TYPE = declare("Coverage Type", AdvancedSelector.class);
    public static final AssetDescriptor<RadioGroup> SELF_ADMINISTERED = declare("Self-Administered?", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> COVERAGE_STATUS = declare("Coverage Status", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ACCELERATED_BENEFIT = declare("Accelerated Benefit", ComboBox.class);
    public static final AssetDescriptor<TextBox> ACCELERATED_BENEFIT_MAXIMUM_AMOUNT = declare("Accelerated Benefit Maximum Amount", TextBox.class);
    public static final AssetDescriptor<ComboBox> COVERAGE_NAME = declare("Coverage Name", ComboBox.class);
    public static final AssetDescriptor<TextBox> COVERAGE_EXPIRATION_DATE = declare("Coverage Expiration Date", TextBox.class);
    public static final AssetDescriptor<TextBox> ACCELERATED_BENEFIT_PERCENTAGE = declare("Accelerated Benefit Percentage", TextBox.class);
    public static final AssetDescriptor<ComboBox> ERISA_INDICATOR = declare("ERISA Indicator", ComboBox.class);
    public static final AssetDescriptor<ComboBox> LIMIT_LEVEL = declare("Limit Level", ComboBox.class);
    public static final AssetDescriptor<TextBox> BENEFIT_TYPE = declare("Benefit Type", TextBox.class);
    public static final AssetDescriptor<TextBox> BENEFIT_MINIMUM_AMOUNT = declare("Benefit Minimum Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> MINIMUM_SALARY_MULTIPLE = declare("Minimum Salary Multiple", TextBox.class);
    public static final AssetDescriptor<TextBox> MINIMUM_SALARY_PERCENTAGE = declare("Minimum Salary Percentage %", TextBox.class);
    public static final AssetDescriptor<TextBox> INCREMENT = declare("Increment", TextBox.class);
    public static final AssetDescriptor<TextBox> LIFETIME_BENEFIT_MAXIMUM_AMOUNT = declare("Lifetime Benefit Maximum Amount", TextBox.class);
    public static final AssetDescriptor<ComboBox> LIMIT_AMOUNT = declare("Limit Amount", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SALARY_MULTIPLE = declare("Salary Multiple", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SALARY_PERCENTAGE = declare("Salary Percentage %", ComboBox.class);
    public static final AssetDescriptor<TextBox> BENEFIT_MAXIMUM_AMOUNT = declare("Benefit Maximum Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> MAXIMUM_SALARY_MULTIPLE = declare("Maximum Salary Multiple", TextBox.class);
    public static final AssetDescriptor<TextBox> MAXIMUM_SALARY_PERCENTAGE = declare("Maximum Salary Percentage %", TextBox.class);
    public static final AssetDescriptor<ComboBox> VERIFIED_POLICY_DEPENDENT = declare("Verified Policy Dependent", ComboBox.class);
    public static final AssetDescriptor<TextBox> DEPENDENT_COVERAGE_EFFECTIVE_DATE = declare("Dependent Coverage Effective Date", TextBox.class);
    public static final AssetDescriptor<TextBox> DATE_OF_VERIFICATION = declare("Date of Verification", TextBox.class);
    public static final AssetDescriptor<TextBox> DEPENDENT_COVERAGE_TERMINATION_DATE = declare("Dependent Coverage Termination Date", TextBox.class);
}
