/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.billing.model;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingInvoiceCoveragePeriodDatePUTModel extends RestError {

    private String transactionReason;
    private List<PremiumItems> premiumItems;

    public String getTransactionReason() {
        return transactionReason;
    }

    public void setTransactionReason(String transactionReason) {
        this.transactionReason = transactionReason;
    }

    public List<PremiumItems> getPremiumItems() {
        return premiumItems;
    }

    public void setPremiumItems(List<PremiumItems> premiumItems) {
        this.premiumItems = premiumItems;
    }

    public static class PremiumItems {

        private String itemId;
        private String periodAmount;
        private String numberOfInsureds;
        private String benefitsAmountOrVolume;

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getPeriodAmount() {
            return periodAmount;
        }

        public void setPeriodAmount(String periodAmount) {
            this.periodAmount = periodAmount;
        }

        public String getNumberOfInsureds() {
            return numberOfInsureds;
        }

        public void setNumberOfInsureds(String numberOfInsureds) {
            this.numberOfInsureds = numberOfInsureds;
        }

        public String getBenefitsAmountOrVolume() {
            return benefitsAmountOrVolume;
        }

        public void setBenefitsAmountOrVolume(String benefitsAmountOrVolume) {
            this.benefitsAmountOrVolume = benefitsAmountOrVolume;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        BillingInvoiceCoveragePeriodDatePUTModel that = (BillingInvoiceCoveragePeriodDatePUTModel) o;
        return Objects.equals(transactionReason, that.transactionReason) &&
                Objects.equals(premiumItems, that.premiumItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transactionReason, premiumItems);
    }
}
