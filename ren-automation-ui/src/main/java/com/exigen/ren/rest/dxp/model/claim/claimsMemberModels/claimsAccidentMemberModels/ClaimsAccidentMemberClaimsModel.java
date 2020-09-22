package com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsAccidentMemberModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClaimsAccidentMemberClaimsModel extends RestError {
    private String claimNumber;
    private String customerNumber;
    private String productCd;
    private List<ClaimsDamageModel> claimsDamages;

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
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

    public List<ClaimsDamageModel> getClaimsDamages() {
        return claimsDamages;
    }

    public void setClaimsDamages(List<ClaimsDamageModel> claimsDamages) {
        this.claimsDamages = claimsDamages;
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
        ClaimsAccidentMemberClaimsModel that = (ClaimsAccidentMemberClaimsModel) o;
        return Objects.equals(claimNumber, that.claimNumber) &&
                Objects.equals(customerNumber, that.customerNumber) &&
                Objects.equals(productCd, that.productCd) &&
                Objects.equals(claimsDamages, that.claimsDamages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), claimNumber, customerNumber, productCd, claimsDamages);
    }
}
