/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata;

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

public class InsuredTabMetaData extends MetaData {
    public static final AssetDescriptor<RadioGroup> CREATE_NEW_CUSTOMER = declare("Create New Customer?", RadioGroup.class);
    public static final AssetDescriptor<DialogSingleSelector> SEARCH_CUSTOMER = declare("Search Customer", DialogSingleSelector.class,
            SearchCustomerSingleSelector.class, false, By.id("customerInsertionPopup_GroupCertificateCustomerInsertionType_container"));

    public static final AssetDescriptor<ComboBox> TITLE = declare("Title", ComboBox.class);
    public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
    public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
    public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
    public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
    public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class);
    public static final AssetDescriptor<ComboBox> MARITAL_STATUS = declare("Marital Status", ComboBox.class);
    public static final AssetDescriptor<ComboBox> TOBACCO = declare("Tobacco", ComboBox.class);
    public static final AssetDescriptor<TextBox> TAX_IDENTIFICATION = declare("Tax Identification", TextBox.class);
    public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
    public static final AssetDescriptor<TextBox> ZIP_POST_CODE = declare("Zip / Post Code", TextBox.class);
    public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
    public static final AssetDescriptor<ComboBox> RELATIONSHIP_TYPE = declare("Relationship Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> JOB_CODE = declare("Job Code", ComboBox.class);
    public static final AssetDescriptor<ComboBox> EMPLOYMENT_STATUS = declare("Employment Status", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> EXPATRIATE = declare("Expatriate", RadioGroup.class);
    public static final AssetDescriptor<TextBox> PAY_TYPE = declare("Pay Type", TextBox.class);
    public static final AssetDescriptor<TextBox> SALARY_AMOUNT = declare("Salary Amount", TextBox.class);
    public static final AssetDescriptor<RadioGroup> UNION_MEMBER = declare("Union Member", RadioGroup.class);
    public static final AssetDescriptor<TextBox> JOB_TITLE = declare("Job Title", TextBox.class);
    public static final AssetDescriptor<TextBox> ORIGINAL_HIRE_DATE = declare("Original Hire Date", TextBox.class);
    public static final AssetDescriptor<TextBox> REHIRE_DATE = declare("Rehire Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> EMPLOYMENT_TYPE = declare("Employment Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PAY_CLASS = declare("Pay Class", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PAYROLL_FREQUENCY = declare("Payroll Frequency", ComboBox.class);
    public static final AssetDescriptor<TextBox> HOURLY_WAGE = declare("Hourly Wage", TextBox.class);
    public static final AssetDescriptor<TextBox> EMPLOYEE_ID = declare("Employee ID", TextBox.class);
    public static final AssetDescriptor<TextBox> ANNUAL_EARNINGS = declare("Annual Earnings", TextBox.class);
    public static final AssetDescriptor<TextBox> MEMBERSHIP_START_DATE = declare("Membership Start Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> MEMBERSHIP_STATUS = declare("Membership Status", ComboBox.class);
    public static final AssetDescriptor<TextBox> MEMBERSHIP_ID = declare("Membership ID", TextBox.class);
    public static final AssetDescriptor<TextBox> MEMBERSHIP_NUMBER = declare("Membership Number", TextBox.class);
    public static final AssetDescriptor<TextBox> STUDENT_START_DATE = declare("Student Start Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> STUDENT_STATUS = declare("Student Status", ComboBox.class);
    public static final AssetDescriptor<ComboBox> STUDENT_TYPE = declare("Student Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> STUDENT_ATHLETE = declare("Student Athlete", ComboBox.class);
    public static final AssetDescriptor<TextBox> STUDENT_ID = declare("Student ID", TextBox.class);
    public static final AssetDescriptor<TextBox> FIELD_OF_STUDY = declare("Field of Study", TextBox.class);
    public static final AssetDescriptor<ComboBox> CLASSIFICATION_GROUP = declare("Classification Group", ComboBox.class);
    public static final AssetDescriptor<TextBox> MAIL = declare("e-Mail", TextBox.class);

    public static final class SearchCustomerSingleSelector extends MetaData {
        public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
        public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);

        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class,
                Waiters.DEFAULT, false, By.id("policyDataGatherForm:insertCustomer_GroupCertificateCustomerInsertionType"));
    }
}
