package com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels;

import com.exigen.ren.rest.model.RestError;

import java.util.Objects;

public class ClaimsEmployerIncidentAbstractModel extends RestError {
    private String dateOfLastWorked;

    public String getDateOfLastWorked() {
        return dateOfLastWorked;
    }

    public void setDateOfLastWorked(String dateOfLastWorked) {
        this.dateOfLastWorked = dateOfLastWorked;
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
        ClaimsEmployerIncidentAbstractModel that = (ClaimsEmployerIncidentAbstractModel) o;
        return Objects.equals(dateOfLastWorked, that.dateOfLastWorked);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateOfLastWorked);
    }
}