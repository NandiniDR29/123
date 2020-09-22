package com.exigen.ren.main.modules.policy.common.metadata.master;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.webdriver.controls.CheckBox;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class PlanDefinitionEndorseNPBInfoActionTabMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> COVERAGE_NAME = declare("Coverage Name", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PLAN = declare("Plan", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> GRANDFATHER_PLAN = declare("Grandfathered Plan", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> POPULATION_TYPE = declare("Population Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> PLAN_NAME = declare("Plan Name", TextBox.class);
    public static final AssetDescriptor<TextBox> VSP_DIVISION = declare("VSP Division", TextBox.class);
    public static final AssetDescriptor<ComboBox> ENROLLMENT_UNDERWRITING_OFFER = declare("Enrollment Underwriting Offer", ComboBox.class);
    public static final AssetDescriptor<TextBox> ENROLLMENT_UNDERWRITING_OFFER_OTHER_DESCRIPTION = declare("Enrollment Underwriting Offer -Other Description", TextBox.class);
    public static final AssetDescriptor<TextBox> PRIOR_COVERAGE = declare("Prior Coverage %", TextBox.class);
    public static final AssetDescriptor<TextBox> SIC_CODE = declare("SIC Code", TextBox.class);
    public static final AssetDescriptor<ComboBox> SIC_DESCRIPTION = declare("SIC Description", ComboBox.class);
    public static final AssetDescriptor<CheckBox> PRIOR_COVERAGE_CHECK_BOX = declare("Prior Coverage?", CheckBox.class);
    public static final AssetDescriptor<ComboBox> CENSUS_TYPE = declare("Census Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> NETWORK = declare("Network", ComboBox.class);
    public static final AssetDescriptor<ComboBox> NUMBER_OF_UNITS = declare("Number of Units", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> ENHANCED_AD_D = declare("Enhanced AD&D?", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> COVERAGE_TYPE = declare("Coverage Type", ComboBox.class);
    public static final AssetDescriptor<AdvancedSelector> COVERAGE_TIERS = declare("Coverage Tiers", AdvancedSelector.class, By.id("policyDataGatherForm:searchAvailableTiers"));
    public static final AssetDescriptor<TextBox> TOTAL_NUMBER_ELIGIBLE_LIVES = declare("Total Number of Eligible Lives", TextBox.class);
    public static final AssetDescriptor<ComboBox> ASSUMED_PARTICIPATION = declare("Assumed Participation %", ComboBox.class);
    public static final AssetDescriptor<TextBox> DENTAL_PLAN_DESIGN_CODE = declare("Dental Plan Design Code", TextBox.class);
    public static final AssetDescriptor<TextBox> DEPENDENT_MAXIMUM_AGE = declare("Dependent Maximum Age", TextBox.class);
    public static final AssetDescriptor<ComboBox> COVERAGE_BASIS = declare("Coverage Basis", ComboBox.class);
    public static final AssetDescriptor<ComboBox> REQUIRED_PARTICIPATION = declare("Required Participation %", ComboBox.class);
    public static final AssetDescriptor<AssetList> ELIGIBILITY = declare("Eligibility", AssetList.class, PlanDefinitionEndorseNPBInfoActionTabMetaData.EligibilityMetadata.class, false,
            By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> COVERAGE_INCLUDED_IN_PACKAGE = declare("Coverage Included in Package", AssetList.class, CoverageIncludedInPackageMetaData.class, false, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

    public static final class EligibilityMetadata extends MetaData {
        public static final AssetDescriptor<RadioGroup> DOES_MIN_HOURLY_REQUIREMENT_APPLY = declare("Does Min Hourly Requirement Apply?", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> DOES_MIN_HOURLY_REQUIREMENT_APPLY_VS_DN = declare("Does Minimum Hourly Requirement Apply?", RadioGroup.class);
        public static final AssetDescriptor<TextBox> MINIMUM_HOURLY_REQUIREMENT = declare("Minimum Hourly Requirement (hours per week)", TextBox.class);
        public static final AssetDescriptor<ComboBox> ELIGIBILITY_WAITING_PERIOD_DEFINITION = declare("Eligibility Waiting Period Definition", ComboBox.class);
        public static final AssetDescriptor<TextBox> WAITING_PERIOD_AMOUNT = declare("Waiting Period Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> WAITING_PERIOD_MODE = declare("Waiting Period Mode", ComboBox.class);
        public static final AssetDescriptor<TextBox> WAITING_PERIOD = declare("Waiting Period", TextBox.class);
        public static final AssetDescriptor<TextBox> DEPENDENT_MAXIMUM_AGE = declare("Dependent Maximum Age", TextBox.class);
        public static final AssetDescriptor<RadioGroup> INCLUDE_DISABLED_DEPENDENTS = declare("Include Disabled Dependents", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> ELIGIBILITY_WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES = declare("Eligibility Waiting Period Waived For Current Employees?", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES = declare("Waiting Period Waived For Current Employees?", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES_TL = declare("Waiting Period Waived for Current Employees?", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> ALLOW_MEMBER_AND_SPOUSE = declare("Allow Member and Spouse (Who are part of Group) on Separate Certificate?", RadioGroup.class);
        public static final AssetDescriptor<ComboBox> BENEFITS_END_ON = declare("Benefits End On", ComboBox.class);
    }

    public static final class CoverageIncludedInPackageMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> STD = declare("STD", CheckBox.class);
        public static final AssetDescriptor<CheckBox> LIFE = declare("LIFE", CheckBox.class);
        public static final AssetDescriptor<CheckBox> DENTAL = declare("DENTAL", CheckBox.class);
        public static final AssetDescriptor<CheckBox> VISION = declare("VISION", CheckBox.class);
        public static final AssetDescriptor<CheckBox> STD_ADMINISTERED = declare("STD Administered?", CheckBox.class);
        public static final AssetDescriptor<ComboBox> INITIAL_ENROLLMENT_UNDERWRITING_OFFER = declare("Initial Enrollment Underwriting Offer", ComboBox.class);
        public static final AssetDescriptor<ComboBox> ANNUAL_ENROLLMENT_UNDERWRITING_OFFER = declare("Annual Enrollment Underwriting Offer", ComboBox.class);
        public static final AssetDescriptor<ComboBox> UNDERWRITING_OFFER_FOR_QUALIFYING_LIFE_EVENT = declare("Underwriting Offer for Qualifying Life Event", ComboBox.class);
    }
}
