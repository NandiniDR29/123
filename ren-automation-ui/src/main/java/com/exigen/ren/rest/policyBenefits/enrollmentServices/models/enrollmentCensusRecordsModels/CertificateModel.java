package com.exigen.ren.rest.policyBenefits.enrollmentServices.models.enrollmentCensusRecordsModels;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CertificateModel extends Model {
    private String effectiveDt;
    private CoveragesModel coverages;
    private PrimaryParticipantModel primaryParticipant;
    private String enrollmentType;

    public String getEffectiveDt() {
        return effectiveDt;
    }

    public void setEffectiveDt(String effectiveDt) {
        this.effectiveDt = effectiveDt;
    }

    public CoveragesModel getCoverages() {
        return coverages;
    }

    public void setCoverages(CoveragesModel coverages) {
        this.coverages = coverages;
    }

    public PrimaryParticipantModel getPrimaryParticipant() {
        return primaryParticipant;
    }

    public void setPrimaryParticipant(PrimaryParticipantModel primaryParticipant) {
        this.primaryParticipant = primaryParticipant;
    }

    public String getEnrollmentType() {
        return enrollmentType;
    }

    public void setEnrollmentType(String enrollmentType) {
        this.enrollmentType = enrollmentType;
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
        CertificateModel that = (CertificateModel) o;
        return Objects.equals(effectiveDt, that.effectiveDt) &&
                Objects.equals(coverages, that.coverages) &&
                Objects.equals(primaryParticipant, that.primaryParticipant) &&
                Objects.equals(enrollmentType, that.enrollmentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), effectiveDt, coverages, primaryParticipant, enrollmentType);
    }
}
