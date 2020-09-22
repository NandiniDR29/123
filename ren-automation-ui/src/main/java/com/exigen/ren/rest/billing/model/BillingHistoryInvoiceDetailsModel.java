package com.exigen.ren.rest.billing.model;

import com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.MoneyModel;
import com.exigen.ren.rest.model.RestError;

import java.util.Objects;

public class BillingHistoryInvoiceDetailsModel extends RestError {
    private String invoiceNumber;
    private String billingPeriod;
    private String generationDate;
    private MoneyModel previousBalanceAmt;
    private MoneyModel paymentsReceived;
    private MoneyModel priorPeriodAdjustmentsAmt;
    private MoneyModel paidAmt;
    private MoneyModel currentPremium;
    private MoneyModel totalAmountDue;
    private String dueDate;
    private String lastPaymentDt;

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public String getGenerationDate() {
        return generationDate;
    }

    public void setGenerationDate(String generationDate) {
        this.generationDate = generationDate;
    }

    public MoneyModel getPreviousBalanceAmt() {
        return previousBalanceAmt;
    }

    public void setPreviousBalanceAmt(MoneyModel previousBalanceAmt) {
        this.previousBalanceAmt = previousBalanceAmt;
    }

    public MoneyModel getPaymentsReceived() {
        return paymentsReceived;
    }

    public void setPaymentsReceived(MoneyModel paymentsReceived) {
        this.paymentsReceived = paymentsReceived;
    }

    public MoneyModel getPriorPeriodAdjustmentsAmt() {
        return priorPeriodAdjustmentsAmt;
    }

    public void setPriorPeriodAdjustmentsAmt(MoneyModel priorPeriodAdjustmentsAmt) {
        this.priorPeriodAdjustmentsAmt = priorPeriodAdjustmentsAmt;
    }

    public MoneyModel getPaidAmt() {
        return paidAmt;
    }

    public void setPaidAmt(MoneyModel paidAmt) {
        this.paidAmt = paidAmt;
    }

    public MoneyModel getCurrentPremium() {
        return currentPremium;
    }

    public void setCurrentPremium(MoneyModel currentPremium) {
        this.currentPremium = currentPremium;
    }

    public MoneyModel getTotalAmountDue() {
        return totalAmountDue;
    }

    public void setTotalAmountDue(MoneyModel totalAmountDue) {
        this.totalAmountDue = totalAmountDue;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getLastPaymentDt() {
        return lastPaymentDt;
    }

    public void setLastPaymentDt(String lastPaymentDt) {
        this.lastPaymentDt = lastPaymentDt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BillingHistoryInvoiceDetailsModel that = (BillingHistoryInvoiceDetailsModel) o;
        return Objects.equals(invoiceNumber, that.invoiceNumber) &&
                Objects.equals(billingPeriod, that.billingPeriod) &&
                Objects.equals(generationDate, that.generationDate) &&
                Objects.equals(previousBalanceAmt, that.previousBalanceAmt) &&
                Objects.equals(paymentsReceived, that.paymentsReceived) &&
                Objects.equals(priorPeriodAdjustmentsAmt, that.priorPeriodAdjustmentsAmt) &&
                Objects.equals(paidAmt, that.paidAmt) &&
                Objects.equals(currentPremium, that.currentPremium) &&
                Objects.equals(totalAmountDue, that.totalAmountDue) &&
                Objects.equals(dueDate, that.dueDate) &&
                Objects.equals(lastPaymentDt, that.lastPaymentDt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), invoiceNumber, billingPeriod, generationDate, previousBalanceAmt, paymentsReceived, priorPeriodAdjustmentsAmt, paidAmt, currentPremium, totalAmountDue, dueDate, lastPaymentDt);
    }
}