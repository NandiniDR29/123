package com.exigen.ren.rest.claim.model.dental.claiminfo;

import com.exigen.ren.rest.model.RestError;

public class ClaimInfoSubmittedProcedurePolicyInfoModel extends RestError {
    private String policyPaidToDate;
    private String policyPaidToDateWithGracePeriod;

    public String getPolicyPaidToDate() {
        return policyPaidToDate;
    }

    public void setPolicyPaidToDate(String policyPaidToDate) {
        this.policyPaidToDate = policyPaidToDate;
    }

    public String getPolicyPaidToDateWithGracePeriod() {
        return policyPaidToDateWithGracePeriod;
    }

    public void setPolicyPaidToDateWithGracePeriod(String policyPaidToDateWithGracePeriod) {
        this.policyPaidToDateWithGracePeriod = policyPaidToDateWithGracePeriod;
    }

}
