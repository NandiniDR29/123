package com.exigen.ren.rest.dxp.model;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CertificatesPremiumSummariesModel extends RestError {

    private List<PremiumSummariesModel> premiumSummaries;
    private String planCd;
    private String planName;

    public List<PremiumSummariesModel> getPremiumSummaries() {
        return premiumSummaries;
    }

    public void setPremiumSummaries(List<PremiumSummariesModel> premiumSummaries) {
        this.premiumSummaries = premiumSummaries;
    }

    public String getPlanCd() {
        return planCd;
    }

    public void setPlanCd(String planCd) {
        this.planCd = planCd;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        CertificatesPremiumSummariesModel that = (CertificatesPremiumSummariesModel) o;
        return Objects.equals(premiumSummaries, that.premiumSummaries) &&
                Objects.equals(planCd, that.planCd) &&
                Objects.equals(planName, that.planName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), premiumSummaries, planCd, planName);
    }
}
