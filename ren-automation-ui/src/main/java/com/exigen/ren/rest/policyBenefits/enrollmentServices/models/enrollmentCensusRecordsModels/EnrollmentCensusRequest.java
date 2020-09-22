package com.exigen.ren.rest.policyBenefits.enrollmentServices.models.enrollmentCensusRecordsModels;

import com.exigen.ipb.eisa.ws.rest.model.Model;

import java.util.Objects;

public class EnrollmentCensusRequest extends Model {
    private FileMetaDataModel fileMetaData;
    private EnrollmentCensusRecordsModel enrollmentCensusRecords;

    public FileMetaDataModel getFileMetaData() {
        return fileMetaData;
    }

    public void setFileMetaData(FileMetaDataModel fileMetaData) {
        this.fileMetaData = fileMetaData;
    }

    public EnrollmentCensusRecordsModel getEnrollmentCensusRecords() {
        return enrollmentCensusRecords;
    }

    public void setEnrollmentCensusRecords(EnrollmentCensusRecordsModel enrollmentCensusRecords) {
        this.enrollmentCensusRecords = enrollmentCensusRecords;
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
        EnrollmentCensusRequest that = (EnrollmentCensusRequest) o;
        return Objects.equals(fileMetaData, that.fileMetaData) &&
                Objects.equals(enrollmentCensusRecords, that.enrollmentCensusRecords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileMetaData, enrollmentCensusRecords);
    }
}
