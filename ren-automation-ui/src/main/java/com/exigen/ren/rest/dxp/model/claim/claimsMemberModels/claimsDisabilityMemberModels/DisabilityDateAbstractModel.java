package com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsDisabilityMemberModels;

import com.exigen.ren.rest.model.RestError;

import java.util.Objects;

public abstract class DisabilityDateAbstractModel extends RestError {
    private String diDisabilityDate;
    private String diDisabilityType;

    public String getDiDisabilityDate() {
        return diDisabilityDate;
    }

    public void setDiDisabilityDate(String diDisabilityDate) {
        this.diDisabilityDate = diDisabilityDate;
    }

    public String getDiDisabilityType() {
        return diDisabilityType;
    }

    public void setDiDisabilityType(String diDisabilityType) {
        this.diDisabilityType = diDisabilityType;
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
        DisabilityDateAbstractModel that = (DisabilityDateAbstractModel) o;
        return Objects.equals(diDisabilityDate, that.diDisabilityDate) &&
                Objects.equals(diDisabilityType, that.diDisabilityType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), diDisabilityDate, diDisabilityType);
    }
}
