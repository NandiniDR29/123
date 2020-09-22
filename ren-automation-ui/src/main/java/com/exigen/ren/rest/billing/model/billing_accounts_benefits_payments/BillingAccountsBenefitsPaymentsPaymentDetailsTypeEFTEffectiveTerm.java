package com.exigen.ren.rest.billing.model.billing_accounts_benefits_payments;

import java.util.Objects;

public class BillingAccountsBenefitsPaymentsPaymentDetailsTypeEFTEffectiveTerm {

    private String effective;
    private String expiration;

    public String getEffective() {
        return effective;
    }

    public void setEffective(String effective) {
        this.effective = effective;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        BillingAccountsBenefitsPaymentsPaymentDetailsTypeEFTEffectiveTerm that = (BillingAccountsBenefitsPaymentsPaymentDetailsTypeEFTEffectiveTerm) o;
        return Objects.equals(effective, that.effective) &&
                Objects.equals(expiration, that.expiration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(effective, expiration);
    }
}
