package com.exigen.ren.rest.rating.model.gb_dn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpouseDetailsModel {

    private String tierElement;
    private String benefitCategory;
    private String base;
    private String deductibleCredit;
    private String deductibleUtil;
    private String ageFactor;
    private String copay;
    private String claimsRate;
    private String rateCalc;
    private String rateCalcWO;

    public String getTierElement() {
        return tierElement;
    }

    public SpouseDetailsModel setTierElement(String tierElement) {
        this.tierElement = tierElement;
        return this;
    }

    public String getBenefitCategory() {
        return benefitCategory;
    }

    public SpouseDetailsModel setBenefitCategory(String benefitCategory) {
        this.benefitCategory = benefitCategory;
        return this;
    }

    public String getBase() {
        return base;
    }

    public SpouseDetailsModel setBase(String base) {
        this.base = base;
        return this;
    }

    public String getDeductibleCredit() {
        return deductibleCredit;
    }

    public SpouseDetailsModel setDeductibleCredit(String deductibleCredit) {
        this.deductibleCredit = deductibleCredit;
        return this;
    }

    public String getDeductibleUtil() {
        return deductibleUtil;
    }

    public SpouseDetailsModel setDeductibleUtil(String deductibleUtil) {
        this.deductibleUtil = deductibleUtil;
        return this;
    }

    public String getAgeFactor() {
        return ageFactor;
    }

    public SpouseDetailsModel setAgeFactor(String ageFactor) {
        this.ageFactor = ageFactor;
        return this;
    }

    public String getCopay() {
        return copay;
    }

    public SpouseDetailsModel setCopay(String copay) {
        this.copay = copay;
        return this;
    }

    public String getClaimsRate() {
        return claimsRate;
    }

    public SpouseDetailsModel setClaimsRate(String claimsRate) {
        this.claimsRate = claimsRate;
        return this;
    }

    public String getRateCalc() {
        return rateCalc;
    }

    public SpouseDetailsModel setRateCalc(String rateCalc) {
        this.rateCalc = rateCalc;
        return this;
    }

    public String getRateCalcWO() {
        return rateCalcWO;
    }

    public SpouseDetailsModel setRateCalcWO(String rateCalcWO) {
        this.rateCalcWO = rateCalcWO;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpouseDetailsModel)) {
            return false;
        }
        SpouseDetailsModel that = (SpouseDetailsModel) o;
        return Objects.equals(tierElement, that.tierElement) &&
                Objects.equals(benefitCategory, that.benefitCategory) &&
                Objects.equals(base, that.base) &&
                Objects.equals(deductibleCredit, that.deductibleCredit) &&
                Objects.equals(deductibleUtil, that.deductibleUtil) &&
                Objects.equals(ageFactor, that.ageFactor) &&
                Objects.equals(copay, that.copay) &&
                Objects.equals(claimsRate, that.claimsRate) &&
                Objects.equals(rateCalc, that.rateCalc) &&
                Objects.equals(rateCalcWO, that.rateCalcWO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tierElement, benefitCategory, base, deductibleCredit, deductibleUtil, ageFactor, copay, claimsRate, rateCalc, rateCalcWO);
    }

    @Override
    public String toString() {
        return "SpouseDetailsModel{" +
                "tierElement='" + tierElement + '\'' +
                ", benefitCategory='" + benefitCategory + '\'' +
                ", base='" + base + '\'' +
                ", deductibleCredit='" + deductibleCredit + '\'' +
                ", deductibleUtil='" + deductibleUtil + '\'' +
                ", ageFactor='" + ageFactor + '\'' +
                ", copay='" + copay + '\'' +
                ", claimsRate='" + claimsRate + '\'' +
                ", rateCalc='" + rateCalc + '\'' +
                ", rateCalcWO='" + rateCalcWO + '\'' +
                '}';
    }
}
