package com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsTermLifeEmployerModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeathModel extends RestError {
    private DeathIncidentModel deathIncident;

    public DeathIncidentModel getDeathIncident() {
        return deathIncident;
    }

    public void setDeathIncident(DeathIncidentModel deathIncident) {
        this.deathIncident = deathIncident;
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
        DeathModel that = (DeathModel) o;
        return Objects.equals(deathIncident, that.deathIncident);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), deathIncident);
    }
}
