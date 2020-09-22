package com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsDisabilityEmployerModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClaimsDamageModel extends RestError {
    private ShortTermDisabilityModel shortTermDisability;
    private LongTermDisabilityModel longTermDisability;
    private PaidFamilyLeaveModel paidFamilyLeave;
    private StatutoryShortTermDisabilityModel statutoryShortTermDisability;

    public ShortTermDisabilityModel getShortTermDisability() {
        return shortTermDisability;
    }

    public void setShortTermDisability(ShortTermDisabilityModel shortTermDisability) {
        this.shortTermDisability = shortTermDisability;
    }

    public LongTermDisabilityModel getLongTermDisability() {
        return longTermDisability;
    }

    public void setLongTermDisability(LongTermDisabilityModel longTermDisability) {
        this.longTermDisability = longTermDisability;
    }

    public PaidFamilyLeaveModel getPaidFamilyLeave() {
        return paidFamilyLeave;
    }

    public void setPaidFamilyLeave(PaidFamilyLeaveModel paidFamilyLeave) {
        this.paidFamilyLeave = paidFamilyLeave;
    }

    public StatutoryShortTermDisabilityModel getStatutoryShortTermDisability() {
        return statutoryShortTermDisability;
    }

    public void setStatutoryShortTermDisability(StatutoryShortTermDisabilityModel statutoryShortTermDisability) {
        this.statutoryShortTermDisability = statutoryShortTermDisability;
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
        return Objects.equals(shortTermDisability, that.shortTermDisability) &&
                Objects.equals(longTermDisability, that.longTermDisability) &&
                Objects.equals(paidFamilyLeave, that.paidFamilyLeave) &&
                Objects.equals(statutoryShortTermDisability, that.statutoryShortTermDisability);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), shortTermDisability, longTermDisability, paidFamilyLeave, statutoryShortTermDisability);
    }
}
