package com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsDentalMemberModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DentalEvaluationFeaturesModel extends RestError {
    private String netBenefit;
    private String statusReason;
    private String patientResponsibility;
    private String cobAmount;
    private String dateToBePaid;
    private String consideredProcedure;
    private String lineID;
    private String coveredFee;
    private String payableDeductible;
    private String innOon;
    private String category;
    private String coinsuranceAmt;

    public String getNetBenefit() {
        return netBenefit;
    }

    public void setNetBenefit(String netBenefit) {
        this.netBenefit = netBenefit;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public String getPatientResponsibility() {
        return patientResponsibility;
    }

    public void setPatientResponsibility(String patientResponsibility) {
        this.patientResponsibility = patientResponsibility;
    }

    public String getCobAmount() {
        return cobAmount;
    }

    public void setCobAmount(String cobAmount) {
        this.cobAmount = cobAmount;
    }

    public String getDateToBePaid() {
        return dateToBePaid;
    }

    public void setDateToBePaid(String dateToBePaid) {
        this.dateToBePaid = dateToBePaid;
    }

    public String getConsideredProcedure() {
        return consideredProcedure;
    }

    public void setConsideredProcedure(String consideredProcedure) {
        this.consideredProcedure = consideredProcedure;
    }

    public String getLineID() {
        return lineID;
    }

    public void setLineID(String lineID) {
        this.lineID = lineID;
    }

    public String getCoveredFee() {
        return coveredFee;
    }

    public void setCoveredFee(String coveredFee) {
        this.coveredFee = coveredFee;
    }

    public String getPayableDeductible() {
        return payableDeductible;
    }

    public void setPayableDeductible(String payableDeductible) {
        this.payableDeductible = payableDeductible;
    }

    public String getInnOon() {
        return innOon;
    }

    public void setInnOon(String innOon) {
        this.innOon = innOon;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCoinsuranceAmt() {
        return coinsuranceAmt;
    }

    public void setCoinsuranceAmt(String coinsuranceAmt) {
        this.coinsuranceAmt = coinsuranceAmt;
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
        DentalEvaluationFeaturesModel that = (DentalEvaluationFeaturesModel) o;
        return Objects.equals(netBenefit, that.netBenefit) &&
                Objects.equals(statusReason, that.statusReason) &&
                Objects.equals(patientResponsibility, that.patientResponsibility) &&
                Objects.equals(cobAmount, that.cobAmount) &&
                Objects.equals(dateToBePaid, that.dateToBePaid) &&
                Objects.equals(consideredProcedure, that.consideredProcedure) &&
                Objects.equals(lineID, that.lineID) &&
                Objects.equals(coveredFee, that.coveredFee) &&
                Objects.equals(payableDeductible, that.payableDeductible) &&
                Objects.equals(innOon, that.innOon) &&
                Objects.equals(category, that.category) &&
                Objects.equals(coinsuranceAmt, that.coinsuranceAmt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), netBenefit, statusReason, patientResponsibility, cobAmount, dateToBePaid, consideredProcedure, lineID, coveredFee, payableDeductible, innOon, category, coinsuranceAmt);
    }
}
