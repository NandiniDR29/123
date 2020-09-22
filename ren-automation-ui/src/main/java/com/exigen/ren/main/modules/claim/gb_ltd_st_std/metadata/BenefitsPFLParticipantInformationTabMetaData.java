package com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector2;
import com.exigen.ipb.eisa.controls.dialog.type.AdvancedSelectorWithSleep;
import com.exigen.istf.webdriver.controls.CheckBox;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class BenefitsPFLParticipantInformationTabMetaData extends MetaData {
    public static final AssetDescriptor<Link> ADD_NEW_BENEFIT_PFL = declare("Add Benefit: Paid Family Leave", Link.class, By.id("productConsolidatedViewForm:addDamagePaidFamilyLeave"));
    public static final AssetDescriptor<ComboBox> ASSOCIATE_POLICY_PARTY = declare("Associate Policy Party", ComboBox.class);
    public static final AssetDescriptor<TextBox> CURRENT_AGE = declare("Current Age", TextBox.class);
    public static final AssetDescriptor<TextBox> OTHER_LAST_NAME = declare("Other last names, if any, under which employee has worked", TextBox.class);
    public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class);
    public static final AssetDescriptor<TextBox> SOCIAL_SECURITY_NUMBER_SSN = declare("Social Security Number (SSN)", TextBox.class);
    public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
    public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
    public static final AssetDescriptor<ComboBox> PREFERRED_PAYMENT_METHOD = declare("Preferred Payment method", ComboBox.class);
    public static final AssetDescriptor<ComboBox> BANK_NAME = declare("Bank Name", ComboBox.class);
    public static final AssetDescriptor<TextBox> BANK_ACCOUNT_NUMBER = declare("Bank Account Number", TextBox.class);
    public static final AssetDescriptor<TextBox> BANK_TRANSIT_ROUTING_NUMBER = declare("Bank Transit / Routing Number", TextBox.class);
    public static final AssetDescriptor<ComboBox> BANK_ACCOUNT_TYPE = declare("Bank Account Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> BILLING_LOCATION = declare("Billing Location", ComboBox.class);

    public static final AssetDescriptor<AdvancedSelectorWithSleep> PREFERRED_LANGUAGE =
            declare("Preferred language", AdvancedSelectorWithSleep.class, Waiters.DEFAULT, false);
    public static final AssetDescriptor<AdvancedSelectorWithSleep> ORIGIN_ETHNICITY =
            declare("Origin/ Ethnicity", AdvancedSelectorWithSleep.class, Waiters.DEFAULT, false);
    public static final AssetDescriptor<AdvancedSelectorWithSleep> RACE =
            declare("Race", AdvancedSelectorWithSleep.class, Waiters.DEFAULT, false);
    public static final AssetDescriptor<TextBox> OTHER_LANGUAGE = declare("Other Language", TextBox.class);

    public static final AssetDescriptor<AssetList> PARTICIPANT_WORK_DAYS = declare("Participant Work Days", AssetList.class, BenefitsPFLParticipantInformationTabMetaData.ParticipantWorkDaysMetaData.class,
            By.xpath("//div[@id='policyDataGatherForm:componentView_PaidFamilyLeavePartyWorkDays_body']"));
    public static final AssetDescriptor<AssetList> PARTICIPANT_INCOME = declare("Participant Income", AssetList.class, ParticipantIncomeMetaData.class,
            By.xpath("//div[@id='policyDataGatherForm:componentView_PaidFamilyLeavePartyIncome']"));

    public static final class ParticipantWorkDaysMetaData extends MetaData {
        public static final AssetDescriptor<CheckBox> SUNDAY = declare("Sunday", CheckBox.class);
        public static final AssetDescriptor<CheckBox> MONDAY = declare("Monday", CheckBox.class);
        public static final AssetDescriptor<CheckBox> TUESDAY = declare("Tuesday", CheckBox.class);
        public static final AssetDescriptor<CheckBox> WEDNESDAY = declare("Wednesday", CheckBox.class);
        public static final AssetDescriptor<CheckBox> THURSDAY = declare("Thursday", CheckBox.class);
        public static final AssetDescriptor<CheckBox> FRIDAY = declare("Friday", CheckBox.class);
        public static final AssetDescriptor<CheckBox> SATURDAY = declare("Saturday", CheckBox.class);
        public static final AssetDescriptor<TextBox> AVERAGE_HOURS_WORKED_PER_WEEK = declare("Average Hours Worked Per Week", TextBox.class);
    }

    public static final class ParticipantIncomeMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> ANNUAL_BASE_SALARY = declare("Annual Base Salary", TextBox.class);
        public static final AssetDescriptor<TextBox> ANNUAL_COMMISSION_AMOUNT = declare("Annual Commission Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> ANNUAL_BONUS_AMOUNT = declare("Annual Bonus Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> TOTAL_ANNUAL_INCOME_AMOUNT = declare("Total Annual Income Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> COVERED_EARNINGS = declare("Covered Earnings", TextBox.class);
    }

}
