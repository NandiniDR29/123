/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata;

import com.exigen.istf.webdriver.controls.CheckBox;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class BasicBenefitsTabMetaData extends MetaData {

    public static final AssetDescriptor<AssetList> AIR_AMBULANCE_BENEFIT = declare("Air Ambulance Benefit", AssetList.class, AirAmbulanceBenefitMetaData.class);
    public static final AssetDescriptor<AssetList> GROUND_AMBULANCE_BENEFIT = declare("Ground Ambulance Benefit", AssetList.class, GroundAmbulanceBenefitMetaData.class);
    public static final AssetDescriptor<AssetList> EMERGENCY_ROOM_TREATMENT_BENEFIT = declare("Emergency Room Treatment Benefit", AssetList.class, EmergencyRoomTreatmentMetaData.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> HOSPITAL_ADMISSION_BENEFIT = declare("Hospital Admission Benefit", AssetList.class, HospitalAdmissionBenefitMetaData.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> PHYICAL_THERAPY_SERVICE_BENEFIT = declare("Physical Therapy Service Benefit", AssetList.class, PhysicalTherapyServiceBenefitMetaData.class);
    public static final AssetDescriptor<AssetList> MAJOR_DIAGNOSTIC_BENEFIT = declare("Major Diagnostic Benefit", AssetList.class, MajorDiagnosticBenefitMetadata.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

    public static final class AirAmbulanceBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccAirAmbulanceBenefit_applied']"));
        public static final AssetDescriptor<ComboBox> TYPE_OF_INCURRAL_PERIOD = declare("Type of Incurral Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccAirAmbulanceBenefit_typeOfIncurralPeriod']"));
        public static final AssetDescriptor<TextBox> INCURRAL_PERIOD_HOURS = declare("Incurral Period (hours)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccAirAmbulanceBenefit_incurralPeriodHours']"));
        public static final AssetDescriptor<TextBox> AIR_AMBULANCE_BENEFIT_AMT = declare("Air Ambulance Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> MAX_TRANSPORT_NO = declare("Maximum number of transports for the Air Ambulance Benefit", ComboBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccAirAmbulanceBenefit_timePeriod']"));
    }

    public static final class GroundAmbulanceBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccGroundAmbulanceBenefit_applied']"));
        public static final AssetDescriptor<ComboBox> TYPE_OF_INCURRAL_PERIOD = declare("Type of Incurral Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccGroundAmbulanceBenefit_typeOfIncurralPeriod']"));
        public static final AssetDescriptor<TextBox> INCURRAL_PERIOD_DAYS = declare("Incurral Period (days)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccGroundAmbulanceBenefit_incurralPeriodDays']"));
        public static final AssetDescriptor<TextBox> GROUND_AMBULANCE_BENEFIT_AMT = declare("Ground Ambulance Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> GROUND_MAX_TRANSPORT_NO = declare("Maximum number of transports for the Ground Ambulance Benefit", ComboBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccGroundAmbulanceBenefit_timePeriod']"));
    }

    public static final class EmergencyRoomTreatmentMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccEmergencyCareBenefit_applied']"));
        public static final AssetDescriptor<TextBox> INCURRAL_PERIOD_HOURS = declare("Incurral Period (hours)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccEmergencyCareBenefit_incurralPeriodHours']"));
        public static final AssetDescriptor<TextBox> PHYSICIAN_OFFICE_BENEFIT_AMT = declare("Physician's Office Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> URGENT_CARE_BENEFIT_AMT = declare("Urgent Care Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> MAX_EMERGENCY_CARE_BENEFIT_AMT = declare("Maximum Emergency Care Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> EMERGENCY_ROOM_BENEFIT_AMOUNT = declare("Emergency Room Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccEmergencyCareBenefitViewOnly_timePeriod']"));
    }

    public static final class HospitalAdmissionBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupHospitalAdmissionBenefit_applied']"));
        public static final AssetDescriptor<TextBox> HOSP_INCURRAL_PERIOD_DAYS = declare("Incurral Period (days)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupHospitalAdmissionBenefit_incurralPeriodDays']"));
        public static final AssetDescriptor<TextBox> HOSPITAL_ADMISSION_BENEFIT_AMT = declare("Hospital Admission Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> MAX_HOSPITAL_ADMISSION_BENEFIT_AMT = declare("Maximum Hospital Admission Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> INTENSIVE_CARE_UNIT_ADMISSION_BENEFIT_AMOUNT = declare("Intensive Care Unit Admission Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupHospitalAdmissionBenefitViewOnly_timePeriod']"));
    }

    public static final class PhysicalTherapyServiceBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccOutpatientTherapyBenefit_applied']"));
        public static final AssetDescriptor<TextBox> PHY_THERAPY_TREATMENT_PERIOD_DAYS = declare("Physical Therapy Services Treatment Period (days)", TextBox.class);
        public static final AssetDescriptor<TextBox> PHY_BENEFIT_AMT = declare("Physical Therapy Services Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> PHY_MAX_NO_SERVICES = declare("Maximum number of services for the Maximum Services Physical Therapy Benefit", TextBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccOutpatientTherapyBenefit_timePeriod']"));
        public static final AssetDescriptor<TextBox> PHY_PERIOD_DAYS = declare("Physical Incurral Period (days)", TextBox.class);
        public static final AssetDescriptor<TextBox> MENTAL_HEALTH = declare("Mental Health", TextBox.class);
    }

    public static final class MajorDiagnosticBenefitMetadata extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccMedicalTestingBenefit_applied']"));
        public static final AssetDescriptor<TextBox> INCURRAL_PERIOD_DAYS = declare("Incurral Period (days)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccMedicalTestingBenefit_incurralPeriodDays']"));
        public static final AssetDescriptor<TextBox> MAJOR_DIAGNOSTIC_BENEFIT_AMOUNT = declare("Major Diagnostic Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> TYPE_OF_INCURRAL_PERIOD = declare("Type of Incurral Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccMedicalTestingBenefit_typeOfIncurralPeriod']"));
        public static final AssetDescriptor<ComboBox> MAXIMUM_NUMBER_OF_TESTS_FOR_THE_MAJOR_DIAGNOSTIC_BENEFIT = declare("Maximum number of tests for the Major Diagnostic Benefit", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccMedicalTestingBenefitView_maxBenefitNumber']"));
        public static final AssetDescriptor<ComboBox> TIME_PERIOD = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccMedicalTestingBenefitView_timePeriod']"));
    }
}
