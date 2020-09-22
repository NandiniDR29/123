/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_dn.metadata;

import com.exigen.ipb.eisa.controls.AutoCompleteBox;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import org.openqa.selenium.By;

public class IntakeInformationTabMetaData extends MetaData {

    public static final AssetDescriptor<AssetList> PATIENT = declare("Patient", AssetList.class, PatientMetaData.class, By.id("policyDataGatherForm:componentView_ClaimsDentalPatient"));
    public static final AssetDescriptor<AssetList> PATIENT_ADDRESS = declare("Patient Address", AssetList.class, PatientAddressMetaData.class, By.id("policyDataGatherForm:componentView_ClaimsDentalPatientPartyAddressContact"));

    public static final AssetDescriptor<ComboBox> PLACE_OF_TREATMENT = declare("Place of Treatment", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PARTY_NAME = declare("Party Name", ComboBox.class);
    public static final AssetDescriptor<ComboBox> RELATIONSHIP_TO_INSURED = declare("Relationship To Insured", ComboBox.class);

    //Next element has a tricky xpath to work both with "Search" elements and 'View' elements
    public static final AssetDescriptor<AssetList> SEARCH_PROVIDER = declare("Search Provider", AssetList.class, SearchProviderMetaData.class, By.xpath("//div[(@id='policyDataGatherForm:componentView_ClaimsDentalVendorView' or @id='policyDataGatherForm:componentView_ClaimsDentalVendorSearch') and .//label and not(contains(@style,'display') and contains(@style,'none'))]"));

    public static final AssetDescriptor<ComboBox> TYPE_OF_TRANSACTION = declare("Type of Transaction", ComboBox.class);
    public static final AssetDescriptor<TextBox> RECEIVED_DATE = declare("Received Date", TextBox.class, By.id("policyDataGatherForm:sedit_ClaimsDentalTreatmentDetails_claim_reportedDtInputDate"));
    public static final AssetDescriptor<ComboBox> PAYEE_TYPE = declare("Payee Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> CLEAN_CLAIM_DATE = declare("Clean Claim Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> SOURCE = declare("Source", ComboBox.class);
    public static final AssetDescriptor<TextBox> ORTHO_MONTHS = declare("Ortho Months", TextBox.class);
    public static final AssetDescriptor<TextBox> PREDETERMINATION_NUM = declare("Predetermination #", TextBox.class);
    public static final AssetDescriptor<TextBox> COMPANY_NAME = declare("Company Name", TextBox.class);
    public static final AssetDescriptor<TextBox> TAX_IDENTIFICATION_NUMBER = declare("Tax Identification Number", TextBox.class);
    public static final AssetDescriptor<RadioGroup> OTHER_COVERAGE = declare("Other Coverage?", RadioGroup.class);

    public static final AssetDescriptor<AssetList> DIGITAL_IMAGE = declare("Digital Image",
            AssetList.class, DigitalImageSection.class, By.xpath("//div[@id='policyDataGatherForm:componentView_RenClaimsDigitalImage']"));
    public static final AssetDescriptor<IntakeInformationTab.SubmittedServicesAssetList> SUBMITTED_SERVICES = declare("Submitted Services",
            IntakeInformationTab.SubmittedServicesAssetList.class, SubmittedServicesSection.class, By.xpath("//div[@id='policyDataGatherForm:componentView_ClaimsProcedure']"));
    public static final AssetDescriptor<AssetList> OTHER_PAYEE_ADDRESS = declare("Other Payee Address",
            AssetList.class, OtherPayeeAddressSection.class, By.xpath("//div[@id='policyDataGatherForm:componentView_RenClaimsOtherPayeeAddressContact']"));
    public static final AssetDescriptor<AssetList> COORDINATION_OF_BENEFITS = declare("Coordination of Benefits",
            AssetList.class, CoordinationOfBenefitsSection.class, By.xpath("//div[@id='policyDataGatherForm:componentView_ClaimsDentalCoordinationOfBenefits']"));
    public static final AssetDescriptor<AssetList> INTERNATIONAL_PROVIDER = declare("International Provider",
            AssetList.class, InternationalProviderSection.class, By.xpath("//div[@id='policyDataGatherForm:componentView_RenClaimsDentalInternationalProvider']"));
    public static final AssetDescriptor<AssetList> INTERNATIONAL_PROVIDER_ADDRESS = declare("International Provider Address",
            AssetList.class, InternationalProviderAddressSection.class, By.xpath("//div[@id='policyDataGatherForm:componentView_RenClaimsDentalInternationalProviderAddress']"));

    public static final class PatientMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> NAME = declare("Name", ComboBox.class);
        public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
        public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
        public static final AssetDescriptor<ComboBox> RELATIONSHIP_TO_PRIMARY_INSURED = declare("Relationship to Primary Insured", ComboBox.class);
        public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
        public static final AssetDescriptor<StaticElement> ADDRESS = declare("Address", StaticElement.class);
        public static final AssetDescriptor<RadioGroup> SPECIAL_REVIEW = declare("Special Review", RadioGroup.class);
    }

    public static final class PatientAddressMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
        public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
        public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
    }

    public static final class SearchProviderMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> TIN = declare("TIN", TextBox.class);
        public static final AssetDescriptor<TextBox> NPI = declare("NPI", TextBox.class, Waiters.SLEEP(1000));
        public static final AssetDescriptor<TextBox> LICENSE_NUMBER = declare("License #", TextBox.class);
        public static final AssetDescriptor<TextBox> LICENSE_NPI = declare("License NPI", TextBox.class);
        public static final AssetDescriptor<ComboBox> LICENSE_STATE_PROVINCE = declare("License State/Province", ComboBox.class);
        public static final AssetDescriptor<TextBox> PROVIDER_NAME = declare("Provider Name", TextBox.class);
        public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
        public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
        public static final AssetDescriptor<TextBox> PRACTICE_NAME = declare("Practice Name", TextBox.class);
        public static final AssetDescriptor<TextBox> PHONE_NUM = declare("Phone #", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS = declare("Address", TextBox.class);
        public static final AssetDescriptor<TextBox> ALTERNATIVE_PAYEE = declare("Alternative Payee", TextBox.class);
        public static final AssetDescriptor<TextBox> SPECIALTY_TEXT_BOX = declare("Specialty TextBox", TextBox.class, By.id("policyDataGatherForm:sedit_ClaimsDentalVendorView_dentalVendorProfileDTO_specialtyDisplayValue"));
        public static final AssetDescriptor<ComboBox> SPECIALTY = declare("Specialty", ComboBox.class);
        public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip/Postal Code", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State/Province", ComboBox.class);
        public static final AssetDescriptor<Button> SEARCH_VENDOR = declare("Search Vendor", Button.class, Waiters.DEFAULT, false, By.id("policyDataGatherForm:searchBtn_ClaimsDentalVendorSearch"));
        public static final AssetDescriptor<Link> ADD_VENDOR = declare("Add Vendor", Link.class, Waiters.DEFAULT,
                By.id("policyDataGatherForm:body_vendorProfilesSearchResultTable_ClaimsDentalVendorSearch:0:ActionLink_ClaimsDentalVendorSearch"));
        public static final AssetDescriptor<StaticElement> TIN_LABEL = declare("TIN Label", StaticElement.class, By.id("policyDataGatherForm:sedit_ClaimsDentalVendorView_dentalVendorProfileDTO_taxId"));
        public static final AssetDescriptor<StaticElement> LICENSE_NPI_LABEL = declare("License NPI Label", StaticElement.class, By.id("policyDataGatherForm:sedit_ClaimsDentalVendorView_dentalVendorProfileDTO_licenseNpi"));
        public static final AssetDescriptor<StaticElement> LICENSE_NUM_LABEL = declare("License # Label", StaticElement.class, By.id("policyDataGatherForm:sedit_ClaimsDentalVendorView_dentalVendorProfileDTO_licenseNumber"));
        public static final AssetDescriptor<StaticElement> PROVIDER_NAME_LABEL = declare("Provider Name Label", StaticElement.class, By.id("policyDataGatherForm:sedit_ClaimsDentalVendorView_dentalVendorProfileDTO_providerName"));
        public static final AssetDescriptor<StaticElement> PRACTICE_NAME_LABEL = declare("Practice Name Label", StaticElement.class, By.id("policyDataGatherForm:sedit_ClaimsDentalVendorView_dentalVendorProfileDTO_practiceName"));
        public static final AssetDescriptor<StaticElement> PHONE_NUM_LABEL = declare("Phone # Label", StaticElement.class, By.id("policyDataGatherForm:sedit_ClaimsDentalVendorView_dentalVendorProfileDTO_phoneNumber_phoneNumber"));
        public static final AssetDescriptor<StaticElement> ADDRESS_LABEL = declare("Address Label", StaticElement.class, By.id("policyDataGatherForm:sedit_ClaimsDentalVendorView_dentalVendorProfileDTO_streetAddress_address"));
        public static final AssetDescriptor<StaticElement> ALTERNATIVE_PAYEE_LABEL = declare("Alternative Payee Label", StaticElement.class, By.id("policyDataGatherForm:sedit_ClaimsDentalVendorView_dentalVendorProfileDTO_alternativePayeeDisplayValue"));
        public static final AssetDescriptor<StaticElement> SPECIALTY_LABEL = declare("Specialty Label", StaticElement.class, By.id("policyDataGatherForm:sedit_ClaimsDentalVendorView_dentalVendorProfileDTO_specialtyDisplayValue"));
    }

    public static final class DigitalImageSection extends MetaData {
        public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, By.id("policyDataGatherForm:addRenClaimsDigitalImage"));
        public static final AssetDescriptor<TextBox> DIGITAL_IMAGE = declare("Digital Image #", TextBox.class);
        public static final AssetDescriptor<Button> REMOVE = declare("Remove", Button.class, By.id("policyDataGatherForm:eliminateRenClaimsDigitalImage"));
    }

    public static final class SubmittedServicesSection extends MetaData {
        public static final AssetDescriptor<TextBox> DOS = declare("DOS", TextBox.class);
        public static final AssetDescriptor<AutoCompleteBox> CDT_CODE = declare("CDT Code", AutoCompleteBox.class);
        public static final AssetDescriptor<TextBox> TOOTH_AREA = declare("Tooth / Area", TextBox.class);
        public static final AssetDescriptor<TextBox> TOOTH = declare("Tooth", TextBox.class);
        public static final AssetDescriptor<TextBox> SURFACE = declare("Surface", TextBox.class);
        public static final AssetDescriptor<TextBox> CHARGE = declare("Charge", TextBox.class);
        public static final AssetDescriptor<TextBox> ORAL_CAVITY = declare("Oral Cavity", TextBox.class);
        public static final AssetDescriptor<TextBox> QUANTITY = declare("Quantity", TextBox.class);
    }

    public static final class OtherPayeeAddressSection extends MetaData {
        public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
        public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
    }

    public static final class CoordinationOfBenefitsSection extends MetaData {
        public static final AssetDescriptor<TextBox> OTHER_INSURED_NAME = declare("Other Insured Name", TextBox.class);
        public static final AssetDescriptor<TextBox> OTHER_INSURED_DOB = declare("Other Insured DOB", TextBox.class);
        public static final AssetDescriptor<TextBox> OTHER_CARRIER_NAME = declare("Other Carrier Name", TextBox.class);
        public static final AssetDescriptor<TextBox> OTHER_CARRIER_AMOUNT = declare("Other Carrier Amount", TextBox.class);
    }

    public static final class InternationalProviderSection extends MetaData {
        public static final AssetDescriptor<RadioGroup> INTERNATIONAL_PROVIDER_VALUE = declare("International Provider", RadioGroup.class);
        public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
        public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
        public static final AssetDescriptor<TextBox> PRACTICE_NAME = declare("Practice Name", TextBox.class);
    }

    public static final class InternationalProviderAddressSection extends MetaData {
        public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
        public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
        public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
    }
}
