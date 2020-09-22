/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.billing.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;

public class BillingRefundModel extends Model {
    private AmountModel amountModel;
    private PaymentDetailsModel paymentDetails;
    private String refundReasonCd;

    public AmountModel getAmountModel() {
        return amountModel;
    }

    public void setAmountModel(AmountModel amountModel) {
        this.amountModel = amountModel;
    }

    public PaymentDetailsModel getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetailsModel paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public String getRefundReasonCd() {
        return refundReasonCd;
    }

    public void setRefundReasonCd(String refundReasonCd) {
        this.refundReasonCd = refundReasonCd;
    }
}
