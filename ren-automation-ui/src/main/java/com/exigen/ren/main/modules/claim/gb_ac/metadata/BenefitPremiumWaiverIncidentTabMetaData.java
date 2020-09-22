package com.exigen.ren.main.modules.claim.gb_ac.metadata;

import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class BenefitPremiumWaiverIncidentTabMetaData extends MetaData {

    public static final AssetDescriptor<AssetList> PREMIUM_WAIVER_INCIDENT = declare("Premium Waiver Incident", AssetList.class, PremiumWaiverIncidentMetaData.class);

    public static final class PremiumWaiverIncidentMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> REPORTED_DATE_OF_DIAGNOSIS = declare("Reported Date of Diagnosis", TextBox.class);
        public static final AssetDescriptor<TextBox> WORK_EDUCATIONAL_HISTORY = declare("Work & Educational History", TextBox.class);
        public static final AssetDescriptor<TextBox> RTW_DATE = declare("RTW Date", TextBox.class);
        public static final AssetDescriptor<RadioGroup> WORK_RELATED = declare("Work Related?", RadioGroup.class);
        public static final AssetDescriptor<TextBox> DATE_OF_HIRE = declare("Date of Hire", TextBox.class, By.id("policyDataGatherForm:sedit_PremiumWaiverIncident_dateOfHireInputDate"));
        public static final AssetDescriptor<TextBox> TERMINATION_DATE = declare("Termination Date", TextBox.class);
        public static final AssetDescriptor<TextBox> FIRST_DATE_OF_TREATMENT = declare("First Date of Treatment", TextBox.class);
        public static final AssetDescriptor<RadioGroup> ACCIDENT = declare("Accident?", RadioGroup.class);
        public static final AssetDescriptor<TextBox> HOURS_NORMALLY_WORKED_PER_WEEK = declare("Hours Normally Worked Per Week", TextBox.class, By.id("policyDataGatherForm:sedit_PremiumWaiverIncident_hoursNormallyWorkedPerWeek"));
        public static final AssetDescriptor<TextBox> DATE_LAST_WORKED = declare("Date Last Worked", TextBox.class);
    }
}
