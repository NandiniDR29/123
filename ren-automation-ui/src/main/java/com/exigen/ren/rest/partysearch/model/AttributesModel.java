/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.partysearch.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributesModel extends Model {

    private String model;
    private String make;
    private String year;
    private String vin;
    private String confidentialFlag;

    private String street1;
    private String city;
    private String state;
    private String zip;
    private String country;
    private String addressTypeCd;
    private String doNotSolicit;
    private String confidentialReference;

    private String firstName;
    private String lastName;
    private String nationalId;
    private String deceased;
    private String gender;
    private String birthDate;
    private String salutation;
    private String maritalStatus;
    private String suffix;
    private String customerNumber;
    private String archived;
    private String agency;
    private String phone;
    private String email;

    private String businessType;
    private String legalName;
    private String legalId;
    private String dateStarted;
    private String sicCode;

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
    public String getBusinessType() {
        return businessType;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }
    public String getLegalName() {
        return legalName;
    }

    public void setLegalId(String legalId) {
        this.legalId = legalId;
    }
    public String getLegalId() {
        return legalId;
    }

    public void setDateStarted(String dateStarted) {
        this.dateStarted = dateStarted;
    }
    public String getDateStarted() {
        return dateStarted;
    }

    public void setSicCode(String sicCode) {
        this.sicCode = sicCode;
    }
    public String getSicCode() {
        return sicCode;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getLastName() {
        return lastName;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }
    public String getNationalId() {
        return nationalId;
    }

    public void setDeceased(String deceased) {
        this.deceased = deceased;
    }
    public String getDeceased() {
        return deceased;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getGender() {
        return gender;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
    public String getBirthDate() {
        return birthDate;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }
    public String getSalutation() {
        return salutation;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    public String getSuffix() {
        return suffix;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }
    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setArchived(String archived) {
        this.archived = archived;
    }
    public String getArchived() {
        return archived;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }
    public String getAgency() {
        return agency;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPhone() {
        return phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }
    public String getStreet1() {
        return street1;
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

    public void setZip(String zip) {
        this.zip = zip;
    }
    public String getZip() {
        return zip;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public String getCountry() {
        return country;
    }

    public void setAddressTypeCd(String addressTypeCd) {
        this.addressTypeCd = addressTypeCd;
    }
    public String getAddressTypeCd() {
        return addressTypeCd;
    }

    public void setDoNotSolicit(String doNotSolicit) {
        this.doNotSolicit = doNotSolicit;
    }
    public String getDoNotSolicit() {
        return doNotSolicit;
    }

    public void setConfidentialReference(String confidentialReference) {
        this.confidentialReference = confidentialReference;
    }
    public String getConfidentialReference() {
        return confidentialReference;
    }

    public void setModel(String model) {
        this.model = model;
    }
    public String getModel() {
        return model;
    }

    public void setMake(String make) {
        this.make = make;
    }
    public String getMake() {
        return make;
    }

    public void setYear(String year) {
        this.year = year;
    }
    public String getYear() {
        return year;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }
    public String getVin() {
        return vin;
    }

    public void setConfidentialFlag(String confidentialFlag) {
        this.confidentialFlag = confidentialFlag;
    }
    public String getConfidentialFlag() {
        return confidentialFlag;
    }

}