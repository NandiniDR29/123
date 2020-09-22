package com.exigen.ren.rest.billing.model.billing_accounts_benefits_payments;

import java.util.Objects;

public class BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetailsNameInfo {

    private String nameTypeCd;
    private String prefix;
    private String firstName;
    private String middleName;
    private String lastName;
    private String otherName;

    public String getNameTypeCd() {
        return nameTypeCd;
    }

    public void setNameTypeCd(String nameTypeCd) {
        this.nameTypeCd = nameTypeCd;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
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
        BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetailsNameInfo that = (BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetailsNameInfo) o;
        return Objects.equals(nameTypeCd, that.nameTypeCd) &&
                Objects.equals(prefix, that.prefix) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(middleName, that.middleName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(otherName, that.otherName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameTypeCd, prefix, firstName, middleName, lastName, otherName);
    }
}
