package com.exigen.ren.rest.dxp.model.billing;

import com.exigen.ren.rest.model.RestError;

import java.util.Objects;

public class InvoiceDetailsModel extends RestError {
    private String businessName;
    private String billingPeriodStart;
    private String billingPeriodEnd;
    private String coverageCount;
    private BillingBenefitsDocumentModel document;

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

    public String getCoverageCount() {
        return coverageCount;
    }

    public void setCoverageCount(String coverageCount) {
        this.coverageCount = coverageCount;
    }

    public BillingBenefitsDocumentModel getDocument() {
        return document;
    }

    public void setDocument(BillingBenefitsDocumentModel document) {
        this.document = document;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        InvoiceDetailsModel that = (InvoiceDetailsModel) o;
        return Objects.equals(businessName, that.businessName) &&
                Objects.equals(billingPeriodStart, that.billingPeriodStart) &&
                Objects.equals(billingPeriodEnd, that.billingPeriodEnd) &&
                Objects.equals(coverageCount, that.coverageCount) &&
                Objects.equals(document, that.document);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), businessName, billingPeriodStart, billingPeriodEnd, coverageCount, document);
    }
}
