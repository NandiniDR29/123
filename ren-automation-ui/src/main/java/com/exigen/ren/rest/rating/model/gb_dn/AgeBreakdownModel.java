package com.exigen.ren.rest.rating.model.gb_dn;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgeBreakdownModel extends Model {

    private String ageBand;
    private String numberOfLives;

    public String getAgeBand() {
        return ageBand;
    }

    public AgeBreakdownModel setAgeBand(String ageBand) {
        this.ageBand = ageBand;
        return this;
    }

    public String getNumberOfLives() {
        return numberOfLives;
    }

    public AgeBreakdownModel setNumberOfLives(String numberOfLives) {
        this.numberOfLives = numberOfLives;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AgeBreakdownModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AgeBreakdownModel that = (AgeBreakdownModel) o;
        return Objects.equals(ageBand, that.ageBand) &&
                Objects.equals(numberOfLives, that.numberOfLives);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ageBand, numberOfLives);
    }

    @Override
    public String toString() {
        return "AgeBreakdownModel{" +
                "ageBand='" + ageBand + '\'' +
                ", numberOfLives='" + numberOfLives + '\'' +
                '}';
    }
}
