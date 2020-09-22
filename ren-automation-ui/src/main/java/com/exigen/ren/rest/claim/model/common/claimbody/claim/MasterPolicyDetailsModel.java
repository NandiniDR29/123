package com.exigen.ren.rest.claim.model.common.claimbody.claim;

import com.exigen.ipb.eisa.ws.rest.model.Model;

import java.util.List;

public class MasterPolicyDetailsModel extends Model {

    private String policyNumber;
    private String policyEffectiveDt;
    private String policyStatusCd;
    private String currencyCd;
    private String countryCd;
    private String riskStateCd;
    private int durationInMonths;
    private List<MasterPolicyDetailsCoverageDefinitionsModel> coverageDefinitions;

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getPolicyEffectiveDt() {
        return policyEffectiveDt;
    }

    public void setPolicyEffectiveDt(String policyEffectiveDt) {
        this.policyEffectiveDt = policyEffectiveDt;
    }

    public String getPolicyStatusCd() {
        return policyStatusCd;
    }

    public void setPolicyStatusCd(String policyStatusCd) {
        this.policyStatusCd = policyStatusCd;
    }

    public String getCurrencyCd() {
        return currencyCd;
    }

    public void setCurrencyCd(String currencyCd) {
        this.currencyCd = currencyCd;
    }

    public String getCountryCd() {
        return countryCd;
    }

    public void setCountryCd(String countryCd) {
        this.countryCd = countryCd;
    }

    public String getRiskStateCd() {
        return riskStateCd;
    }

    public void setRiskStateCd(String riskStateCd) {
        this.riskStateCd = riskStateCd;
    }

    public int getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(int durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public List<MasterPolicyDetailsCoverageDefinitionsModel> getCoverageDefinitions() {
        return coverageDefinitions;
    }

    public void setCoverageDefinitions(List<MasterPolicyDetailsCoverageDefinitionsModel> coverageDefinitions) {
        this.coverageDefinitions = coverageDefinitions;
    }

}
