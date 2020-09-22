package com.exigen.ren.rest.policyBenefits.enrollmentServices.models.enrollmentCensusRecordsModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoverageTierCdModel {
    private String tiersType;
    private AmountEmployeeModel amountEmployee;

    public String getTiersType() {
        return tiersType;
    }

    public void setTiersType(String tiersType) {
        this.tiersType = tiersType;
    }

    public AmountEmployeeModel getAmountEmployee() {
        return amountEmployee;
    }

    public void setAmountEmployee(AmountEmployeeModel amountEmployee) {
        this.amountEmployee = amountEmployee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CoverageTierCdModel that = (CoverageTierCdModel) o;
        return Objects.equals(tiersType, that.tiersType) &&
                Objects.equals(amountEmployee, that.amountEmployee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tiersType, amountEmployee);
    }
}
