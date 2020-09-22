/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab.AgeReductionScheduleDetailsMultiAssetList;
import org.openqa.selenium.By;

public class PlanDefinitionTabMetaData extends MetaData {
    public static final AssetDescriptor<AdvancedSelector> PLAN = declare("Plan", AdvancedSelector.class);
    public static final AssetDescriptor<Button> ADD_COVERAGE = declare("Add Coverage", Button.class, By.id("policyDataGatherForm:addPreconfigGroupCoverageDefinition"));
    public static final AssetDescriptor<ComboBox> PLAN_COMBOBOX = declare("Plan ComboBox", ComboBox.class, By.id("policyDataGatherForm:sedit_GroupCoverageDefinitionCoverageCdViewOnly_packageCd"));
    public static final AssetDescriptor<ComboBox> COVERAGE_NAME = declare("Coverage Name", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COVERAGE_APPLICATION = declare("Coverage Application", ComboBox.class);
    public static final AssetDescriptor<TextBox> PLAN_NAME = declare("Plan Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> ADD_COVERAGE_PLAN = declare("Add Coverage Plan", ComboBox.class, By.id("policyDataGatherForm:sedit_GroupCoverageDefinitionCoverageCdViewOnly_packageCd"));
    public static final AssetDescriptor<ComboBox> CENSUS_TYPE = declare("Census Type", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> ENHANCED_AD_D = declare("Enhanced AD&D?", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> COVERAGE_TYPE = declare("Coverage Type", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> SELF_ADMINISTERED = declare("Self-administered?", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> REQUIRED_PARTICIPATION = declare("Required Participation %", ComboBox.class);
    public static final AssetDescriptor<ComboBox> CONTRIBUTION_TYPE = declare("Contribution Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> PARTICIPANT_CONTRIBUTION = declare("Participant Contribution %", TextBox.class);
    public static final AssetDescriptor<TextBox> MINIMUM_NUMBER_OF_PARTICIPANTS = declare("Minimum Number of Participants", TextBox.class);

    public static final AssetDescriptor<AdvancedSelector> MEMBER_PAYMENT_MODE = declare("Member Payment Mode", AdvancedSelector.class);
    public static final AssetDescriptor<ComboBox> SPONSOR_PAYMENT_MODE = declare("Sponsor Payment Mode", ComboBox.class);
    public static final AssetDescriptor<ComboBox> AGE_REDUCTION_SCHEDULE = declare("Age Reduction Schedule", ComboBox.class);
    public static final AssetDescriptor<TextBox> MAXIMUM_LIFETIME_FAMILY_BENEFIT_AMOUNT = declare("Maximum Lifetime Family Benefit Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> TOTAL_NUMBER_OF_ELIGIBLE_LIVES = declare("Total Number of Eligible Lives", TextBox.class);
    public static final AssetDescriptor<AssetList> EXCLUSIONS = declare("Exclusions", AssetList.class, ExclusionMetaData.class, false, By.xpath("//div[@id='policyDataGatherForm:componentView_PreconfigGroupExclusions']"));
    public static final AssetDescriptor<AssetList> SCHEDULE_OF_CONTINUATION_PROVISION = declare("Schedule of Continuation Provision", AssetList.class, ScheduleOfContinuationProvisionMetaData.class, By.id("policyDataGatherForm:componentRegion_PreconfigGroupTLContinuations"));
    public static final AssetDescriptor<ComboBox> PORTABILITY = declare("Portability", ComboBox.class);
    public static final AssetDescriptor<ComboBox> CONVERSION = declare("Conversion", ComboBox.class);
    public static final AssetDescriptor<TextBox> ACCELERATED_BENEFIT_PERCENTAGE = declare("Accelerated Benefit Percentage", TextBox.class);
    public static final AssetDescriptor<TextBox> ACCELERATED_BENEFIT_AMOUNT = declare("Accelerated Benefit Amount", TextBox.class);
    public static final AssetDescriptor<ComboBox> DISABILITY_OPTION = declare("Waiver of Premium", ComboBox.class);
    // public static final AssetDescriptor<ComboBox> LINE_OF_DUTY_BENEFIT = declare("Line of Duty Benefit", ComboBox.class);
    public static final AssetDescriptor<AssetList> COVERAGE_INCLUDED_IN_PACKAGE = declare("Coverage Included in Package", AssetList.class, CoverageIncludedInPackageMetaData.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> SPOUSE_TRAINING_BENEFIT = declare("Spouse Training Benefit", AssetList.class, SpouseTrainingBenefitMetaData.class, By.id("policyDataGatherForm:componentRegion_PreconfigGroupSpouseTrainingBenefit"));
    public static final AssetDescriptor<AssetList> ADAPTIVE_HOME_VEHICLE_BENEFIT = declare("Adaptive Home / Vehicle Benefit", AssetList.class, AdaptiveHomeVehicleBenefitMetaData.class, By.id("policyDataGatherForm:componentRegion_PreconfigGroupLifeAdaHomeVehBenefit"));
    public static final AssetDescriptor<AssetList> REHABILITATION_BENEFIT_AMOUNT = declare("Rehabilitation Benefit Amount", AssetList.class, RehabilitationBenefitAmountMetaData.class, By.id("policyDataGatherForm:componentRegion_PreconfigGroupLifeRehBenefit"));
    public static final AssetDescriptor<AssetList> BEREAVEMENT_BENEFIT = declare("Bereavement Benefit", AssetList.class, BereavementBenefitMetaData.class, By.id("policyDataGatherForm:componentRegion_RENBereavBenefitComponent"));
    public static final AssetDescriptor<AssetList> MEDICAL_PREMIUM_BENEFIT = declare("Medical Premium Benefit", AssetList.class, MedicalPremiumBenefitMetaData.class, By.id("policyDataGatherForm:componentRegion_RENMedicalPremiumBenefitComponent"));
    public static final AssetDescriptor<AssetList> FUNERAL_EXPENSE_BENEFIT = declare("Funeral Expense Benefit", AssetList.class, FuneralExpenseBenefitMetaData.class, By.id("policyDataGatherForm:componentRegion_RENFuneralExpenseBenefitComponent"));
    public static final AssetDescriptor<AssetList> HOSPITAL_INPATIENT_STAY_BENEFIT = declare("Hospital Inpatient Stay Benefit", AssetList.class, HospitalInpatientStayBenefitMetaData.class, By.id("policyDataGatherForm:componentRegion_RENHospitalInpatientStayBenefitComponent"));
    public static final AssetDescriptor<AssetList> MORTGAGE_BENEFIT = declare("Mortgage Benefit", AssetList.class, MortgageBenefitMetaData.class, By.id("policyDataGatherForm:componentRegion_RENMortgageStayBenefitComponent"));
    public static final AssetDescriptor<ComboBox> ACCELERATED_BENEFIT = declare("Accelerated Benefit", ComboBox.class);
    public static final AssetDescriptor<AssetList> ELIGIBILITY = declare("Eligibility", AssetList.class, EligibilityMetaData.class, By.id("policyDataGatherForm:componentRegion_PreconfigGroupEligibility"));
    public static final AssetDescriptor<AssetList> OPTIONS = declare("Options", AssetList.class, OptionsMetaData.class, By.id("policyDataGatherForm:componentViewPanelHeaderLabel_RENGroupContinuationOption"));
    public static final AssetDescriptor<AssetList> RATING = declare("Rating", AssetList.class, RatingMetaData.class, By.id("policyDataGatherForm:componentRegion_GroupCoverageDefinitionRatingView"));
    public static final AssetDescriptor<ComboBox> PLAN_SELECT = declare("Plan Select", ComboBox.class, By.id("policyDataGatherForm:sedit_GroupCoverageDefinitionCoverageCdViewOnly_packageCd"));
    public static final AssetDescriptor<AssetList> ADD_INSURANCE_LOSS_SCHEDULE = declare("Accidental Death and Dismemberment Insurance - Loss Schedule", AssetList.class, ADDInsuranceLossScheduleMetaData.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> AGE_REDUCTION = declare("Age Reduction", AssetList.class,  PlanDefinitionTabMetaData.class, By.id("policyDataGatherForm:componentView_PreconfigGroupAgeReductionAppliedView"));
    public static final AssetDescriptor<AgeReductionScheduleDetailsMultiAssetList> AGE_REDUCTION_SCHEDULE_DETAILS = declare("Age Reduction Schedule Details", AgeReductionScheduleDetailsMultiAssetList.class, AgeReductionScheduleDetailMetaData.class,
            By.id("policyDataGatherForm:formGrid_RenGroupAgeReductionScheduleSelection"));
    public static final AssetDescriptor<AssetList> GUARANTEED_ISSUE = declare("Guaranteed Issue", AssetList.class, GuaranteedIssueMetaData.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> REPATRIATION_BENEFIT = declare("Repatriation Benefit", AssetList.class, RepatriationBenefitMetaData.class, By.xpath("//div[@id='policyDataGatherForm:componentView_PreconfigGroupTLRepatriationBenefit']"));
    public static final AssetDescriptor<AssetList> COMA_BENEFIT = declare("Coma Benefit", AssetList.class, ComaBenefitMetaData.class, By.xpath("//div[@id='policyDataGatherForm:componentView_PreconfigGroupComaBenefit']"));
    public static final AssetDescriptor<AssetList> CHILD_CARE_BENEFIT = declare("Child Care Benefit", AssetList.class, ChildCareBenefitMetaData.class, By.xpath("//div[@id='policyDataGatherForm:componentView_PreconfigGroupChildCareBenefit']"));
    public static final AssetDescriptor<AssetList> COMMON_CARRIER_BENEFIT = declare("Common Carrier Benefit", AssetList.class, CommonCarrierBenefitMetaData.class, By.xpath("//div[@id='policyDataGatherForm:componentView_PreconfigGroupCommonCarrierBenefit']"));
    public static final AssetDescriptor<AssetList> SEAT_BELT_BENEFIT = declare("Seat Belt Benefit", AssetList.class, SeatBeltBenefitMetaData.class, By.xpath("//div[@id='policyDataGatherForm:componentView_PreconfigGroupTLSeatBeltBenefit']"));
    public static final AssetDescriptor<AssetList> AIR_BAG_BENEFIT = declare("Air Bag Benefit", AssetList.class, AirBagBenefitMetaData.class, By.xpath("//div[@id='policyDataGatherForm:componentView_PreconfigGroupTLAirBagBenefit']"));
    public static final AssetDescriptor<AssetList> CHILD_EDUCATION_BENEFIT = declare("Child Education Benefit", AssetList.class, ChildEducationBenefitMetaData.class, By.xpath("//div[@id='policyDataGatherForm:componentView_PreconfigGroupChildEducationBenefit']"));
    public static final AssetDescriptor<ComboBox> CONTINUATION = declare("Continuation", ComboBox.class);
    public static final AssetDescriptor<TextBox> ACCELERATED_BENEFIT_MINIMUM_PERCENTAGE = declare("Accelerated Benefit Minimum Percentage", TextBox.class);
    public static final AssetDescriptor<TextBox> ACCELERATED_BENEFIT_MAXIMUM_PERCENTAGE = declare("Accelerated Benefit Maximum Percentage", TextBox.class);
    public static final AssetDescriptor<TextBox> ACCELERATED_BENEFIT_MINIMUM_AMOUNT = declare("Accelerated Benefit Minimum Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> ACCELERATED_BENEFIT_MAXIMUM_AMOUNT = declare("Accelerated Benefit Maximum Amount", TextBox.class);
    public static final AssetDescriptor<ComboBox> WAIVER_OF_PREMIUM = declare("Waiver of Premium", ComboBox.class);
    public static final AssetDescriptor<ComboBox> WAIVER_DISABLED_PRIOR_TO_AGE = declare("Waiver disabled prior to age", ComboBox.class);
    public static final AssetDescriptor<ComboBox> TERMINATION_AGE = declare("Termination age", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PREMIUM_WAIVER_ELIMINATION_PERIOD = declare("Premium Waiver Elimination Period", ComboBox.class);
    public static final AssetDescriptor<AssetList> BENEFIT_SCHEDULE = declare("Benefit Schedule", AssetList.class, BenefitScheduleMetaData.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<ComboBox> ENROLLMENT_UNDERWRITING_OFFER = declare("Enrollment Underwriting Offer", ComboBox.class);
    public static final AssetDescriptor<TextBox> ENROLLMENT_UNDERWRITING_OFFER_OTHER_DESCRIPTION = declare("Enrollment Underwriting Offer -Other Description", TextBox.class);
    public static final AssetDescriptor<ComboBox> ASSUMED_PARTICIPATION = declare("Assumed Participation %", ComboBox.class);
    public static final AssetDescriptor<CheckBox> AGE_REDUCTION_CHECKBOX = declare("Age Reduction?", CheckBox.class,By.id("policyDataGatherForm:sedit_PreconfigGroupAgeReductionAppliedView_ageReductionApplied"));
    public static final AssetDescriptor<RadioGroup> CENSUS_VOLUME_REDUCED = declare("Census Volume Reduced", RadioGroup.class,By.id("policyDataGatherForm:sedit_PreconfigGroupAgeReduction_censusVolumeReducedInd"));
    public static final AssetDescriptor<TextBox> PLAN_TERMINATION_ATTAINED_AGE = declare("Plan Termination Attained Age", TextBox.class, Waiters.AJAX.then(Waiters.SLEEP(1000)).then(Waiters.AJAX),
            By.id("policyDataGatherForm:sedit_PreconfigGroupAgeReduction_terminationAge"));
    public static final AssetDescriptor<AssetList> BENEFIT_SCHEDULE_CHILD = declare("Benefit Schedule - Child", AssetList.class, BenefitScheduleChildMetaData.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> BENEFIT_SCHEDULE_SPOUSE = declare("Benefit Schedule - Spouse", AssetList.class, BenefitScheduleSpouseMetaData.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<ComboBox> POPULATION_TYPE = declare("Population Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> SIC_CODE = declare("SIC Code", TextBox.class);
    public static final AssetDescriptor<RadioGroup> SEPARATE_RETIREE_RATE = declare("Separate Retiree Rate", RadioGroup.class);

    public static final class ExclusionMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> AIRCRAFT = declare("Aircraft", CheckBox.class);
        public static final AssetDescriptor<CheckBox> BOARDING = declare("'Boarding, leaving or being in or on any kind of aircraft", CheckBox.class);
        public static final AssetDescriptor<CheckBox> DRIVING_UNDER_THE_INFLUENCE = declare("Driving Under the Influence", CheckBox.class);
        public static final AssetDescriptor<CheckBox> ALCOHOL_PRESENCE = declare("The presence of alcohol in the Covered Person's blood", CheckBox.class);
        public static final AssetDescriptor<CheckBox> HAZARD_SPORTS = declare("Hazardous Sports", CheckBox.class);
        public static final AssetDescriptor<CheckBox> ACTIVE_DUTY = declare("Active Duty", CheckBox.class);
        public static final AssetDescriptor<CheckBox> WORKER_COMPENSATION = declare("Worker's Compensation", CheckBox.class);
    }

    public static final class ScheduleOfContinuationProvisionMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> FAMILY_AND_MEDICAL_LEAVE_ACT = declare("Family & Medical Leave Act", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LAYOFF = declare("Layoff", ComboBox.class);
        public static final AssetDescriptor<ComboBox> MILITARY_LEAVE = declare("Military Leave", ComboBox.class);
        public static final AssetDescriptor<ComboBox> SABBATICAL = declare("Sabbatical", ComboBox.class);
		public static final AssetDescriptor<ComboBox> LAYOFF_DURATION = declare("Layoff Duration: (months)", ComboBox.class);
		public static final AssetDescriptor<ComboBox> MILITARY_LEAVE_DURATION = declare("Military Leave Duration: (months)", ComboBox.class);
		public static final AssetDescriptor<TextBox> SABBATICAL_DURATION = declare("Sabbatical (months)", TextBox.class);
    }

    public static final class SpouseTrainingBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class);
        public static final AssetDescriptor<TextBox> BENEFIT_PERCENTAGE = declare("Benefit Percentage", TextBox.class);
        public static final AssetDescriptor<TextBox> MAXIMUM_BENEFIT_AMOUNT = declare("Maximum Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> ENROLLED_WITH_MONTHS = declare("Enrolled with (months)", ComboBox.class, By.xpath("//*[@id='policyDataGatherForm:componentRegion_RenGroupTLEnrolledWithMonths']//select"));
    }

    public static final class AdaptiveHomeVehicleBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class);
        public static final AssetDescriptor<TextBox> PERCENTAGE_AMOUNT = declare("Percentage Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> MAXIMUM_BENEFIT_AMOUNT = declare("Maximum Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> COVERED_LOSS_MUST_OCCUR_WITHIN_MONTHS = declare("Covered Loss Must Occur Within (months of accident)", TextBox.class);
    }

    public static final class RehabilitationBenefitAmountMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class);
        public static final AssetDescriptor<TextBox> PERCENTAGE_AMOUNT = declare("Percentage Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> MAXIMUM_BENEFIT_AMOUNT = declare("Maximum Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> COVERED_LOSS_MUST_OCCUR_WITHIN_YEAR = declare("Covered Loss Must Occur Within (years of accident)", TextBox.class);
    }

    public static final class BereavementBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class);
        public static final AssetDescriptor<ComboBox> BENEFIT_AMOUNT_PER_SESSION = declare("Benefit Amount (per session)", ComboBox.class);
        public static final AssetDescriptor<TextBox> NUMBER_OF_SESSIONS = declare("Number of Sessions", TextBox.class);
        public static final AssetDescriptor<ComboBox> DURATION_YEARS = declare("Duration (years)", ComboBox.class);
    }

    public static final class MedicalPremiumBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class);
        public static final AssetDescriptor<ComboBox> BENEFIT_PERCENTAGE = declare("Benefit Percentage", ComboBox.class);
        public static final AssetDescriptor<TextBox> MAXIMUM_AMOUNT = declare("Maximum Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> BENEFIT_DURATION_MONTHS = declare("Benefit Duration (months)", ComboBox.class);
        public static final AssetDescriptor<TextBox> COVERED_LOSS_MUST_OCCUR_WITHIN_DAYS_OF_ACCIDENT = declare("Covered Loss Must Occur Within (days of accident)", TextBox.class);
    }

    public static final class FuneralExpenseBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class);
        public static final AssetDescriptor<ComboBox> BENEFIT_PERCENTAGE = declare("Benefit Percentage", ComboBox.class);
        public static final AssetDescriptor<TextBox> MAXIMUM_AMOUNT = declare("Maximum Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> COVERED_LOSS_MUST_OCCUR_WITHIN_DAYS_OF_ACCIDENT = declare("Covered Loss Must Occur Within (days of accident)", TextBox.class);
    }

    public static final class HospitalInpatientStayBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class);
        public static final AssetDescriptor<ComboBox> MONTHLY_BENEFIT_PERCENT = declare("Monthly Benefit Percent", ComboBox.class);
        public static final AssetDescriptor<TextBox> MONTHLY_MAXIMUM_AMOUNT = declare("Monthly Maximum Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> DURATION_MONTHS = declare("Duration (months)", TextBox.class);
        public static final AssetDescriptor<TextBox> HOSPITAL_INPATIENT_STAY_WAITING_PERIOD_DAYS = declare("Hospital Inpatient Stay Waiting Period (days)", TextBox.class);
        public static final AssetDescriptor<TextBox> COVERED_LOSS_MUST_OCCUR_WITHIN_DAYS_OF_ACCIDENT = declare("Covered Loss Must Occur Within (days of accident)", TextBox.class);
    }

    public static final class MortgageBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class);
        public static final AssetDescriptor<TextBox> BENEFIT_AMOUNT = declare("Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> DURATION_MONTHS = declare("Duration (Months)", TextBox.class);
        public static final AssetDescriptor<TextBox> COVERED_LOSS_MUST_OCCUR_WITHIN_DAYS_OF_ACCIDENT = declare("Covered Loss Must Occur Within (days of accident)", TextBox.class);
    }

    public static final class ADDInsuranceLossScheduleMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> LOSS_OF_BOTH_HANDS_OR_BOTH_FEET_BENEFIT = declare("Loss of Both Hands or Both Feet Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LOSS_OF_SIGHT_IN_BOTH_EYES_BENEFIT = declare("Loss of Sight in Both Eyes Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LOSS_OF_SIGHT_WAITING_PERIOD = declare("Loss of Sight - Waiting Period", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LOSS_OF_ONE_HAND_BENEFIT = declare("Loss of One Hand Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LOSS_OF_ONE_FOOT_BENEFIT = declare("Loss of One Foot Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LOSS_OF_ONE_ARM_BENEFIT = declare("Loss of One Arm Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LOSS_OF_ONE_LEG_BENEFIT = declare("Loss of One Leg benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LOSS_OF_SPEECH_BENEFIT = declare("Loss of Speech Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LOSS_OF_HEARING_BENEFIT = declare("Loss of Hearing Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LOSS_OF_HEARING_WAITING_PERIOD = declare("Loss of Hearing - Waiting Period", ComboBox.class);
        public static final AssetDescriptor<ComboBox> TRIPLEGIA_BENEFIT = declare("Triplegia Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LOSS_OF_LIFE_BENEFIT = declare("Loss of Life Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LOSS_OF_ONE_HAND_AND_ONE_FOOT_BENEFIT = declare("Loss of One Hand & One Foot Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LOSS_OF_ONE_HAND_AND_SIGHT_OF_ONE_EYE_BENEFIT = declare("Loss of One Hand & Sight of One Eye Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LOSS_OF_ONE_FOOT_AND_SIGHT_OF_ONE_EYE_BENEFIT = declare("Loss of One Foot & Sight of One Eye Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LOSS_OF_SIGHT_OF_ONE_EYE_BENEFIT = declare("Loss of Sight of One Eye Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> QUADRIPLEGIA_BENEFIT = declare("Quadriplegia Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> PARAPLEGIA_BENEFIT = declare("Paraplegia Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> HEMIPLEGIA_BENEFIT = declare("Hemiplegia Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> UNIPLEGIA_BENEFIT = declare("Uniplegia Benefit %", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LOSS_OF_THUMB_AND_INDEX_FINGER_OF_THE_SAME_HAND_BENEFIT = declare("Loss of Thumb & Index Finger of Same Hand Benefit %", ComboBox.class);
    }

    public static final class EligibilityMetaData extends MetaData {
        public static final AssetDescriptor<RadioGroup> DOES_MINIMUM_HOURLY_REQUIREMENT_APPLY = declare("Does Minimum Hourly Requirement Apply?", RadioGroup.class);
        public static final AssetDescriptor<ComboBox> ELIGIBILITY_WAITING_PERIOD_DEFINITION = declare("Eligibility Waiting Period Definition", ComboBox.class);
        public static final AssetDescriptor<TextBox> MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK = declare("Minimum Hourly Requirement (hours per week)", TextBox.class);
        public static final AssetDescriptor<TextBox> CHILD_STUDENT_TERMINATION_AGE = declare("Child/Student Termination Age", TextBox.class);
        public static final AssetDescriptor<RadioGroup> WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES = declare("Waiting Period Waived For Current Employees?", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> INCLUDE_DOMESTIC_PARTNER = declare("Include domestic partner?", RadioGroup.class);
        public static final AssetDescriptor<TextBox> WAITING_PERIOD_AMOUNT = declare("Waiting Period Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> WAITING_PERIOD_MODE = declare("Waiting Period Mode", ComboBox.class);
    }

    public static final class RatingMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> RATE_BASIS = declare("Rate Basis", ComboBox.class);
        public static final AssetDescriptor<TextBox> EXPERIENCE_CLAIM_AMOUNT = declare("Experience Claim Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> CREDIBILITY_FACTOR = declare("Credibility Factor", TextBox.class);
    }

    public static final class OptionsMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> DISABILITY_OPTION = declare("Waiver of Premium", ComboBox.class);
        public static final AssetDescriptor<TextBox> ACCELERATED_BENEFIT_PERCENTAGE = declare("Accelerated Benefit Percentage", TextBox.class);
        public static final AssetDescriptor<TextBox> ACCELERATED_BENEFIT_AMOUNT = declare("Accelerated Benefit Amount", TextBox.class);
    }

    public static final class SeatBeltBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class);
        public static final AssetDescriptor<TextBox> PERCENTAGE = declare("Percentage", TextBox.class);
        public static final AssetDescriptor<TextBox> MAX_AMOUNT = declare("Maximum Amount", TextBox.class);
    }

    public static final class AirBagBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class);
        public static final AssetDescriptor<TextBox> PERCENTAGE = declare("Percentage", TextBox.class);
        public static final AssetDescriptor<TextBox> MAX_AMOUNT = declare("Maximum Amount", TextBox.class);
    }

    public static final class ChildEducationBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class);
        public static final AssetDescriptor<TextBox> MAX_ANNUAL_BENEFIT_ALL_CHILDREN = declare("Maximum Annual Benefit All Children Combined", TextBox.class);
        public static final AssetDescriptor<TextBox> DEP_CHILDREN_MAX_AGE = declare("Dependent Children Maximum Age", TextBox.class);
        public static final AssetDescriptor<ComboBox> PERCENTAGE_CHILD_BENEFIT = declare("Percentage", ComboBox.class);
        public static final AssetDescriptor<TextBox> MAX_AMOUNT = declare("Maximum Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> DURATION_YEARS = declare("Duration (years)", ComboBox.class);
        public static final AssetDescriptor<TextBox> QUALIFIED_CHILD_AGE = declare("Qualified Child Age", TextBox.class);
        public static final AssetDescriptor<ComboBox> ENROLLED_POST_SECONDARY = declare("Enrolled in Post-Secondary Education within date of death (days)", ComboBox.class);
    }

    public static final class RepatriationBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class);
        public static final AssetDescriptor<ComboBox> PERCENTAGE_REP_BENEFIT = declare("Percentage", ComboBox.class);
        public static final AssetDescriptor<TextBox> MAX_AMOUNT = declare("Maximum Amount", TextBox.class);
    }

    public static final class ComaBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class);
        public static final AssetDescriptor<ComboBox> PERCENTAGE_COMA_BENEFIT = declare("Percentage", ComboBox.class);
        public static final AssetDescriptor<TextBox> MAX_AMOUNT = declare("Maximum Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> WAITING_PERIOD_DAYS = declare("Waiting Period (days)", ComboBox.class);
    }

    public static final class ChildCareBenefitMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class);
        public static final AssetDescriptor<ComboBox> PERCENTAGE_CHILD_BENEFIT = declare("Percentage", ComboBox.class);
        public static final AssetDescriptor<TextBox> MAX_AMOUNT = declare("Maximum Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> MAXIMUM_AGE = declare("Maximum Age", TextBox.class);
        public static final AssetDescriptor<ComboBox> CHILD_CARE_DURATION_YEARS = declare("Duration up to (years)", ComboBox.class);
    }

    public static final class CommonCarrierBenefitMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> MAX_AMOUNT = declare("Maximum Amount", TextBox.class);
        public static final AssetDescriptor<CheckBox> APPLY_BENEFIT = declare("Apply Benefit?", CheckBox.class);
    }

    public static final class AgeReductionScheduleDetailMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> AGE_DROP_DOWN = declare("Age", ComboBox.class);
        public static final AssetDescriptor<ComboBox> REDUCED_TO_PERCENTAGE = declare("Reduced To %", ComboBox.class);
        public static final AssetDescriptor<TextBox> MAXIMUM_TEXTBOX = declare("Maximum", TextBox.class);
        public static final AssetDescriptor<Button> ADD_SCHEDULE_DETAILS = declare("Add Schedule Details", Button.class, By.xpath("//input[@id='policyDataGatherForm:addRenGroupAgeReductionScheduleSelection']"));
    }

    public static final class GuaranteedIssueMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> GI_AMOUNT = declare("GI Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> GI_AMOUNT_AT_AGE = declare("GI Amount At Age", ComboBox.class);
        public static final AssetDescriptor<TextBox> CALCULATED_GI_AMOUNT = declare("Calculated GI Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> AGE_LIMITED_GI_AMOUNT = declare("Age Limited GI Amount", TextBox.class);
    }

    public static final class BenefitScheduleMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> BENEFIT_TYPE = declare("Benefit Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> SALARY_MULTIPLE = declare("Salary Multiple", ComboBox.class);
        public static final AssetDescriptor<ComboBox> MAXIMUM_SALARY_MULTIPLE = declare("Maximum Salary Multiple", ComboBox.class);
        public static final AssetDescriptor<TextBox> ADDITIONAL_AMOUNT = declare("Additional Amount", TextBox.class);
        public static final AssetDescriptor<AdvancedSelector> SALARY_MULTIPLES = declare("Salary Multiples", AdvancedSelector.class);
        public static final AssetDescriptor<TextBox> BENEFIT_MAXIMUM_AMOUNT = declare("Benefit Maximum Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> BENEFIT_MINIMUM_AMOUNT = declare("Benefit Minimum Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> FLAT_BENEFIT_AMOUNT = declare("Flat Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> LINE_OF_DUTY_BENEFIT = declare("Line of Duty Benefit", ComboBox.class);
        public static final AssetDescriptor<ComboBox> ROUNDING_METHOD = declare("Rounding Method", ComboBox.class);
        public static final AssetDescriptor<ComboBox> EARNING_DEFINITION = declare("Earning Definition", ComboBox.class);
        public static final AssetDescriptor<TextBox> INCREMENT = declare("Increment", TextBox.class);
        public static final AssetDescriptor<AdvancedSelector> SPECIFIED_AMOUNTS = declare("Specified Amounts", AdvancedSelector.class);
    }

    public static final class  CoverageIncludedInPackageMetaData  extends MetaData {
        public static final AssetDescriptor<CheckBox> LTD = declare("LTD", CheckBox.class);
        public static final AssetDescriptor<CheckBox> STD = declare("STD", CheckBox.class);
        public static final AssetDescriptor<CheckBox> DENTAL = declare("DENTAL", CheckBox.class);
        public static final AssetDescriptor<CheckBox> VISION = declare("VISION", CheckBox.class);
    }

    public static final class BenefitScheduleChildMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> BENEFIT_TYPE = declare("Benefit Type", ComboBox.class, By.id("policyDataGatherForm:sedit_PreconfigGroupChildBenefitTypeView_proxiedComponents_PreconfigGroupBenefitAmountOptionsTiered_childBenefitType"));
        public static final AssetDescriptor<TextBox> SIX_MONTHS_TO_MAX_AGE = declare("6 months to max age", TextBox.class);
        public static final AssetDescriptor<TextBox> LIVE_BIRTH_TO_FOURTEEN_DAYS = declare("Live birth to 14 days", TextBox.class);
        public static final AssetDescriptor<TextBox> FIFTEEN_DAYS_TO_SIX_MONTH = declare("15 days to 6 months", TextBox.class);
        public static final AssetDescriptor<TextBox> INCREMENT = declare("Increment", TextBox.class,  By.id("policyDataGatherForm:sedit_PreconfigGroupTLChildBAO_increment"));
        public static final AssetDescriptor<TextBox> MAXIMUM_BENEFIT_AMOUNT = declare("Maximum Benefit Amount", TextBox.class, By.id("policyDataGatherForm:sedit_PreconfigGroupTLChildBAO_maxAmount"));
        public static final AssetDescriptor<TextBox> MINIMUM_BENEFIT_AMOUNT = declare("Minimum Benefit Amount", TextBox.class, By.id("policyDataGatherForm:sedit_PreconfigGroupTLChildBAO_minAmount"));
        public static final AssetDescriptor<ComboBox> COVERAGE_UP_TO_MAX_OF_EMPLOYEE_COVERAGE = declare("Coverage up to Max of Employee Coverage", ComboBox.class,  By.id("policyDataGatherForm:sedit_PreconfigGroupTLChildBAOView_maxPctOfEmpCoverage"));
        public static final AssetDescriptor<AdvancedSelector> SPECIFIED_AMOUNTS = declare("Specified Amounts", AdvancedSelector.class);
    }

    public static final class BenefitScheduleSpouseMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> BENEFIT_TYPE = declare("Benefit Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> FLAT_BENEFIT_AMOUNT = declare("Flat Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> MINIMUM_BENEFIT_AMOUNT = declare("Minimum Benefit Amount", TextBox.class, By.id("policyDataGatherForm:sedit_PreconfigGroupTLSpouseBAO_minAmount"));
        public static final AssetDescriptor<TextBox> INCREMENT = declare("Increment", TextBox.class,  By.id("policyDataGatherForm:sedit_PreconfigGroupTLSpouseBAO_increment"));
        public static final AssetDescriptor<ComboBox> COVERAGE_UP_TO_MAX_OF_EMPLOYEE_COVERAGE = declare("Coverage up to Max of Employee Coverage", ComboBox.class, By.id("policyDataGatherForm:sedit_PreconfigGroupTLSpouseBAOView_maxPctOfEmpCoverage"));
        public static final AssetDescriptor<TextBox> MAXIMUM_BENEFIT_AMOUNT = declare("Maximum Benefit Amount", TextBox.class, By.id("policyDataGatherForm:sedit_PreconfigGroupTLSpouseBAO_maxAmount"));
        public static final AssetDescriptor<AdvancedSelector> SPECIFIED_AMOUNTS = declare("Specified Amounts", AdvancedSelector.class);
    }
}
