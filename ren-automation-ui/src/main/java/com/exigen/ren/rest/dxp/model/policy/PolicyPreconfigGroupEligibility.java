package com.exigen.ren.rest.dxp.model.policy;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PolicyPreconfigGroupEligibility extends RestError {

    private String waitingPeriodInteger;
    private String dependentMaxAge;

    public String getWaitingPeriodInteger() {
        return waitingPeriodInteger;
    }

    public void setWaitingPeriodInteger(String waitingPeriodInteger) {
        this.waitingPeriodInteger = waitingPeriodInteger;
    }

    public String getDependentMaxAge() {
        return dependentMaxAge;
    }

    public void setDependentMaxAge(String dependentMaxAge) {
        this.dependentMaxAge = dependentMaxAge;
    }
}