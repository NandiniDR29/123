package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentSeriesCalculatorActionTab;
import org.openqa.selenium.By;

public class PaymentSeriesCalculatorActionTabMetaData extends MetaData {

    public static final AssetDescriptor<PaymentSeriesCalculatorActionTab.PaymentAdditionMultiAssetList> PAYMENT_ADDITION = declare("Payment Addition", PaymentSeriesCalculatorActionTab.PaymentAdditionMultiAssetList.class, PaymentAdditionMetaData.class, By.id("policyDataGatherForm:formGrid_ClaimsPaymentCalculatorAdditionTemplate"));
    public static final AssetDescriptor<AssetList> PAYMENT_REDUCTION = declare("Payment Reduction", AssetList.class, PaymentReductionMetaData.class, By.id("policyDataGatherForm:formGrid_ClaimsPaymentCalculatorReductionTemplate"));
    public static final AssetDescriptor<TextBox> BENEFIT_AMOUNT_PER_CHILD = declare("Benefit Amount per Child", TextBox.class);
    public static final AssetDescriptor<TextBox> TOTAL_CHILD_EDUCATION_BENEFIT_AMOUNT = declare("Total Child Education Benefit Amount", TextBox.class,
            By.xpath("//input[@id='policyDataGatherForm:sedit_ClaimsPaymentCalculatorAdditionTemplate_totChEduBenefitAmount']"));
    public static final AssetDescriptor<TextBox> COLA_PERCENT = declare("COLA %", TextBox.class);
    public static final AssetDescriptor<TextBox> COLA_AMOUNT = declare("COLA Amount", TextBox.class);
    public static final AssetDescriptor<ComboBox> NUMBER_OF_ADJUSTMENTS = declare("Number of Adjustments", ComboBox.class);
    public static final AssetDescriptor<Button> BUTTON_ADD_PAYMENT_ADDITION = declare("Add Payment Addition", Button.class, By.id("policyDataGatherForm:addClaimsPaymentCalculatorAdditionTemplate"));
    public static final AssetDescriptor<Button> BUTTON_REMOVE = declare("Remove", Button.class, By.id("policyDataGatherForm:eliminateClaimsPaymentCalculatorAddition"));

    public static final class PaymentAdditionMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> ADDITION_TYPE = declare("Addition Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> WEEKLY_BENEFIT_AMOUNT = declare("Weekly Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> REMAINING_BENEFIT_MAXIMUM = declare("Remaining Benefit Maximum for current Calendar Year", TextBox.class);
        public static final AssetDescriptor<TextBox> NUMBER_OF_FAMILY_MEMBERS = declare("Number of Family Members", TextBox.class);
        public static final AssetDescriptor<TextBox> BEGINNING_DATE = declare("Beginning Date", TextBox.class);
        public static final AssetDescriptor<TextBox> THROUGH_DATE = declare("Through Date", TextBox.class);
        public static final AssetDescriptor<TextBox> REQUIRED_MONTHLY_401K_CONTRIBUTION_AMOUNT = declare("Required Monthly 401K Contribution Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> MONTHLY_BENEFIT_AMOUNT = declare("Monthly Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> TOTAL_BENEFIT_AMOUNT = declare("Total Benefit Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDITION_COMMENTS = declare("Addition Comments", TextBox.class);
        public static final AssetDescriptor<TextBox> REQUIRED_MONTHLY_COBRA_PREMIUM_AMOUNT = declare("Required Monthly COBRA Premium Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> REQUIRED_MONTHLY_STUDENT_LOAN_AMOUNT = declare("Required Monthly Student Loan Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> NUMBER_OF_CHILDREN = declare("Number of Children", TextBox.class);
    }

    public static final class PaymentReductionMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> REDUCTION_TYPE = declare("Reduction Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> TYPE_OF_DEDUCTION = declare("Type Of Deduction", ComboBox.class);
        public static final AssetDescriptor<TextBox> BEGINNING_DATE = declare("Beginning Date", TextBox.class);
        public static final AssetDescriptor<TextBox> THROUGH_DATE = declare("Through Date", TextBox.class);
        public static final AssetDescriptor<TextBox> DEDUCTION_AMOUNT = declare("Deduction Amount", TextBox.class);
        public static final AssetDescriptor<TextBox> MONTHLY_BENEFIT_AMOUNT = declare("Monthly Benefit Amount", TextBox.class);
        public static final AssetDescriptor<RadioGroup> APPLY_PRE_TAX = declare("Apply Pre-tax", RadioGroup.class);
        public static final AssetDescriptor<TextBox> PRIORITY = declare("Priority", TextBox.class);
        public static final AssetDescriptor<ComboBox> PARTY = declare("Party", ComboBox.class);
        public static final AssetDescriptor<RadioGroup> EXCLUDED = declare("Excluded", RadioGroup.class);
    }

}
