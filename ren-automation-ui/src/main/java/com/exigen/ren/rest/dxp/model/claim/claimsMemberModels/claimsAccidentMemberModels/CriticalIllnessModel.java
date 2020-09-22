package com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsAccidentMemberModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CriticalIllnessModel extends RestError {
    private CriticalIllnessIncidentModel criticalIllnessIncident;

    public CriticalIllnessIncidentModel getCriticalIllnessIncident() {
        return criticalIllnessIncident;
    }

    public void setCriticalIllnessIncident(CriticalIllnessIncidentModel criticalIllnessIncident) {
        this.criticalIllnessIncident = criticalIllnessIncident;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CriticalIllnessModel that = (CriticalIllnessModel) o;
        return Objects.equals(criticalIllnessIncident, that.criticalIllnessIncident);
    }

    @Override
    public int hashCode() {
        return Objects.hash(criticalIllnessIncident);
    }
}
