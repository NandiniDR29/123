package com.exigen.ren.rest.policyBenefits.enrollmentServices.models.enrollmentCensusRecordsModels;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmountEmployeeModel extends Model {
    private String approvedAmount;
    private String approvedAmountPct;
    private String inforceAmount;
    private String inforceAmountPct;
    private String eoiRequired;

    public String getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(String approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public String getApprovedAmountPct() {
        return approvedAmountPct;
    }

    public void setApprovedAmountPct(String approvedAmountPct) {
        this.approvedAmountPct = approvedAmountPct;
    }

    public String getInforceAmount() {
        return inforceAmount;
    }

    public void setInforceAmount(String inforceAmount) {
        this.inforceAmount = inforceAmount;
    }

    public String getInforceAmountPct() {
        return inforceAmountPct;
    }

    public void setInforceAmountPct(String inforceAmountPct) {
        this.inforceAmountPct = inforceAmountPct;
    }

    public String getEoiRequired() {
        return eoiRequired;
    }

    public void setEoiRequired(String eoiRequired) {
        this.eoiRequired = eoiRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AmountEmployeeModel)) return false;
        if (!super.equals(o)) return false;
        AmountEmployeeModel that = (AmountEmployeeModel) o;
        return Objects.equals(approvedAmount, that.approvedAmount) &&
                Objects.equals(approvedAmountPct, that.approvedAmountPct) &&
                Objects.equals(inforceAmount, that.inforceAmount) &&
                Objects.equals(inforceAmountPct, that.inforceAmountPct) &&
                Objects.equals(eoiRequired, that.eoiRequired);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), approvedAmount, approvedAmountPct, inforceAmount, inforceAmountPct, eoiRequired);
    }
}