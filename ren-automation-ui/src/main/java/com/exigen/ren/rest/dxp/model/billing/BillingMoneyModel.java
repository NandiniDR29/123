package com.exigen.ren.rest.dxp.model.billing;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingMoneyModel extends RestError {
    private String value;
    private String currencyCd;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        if (!super.equals(o)) {
            return false;
        }
        BillingMoneyModel that = (BillingMoneyModel) o;
        return Objects.equals(value, that.value) &&
                Objects.equals(currencyCd, that.currencyCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value, currencyCd);
    }
}
