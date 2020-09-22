package com.exigen.ren.common.components;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import org.openqa.selenium.By;

import java.util.ArrayList;

public class DialogRateAndPremiumOverride extends Dialog {

    public static ComboBox coveragePlan = new ComboBox(By.id("policyOverrideInfoForm:coveragePlanSelection"));
    public static ComboBox classificationGroup = new ComboBox(By.id("policyOverrideInfoForm:classificationGroup"));
    public static ComboBox overrideReason = new ComboBox(By.id("policyOverrideInfoForm:overrideReasonCd"));

    public static Dialog dialogPremiumOverride = new Dialog("//div[@id='policyOverrideInfoForm:masterPolicyPremiumOverridePopup']");
    public static TableExtended<DialogRateAndPremiumOverride.RateAndPremiumOverrideTable> rateAndPremiumOverrideTable
            = new TableExtended<>("Rate And Premium Override Table", (By.xpath("//div[@id='policyOverrideInfoForm:policyOverrideInfoTable']//div[2]//table")));
    private static final ByT OVERRIDDEN_TERM_RATE = ByT.xpath(".//*[@id='policyOverrideInfoForm:policyOverrideInfoTable:%1$s:overrideRateAmtInput' or @id='policyOverrideInfoForm:policyOverrideInfoTable:%1$s:overrideRateMoneyAmtInput']");
    private static final ByT VALIDATION_MESSAGES_PANEL = ByT.xpath("(//span[@id='policyOverrideInfoForm:validationMessagesPanel']/span[@class='error_message'])[%s]");

    public DialogRateAndPremiumOverride(String dialogLocator) {
        super(dialogLocator);
    }

    public static String getOverriddenTermRateValueByIndex(int index) {
        return new TextBox(OVERRIDDEN_TERM_RATE.format(index)).getValue();
    }

    public static StaticElement getValidationMessageValueByIndex(int index) {
        return new StaticElement(VALIDATION_MESSAGES_PANEL.format(index));
    }

    public static void setOverriddenTermRateValueByIndex(int index, String value) {
        new TextBox(OVERRIDDEN_TERM_RATE.format(index)).setValue(value);
    }

    public enum RateAndPremiumOverrideTable implements Named {
        COVERAGE_PLAN("Coverage - Plan"),
        CLASSIFICATION_GROUP("Classification Group"),
        SUB_GROUP_TIER("Sub Group/Tier"),
        ORIGINAL_TERM_RATE("Original Term Rate"),
        OVERRIDDEN_TERM_RATE("Overridden Term Rate"),
        MIN_VALUE("Min Value"),
        MAX_VALUE("Max Value");

        private String name;

        RateAndPremiumOverrideTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static ArrayList getOverriddenTermRateValues() {
        ArrayList<String> overriddenTermRateList = new ArrayList<String>();
        int overriddenTermRateCount = DialogRateAndPremiumOverride.rateAndPremiumOverrideTable.getRowsCount();
        for (int i = 0; i < overriddenTermRateCount; i++) {
            overriddenTermRateList.add(DialogRateAndPremiumOverride.getOverriddenTermRateValueByIndex(i));
        }
        return overriddenTermRateList;
    }

    public static TextBox overriddenTermRateField(int index) {
        return new TextBox(OVERRIDDEN_TERM_RATE.format(index));
    }
}
