package com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsDentalMemberModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClaimsServiceRequestModel extends RestError {
    private List<ClaimsDentalVendorViewModel> claimsDentalVendorViews;

    public List<ClaimsDentalVendorViewModel> getClaimsDentalVendorViews() {
        return claimsDentalVendorViews;
    }

    public void setClaimsDentalVendorViews(List<ClaimsDentalVendorViewModel> claimsDentalVendorViews) {
        this.claimsDentalVendorViews = claimsDentalVendorViews;
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
        ClaimsServiceRequestModel that = (ClaimsServiceRequestModel) o;
        return Objects.equals(claimsDentalVendorViews, that.claimsDentalVendorViews);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), claimsDentalVendorViews);
    }
}
