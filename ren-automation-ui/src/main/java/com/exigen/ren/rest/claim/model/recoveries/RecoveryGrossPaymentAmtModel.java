package com.exigen.ren.rest.claim.model.recoveries;

import java.util.Objects;

public class RecoveryGrossPaymentAmtModel {

    private String amount;
    private String currencyCd;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrencyCd() {
        return currencyCd;
    }

    public void setCurrencyCd(String currencyCd) {
        this.currencyCd = currencyCd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecoveryGrossPaymentAmtModel that = (RecoveryGrossPaymentAmtModel) o;
        return Objects.equals(amount, that.amount) &&
                Objects.equals(currencyCd, that.currencyCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currencyCd);
    }

}
