/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.billing.model.billing_accounts_invoices;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingAccountsInvoices extends RestError {

    private String businessName;
    private String billingPeriodStart;
    private String billingPeriodEnd;
    private Integer coverageCount;
    private BillingAccountsInvoicesDocuments document;
    private List<BillingAccountsInvoicesCoverages> coverages;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBillingPeriodStart() {
        return billingPeriodStart;
    }

    public void setBillingPeriodStart(String billingPeriodStart) {
        this.billingPeriodStart = billingPeriodStart;
    }

    public String getBillingPeriodEnd() {
        return billingPeriodEnd;
    }

    public void setBillingPeriodEnd(String billingPeriodEnd) {
        this.billingPeriodEnd = billingPeriodEnd;
    }

    public Integer getCoverageCount() {
        return coverageCount;
    }

    public void setCoverageCount(Integer coverageCount) {
        this.coverageCount = coverageCount;
    }

    public BillingAccountsInvoicesDocuments getDocument() {
        return document;
    }

    public void setDocument(BillingAccountsInvoicesDocuments document) {
        this.document = document;
    }

    public List<BillingAccountsInvoicesCoverages> getCoverages() {
        return coverages;
    }

    public void setCoverages(List<BillingAccountsInvoicesCoverages> coverages) {
        this.coverages = coverages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        BillingAccountsInvoices that = (BillingAccountsInvoices) o;
        return Objects.equals(businessName, that.businessName) &&
                Objects.equals(billingPeriodStart, that.billingPeriodStart) &&
                Objects.equals(billingPeriodEnd, that.billingPeriodEnd) &&
                Objects.equals(coverageCount, that.coverageCount) &&
                Objects.equals(document, that.document) &&
                Objects.equals(coverages, that.coverages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), businessName, billingPeriodStart, billingPeriodEnd, coverageCount, document, coverages);
    }
}

class BillingAccountsInvoicesCoverages {

    private Integer referenceNumber;
    private String code;
    private String policyPlan;
    private String productCd;
    private String billingGroup;
    private String payor;
    private String coverageSegmentClassifier;
    private BillingAccountsInvoicesCoveragesPremium premium;

    public Integer getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(Integer referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPolicyPlan() {
        return policyPlan;
    }

    public void setPolicyPlan(String policyPlan) {
        this.policyPlan = policyPlan;
    }

    public String getProductCd() {
        return productCd;
    }

    public void setProductCd(String productCd) {
        this.productCd = productCd;
    }

    public String getBillingGroup() {
        return billingGroup;
    }

    public void setBillingGroup(String billingGroup) {
        this.billingGroup = billingGroup;
    }

    public String getPayor() {
        return payor;
    }

    public void setPayor(String payor) {
        this.payor = payor;
    }

    public String getCoverageSegmentClassifier() {
        return coverageSegmentClassifier;
    }

    public void setCoverageSegmentClassifier(String coverageSegmentClassifier) {
        this.coverageSegmentClassifier = coverageSegmentClassifier;
    }

    public BillingAccountsInvoicesCoveragesPremium getPremium() {
        return premium;
    }

    public void setPremium(BillingAccountsInvoicesCoveragesPremium premium) {
        this.premium = premium;
    }

    public Integer getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
    }

    private Integer participantCount;
}

class BillingAccountsInvoicesCoveragesPremium {
    private Integer value;
    private String currency;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}