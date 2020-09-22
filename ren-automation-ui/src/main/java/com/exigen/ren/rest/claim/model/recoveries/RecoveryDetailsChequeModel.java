package com.exigen.ren.rest.claim.model.recoveries;

import java.util.Objects;

public class RecoveryDetailsChequeModel {

    private String checkNumber;
    private String manualInd;
    private String paymentDeliveryMethodCd;

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getManualInd() {
        return manualInd;
    }

    public void setManualInd(String manualInd) {
        this.manualInd = manualInd;
    }

    public String getPaymentDeliveryMethodCd() {
        return paymentDeliveryMethodCd;
    }

    public void setPaymentDeliveryMethodCd(String paymentDeliveryMethodCd) {
        this.paymentDeliveryMethodCd = paymentDeliveryMethodCd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecoveryDetailsChequeModel that = (RecoveryDetailsChequeModel) o;
        return Objects.equals(checkNumber, that.checkNumber) &&
                Objects.equals(manualInd, that.manualInd) &&
                Objects.equals(paymentDeliveryMethodCd, that.paymentDeliveryMethodCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkNumber, manualInd, paymentDeliveryMethodCd);
    }

}
