package com.exigen.ren.rest.rating.model.gb_dn;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollmentBreakdownReportModel extends Model {

    private String tier;
    private String numberOfLives;

    public String getTier() {
        return tier;
    }

    public EnrollmentBreakdownReportModel setTier(String tier) {
        this.tier = tier;
        return this;
    }

    public String getNumberOfLives() {
        return numberOfLives;
    }

    public EnrollmentBreakdownReportModel setNumberOfLives(String numberOfLives) {
        this.numberOfLives = numberOfLives;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnrollmentBreakdownReportModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        EnrollmentBreakdownReportModel that = (EnrollmentBreakdownReportModel) o;
        return Objects.equals(tier, that.tier) &&
                Objects.equals(numberOfLives, that.numberOfLives);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tier, numberOfLives);
    }

    @Override
    public String toString() {
        return "EnrollmentBreakdownReportModel{" +
                "tier='" + tier + '\'' +
                ", numberOfLives='" + numberOfLives + '\'' +
                '}';
    }
}
