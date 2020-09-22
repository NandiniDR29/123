/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.providers.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;

public class AssignmentInfoModel extends Model {
    private String serviceAgentAssignmentID;
    private String serviceProviderFacilityID;
    private String serviceAgentAssignmentOID;
    private String serviceProviderFacilityOID;
    private String nonIndividualName;
    private String individualFirstName;
    private String individualLastName;
    private String nonIndividualPhone;
    private AddressDataModel facilityAddress;
    private ProviderIdentifiersModel nonIndividualInfo;
    private ProviderIdentifiersModel individualInfo;
    private String facilityName;
    private String facilityPhone;
    private String facilityExternalId;
    private String serviceFacilityOID;
    private TermModel serviceProviderFacilityEffectiveTerm;

    public String getServiceAgentAssignmentID() {
        return serviceAgentAssignmentID;
    }

    public void setServiceAgentAssignmentID(String serviceAgentAssignmentID) {
        this.serviceAgentAssignmentID = serviceAgentAssignmentID;
    }

    public String getServiceProviderFacilityID() {
        return serviceProviderFacilityID;
    }

    public void setServiceProviderFacilityID(String serviceProviderFacilityID) {
        this.serviceProviderFacilityID = serviceProviderFacilityID;
    }

    public String getServiceAgentAssignmentOID() {
        return serviceAgentAssignmentOID;
    }

    public void setServiceAgentAssignmentOID(String serviceAgentAssignmentOID) {
        this.serviceAgentAssignmentOID = serviceAgentAssignmentOID;
    }

    public String getServiceProviderFacilityOID() {
        return serviceProviderFacilityOID;
    }

    public void setServiceProviderFacilityOID(String serviceProviderFacilityOID) {
        this.serviceProviderFacilityOID = serviceProviderFacilityOID;
    }

    public String getNonIndividualName() {
        return nonIndividualName;
    }

    public void setNonIndividualName(String nonIndividualName) {
        this.nonIndividualName = nonIndividualName;
    }

    public String getIndividualFirstName() {
        return individualFirstName;
    }

    public void setIndividualFirstName(String individualFirstName) {
        this.individualFirstName = individualFirstName;
    }

    public String getIndividualLastName() {
        return individualLastName;
    }

    public void setIndividualLastName(String individualLastName) {
        this.individualLastName = individualLastName;
    }

    public String getNonIndividualPhone() {
        return nonIndividualPhone;
    }

    public void setNonIndividualPhone(String nonIndividualPhone) {
        this.nonIndividualPhone = nonIndividualPhone;
    }

    public AddressDataModel getFacilityAddress() {
        return facilityAddress;
    }

    public void setFacilityAddress(AddressDataModel facilityAddress) {
        this.facilityAddress = facilityAddress;
    }

    public ProviderIdentifiersModel getNonIndividualInfo() {
        return nonIndividualInfo;
    }

    public void setNonIndividualInfo(ProviderIdentifiersModel nonIndividualInfo) {
        this.nonIndividualInfo = nonIndividualInfo;
    }

    public ProviderIdentifiersModel getIndividualInfo() {
        return individualInfo;
    }

    public void setIndividualInfo(ProviderIdentifiersModel individualInfo) {
        this.individualInfo = individualInfo;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityPhone() {
        return facilityPhone;
    }

    public void setFacilityPhone(String facilityPhone) {
        this.facilityPhone = facilityPhone;
    }

    public String getFacilityExternalId() {
        return facilityExternalId;
    }

    public void setFacilityExternalId(String facilityExternalId) {
        this.facilityExternalId = facilityExternalId;
    }

    public String getServiceFacilityOID() {
        return serviceFacilityOID;
    }

    public void setServiceFacilityOID(String serviceFacilityOID) {
        this.serviceFacilityOID = serviceFacilityOID;
    }

    public TermModel getServiceProviderFacilityEffectiveTerm() {
        return serviceProviderFacilityEffectiveTerm;
    }

    public void setServiceProviderFacilityEffectiveTerm(TermModel serviceProviderFacilityEffectiveTerm) {
        this.serviceProviderFacilityEffectiveTerm = serviceProviderFacilityEffectiveTerm;
    }
}
