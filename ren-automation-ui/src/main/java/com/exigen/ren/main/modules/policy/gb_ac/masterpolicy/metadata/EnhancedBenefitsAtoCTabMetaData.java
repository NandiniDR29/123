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

public class EnhancedBenefitsAtoCTabMetaData extends MetaData {
    public static final AssetDescriptor<AssetList> SKIN_GRAFT_BURN_BENEFIT = declare("Skin Graft Burn Benefit", AssetList.class, SkinGraftBurnBenefitMetaData.class,
            By.xpath("//div[@id='policyDataGatherForm:componentView_PreconfigGroupAccSkinGraftBurnBenefit']"));
    public static final AssetDescriptor<AssetList> CATASTROPHIC_ACCIDENT_BENEFIT = declare("Catastrophic Accident Benefit", AssetList.class, CatastrophicAccidentBenefitMetaData.class,
            By.xpath(DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT = declare("Abdominal or Thoracic Surgery Benefit", AssetList.class, AbdominalOrThoracicSurgeryBenefitMetaData.class, By.xpath("//div[@id='policyDataGatherForm:componentView_RenACClassGroupEnhancedBenefitsAbdominalSurgeryBenefit']"));
    public static final AssetDescriptor<AssetList> ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT = declare("Accident Follow-up Treatment Benefit", AssetList.class, AccidentFollowUpTreatmentBenefitMetadata.class, By.xpath("//div[@id='policyDataGatherForm:componentView_RenACClassGroupEnhancedBenefitsAccidentFollowupBenefit']"));
    public static final AssetDescriptor<AssetList> ACCIDENTAL_DEATH_BENEFIT = declare("Accidental Death Benefit", AssetList.class, AccidentalDeathBenefitMetaData.class, By.xpath(DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> ACCIDENTAL_DEATH_BENEFIT_COMMON_CARRIER = declare("Accidental Death Benefit - Common Carrier", AssetList.class, AccidentalDeathBenefitCommonCarrierMetaData.class, By.xpath(DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> COMA_BENEFIT = declare("Coma Benefit", AssetList.class, ComaBenefitMetaData.class, By.xpath(DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> CONCUSSION_BENEFIT = declare("Concussion Benefit", AssetList.class, ConcussionBenefitMetaData.class, By.xpath(DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> BLOOD_PLASMA_PLATELETS_BENEFIT = declare("Blood / Plasma / Platelets Benefit", AssetList.class, BloodPlasmaPlateletsBenefitMetaData.class, By.xpath(DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> AIR_AMBULANCE_BENEFIT = declare("Air Ambulance Benefit", AssetList.class, AirAmbulanceBenefitMetaData.class, By.xpath("//div[@id='policyDataGatherForm:componentView_PreconfigGroupAccAirAmbulanceBenefitEnhanced']"));
    public static final AssetDescriptor<AssetList> GROUND_AMBULANCE_BENEFIT = declare("Ground Ambulance Benefit", AssetList.class, GroundAmbulanceBenefitMetaData.class, By.xpath("//div[@id='policyDataGatherForm:componentView_PreconfigGroupAccGroundAmbulanceBenefitEnhanced']"));
    public static final AssetDescriptor<AssetList> APPLIANCE_BENEFIT = declare("Appliance Benefit", AssetList.class, ApplianceBenefitMetaData.class, By.xpath(DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> BURN_BENEFIT = declare("Burn Benefit", AssetList.class, BurnBenefitMetaData.class, By.xpath(DEFAULT_ASSETLIST_CONTAINER));

    public static final class SkinGraftBurnBenefitMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> TREATMENT_PERIOD_DAYS = declare("Treatment Period (days)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccSkinGraftBurnBenefit_incurralPeriodDays']"));
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT_SKIN_GRAFT_BURN = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccSkinGraftBurnBenefit_applied']"));
        public static final AssetDescriptor<ComboBox> SKIN_GRAFT_BURN_BENEFIT_AMOUNT = declare("Skin Graft Burn Benefit Amount", ComboBox.class);
        public static final AssetDescriptor<TextBox> INCURRAL_TIME_PERIOD_SKIN_GRAFT_BURN = declare("Incurral Period (days)", TextBox.class);
    }

    public static final class CatastrophicAccidentBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupCatastrophicDismembermentBenefit_applied']"));
        public static final AssetDescriptor<TextBox> CATASTROPHIC_BENEFIT_AMOUNT_PRIOR_TO_AGE_70_EMPLOYEE = declare("Catastrophic Benefit Amount - Prior to Age 70 - Employee", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_RenCatastrophicBenefitAmountPriorToAge70EmployeeCoveredOption_value_amount']"));
        public static final AssetDescriptor<TextBox> CATASTROPHIC_BENEFIT_AMOUNT_PRIOR_TO_AGE_70_SPOUSE = declare("Catastrophic Benefit Amount - Prior to Age 70 - Spouse", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_RenCatastrophicBenefitAmountPriorToAge70SpouseCoveredOption_value_amount']"));
        public static final AssetDescriptor<TextBox> CATASTROPHIC_BENEFIT_AMOUNT_PRIOR_TO_AGE_70_CHILD = declare("Catastrophic Benefit Amount - Prior to Age 70 - Child", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_RenCatastrophicBenefitAmountPriorToAge70ChildCoveredOption_value_amount']"));
        public static final AssetDescriptor<TextBox> CATASTROPHIC_BENEFIT_AMOUNT_ON_OR_AFTER_AGE_70_EMPLOYEE = declare("Catastrophic Benefit Amount - On or After Age 70 - Employee", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_RenCatastrophicBenefitAmountOnOrAfterAge70EmployeeCoveredOption_value_amount']"));
        public static final AssetDescriptor<TextBox> CATASTROPHIC_BENEFIT_AMOUNT_ON_OR_AFTER_AGE_70_SPOUSE = declare("Catastrophic Benefit Amount - On or After Age 70 - Spouse", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_RenCatastrophicBenefitAmountOnOrAfterAge70SpouseCoveredOption_value_amount']"));
        public static final AssetDescriptor<TextBox> ELIMINATION_PERIOD_DAYS = declare("Elimination Period (days)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupCatastrophicDismembermentBenefit_incurralPeriodDays']"));
    }

    public static final class AbdominalOrThoracicSurgeryBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_RenACClassGroupEnhancedBenefitsAbdominalSurgeryBenefit_applied']"));
        public static final AssetDescriptor<TextBox> ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT_AMOUNT = declare("Abdominal or Thoracic Surgery Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> TREATMENT_PERIOD_HOURS = declare("Treatment Period (hours)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_RenACClassGroupEnhancedBenefitsAbdominalSurgeryBenefit_incurralPeriodHours']"));
    }

    public static final class AccidentFollowUpTreatmentBenefitMetadata extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_RenACClassGroupEnhancedBenefitsAccidentFollowupBenefit_applied']"));
        public static final AssetDescriptor<TextBox> ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT_AMOUNT = declare("Accident Follow-up Treatment Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> TREATMENT_PERIOD_DAYS = declare("Treatment Period (days)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_RenACClassGroupEnhancedBenefitsAccidentFollowupBenefit_incurralPeriodDays']"));
    }

    public static final class AccidentalDeathBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT_ACCIDENTAL_DEATH_BENEFIT = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccAccDeathBenefit_applied']"));
        public static final AssetDescriptor<TextBox> INCURRAL_PERIOD_ACCIDENTAL_DEATH_BENEFIT = declare("Incurral Period (days)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccAccDeathBenefit_incurralPeriodDays']"));
        public static final AssetDescriptor<CheckBox> DISAPPEARANCE_INCLUDED_ACCIDENTAL_DEATH_BENEFIT = declare("Disappearance Included", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccAccDeathBenefit_exposureDisIncluded']"));
        public static final AssetDescriptor<TextBox> ACCIDENTAL_DEATH_BENEFIT_AMOUNT_FOR_COVERED_PERSON = declare("Accidental Death Benefit Amount for Covered Person", TextBox.class);
        public static final AssetDescriptor<TextBox> ACCIDENTAL_DEATH_BENEFIT_AMOUNT_FOR_COVERED_SPOUSE = declare("Accidental Death Benefit Amount for Covered Spouse", TextBox.class);
        public static final AssetDescriptor<TextBox> ACCIDENTAL_DEATH_BENEFIT_AMOUNT_FOR_EACH_COVERED_DEPENDENT_CHILD = declare("Accidental Death Benefit Amount for each Covered Dependent Child", TextBox.class);
    }

    public static final class AccidentalDeathBenefitCommonCarrierMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT_COMMON_CARRIER = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccDeathCommonCarrierBenefit_applied']"));
        public static final AssetDescriptor<TextBox> INCURRAL_PERIOD_COMMON_CARRIER = declare("Incurral Period (days)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccDeathCommonCarrierBenefit_incurralPeriodDays']"));
        public static final AssetDescriptor<CheckBox> DISAPPEARANCE_INCLUDED_COMMON_CARRIER = declare("Disappearance Included", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccDeathCommonCarrierBenefit_exposureDisIncluded']"));
        public static final AssetDescriptor<TextBox> ACCIDENTAL_DEATH_BENEFIT_AMOUNT_COMMON_CARRIER_FOR_COVERED_PERSON = declare("Accidental Death Benefit Amount - Common Carrier for Covered Person", TextBox.class);
        public static final AssetDescriptor<TextBox> ACCIDENTAL_DEATH_BENEFIT_AMOUNT_COMMON_CARRIER_FOR_COVERED_SPOUSE = declare("Accidental Death Benefit Amount - Common Carrier for Covered Spouse", TextBox.class);
        public static final AssetDescriptor<TextBox> ACCIDENTAL_DEATH_BENEFIT_AMOUNT_COMMON_CARRIER_FOR_EACH_COVERED_DEPENDENT_CHILD = declare("Accidental Death Benefit Amount - Common Carrier for each Covered Dependent Child", TextBox.class);
    }

    public static final class ComaBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT_COMA_BENEFIT = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccComaBenefit_applied']"));
        public static final AssetDescriptor<TextBox> INCURRAL_PERIOD_COMA_BENEFIT = declare("Incurral Period (days)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccComaBenefit_incurralPeriodDays']"));
        public static final AssetDescriptor<TextBox> COMA_BENEFIT_AMOUNT = declare("Coma Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> NUMBER_OF_TIME_A_COMA_CAN_OCCUR = declare("Number of times a coma can occur", ComboBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD_COMA_BENEFIT = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccComaBenefit_timePeriod']"));
    }

    public static final class ConcussionBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT_CONCUSSION_BENEFIT = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccConcussionBenefit_applied']"));
        public static final AssetDescriptor<TextBox> INCURRAL_PERIOD_HOURS = declare("Incurral Period (hours)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccConcussionBenefit_incurralPeriodHours']"));
        public static final AssetDescriptor<TextBox> CONCUSSION_BENEFIT_AMOUNT = declare("Concussion Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> NUMBER_OF_TIME_A_CONCUSSION_CAN_OCCUR = declare("Number of times a concussion can occur", ComboBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD_CONCUSSION_BENEFIT = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccConcussionBenefit_timePeriod']"));
    }

    public static final class BloodPlasmaPlateletsBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT_BLOOD_PLASMA_PLATELETS = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccBloodPlasmaPlateletsBenefit_applied']"));
        public static final AssetDescriptor<TextBox> INCURRAL_PERIOD_BLOOD_PLASMA_PLATELETS = declare("Incurral Period (days)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccBloodPlasmaPlateletsBenefit_incurralPeriodDays']"));
        public static final AssetDescriptor<TextBox> BLOOD_PLASMA_PLATELETS_BENEFIT_AMOUNT = declare("Blood / Plasma / Platelets Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> MAXIMUM_NUMBER_OF_TRANSFUSIONS_OF_THE_BLOOD_PLASMA_PLATELETS_BENEFIT = declare("Maximum number of transfusions of the Blood / Plasma / Platelets Benefit", ComboBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD_BLOOD_PLASMA_PLATELETS = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccBPPBenefitViewOnly_timePeriod']"));
    }

    public static final class AirAmbulanceBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT_AIR_AMBULANCE = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccAirAmbulanceBenefitEnhanced_applied']"));
        public static final AssetDescriptor<TextBox> INCURRAL_PERIOD_AIR_AMBULANCE = declare("Incurral Period (hours)", TextBox.class);
        public static final AssetDescriptor<ComboBox> TYPE_OF_INCURRAL_PERIOD_AIR_AMBULANCE = declare("Type of Incurral Period", ComboBox.class);
        public static final AssetDescriptor<TextBox> AIR_AMBULANCE_BENEFIT_AMOUNT = declare("Air Ambulance Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> MAXIMUM_NUMBER_OF_TRANSPORTS_FOR_THE_AIR_AMBULANCE_BENEFIT = declare("Maximum number of transports for the Air Ambulance Benefit", ComboBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD_AIR_AMBULANCE = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccAirAmbulanceBenefitEnhanced_timePeriod']"));
    }

    public static final class GroundAmbulanceBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT_GROUND_AMBULANCE = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccGroundAmbulanceBenefitEnhanced_applied']"));
        public static final AssetDescriptor<TextBox> INCURRAL_PERIOD_GROUND_AMBULANCE = declare("Incurral Period (days)", TextBox.class);
        public static final AssetDescriptor<ComboBox> TYPE_OF_INCURRAL_PERIOD_GROUND_AMBULANCE = declare("Type of Incurral Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccGroundAmbulanceBenefitEnhanced_typeOfIncurralPeriod']"));
        public static final AssetDescriptor<TextBox> GROUND_AMBULANCE_BENEFIT_AMOUNT = declare("Ground Ambulance Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> MAXIMUM_NUMBER_OF_TRANSPORTS_FOR_THE_GROUND_AMBULANCE_BENEFIT = declare("Maximum number of transports for the Ground Ambulance Benefit", ComboBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD_GROUND_AMBULANCE = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccGroundAmbulanceBenefitEnhanced_timePeriod']"));
    }

    public static final class ApplianceBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT_APPLIANCE_BENEFIT = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccMedicalApplianceBenefit_applied']"));
        public static final AssetDescriptor<TextBox> USE_OF_APPLIANCE_MUST_BEGIN_WITH_DAYS = declare("Use of Appliance must begin with (days)", TextBox.class);
        public static final AssetDescriptor<TextBox> APPLIANCE_BENEFIT_AMOUNT = declare("Appliance Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> TIME_PERIOD_APPLIANCE_BENEFIT = declare("Time Period", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_PreconfigGroupAccMedicalApplianceBenefitViewOnly_timePeriod']"));
    }

    public static final class BurnBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT_BURN = declare("Apply Benefit?", CheckBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccBurnBenefit_applied']"));
        public static final AssetDescriptor<TextBox> TREATMENT_PERIOD_HOURS = declare("Treatment Period (hours)", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_PreconfigGroupAccBurnBenefit_incurralPeriodHours']"));
        public static final AssetDescriptor<TextBox> SECOND_DEGREE_BURNS = declare("Second-degree burns that cover at least 36% of body surface", TextBox.class);
        public static final AssetDescriptor<TextBox> THIRD_DEGREE_BURNS_COVERS_LESS_INCHES = declare("Third-degree burns that covers at least 9 square inches of body surface but less than 35 square inches of body surface", TextBox.class);
        public static final AssetDescriptor<TextBox> THIRD_DEGREE_BURNS_COVERS_MORE_INCHES = declare("Third-degree burns that cover 35 or more square inches of body surface", TextBox.class);
    }
}
