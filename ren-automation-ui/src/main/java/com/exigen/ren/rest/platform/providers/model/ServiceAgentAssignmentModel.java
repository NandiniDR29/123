/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.providers.model;

import com.exigen.ren.rest.model.RestError;

public class ServiceAgentAssignmentModel extends RestError {
    private String assignmentType;
    private String externalId;
    private String effective;
    private String expiration;
    private ProviderIdentifiersModel nonIndividualProviderIdentifiers;
    private ProviderIdentifiersModel individualProviderIdentifiers;
    private String facilityID;
    private String facilityExternalId;
    private String vendorFinancialInfoId;
    private String serviceFacilityOID;
    private String providerFinancialInfoOID;

    public ProviderIdentifiersModel getIndividualProviderIdentifiers() {
        return individualProviderIdentifiers;
    }

    public ProviderIdentifiersModel getNonIndividualProviderIdentifiers() {
        return nonIndividualProviderIdentifiers;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public String getEffective() {
        return effective;
    }

    public String getExpiration() {
        return expiration;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getFacilityID() {
        return facilityID;
    }

    public String getFacilityExternalId() {
        return facilityExternalId;
    }

    public String getVendorFinancialInfoId() {
        return vendorFinancialInfoId;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
    }

    public void setEffective(String effective) {
        this.effective = effective;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public void setFacilityExternalId(String facilityExternalId) {
        this.facilityExternalId = facilityExternalId;
    }

    public void setFacilityID(String facilityID) {
        this.facilityID = facilityID;
    }

    public void setIndividualProviderIdentifiers(ProviderIdentifiersModel individualProviderIdentifiers) {
        this.individualProviderIdentifiers = individualProviderIdentifiers;
    }

    public void setNonIndividualProviderIdentifiers(ProviderIdentifiersModel nonIndividualProviderIdentifiers) {
        this.nonIndividualProviderIdentifiers = nonIndividualProviderIdentifiers;
    }

    public void setVendorFinancialInfoId(String vendorFinancialInfoId) {
        this.vendorFinancialInfoId = vendorFinancialInfoId;
    }

    public String getServiceFacilityOID() {
        return serviceFacilityOID;
    }

    public void setServiceFacilityOID(String serviceFacilityOID) {
        this.serviceFacilityOID = serviceFacilityOID;
    }

    public String getProviderFinancialInfoOID() {
        return providerFinancialInfoOID;
    }

    public void setProviderFinancialInfoOID(String providerFinancialInfoOID) {
        this.providerFinancialInfoOID = providerFinancialInfoOID;
    }
}
