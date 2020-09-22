package com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsDisabilityMemberModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LongTermDisabilityIncidentModel extends RestError {
    private List<LongTermDisabilityDateModel> longTermDisabilityDates;

    public List<LongTermDisabilityDateModel> getLongTermDisabilityDates() {
        return longTermDisabilityDates;
    }

    public void setLongTermDisabilityDates(List<LongTermDisabilityDateModel> longTermDisabilityDates) {
        this.longTermDisabilityDates = longTermDisabilityDates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LongTermDisabilityIncidentModel that = (LongTermDisabilityIncidentModel) o;
        return Objects.equals(longTermDisabilityDates, that.longTermDisabilityDates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(longTermDisabilityDates);
    }
}
