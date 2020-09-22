package com.exigen.ren.rest.rating.model.gb_vs;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminCostModel extends Model {

    private String expensesNetCommission;
    private String commission;
    private String asoFee;
    private String initialASOFeePEPM;

    public String getExpensesNetCommission() {
        return expensesNetCommission;
    }

    public AdminCostModel setExpensesNetCommission(String expensesNetCommission) {
        this.expensesNetCommission = expensesNetCommission;
        return this;
    }

    public String getCommission() {
        return commission;
    }

    public AdminCostModel setCommission(String commission) {
        this.commission = commission;
        return this;
    }

    @JsonProperty("ASOFee")
    public String getASOFee() {
        return asoFee;
    }

    @JsonProperty("ASOFee")
    public AdminCostModel setASOFee(String asoFee) {
        this.asoFee = asoFee;
        return this;
    }

    @JsonProperty("InitialASOFeePEPM")
    public String getInitialASOFeePEPM() {
        return initialASOFeePEPM;
    }

    @JsonProperty("InitialASOFeePEPM")
    public AdminCostModel setInitialASOFeePEPM(String initialASOFeePEPM) {
        this.initialASOFeePEPM = initialASOFeePEPM;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AdminCostModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AdminCostModel that = (AdminCostModel) o;
        return Objects.equals(expensesNetCommission, that.expensesNetCommission) &&
                Objects.equals(commission, that.commission) &&
                Objects.equals(asoFee, that.asoFee) &&
                Objects.equals(initialASOFeePEPM, that.initialASOFeePEPM);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expensesNetCommission, commission, asoFee, initialASOFeePEPM);
    }

    @Override
    public String toString() {
        return "AdminCostModel{" +
                "expensesNetCommission='" + expensesNetCommission + '\'' +
                ", commission='" + commission + '\'' +
                ", asoFee='" + asoFee + '\'' +
                ", initialASOFeePEPM='" + initialASOFeePEPM + '\'' +
                '}';
    }
}
