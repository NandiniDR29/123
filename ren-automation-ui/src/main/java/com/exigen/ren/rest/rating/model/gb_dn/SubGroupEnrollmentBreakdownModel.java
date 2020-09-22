package com.exigen.ren.rest.rating.model.gb_dn;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubGroupEnrollmentBreakdownModel extends Model {

    private String planName;
    private String subGroup;
    List<EnrollmentBreakdownModel> enrollmentBreakdown;

    public String getPlanName() {
        return planName;
    }

    public SubGroupEnrollmentBreakdownModel setPlanName(String planName) {
        this.planName = planName;
        return this;
    }

    public String getSubGroup() {
        return subGroup;
    }

    public SubGroupEnrollmentBreakdownModel setSubGroup(String subGroup) {
        this.subGroup = subGroup;
        return this;
    }

    public List<EnrollmentBreakdownModel> getEnrollmentBreakdown() {
        return enrollmentBreakdown;
    }

    public SubGroupEnrollmentBreakdownModel setEnrollmentBreakdown(List<EnrollmentBreakdownModel> enrollmentBreakdown) {
        this.enrollmentBreakdown = enrollmentBreakdown;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubGroupEnrollmentBreakdownModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        SubGroupEnrollmentBreakdownModel that = (SubGroupEnrollmentBreakdownModel) o;
        return Objects.equals(planName, that.planName) &&
                Objects.equals(subGroup, that.subGroup) &&
                Objects.equals(enrollmentBreakdown, that.enrollmentBreakdown);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), planName, subGroup, enrollmentBreakdown);
    }

    @Override
    public String toString() {
        return "SubGroupEnrollmentBreakdownModel{" +
                "planName='" + planName + '\'' +
                ", subGroup='" + subGroup + '\'' +
                ", enrollmentBreakdown=" + enrollmentBreakdown +
                '}';
    }
}
