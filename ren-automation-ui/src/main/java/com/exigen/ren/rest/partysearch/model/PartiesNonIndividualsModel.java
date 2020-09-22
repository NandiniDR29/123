/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.partysearch.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PartiesNonIndividualsModel extends Model {

    private String agency;
    private DefaultAddressModel defaultAddress;
    private String confidentialFlag;
    private String legalId;
    private String businessName;
    private List<String> roles;
    private String partyType;
    private String customerNumber;
    private String legalName;
    private String archived;
    private Date lastUpdated;
    private Date dateStarted;
    private List<PhonesModel> phones;
    private String sicCode;
    private String confidentialReference;
    private String partyId;
    private String businessType;

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getAgency() {
        return agency;
    }

    public void setDefaultAddress(DefaultAddressModel defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public DefaultAddressModel getDefaultAddress() {
        return defaultAddress;
    }

    public void setConfidentialFlag(String confidentialFlag) {
        this.confidentialFlag = confidentialFlag;
    }

    public String getConfidentialFlag() {
        return confidentialFlag;
    }

    public void setLegalId(String legalId) {
        this.legalId = legalId;
    }

    public String getLegalId() {
        return legalId;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setPartyType(String partyType) {
        this.partyType = partyType;
    }

    public String getPartyType() {
        return partyType;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setArchived(String archived) {
        this.archived = archived;
    }

    public String getArchived() {
        return archived;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setPhones(List<PhonesModel> phones) {
        this.phones = phones;
    }

    public List<PhonesModel> getPhones() {
        return phones;
    }

    public void setSicCode(String sicCode) {
        this.sicCode = sicCode;
    }

    public String getSicCode() {
        return sicCode;
    }

    public void setConfidentialReference(String confidentialReference) {
        this.confidentialReference = confidentialReference;
    }

    public String getConfidentialReference() {
        return confidentialReference;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessType() {
        return businessType;
    }
}
