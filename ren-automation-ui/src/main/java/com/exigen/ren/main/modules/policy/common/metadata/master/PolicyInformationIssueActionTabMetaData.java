/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.exigen.ren.main.modules.policy.common.metadata.master;

import com.exigen.istf.webdriver.controls.CheckBox;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;

public class PolicyInformationIssueActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> QUOTE_CREATION_DATE = declare("Quote Creation Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> UNDEWRITING_COMPANY = declare("Underwriting Company", ComboBox.class);
    public static final AssetDescriptor<TextBox> QUOTE_EXPIRATION_DATE = declare("Quote Expiration Date", TextBox.class);
    public static final AssetDescriptor<TextBox> APPLICATION_SIGNED_DATE = declare("Application Signed Date", TextBox.class);
    public static final AssetDescriptor<RadioGroup> ASO_PLAN = declare("ASO Plan", RadioGroup.class);
    public static final AssetDescriptor<TextBox> ISSUE_DATE = declare("Issue Date", TextBox.class);
    public static final AssetDescriptor<TextBox> COVERAGE_EFFECTIVE_DATE = declare("Coverage Effective Date", TextBox.class);
    public static final AssetDescriptor<TextBox> NEXT_POLICY_YEAR_START_DATE = declare("Next Policy Year Start Date", TextBox.class);
    public static final AssetDescriptor<TextBox> RENEWAL_DATE = declare("Renewal Date", TextBox.class);
    public static final AssetDescriptor<TextBox> ANNIVERSARY_DATE = declare("Anniversary Date", TextBox.class);
    public static final AssetDescriptor<TextBox> CANCELLATION_DATE = declare("Cancellation Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> FUNDING_METHOD = declare("Funding Method", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ISSUE_STATE = declare("Issue State", ComboBox.class);
    public static final AssetDescriptor<TextBox> OPERATING_STATES = declare("Operating States", TextBox.class);
    public static final AssetDescriptor<ComboBox> RENEWAL_FREQUENCY = declare("Renewal Frequency", ComboBox.class);
    public static final AssetDescriptor<TextBox> POLICY_TERM = declare("Policy Term", TextBox.class);
    public static final AssetDescriptor<TextBox> AMOUNT_RECEIVED_WITH_APPLICATION = declare("Amount Received with Application", TextBox.class);
    public static final AssetDescriptor<TextBox> PRIOR_CARRIER_NAME = declare("Prior Carrier Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> PRIOR_CARRIER_NAME_COMBO = declare("Prior Carrier Name", ComboBox.class);
    public static final AssetDescriptor<TextBox> PRIOR_CARRIER_POLICY_NUMBER = declare("Prior Carrier Policy Number", TextBox.class);
    public static final AssetDescriptor<CheckBox> PRIOR_CLAIMS_ALLOWED = declare("Prior Claims Allowed?", CheckBox.class);
    public static final AssetDescriptor<TextBox> PRIOR_CLAIMS_RETROACTIVE_EFFECTIVE_DATE = declare("Prior Claims Retroactive Effective Date", TextBox.class);
    public static final AssetDescriptor<TextBox> MINIMUM_PARTICIPATION_PERCENTAGE = declare("Minimum Participation Percentage", TextBox.class);
    public static final AssetDescriptor<TextBox> MINIMUM_NUMBER_OF_PARTICIPANTS = declare("Minimum Number of Participants", TextBox.class);
    public static final AssetDescriptor<TextBox> MAXIMUM_NUMBER_OF_PARTICIPANTS = declare("Maximum Number of Participants", TextBox.class);
    public static final AssetDescriptor<TextBox> TOTAL_NUMBER_OF_ELIGIBLE_LIVES = declare("Total Number of Eligible Lives", TextBox.class);
    public static final AssetDescriptor<TextBox> AGENCY_PRODUCER = declare("Agency / Producer", TextBox.class);
    public static final AssetDescriptor<ComboBox> AGENT_SUB_PRODUCER = declare("Agent Sub Producer", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COUNTY_CODE = declare("County Code", ComboBox.class, Waiters.DEFAULT);
    public static final AssetDescriptor<RadioGroup> GROUP_IS_AN_ASSOCIATION = declare("Group is an Association?", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> GROUP_IS_MEMBER_COMPANY = declare("Group is Member Company", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> MEMBER_COMPANY_NAME = declare("Member Company Name", ComboBox.class);
    public static final AssetDescriptor<ComboBox> NAME_TO_DISPLAY_ON_MP_DOCUMENTS = declare("Name to Display on Master Policy Documents", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> SMALL_GROUP = declare("Small group?", RadioGroup.class);
}
