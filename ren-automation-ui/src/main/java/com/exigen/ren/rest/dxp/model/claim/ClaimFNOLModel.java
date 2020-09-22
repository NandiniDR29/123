package com.exigen.ren.rest.dxp.model.claim;

import com.exigen.ren.rest.model.RestError;

import java.util.Objects;

public class ClaimFNOLModel extends RestError {
    private String claimNumber;

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        ClaimFNOLModel that = (ClaimFNOLModel) o;
        return Objects.equals(claimNumber, that.claimNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), claimNumber);
    }
}

