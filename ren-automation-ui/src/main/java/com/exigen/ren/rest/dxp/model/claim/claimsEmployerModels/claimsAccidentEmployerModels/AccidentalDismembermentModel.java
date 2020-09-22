package com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsAccidentEmployerModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccidentalDismembermentModel extends RestError {
    private AccidentalDismembermentIncidentModel accidentalDismembermentIncident;

    public AccidentalDismembermentIncidentModel getAccidentalDismembermentIncident() {
        return accidentalDismembermentIncident;
    }

    public void setAccidentalDismembermentIncident(AccidentalDismembermentIncidentModel accidentalDismembermentIncident) {
        this.accidentalDismembermentIncident = accidentalDismembermentIncident;
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
        AccidentalDismembermentModel that = (AccidentalDismembermentModel) o;
        return Objects.equals(accidentalDismembermentIncident, that.accidentalDismembermentIncident);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accidentalDismembermentIncident);
    }
}
