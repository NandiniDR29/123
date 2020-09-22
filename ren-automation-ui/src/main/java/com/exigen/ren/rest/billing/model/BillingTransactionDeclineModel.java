/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.billing.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;

public class BillingTransactionDeclineModel extends Model {

    private String declineReasonCd;


    public BillingTransactionDeclineModel(String declineReasonCd) {
        this.declineReasonCd = declineReasonCd;
    }

    public BillingTransactionDeclineModel() {
    }

    public String getDeclineReasonCd() {
        return declineReasonCd;
    }

    public void setDeclineReasonCd(String declineReasonCd) {
        this.declineReasonCd = declineReasonCd;
    }
}
