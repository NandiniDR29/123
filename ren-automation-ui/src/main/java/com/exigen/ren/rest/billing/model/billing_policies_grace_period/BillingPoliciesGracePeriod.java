/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.billing.model.billing_policies_grace_period;

import com.exigen.ren.rest.model.RestError;

import java.util.List;
import java.util.Objects;

public class BillingPoliciesGracePeriod extends RestError {

    private List<BillingPoliciesGracePeriodConfigurations> gracePeriodConfigurations;

    public List<BillingPoliciesGracePeriodConfigurations> getGracePeriodConfigurations() {
        return gracePeriodConfigurations;
    }

    public void setGracePeriodConfigurations(List<BillingPoliciesGracePeriodConfigurations> gracePeriodConfigurations) {
        this.gracePeriodConfigurations = gracePeriodConfigurations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        BillingPoliciesGracePeriod that = (BillingPoliciesGracePeriod) o;
        return Objects.equals(gracePeriodConfigurations, that.gracePeriodConfigurations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gracePeriodConfigurations);
    }
}