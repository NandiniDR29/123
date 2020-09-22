/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.providers.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceProviderSearchResultModel extends Model {
    private IndividualInfoModel individualInfo;
    private NonIndividualInfoModel nonIndividualInfo;
    private String serviceAgentAssignmentID;
    private String serviceProviderFacilityID;
    private String serviceAgentAssignmentOID;
    private String serviceProviderFacilityOID;
    private String serviceAgentAssignmentExternalID;
    private AssignmentEffectiveTermModel assignmentEffectiveTerm;
    private String individualDateOfBirth;
    private String nonIndividualName;
    private String individualFirstName;
    private String individualMiddleName;
    private String individualLastName;
    private String individualSuffix;
    private String individualGender;
    private String individualSalutation;
    private String nonIndividualPhone;
    private String individualPhone;
    private TermModel thirdPartyTerm;
    private FacilityAddressModel facilityAddress;
    private String facilityName;
    private String facilityPhone;
    private String facilityExternalId;
    private String serviceFacilityOID;
    private String serviceProviderFacilityExternalID;
    private TermModel serviceProviderFacilityEffectiveTerm;
    private int financialInfoID;
    private String financialInfoExternalID;
    private String providerFinancialInfoOID;

    public void setIndividualInfo(IndividualInfoModel individualInfo) {
        this.individualInfo = individualInfo;
    }

    public IndividualInfoModel getIndividualInfo() {
        return individualInfo;
    }

    public void setNonIndividualInfo(NonIndividualInfoModel nonIndividualInfo) {
        this.nonIndividualInfo = nonIndividualInfo;
    }

    public NonIndividualInfoModel getNonIndividualInfo() {
        return nonIndividualInfo;
    }

    public void setServiceAgentAssignmentID(String serviceAgentAssignmentID) {
        this.serviceAgentAssignmentID = serviceAgentAssignmentID;
    }

    public String getServiceAgentAssignmentID() {
        return serviceAgentAssignmentID;
    }

    public void setServiceProviderFacilityID(String serviceProviderFacilityID) {
        this.serviceProviderFacilityID = serviceProviderFacilityID;
    }

    public String getServiceProviderFacilityID() {
        return serviceProviderFacilityID;
    }

    public void setServiceAgentAssignmentOID(String serviceAgentAssignmentOID) {
        this.serviceAgentAssignmentOID = serviceAgentAssignmentOID;
    }

    public String getServiceAgentAssignmentOID() {
        return serviceAgentAssignmentOID;
    }

    public void setServiceProviderFacilityOID(String serviceProviderFacilityOID) {
        this.serviceProviderFacilityOID = serviceProviderFacilityOID;
    }

    public String getServiceProviderFacilityOID() {
        return serviceProviderFacilityOID;
    }

    public void setServiceAgentAssignmentExternalID(String serviceAgentAssignmentExternalID) {
        this.serviceAgentAssignmentExternalID = serviceAgentAssignmentExternalID;
    }

    public String getServiceAgentAssignmentExternalID() {
        return serviceAgentAssignmentExternalID;
    }

    public void setAssignmentEffectiveTerm(AssignmentEffectiveTermModel assignmentEffectiveTerm) {
        this.assignmentEffectiveTerm = assignmentEffectiveTerm;
    }

    public AssignmentEffectiveTermModel getAssignmentEffectiveTerm() {
        return assignmentEffectiveTerm;
    }

    public void setIndividualDateOfBirth(String individualDateOfBirth) {
        this.individualDateOfBirth = individualDateOfBirth;
    }

    public String getIndividualDateOfBirth() {
        return individualDateOfBirth;
    }

    public void setNonIndividualName(String nonIndividualName) {
        this.nonIndividualName = nonIndividualName;
    }

    public String getNonIndividualName() {
        return nonIndividualName;
    }

    public void setIndividualFirstName(String individualFirstName) {
        this.individualFirstName = individualFirstName;
    }

    public String getIndividualFirstName() {
        return individualFirstName;
    }

    public void setIndividualMiddleName(String individualMiddleName) {
        this.individualMiddleName = individualMiddleName;
    }

    public String getIndividualMiddleName() {
        return individualMiddleName;
    }

    public void setIndividualLastName(String individualLastName) {
        this.individualLastName = individualLastName;
    }

    public String getIndividualLastName() {
        return individualLastName;
    }

    public void setIndividualSuffix(String individualSuffix) {
        this.individualSuffix = individualSuffix;
    }

    public String getIndividualSuffix() {
        return individualSuffix;
    }

    public void setIndividualGender(String individualGender) {
        this.individualGender = individualGender;
    }

    public String getIndividualGender() {
        return individualGender;
    }

    public void setIndividualSalutation(String individualSalutation) {
        this.individualSalutation = individualSalutation;
    }

    public String getIndividualSalutation() {
        return individualSalutation;
    }

    public void setNonIndividualPhone(String nonIndividualPhone) {
        this.nonIndividualPhone = nonIndividualPhone;
    }

    public String getNonIndividualPhone() {
        return nonIndividualPhone;
    }

    public void setIndividualPhone(String individualPhone) {
        this.individualPhone = individualPhone;
    }

    public String getIndividualPhone() {
        return individualPhone;
    }

    public void setThirdPartyTerm(TermModel thirdPartyTerm) {
        this.thirdPartyTerm = thirdPartyTerm;
    }

    public TermModel getThirdPartyTerm() {
        return thirdPartyTerm;
    }

    public void setFacilityAddress(FacilityAddressModel facilityAddress) {
        this.facilityAddress = facilityAddress;
    }

    public FacilityAddressModel getFacilityAddress() {
        return facilityAddress;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityPhone(String facilityPhone) {
        this.facilityPhone = facilityPhone;
    }

    public String getFacilityPhone() {
        return facilityPhone;
    }

    public void setFacilityExternalId(String facilityExternalId) {
        this.facilityExternalId = facilityExternalId;
    }

    public String getFacilityExternalId() {
        return facilityExternalId;
    }

    public void setServiceFacilityOID(String serviceFacilityOID) {
        this.serviceFacilityOID = serviceFacilityOID;
    }

    public String getServiceFacilityOID() {
        return serviceFacilityOID;
    }

    public void setServiceProviderFacilityExternalID(String serviceProviderFacilityExternalID) {
        this.serviceProviderFacilityExternalID = serviceProviderFacilityExternalID;
    }

    public String getServiceProviderFacilityExternalID() {
        return serviceProviderFacilityExternalID;
    }

    public void setServiceProviderFacilityEffectiveTerm(TermModel serviceProviderFacilityEffectiveTerm) {
        this.serviceProviderFacilityEffectiveTerm = serviceProviderFacilityEffectiveTerm;
    }

    public TermModel getServiceProviderFacilityEffectiveTerm() {
        return serviceProviderFacilityEffectiveTerm;
    }

    public void setFinancialInfoID(int financialInfoID) {
        this.financialInfoID = financialInfoID;
    }

    public int getFinancialInfoID() {
        return financialInfoID;
    }

    public void setFinancialInfoExternalID(String financialInfoExternalID) {
        this.financialInfoExternalID = financialInfoExternalID;
    }

    public String getFinancialInfoExternalID() {
        return financialInfoExternalID;
    }

    public void setProviderFinancialInfoOID(String providerFinancialInfoOID) {
        this.providerFinancialInfoOID = providerFinancialInfoOID;
    }

    public String getProviderFinancialInfoOID() {
        return providerFinancialInfoOID;
    }
}
