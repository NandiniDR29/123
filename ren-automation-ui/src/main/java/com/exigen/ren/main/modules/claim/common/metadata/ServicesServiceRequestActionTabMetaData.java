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

public class ServicesServiceRequestActionTabMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> DAMAGE = declare("Damage", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SERVICE_TYPE = declare("Service Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COVERAGE = declare("Coverage", ComboBox.class);
    public static final AssetDescriptor<TextBox> COVERAGE_DEDUCTIBLE = declare("Coverage Deductible", TextBox.class);
    public static final AssetDescriptor<RadioGroup> CREATE_NEW_VENDOR = declare("Create New Vendor?", RadioGroup.class);
    public static final AssetDescriptor<TextBox> COMPANY_NAME = declare("Company Name", TextBox.class);
    public static final AssetDescriptor<TextBox> TAX_IDENTIFICATION_NUMBER = declare("Tax Identification Number", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS = declare("Address", TextBox.class);
    public static final AssetDescriptor<TextBox> DEFAULT_CONTACT_FIRST_NAME = declare("Default Contact First Name", TextBox.class);
    public static final AssetDescriptor<TextBox> DEFAULT_CONTACT_LAST_NAME = declare("Default Contact Last Name", TextBox.class);
    public static final AssetDescriptor<TextBox> DEFAULT_CONTACT_PHONE = declare("Default Contact Phone #", TextBox.class);
    public static final AssetDescriptor<TextBox> DEFAULT_CONTACT_EXTENSION = declare("Default Contact Extension #", TextBox.class);
    public static final AssetDescriptor<TextBox> DEFAULT_CONTACT_FAX = declare("Default Contact Fax #", TextBox.class);
    public static final AssetDescriptor<TextBox> DEFAULT_CONTACT_EMAIL = declare("Default Contact Email", TextBox.class);
    public static final AssetDescriptor<ComboBox> DEFAULT_CONTACT_PREFERENCE = declare("Default Contact Preference", ComboBox.class);
    public static final AssetDescriptor<TextBox> CONTACT_FIRST_NAME = declare("Contact First Name", TextBox.class);
    public static final AssetDescriptor<TextBox> CONTACT_LAST_NAME = declare("Contact Last Name", TextBox.class);
    public static final AssetDescriptor<TextBox> CONTACT_PHONE = declare("Contact Phone #", TextBox.class);
    public static final AssetDescriptor<TextBox> CONTACT_EXTENSION = declare("Contact Extension #", TextBox.class);
    public static final AssetDescriptor<TextBox> CONTACT_EMAIL = declare("Contact Email", TextBox.class);
    public static final AssetDescriptor<TextBox> BEGINNING_DATE_OF_SERVICE = declare("Beginning Date of Service", TextBox.class);
    public static final AssetDescriptor<TextBox> END_DATE_OF_SERVICE = declare("End Date of Service", TextBox.class);
    public static final AssetDescriptor<TextBox> ESTIMATED_COST_OF_SERVICE = declare("Estimated Cost of Service", TextBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION_OF_WORK_PERFORMED = declare("Description of Work Performed", TextBox.class);
    public static final AssetDescriptor<TextBox> RESULTS_FINDINGS = declare("Results/Findings", TextBox.class);
    public static final AssetDescriptor<RadioGroup> TOTAL_LOSS_DETERMINATION = declare("Total Loss Determination", RadioGroup.class);
    public static final AssetDescriptor<TextBox> APPRAISAL_VALUE = declare("Appraisal Value", TextBox.class);
    public static final AssetDescriptor<TextBox> ACTUAL_CASH_VALUE = declare("Actual Cash Value", TextBox.class);
    public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
    public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip/Postal Code", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
    public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State/Province", ComboBox.class);
    public static final AssetDescriptor<ComboBox> TYPE_OF_VENDOR = declare("Type of Vendor", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SERVICE_AREA = declare("Service Area", ComboBox.class);
    public static final AssetDescriptor<ComboBox> VENDOR_RATING = declare("Vendor Rating", ComboBox.class);
    public static final AssetDescriptor<ComboBox> VENDOR_STATUS = declare("Vendor Status", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SETTLEMENT_METHOD = declare("Settlement Method", ComboBox.class);
}
