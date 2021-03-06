/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer.opportunity.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OwnerModel extends Model {
    private String displayValue;
    private String loginName;
    private String queueName;
    private String type;
    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
    public String getDisplayValue() {
        return displayValue;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
    public String getLoginName() {
        return loginName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
    public String getQueueName() {
        return queueName;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

}