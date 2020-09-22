package com.exigen.ren.rest.rating.model.gb_dn;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AsoAdminCostModel extends Model {

    private String fullyInsuredPremiumNetComAmt;
    private String fullyInsuredPremiumNetComPct;
    private String admExpencesAmt;
    private String admExpencesPct;
    private String claimsAmt;
    private String claimsPct;
    private String riskAndTaxesAmt;
    private String riskAndTaxesPct;
    private String lostInterestAmt;
    private String lostInterestPct;
    private String asoPreCommision;
    private String asoPreCommisionPEPM;
    private String exeptionPct;
    private String asoNetCommisions;
    private String asoCommissionPEPMOverride;
    private String asoFinalPEPM;
    private String asoFinal;
    private String asoEquivalency;
    private String asoFactor;

    public String getFullyInsuredPremiumNetComAmt() {
        return fullyInsuredPremiumNetComAmt;
    }

    public AsoAdminCostModel setFullyInsuredPremiumNetComAmt(String fullyInsuredPremiumNetComAmt) {
        this.fullyInsuredPremiumNetComAmt = fullyInsuredPremiumNetComAmt;
        return this;
    }

    public String getFullyInsuredPremiumNetComPct() {
        return fullyInsuredPremiumNetComPct;
    }

    public AsoAdminCostModel setFullyInsuredPremiumNetComPct(String fullyInsuredPremiumNetComPct) {
        this.fullyInsuredPremiumNetComPct = fullyInsuredPremiumNetComPct;
        return this;
    }

    public String getAdmExpencesAmt() {
        return admExpencesAmt;
    }

    public AsoAdminCostModel setAdmExpencesAmt(String admExpencesAmt) {
        this.admExpencesAmt = admExpencesAmt;
        return this;
    }

    public String getAdmExpencesPct() {
        return admExpencesPct;
    }

    public AsoAdminCostModel setAdmExpencesPct(String admExpencesPct) {
        this.admExpencesPct = admExpencesPct;
        return this;
    }

    public String getClaimsAmt() {
        return claimsAmt;
    }

    public AsoAdminCostModel setClaimsAmt(String claimsAmt) {
        this.claimsAmt = claimsAmt;
        return this;
    }

    public String getClaimsPct() {
        return claimsPct;
    }

    public AsoAdminCostModel setClaimsPct(String claimsPct) {
        this.claimsPct = claimsPct;
        return this;
    }

    public String getRiskAndTaxesAmt() {
        return riskAndTaxesAmt;
    }

    public AsoAdminCostModel setRiskAndTaxesAmt(String riskAndTaxesAmt) {
        this.riskAndTaxesAmt = riskAndTaxesAmt;
        return this;
    }

    public String getLostInterestAmt() {
        return lostInterestAmt;
    }

    public AsoAdminCostModel setLostInterestAmt(String lostInterestAmt) {
        this.lostInterestAmt = lostInterestAmt;
        return this;
    }

    public String getLostInterestPct() {
        return lostInterestPct;
    }

    public AsoAdminCostModel setLostInterestPct(String lostInterestPct) {
        this.lostInterestPct = lostInterestPct;
        return this;
    }

    public String getAsoPreCommision() {
        return asoPreCommision;
    }

    public AsoAdminCostModel setAsoPreCommision(String asoPreCommision) {
        this.asoPreCommision = asoPreCommision;
        return this;
    }

    public String getAsoPreCommisionPEPM() {
        return asoPreCommisionPEPM;
    }

    public AsoAdminCostModel setAsoPreCommisionPEPM(String asoPreCommisionPEPM) {
        this.asoPreCommisionPEPM = asoPreCommisionPEPM;
        return this;
    }

    public String getExeptionPct() {
        return exeptionPct;
    }

    public AsoAdminCostModel setExeptionPct(String exeptionPct) {
        this.exeptionPct = exeptionPct;
        return this;
    }

    public String getAsoNetCommisions() {
        return asoNetCommisions;
    }

    public AsoAdminCostModel setAsoNetCommisions(String asoNetCommisions) {
        this.asoNetCommisions = asoNetCommisions;
        return this;
    }

    public String getAsoCommissionPEPMOverride() {
        return asoCommissionPEPMOverride;
    }

    public AsoAdminCostModel setAsoCommissionPEPMOverride(String asoCommissionPEPMOverride) {
        this.asoCommissionPEPMOverride = asoCommissionPEPMOverride;
        return this;
    }

    public String getAsoFinalPEPM() {
        return asoFinalPEPM;
    }

    public AsoAdminCostModel setAsoFinalPEPM(String asoFinalPEPM) {
        this.asoFinalPEPM = asoFinalPEPM;
        return this;
    }

    public String getAsoFinal() {
        return asoFinal;
    }

    public AsoAdminCostModel setAsoFinal(String asoFinal) {
        this.asoFinal = asoFinal;
        return this;
    }

    public String getAsoEquivalency() {
        return asoEquivalency;
    }

    public AsoAdminCostModel setAsoEquivalency(String asoEquivalency) {
        this.asoEquivalency = asoEquivalency;
        return this;
    }

    public String getAsoFactor() {
        return asoFactor;
    }

    public AsoAdminCostModel setAsoFactor(String asoFactor) {
        this.asoFactor = asoFactor;
        return this;
    }

    public String getRiskAndTaxesPct() {
        return riskAndTaxesPct;
    }

    public AsoAdminCostModel setRiskAndTaxesPct(String riskAndTaxesPct) {
        this.riskAndTaxesPct = riskAndTaxesPct;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AsoAdminCostModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AsoAdminCostModel that = (AsoAdminCostModel) o;
        return Objects.equals(fullyInsuredPremiumNetComAmt, that.fullyInsuredPremiumNetComAmt) &&
                Objects.equals(fullyInsuredPremiumNetComPct, that.fullyInsuredPremiumNetComPct) &&
                Objects.equals(admExpencesAmt, that.admExpencesAmt) &&
                Objects.equals(admExpencesPct, that.admExpencesPct) &&
                Objects.equals(claimsAmt, that.claimsAmt) &&
                Objects.equals(claimsPct, that.claimsPct) &&
                Objects.equals(riskAndTaxesAmt, that.riskAndTaxesAmt) &&
                Objects.equals(riskAndTaxesPct, that.riskAndTaxesPct) &&
                Objects.equals(lostInterestAmt, that.lostInterestAmt) &&
                Objects.equals(lostInterestPct, that.lostInterestPct) &&
                Objects.equals(asoPreCommision, that.asoPreCommision) &&
                Objects.equals(asoPreCommisionPEPM, that.asoPreCommisionPEPM) &&
                Objects.equals(exeptionPct, that.exeptionPct) &&
                Objects.equals(asoNetCommisions, that.asoNetCommisions) &&
                Objects.equals(asoCommissionPEPMOverride, that.asoCommissionPEPMOverride) &&
                Objects.equals(asoFinalPEPM, that.asoFinalPEPM) &&
                Objects.equals(asoFinal, that.asoFinal) &&
                Objects.equals(asoEquivalency, that.asoEquivalency) &&
                Objects.equals(asoFactor, that.asoFactor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fullyInsuredPremiumNetComAmt, fullyInsuredPremiumNetComPct, admExpencesAmt, admExpencesPct, claimsAmt, claimsPct, riskAndTaxesAmt, riskAndTaxesPct, lostInterestAmt, lostInterestPct, asoPreCommision, asoPreCommisionPEPM, exeptionPct, asoNetCommisions, asoCommissionPEPMOverride, asoFinalPEPM, asoFinal, asoEquivalency, asoFactor);
    }

    @Override
    public String toString() {
        return "AsoAdminCostModel{" +
                "fullyInsuredPremiumNetComAmt='" + fullyInsuredPremiumNetComAmt + '\'' +
                ", fullyInsuredPremiumNetComPct='" + fullyInsuredPremiumNetComPct + '\'' +
                ", admExpencesAmt='" + admExpencesAmt + '\'' +
                ", admExpencesPct='" + admExpencesPct + '\'' +
                ", claimsAmt='" + claimsAmt + '\'' +
                ", claimsPct='" + claimsPct + '\'' +
                ", riskAndTaxesAmt='" + riskAndTaxesAmt + '\'' +
                ", riskAndTaxesPct='" + riskAndTaxesPct + '\'' +
                ", lostInterestAmt='" + lostInterestAmt + '\'' +
                ", lostInterestPct='" + lostInterestPct + '\'' +
                ", asoPreCommision='" + asoPreCommision + '\'' +
                ", asoPreCommisionPEPM='" + asoPreCommisionPEPM + '\'' +
                ", exeptionPct='" + exeptionPct + '\'' +
                ", asoNetCommisions='" + asoNetCommisions + '\'' +
                ", asoCommissionPEPMOverride='" + asoCommissionPEPMOverride + '\'' +
                ", asoFinalPEPM='" + asoFinalPEPM + '\'' +
                ", asoFinal='" + asoFinal + '\'' +
                ", asoEquivalency='" + asoEquivalency + '\'' +
                ", asoFactor='" + asoFactor + '\'' +
                '}';
    }
}
