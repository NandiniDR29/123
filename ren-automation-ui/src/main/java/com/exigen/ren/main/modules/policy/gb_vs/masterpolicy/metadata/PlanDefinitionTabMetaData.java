/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class PlanDefinitionTabMetaData extends MetaData {
    public static final AssetDescriptor<AdvancedSelector> PLAN = declare("Plan", AdvancedSelector.class);
    public static final AssetDescriptor<TextBox> PLAN_NAME = declare("Plan Name", TextBox.class);
    public static final AssetDescriptor<TextBox> VSP_DIVISION = declare("VSP Division", TextBox.class, Waiters.AJAX.then(Waiters.SLEEP(1000)));
    public static final AssetDescriptor<AdvancedSelector> COVERAGE_TIERS = declare("Coverage Tiers", AdvancedSelector.class);
    public static final AssetDescriptor<Button> COVERAGE_TIERS_CHANGE_CONFIRMATION = declare("Coverage Tiers Change Confirmation", Button.class, By.id("tierChangeWarningPopupForm:okBtn"));
    public static final AssetDescriptor<ComboBox> CENSUS_TYPE = declare("Census Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> NETWORK = declare("Network", ComboBox.class);

    public static final AssetDescriptor<AssetList> COVERAGE_INCLUDED_IN_PACKAGE = declare("Coverage Included in Package", AssetList.class, CoverageIncludedInPackage.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> SPONSOR_PARTICIPANT_FUNDING_STRUCTURE = declare("Sponsor/Participant Funding Structure", AssetList.class, SponsorParticipantFundingStructure.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> RATING = declare("Rating", AssetList.class, RatingMetadata.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> ELIGIBILITY = declare("Eligibility", AssetList.class, EligibilityMetadata.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> COPAY = declare("Copay", AssetList.class, CopayMetadata.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> PLAN_BENEFITS = declare("Plan Benefits", AssetList.class, PlanBenefitsMetadata.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> FREQUENCY = declare("Frequency", AssetList.class, FrequencyMetadata.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> SPECIAL_PLAN_FEATURES = declare("Special Plan Features", AssetList.class, SpecialPlanFeaturesMetadata.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> OUT_OF_NETWORK_COVERAGE = declare("Out of Network Coverage", AssetList.class, OutOfNetworkCoverageMetadata.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

    public static final class CoverageIncludedInPackage extends MetaData {
        public static final AssetDescriptor<CheckBox> STD = declare("STD", CheckBox.class);
        public static final AssetDescriptor<CheckBox> LTD = declare("LTD", CheckBox.class);
        public static final AssetDescriptor<CheckBox> LIFE = declare("LIFE", CheckBox.class);
        public static final AssetDescriptor<CheckBox> DENTAL = declare("DENTAL", CheckBox.class);
    }

    public static final class SponsorParticipantFundingStructure extends MetaData {
        public static final AssetDescriptor<RadioGroup> SELF_ADMINISTERED = declare("Self-administered?", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> DIRECT_BILL = declare("Direct Bill", RadioGroup.class);
        public static final AssetDescriptor<ComboBox> CONTRIBUTION_TYPE = declare("Contribution Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> PARTICIPANT_CONTRIBUTION = declare("Participant Contribution %", TextBox.class);
        public static final AssetDescriptor<TextBox> ASSUMED_PARTICIPATION_PERCENTAGE = declare("Assumed Participation %", TextBox.class);
        public static final AssetDescriptor<RadioGroup> IS_CONTRIBUTION_PERCENTAGE_BASED = declare("Is Contribution Percentage Based?", RadioGroup.class);
        public static final AssetDescriptor<TextBox> PARTICIPANT_CONTRIBUTION_PERCENTAGE_EMPLOYEE_COVERAGE = declare("Participant Contribution % Employee Coverage", TextBox.class);
        public static final AssetDescriptor<TextBox> PARTICIPANT_CONTRIBUTION_PERCENTAGE_DEPENDENT_COVERAGE = declare("Participant Contribution % Dependent Coverage", TextBox.class);
        public static final AssetDescriptor<TextBox> SPONSOR_CONTRIBUTION_AMOUNT_EMPLOYEE_COVERAGE_MONTHLY = declare("Sponsor Contribution Amount Employee Coverage (monthly)", TextBox.class);
        public static final AssetDescriptor<TextBox> SPONSOR_CONTRIBUTION_AMOUNT_DEPENDENT_COVERAGE_MONTHLY = declare("Sponsor Contribution Amount Dependent Coverage (monthly)", TextBox.class);
        public static final AssetDescriptor<TextBox> MINIMUM_NUMBER_OF_PARTICIPANTS = declare("Minimum Number of Participants", TextBox.class);
        public static final AssetDescriptor<ComboBox> SPONSOR_PAYMENT_MODE = declare("Sponsor Payment Mode", ComboBox.class);
        public static final AssetDescriptor<AdvancedSelector> MEMBER_PAYMENT_MODE = declare("Member Payment Mode", AdvancedSelector.class);
    }

    public static final class RatingMetadata extends MetaData {
        public static final AssetDescriptor<ComboBox> RATE_TYPE = declare("Rate Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> RATE_BASIS = declare("Rate Basis", ComboBox.class);
        public static final AssetDescriptor<RadioGroup> USE_BROCHURE_RATES = declare("Use Brochure Rates?", RadioGroup.class);
        public static final AssetDescriptor<ComboBox> PLAN_TYPE = declare("Plan Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> EXPERIENCE_CLAIM_AMOUNT = declare("Experience Claim Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> CREDIBILITY_FACTOR = declare("Credibility Factor", TextBox.class);
        public static final AssetDescriptor<ComboBox> RATE_CAP = declare("Rate Cap", ComboBox.class);
        public static final AssetDescriptor<TextBox> SECOND_YEAR_CAP_PCT = declare("2nd Year Cap %", TextBox.class);
        public static final AssetDescriptor<TextBox> THIRD_YEAR_CAP_PCT = declare("3rd Year Cap %", TextBox.class);
    }

    public static final class EligibilityMetadata extends MetaData {
        public static final AssetDescriptor<RadioGroup> DOES_MINIMUM_HOURLY_REQUIREMENT_APPLY = declare("Does Minimum Hourly Requirement Apply?", RadioGroup.class);
        public static final AssetDescriptor<TextBox> MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK = declare("Minimum Hourly Requirement (hours per week)", TextBox.class);
        public static final AssetDescriptor<RadioGroup> ELIGIBILITY_WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEE = declare("Eligibility Waiting Period Waived For Current Employees?", RadioGroup.class);
        public static final AssetDescriptor<ComboBox> ELIGIBILITY_WAITING_PERIOD_DEFINITION = declare("Eligibility Waiting Period Definition", ComboBox.class);
        public static final AssetDescriptor<TextBox> WAITING_PERIOD_AMOUNT = declare("Waiting Period Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> WAITING_PERIOD_MODE = declare("Waiting Period Mode", ComboBox.class);
        public static final AssetDescriptor<TextBox> DEPENDENT_MAXIMUM_AGE = declare("Dependent Maximum Age", TextBox.class);
        public static final AssetDescriptor<RadioGroup> INCLUDE_DISABLED_DEPENDENTS = declare("Include Disabled Dependents", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> ALLOW_MEMBER_AND_SPOUSE_ON_SEPARATE_CERTIFICATE = declare("Allow Member and Spouse (Who are part of Group) on Separate Certificate?", RadioGroup.class);
        public static final AssetDescriptor<ComboBox> BENEFIT_END_ON = declare("Benefits End On", ComboBox.class);
    }

    public static final class CopayMetadata extends MetaData {
        public static final AssetDescriptor<ComboBox> EXAM_MATERIALS = declare("Exam/Materials", ComboBox.class);
        public static final AssetDescriptor<ComboBox> MEDICALLY_NECESSARY_CONTACT_LENSES = declare("Medically Necessary Contact Lenses", ComboBox.class);
        public static final AssetDescriptor<RadioGroup> PROGRESSIVE_LENSES_IN_FULL = declare("Progressive Lenses in Full", RadioGroup.class);
        public static final AssetDescriptor<ComboBox> LENS_ENHANCEMENTS_STANDARD = declare("Lens Enhancements - Standard", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LENS_ENHANCEMENTS_PREMIUM= declare("Lens Enhancements - Premium", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LENS_ENHANCEMENTS_CUSTOM = declare("Lens Enhancements - Custom", ComboBox.class);
    }

    public static final class PlanBenefitsMetadata extends MetaData {
        public static final AssetDescriptor<ComboBox> FRAMES_ALLOWANCE_UP_TO = declare("Frames - Allowance up to", ComboBox.class);
        public static final AssetDescriptor<ComboBox> CONTACT_LENSES_ALLOWANCE_UP_TO = declare("Contact Lenses - Allowance up to", ComboBox.class);
    }

    public static final class FrequencyMetadata extends MetaData {
        public static final AssetDescriptor<ComboBox> PLAN_LIMITATION = declare("Plan Limitation", ComboBox.class);
        public static final AssetDescriptor<ComboBox> FREQUENCY_DEFINITION = declare("Frequency Definition", ComboBox.class);
        public static final AssetDescriptor<ComboBox> EXAM_LENSES_FRAME = declare("Exam/Lenses/Frame", ComboBox.class);
        public static final AssetDescriptor<ComboBox> CONTACTS = declare("Contacts", ComboBox.class);
    }

    public static final class SpecialPlanFeaturesMetadata extends MetaData {
        public static final AssetDescriptor<TextBox> PHOTOCHROMIC_LENSES_FACTOR = declare("Photochromic Lenses Factor", TextBox.class);
        public static final AssetDescriptor<TextBox> SCRATCH_COATING_FACTOR = declare("Scratch Coating Factor", TextBox.class);
        public static final AssetDescriptor<TextBox> SAFETY_GLASSES_FACTOR = declare("Safety Glasses Factor", TextBox.class);
    }

    public static final class OutOfNetworkCoverageMetadata extends MetaData {
        public static final AssetDescriptor<TextBox> EXAM_UP_TO = declare("Exam - Up to", TextBox.class);
        public static final AssetDescriptor<TextBox> FRAME_UP_TO = declare("Frame - Up to", TextBox.class);
        public static final AssetDescriptor<TextBox> SINGLE_VISION_LENSES_UP_TO = declare("Single Vision Lenses - Up to", TextBox.class);
        public static final AssetDescriptor<TextBox> LINED_BIFOCAL_LENSES_UP_TO = declare("Lined Bifocal Lenses - Up to", TextBox.class);
        public static final AssetDescriptor<TextBox> LINED_TRIFOCAL_LENSES_UP_TO = declare("Lined Trifocal Lenses - Up to", TextBox.class);
        public static final AssetDescriptor<TextBox> PROGRESSIVE_LENSES_UP_TO = declare("Progressive Lenses - Up to", TextBox.class);
        public static final AssetDescriptor<TextBox> LENTICULAR_LENSES_UP_TO = declare("Lenticular Lenses - Up to", TextBox.class);
        public static final AssetDescriptor<ComboBox> CONTACTS_UP_TO = declare("Contacts - Up to", ComboBox.class);
    }
}
