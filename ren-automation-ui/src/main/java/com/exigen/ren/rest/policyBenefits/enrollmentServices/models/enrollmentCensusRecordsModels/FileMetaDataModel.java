package com.exigen.ren.rest.policyBenefits.enrollmentServices.models.enrollmentCensusRecordsModels;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileMetaDataModel extends Model {
    private String masterPolicyNumber;
    private String participantCount;
    private String timestamp;
    private String fileIdentifier;
    private String fileName;
    private String fileId;
    private String enrollmentTerm;
    private String enrollmentFileType;
    private String enrollmentSource;
    private String enrollmentDataType;
    private String fullyEnrolledInd;

    public String getMasterPolicyNumber() {
        return masterPolicyNumber;
    }

    public void setMasterPolicyNumber(String masterPolicyNumber) {
        this.masterPolicyNumber = masterPolicyNumber;
    }

    public String getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(String participantCount) {
        this.participantCount = participantCount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getFileIdentifier() {
        return fileIdentifier;
    }

    public void setFileIdentifier(String fileIdentifier) {
        this.fileIdentifier = fileIdentifier;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getEnrollmentTerm() {
        return enrollmentTerm;
    }

    public void setEnrollmentTerm(String enrollmentTerm) {
        this.enrollmentTerm = enrollmentTerm;
    }

    public String getEnrollmentFileType() {
        return enrollmentFileType;
    }

    public void setEnrollmentFileType(String enrollmentFileType) {
        this.enrollmentFileType = enrollmentFileType;
    }

    public String getEnrollmentSource() {
        return enrollmentSource;
    }

    public void setEnrollmentSource(String enrollmentSource) {
        this.enrollmentSource = enrollmentSource;
    }

    public String getEnrollmentDataType() {
        return enrollmentDataType;
    }

    public void setEnrollmentDataType(String enrollmentDataType) {
        this.enrollmentDataType = enrollmentDataType;
    }

    public String getFullyEnrolledInd() {
        return fullyEnrolledInd;
    }

    public void setFullyEnrolledInd(String fullyEnrolledInd) {
        this.fullyEnrolledInd = fullyEnrolledInd;
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
        FileMetaDataModel that = (FileMetaDataModel) o;
        return Objects.equals(masterPolicyNumber, that.masterPolicyNumber) &&
                Objects.equals(participantCount, that.participantCount) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(fileIdentifier, that.fileIdentifier) &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(fileId, that.fileId) &&
                Objects.equals(enrollmentTerm, that.enrollmentTerm) &&
                Objects.equals(enrollmentFileType, that.enrollmentFileType) &&
                Objects.equals(enrollmentSource, that.enrollmentSource) &&
                Objects.equals(enrollmentDataType, that.enrollmentDataType) &&
                Objects.equals(fullyEnrolledInd, that.fullyEnrolledInd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), masterPolicyNumber, participantCount, timestamp, fileIdentifier, fileName, fileId, enrollmentTerm, enrollmentFileType, enrollmentSource, enrollmentDataType, fullyEnrolledInd);
    }
}
