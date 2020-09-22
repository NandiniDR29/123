package com.exigen.ren.rest.dxp.model.claim;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClaimEmployerModel extends RestError {
    private String claimNumber;
    private String productCd;
    private String policyProductCd;
    private String lineOfBusinessCd;
    private String claimStatusCd;
    private String policyNumber;
    private String customerNumber;
    private String lossDate;
    private String reportedDate;
    private InsuredModel insured;
    private TotalIncurredModel totalIncurredModel;

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public String getProductCd() {
        return productCd;
    }

    public void setProductCd(String productCd) {
        this.productCd = productCd;
    }

    public String getPolicyProductCd() {
        return policyProductCd;
    }

    public void setPolicyProductCd(String policyProductCd) {
        this.policyProductCd = policyProductCd;
    }

    public String getLineOfBusinessCd() {
        return lineOfBusinessCd;
    }

    public void setLineOfBusinessCd(String lineOfBusinessCd) {
        this.lineOfBusinessCd = lineOfBusinessCd;
    }

    public String getClaimStatusCd() {
        return claimStatusCd;
    }

    public void setClaimStatusCd(String claimStatusCd) {
        this.claimStatusCd = claimStatusCd;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getLossDate() {
        return lossDate;
    }

    public void setLossDate(String lossDate) {
        this.lossDate = lossDate;
    }

    public String getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(String reportedDate) {
        this.reportedDate = reportedDate;
    }

    public InsuredModel getInsured() {
        return insured;
    }

    public void setInsured(InsuredModel insured) {
        this.insured = insured;
    }

    public TotalIncurredModel getTotalIncurredModel() {
        return totalIncurredModel;
    }

    public void setTotalIncurredModel(TotalIncurredModel totalIncurredModel) {
        this.totalIncurredModel = totalIncurredModel;
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
        ClaimEmployerModel that = (ClaimEmployerModel) o;
        return Objects.equals(claimNumber, that.claimNumber) &&
                Objects.equals(productCd, that.productCd) &&
                Objects.equals(policyProductCd, that.policyProductCd) &&
                Objects.equals(lineOfBusinessCd, that.lineOfBusinessCd) &&
                Objects.equals(claimStatusCd, that.claimStatusCd) &&
                Objects.equals(policyNumber, that.policyNumber) &&
                Objects.equals(customerNumber, that.customerNumber) &&
                Objects.equals(lossDate, that.lossDate) &&
                Objects.equals(reportedDate, that.reportedDate) &&
                Objects.equals(insured, that.insured) &&
                Objects.equals(totalIncurredModel, that.totalIncurredModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), claimNumber, productCd, policyProductCd, lineOfBusinessCd, claimStatusCd, policyNumber, customerNumber, lossDate, reportedDate, insured, totalIncurredModel);
    }
}