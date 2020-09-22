/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class PlanDefinitionTabMetaData extends MetaData {
    public static final AssetDescriptor<AdvancedSelector> PLAN = declare("Plan", AdvancedSelector.class);
    public static final AssetDescriptor<TextBox> PLAN_NAME = declare("Plan Name", TextBox.class);

    public static final AssetDescriptor<ComboBox> COVERAGE_NAME = declare("Coverage Name", ComboBox.class);

    public static final AssetDescriptor<AssetList> SPONSOR_PARTICIPANT_FUNDING_STRUCTURE = declare("Sponsor/Participant Funding Structure", AssetList.class, SponsorParticipantFundingStructureMetaData.class, false, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> RATING = declare("Rating", AssetList.class, RatingMetaData.class, false, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> BENEFIT_SCHEDULE = declare("Benefit Schedule", AssetList.class, BenefitScheduleMetaData.class, false, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<AssetList> OPTIONS = declare("Options", AssetList.class, OptionsMetaData.class, false, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

    public static final class SponsorParticipantFundingStructureMetaData extends MetaData {
        public static final AssetDescriptor<RadioGroup> SELF_ADMINISTERED = declare("Self-administered?", RadioGroup.class);
        public static final AssetDescriptor<ComboBox> CONTRIBUTION_TYPE = declare("Contribution Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> PARTICIPANT_CONTRIBUTION = declare("Participant Contribution %", TextBox.class);
        public static final AssetDescriptor<TextBox> MINIMUM_NUMBER_OF_PARTICIPANTS = declare("Minimum Number of Participants", TextBox.class);
        public static final AssetDescriptor<ComboBox> TAXABILITY = declare("Taxability", ComboBox.class);
        public static final AssetDescriptor<RadioGroup> PREMIUMS_PAID_POST_TAX = declare("Premiums Paid-Post Tax", RadioGroup.class);
        public static final AssetDescriptor<ComboBox> SPONSOR_PAYMENT_MODE = declare("Sponsor Payment Mode", ComboBox.class);
        public static final AssetDescriptor<AdvancedSelector> MEMBER_PAYMENT_MODE = declare("Member Payment Mode", AdvancedSelector.class);
    }

    public static final class RatingMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> RATE_BASIS = declare("Rate Basis", ComboBox.class);
        public static final AssetDescriptor<TextBox> MAX_TAXABLE_WAGE = declare("Maximum Taxable Wage", TextBox.class);
    }

    public static final class BenefitScheduleMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> MAXIMUM_WEEKLY_BENEFIT_AMOUNT = declare("Maximum Weekly Benefit Amount", TextBox.class);
        public static final AssetDescriptor<ComboBox> ELIMINATION_PERIOD_CALENDAR_DAYS = declare("Elimination Period (Calendar Days)", ComboBox.class);
        public static final AssetDescriptor<TextBox> MAXIMUM_BENEFIT_PERIOD_WEEKS = declare("Maximum Benefit Period (Weeks)", TextBox.class);
        public static final AssetDescriptor<TextBox> MAXIMUM_BENEFIT_PERIOD_DAYS = declare("Maximum Benefit Period (Days)", TextBox.class);
        public static final AssetDescriptor<TextBox> MAXIMUM_WEEKLY_BENEFIT_PERCENTAGE_PER_STATE_AVERAGE = declare("Maximum Weekly Benefit Percentage Per State Average", TextBox.class);
        public static final AssetDescriptor<ComboBox> BENEFIT_TYPE = declare("Benefit Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> BENEFIT_PERCENTAGE = declare("Benefit Percentage", ComboBox.class);
    }

    public static final class OptionsMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> FICA_MATCH = declare("FICA Match", ComboBox.class);
        public static final AssetDescriptor<ComboBox> W2 = declare("W2", ComboBox.class);
    }
}