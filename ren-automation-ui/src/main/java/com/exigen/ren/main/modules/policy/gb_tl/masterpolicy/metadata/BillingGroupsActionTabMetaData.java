/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class BillingGroupsActionTabMetaData extends MetaData {
    public static final AssetDescriptor<RadioGroup> ALLOW_MANUAL_SETUP_OF_BILLING_ACCOUNTS = declare("Allow Manual Setup of Billing Accounts", RadioGroup.class);
    public static final AssetDescriptor<TextBox> BILLING_GROUP_NAME = declare("Billing Group Name", TextBox.class);
    public static final AssetDescriptor<TextBox> BILLING_GROUP_NUMBER = declare("Billing Group Number", TextBox.class);
    public static final AssetDescriptor<ComboBox> BILLING_GROUP_TYPE = declare("Billing Group Type", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> SELF_ADMINISTERED = declare("Self-Administered?", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> PAYOR = declare("Payor", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PAYMENT_MODE = declare("Payment Mode", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PAYROLL_FREQUENCY = declare("Payroll Frequency", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> DIRECT_BILL = declare("Direct Bill", RadioGroup.class);
    public static final AssetDescriptor<AdvancedSelector> COVERAGE = declare("Coverage", AdvancedSelector.class);
    public static final AssetDescriptor<AdvancedSelector> CLASS = declare("Class", AdvancedSelector.class);
    public static final AssetDescriptor<TextBox> JOB_CODE = declare("Job Code", TextBox.class);
    public static final AssetDescriptor<TextBox> EMPLOYMENT_STATUS = declare("Employment Status", TextBox.class);
    public static final AssetDescriptor<TextBox> EMPLOYMENT_TYPE = declare("Employment Type", TextBox.class);
    public static final AssetDescriptor<RadioGroup> EXPATRIATE = declare("Expatriate", RadioGroup.class);
    public static final AssetDescriptor<TextBox> PAY_CLASS = declare("Pay Class", TextBox.class);
    public static final AssetDescriptor<TextBox> PAY_TYPE = declare("Pay Type", TextBox.class);
    public static final AssetDescriptor<RadioGroup> UNION_MEMBER = declare("Union Member", RadioGroup.class);
    public static final AssetDescriptor<TextBox> DEPARTMENT_ID = declare("Department ID", TextBox.class);
    public static final AssetDescriptor<TextBox> DIVISION_ID = declare("Division ID", TextBox.class);
    public static final AssetDescriptor<TextBox> LOCATION_ID = declare("Location ID", TextBox.class);
    public static final AssetDescriptor<TextBox> MEMBERSHIP_STATUS = declare("Membership Status", TextBox.class);
    public static final AssetDescriptor<ComboBox> STUDENT_ATHLETE = declare("Student Athlete", ComboBox.class);
    public static final AssetDescriptor<TextBox> STUDENT_STATUS = declare("Student Status", TextBox.class);
    public static final AssetDescriptor<TextBox> STUDENT_TYPE = declare("Student Type", TextBox.class);
    public static final AssetDescriptor<ComboBox> PAYROLL_CALENDAR = declare("Payroll Calendar", ComboBox.class);
    public static final AssetDescriptor<TextBox> PAY_DAY = declare("Pay Day", TextBox.class);
    public static final AssetDescriptor<TextBox> SECOND_PAY_DAY = declare("Second Pay Day", TextBox.class);
    public static final AssetDescriptor<TextBox> DEDUCTION_START_DATE = declare("Deduction Start Date", TextBox.class);

}
