/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.billing.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;

import java.util.List;

public class BillingPaymentCustomAllocationModel extends Model {

    private List<AllocationModel> allocations;
    private PaymentDetailsModel paymentDetails;
    private String referenceId;
    private String subType;
    private String refundReasonCd;
    private String refundReasonDescription;
    private String transactionNumber;

    public String getRefundReasonCd() {
        return refundReasonCd;
    }

    public void setRefundReasonCd(String refundReasonCd) {
        this.refundReasonCd = refundReasonCd;
    }

    public String getRefundReasonDescription() {
        return refundReasonDescription;
    }

    public void setRefundReasonDescription(String refundReasonDescription) {
        this.refundReasonDescription = refundReasonDescription;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public List<AllocationModel> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<AllocationModel> allocations) {
        this.allocations = allocations;
    }

    public PaymentDetailsModel getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetailsModel paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }
}
