package com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsTermLifeEmployerModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClaimsTermLifeEmployerClaimsModel extends RestError {
    private String lossDt;
    private List<DamageModel> claimsDamages;
    private String lineOfBusinessCd;
    private String claimStatusCd;
    private String claimNumber;
    private String reportedDt;
    private String customerNumber;
    private String productCd;
    private String totalIncurred;

    public String getLossDt() {
        return lossDt;
    }

    public void setLossDt(String lossDt) {
        this.lossDt = lossDt;
    }

    public List<DamageModel> getClaimsDamages() {
        return claimsDamages;
    }

    public void setClaimsDamages(List<DamageModel> claimsDamages) {
        this.claimsDamages = claimsDamages;
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

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public String getReportedDt() {
        return reportedDt;
    }

    public void setReportedDt(String reportedDt) {
        this.reportedDt = reportedDt;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getProductCd() {
        return productCd;
    }

    public void setProductCd(String productCd) {
        this.productCd = productCd;
    }

    public String getTotalIncurred() {
        return totalIncurred;
    }

    public void setTotalIncurred(String totalIncurred) {
        this.totalIncurred = totalIncurred;
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
        ClaimsTermLifeEmployerClaimsModel that = (ClaimsTermLifeEmployerClaimsModel) o;
        return Objects.equals(lossDt, that.lossDt) &&
                Objects.equals(claimsDamages, that.claimsDamages) &&
                Objects.equals(lineOfBusinessCd, that.lineOfBusinessCd) &&
                Objects.equals(claimStatusCd, that.claimStatusCd) &&
                Objects.equals(claimNumber, that.claimNumber) &&
                Objects.equals(reportedDt, that.reportedDt) &&
                Objects.equals(customerNumber, that.customerNumber) &&
                Objects.equals(productCd, that.productCd) &&
                Objects.equals(totalIncurred, that.totalIncurred);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lossDt, claimsDamages, lineOfBusinessCd, claimStatusCd, claimNumber, reportedDt, customerNumber, productCd, totalIncurred);
    }
}
