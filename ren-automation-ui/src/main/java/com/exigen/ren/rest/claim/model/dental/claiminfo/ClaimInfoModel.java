package com.exigen.ren.rest.claim.model.dental.claiminfo;

import com.exigen.ren.rest.model.RestError;

import java.util.List;

public class ClaimInfoModel extends RestError {
    private String claimID;
    private List<ClaimInfoSubmittedProcedureModel> submittedProcedures;

    public String getClaimID() {
        return claimID;
    }

    public void setClaimID(String claimID) {
        this.claimID = claimID;
    }

    public List<ClaimInfoSubmittedProcedureModel> getSubmittedProcedures() {
        return submittedProcedures;
    }

    public void setSubmittedProcedures(List<ClaimInfoSubmittedProcedureModel> submittedProcedures) {
        this.submittedProcedures = submittedProcedures;
    }

}
