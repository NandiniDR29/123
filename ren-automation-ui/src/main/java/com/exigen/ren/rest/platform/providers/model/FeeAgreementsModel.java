/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.providers.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeeAgreementsModel extends Model {

    private String serviceAgentAssignmentID;
    private List<NetworkJoinAgreementBaseCreateDTOsModel> networkJoinAgreementBaseCreateDTOModels;

    private String serviceProviderFacilityID;
    private List<NetworkJoinAgreementBaseCreateDTOsModel> serviceFeeAgreementBaseCreateDTOs;

    public void setServiceProviderFacilityID(String serviceProviderFacilityID) {
        this.serviceProviderFacilityID = serviceProviderFacilityID;
    }

    public String getServiceProviderFacilityID() {
        return serviceProviderFacilityID;
    }

    public void setServiceFeeAgreementBaseCreateDTOs(List<NetworkJoinAgreementBaseCreateDTOsModel> serviceFeeAgreementBaseCreateDTOs) {
        this.serviceFeeAgreementBaseCreateDTOs = serviceFeeAgreementBaseCreateDTOs;
    }

    public List<NetworkJoinAgreementBaseCreateDTOsModel> getServiceFeeAgreementBaseCreateDTOs() {
        return serviceFeeAgreementBaseCreateDTOs;
    }

    public void setServiceAgentAssignmentID(String serviceAgentAssignmentID) {
        this.serviceAgentAssignmentID = serviceAgentAssignmentID;
    }

    public String getServiceAgentAssignmentID() {
        return serviceAgentAssignmentID;
    }

    public void setNetworkJoinAgreementBaseCreateDTOModels(List<NetworkJoinAgreementBaseCreateDTOsModel> networkJoinAgreementBaseCreateDTOModels) {
        this.networkJoinAgreementBaseCreateDTOModels = networkJoinAgreementBaseCreateDTOModels;
    }

    public List<NetworkJoinAgreementBaseCreateDTOsModel> getNetworkJoinAgreementBaseCreateDTOModels() {
        return networkJoinAgreementBaseCreateDTOModels;
    }
}
