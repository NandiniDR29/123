package com.exigen.ren.rest.rating.model.gb_vs;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlanModel extends Model {

    private String redistributionType;
    List<UwManualAdjustmentsModel> uwManualAdjustments;
    private String planName;
    List<CurrentRatesBreakdownModel> currentRatesBreakdown;

    public String getRedistributionType() {
        return redistributionType;
    }

    public PlanModel setRedistributionType(String redistributionType) {
        this.redistributionType = redistributionType;
        return this;
    }

    public List<UwManualAdjustmentsModel> getUwManualAdjustments() {
        return uwManualAdjustments;
    }

    public PlanModel setUwManualAdjustments(List<UwManualAdjustmentsModel> uwManualAdjustments) {
        this.uwManualAdjustments = uwManualAdjustments;
        return this;
    }

    public String getPlanName() {
        return planName;
    }

    public PlanModel setPlanName(String planName) {
        this.planName = planName;
        return this;
    }

    public List<CurrentRatesBreakdownModel> getCurrentRatesBreakdown() {
        return currentRatesBreakdown;
    }

    public PlanModel setCurrentRatesBreakdown(List<CurrentRatesBreakdownModel> currentRatesBreakdown) {
        this.currentRatesBreakdown = currentRatesBreakdown;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlanModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PlanModel planModel = (PlanModel) o;
        return Objects.equals(redistributionType, planModel.redistributionType) &&
                Objects.equals(uwManualAdjustments, planModel.uwManualAdjustments) &&
                Objects.equals(planName, planModel.planName) &&
                Objects.equals(currentRatesBreakdown, planModel.currentRatesBreakdown);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), redistributionType, uwManualAdjustments, planName, currentRatesBreakdown);
    }

    @Override
    public String toString() {
        return "PlanModel{" +
                "redistributionType='" + redistributionType + '\'' +
                ", uwManualAdjustments=" + uwManualAdjustments +
                ", planName='" + planName + '\'' +
                ", currentRatesBreakdown=" + currentRatesBreakdown +
                '}';
    }
}
