package com.exigen.ren.rest.policyBenefits.enrollmentServices.models;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollmentCensusResponseModel extends RestError {
    private String policyNumber;
    private String participantCount;
    private String date;
    private List<EnrollmentCensusRecordModel> records;
    private List<EnrollmentStatusMessageModel> statusMessage;

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(String participantCount) {
        this.participantCount = participantCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<EnrollmentCensusRecordModel> getRecords() {
        return records;
    }

    public void setRecords(List<EnrollmentCensusRecordModel> records) {
        this.records = records;
    }

    public List<EnrollmentStatusMessageModel> getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(List<EnrollmentStatusMessageModel> statusMessage) {
        this.statusMessage = statusMessage;
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
        EnrollmentCensusResponseModel that = (EnrollmentCensusResponseModel) o;
        return Objects.equals(policyNumber, that.policyNumber) &&
                Objects.equals(participantCount, that.participantCount) &&
                Objects.equals(date, that.date) &&
                Objects.equals(records, that.records) &&
                Objects.equals(statusMessage, that.statusMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), policyNumber, participantCount, date, records, statusMessage);
    }
}
