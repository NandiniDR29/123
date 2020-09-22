package com.exigen.ren.rest.billing.model.billing_customers_payment_methods;

public class BillingSavedPaymentMethod {

    private String id;
    private String description;
    private String type;
    private String profileId;
    private String accountNumber;
    private String issuedBy;
    private String expirationDate;
    private String bankName;
    private String number;
    private String creditCardType;
    private String paymentMethodEffectiveDate;
    private String paymentMethodExpirationDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCreditCardType() {
        return creditCardType;
    }

    public void setCreditCardType(String creditCardType) {
        this.creditCardType = creditCardType;
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
}
