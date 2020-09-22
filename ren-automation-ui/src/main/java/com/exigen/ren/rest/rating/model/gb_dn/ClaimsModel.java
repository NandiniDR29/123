package com.exigen.ren.rest.rating.model.gb_dn;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClaimsModel extends Model {

    private String tier;
    private String rate;

    public String getTier() {
        return tier;
    }

    public ClaimsModel setTier(String tier) {
        this.tier = tier;
        return this;
    }

    public String getRate() {
        return rate;
    }

    public ClaimsModel setRate(String rate) {
        this.rate = rate;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClaimsModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ClaimsModel that = (ClaimsModel) o;
        return Objects.equals(tier, that.tier) &&
                Objects.equals(rate, that.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tier, rate);
    }

    @Override
    public String toString() {
        return "ClaimsModel{" +
                "tier='" + tier + '\'' +
                ", rate='" + rate + '\'' +
                '}';
    }
}
