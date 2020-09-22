package com.exigen.ren.rest.rating.model.gb_dn;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrentRatesBreakdownModel extends Model {

    private String currentRate;
    private String renewalRate;
    private String numberOfLives;
    private String tier;

    public String getCurrentRate() {
        return currentRate;
    }

    public CurrentRatesBreakdownModel setCurrentRate(String currentRate) {
        this.currentRate = currentRate;
        return this;
    }

    public String getRenewalRate() {
        return renewalRate;
    }

    public CurrentRatesBreakdownModel setRenewalRate(String renewalRate) {
        this.renewalRate = renewalRate;
        return this;
    }

    public String getNumberOfLives() {
        return numberOfLives;
    }

    public CurrentRatesBreakdownModel setNumberOfLives(String numberOfLives) {
        this.numberOfLives = numberOfLives;
        return this;
    }

    public String getTier() {
        return tier;
    }

    public CurrentRatesBreakdownModel setTier(String tier) {
        this.tier = tier;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CurrentRatesBreakdownModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CurrentRatesBreakdownModel that = (CurrentRatesBreakdownModel) o;
        return Objects.equals(currentRate, that.currentRate) &&
                Objects.equals(renewalRate, that.renewalRate) &&
                Objects.equals(numberOfLives, that.numberOfLives) &&
                Objects.equals(tier, that.tier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), currentRate, renewalRate, numberOfLives, tier);
    }

    @Override
    public String toString() {
        return "CurrentRatesBreakdownModel{" +
                "currentRate='" + currentRate + '\'' +
                ", renewalRate='" + renewalRate + '\'' +
                ", numberOfLives='" + numberOfLives + '\'' +
                ", tier='" + tier + '\'' +
                '}';
    }
}
