package com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsDisabilityEmployerModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatutoryShortTermDisabilityModel extends RestError {
    private List<StatutoryShortTermDisabilityCoverageEvaluation> statutoryShortTermDisabilityCoverageEvaluations;


    public List<StatutoryShortTermDisabilityCoverageEvaluation> getStatutoryShortTermDisabilityCoverageEvaluations() {
        return statutoryShortTermDisabilityCoverageEvaluations;
    }

    public void setStatutoryShortTermDisabilityCoverageEvaluations(List<StatutoryShortTermDisabilityCoverageEvaluation> statutoryShortTermDisabilityCoverageEvaluations) {
        this.statutoryShortTermDisabilityCoverageEvaluations = statutoryShortTermDisabilityCoverageEvaluations;
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
        StatutoryShortTermDisabilityModel that = (StatutoryShortTermDisabilityModel) o;
        return Objects.equals(statutoryShortTermDisabilityCoverageEvaluations, that.statutoryShortTermDisabilityCoverageEvaluations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), statutoryShortTermDisabilityCoverageEvaluations);
    }
}
