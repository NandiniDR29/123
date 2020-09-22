/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.providers.model;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutOfNetWorkFeeRatesInfoModel extends RestError {
    private ServiceFeeScheduleModel feeScheduleVersionDTO;
    private List<ServiceFeeScheduleDataModel> serviceFeeRates;
    private TermModel agreementTerm;

    public void setFeeScheduleVersionDTO(ServiceFeeScheduleModel feeScheduleVersionDTO) {
        this.feeScheduleVersionDTO = feeScheduleVersionDTO;
    }

    public ServiceFeeScheduleModel getFeeScheduleVersionDTO() {
        return feeScheduleVersionDTO;
    }

    public void setServiceFeeRates(List<ServiceFeeScheduleDataModel> serviceFeeRates) {
        this.serviceFeeRates = serviceFeeRates;
    }

    public List<ServiceFeeScheduleDataModel> getServiceFeeRates() {
        return serviceFeeRates;
    }

    public void setAgreementTerm(TermModel agreementTerm) {
        this.agreementTerm = agreementTerm;
    }

    public TermModel getAgreementTerm() {
        return agreementTerm;
    }
}
