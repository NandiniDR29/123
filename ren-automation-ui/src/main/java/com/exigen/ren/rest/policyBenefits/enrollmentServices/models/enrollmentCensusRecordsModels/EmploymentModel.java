package com.exigen.ren.rest.policyBenefits.enrollmentServices.models.enrollmentCensusRecordsModels;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmploymentModel extends Model {
    private String departmentId;
    private String divisionId;
    private String employeeId;
    private String employmentStatus;
    private String employmentType;
    private String expatriate;
    private String hourlyWage;
    private String jobCode;
    private String jobTitle;
    private String originalHireDt;
    private String payClass;
    private String payType;
    private String payrollFreq;
    private String salaryAmt;
    private String unionMember;
    private String locationId;

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getExpatriate() {
        return expatriate;
    }

    public void setExpatriate(String expatriate) {
        this.expatriate = expatriate;
    }

    public String getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(String hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getOriginalHireDt() {
        return originalHireDt;
    }

    public void setOriginalHireDt(String originalHireDt) {
        this.originalHireDt = originalHireDt;
    }

    public String getPayClass() {
        return payClass;
    }

    public void setPayClass(String payClass) {
        this.payClass = payClass;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayrollFreq() {
        return payrollFreq;
    }

    public void setPayrollFreq(String payrollFreq) {
        this.payrollFreq = payrollFreq;
    }

    public String getSalaryAmt() {
        return salaryAmt;
    }

    public void setSalaryAmt(String salaryAmt) {
        this.salaryAmt = salaryAmt;
    }

    public String getUnionMember() {
        return unionMember;
    }

    public void setUnionMember(String unionMember) {
        this.unionMember = unionMember;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
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
        EmploymentModel that = (EmploymentModel) o;
        return Objects.equals(departmentId, that.departmentId) &&
                Objects.equals(divisionId, that.divisionId) &&
                Objects.equals(employeeId, that.employeeId) &&
                Objects.equals(employmentStatus, that.employmentStatus) &&
                Objects.equals(employmentType, that.employmentType) &&
                Objects.equals(expatriate, that.expatriate) &&
                Objects.equals(hourlyWage, that.hourlyWage) &&
                Objects.equals(jobCode, that.jobCode) &&
                Objects.equals(jobTitle, that.jobTitle) &&
                Objects.equals(originalHireDt, that.originalHireDt) &&
                Objects.equals(payClass, that.payClass) &&
                Objects.equals(payType, that.payType) &&
                Objects.equals(payrollFreq, that.payrollFreq) &&
                Objects.equals(salaryAmt, that.salaryAmt) &&
                Objects.equals(unionMember, that.unionMember) &&
                Objects.equals(locationId, that.locationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), departmentId, divisionId, employeeId, employmentStatus, employmentType, expatriate, hourlyWage, jobCode, jobTitle, originalHireDt, payClass, payType, payrollFreq, salaryAmt, unionMember, locationId);
    }
}
