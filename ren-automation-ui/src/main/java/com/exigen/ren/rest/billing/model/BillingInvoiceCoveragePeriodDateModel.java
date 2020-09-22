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
public class BillingInvoiceCoveragePeriodDateModel extends RestError {

    private String invoiceNumber;
    private String currencyCd;
    private List<PremiumItems> premiumItems;

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCurrencyCd() {
        return currencyCd;
    }

    public void setCurrencyCd(String currencyCd) {
        this.currencyCd = currencyCd;
    }

    public List<PremiumItems> getPremiumItems() {
        return premiumItems;
    }

    public void setPremiumItems(List<PremiumItems> premiumItems) {
        this.premiumItems = premiumItems;
    }

    public static class PremiumItems {

        private String itemId;
        private String billedAmount;
        private String periodAmount;
        private Rate periodRate;
        private Rate premiumRate;
        private ItemPeriod itemPeriod;
        private BillingLocation billingLocation;
        private String billingGroupNumber;
        private String billingGroupName;
        private Coverage coverage;

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getBilledAmount() {
            return billedAmount;
        }

        public void setBilledAmount(String billedAmount) {
            this.billedAmount = billedAmount;
        }

        public String getPeriodAmount() {
            return periodAmount;
        }

        public void setPeriodAmount(String periodAmount) {
            this.periodAmount = periodAmount;
        }

        public Rate getPeriodRate() {
            return periodRate;
        }

        public void setPeriodRate(Rate periodRate) {
            this.periodRate = periodRate;
        }

        public Rate getPremiumRate() {
            return premiumRate;
        }

        public void setPremiumRate(Rate premiumRate) {
            this.premiumRate = premiumRate;
        }

        public ItemPeriod getItemPeriod() {
            return itemPeriod;
        }

        public void setItemPeriod(ItemPeriod itemPeriod) {
            this.itemPeriod = itemPeriod;
        }

        public String getBillingGroupNumber() {
            return billingGroupNumber;
        }

        public void setBillingGroupNumber(String billingGroupNumber) {
            this.billingGroupNumber = billingGroupNumber;
        }

        public String getBillingGroupName() {
            return billingGroupName;
        }

        public void setBillingGroupName(String billingGroupName) {
            this.billingGroupName = billingGroupName;
        }

        public Coverage getCoverage() {
            return coverage;
        }

        public void setCoverage(Coverage coverage) {
            this.coverage = coverage;
        }


        public BillingLocation getBillingLocation() {
            return billingLocation;
        }

        public void setBillingLocation(BillingLocation billingLocation) {
            this.billingLocation = billingLocation;
        }

        public static class Rate {
            private String rateDefinition;
            private String rate;
            private String numberOfInsureds;
            private String benefitsAmountOrVolume;

            public String getRateDefinition() {
                return rateDefinition;
            }

            public void setRateDefinition(String rateDefinition) {
                this.rateDefinition = rateDefinition;
            }

            public String getRate() {
                return rate;
            }

            public void setRate(String rate) {
                this.rate = rate;
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

        public static class ItemPeriod {
            private String startDate;
            private String endDate;

            public String getStartDate() {
                return startDate;
            }

            public void setStartDate(String startDate) {
                this.startDate = startDate;
            }

            public String getEndDate() {
                return endDate;
            }

            public void setEndDate(String endDate) {
                this.endDate = endDate;
            }
        }

        public static class BillingLocation {
            private String billingLocationCode;
            private String billingLocationName;

            public String getBillingLocationCode() {
                return billingLocationCode;
            }

            public void setBillingLocationCode(String billingLocationCode) {
                this.billingLocationCode = billingLocationCode;
            }

            public String getBillingLocationName() {
                return billingLocationName;
            }

            public void setBillingLocationName(String billingLocationName) {
                this.billingLocationName = billingLocationName;
            }
        }

        public static class Coverage {

            private String policyNumber;
            private String policyEffectiveDate;
            private String policyExpirationDate;
            private String coverageCd;
            private String coverageName;
            private String policyPackageCd;
            private String policyPackageName;
            private String classificationGroupId;
            private String classificationGroupName;
            private String classificationSubgroupId;
            private String classificationSubgroupName;
            private String coverageTierCd;
            private String coverageTierName;
            private String payorCd;

            public String getPolicyNumber() {
                return policyNumber;
            }

            public void setPolicyNumber(String policyNumber) {
                this.policyNumber = policyNumber;
            }

            public String getPolicyEffectiveDate() {
                return policyEffectiveDate;
            }

            public void setPolicyEffectiveDate(String policyEffectiveDate) {
                this.policyEffectiveDate = policyEffectiveDate;
            }

            public String getPolicyExpirationDate() {
                return policyExpirationDate;
            }

            public void setPolicyExpirationDate(String policyExpirationDate) {
                this.policyExpirationDate = policyExpirationDate;
            }

            public String getCoverageCd() {
                return coverageCd;
            }

            public void setCoverageCd(String coverageCd) {
                this.coverageCd = coverageCd;
            }

            public String getCoverageName() {
                return coverageName;
            }

            public void setCoverageName(String coverageName) {
                this.coverageName = coverageName;
            }

            public String getPolicyPackageCd() {
                return policyPackageCd;
            }

            public void setPolicyPackageCd(String policyPackageCd) {
                this.policyPackageCd = policyPackageCd;
            }

            public String getPolicyPackageName() {
                return policyPackageName;
            }

            public void setPolicyPackageName(String policyPackageName) {
                this.policyPackageName = policyPackageName;
            }

            public String getClassificationGroupId() {
                return classificationGroupId;
            }

            public void setClassificationGroupId(String classificationGroupId) {
                this.classificationGroupId = classificationGroupId;
            }

            public String getClassificationGroupName() {
                return classificationGroupName;
            }

            public void setClassificationGroupName(String classificationGroupName) {
                this.classificationGroupName = classificationGroupName;
            }

            public String getClassificationSubgroupId() {
                return classificationSubgroupId;
            }

            public void setClassificationSubgroupId(String classificationSubgroupId) {
                this.classificationSubgroupId = classificationSubgroupId;
            }

            public String getClassificationSubgroupName() {
                return classificationSubgroupName;
            }

            public void setClassificationSubgroupName(String classificationSubgroupName) {
                this.classificationSubgroupName = classificationSubgroupName;
            }

            public String getCoverageTierCd() {
                return coverageTierCd;
            }

            public void setCoverageTierCd(String coverageTierCd) {
                this.coverageTierCd = coverageTierCd;
            }

            public String getCoverageTierName() {
                return coverageTierName;
            }

            public void setCoverageTierName(String coverageTierName) {
                this.coverageTierName = coverageTierName;
            }

            public String getPayorCd() {
                return payorCd;
            }

            public void setPayorCd(String payorCd) {
                this.payorCd = payorCd;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        BillingInvoiceCoveragePeriodDateModel that = (BillingInvoiceCoveragePeriodDateModel) o;
        return Objects.equals(invoiceNumber, that.invoiceNumber) &&
                Objects.equals(currencyCd, that.currencyCd) &&
                Objects.equals(premiumItems, that.premiumItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), invoiceNumber, currencyCd, premiumItems);
    }
}
