package com.exigen.ren.rest.dxp.model.certificate;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployerCertificatePoliciesDetailedModel extends RestError {

    private String revisionNumber;
    private String policyNumber;
    private String policyStatusCd;
    private String productVersion;
    private GroupCertificatePrimaryParticipant groupCertificatePrimaryParticipant;
    private String transactionTypeCd;
    private List<GroupCertificateParticipantGroups> groupCertificateParticipantGroups;
    private String masterPolicyNumber;
    private String enrollmentType;
    private String currentRevisionInd;
    private String cancellationDate;
    private String expirationDate;
    private String masterPolicyEffectiveDate;
    private String renewalDate;
    private String timedPolicyStatusCd;
    private String customerNumber;
    private String billingLocation;
    private String billingLocationName;
    private String anniversaryDate;
    private String transactionEffectiveDate;
    private String effectiveDate;
    private String masterPolicyCustomerNumber;
    private String planName;
    private String planCd;
    private String masterPolicyCustomerName;
    private String issueState;
    private String productCode;

    public String getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(String revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getPolicyStatusCd() {
        return policyStatusCd;
    }

    public void setPolicyStatusCd(String policyStatusCd) {
        this.policyStatusCd = policyStatusCd;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public GroupCertificatePrimaryParticipant getGroupCertificatePrimaryParticipant() {
        return groupCertificatePrimaryParticipant;
    }

    public void setGroupCertificatePrimaryParticipant(GroupCertificatePrimaryParticipant groupCertificatePrimaryParticipant) {
        this.groupCertificatePrimaryParticipant = groupCertificatePrimaryParticipant;
    }

    public String getTransactionTypeCd() {
        return transactionTypeCd;
    }

    public void setTransactionTypeCd(String transactionTypeCd) {
        this.transactionTypeCd = transactionTypeCd;
    }

    public List<GroupCertificateParticipantGroups> getGroupCertificateParticipantGroups() {
        return groupCertificateParticipantGroups;
    }

    public void setGroupCertificateParticipantGroups(List<GroupCertificateParticipantGroups> groupCertificateParticipantGroups) {
        this.groupCertificateParticipantGroups = groupCertificateParticipantGroups;
    }

    public String getMasterPolicyNumber() {
        return masterPolicyNumber;
    }

    public void setMasterPolicyNumber(String masterPolicyNumber) {
        this.masterPolicyNumber = masterPolicyNumber;
    }

    public String getEnrollmentType() {
        return enrollmentType;
    }

    public void setEnrollmentType(String enrollmentType) {
        this.enrollmentType = enrollmentType;
    }

    public String getCurrentRevisionInd() {
        return currentRevisionInd;
    }

    public void setCurrentRevisionInd(String currentRevisionInd) {
        this.currentRevisionInd = currentRevisionInd;
    }

    public String getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(String cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getMasterPolicyEffectiveDate() {
        return masterPolicyEffectiveDate;
    }

    public void setMasterPolicyEffectiveDate(String masterPolicyEffectiveDate) {
        this.masterPolicyEffectiveDate = masterPolicyEffectiveDate;
    }

    public String getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(String renewalDate) {
        this.renewalDate = renewalDate;
    }

    public String getTimedPolicyStatusCd() {
        return timedPolicyStatusCd;
    }

    public void setTimedPolicyStatusCd(String timedPolicyStatusCd) {
        this.timedPolicyStatusCd = timedPolicyStatusCd;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getBillingLocation() {
        return billingLocation;
    }

    public void setBillingLocation(String billingLocation) {
        this.billingLocation = billingLocation;
    }

    public String getBillingLocationName() {
        return billingLocationName;
    }

    public void setBillingLocationName(String billingLocationName) {
        this.billingLocationName = billingLocationName;
    }

    public String getAnniversaryDate() {
        return anniversaryDate;
    }

    public void setAnniversaryDate(String anniversaryDate) {
        this.anniversaryDate = anniversaryDate;
    }

    public String getTransactionEffectiveDate() {
        return transactionEffectiveDate;
    }

    public void setTransactionEffectiveDate(String transactionEffectiveDate) {
        this.transactionEffectiveDate = transactionEffectiveDate;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getMasterPolicyCustomerNumber() {
        return masterPolicyCustomerNumber;
    }

    public void setMasterPolicyCustomerNumber(String masterPolicyCustomerNumber) {
        this.masterPolicyCustomerNumber = masterPolicyCustomerNumber;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanCd() {
        return planCd;
    }

    public void setPlanCd(String planCd) {
        this.planCd = planCd;
    }

    public String getMasterPolicyCustomerName() {
        return masterPolicyCustomerName;
    }

    public void setMasterPolicyCustomerName(String masterPolicyCustomerName) {
        this.masterPolicyCustomerName = masterPolicyCustomerName;
    }

    public String getIssueState() {
        return issueState;
    }

    public void setIssueState(String issueState) {
        this.issueState = issueState;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        EmployerCertificatePoliciesDetailedModel that = (EmployerCertificatePoliciesDetailedModel) o;
        return Objects.equals(revisionNumber, that.revisionNumber) &&
                Objects.equals(policyNumber, that.policyNumber) &&
                Objects.equals(policyStatusCd, that.policyStatusCd) &&
                Objects.equals(productVersion, that.productVersion) &&
                Objects.equals(groupCertificatePrimaryParticipant, that.groupCertificatePrimaryParticipant) &&
                Objects.equals(transactionTypeCd, that.transactionTypeCd) &&
                Objects.equals(groupCertificateParticipantGroups, that.groupCertificateParticipantGroups) &&
                Objects.equals(masterPolicyNumber, that.masterPolicyNumber) &&
                Objects.equals(enrollmentType, that.enrollmentType) &&
                Objects.equals(currentRevisionInd, that.currentRevisionInd) &&
                Objects.equals(cancellationDate, that.cancellationDate) &&
                Objects.equals(expirationDate, that.expirationDate) &&
                Objects.equals(masterPolicyEffectiveDate, that.masterPolicyEffectiveDate) &&
                Objects.equals(renewalDate, that.renewalDate) &&
                Objects.equals(timedPolicyStatusCd, that.timedPolicyStatusCd) &&
                Objects.equals(customerNumber, that.customerNumber) &&
                Objects.equals(billingLocation, that.billingLocation) &&
                Objects.equals(billingLocationName, that.billingLocationName) &&
                Objects.equals(anniversaryDate, that.anniversaryDate) &&
                Objects.equals(transactionEffectiveDate, that.transactionEffectiveDate) &&
                Objects.equals(effectiveDate, that.effectiveDate) &&
                Objects.equals(masterPolicyCustomerNumber, that.masterPolicyCustomerNumber) &&
                Objects.equals(planName, that.planName) &&
                Objects.equals(planCd, that.planCd) &&
                Objects.equals(masterPolicyCustomerName, that.masterPolicyCustomerName) &&
                Objects.equals(issueState, that.issueState) &&
                Objects.equals(productCode, that.productCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), revisionNumber, policyNumber, policyStatusCd, productVersion, groupCertificatePrimaryParticipant, transactionTypeCd, groupCertificateParticipantGroups, masterPolicyNumber, enrollmentType, currentRevisionInd, cancellationDate, expirationDate, masterPolicyEffectiveDate, renewalDate, timedPolicyStatusCd, customerNumber, billingLocation, billingLocationName, anniversaryDate, transactionEffectiveDate, effectiveDate, masterPolicyCustomerNumber, planName, planCd, masterPolicyCustomerName, issueState, productCode);
    }
}