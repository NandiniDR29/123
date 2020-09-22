package com.exigen.ren.rest.claim.model.common.claimbody.serviceRequests;

import com.exigen.ipb.eisa.ws.rest.model.Model;

public class ProfileExtensionModel extends Model {
    private String tin;
    private String licenseNpi;
    private String licenseNumber;
    private String licenseStateCode;
    private String licenseCountryCode;
    private String addressLine1;
    private String city;
    private String countryCode;
    private String stateCode;
    private String zipCode;

    public String getLicenseNpi() {
        return licenseNpi;
    }

    public void setLicenseNpi(String licenseNpi) {
        this.licenseNpi = licenseNpi;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseStateCode() {
        return licenseStateCode;
    }

    public void setLicenseStateCode(String licenseStateCode) {
        this.licenseStateCode = licenseStateCode;
    }

    public String getLicenseCountryCode() {
        return licenseCountryCode;
    }

    public void setLicenseCountryCode(String licenseCountryCode) {
        this.licenseCountryCode = licenseCountryCode;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getSpecialtyCode() {
        return specialtyCode;
    }

    public void setSpecialtyCode(String specialtyCode) {
        this.specialtyCode = specialtyCode;
    }

    private String specialtyCode;

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

}
