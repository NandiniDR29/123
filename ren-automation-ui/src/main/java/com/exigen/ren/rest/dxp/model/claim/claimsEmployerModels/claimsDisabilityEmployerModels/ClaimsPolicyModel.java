package com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsDisabilityEmployerModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClaimsPolicyModel extends RestError {
    private String policyNumber;
    private PrecClaimsPolicyRiskParticipantModel precClaimsPolicyRiskParticipant;

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public PrecClaimsPolicyRiskParticipantModel getPrecClaimsPolicyRiskParticipant() {
        return precClaimsPolicyRiskParticipant;
    }

    public void setPrecClaimsPolicyRiskParticipant(PrecClaimsPolicyRiskParticipantModel precClaimsPolicyRiskParticipant) {
        this.precClaimsPolicyRiskParticipant = precClaimsPolicyRiskParticipant;
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
        ClaimsPolicyModel that = (ClaimsPolicyModel) o;
        return Objects.equals(policyNumber, that.policyNumber) &&
                Objects.equals(precClaimsPolicyRiskParticipant, that.precClaimsPolicyRiskParticipant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), policyNumber, precClaimsPolicyRiskParticipant);
    }
}
