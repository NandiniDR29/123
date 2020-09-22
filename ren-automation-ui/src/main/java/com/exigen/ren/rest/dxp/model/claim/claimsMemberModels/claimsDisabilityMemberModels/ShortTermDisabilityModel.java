package com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsDisabilityMemberModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShortTermDisabilityModel extends RestError {
    private ShortTermDisabilityIncidentModel shortTermDisabilityIncident;

    public ShortTermDisabilityIncidentModel getShortTermDisabilityIncident() {
        return shortTermDisabilityIncident;
    }

    public void setShortTermDisabilityIncident(ShortTermDisabilityIncidentModel shortTermDisabilityIncident) {
        this.shortTermDisabilityIncident = shortTermDisabilityIncident;
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
        ShortTermDisabilityModel that = (ShortTermDisabilityModel) o;
        return Objects.equals(shortTermDisabilityIncident, that.shortTermDisabilityIncident);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), shortTermDisabilityIncident);
    }
}
