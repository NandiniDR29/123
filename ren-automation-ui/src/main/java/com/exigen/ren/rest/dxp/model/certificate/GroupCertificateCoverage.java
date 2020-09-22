package com.exigen.ren.rest.dxp.model.certificate;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupCertificateCoverage extends RestError {

    private GroupCertificateCoverageDetail groupCertificateCoverageDetail;
    private GroupCertificatePlanDetailAge groupCertificatePlanDetailAge;

    public GroupCertificateCoverageDetail getGroupCertificateCoverageDetail() {
        return groupCertificateCoverageDetail;
    }

    public void setGroupCertificateCoverageDetail(GroupCertificateCoverageDetail groupCertificateCoverageDetail) {
        this.groupCertificateCoverageDetail = groupCertificateCoverageDetail;
    }

    public GroupCertificatePlanDetailAge getGroupCertificatePlanDetailAge() {
        return groupCertificatePlanDetailAge;
    }

    public void setGroupCertificatePlanDetailAge(GroupCertificatePlanDetailAge groupCertificatePlanDetailAge) {
        this.groupCertificatePlanDetailAge = groupCertificatePlanDetailAge;
    }

    public static class GroupCertificateCoverageDetail {
        private String coverageSelection;
        private String benefitType;
        private String enrollmentDate;
        private String className;
        private String coverageCd;
        private String sponsorPaymentMode;
        private String subGroupName;
        private String contributionType;
        private String memberPaymentMode;
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

        public String getBenefitType() {
            return benefitType;
        }

        public void setBenefitType(String benefitType) {
            this.benefitType = benefitType;
        }

        public String getEnrollmentDate() {
            return enrollmentDate;
        }

        public void setEnrollmentDate(String enrollmentDate) {
            this.enrollmentDate = enrollmentDate;
        }

        public String getSponsorPaymentMode() {
            return sponsorPaymentMode;
        }

        public void setSponsorPaymentMode(String sponsorPaymentMode) {
            this.sponsorPaymentMode = sponsorPaymentMode;
        }

        public String getCoverageSelection() {
            return coverageSelection;
        }

        public void setCoverageSelection(String coverageSelection) {
            this.coverageSelection = coverageSelection;
        }
    }

    public static class GroupCertificatePlanDetailAge {
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