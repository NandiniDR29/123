/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer.model;

import com.exigen.istf.data.TestData;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmploymentModel extends RestError {

    private int id;
    private String employerName;
    private String occupationCd;
    private String occupationDescription;
    private String occupationStatusCd;
    private String jobTitleCd;
    private String jobTitleDescription;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate asOfDate;

    public EmploymentModel() {
    }

    public EmploymentModel(TestData data) {
        setEmployerName(data.getValue(CustomerConstants.EmploymentsTable.EMPLOYER_NAME.getName()));
        setOccupationStatusCd(data.getValue(CustomerConstants.EmploymentsTable.OCCUPATION_STATUS.getName()).toUpperCase());
        setOccupationCd(data.getValue(CustomerConstants.EmploymentsTable.OCCUPATION.getName()));
        setJobTitleCd(data.getValue(CustomerConstants.EmploymentsTable.JOB_TITLE.getName()).toUpperCase());
        setAsOfDate(LocalDate.parse(data.getValue(CustomerConstants.EmploymentsTable.AS_OF_DATE.getName()), MM_DD_YYYY));
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setOccupationCd(String occupationCd) {
        this.occupationCd = occupationCd;
    }

    public String getOccupationCd() {
        return occupationCd;
    }

    public void setOccupationDescription(String occupationDescription) {
        this.occupationDescription = occupationDescription;
    }

    public String getOccupationDescription() {
        return occupationDescription;
    }

    public void setOccupationStatusCd(String occupationStatusCd) {
        this.occupationStatusCd = occupationStatusCd;
    }

    public String getOccupationStatusCd() {
        return occupationStatusCd;
    }

    public void setJobTitleCd(String jobTitleCd) {
        this.jobTitleCd = jobTitleCd;
    }

    public String getJobTitleCd() {
        return jobTitleCd;
    }

    public void setJobTitleDescription(String jobTitleDescription) {
        this.jobTitleDescription = jobTitleDescription;
    }

    public String getJobTitleDescription() {
        return jobTitleDescription;
    }

    public void setAsOfDate(LocalDate asOfDate) {
        this.asOfDate = asOfDate;
    }

    public LocalDate getAsOfDate() {
        return asOfDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RestError)) {
            return false;
        }

        if (getErrorCode() != null) {
            return super.equals(o);
        }

        if (getClass() != o.getClass()) {
            return false;
        }

        EmploymentModel that = (EmploymentModel) o;

        if (employerName != null ? !employerName.equals(that.employerName) : that.employerName != null) {
            return false;
        }
        if (occupationCd != null ? !occupationCd.equals(that.occupationCd) : that.occupationCd != null) {
            return false;
        }
        if (occupationDescription != null ? !occupationDescription.equals(that.occupationDescription) : that.occupationDescription != null) {
            return false;
        }
        if (occupationStatusCd != null ? !occupationStatusCd.equals(that.occupationStatusCd) : that.occupationStatusCd != null) {
            return false;
        }
        if (jobTitleCd != null ? !jobTitleCd.equals(that.jobTitleCd) : that.jobTitleCd != null) {
            return false;
        }
        if (jobTitleDescription != null ? !jobTitleDescription.equals(that.jobTitleDescription) : that.jobTitleDescription != null) {
            return false;
        }
        return asOfDate != null ? asOfDate.equals(that.asOfDate) : that.asOfDate == null;
    }

    @Override
    public int hashCode() {
        int result = employerName != null ? employerName.hashCode() : 0;
        result = 31 * result + (occupationCd != null ? occupationCd.hashCode() : 0);
        result = 31 * result + (occupationDescription != null ? occupationDescription.hashCode() : 0);
        result = 31 * result + (occupationStatusCd != null ? occupationStatusCd.hashCode() : 0);
        result = 31 * result + (jobTitleCd != null ? jobTitleCd.hashCode() : 0);
        result = 31 * result + (jobTitleDescription != null ? jobTitleDescription.hashCode() : 0);
        result = 31 * result + (asOfDate != null ? asOfDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Employment{" +
                "id(Excluded from equals)='" + id + '\'' +
                ", employerName='" + employerName + '\'' +
                ", occupationCd='" + occupationCd + '\'' +
                ", occupationDescription='" + occupationDescription + '\'' +
                ", occupationStatusCd='" + occupationStatusCd + '\'' +
                ", jobTitleCd='" + jobTitleCd + '\'' +
                ", jobTitleDescription='" + jobTitleDescription + '\'' +
                ", asOfDate='" + asOfDate + '\'' +
                '}';
    }

}
