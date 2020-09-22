package com.exigen.ren.rest.dxp.model;

import com.exigen.ren.rest.dxp.model.certificate.GroupCertificatePrimaryParticipant;
import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CertificatesModel extends RestError {

    private String policyNumber;
    private String transactionEffectiveDate;
    private Boolean currentRevisionInd;
    private String revisionNumber;
    private String policyStatusCd;
    private String timedPolicyStatusCd;
    private String transactionTypeCd;
    private String effectiveDate;
    private String expirationDate;
    private String anniversaryDate;
    private String renewalDate;
    private String cancellationDate;
    private String productCode;
    private String productVersion;
    private String masterPolicyNumber;
    private String masterPolicyCustomerNumber;
    private String masterPolicyCustomerName;
    private String masterPolicyEffectiveDate;
    private String issueState;
    private String planCd;
    private String planName;
    private String customerNumber;
    private GroupCertificatePrimaryParticipant groupCertificatePrimaryParticipant;

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getTransactionEffectiveDate() {
        return transactionEffectiveDate;
    }

    public void setTransactionEffectiveDate(String transactionEffectiveDate) {
        this.transactionEffectiveDate = transactionEffectiveDate;
    }

    public Boolean getCurrentRevisionInd() {
        return currentRevisionInd;
    }

    public void setCurrentRevisionInd(Boolean currentRevisionInd) {
        this.currentRevisionInd = currentRevisionInd;
    }

    public String getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(String revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public String getPolicyStatusCd() {
        return policyStatusCd;
    }

    public void setPolicyStatusCd(String policyStatusCd) {
        this.policyStatusCd = policyStatusCd;
    }

    public String getTimedPolicyStatusCd() {
        return timedPolicyStatusCd;
    }

    public void setTimedPolicyStatusCd(String timedPolicyStatusCd) {
        this.timedPolicyStatusCd = timedPolicyStatusCd;
    }

    public String getTransactionTypeCd() {
        return transactionTypeCd;
    }

    public void setTransactionTypeCd(String transactionTypeCd) {
        this.transactionTypeCd = transactionTypeCd;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getAnniversaryDate() {
        return anniversaryDate;
    }

    public void setAnniversaryDate(String anniversaryDate) {
        this.anniversaryDate = anniversaryDate;
    }

    public String getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(String renewalDate) {
        this.renewalDate = renewalDate;
    }

    public String getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(String cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public String getMasterPolicyNumber() {
        return masterPolicyNumber;
    }

    public void setMasterPolicyNumber(String masterPolicyNumber) {
        this.masterPolicyNumber = masterPolicyNumber;
    }

    public String getMasterPolicyCustomerNumber() {
        return masterPolicyCustomerNumber;
    }

    public void setMasterPolicyCustomerNumber(String masterPolicyCustomerNumber) {
        this.masterPolicyCustomerNumber = masterPolicyCustomerNumber;
    }

    public String getMasterPolicyCustomerName() {
        return masterPolicyCustomerName;
    }

    public void setMasterPolicyCustomerName(String masterPolicyCustomerName) {
        this.masterPolicyCustomerName = masterPolicyCustomerName;
    }

    public String getMasterPolicyEffectiveDate() {
        return masterPolicyEffectiveDate;
    }

    public void setMasterPolicyEffectiveDate(String masterPolicyEffectiveDate) {
        this.masterPolicyEffectiveDate = masterPolicyEffectiveDate;
    }

    public String getIssueState() {
        return issueState;
    }

    public void setIssueState(String issueState) {
        this.issueState = issueState;
    }

    public String getPlanCd() {
        return planCd;
    }

    public void setPlanCd(String planCd) {
        this.planCd = planCd;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public GroupCertificatePrimaryParticipant getGroupCertificatePrimaryParticipant() {
        return groupCertificatePrimaryParticipant;
    }

    public void setGroupCertificatePrimaryParticipant(GroupCertificatePrimaryParticipant groupCertificatePrimaryParticipant) {
        this.groupCertificatePrimaryParticipant = groupCertificatePrimaryParticipant;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        CertificatesModel that = (CertificatesModel) o;
        return Objects.equals(policyNumber, that.policyNumber) &&
                Objects.equals(transactionEffectiveDate, that.transactionEffectiveDate) &&
                Objects.equals(currentRevisionInd, that.currentRevisionInd) &&
                Objects.equals(revisionNumber, that.revisionNumber) &&
                Objects.equals(policyStatusCd, that.policyStatusCd) &&
                Objects.equals(timedPolicyStatusCd, that.timedPolicyStatusCd) &&
                Objects.equals(transactionTypeCd, that.transactionTypeCd) &&
                Objects.equals(effectiveDate, that.effectiveDate) &&
                Objects.equals(expirationDate, that.expirationDate) &&
                Objects.equals(anniversaryDate, that.anniversaryDate) &&
                Objects.equals(renewalDate, that.renewalDate) &&
                Objects.equals(cancellationDate, that.cancellationDate) &&
                Objects.equals(productCode, that.productCode) &&
                Objects.equals(productVersion, that.productVersion) &&
                Objects.equals(masterPolicyNumber, that.masterPolicyNumber) &&
                Objects.equals(masterPolicyCustomerNumber, that.masterPolicyCustomerNumber) &&
                Objects.equals(masterPolicyCustomerName, that.masterPolicyCustomerName) &&
                Objects.equals(masterPolicyEffectiveDate, that.masterPolicyEffectiveDate) &&
                Objects.equals(issueState, that.issueState) &&
                Objects.equals(planCd, that.planCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), policyNumber, transactionEffectiveDate, currentRevisionInd, revisionNumber, policyStatusCd, timedPolicyStatusCd, transactionTypeCd, effectiveDate, expirationDate, anniversaryDate, renewalDate, cancellationDate, productCode, productVersion, masterPolicyNumber, masterPolicyCustomerNumber, masterPolicyCustomerName, masterPolicyEffectiveDate, issueState, planCd);
    }
}
