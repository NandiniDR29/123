package com.exigen.ren.rest.rating.model.gb_vs;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollmentByStateModel extends Model {

    private String state;
    private String numberOfLives;
    private String enrolledPct;

    public String getState() {
        return state;
    }

    public EnrollmentByStateModel setState(String state) {
        this.state = state;
        return this;
    }

    public String getNumberOfLives() {
        return numberOfLives;
    }

    public EnrollmentByStateModel setNumberOfLives(String numberOfLives) {
        this.numberOfLives = numberOfLives;
        return this;
    }

    public String getEnrolledPct() {
        return enrolledPct;
    }

    public EnrollmentByStateModel setEnrolledPct(String enrolledPct) {
        this.enrolledPct = enrolledPct;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnrollmentByStateModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        EnrollmentByStateModel that = (EnrollmentByStateModel) o;
        return Objects.equals(state, that.state) &&
                Objects.equals(numberOfLives, that.numberOfLives) &&
                Objects.equals(enrolledPct, that.enrolledPct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), state, numberOfLives, enrolledPct);
    }

    @Override
    public String toString() {
        return "EnrollmentByStateModel{" +
                "state='" + state + '\'' +
                ", numberOfLives='" + numberOfLives + '\'' +
                ", enrolledPct='" + enrolledPct + '\'' +
                '}';
    }
}
