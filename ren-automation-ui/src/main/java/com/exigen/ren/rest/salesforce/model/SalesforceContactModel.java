package com.exigen.ren.rest.salesforce.model;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesforceContactModel extends RestError {

    @JsonProperty("Id")
    private String id;
    @JsonProperty("AccountId")
    private String accountName;
    @JsonProperty("Birthdate")
    private String birthdate;
    @JsonProperty("Email")
    private String email;
    @JsonProperty("MailingStreet")
    private String mailingStreet;
    @JsonProperty("MailingCity")
    private String mailingCity;
    @JsonProperty("MailingState")
    private String mailingState;
    @JsonProperty("MailingPostalCode")
    private String mailingPostalCode;
    @JsonProperty("MailingCountry")
    private String mailingCountry;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Salutation")
    private String salutation;
    @JsonProperty("FirstName")
    private String firstName;
    @JsonProperty("LastName")
    private String lastName;

    public String getId() {
        return id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMailingStreet() {
        return mailingStreet;
    }

    public void setMailingStreet(String mailingStreet) {
        this.mailingStreet = mailingStreet;
    }

    public String getMailingCity() {
        return mailingCity;
    }

    public void setMailingCity(String mailingCity) {
        this.mailingCity = mailingCity;
    }

    public String getMailingState() {
        return mailingState;
    }

    public void setMailingState(String mailingState) {
        this.mailingState = mailingState;
    }

    public String getMailingPostalCode() {
        return mailingPostalCode;
    }

    public void setMailingPostalCode(String mailingPostalCode) {
        this.mailingPostalCode = mailingPostalCode;
    }

    public String getMailingCountry() {
        return mailingCountry;
    }

    public void setMailingCountry(String mailingCountry) {
        this.mailingCountry = mailingCountry;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastNamed(String lastName) {
        this.lastName = lastName;
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
        SalesforceContactModel that = (SalesforceContactModel) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(accountName, that.accountName) &&
                Objects.equals(birthdate, that.birthdate) &&
                Objects.equals(email, that.email) &&
                Objects.equals(mailingStreet, that.mailingStreet) &&
                Objects.equals(mailingCity, that.mailingCity) &&
                Objects.equals(mailingState, that.mailingState) &&
                Objects.equals(mailingPostalCode, that.mailingPostalCode) &&
                Objects.equals(mailingCountry, that.mailingCountry) &&
                Objects.equals(name, that.name) &&
                Objects.equals(salutation, that.salutation) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, accountName, birthdate, email, mailingStreet, mailingCity, mailingState, mailingPostalCode, mailingCountry, name, salutation, firstName, lastName);
    }
}
