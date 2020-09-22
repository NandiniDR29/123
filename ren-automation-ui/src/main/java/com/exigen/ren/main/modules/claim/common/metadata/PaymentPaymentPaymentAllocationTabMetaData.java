/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class PaymentPaymentPaymentAllocationTabMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> BENEFIT = declare("Benefit", ComboBox.class);
    public static final AssetDescriptor<ComboBox> DAMAGE = declare("Damage", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COVERAGE = declare("Coverage", ComboBox.class);
    public static final AssetDescriptor<TextBox> ASSOCIATED_INSURABLE_RISK = declare("Associated Insurable Risk", TextBox.class);
    public static final AssetDescriptor<StaticElement> ASSOCIATED_INSURABLE_RISK_LABEL = declare("Associated Insurable Risk", StaticElement.class, By.id("policyDataGatherForm:associatedInsurableRisk_PrecClaimsPaymentDistribution"));
    public static final AssetDescriptor<StaticElement> SINGLE_BENEFIT_CALCULATION_NUMBER_LABEL = declare("Single Benefit Calculation Number", StaticElement.class, By.id("policyDataGatherForm:featureNumber_PrecClaimsPaymentDistribution"));
    public static final AssetDescriptor<ComboBox> STATUS = declare("Status", ComboBox.class);
    public static final AssetDescriptor<TextBox> FEATURE_NUMBER = declare("Feature Number", TextBox.class);
    public static final AssetDescriptor<ComboBox> TYPE_OF_PAYMENT = declare("Type of Payment", ComboBox.class);
    public static final AssetDescriptor<ComboBox> RESERVE_TYPE = declare("Reserve Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> IN_LIEU_BENEFIT = declare("In Lieu Benefit", ComboBox.class, By.id("policyDataGatherForm:sedit_PrecClaimsPaymentDistribution_paymentDistribution_inLieuBenefitType"));
    public static final AssetDescriptor<RadioGroup> IN_APPROVED_REHABILITATION_PROGRAM = declare("In Approved Rehabilitation Program", RadioGroup.class);
    public static final AssetDescriptor<TextBox> EXISTING_RESERVE_AMOUNT = declare("Existing Reserve Amount", TextBox.class);
    public static final AssetDescriptor<RadioGroup> PARTIAL_DISABILITY_CALCULATION = declare("Partial Disability Calculation", RadioGroup.class);
    public static final AssetDescriptor<TextBox> CURRENT_EARNINGS = declare("Current Earnings", TextBox.class);
    public static final AssetDescriptor<RadioGroup> SUPPLEMENTAL_PAYMENT = declare("Supplemental Payment", RadioGroup.class);
    public static final AssetDescriptor<TextBox> ALLOCATION_AMOUNT = declare("Allocation Amount", TextBox.class, By.xpath("//*[@id='policyDataGatherForm:sedit_PrecClaimsPaymentDistribution_distribution_amount']"));
    public static final AssetDescriptor<RadioGroup> EX_GRATIA_PAYMENT = declare("Ex Gratia Payment", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> REASON_FOR_EX_GRATIA_REQUEST = declare("Reason for Ex Gratia Request", ComboBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION_OF_EX_GRATIA_REQUEST = declare("Description of Ex Gratia Request", TextBox.class);
    public static final AssetDescriptor<RadioGroup> ADJUST_RESERVE = declare("Adjust reserve", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> ALLOCATION_TYPE = declare("Allocation Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> TYPE_DESCRIPTION = declare("Type Description", TextBox.class);
    public static final AssetDescriptor<TextBox> PAYMENT_FROM_DATE = declare("Payment From Date", TextBox.class);
    public static final AssetDescriptor<TextBox> PAYMENT_THROUGH_DATE = declare("Payment Through Date", TextBox.class);
    public static final AssetDescriptor<TextBox> PAYABLE_DAYS = declare("Payable Days", TextBox.class);
    public static final AssetDescriptor<TextBox> WORKED_PAYABLE_DAYS = declare("Worked Payable Days", TextBox.class);
    public static final AssetDescriptor<TextBox> ALLOCATION_NET_AMOUNT = declare("Allocation Net Amount", TextBox.class, By.xpath("//*[@id='policyDataGatherForm:sedit_PrecClaimsPaymentDistribution_distribution_netAmount']"));
    public static final AssetDescriptor<RadioGroup> FINAL_PAYMENT = declare("Final Payment", RadioGroup.class);
}