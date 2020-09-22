/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata;

import com.exigen.istf.webdriver.controls.CheckBox;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

import static com.exigen.ren.common.pages.Page.DEFAULT_ASSETLIST_CONTAINER;

public class EnhancedBenefitsMtoTTabMetaData extends MetaData {
    public static final AssetDescriptor<AssetList> PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT = declare("Physician’s Office/Urgent Care Treatment Benefit", AssetList.class, PhysiciansOfficeUrgentCareTreatmentBenefitMetaData.class, By.xpath("//div[@id = 'policyDataGatherForm:componentView_RenACClassGroupEnhancedBenefitsPhysicianOfficeBenefit']"));
    public static final AssetDescriptor<AssetList> SPORTS_INJURY_BENEFIT = declare("Sports Injury Benefit", AssetList.class, SportsInjuryBenefitMetaData.class, By.xpath("//div[@id = 'policyDataGatherForm:componentView_RenACClassGroupEnhancedBenefitsSportsInjuryBenefit']"));
    public static final AssetDescriptor<AssetList> TRANSPORTATION_BENEFIT = declare("Transportation Benefit", AssetList.class, TransportationBenefitMetaData.class, By.xpath(DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> MAJOR_DIAGNOSTIC_BENEFIT = declare("Major Diagnostic Benefit", AssetList.class, MajorDiagnosticBenefitMetaData.class, By.xpath(DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> PHYSICAL_THERAPY_SERVICE_BENEFIT = declare("Physical Therapy Service Benefit", AssetList.class, PhysicalTherapyServiceBenefitMetaData.class, By.xpath("//div[@id='policyDataGatherForm:componentView_PreconfigGroupAccOutpatientTherapyBenefitEnhanced']"));
    public static final AssetDescriptor<AssetList> PROSTHETIC_DEVICE_BENEFIT = declare("Prosthetic Device Benefit", AssetList.class, ProstheticDeviceBenefitMetaData.class, By.xpath(DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> SURGERY_BENEFIT = declare("Surgery Benefit", AssetList.class, SurgeryBenefitMetaData.class, By.xpath(DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> REHABILITATION_UNIT_BENEFIT = declare("Rehabilitation Unit Benefit", AssetList.class, RehabilitationUnitBenefitMetaData.class, By.xpath(DEFAULT_ASSETLIST_CONTAINER));

    public static final class PhysiciansOfficeUrgentCareTreatmentBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_RenACClassGroupEnhancedBenefitsPhysicianOfficeBenefit_applied']"));
        public static final AssetDescriptor<TextBox> PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT_AMOUNT = declare("Physicians Office/Urgent Care Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> TREATMENT_PERIOD_DAYS_PHY_OFC_URGENT_CARE = declare("Treatment Period (days)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_RenACClassGroupEnhancedBenefitsPhysicianOfficeBenefit_incurralPeriodDays']"));
    }

    public static final class SportsInjuryBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_RenACClassGroupEnhancedBenefitsSportsInjuryBenefit_applied']"));
        public static final AssetDescriptor<TextBox> SPORTS_INJURY_BENEFIT_LESSER_OF_25PERCENT_OF_ALL_BENEFITS_PAID = declare("Sports Injury Benefit - Lesser of 25% of All Benefits Paid or [benefit amount]", TextBox.class);
    }

    public static final class TransportationBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT_TRANSPORTATION = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccTransportationBenefit_applied']"));
        public static final AssetDescriptor<TextBox> TRANSPORTATION_BENEFIT_AMOUNT = declare("Transportation Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> MAXIMUM_NUMBER_OF_TIMES_FOR_THE_TRANSPORTATION_BENEFIT = declare("Maximum number of times for the Transportation Benefit", ComboBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD_TRANSPORTATION = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccTransportationBenefit_timePeriod']"));
    }

    public static final class MajorDiagnosticBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT_MAJOR_DIAGNOSTIC = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccMedicalTestingBenefitEnhanced_applied']"));
        public static final AssetDescriptor<ComboBox> TYPE_OF_INCURRAL_PERIOD_MAJOR_DIAGNOSTIC = declare("Type of Incurral Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccMedicalTestingBenefitEnhanced_typeOfIncurralPeriod']"));
        public static final AssetDescriptor<TextBox> INCURRAL_PERIOD_MAJOR_DIAGNOSTIC = declare("Incurral Period (days)", TextBox.class);
        public static final AssetDescriptor<TextBox> MAJOR_DIAGNOSTIC_BENEFIT_AMOUNT = declare("Major Diagnostic Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> MAXIMUM_NUMBER_OF_TESTS_FOR_THE_MAJOR_DIAGNOSTIC_BENEFIT = declare("Maximum number of tests for the Major Diagnostic Benefit", ComboBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD_MAJOR_DIAGNOSTIC = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccMedicalTestingBenefitViewEnhanced_timePeriod']"));
    }

    public static final class PhysicalTherapyServiceBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT_PHYSICAL_THERAPY = declare("Apply Benefit?", CheckBox.class);
        public static final AssetDescriptor<TextBox> PHYSICAL_THERAPY_SERVICES_TREATMENT_PERIOD = declare("Physical Therapy Services Treatment Period (days)", TextBox.class);
        public static final AssetDescriptor<TextBox> PHYSICAL_THERAPY_SERVICES_BENEFIT_AMOUNT = declare("Physical Therapy Services Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> MAXIMUM_NUMBER_OF_SERVICES_FOR_THE_MAXIMUM_SERVICES = declare("Maximum number of services for the Maximum Services Physical Therapy Benefit", TextBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD_PHYSICAL_THERAPY = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccOutpatientTherapyBenefitEnhanced_timePeriod']"));
    }

    public static final class ProstheticDeviceBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT_PROSTHETIC_DEVICE = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccProstheticDeviceBenefit_applied']"));
        public static final AssetDescriptor<TextBox> TREATMENT_PERIOD_PROSTHETIC_DEVICE = declare("Treatment Period (days)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccProstheticDeviceBenefit_incurralPeriodDays']"));
        public static final AssetDescriptor<TextBox> ONE_PROSTHETIC_DEVICE = declare("One Prosthetic Device or Artificial Limb Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> MORE_THAN_ONE_PROSTHETIC_DEVICE = declare("More than one Prosthetic Device or Artificial Limb Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD_PROSTHETIC_DEVICE = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccProstheticDeviceBenefitViewOnly_timePeriod']"));
    }

    public static final class SurgeryBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT_SURGERY = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccSurgeryBenefit_applied']"));
        public static final AssetDescriptor<TextBox> SURGERY_PERFORMANCE_PERIOD = declare("Surgery Performance Period (days)", TextBox.class);
        public static final AssetDescriptor<TextBox> HERNIATED_DISC_BENEFIT_AMOUNT = declare("Herniated Disc Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> TORN_RUPTURED_OR_SEVERED_TENDON = declare("Torn, Ruptured or Severed Tendon or Ligament Surgery Benefit Amount to Torn, Ruptured or Severed Tendon or Ligament or Rotator Cuff Surgery Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> TORN_RUPTURED_OR_SEVERED_OF_TWO_OR_MORE = declare("Torn, Ruptured or Severed of Two or More Tendons / Ligaments Surgery Benefit Amount to Torn, Ruptured or Severed of Two or More Tendons / Ligaments / Rotator Cuff Surgery Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> TORN_KNEE_CARTILAGE_SURGERY_BENEFIT_AMOUNT = declare("Torn Knee Cartilage Surgery Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> EXPLORATORY_SURGERY_FOR_TENDON = declare("Exploratory Surgery for Tendon/ Ligament/ Rotator Cuff/ Knee Cartilage Without Repair Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD_SURGERY = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccSurgeryBenefitViewOnly_timePeriod']"));
    }

    public static final class RehabilitationUnitBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT_REHABILITATION = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupInpatientTherapyServicesBenefit_applied']"));
        public static final AssetDescriptor<TextBox> IMPATIEMT_REHABILITATION = declare("Inpatient Rehabilitation Unit Benefit Amount per day", TextBox.class);
        public static final AssetDescriptor<TextBox> MAXIMUM_NUMBER_OF_DAYS_FOR_REHABILITATION = declare("Maximum number of days for Rehabilitation Unit Benefit", TextBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD_REHABILITATION = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupInpatientTherapyBenefitViewOnly_timePeriod']"));
    }
}
