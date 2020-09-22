package com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsDisabilityEmployerModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrecClaimsPolicyRiskParticipantModel extends RestError {
    private PrecClaimsPolicyRiskParticipantPartyModel precClaimsPolicyRiskParticipantParty;

    public PrecClaimsPolicyRiskParticipantPartyModel getPrecClaimsPolicyRiskParticipantParty() {
        return precClaimsPolicyRiskParticipantParty;
    }

    public void setPrecClaimsPolicyRiskParticipantParty(PrecClaimsPolicyRiskParticipantPartyModel precClaimsPolicyRiskParticipantParty) {
        this.precClaimsPolicyRiskParticipantParty = precClaimsPolicyRiskParticipantParty;
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
        PrecClaimsPolicyRiskParticipantModel that = (PrecClaimsPolicyRiskParticipantModel) o;
        return Objects.equals(precClaimsPolicyRiskParticipantParty, that.precClaimsPolicyRiskParticipantParty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), precClaimsPolicyRiskParticipantParty);
    }
}
