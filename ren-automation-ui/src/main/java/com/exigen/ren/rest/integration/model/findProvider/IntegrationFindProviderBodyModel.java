/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.integration.model.findProvider;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntegrationFindProviderBodyModel extends RestError {

    private String businessName;
    private String callerIdentifier;
    private String claimReceiptDate;
    private String dateOfService;
    private String inputSource;
    private String licenseCountryCode;
    private String licenseNpi;
    private String licenseNumber;
    private String licenseStateCode;
    private String providerFirstName;
    private String providerLastName;
    private String providerMiddleName;
    private ServiceOfficeAddressModel serviceOfficeAddress;
    private String serviceOfficeNpi;
    private String specialtyCode;
    private String taxNumber;


    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getInputSource() {
        return inputSource;
    }

    public void setInputSource(String inputSource) {
        this.inputSource = inputSource;
    }

    public String getClaimReceiptDate() {
        return claimReceiptDate;
    }

    public void setClaimReceiptDate(String claimReceiptDate) {
        this.claimReceiptDate = claimReceiptDate;
    }

    public String getDateOfService() {
        return dateOfService;
    }

    public void setDateOfService(String dateOfService) {
        this.dateOfService = dateOfService;
    }

    public String getLicenseCountryCode() {
        return licenseCountryCode;
    }

    public void setLicenseCountryCode(String licenseCountryCode) {
        this.licenseCountryCode = licenseCountryCode;
    }

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

    public String getProviderFirstName() {
        return providerFirstName;
    }

    public void setProviderFirstName(String providerFirstName) {
        this.providerFirstName = providerFirstName;
    }

    public String getProviderLastName() {
        return providerLastName;
    }

    public void setProviderLastName(String providerLastName) {
        this.providerLastName = providerLastName;
    }

    public String getProviderMiddleName() {
        return providerMiddleName;
    }

    public void setProviderMiddleName(String providerMiddleName) {
        this.providerMiddleName = providerMiddleName;
    }

    public ServiceOfficeAddressModel getServiceOfficeAddress() {
        return serviceOfficeAddress;
    }

    public void setServiceOfficeAddress(ServiceOfficeAddressModel serviceOfficeAddress) {
        this.serviceOfficeAddress = serviceOfficeAddress;
    }

    public String getServiceOfficeNpi() {
        return serviceOfficeNpi;
    }

    public void setServiceOfficeNpi(String serviceOfficeNpi) {
        this.serviceOfficeNpi = serviceOfficeNpi;
    }

    public String getSpecialtyCode() {
        return specialtyCode;
    }

    public void setSpecialtyCode(String specialtyCode) {
        this.specialtyCode = specialtyCode;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getCallerIdentifier() {
        return callerIdentifier;
    }

    public void setCallerIdentifier(String callerIdentifier) {
        this.callerIdentifier = callerIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        IntegrationFindProviderBodyModel that = (IntegrationFindProviderBodyModel) o;
        return Objects.equals(businessName, that.businessName) &&
                Objects.equals(claimReceiptDate, that.claimReceiptDate) &&
                Objects.equals(dateOfService, that.dateOfService) &&
                Objects.equals(licenseCountryCode, that.licenseCountryCode) &&
                Objects.equals(licenseNpi, that.licenseNpi) &&
                Objects.equals(licenseNumber, that.licenseNumber) &&
                Objects.equals(licenseStateCode, that.licenseStateCode) &&
                Objects.equals(providerFirstName, that.providerFirstName) &&
                Objects.equals(providerLastName, that.providerLastName) &&
                Objects.equals(providerMiddleName, that.providerMiddleName) &&
                Objects.equals(serviceOfficeAddress, that.serviceOfficeAddress) &&
                Objects.equals(serviceOfficeNpi, that.serviceOfficeNpi) &&
                Objects.equals(specialtyCode, that.specialtyCode) &&
                Objects.equals(taxNumber, that.taxNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), businessName, claimReceiptDate, dateOfService, licenseCountryCode, licenseNpi, licenseNumber, licenseStateCode, providerFirstName, providerLastName, providerMiddleName, serviceOfficeAddress, serviceOfficeNpi, specialtyCode, taxNumber);
    }
}

