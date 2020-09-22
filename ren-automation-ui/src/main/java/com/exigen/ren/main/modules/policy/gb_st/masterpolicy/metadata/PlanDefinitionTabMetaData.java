/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class PlanDefinitionTabMetaData extends MetaData {
    public static final AssetDescriptor<Button> ADD_COVERAGE = declare("Add Coverage", Button.class, By.id("policyDataGatherForm:addPreconfigGroupCoverageDefinition"));
    public static final AssetDescriptor<ComboBox> PLAN = declare("Plan", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COVERAGE_NAME = declare("Coverage Name", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SPONSOR_PAYMENT_MODE = declare("Sponsor Payment Mode", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> SELF_ADMINISTERED = declare("Self-administered?", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> FICA_MATCH = declare("FICA Match", ComboBox.class);
    public static final AssetDescriptor<TextBox> TOTAL_NUMBER_OF_ELIGIBLE_LIVES = declare("Total Number of Eligible Lives", TextBox.class);
    public static final AssetDescriptor<TextBox> COVERAGE_EFFECTIVE_DATE = declare("Coverage Effective Date", TextBox.class);
    public static final AssetDescriptor<AdvancedSelector> MEMBER_PAYMENT_MODE = declare("Member Payment Mode", AdvancedSelector.class);
    public static final AssetDescriptor<AssetList> ELIGIBILITY = declare("Eligibility", AssetList.class, EligibilityMetadata.class, false,
            By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> RATING = declare("Rating", AssetList.class, RatingMetadata.class, false,
            By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> PREMIUM_AND_RATE_HISTORY = declare("Premium and Rate History", AssetList.class, PremiumAndRateHistoryMetaData.class, false,
            By.xpath("//div[@id='policyDataGatherForm:componentView_RenGroupRatingHistoryComponent']"));

    public static final AssetDescriptor<AssetList> RATE_HISTORY_MALE = declare("Rate History - Male", AssetList.class, RateHistoryMaleMetaData.class, false,
            By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

    public static final AssetDescriptor<AssetList> RATE_HISTORY_FEMALE = declare("Rate History - Female", AssetList.class, RateHistoryFemaleMetaData.class, false,
            By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

    public static final AssetDescriptor<AssetList> HISTORICAL_CLAIM = declare("Historical Claim", AssetList.class, HistoricalClaimMetaData.class, false,
            By.xpath("//div[@id='policyDataGatherForm:componentView_RenGroupClaimHistoryComponent']"));

    public static final AssetDescriptor<AssetList> EXCLUSIONS = declare("Exclusions", AssetList.class, ExclusionsMetadata.class, false,
            By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<TextBox> PLAN_NAME = declare("Plan Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> TAXABILITY = declare("Taxability", ComboBox.class);
    public static final AssetDescriptor<ComboBox> CONTRIBUTION_TYPE = declare("Contribution Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> PARTICIPANT_CONTRIBUTION = declare("Participant Contribution %", TextBox.class);
    public static final AssetDescriptor<TextBox> MINIMUM_NUMBER_OF_PARTICIPANTS = declare("Minimum Number of Participants", TextBox.class);
    public static final AssetDescriptor<RadioGroup> PREMIUMS_PAID_POST_TAX = declare("Premiums Paid-Post Tax", RadioGroup.class);
    public static final AssetDescriptor<AssetList> BENEFIT_SCHEDULE = declare("Benefit Schedule", AssetList.class, BenefitScheduleMetadata.class, false, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> OFFSETS = declare("Offsets", AssetList.class, OffsetsMetadata.class, false, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> OPTIONS = declare("Options", AssetList.class, OptionsMetaData.class, false, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<ComboBox> CENSUS_TYPE = declare("Census Type", ComboBox.class, Waiters.DEFAULT);

    public static final class EligibilityMetadata extends MetaData {
        public static final AssetDescriptor<TextBox> MINIMUM_HOURLY_REQUIREMENT = declare("Minimum Hourly Requirement (hours per week)", TextBox.class);
        public static final AssetDescriptor<TextBox> WAITING_PERIOD = declare("Waiting Period", TextBox.class);
        public static final AssetDescriptor<ComboBox> WAITING_PERIOD_MODE = declare("Waiting Period Mode", ComboBox.class);
        public static final AssetDescriptor<TextBox> CONTINUOUS_PERIOD_WEEKS = declare("Continuous Working Period (weeks)", TextBox.class);
    }

    public static final class RatingMetadata extends MetaData {
        public static final AssetDescriptor<ComboBox> RATE_BASIS = declare("Rate Basis", ComboBox.class);
        public static final AssetDescriptor<TextBox> SIC_CODE = declare("SIC Code", TextBox.class);
        public static final AssetDescriptor<RadioGroup> USE_EXPERIENCE_RATING = declare("Use Experience Rating?", RadioGroup.class);
        public static final AssetDescriptor<TextBox> EMPLOYER_MAXIMUM_ANNUAL_TAXABLE_WAGE_PER_PERSON = declare("Employer Maximum Annual Taxable Wage Per Person", TextBox.class);
        public static final AssetDescriptor<TextBox> NUMBER_OF_LIVES_MALE = declare("Number of Lives - Male", TextBox.class);
        public static final AssetDescriptor<TextBox> NUMBER_OF_LIVES_FEMALE = declare("Number of Lives - Female", TextBox.class);
        public static final AssetDescriptor<TextBox> INFORCE_RATE_MALE = declare("Inforce Rate - Male", TextBox.class);
        public static final AssetDescriptor<TextBox> INFORCE_RATE_FEMALE = declare("Inforce Rate - Female", TextBox.class);
        public static final AssetDescriptor<TextBox> ANNUAL_PREMIUM = declare("Annual Premium", TextBox.class);
        public static final AssetDescriptor<TextBox> ANNUAL_COVERED_PAYROLL = declare("Annual Covered Payroll", TextBox.class);
        public static final AssetDescriptor<TextBox> ANNUAL_TAXABLE_WAGE_PER_PERSON = declare("Annual Taxable Wage Per Person", TextBox.class);
        public static final AssetDescriptor<TextBox> TOTAL_TAXABLE_WAGE = declare("Total Taxable Wage", TextBox.class);

        public static final AssetDescriptor<TextBox> STATEWIDE_MAX_COVERED_PAYROLL = declare("Statewide Maximum Covered Payroll", TextBox.class);
    }

    public static final class PremiumAndRateHistoryMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> PERIOD = declare("Period", TextBox.class);
        public static final AssetDescriptor<TextBox> PERIOD_BEGIN_DATE = declare("Period Begin Date", TextBox.class);
        public static final AssetDescriptor<TextBox> PERIOD_END_DATE = declare("Period End Date", TextBox.class);
        public static final AssetDescriptor<TextBox> PREMIUM = declare("Premium", TextBox.class);
        public static final AssetDescriptor<TextBox> EXPERIENCE_PERIOD_ADJUSTMENT = declare("Experience Period Adjustment", TextBox.class);
        public static final AssetDescriptor<TextBox> COMPOSITE_RATE = declare("Composite Rate", TextBox.class);
        public static final AssetDescriptor<Button> ADD = declare("Add Premium and Rate History", Button.class, By.id("policyDataGatherForm:addRenGroupRatingHistoryComponent"));
        public static final AssetDescriptor<Button> REMOVE = declare("Remove Premium and Rate History", Button.class, By.id("policyDataGatherForm:eliminateRenGroupRatingHistoryComponent"));
    }

    public static final class RateHistoryMaleMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> RATE_1 = declare("Rate 1", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_RenGroupRateHistoryMaleOneComponent_rate']"));
        public static final AssetDescriptor<ComboBox> NUMBER_OF_MONTHS_RATE_1 = declare("Number of Months Rate 1", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_RenGroupRateHistoryMaleOneComponent_numberOfMonths']"));
        public static final AssetDescriptor<TextBox> RATE_2 = declare("Rate 2", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_RenGroupRateHistoryMaleTwoComponent_rate']"));
        public static final AssetDescriptor<ComboBox> NUMBER_OF_MONTHS_RATE_2 = declare("Number of Months Rate 2", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_RenGroupRateHistoryMaleTwoComponent_numberOfMonths']"));
    }

    public static final class RateHistoryFemaleMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> RATE_1 = declare("Rate 1", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_RenGroupRateHistoryFemaleOneComponent_rate']"));
        public static final AssetDescriptor<ComboBox> NUMBER_OF_MONTHS_RATE_1 = declare("Number of Months Rate 1", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_RenGroupRateHistoryFemaleOneComponent_numberOfMonths']"));
        public static final AssetDescriptor<TextBox> RATE_2 = declare("Rate 2", TextBox.class, By.xpath("//input[@id='policyDataGatherForm:sedit_RenGroupRateHistoryFemaleTwoComponent_rate']"));
        public static final AssetDescriptor<ComboBox> NUMBER_OF_MONTHS_RATE_2 = declare("Number of Months Rate 2", ComboBox.class, By.xpath("//select[@id='policyDataGatherForm:sedit_RenGroupRateHistoryFemaleTwoComponent_numberOfMonths']"));
    }

    public static final class HistoricalClaimMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> CLAIMS = declare("Claims", TextBox.class);
        public static final AssetDescriptor<TextBox> CLAIM_RESERVES = declare("Claim Reserves", TextBox.class);
        public static final AssetDescriptor<TextBox> UNDERWRITER_ADJUSTMENT = declare("Underwriter Adjustment", TextBox.class);
    }


    public static final class ExclusionsMetadata extends MetaData {
        public static final AssetDescriptor<CheckBox> UNEMPLOYMENT = declare("Unemployment", CheckBox.class);
        public static final AssetDescriptor<CheckBox> WORKERS_COMPENSATION = declare("Worker's Compensation", CheckBox.class);
        public static final AssetDescriptor<CheckBox> NOT_DISABLED = declare("Not Disabled", CheckBox.class);
        public static final AssetDescriptor<CheckBox> NOT_UNDER_CARE_OF_DOCTOR = declare("Not Under Care of Doctor (dependents)", CheckBox.class);
        public static final AssetDescriptor<CheckBox> SELF_INFLICTED = declare("Self-Inflicted", CheckBox.class);
        public static final AssetDescriptor<CheckBox> INFLICTED_ANOTHER = declare("Inflicted Another", CheckBox.class);
        public static final AssetDescriptor<CheckBox> CRIME = declare("Crime", CheckBox.class);
        public static final AssetDescriptor<CheckBox> MISCONDUCT = declare("Misconduct", CheckBox.class);
        public static final AssetDescriptor<CheckBox> LABOR_DISPUTE = declare("Labor Dispute", CheckBox.class);
        public static final AssetDescriptor<CheckBox> WORK_FOR_PAY = declare("Work for Pay", CheckBox.class);
        public static final AssetDescriptor<CheckBox> PERIOD_OF_WORK_FOR_PAY_BENEFIT_EXCEEDS_PRE_DISABILITY_WAGES = declare("Period of Work for Pay + Benefit Exceeds Pre-Disability Wages", CheckBox.class);
        public static final AssetDescriptor<CheckBox> DISBAILITY_BENEFIT = declare("Disability Benefit", CheckBox.class);
        public static final AssetDescriptor<CheckBox> WAR = declare("War", CheckBox.class);
        public static final AssetDescriptor<CheckBox> DISABILITY_OCURRED_MORE_THAN_4WEEKS_SINCE_LAST_DAY_WORKED = declare("Disability Occurred More Than 4 Weeks Since Last Day Worked", CheckBox.class);
        public static final AssetDescriptor<CheckBox> SELF_PROPRIETOR_2YEAR_WAITING_PERIOD = declare("Self-Proprietor - 2 Year Waiting Period", CheckBox.class);
        public static final AssetDescriptor<CheckBox> MEDICAL_CERTIFICATION_NOT_FILED = declare("Medical Certification Not Filed (family member)", CheckBox.class);

    }

    public static final class MaleOneRateHistoryMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> RATE_1 = declare("Rate 1", TextBox.class);
        public static final AssetDescriptor<ComboBox> NUMBER_OF_MONTHS = declare("Number of Months", ComboBox.class);
    }

    public static final class MaleTwoRateHistoryMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> RATE_2 = declare("Rate 2", TextBox.class);
        public static final AssetDescriptor<ComboBox> NUMBER_OF_MONTHS = declare("Number of Months", ComboBox.class);
    }

    public static final class FemaleOneRateHistoryMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> RATE_1 = declare("Rate 1", TextBox.class);
        public static final AssetDescriptor<ComboBox> NUMBER_OF_MONTHS = declare("Number of Months", ComboBox.class);
    }

    public static final class FemaleTwoRateHistoryMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> RATE_2 = declare("Rate 2", TextBox.class);
        public static final AssetDescriptor<ComboBox> NUMBER_OF_MONTHS = declare("Number of Months", ComboBox.class);
    }

    public static final class BenefitScheduleMetadata extends MetaData {
        public static final AssetDescriptor<TextBox> MAXIMUM_WEEKLY_BENEFIT_AMOUNT = declare("Maximum Weekly Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> MAXIMUM_BENEFIT_PERIOD = declare("Maximum Benefit period", TextBox.class);
        public static final AssetDescriptor<ComboBox> EARNING_TEST_DEFINATION = declare("Earning Test Definition", ComboBox.class);
        public static final AssetDescriptor<ComboBox> RETROACTIVE_BENEFIT = declare("Retroactive Benefit", ComboBox.class);
        public static final AssetDescriptor<TextBox> ACCUMULATION_PERIOD = declare("Accumulation Period", TextBox.class);
        public static final AssetDescriptor<ComboBox> BENEFIT_TYPE = declare("Benefit Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> BENEFIT_PERCENTAGE = declare("Benefit Percentage", ComboBox.class);
        public static final AssetDescriptor<ComboBox> ELIMINATION_PERIOD_INJURY = declare("Elimination Period - Injury (Calendar Days)", ComboBox.class);
        public static final AssetDescriptor<ComboBox> ELIMINATION_PERIOD_SICKNESS = declare("Elimination Period - Sickness (Calendar Days)", ComboBox.class);
        public static final AssetDescriptor<ComboBox> PARTIAL_DISABILITY = declare("Partial Disability", ComboBox.class);
        public static final AssetDescriptor<TextBox> MAXIMUM_BENEFIT_PERIOD_PERCENTAGE_PER_STATE_AVERAGE = declare("Maximum Benefit Period Percentage Per State Average", TextBox.class);
    }

    public static final class OffsetsMetadata extends MetaData {
        public static final AssetDescriptor<ComboBox> RETIREMENT = declare("Retirement", ComboBox.class);
        public static final AssetDescriptor<ComboBox> PENSION = declare("Pension", ComboBox.class);
        public static final AssetDescriptor<ComboBox> PERMANENT_DISABILITY = declare("Permanent Disability", ComboBox.class);
    }

    public static final class OptionsMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> FICA_MATCH = declare("FICA Match", ComboBox.class);
        public static final AssetDescriptor<ComboBox> W2 = declare("W2", ComboBox.class);
        public static final AssetDescriptor<ComboBox> FIRST_DAY_HOSPITALIZATION = declare("First Day Hospitalization", ComboBox.class);
        public static final AssetDescriptor<ComboBox> REHABILITATION_INCENTIVE_BENEFIT = declare("Rehabilitation Incentive Benefit", ComboBox.class);
    }
}