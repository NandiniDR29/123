package com.exigen.ren.rest.billing.model.billing_accounts_benefits_payments;

import java.util.Objects;

public class BillingAccountsBenefitsPaymentsPaymentDetails {

    private String type;
    private String cheque;
    private BillingAccountsBenefitsPaymentsPaymentDetailsTypeCreditCard creditCard;
    private BillingAccountsBenefitsPaymentsPaymentDetailsTypeEFT eft;
    private BillingAccountsBenefitsPaymentsPaymentDetailsTypeCash cash;
    private BillingAccountsBenefitsPaymentsPaymentDetailsTypeAch ach;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCheque() {
        return cheque;
    }

    public void setCheque(String cheque) {
        this.cheque = cheque;
    }

    public BillingAccountsBenefitsPaymentsPaymentDetailsTypeCreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(BillingAccountsBenefitsPaymentsPaymentDetailsTypeCreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public BillingAccountsBenefitsPaymentsPaymentDetailsTypeEFT getEft() {
        return eft;
    }

    public void setEft(BillingAccountsBenefitsPaymentsPaymentDetailsTypeEFT eft) {
        this.eft = eft;
    }

    public BillingAccountsBenefitsPaymentsPaymentDetailsTypeCash getCash() {
        return cash;
    }

    public void setCash(BillingAccountsBenefitsPaymentsPaymentDetailsTypeCash cash) {
        this.cash = cash;
    }

    public BillingAccountsBenefitsPaymentsPaymentDetailsTypeAch getAch() {
        return ach;
    }

    public void setAch(BillingAccountsBenefitsPaymentsPaymentDetailsTypeAch ach) {
        this.ach = ach;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        BillingAccountsBenefitsPaymentsPaymentDetails that = (BillingAccountsBenefitsPaymentsPaymentDetails) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(cheque, that.cheque) &&
                Objects.equals(creditCard, that.creditCard) &&
                Objects.equals(eft, that.eft) &&
                Objects.equals(cash, that.cash) &&
                Objects.equals(ach, that.ach);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, cheque, creditCard, eft, cash, ach);
    }
}
