package com.exigen.ren.rest.claim.model.dental.claiminfo;

import com.exigen.ren.rest.model.RestError;

import java.util.Objects;

public class ClaimInfoSubmittedProcedureModel extends RestError {
    private String procedureID;
    private ClaimInfoSubmittedProcedurePolicyInfoModel policyInfo;
    private DentistModel dentist;
    private CalculationResultModel calculationResult;
    private String dateOfService;

    public String getProcedureID() {
        return procedureID;
    }

    public void setProcedureID(String procedureID) {
        this.procedureID = procedureID;
    }

    public ClaimInfoSubmittedProcedurePolicyInfoModel getPolicyInfo() {
        return policyInfo;
    }

    public void setPolicyInfo(ClaimInfoSubmittedProcedurePolicyInfoModel policyInfo) {
        this.policyInfo = policyInfo;
    }

    public DentistModel getDentist() {
        return dentist;
    }

    public void setDentist(DentistModel dentist) {
        this.dentist = dentist;
    }

    public CalculationResultModel getCalculationResult() {
        return calculationResult;
    }

    public void setCalculationResult(CalculationResultModel calculationResult) {
        this.calculationResult = calculationResult;
    }

    public String getDateOfService() {
        return dateOfService;
    }

    public void setDateOfService(String dateOfService) {
        this.dateOfService = dateOfService;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClaimInfoSubmittedProcedureModel that = (ClaimInfoSubmittedProcedureModel) o;
        return Objects.equals(procedureID, that.procedureID) &&
                Objects.equals(policyInfo, that.policyInfo) &&
                Objects.equals(dentist, that.dentist) &&
                Objects.equals(calculationResult, that.calculationResult) &&
                Objects.equals(dateOfService, that.dateOfService);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), procedureID, policyInfo, dentist, calculationResult, dateOfService);
    }
}
