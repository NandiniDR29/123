package com.exigen.ren.rest.billing.model.billing_accounts_benefits_payments;

import java.util.List;
import java.util.Objects;

public class BillingAccountsBenefitsPaymentsPaymentDetailsTypeEFT {

    private String profileId;
    private List extensionFields;
    private BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetails payorDetails;
    private String paymentMethodEffectiveDate;
    private String paymentMethodExpirationDate;
    private String transitNumber;
    private String bankName;
    private String accountNumber;
    private String firstName;
    private String lastName;
    private String status;
    private String bankAccountHolderName;
    private String bankAccountHolderNamePhonetic;
    private String statusUpdatedBy;
    private String bankCd;
    private String bankBranchCd;
    private String accountTypeCd;
    private Boolean eftProtocolFormReceived;
    private BillingAccountsBenefitsPaymentsPaymentDetailsTypeEFTEffectiveTerm effectiveTerm;
    private BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetailsNameInfo accountHolderInfo;
    private Boolean internationalAchFormatting;
    private String bankAccountType;
    private String countryCd;
    private Boolean payorDifferent;

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public List getExtensionFields() {
        return extensionFields;
    }

    public void setExtensionFields(List extensionFields) {
        this.extensionFields = extensionFields;
    }

    public BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetails getPayorDetails() {
        return payorDetails;
    }

    public void setPayorDetails(BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetails payorDetails) {
        this.payorDetails = payorDetails;
    }

    public String getPaymentMethodEffectiveDate() {
        return paymentMethodEffectiveDate;
    }

    public void setPaymentMethodEffectiveDate(String paymentMethodEffectiveDate) {
        this.paymentMethodEffectiveDate = paymentMethodEffectiveDate;
    }

    public String getPaymentMethodExpirationDate() {
        return paymentMethodExpirationDate;
    }

    public void setPaymentMethodExpirationDate(String paymentMethodExpirationDate) {
        this.paymentMethodExpirationDate = paymentMethodExpirationDate;
    }

    public String getTransitNumber() {
        return transitNumber;
    }

    public void setTransitNumber(String transitNumber) {
        this.transitNumber = transitNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBankAccountHolderName() {
        return bankAccountHolderName;
    }

    public void setBankAccountHolderName(String bankAccountHolderName) {
        this.bankAccountHolderName = bankAccountHolderName;
    }

    public String getBankAccountHolderNamePhonetic() {
        return bankAccountHolderNamePhonetic;
    }

    public void setBankAccountHolderNamePhonetic(String bankAccountHolderNamePhonetic) {
        this.bankAccountHolderNamePhonetic = bankAccountHolderNamePhonetic;
    }

    public String getStatusUpdatedBy() {
        return statusUpdatedBy;
    }

    public void setStatusUpdatedBy(String statusUpdatedBy) {
        this.statusUpdatedBy = statusUpdatedBy;
    }

    public String getBankCd() {
        return bankCd;
    }

    public void setBankCd(String bankCd) {
        this.bankCd = bankCd;
    }

    public String getBankBranchCd() {
        return bankBranchCd;
    }

    public void setBankBranchCd(String bankBranchCd) {
        this.bankBranchCd = bankBranchCd;
    }

    public String getAccountTypeCd() {
        return accountTypeCd;
    }

    public void setAccountTypeCd(String accountTypeCd) {
        this.accountTypeCd = accountTypeCd;
    }

    public Boolean getEftProtocolFormReceived() {
        return eftProtocolFormReceived;
    }

    public void setEftProtocolFormReceived(Boolean eftProtocolFormReceived) {
        this.eftProtocolFormReceived = eftProtocolFormReceived;
    }

    public BillingAccountsBenefitsPaymentsPaymentDetailsTypeEFTEffectiveTerm getEffectiveTerm() {
        return effectiveTerm;
    }

    public void setEffectiveTerm(BillingAccountsBenefitsPaymentsPaymentDetailsTypeEFTEffectiveTerm effectiveTerm) {
        this.effectiveTerm = effectiveTerm;
    }

    public BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetailsNameInfo getAccountHolderInfo() {
        return accountHolderInfo;
    }

    public void setAccountHolderInfo(BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetailsNameInfo accountHolderInfo) {
        this.accountHolderInfo = accountHolderInfo;
    }

    public Boolean getInternationalAchFormatting() {
        return internationalAchFormatting;
    }

    public void setInternationalAchFormatting(Boolean internationalAchFormatting) {
        this.internationalAchFormatting = internationalAchFormatting;
    }

    public String getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(String bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public String getCountryCd() {
        return countryCd;
    }

    public void setCountryCd(String countryCd) {
        this.countryCd = countryCd;
    }

    public Boolean getPayorDifferent() {
        return payorDifferent;
    }

    public void setPayorDifferent(Boolean payorDifferent) {
        this.payorDifferent = payorDifferent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        BillingAccountsBenefitsPaymentsPaymentDetailsTypeEFT that = (BillingAccountsBenefitsPaymentsPaymentDetailsTypeEFT) o;
        return Objects.equals(profileId, that.profileId) &&
                Objects.equals(extensionFields, that.extensionFields) &&
                Objects.equals(payorDetails, that.payorDetails) &&
                Objects.equals(paymentMethodEffectiveDate, that.paymentMethodEffectiveDate) &&
                Objects.equals(paymentMethodExpirationDate, that.paymentMethodExpirationDate) &&
                Objects.equals(transitNumber, that.transitNumber) &&
                Objects.equals(bankName, that.bankName) &&
                Objects.equals(accountNumber, that.accountNumber) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(status, that.status) &&
                Objects.equals(bankAccountHolderName, that.bankAccountHolderName) &&
                Objects.equals(bankAccountHolderNamePhonetic, that.bankAccountHolderNamePhonetic) &&
                Objects.equals(statusUpdatedBy, that.statusUpdatedBy) &&
                Objects.equals(bankCd, that.bankCd) &&
                Objects.equals(bankBranchCd, that.bankBranchCd) &&
                Objects.equals(accountTypeCd, that.accountTypeCd) &&
                Objects.equals(eftProtocolFormReceived, that.eftProtocolFormReceived) &&
                Objects.equals(effectiveTerm, that.effectiveTerm) &&
                Objects.equals(accountHolderInfo, that.accountHolderInfo) &&
                Objects.equals(internationalAchFormatting, that.internationalAchFormatting) &&
                Objects.equals(bankAccountType, that.bankAccountType) &&
                Objects.equals(countryCd, that.countryCd) &&
                Objects.equals(payorDifferent, that.payorDifferent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileId, extensionFields, payorDetails, paymentMethodEffectiveDate, paymentMethodExpirationDate, transitNumber, bankName, accountNumber, firstName, lastName, status, bankAccountHolderName, bankAccountHolderNamePhonetic, statusUpdatedBy, bankCd, bankBranchCd, accountTypeCd, eftProtocolFormReceived, effectiveTerm, accountHolderInfo, internationalAchFormatting, bankAccountType, countryCd, payorDifferent);
    }
}
