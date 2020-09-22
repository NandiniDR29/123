/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.billing.model.billing_accounts_invoices_regenerate;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingExtensionFields extends RestError {

    private BillingValue currentPremium;
    private BillingValue previousBalance;
    private BillingValue totalAmountDue;

    public BillingValue getCurrentPremium() {
        return currentPremium;
    }

    public void setCurrentPremium(BillingValue currentPremium) {
        this.currentPremium = currentPremium;
    }

    public BillingValue getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(BillingValue previousBalance) {
        this.previousBalance = previousBalance;
    }

    public BillingValue getTotalAmountDue() {
        return totalAmountDue;
    }

    public void setTotalAmountDue(BillingValue totalAmountDue) {
        this.totalAmountDue = totalAmountDue;
    }
}

