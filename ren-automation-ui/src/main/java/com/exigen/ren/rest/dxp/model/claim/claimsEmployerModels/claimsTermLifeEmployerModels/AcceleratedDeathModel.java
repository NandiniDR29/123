package com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsTermLifeEmployerModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AcceleratedDeathModel extends RestError {
    private AcceleratedDeathIncidentModel acceleratedDeathIncident;

    public AcceleratedDeathIncidentModel getAcceleratedDeathIncident() {
        return acceleratedDeathIncident;
    }

    public void setAcceleratedDeathIncident(AcceleratedDeathIncidentModel acceleratedDeathIncident) {
        this.acceleratedDeathIncident = acceleratedDeathIncident;
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
        AcceleratedDeathModel that = (AcceleratedDeathModel) o;
        return Objects.equals(acceleratedDeathIncident, that.acceleratedDeathIncident);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), acceleratedDeathIncident);
    }
}
