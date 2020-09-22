/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.providers.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceFeeSchedulesModel extends Model {
    private String id;
    private String feeScheduleType;
    private String feeScheduleCode;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setFeeScheduleType(String feeScheduleType) {
        this.feeScheduleType = feeScheduleType;
    }

    public String getFeeScheduleType() {
        return feeScheduleType;
    }

    public void setFeeScheduleCode(String feeScheduleCode) {
        this.feeScheduleCode = feeScheduleCode;
    }

    public String getFeeScheduleCode() {
        return feeScheduleCode;
    }
}
