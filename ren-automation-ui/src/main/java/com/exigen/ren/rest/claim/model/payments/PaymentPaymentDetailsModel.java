package com.exigen.ren.rest.claim.model.payments;

import java.util.Objects;

public class PaymentPaymentDetailsModel {

    private String type;
    private PaymentPaymentDetailsChequeModel cheque;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PaymentPaymentDetailsChequeModel getCheque() {
        return cheque;
    }

    public void setCheque(PaymentPaymentDetailsChequeModel cheque) {
        this.cheque = cheque;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PaymentPaymentDetailsModel that = (PaymentPaymentDetailsModel) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(cheque, that.cheque);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, cheque);
    }

}
