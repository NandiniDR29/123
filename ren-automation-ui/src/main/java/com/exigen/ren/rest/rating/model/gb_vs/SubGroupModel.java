package com.exigen.ren.rest.rating.model.gb_vs;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubGroupModel extends Model {

    private String subGroupName;
    private List<EnrollmentBreakdownModel> enrollmentBreakdown;

    public String getSubGroupName() {
        return subGroupName;
    }

    public SubGroupModel setSubGroupName(String subGroupName) {
        this.subGroupName = subGroupName;
        return this;
    }

    public List<EnrollmentBreakdownModel> getEnrollmentBreakdown() {
        return enrollmentBreakdown;
    }

    public SubGroupModel setEnrollmentBreakdown(List<EnrollmentBreakdownModel> enrollmentBreakdown) {
        this.enrollmentBreakdown = enrollmentBreakdown;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubGroupModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        SubGroupModel that = (SubGroupModel) o;
        return Objects.equals(subGroupName, that.subGroupName) &&
                Objects.equals(enrollmentBreakdown, that.enrollmentBreakdown);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subGroupName, enrollmentBreakdown);
    }

    @Override
    public String toString() {
        return "SubGroupModel{" +
                "subGroupName='" + subGroupName + '\'' +
                ", enrollmentBreakdown=" + enrollmentBreakdown +
                '}';
    }
}
