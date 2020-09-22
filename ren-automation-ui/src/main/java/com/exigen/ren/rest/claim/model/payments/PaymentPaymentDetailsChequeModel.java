package com.exigen.ren.rest.claim.model.payments;

import java.util.Objects;

public class PaymentPaymentDetailsChequeModel {

    private String checkNumber;
    private String manualInd;
    private String paymentDeliveryMethodCd;
    private String address;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PaymentPaymentDetailsChequeModel that = (PaymentPaymentDetailsChequeModel) o;
        return Objects.equals(checkNumber, that.checkNumber) &&
                Objects.equals(manualInd, that.manualInd) &&
                Objects.equals(paymentDeliveryMethodCd, that.paymentDeliveryMethodCd) &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkNumber, manualInd, paymentDeliveryMethodCd, address);
    }

}
