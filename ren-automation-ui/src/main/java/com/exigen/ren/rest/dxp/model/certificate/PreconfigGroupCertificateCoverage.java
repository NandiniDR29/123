package com.exigen.ren.rest.dxp.model.certificate;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreconfigGroupCertificateCoverage extends RestError {

    private PreconfigGroupCertificateCoverageDetail preconfigGroupCertificateCoverageDetail;
    private PreconfigGroupCertificatePlanDetailAge preconfigGroupCertificatePlanDetailAge;

    public PreconfigGroupCertificateCoverageDetail getPreconfigGroupCertificateCoverageDetail() {
        return preconfigGroupCertificateCoverageDetail;
    }

    public void setPreconfigGroupCertificateCoverageDetail(PreconfigGroupCertificateCoverageDetail preconfigGroupCertificateCoverageDetail) {
        this.preconfigGroupCertificateCoverageDetail = preconfigGroupCertificateCoverageDetail;
    }

    public PreconfigGroupCertificatePlanDetailAge getPreconfigGroupCertificatePlanDetailAge() {
        return preconfigGroupCertificatePlanDetailAge;
    }

    public void setPreconfigGroupCertificatePlanDetailAge(PreconfigGroupCertificatePlanDetailAge preconfigGroupCertificatePlanDetailAge) {
        this.preconfigGroupCertificatePlanDetailAge = preconfigGroupCertificatePlanDetailAge;
    }

    public static class PreconfigGroupCertificateCoverageDetail {
        private String className;
        private String subGroupName;
        private String coverageCd;
        private String contributionType;
        private String memberPaymentMode;
        private String sponsorPaymentMode;
        private String originalEffectiveDate;
        private String coverageTier;
        private String effectiveDate;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getSubGroupName() {
            return subGroupName;
        }

        public void setSubGroupName(String subGroupName) {
            this.subGroupName = subGroupName;
        }

        public String getContributionType() {
            return contributionType;
        }

        public void setContributionType(String contributionType) {
            this.contributionType = contributionType;
        }

        public String getMemberPaymentMode() {
            return memberPaymentMode;
        }

        public void setMemberPaymentMode(String memberPaymentMode) {
            this.memberPaymentMode = memberPaymentMode;
        }

        public String getCoverageTier() {
            return coverageTier;
        }

        public void setCoverageTier(String coverageTier) {
            this.coverageTier = coverageTier;
        }

        public String getEffectiveDate() {
            return effectiveDate;
        }

        public void setEffectiveDate(String effectiveDate) {
            this.effectiveDate = effectiveDate;
        }

        public String getCoverageCd() {
            return coverageCd;
        }

        public void setCoverageCd(String coverageCd) {
            this.coverageCd = coverageCd;
        }

        public String getOriginalEffectiveDate() {
            return originalEffectiveDate;
        }

        public void setOriginalEffectiveDate(String originalEffectiveDate) {
            this.originalEffectiveDate = originalEffectiveDate;
        }

        public String getSponsorPaymentMode() {
            return sponsorPaymentMode;
        }

        public void setSponsorPaymentMode(String sponsorPaymentMode) {
            this.sponsorPaymentMode = sponsorPaymentMode;
        }
    }

    public static class PreconfigGroupCertificatePlanDetailAge {
        private String disabledDependents;
        private String studentMaxAge;

        public String getDisabledDependents() {
            return disabledDependents;
        }

        public void setDisabledDependents(String disabledDependents) {
            this.disabledDependents = disabledDependents;
        }

        public String getStudentMaxAge() {
            return studentMaxAge;
        }

        public void setStudentMaxAge(String studentMaxAge) {
            this.studentMaxAge = studentMaxAge;
        }
    }
}