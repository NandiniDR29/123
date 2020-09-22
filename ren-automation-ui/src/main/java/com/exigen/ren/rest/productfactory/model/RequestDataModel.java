/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.rest.productfactory.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestDataModel extends Model {
    private String endorsementReason;
    private String endorsementDate;
    @JsonProperty("policyTxInfo.daysOfNotice")
    private String policyTxInfoDaysOfNotice;
    @JsonProperty("policyTxInfo.txReasonCd")
    private String policyTxInfoTxReasonCd;
    @JsonProperty("policyTxInfo.printNotice")
    private String policyTxInfoPrintNotice;
    @JsonProperty("policyTxInfo.supportingData")
    private String policyTxInfoSupportingData;

    public String getEndorsementReason() {
        return endorsementReason;
    }

    public RequestDataModel setEndorsementReason(String endorsementReason) {
        this.endorsementReason = endorsementReason;
        return this;
    }

    public String getEndorsementDate() {
        return endorsementDate;
    }

    public RequestDataModel setEndorsementDate(String endorsementDate) {
        this.endorsementDate = endorsementDate;
        return this;
    }

    public String getPolicyTxInfoDaysOfNotice() {
        return policyTxInfoDaysOfNotice;
    }

    public RequestDataModel setPolicyTxInfoDaysOfNotice(String policyTxInfoDaysOfNotice) {
        this.policyTxInfoDaysOfNotice = policyTxInfoDaysOfNotice;
        return this;
    }

    public String getPolicyTxInfoTxReasonCd() {
        return policyTxInfoTxReasonCd;
    }

    public RequestDataModel setPolicyTxInfoTxReasonCd(String policyTxInfoTxReasonCd) {
        this.policyTxInfoTxReasonCd = policyTxInfoTxReasonCd;
        return this;
    }

    public String getPolicyTxInfoPrintNotice() {
        return policyTxInfoPrintNotice;
    }

    public RequestDataModel setPolicyTxInfoPrintNotice(String policyTxInfoPrintNotice) {
        this.policyTxInfoPrintNotice = policyTxInfoPrintNotice;
        return this;
    }

    public String getPolicyTxInfoSupportingData() {
        return policyTxInfoSupportingData;
    }

    public RequestDataModel setPolicyTxInfoSupportingData(String policyTxInfoSupportingData) {
        this.policyTxInfoSupportingData = policyTxInfoSupportingData;
        return this;
    }
}
