package com.exigen.ren.rest.billing.model.billing_accounts_benefits_payments;

import java.util.Objects;

public class BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetails {

    private BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetailsNameInfo payorNameInfo;
    private BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetailsAddress payorAddress;

    public BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetailsNameInfo getPayorNameInfo() {
        return payorNameInfo;
    }

    public void setPayorNameInfo(BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetailsNameInfo payorNameInfo) {
        this.payorNameInfo = payorNameInfo;
    }

    public BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetailsAddress getPayorAddress() {
        return payorAddress;
    }

    public void setPayorAddress(BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetailsAddress payorAddress) {
        this.payorAddress = payorAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetails that = (BillingAccountsBenefitsPaymentsPaymentDetailsTypePayorDetails) o;
        return Objects.equals(payorNameInfo, that.payorNameInfo) &&
                Objects.equals(payorAddress, that.payorAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payorNameInfo, payorAddress);
    }
}
