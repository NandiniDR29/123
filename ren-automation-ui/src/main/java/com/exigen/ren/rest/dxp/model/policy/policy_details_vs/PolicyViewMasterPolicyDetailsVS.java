package com.exigen.ren.rest.dxp.model.policy.policy_details_vs;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PolicyViewMasterPolicyDetailsVS extends RestError {


    private List<PreconfigGroupCoverageDefinitions> preconfigGroupCoverageDefinitions;

    public List<PreconfigGroupCoverageDefinitions> getPreconfigGroupCoverageDefinitions() {
        return preconfigGroupCoverageDefinitions;
    }

    public void setPreconfigGroupCoverageDefinitions(List<PreconfigGroupCoverageDefinitions> preconfigGroupCoverageDefinitions) {
        this.preconfigGroupCoverageDefinitions = preconfigGroupCoverageDefinitions;
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
        PolicyViewMasterPolicyDetailsVS that = (PolicyViewMasterPolicyDetailsVS) o;
        return Objects.equals(preconfigGroupCoverageDefinitions, that.preconfigGroupCoverageDefinitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), preconfigGroupCoverageDefinitions);
    }
}


