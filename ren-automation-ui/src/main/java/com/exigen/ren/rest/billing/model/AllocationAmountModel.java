/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.billing.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllocationAmountModel extends Model {

    public String amountType;
    public String code;
    public Double amount;

    public String getAmountType() { return amountType; }

    public void setAmountType(String amountType) { this.amountType = amountType; }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public Double getAmount() { return amount; }

    public void setAmount(Double amount) { this.amount = amount; }
}
