package com.exigen.ren.rest.salesforce.model;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesforceUserModel extends RestError {

    @JsonProperty("Username")
    private String userName;
    @JsonProperty("LastName")
    private String lastName;
    @JsonProperty("FirstName")
    private String firstName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SalesforceUserModel userModel = (SalesforceUserModel) o;
        return Objects.equals(userName, userModel.userName) &&
                Objects.equals(lastName, userModel.lastName) &&
                Objects.equals(firstName, userModel.firstName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userName, lastName, firstName);
    }
}
