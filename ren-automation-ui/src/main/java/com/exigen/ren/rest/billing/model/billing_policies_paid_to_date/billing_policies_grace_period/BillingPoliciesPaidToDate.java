/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.billing.model.billing_policies_paid_to_date.billing_policies_grace_period;

import com.exigen.ren.rest.model.RestError;

import java.util.Objects;

public class BillingPoliciesPaidToDate extends RestError {

    private String paidToDate;

    public String getPaidToDate() {
        return paidToDate;
    }

    public void setPaidToDate(String paidToDate) {
        this.paidToDate = paidToDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){ return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        BillingPoliciesPaidToDate that = (BillingPoliciesPaidToDate) o;
        return Objects.equals(paidToDate, that.paidToDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), paidToDate);
    }
}