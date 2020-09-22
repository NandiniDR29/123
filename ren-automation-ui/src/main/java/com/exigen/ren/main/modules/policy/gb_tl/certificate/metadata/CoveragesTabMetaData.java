/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata;

import com.exigen.ipb.eisa.controls.BeneficiaryMultiAssetList;
import com.exigen.ipb.eisa.controls.ParticipantMultiAssetList;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class CoveragesTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> CLASS_NAME = declare("Class Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> PLAN = declare("Plan Name", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COVERAGE_NAME = declare("Coverage Name", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COVERAGE_SELECTION = declare("Coverage Selection", ComboBox.class);
    public static final AssetDescriptor<TextBox> ENROLLMENT_DATE = declare("Enrollment Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> CURRENT_EFFECTIVE_AMOUNT = declare("Current Effective Amount", ComboBox.class);
    public static final AssetDescriptor<TextBox> APPROVED_AMOUNT = declare("Approved Amount", TextBox.class);

    public static final AssetDescriptor<AssetList> BENEFIT_SPOUSE = declare("Benefit Spouse", AssetList.class, BenefitSpouseMetaData.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> BENEFIT_CHILD = declare("Benefit Child", AssetList.class, BenefitChildMetaData.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

    public static final AssetDescriptor<Button> ADD_PARTICIPANT = declare("Add Participant", Button.class, By.id("policyDataGatherForm:addGroupCertificateParticipant"));
    public static final AssetDescriptor<ComboBox> PARTICIPANT_SELECTION = declare("Participant Selection", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ROLE_NAME = declare("Role Name", ComboBox.class);
    public static final AssetDescriptor<AssetList> PARTICIPANT_GENERAL_INFO = declare("Participant General Info", AssetList.class, ParticipantGeneralInfoMetaData.class,
            By.xpath("//div[@id='policyDataGatherForm:componentView_GroupCertificateParticipantPersonInfoProxy_body']"));
    public static final AssetDescriptor<AssetList> PARTICIPANT_ADDRESS_INFO = declare("Participant Address Info", AssetList.class, ParticipantAddressInfoMetaData.class,
            By.xpath("//div[@id='policyDataGatherForm:componentView_GroupCertificateParticipantContactInfoProxy_body']"));
    public static final AssetDescriptor<ComboBox> RELATIONSHIP_TO_INSURED = declare("Relationship to Insured", ComboBox.class);

    public static final AssetDescriptor<TextBox> ROLE_PERCENT = declare("Role Percent", TextBox.class);
    public static final AssetDescriptor<TextBox> APPROVED_AMMOUNT = declare("Approved Amount", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
    public static final AssetDescriptor<ComboBox> BENEFIT_TYPE = declare("Benefit Type", ComboBox.class);

    //TODO (Lapai): remove duplicated items for Participant
    public static final AssetDescriptor<ParticipantMultiAssetList> PARTICIPANTS = declare("Participants", ParticipantMultiAssetList.class, CoveragesTabMetaData.ParticipantsMetaData.class);
    public static final AssetDescriptor<BeneficiaryMultiAssetList> BENEFICIARIES = declare("Beneficiaries", BeneficiaryMultiAssetList.class, BeneficiariesMetaData.class);

    public static final class ParticipantsMetaData extends MetaData {
        public static final AssetDescriptor<Button> ADD_PARTICIPANT = declare("Add Participant", Button.class, By.id("policyDataGatherForm:addGroupCertificateParticipant"));
        public static final AssetDescriptor<ComboBox> PARTICIPANT_SELECTION = declare("Participant Selection", ComboBox.class);
        public static final AssetDescriptor<ComboBox> ROLE_NAME = declare("Role Name", ComboBox.class);
        public static final AssetDescriptor<AssetList> PARTICIPANT_GENERAL_INFO = declare("Participant General Info", AssetList.class, CoveragesTabMetaData.ParticipantGeneralInfoMetaData.class,
                By.xpath("//div[@id='policyDataGatherForm:componentView_GroupCertificateParticipantPersonInfoProxy_body']"));
        public static final AssetDescriptor<AssetList> PARTICIPANT_ADDRESS_INFO = declare("Participant Address Info", AssetList.class, CoveragesTabMetaData.ParticipantAddressInfoMetaData.class,
                By.xpath("//div[@id='policyDataGatherForm:componentView_GroupCertificateParticipantContactInfoProxy_body']"));
    }

    public static final class BeneficiariesMetaData extends MetaData {
        public static final AssetDescriptor<Button> ADD_BENEFICIARY = declare("Add Beneficiary", Button.class, By.id("policyDataGatherForm:addGroupCertificateB"));
        public static final AssetDescriptor<ComboBox> BENEFICIARY_SELECTION = declare("Beneficiary Selection", ComboBox.class);
        public static final AssetDescriptor<ComboBox> ROLE_NAME = declare("Role Name", ComboBox.class, By.id("policyDataGatherForm:sedit_GroupCertificateBeneficiaryViewOnly_beneficiaryType"));
        public static final AssetDescriptor<TextBox> ROLE_PERCENT = declare("Role Percent", TextBox.class);
        public static final AssetDescriptor<AssetList> BENEFICIARY_GENERAL_INFO = declare("Beneficiary General Info", AssetList.class, BeneficiaryGeneralInfoMetaData.class,
                By.xpath("//div[@id='policyDataGatherForm:componentView_GroupCertificateBPersonInfoProxy']"));
        public static final AssetDescriptor<AssetList> BENEFICIARY_GENERAL_INFO_CORP = declare("Beneficiary General Info Non-Person", AssetList.class, BeneficiaryGeneralInfoMetaData.class,
                By.xpath("//div[@id='policyDataGatherForm:componentView_GroupCertificateBCorporationInfoProxy']"));
        public static final AssetDescriptor<AssetList> BENEFICIARY_ADDRESS_INFO_CORP = declare("Beneficiary Address Info Non-Person", AssetList.class, BeneficiaryAddressInfoMetaData.class,
                By.xpath("//div[@id='policyDataGatherForm:componentView_GroupCertificateBCorporationContactInfoProxy']"));
        public static final AssetDescriptor<AssetList> BENEFICIARY_ADDRESS_INFO = declare("Beneficiary Address Info", AssetList.class, BeneficiaryAddressInfoMetaData.class,
                By.xpath("//div[@id='policyDataGatherForm:componentView_GroupCertificateBContactInfoProxy']"));
    }

    public static final class BenefitSpouseMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> ELECTED_BENEFIT_AMOUNT = declare("Elected Benefit Amount", TextBox.class, By.id("policyDataGatherForm:sedit_PrecGrpTLCertCovSpouseTieredAmountSelectionView_electedBenefitAmount"));
        public static final AssetDescriptor<TextBox> COVERAGE_UP_TO_MAX_OF_EMPLOYEE_COVERAGE = declare("Coverage up to Max of Employee Coverage", TextBox.class, By.id("policyDataGatherForm:sedit_PrecGrpTLCertCovSpouseTieredAmountSelectionView_maxPctOfEmpCoverage"));
        public static final AssetDescriptor<TextBox> EMPLOYEE_BENEFIT_AMOUNT = declare("Employee Benefit Amount", TextBox.class, By.id("policyDataGatherForm:sedit_PrecGrpTLCertCovSpouseTieredAmountSelectionView_employeeBenefitAmount"));
        public static final AssetDescriptor<RadioGroup> EOI_SPOUSE_REQUIRED = declare("EOI - Spouse Required?", RadioGroup.class);
        public static final AssetDescriptor<TextBox> CURRENT_EFFECTIVE_AMOUNT_SPOUSE = declare("Current Effective Amount - Spouse", TextBox.class);
        public static final AssetDescriptor<ComboBox> CURRENT_EFFECTIVE_AMOUNT_SPOUSE_COMBOBOX = declare("Current Effective Amount - Spouse Combobox", ComboBox.class, By.id("policyDataGatherForm:sedit_PrecGrpTLCertCovSpouseTieredAmountSelectionView2_inforceAmount"));
        public static final AssetDescriptor<TextBox> APPROVED_AMOUNT_SPOUSE = declare("Approved Amount - Spouse", TextBox.class);
    }

    public static final class BenefitChildMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> BENEFIT_AMOUNT = declare("Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> BENEFIT_AMOUNT_COMBOBOX = declare("Benefit Amount Combobox", ComboBox.class, By.id("policyDataGatherForm:benefitAmountSelect_PrecGrpTLCertCovChildTieredAmountSelectionView"));
        public static final AssetDescriptor<TextBox> ELECTED_BENEFIT_AMOUNT = declare("Elected Benefit Amount", TextBox.class, By.id("policyDataGatherForm:sedit_PrecGrpTLCertCovChildTieredAmountSelectionView_electedBenefitAmount"));
        public static final AssetDescriptor<TextBox> COVERAGE_UP_TO_MAX_OF_EMPLOYEE_COVERAGE = declare("Coverage up to Max of Employee Coverage", TextBox.class, By.id("policyDataGatherForm:sedit_PrecGrpTLCertCovChildTieredAmountSelectionView_maxPctOfEmpCoverage"));
        public static final AssetDescriptor<TextBox> EMPLOYEE_BENEFIT_AMOUNT = declare("Employee Benefit Amount", TextBox.class, By.id("policyDataGatherForm:sedit_PrecGrpTLCertCovChildTieredAmountSelectionView_employeeBenefitAmount"));
    }

    public static final class BeneficiaryGeneralInfoMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> TITLE = declare("Title", ComboBox.class);
        public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
        public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
        public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
        public static final AssetDescriptor<TextBox> NAME_LEGAL = declare("Name - Legal", TextBox.class);
        public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
        public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
        public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class);
        public static final AssetDescriptor<ComboBox> MARITAL_STATUS = declare("Marital Status", ComboBox.class);
        public static final AssetDescriptor<Button> VIEW_BUTTON = declare("View", Button.class, By.id("policyDataGatherForm:sedit_GroupCertificateBPersonInfoProxy_person_legalIdentificationUnmaskBtn"));
        public static final AssetDescriptor<TextBox> TAX_IDENTIFICATION = declare("Tax Identification", TextBox.class);
        public static final AssetDescriptor<ComboBox> RELATIONSHIP_TO_INSURED = declare("Relationship to Insured", ComboBox.class);
    }

    public static final class BeneficiaryAddressInfoMetaData extends MetaData {
        public static final AssetDescriptor<RadioGroup> USE_PRIMARY_INSUREDS_ADDRESS = declare("Use Primary Insured's Address", RadioGroup.class);
        public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
        public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Post Code", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
    }

    public static final class ParticipantGeneralInfoMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
        public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
        public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
        public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
        public static final AssetDescriptor<TextBox> TAX_IDENTIFICATION = declare("Tax Identification", TextBox.class);
        public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
        public static final AssetDescriptor<ComboBox> RELATIONSHIP_TO_INSURED = declare("Relationship to Insured", ComboBox.class);
        public static final AssetDescriptor<TextBox> ATTAINED_AGE = declare("Attained Age", TextBox.class);
    }

    public static final class ParticipantAddressInfoMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
        public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
    }
}