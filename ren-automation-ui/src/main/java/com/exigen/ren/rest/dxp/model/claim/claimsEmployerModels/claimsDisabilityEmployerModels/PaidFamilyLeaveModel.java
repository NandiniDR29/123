package com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsDisabilityEmployerModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaidFamilyLeaveModel extends RestError {
    private List<PaidFamilyLeaveCoverageEvaluationModel> paidFamilyLeaveCoverageEvaluations;

    public List<PaidFamilyLeaveCoverageEvaluationModel> getPaidFamilyLeaveCoverageEvaluations() {
        return paidFamilyLeaveCoverageEvaluations;
    }

    public void setPaidFamilyLeaveCoverageEvaluations(List<PaidFamilyLeaveCoverageEvaluationModel> paidFamilyLeaveCoverageEvaluations) {
        this.paidFamilyLeaveCoverageEvaluations = paidFamilyLeaveCoverageEvaluations;
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
        PaidFamilyLeaveModel that = (PaidFamilyLeaveModel) o;
        return Objects.equals(paidFamilyLeaveCoverageEvaluations, that.paidFamilyLeaveCoverageEvaluations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), paidFamilyLeaveCoverageEvaluations);
    }
}
