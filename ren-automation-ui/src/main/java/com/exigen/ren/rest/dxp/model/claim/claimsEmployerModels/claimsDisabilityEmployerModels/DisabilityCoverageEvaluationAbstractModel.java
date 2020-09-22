package com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsDisabilityEmployerModels;

import com.exigen.ren.rest.model.RestError;

import java.util.Objects;

public abstract class DisabilityCoverageEvaluationAbstractModel extends RestError {
    private String supportingDocReceivedDt;
    private String verificationDt;
    private String approvedThroughDt;
    private String eligibilityVerified;
    private String liabilityDecision;

    public String getSupportingDocReceivedDt() {
        return supportingDocReceivedDt;
    }

    public void setSupportingDocReceivedDt(String supportingDocReceivedDt) {
        this.supportingDocReceivedDt = supportingDocReceivedDt;
    }

    public String getVerificationDt() {
        return verificationDt;
    }

    public void setVerificationDt(String verificationDt) {
        this.verificationDt = verificationDt;
    }

    public String getApprovedThroughDt() {
        return approvedThroughDt;
    }

    public void setApprovedThroughDt(String approvedThroughDt) {
        this.approvedThroughDt = approvedThroughDt;
    }

    public String getEligibilityVerified() {
        return eligibilityVerified;
    }

    public void setEligibilityVerified(String eligibilityVerified) {
        this.eligibilityVerified = eligibilityVerified;
    }

    public String getLiabilityDecision() {
        return liabilityDecision;
    }

    public void setLiabilityDecision(String liabilityDecision) {
        this.liabilityDecision = liabilityDecision;
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
        DisabilityCoverageEvaluationAbstractModel that = (DisabilityCoverageEvaluationAbstractModel) o;
        return Objects.equals(supportingDocReceivedDt, that.supportingDocReceivedDt) &&
                Objects.equals(verificationDt, that.verificationDt) &&
                Objects.equals(approvedThroughDt, that.approvedThroughDt) &&
                Objects.equals(eligibilityVerified, that.eligibilityVerified) &&
                Objects.equals(liabilityDecision, that.liabilityDecision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), supportingDocReceivedDt, verificationDt, approvedThroughDt, eligibilityVerified, liabilityDecision);
    }
}
