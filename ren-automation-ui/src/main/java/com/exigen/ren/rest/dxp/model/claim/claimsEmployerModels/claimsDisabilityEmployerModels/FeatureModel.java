package com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsDisabilityEmployerModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeatureModel extends RestError {
    private String eliminationPeriodEndDate;
    private String benefitPeriod;
    private String eliminationPeriod;
    private String benefitPeriodEndDate;
    private String benefitPeriodStartDate;
    private String periodCompleted;
    private String periodRemaining;
    private String eliminationPeriodStartDate;

    public String getEliminationPeriodEndDate() {
        return eliminationPeriodEndDate;
    }

    public void setEliminationPeriodEndDate(String eliminationPeriodEndDate) {
        this.eliminationPeriodEndDate = eliminationPeriodEndDate;
    }

    public String getBenefitPeriod() {
        return benefitPeriod;
    }

    public void setBenefitPeriod(String benefitPeriod) {
        this.benefitPeriod = benefitPeriod;
    }

    public String getEliminationPeriod() {
        return eliminationPeriod;
    }

    public void setEliminationPeriod(String eliminationPeriod) {
        this.eliminationPeriod = eliminationPeriod;
    }

    public String getBenefitPeriodEndDate() {
        return benefitPeriodEndDate;
    }

    public void setBenefitPeriodEndDate(String benefitPeriodEndDate) {
        this.benefitPeriodEndDate = benefitPeriodEndDate;
    }

    public String getBenefitPeriodStartDate() {
        return benefitPeriodStartDate;
    }

    public void setBenefitPeriodStartDate(String benefitPeriodStartDate) {
        this.benefitPeriodStartDate = benefitPeriodStartDate;
    }

    public String getPeriodCompleted() {
        return periodCompleted;
    }

    public void setPeriodCompleted(String periodCompleted) {
        this.periodCompleted = periodCompleted;
    }

    public String getPeriodRemaining() {
        return periodRemaining;
    }

    public void setPeriodRemaining(String periodRemaining) {
        this.periodRemaining = periodRemaining;
    }

    public String getEliminationPeriodStartDate() {
        return eliminationPeriodStartDate;
    }

    public void setEliminationPeriodStartDate(String eliminationPeriodStartDate) {
        this.eliminationPeriodStartDate = eliminationPeriodStartDate;
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
        FeatureModel that = (FeatureModel) o;
        return Objects.equals(eliminationPeriodEndDate, that.eliminationPeriodEndDate) &&
                Objects.equals(benefitPeriod, that.benefitPeriod) &&
                Objects.equals(eliminationPeriod, that.eliminationPeriod) &&
                Objects.equals(benefitPeriodEndDate, that.benefitPeriodEndDate) &&
                Objects.equals(benefitPeriodStartDate, that.benefitPeriodStartDate) &&
                Objects.equals(periodCompleted, that.periodCompleted) &&
                Objects.equals(periodRemaining, that.periodRemaining) &&
                Objects.equals(eliminationPeriodStartDate, that.eliminationPeriodStartDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), eliminationPeriodEndDate, benefitPeriod, eliminationPeriod, benefitPeriodEndDate, benefitPeriodStartDate, periodCompleted, periodRemaining, eliminationPeriodStartDate);
    }
}
