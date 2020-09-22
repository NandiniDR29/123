package com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsAccidentMemberModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccidentalInjuryModel extends RestError {
    private AccidentalInjuryIncidentModel accidentalInjuryIncident;

    public AccidentalInjuryIncidentModel getAccidentalInjuryIncident() {
        return accidentalInjuryIncident;
    }

    public void setAccidentalInjuryIncident(AccidentalInjuryIncidentModel accidentalInjuryIncident) {
        this.accidentalInjuryIncident = accidentalInjuryIncident;
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
        AccidentalInjuryModel that = (AccidentalInjuryModel) o;
        return Objects.equals(accidentalInjuryIncident, that.accidentalInjuryIncident);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accidentalInjuryIncident);
    }
}