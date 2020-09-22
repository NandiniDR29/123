package com.exigen.ren.rest.billing.model.billing_accounts_benefits_payments;

import java.util.Objects;

public class BillingAccountsBenefitsPaymentsPaymentDetailsTypeBillingAddress {

    private String countryCd;
    private String stateProvCd;
    private String city;
    private String postalCode;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String county;
    private String extensionFields;

    public String getCountryCd() {
        return countryCd;
    }

    public void setCountryCd(String countryCd) {
        this.countryCd = countryCd;
    }

    public String getStateProvCd() {
        return stateProvCd;
    }

    public void setStateProvCd(String stateProvCd) {
        this.stateProvCd = stateProvCd;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getExtensionFields() {
        return extensionFields;
    }

    public void setExtensionFields(String extensionFields) {
        this.extensionFields = extensionFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        BillingAccountsBenefitsPaymentsPaymentDetailsTypeBillingAddress that = (BillingAccountsBenefitsPaymentsPaymentDetailsTypeBillingAddress) o;
        return Objects.equals(countryCd, that.countryCd) &&
                Objects.equals(stateProvCd, that.stateProvCd) &&
                Objects.equals(city, that.city) &&
                Objects.equals(postalCode, that.postalCode) &&
                Objects.equals(addressLine1, that.addressLine1) &&
                Objects.equals(addressLine2, that.addressLine2) &&
                Objects.equals(addressLine3, that.addressLine3) &&
                Objects.equals(county, that.county) &&
                Objects.equals(extensionFields, that.extensionFields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCd, stateProvCd, city, postalCode, addressLine1, addressLine2, addressLine3, county, extensionFields);
    }
}
