/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata;

import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitStatutorySTDInjuryPartyInformationTab;
import org.openqa.selenium.By;

public class BenefitStatutorySTDInjuryPartyInformationTabMetaData extends MetaData {
    public static final AssetDescriptor<Link> ADD_NEW_BENEFIT_STATUTORY_STD = declare("Add Benefit: Statutory Short Term Disability", Link.class, By.id("productConsolidatedViewForm:addDamageStatutoryShortTermDisability"));
    public static final AssetDescriptor<TextBox> ASSOCIATED_POLICY_PARTY = declare("Associated Policy Party", TextBox.class);
    public static final AssetDescriptor<ComboBox> ASSOCIATE_POLICY_PARTY = declare("Associate Policy Party", ComboBox.class);
    public static final AssetDescriptor<ComboBox> RELATIONSHIP_TO_PARTICIPANT = declare("Relationship To Participant", ComboBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION_OF_RELATIONSHIP_TO_PARTICIPANT = declare("Description of Relationship to Participant", TextBox.class);
    public static final AssetDescriptor<ComboBox> PREFIX = declare("Prefix", ComboBox.class);
    public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
    public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
    public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PHONE_TYPE2 = declare("Phone Type2", ComboBox.class);
    public static final AssetDescriptor<TextBox> PHONE2 = declare("Phone2", TextBox.class);
    public static final AssetDescriptor<ComboBox> PHONE_TYPE3 = declare("Phone Type3", ComboBox.class);
    public static final AssetDescriptor<TextBox> PHONE3 = declare("Phone3", TextBox.class);
    public static final AssetDescriptor<ComboBox> PHONE_TYPE4 = declare("Phone Type4", ComboBox.class);
    public static final AssetDescriptor<TextBox> PHONE4 = declare("Phone4", TextBox.class);
    public static final AssetDescriptor<TextBox> SOCIAL_SECURITY_NUMBER_SSN = declare("Social Security Number (SSN)", TextBox.class);
    public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
    public static final AssetDescriptor<TextBox> CURRENT_AGE = declare("Current Age", TextBox.class);
    public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class);
    public static final AssetDescriptor<ComboBox> MARITAL_STATUS = declare("Marital Status", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PHONE_TYPE = declare("Phone Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> PHONE = declare("Phone", TextBox.class);
    public static final AssetDescriptor<ComboBox> PHONE_TYPE5 = declare("Phone Type5", ComboBox.class);
    public static final AssetDescriptor<TextBox> PHONE5 = declare("Phone5", TextBox.class);
    public static final AssetDescriptor<TextBox> EMAIL = declare("Email", TextBox.class);
    public static final AssetDescriptor<TextBox> FAX = declare("Fax", TextBox.class);
    public static final AssetDescriptor<ComboBox> CONTACT_PREFERENCE = declare("Contact Preference", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
    public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
    public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
    public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class);
    public static final AssetDescriptor<ComboBox> ASSOCIATED_SCHEDULED_ITEM = declare("Associated Scheduled Item", ComboBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
    public static final AssetDescriptor<TextBox> ESTIMATED_COST_VALUE = declare("Estimated Cost/Value", TextBox.class);
    public static final AssetDescriptor<RadioGroup> EXCLUDED_FROM_COVERAGE = declare("Excluded from Coverage", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> TYPE_OF_ITEM = declare("Type of Item", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ELIGIBILITY_VERIFICATION = declare("Eligibility Verification", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> PART_TIME_WORKED_25_DAYS_OR_MORE = declare("Part Time worked 25 days or more?", RadioGroup.class);
    public static final AssetDescriptor<TextBox> DATE_OF_VERIFICATION = declare("Date of Verification", TextBox.class);
    public static final AssetDescriptor<ComboBox> FULL_TIME_DEPENDENT_STUDENT = declare("Full Time Dependent Student", ComboBox.class);
    public static final AssetDescriptor<ComboBox> EMPLOYED_DEPENDENT = declare("Employed Dependent", ComboBox.class);
    public static final AssetDescriptor<ComboBox> DISABLED_DEPENDENT = declare("Disabled Dependent", ComboBox.class);
    public static final AssetDescriptor<ComboBox> EMPLOYMENT_TYPE = declare("Employment Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PREFERRED_PAYMENT_METHOD = declare("Preferred Payment method", ComboBox.class);
    public static final AssetDescriptor<ComboBox> BANK_NAME = declare("Bank Name", ComboBox.class);
    public static final AssetDescriptor<TextBox> BANK_ACCOUNT_NUMBER = declare("Bank Account Number", TextBox.class);
    public static final AssetDescriptor<TextBox> BANK_TRANSIT_ROUTING_NUMBER = declare("Bank Transit / Routing Number", TextBox.class);
    public static final AssetDescriptor<ComboBox> BANK_ACCOUNT_TYPE = declare("Bank Account Type", ComboBox.class);

    public static final AssetDescriptor<AssetList> PARTICIPANT_WORK_DAYS = declare("Participant Work Days", AssetList.class, BenefitsStatutorySTDInjuryPartyInformationTabMetaData.ParticipantWorkDaysMetaData.class,
            By.xpath("//div[@id='policyDataGatherForm:componentView_StatDisabilityPartyWorkDays']"));
    public static final AssetDescriptor<BenefitStatutorySTDInjuryPartyInformationTab.YTDTaxableWageMultiAssetList> YTD_TAXABLE_WAGE =
            declare("YTD Taxable Wage", BenefitStatutorySTDInjuryPartyInformationTab.YTDTaxableWageMultiAssetList.class, BenefitLTDInjuryPartyInformationTabMetaData.YTDTaxableWageMetaData.class, By.id("policyDataGatherForm:formGrid_StatPartyYTDTaxableWage"));

    public static final class YTDTaxableWageMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> ID = declare("ID", TextBox.class);
        public static final AssetDescriptor<TextBox> YTD_TAXABLE_WAGE = declare("YTD Taxable Wage", TextBox.class);
        public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class);
        public static final AssetDescriptor<ComboBox> BILLING_LOCATION = declare("Billing Location", ComboBox.class);
    }

    public static final class ParticipantWorkDaysMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> SUNDAY = declare("Sunday", CheckBox.class);
        public static final AssetDescriptor<CheckBox> MONDAY = declare("Monday", CheckBox.class);
        public static final AssetDescriptor<CheckBox> TUESDAY = declare("Tuesday", CheckBox.class);
        public static final AssetDescriptor<CheckBox> WEDNESDAY = declare("Wednesday", CheckBox.class);
        public static final AssetDescriptor<CheckBox> THURSDAY = declare("Thursday", CheckBox.class);
        public static final AssetDescriptor<CheckBox> FRIDAY = declare("Friday", CheckBox.class);
        public static final AssetDescriptor<CheckBox> SATURDAY = declare("Saturday", CheckBox.class);
    }
}
