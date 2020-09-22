package com.exigen.ren.rest.billing.model.billing_accounts_benefits_payments;

import java.util.Objects;

public class BillingAccountsBenefitsPaymentsPaymentDetailsTypeAch {

    private String transitNumber;
    private String bankName;
    private String accountNumber;
    private String nameTypeCd;
    private String firstName;
    private String lastName;
    private String otherName;

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

    public String getNameTypeCd() {
        return nameTypeCd;
    }

    public void setNameTypeCd(String nameTypeCd) {
        this.nameTypeCd = nameTypeCd;
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

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        BillingAccountsBenefitsPaymentsPaymentDetailsTypeAch that = (BillingAccountsBenefitsPaymentsPaymentDetailsTypeAch) o;
        return Objects.equals(transitNumber, that.transitNumber) &&
                Objects.equals(bankName, that.bankName) &&
                Objects.equals(accountNumber, that.accountNumber) &&
                Objects.equals(nameTypeCd, that.nameTypeCd) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(otherName, that.otherName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transitNumber, bankName, accountNumber, nameTypeCd, firstName, lastName, otherName);
    }
}
