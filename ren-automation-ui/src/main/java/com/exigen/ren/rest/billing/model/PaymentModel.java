/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.billing.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
public class PaymentModel extends Model {

    private String billingAccountNumber;
    private DueDayConfigModel dueDayConfigModel;
    private String effectiveDate;
    private String expirationDate;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String suffix;
    private String planCd;
    private String productCd;
    private String brandCd;
    private String countryCd;
    private String riskStateCd;
    private String scheduleEffectiveDate;
    private String transactionDate;
    private Integer downPaymentAmount;
    private String currencyCd;
    private String overpaidAmountOption;
    private List<PaymentPlanModel> paymentPlans;
    private List<BalanceChangeAmountModel> balanceChangeAmounts;

    public String getScheduleEffectiveDate() { return scheduleEffectiveDate; }

    public void setScheduleEffectiveDate(String scheduleEffectiveDate) { this.scheduleEffectiveDate = scheduleEffectiveDate; }

    public String getTransactionDate() { return transactionDate; }

    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }

    public Integer getDownPaymentAmount() { return downPaymentAmount; }

    public void setDownPaymentAmount(Integer downPaymentAmount) { this.downPaymentAmount = downPaymentAmount; }

    public String getCurrencyCd() { return currencyCd; }

    public void setCurrencyCd(String currencyCd) { this.currencyCd = currencyCd; }

    public String getOverpaidAmountOption() { return overpaidAmountOption; }

    public void setOverpaidAmountOption(String overpaidAmountOption) { this.overpaidAmountOption = overpaidAmountOption; }

    public List<PaymentPlanModel> getPaymentPlans() { return paymentPlans; }

    public void setPaymentPlans(List<PaymentPlanModel> paymentPlans) { this.paymentPlans = paymentPlans; }

    public List<BalanceChangeAmountModel> getBalanceChangeAmounts() { return balanceChangeAmounts; }

    public void setBalanceChangeAmounts(List<BalanceChangeAmountModel> balanceChangeAmounts) { this.balanceChangeAmounts = balanceChangeAmounts; }

    public String getBillingAccountNumber() { return billingAccountNumber; }

    public void setBillingAccountNumber(String billingAccountNumber) { this.billingAccountNumber = billingAccountNumber; }

    public String getEffectiveDate() { return effectiveDate; }

    public void setEffectiveDate(String effectiveDate) { this.effectiveDate = effectiveDate; }

    public String getExpirationDate() { return expirationDate; }

    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }

    public String getSuffix() { return suffix; }

    public void setSuffix(String suffix) { this.suffix = suffix; }

    public String getPlanCd() { return planCd; }

    public void setPlanCd(String planCd) { this.planCd = planCd; }

    public String getProductCd() { return productCd; }

    public void setProductCd(String productCd) { this.productCd = productCd; }

    public String getBrandCd() { return brandCd; }

    public void setBrandCd(String brandCd) { this.brandCd = brandCd; }

    public String getCountryCd() { return countryCd; }

    public void setCountryCd(String countryCd) { this.countryCd = countryCd; }

    public String getRiskStateCd() { return riskStateCd; }

    public void setRiskStateCd(String riskStateCd) { this.riskStateCd = riskStateCd; }

    public DueDayConfigModel getDueDayConfigModel() { return dueDayConfigModel; }

    public void setDueDayConfigModel(DueDayConfigModel dueDayConfigModel) { this.dueDayConfigModel = dueDayConfigModel; }
}

class DueDayConfigModel {

    private String type;
    private List<Integer> dueDays;

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public List<Integer> getDueDays() { return dueDays; }

    public void setDueDays(List<Integer> dueDays) { this.dueDays = dueDays; }
}

class BalanceChangeAmountModel {

    private String amountType;
    private String code;
    private Integer amount;

    public String getAmountType() { return amountType; }

    public void setAmountType(String amountType) { this.amountType = amountType; }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public Integer getAmount() { return amount; }

    public void setAmount(Integer amount) { this.amount = amount; }
}

class PaymentPlanModel {

    private String planCd;
    private String installmentStartDate;
    private String dueDateStartFrom;

    public String getPlanCd() { return planCd; }

    public void setPlanCd(String planCd) { this.planCd = planCd; }

    public String getInstallmentStartDate() { return installmentStartDate; }

    public void setInstallmentStartDate(String installmentStartDate) { this.installmentStartDate = installmentStartDate; }

    public String getDueDateStartFrom() { return dueDateStartFrom; }

    public void setDueDateStartFrom(String dueDateStartFrom) { this.dueDateStartFrom = dueDateStartFrom; }
}
