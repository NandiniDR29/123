/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.billing.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;

/**
 * @author ikisly
 */
public class BillingPaymentModel extends Model {

    private String referenceId;
    private String subType;
    private AmountModel amount;
    private PaymentDetailsModel paymentDetails;

    public String getReferenceId() { return referenceId; }

    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getSubType() { return subType; }

    public void setSubType(String subType) { this.subType = subType; }

    public AmountModel getAmount() { return amount; }

    public void setAmount(AmountModel amount) { this.amount = amount; }

    public PaymentDetailsModel getPaymentDetails() { return paymentDetails; }

    public void setPaymentDetails(PaymentDetailsModel paymentDetails) { this.paymentDetails = paymentDetails; }
}
