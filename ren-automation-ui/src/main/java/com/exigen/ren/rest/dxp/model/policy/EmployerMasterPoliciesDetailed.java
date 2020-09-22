package com.exigen.ren.rest.dxp.model.policy;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployerMasterPoliciesDetailed extends RestError {

    private String transactionTypeCd;
    private String revisionNumber;
    private String policyNumber;
    private List<PolicyPreconfigGroupCoverageDefinitions> preconfigGroupCoverageDefinitions;

    public String getTransactionTypeCd() {
        return transactionTypeCd;
    }

    public void setTransactionTypeCd(String transactionTypeCd) {
        this.transactionTypeCd = transactionTypeCd;
    }

    public String getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(String revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public List<PolicyPreconfigGroupCoverageDefinitions> getPreconfigGroupCoverageDefinitions() {
        return preconfigGroupCoverageDefinitions;
    }

    public void setPreconfigGroupCoverageDefinitions(List<PolicyPreconfigGroupCoverageDefinitions> preconfigGroupCoverageDefinitions) {
        this.preconfigGroupCoverageDefinitions = preconfigGroupCoverageDefinitions;
    }
}