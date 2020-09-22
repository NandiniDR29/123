package com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsDentalMemberModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClaimsDamageModel extends RestError {
    private ClaimsProcedureModel claimsProcedure;
    private String lossDescription;
    private List<DentalEvaluationFeaturesModel> dentalEvaluationFeatures;

    public ClaimsProcedureModel getClaimsProcedure() {
        return claimsProcedure;
    }

    public void setClaimsProcedure(ClaimsProcedureModel claimsProcedure) {
        this.claimsProcedure = claimsProcedure;
    }

    public String getLossDescription() {
        return lossDescription;
    }

    public void setLossDescription(String lossDescription) {
        this.lossDescription = lossDescription;
    }

    public List<DentalEvaluationFeaturesModel> getDentalEvaluationFeatures() {
        return dentalEvaluationFeatures;
    }

    public void setDentalEvaluationFeatures(List<DentalEvaluationFeaturesModel> dentalEvaluationFeatures) {
        this.dentalEvaluationFeatures = dentalEvaluationFeatures;
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
        return Objects.equals(claimsProcedure, that.claimsProcedure) &&
                Objects.equals(lossDescription, that.lossDescription) &&
                Objects.equals(dentalEvaluationFeatures, that.dentalEvaluationFeatures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), claimsProcedure, lossDescription, dentalEvaluationFeatures);
    }
}
