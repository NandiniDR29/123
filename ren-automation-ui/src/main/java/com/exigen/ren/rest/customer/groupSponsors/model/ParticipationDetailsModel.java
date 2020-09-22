/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer.groupSponsors.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipationDetailsModel extends Model {
    private String type;
    private String memberId;
    private String membershipId;
    private String membershipStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate membershipStartDate;

    private String employeeId;
    private String jobTitle;
    private String jobCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate originalHireDate;
    private String rehireDate;
    private String employmentStatus;
    private String employmentType;
    private String expatriate;
    private String payClass;
    private String payType;
    private String payrollFrequency;
    private Double salaryAmount;
    private String hourlyWage;
    private String unionMember;
    private String departmentId;
    private String divisionId;
    private String locationId;

    private String studentId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate studentStartDate;
    private String studentType;
    private String studentStatus;
    private String fieldOfStudy;
    private String athlete;

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentStartDate(LocalDate studentStartDate) {
        this.studentStartDate = studentStartDate;
    }

    public LocalDate getStudentStartDate() {
        return studentStartDate;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }

    public String getStudentType() {
        return studentType;
    }

    public void setStudentStatus(String studentStatus) {
        this.studentStatus = studentStatus;
    }

    public String getStudentStatus() {
        return studentStatus;
    }

    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }

    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    public void setAthlete(String athlete) {
        this.athlete = athlete;
    }

    public String getAthlete() {
        return athlete;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public String getJobCode() {
        return jobCode;
    }

    public void setOriginalHireDate(LocalDate originalHireDate) {
        this.originalHireDate = originalHireDate;
    }

    public LocalDate getOriginalHireDate() {
        return originalHireDate;
    }

    public void setRehireDate(String rehireDate) {
        this.rehireDate = rehireDate;
    }

    public String getRehireDate() {
        return rehireDate;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setExpatriate(String expatriate) {
        this.expatriate = expatriate;
    }

    public String getExpatriate() {
        return expatriate;
    }

    public void setPayClass(String payClass) {
        this.payClass = payClass;
    }

    public String getPayClass() {
        return payClass;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayrollFrequency(String payrollFrequency) {
        this.payrollFrequency = payrollFrequency;
    }

    public String getPayrollFrequency() {
        return payrollFrequency;
    }

    public void setSalaryAmount(Double salaryAmount) {
        this.salaryAmount = salaryAmount;
    }

    public Double getSalaryAmount() {
        return salaryAmount;
    }

    public void setHourlyWage(String hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    public String getHourlyWage() {
        return hourlyWage;
    }

    public void setUnionMember(String unionMember) {
        this.unionMember = unionMember;
    }

    public String getUnionMember() {
        return unionMember;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return this.memberId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getMembershipId() {
        return this.membershipId;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public String getMembershipStatus() {
        return this.membershipStatus;
    }

    public void setMembershipStartDate(LocalDate membershipStartDate) {
        this.membershipStartDate = membershipStartDate;
    }

    public LocalDate getMembershipStartDate() {
        return this.membershipStartDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParticipationDetailsModel)) {
            return false;
        }

        ParticipationDetailsModel that = (ParticipationDetailsModel) o;

        if (type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }
        if (memberId != null ? !memberId.equals(that.memberId) : that.memberId != null) {
            return false;
        }
        if (membershipId != null ? !membershipId.equals(that.membershipId) : that.membershipId != null) {
            return false;
        }
        if (membershipStatus != null ? !membershipStatus.equals(that.membershipStatus) : that.membershipStatus != null) {
            return false;
        }
        if (employeeId != null ? !employeeId.equals(that.employeeId) : that.employeeId != null) {
            return false;
        }
        if (jobTitle != null ? !jobTitle.equals(that.jobTitle) : that.jobTitle != null) {
            return false;
        }
        if (jobCode != null ? !jobCode.equals(that.jobCode) : that.jobCode != null) {
            return false;
        }
        if (employmentStatus != null ? !employmentStatus.equals(that.employmentStatus) : that.employmentStatus != null) {
            return false;
        }
        if (employmentType != null ? !employmentType.equals(that.employmentType) : that.employmentType != null) {
            return false;
        }
        if (payType != null ? !payType.equals(that.payType) : that.payType != null) {
            return false;
        }
        if (payrollFrequency != null ? !payrollFrequency.equals(that.payrollFrequency) : that.payrollFrequency != null) {
            return false;
        }
        if (salaryAmount != null ? !salaryAmount.equals(that.salaryAmount) : that.salaryAmount != null) {
            return false;
        }
        if (studentId != null ? !studentId.equals(that.studentId) : that.studentId != null) {
            return false;
        }
        if (studentType != null ? !studentType.equals(that.studentType) : that.studentType != null) {
            return false;
        }
        if (studentStatus != null ? !studentStatus.equals(that.studentStatus) : that.studentStatus != null) {
            return false;
        }
        if (fieldOfStudy != null ? !fieldOfStudy.equals(that.fieldOfStudy) : that.fieldOfStudy != null) {
            return false;
        }
        return athlete != null ? athlete.equals(that.athlete) : that.athlete == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (memberId != null ? memberId.hashCode() : 0);
        result = 31 * result + (membershipId != null ? membershipId.hashCode() : 0);
        result = 31 * result + (membershipStatus != null ? membershipStatus.hashCode() : 0);
        result = 31 * result + (employeeId != null ? employeeId.hashCode() : 0);
        result = 31 * result + (jobTitle != null ? jobTitle.hashCode() : 0);
        result = 31 * result + (jobCode != null ? jobCode.hashCode() : 0);
        result = 31 * result + (employmentStatus != null ? employmentStatus.hashCode() : 0);
        result = 31 * result + (employmentType != null ? employmentType.hashCode() : 0);
        result = 31 * result + (payType != null ? payType.hashCode() : 0);
        result = 31 * result + (payrollFrequency != null ? payrollFrequency.hashCode() : 0);
        result = 31 * result + (salaryAmount != null ? salaryAmount.hashCode() : 0);
        result = 31 * result + (studentId != null ? studentId.hashCode() : 0);
        result = 31 * result + (studentType != null ? studentType.hashCode() : 0);
        result = 31 * result + (studentStatus != null ? studentStatus.hashCode() : 0);
        result = 31 * result + (fieldOfStudy != null ? fieldOfStudy.hashCode() : 0);
        result = 31 * result + (athlete != null ? athlete.hashCode() : 0);
        return result;
    }
}
