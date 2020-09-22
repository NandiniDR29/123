package com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsTermLifeEmployerModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccidentalDeathModel extends RestError {
    private AccidentalDeathIncidentModel accidentalDeathIncident;

    public AccidentalDeathIncidentModel getAccidentalDeathIncident() {
        return accidentalDeathIncident;
    }

    public void setAccidentalDeathIncident(AccidentalDeathIncidentModel accidentalDeathIncident) {
        this.accidentalDeathIncident = accidentalDeathIncident;
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
        AccidentalDeathModel that = (AccidentalDeathModel) o;
        return Objects.equals(accidentalDeathIncident, that.accidentalDeathIncident);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accidentalDeathIncident);
    }
}
