package com.exigen.ren.rest.dxp.model.policy;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PolicyPreconfigGroupCoverageDefinitions extends RestError {


    private String packageName;
    private String totalNumberOfEligibleLives;
    private String requiredParticipationPct;
    private String rateBasisCd;
    private String sicCode;
    private String packageCd;
    private PolicyDefPaymentModesSelectionComponent groupCoverageDefPaymentModesSelectionComponent;
    private PolicyPreconfigGroupEligibility preconfigGroupEligibility;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTotalNumberOfEligibleLives() {
        return totalNumberOfEligibleLives;
    }

    public void setTotalNumberOfEligibleLives(String totalNumberOfEligibleLives) {
        this.totalNumberOfEligibleLives = totalNumberOfEligibleLives;
    }

    public PolicyDefPaymentModesSelectionComponent getGroupCoverageDefPaymentModesSelectionComponent() {
        return groupCoverageDefPaymentModesSelectionComponent;
    }

    public void setGroupCoverageDefPaymentModesSelectionComponent(PolicyDefPaymentModesSelectionComponent groupCoverageDefPaymentModesSelectionComponent) {
        this.groupCoverageDefPaymentModesSelectionComponent = groupCoverageDefPaymentModesSelectionComponent;
    }

    public PolicyPreconfigGroupEligibility getPreconfigGroupEligibility() {
        return preconfigGroupEligibility;
    }

    public void setPreconfigGroupEligibility(PolicyPreconfigGroupEligibility preconfigGroupEligibility) {
        this.preconfigGroupEligibility = preconfigGroupEligibility;
    }

    public String getRequiredParticipationPct() {
        return requiredParticipationPct;
    }

    public void setRequiredParticipationPct(String requiredParticipationPct) {
        this.requiredParticipationPct = requiredParticipationPct;
    }

    public String getRateBasisCd() {
        return rateBasisCd;
    }

    public void setRateBasisCd(String rateBasisCd) {
        this.rateBasisCd = rateBasisCd;
    }

    public String getSicCode() {
        return sicCode;
    }

    public void setSicCode(String sicCode) {
        this.sicCode = sicCode;
    }

    public String getPackageCd() {
        return packageCd;
    }

    public void setPackageCd(String packageCd) {
        this.packageCd = packageCd;
    }
}