/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.providers.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndividualInfoModel extends Model {
    private long id;
    private String oid;
    private String county;
    private String externalID;
    private String npi;
    private String taxID;
    private String externalSourceType;
    private String vendorTypeCd;
    private String taxIdType;
    private String taxCorporationName;
    private String alias;
    private String billingType;
    private String name;
    private String addressLine1;
    private String status;
    private String addressLine2;
    private String addressLine3;
    private String unitNumber;
    private String city;
    private String state;
    private String zipCd;
    private String country;
    private String phoneNumber;
    private String irsIndicator;
    private String effective;
    private String expiration;
    private List<String> selectedServiceSubTypes;
    private String licenseDTO;
    private String fraudInd;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOid() {
        return oid;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCounty() {
        return county;
    }

    public void setExternalID(String externalID) {
        this.externalID = externalID;
    }

    public String getExternalID() {
        return externalID;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    public String getNpi() {
        return npi;
    }

    public void setTaxID(String taxID) {
        this.taxID = taxID;
    }

    public String getTaxID() {
        return taxID;
    }

    public void setExternalSourceType(String externalSourceType) {
        this.externalSourceType = externalSourceType;
    }

    public String getExternalSourceType() {
        return externalSourceType;
    }

    public void setVendorTypeCd(String vendorTypeCd) {
        this.vendorTypeCd = vendorTypeCd;
    }

    public String getVendorTypeCd() {
        return vendorTypeCd;
    }

    public void setTaxIdType(String taxIdType) {
        this.taxIdType = taxIdType;
    }

    public String getTaxIdType() {
        return taxIdType;
    }

    public void setTaxCorporationName(String taxCorporationName) {
        this.taxCorporationName = taxCorporationName;
    }

    public String getTaxCorporationName() {
        return taxCorporationName;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setBillingType(String billingType) {
        this.billingType = billingType;
    }

    public String getBillingType() {
        return billingType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setZipCd(String zipCd) {
        this.zipCd = zipCd;
    }

    public String getZipCd() {
        return zipCd;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setIrsIndicator(String irsIndicator) {
        this.irsIndicator = irsIndicator;
    }

    public String getIrsIndicator() {
        return irsIndicator;
    }

    public void setEffective(String effective) {
        this.effective = effective;
    }

    public String getEffective() {
        return effective;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setSelectedServiceSubTypes(List<String> selectedServiceSubTypes) {
        this.selectedServiceSubTypes = selectedServiceSubTypes;
    }

    public List<String> getSelectedServiceSubTypes() {
        return selectedServiceSubTypes;
    }

    public void setLicenseDTO(String licenseDTO) {
        this.licenseDTO = licenseDTO;
    }

    public String getLicenseDTO() {
        return licenseDTO;
    }

    public void setFraudInd(String fraudInd) {
        this.fraudInd = fraudInd;
    }

    public String getFraudInd() {
        return fraudInd;
    }
}
