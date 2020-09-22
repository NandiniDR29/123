/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.billing.model.billing_payment;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllocatedSuspenses extends RestError {

    private String suspenseReferenceNumber;
    private String suspenseAmount;
    private PaymentDetailsModel paymentDetails;

    public String getSuspenseReferenceNumber() {
        return suspenseReferenceNumber;
    }

    public void setSuspenseReferenceNumber(String suspenseReferenceNumber) {
        this.suspenseReferenceNumber = suspenseReferenceNumber;
    }

    public String getSuspenseAmount() {
        return suspenseAmount;
    }

    public void setSuspenseAmount(String suspenseAmount) {
        this.suspenseAmount = suspenseAmount;
    }

    public PaymentDetailsModel getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetailsModel paymentDetails) {
        this.paymentDetails = paymentDetails;
    }
}
