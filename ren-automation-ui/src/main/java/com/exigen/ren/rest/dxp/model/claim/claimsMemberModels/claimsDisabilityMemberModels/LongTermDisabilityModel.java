package com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsDisabilityMemberModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LongTermDisabilityModel extends RestError {
    private LongTermDisabilityIncidentModel longTermDisabilityIncident;

    public LongTermDisabilityIncidentModel getLongTermDisabilityIncident() {
        return longTermDisabilityIncident;
    }

    public void setLongTermDisabilityIncident(LongTermDisabilityIncidentModel longTermDisabilityIncident) {
        this.longTermDisabilityIncident = longTermDisabilityIncident;
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
        LongTermDisabilityModel that = (LongTermDisabilityModel) o;
        return Objects.equals(longTermDisabilityIncident, that.longTermDisabilityIncident);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), longTermDisabilityIncident);
    }
}
