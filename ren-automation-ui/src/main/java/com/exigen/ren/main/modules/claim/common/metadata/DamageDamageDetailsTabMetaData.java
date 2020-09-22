/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class DamageDamageDetailsTabMetaData extends MetaData {
    public static final AssetDescriptor<Link> ADD_NEW_DAMAGE_VEHICLE = declare("Add New Damage: Vehicle", Link.class, By.id("productConsolidatedViewForm:addDamageAutoLoss"));
    public static final AssetDescriptor<Link> ADD_NEW_DAMAGE_INJURY = declare("Add New Damage: Injury", Link.class, By.id("productConsolidatedViewForm:addDamageClaimsInjury"));
    public static final AssetDescriptor<Link> ADD_NEW_DAMAGE_OTHER_PROPERTY = declare("Add New Damage: Other Property", Link.class, By.id("productConsolidatedViewForm:addDamagePropertyDamage"));
    public static final AssetDescriptor<Link> ADD_NEW_DAMAGE_HOME_AND_CONTENTS = declare("Add New Damage: Home and Contents", Link.class, By.id("productConsolidatedViewForm:addDamagePropertyLoss"));
    public static final AssetDescriptor<ComboBox> DAMAGE = declare("Damage", ComboBox.class);
    public static final AssetDescriptor<TextBox> DAMAGE_TYPE = declare("Damage Type", TextBox.class);
    public static final AssetDescriptor<ComboBox> DAMAGED_PROPERTY = declare("Damaged Property", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ASSOCIATED_RISK_ITEM = declare("Associated Risk Item", ComboBox.class);
    public static final AssetDescriptor<TextBox> LOCATION_DURING_LOSS_EVENT = declare("Location During Loss Event", TextBox.class);
    public static final AssetDescriptor<TextBox> DAMAGE_DESCRIPTION = declare("Damage Description", TextBox.class);
    public static final AssetDescriptor<ComboBox> TYPE_OF_INJURY = declare("Type Of Injury", ComboBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION_OF_OTHER = declare("Description of Other", TextBox.class);
    public static final AssetDescriptor<ComboBox> GENERAL_AREA_OF_INJURY = declare("General Area of Injury", ComboBox.class);
    public static final AssetDescriptor<TextBox> WHAT_WAS_THE_CLAIMANT_DOING_WHEN_INJURED = declare("What was the claimant doing when injured?", TextBox.class);
    public static final AssetDescriptor<TextBox> INJURY_NATURE_DESCRIPTION = declare("Injury Nature Description", TextBox.class);
    public static final AssetDescriptor<RadioGroup> AMBULANCE_REQUIRED = declare("Ambulance Required?", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> RESTRAINT_SAFETY_DEVICE_USED = declare("Restraint/Safety Device Used?", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> CONDITION_CODE = declare("Condition Code", ComboBox.class);
    public static final AssetDescriptor<ComboBox> BODILY_INJURY_SEVERITY = declare("Bodily Injury Severity", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PARTY_NAME = declare("Party Name", ComboBox.class);
    public static final AssetDescriptor<ComboBox> RELATIONSHIP_TO_INSURED = declare("Relationship To Insured", ComboBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION_OF_RELATIONSHIP_TO_INSURED = declare("Description of Relationship to Insured", TextBox.class);
    public static final AssetDescriptor<ComboBox> NAME_TYPE = declare("Name Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PREFIX = declare("Prefix", ComboBox.class);
    public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
    public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
    public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
    public static final AssetDescriptor<TextBox> COMPANY_NAME = declare("Company Name", TextBox.class);
    public static final AssetDescriptor<TextBox> SOCIAL_SECURITY_NUMBER_SSN = declare("Social Security Number (SSN)", TextBox.class);
    public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
    public static final AssetDescriptor<TextBox> TAX_IDENTIFICATION_NUMBER_OR_FEDERAL_EMPLOYMENT_IDENTIFICATION_NUMBER =
            declare("Tax Identification Number or Federal Employment Identification Number", TextBox.class);
    public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class);
    public static final AssetDescriptor<ComboBox> MARITAL_STATUS = declare("Marital Status", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PHONE_TYPE = declare("Phone Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> PHONE = declare("Phone", TextBox.class);
    public static final AssetDescriptor<ComboBox> PHONE_TYPE2 = declare("Phone Type2", ComboBox.class);
    public static final AssetDescriptor<TextBox> PHONE2 = declare("Phone2", TextBox.class);
    public static final AssetDescriptor<ComboBox> PHONE_TYPE3 = declare("Phone Type3", ComboBox.class);
    public static final AssetDescriptor<TextBox> PHONE3 = declare("Phone3", TextBox.class);
    public static final AssetDescriptor<ComboBox> PHONE_TYPE4 = declare("Phone Type4", ComboBox.class);
    public static final AssetDescriptor<TextBox> PHONE4 = declare("Phone4", TextBox.class);
    public static final AssetDescriptor<ComboBox> PHONE_TYPE5 = declare("Phone Type5", ComboBox.class);
    public static final AssetDescriptor<TextBox> PHONE5 = declare("Phone5", TextBox.class);
    public static final AssetDescriptor<TextBox> EMAIL = declare("Email", TextBox.class);
    public static final AssetDescriptor<ComboBox> CONTACT_PREFERENCE = declare("Contact Preference", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PREFERRED_PAYMENT_METHOD= declare("Preferred Payment Method", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> STATEMENT_OBTAINED = declare("Statement Obtained", RadioGroup.class);
    public static final AssetDescriptor<TextBox> PARTY_S_PERSPECTIVE = declare("Party's Perspective", TextBox.class);
    public static final AssetDescriptor<ComboBox> ROLE_DURING_LOSS_EVENT = declare("Role During Loss Event", ComboBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION_OF_ROLE_DURING_LOSS_EVENT = declare("Description of Role During Loss Event", TextBox.class);
    public static final AssetDescriptor<TextBox> DATE_OF_CONTACT = declare("Date Of Contact", TextBox.class);
    public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
    public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class,
            By.xpath("//input[contains(@id,'address_postalCode') and not(contains(@disabled,'disabled'))]"));
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class,
            By.xpath("//input[contains(@id,'_address_addressLine1') and not(contains(@disabled,'disabled'))]"));
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
    public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
    public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class);
    public static final AssetDescriptor<ComboBox> YEAR = declare("Year", ComboBox.class);
    public static final AssetDescriptor<ComboBox> MAKE = declare("Make", ComboBox.class);
    public static final AssetDescriptor<ComboBox> MODEL = declare("Model", ComboBox.class);
    public static final AssetDescriptor<TextBox> VIN = declare("VIN", TextBox.class);
    public static final AssetDescriptor<ComboBox> BODY_TYPE = declare("Body Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> COLOR = declare("Color", TextBox.class);
    public static final AssetDescriptor<TextBox> LICENSE_PLATE_NUMBER = declare("License plate number", TextBox.class);
    public static final AssetDescriptor<ComboBox> LICENSE_PLATE_STATE = declare("License Plate State", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> VEHICLE_USED_WITH_PERMISSION_OF_OWNER = declare("Vehicle used with permission of owner", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> VEHICLE_DRIVABLE = declare("Vehicle drivable? ", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> POINT_OF_INITIAL_IMPACT = declare("Point of Initial Impact", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> AIRBAGS_DEPLOYED = declare("Airbags Deployed?", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> VEHICLE_USED_FOR_COMMERCIAL_PURPOSES = declare("Vehicle Used for Commercial Purposes", RadioGroup.class);
    public static final AssetDescriptor<TextBox> DIRECTION_OF_TRAVEL = declare("Direction of Travel", TextBox.class);
    public static final AssetDescriptor<TextBox> SPEED = declare("Speed", TextBox.class);
    public static final AssetDescriptor<RadioGroup> TOTAL_LOSS_DETERMINATION = declare("Total Loss Determination", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> LEAKING_FLUIDS = declare("Leaking Fluids", RadioGroup.class);
    public static final AssetDescriptor<TextBox> OVERNIGHT_ZIP = declare("Overnight Zip", TextBox.class);
    public static final AssetDescriptor<ComboBox> PARTY_TYPE = declare("Party Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> LICENSE_PLATE_COUNTRY = declare("License Plate Country", ComboBox.class);
    public static final AssetDescriptor<ComboBox> GENERAL_AREA_OF_DAMAGE = declare("General Area of Damage", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SECURITY_SYSTEM = declare("Security System", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> INSPECTION_REQUIRED = declare("Inspection Required", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> VEHICLE_OCCUPIED = declare("Vehicle Occupied", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> PARKED_DURING_EVENT = declare("Parked During Event?", RadioGroup.class);
    public static final AssetDescriptor<TextBox> WHERE_IS_THE_DAMAGED_PROPERTY_CURRENTLY_LOCATED =
            declare("Where is the damaged property currently located?", TextBox.class);
    public static final AssetDescriptor<ComboBox> SPEED_LABEL = declare("Speed Label", ComboBox.class);
    public static final AssetDescriptor<TextBox> CHASSIS = declare("Chassis", TextBox.class);
    public static final AssetDescriptor<ComboBox> FREQUENCY_THE_DRIVER_USES_THE_VEHICLE = declare("Frequency the driver uses the vehicle", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PROPERTY_DAMAGE_SEVERITY = declare("Property Damage Severity", ComboBox.class);
    public static final AssetDescriptor<TextBox> PROPERTY_DESCRIPTION = declare("Property Description", TextBox.class);
    public static final AssetDescriptor<RadioGroup> OWNER = declare("Owner", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> DO_YOU_CONSIDER_YOURSELF_RESPONSIBLE_FOR_THE_ACCIDENT =
            declare("Do you consider yourself responsible for the accident?", RadioGroup.class);
    public static final AssetDescriptor<TextBox> DRIVER_S_LICENSE = declare("Driver's License #", TextBox.class);
    public static final AssetDescriptor<ComboBox> LICENSE_COUNTRY = declare("License Country", ComboBox.class);
    public static final AssetDescriptor<ComboBox> LICENSE_STATE = declare("License State", ComboBox.class);
    public static final AssetDescriptor<TextBox> LICENSE_EXPIRATION_DATE = declare("License Expiration Date", TextBox.class);
    public static final AssetDescriptor<TextBox> CITATION_ISSUED = declare("Citation Issued", TextBox.class);
    public static final AssetDescriptor<RadioGroup> DRUG_ALCOHOL_TEST_CONDUCTED = declare("Drug/ Alcohol Test Conducted?", RadioGroup.class);
    public static final AssetDescriptor<TextBox> TEST_RESULTS = declare("Test Results", TextBox.class);
    public static final AssetDescriptor<ComboBox> POLICY_DRIVER_TYPE = declare("Policy Driver Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> REPORTED_DRIVER_TYPE = declare("Reported Driver Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ASSOCIATED_SCHEDULED_ITEM = declare("Associated Scheduled Item", ComboBox.class);
    public static final AssetDescriptor<TextBox> ITEM = declare("Item", TextBox.class);
    public static final AssetDescriptor<TextBox> ESTIMATED_COST_VALUE = declare("Estimated Cost/Value", TextBox.class);
    public static final AssetDescriptor<RadioGroup> EXCLUDED_FROM_COVERAGE = declare("Excluded from Coverage", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> TYPE_OF_ITEM = declare("Type of Item", ComboBox.class);
    public static final AssetDescriptor<TextBox> NAME_PREFIX = declare("Name Prefix", TextBox.class);
    public static final AssetDescriptor<TextBox> CUSTOMER_NAME = declare("Customer Name", TextBox.class);
}
