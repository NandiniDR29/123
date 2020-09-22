package com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsDisabilityMemberModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClaimsDamageModel extends RestError {
    private LongTermDisabilityModel longTermDisability;
    private ShortTermDisabilityModel shortTermDisability;

    public LongTermDisabilityModel getLongTermDisability() {
        return longTermDisability;
    }

    public void setLongTermDisability(LongTermDisabilityModel longTermDisability) {
        this.longTermDisability = longTermDisability;
    }

    public ShortTermDisabilityModel getShortTermDisability() {
        return shortTermDisability;
    }

    public void setShortTermDisability(ShortTermDisabilityModel shortTermDisability) {
        this.shortTermDisability = shortTermDisability;
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
        ClaimsDamageModel that = (ClaimsDamageModel) o;
        return Objects.equals(longTermDisability, that.longTermDisability) &&
                Objects.equals(shortTermDisability, that.shortTermDisability);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), longTermDisability, shortTermDisability);
    }
}