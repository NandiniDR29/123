/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer.model;

import com.exigen.istf.data.TestData;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactMethodPhoneModel extends ContactMethodModel {

    private String phoneNumber;
    private String phoneExtension;
    private String consentStatus;
    private String consentDate;
    private List<String> preferredDaysToContact = new ArrayList<>();
    private List<String> preferredTimesToContact = new ArrayList<>();

    public ContactMethodPhoneModel() {
    }

    public ContactMethodPhoneModel(ContactMethodPhoneModel contactMethod) {
        super(contactMethod);
        this.phoneNumber = contactMethod.getPhoneNumber();
        this.phoneExtension = contactMethod.getPhoneExtension();
        this.consentStatus = contactMethod.getConsentStatus();
        this.consentDate = contactMethod.getConsentDate();
        this.preferredDaysToContact = contactMethod.getPreferredDaysToContact();
        this.preferredTimesToContact = contactMethod.getPreferredTimesToContact();
    }

    public ContactMethodPhoneModel(TestData data, boolean individual) {
        setContactMethod(Type.PHONE);
        setContactType(individual ? "HOME" : "BMOB");
//        setContactType(data.getValue("Phone Type").toUpperCase());
        setPhoneNumber(data.getValue(GeneralTabMetaData.PhoneDetailsMetaData.PHONE_NUMBER.getLabel()));
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneExtension() {
        return phoneExtension;
    }

    public void setPhoneExtension(String phoneExtension) {
        this.phoneExtension = phoneExtension;
    }

    public List<String> getPreferredDaysToContact() {
        return preferredDaysToContact;
    }

    public void addPreferredDaysToContact(String... preferredDaysToContact) {
        Collections.addAll(this.preferredDaysToContact, preferredDaysToContact);
    }

    public List<String> getPreferredTimesToContact() {
        return preferredTimesToContact;
    }

    public void addPreferredTimesToContact(String... preferredTimesToContact) {
        Collections.addAll(this.preferredTimesToContact, preferredTimesToContact);
    }

    public String getConsentStatus() {
        return consentStatus;
    }

    public void setConsentStatus(String consentStatus) {
        this.consentStatus = consentStatus;
    }

    public String getConsentDate() {
        //cutting time if exist, leaving only date
        if (null != this.consentDate && this.consentDate.contains("T")) {
            this.consentDate = this.consentDate.split("T")[0];
        }
        return consentDate;
    }

    public void setConsentDate(String consentDate) {
        this.consentDate = consentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RestError)) {
            return false;
        }

        if (getErrorCode() != null) {
            return super.equals(o);
        }

        if (getClass() != o.getClass()) {
            return false;
        }

        ContactMethodPhoneModel that = (ContactMethodPhoneModel) o;

        if (phoneNumber != null ? !phoneNumber.equals(that.phoneNumber) : that.phoneNumber != null) {
            return false;
        }
        if (phoneExtension != null ? !phoneExtension.equals(that.phoneExtension) : that.phoneExtension != null) {
            return false;
        }
        if (consentDate != null ? !getConsentDate().equals(that.getConsentDate()) : that.consentDate != null) {
            return false;
        }
        if (consentStatus != null ? !consentStatus.equals(that.consentStatus) : that.consentStatus != null) {
            return false;
        }
        if (preferredDaysToContact != null ? !preferredDaysToContact.containsAll(that.preferredDaysToContact) : that.preferredDaysToContact != null) {
            return false;
        }
        return preferredTimesToContact != null ? preferredTimesToContact.containsAll(that.preferredTimesToContact) : that.preferredTimesToContact == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (phoneExtension != null ? phoneExtension.hashCode() : 0);
        result = 31 * result + (consentDate != null ? consentDate.hashCode() : 0);
        result = 31 * result + (consentStatus != null ? consentStatus.hashCode() : 0);
        result = 31 * result + (preferredDaysToContact != null ? preferredDaysToContact.hashCode() : 0);
        result = 31 * result + (preferredTimesToContact != null ? preferredTimesToContact.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String parentToString = super.toString();
        return parentToString + "\nChildClass\nContactMethodPhone{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", phoneExtension='" + phoneExtension + '\'' +
                ", consentDate='" + consentDate + '\'' +
                ", consentStatus='" + consentStatus + '\'' +
                ", preferredDaysToContact=" + preferredDaysToContact +
                ", preferredTimesToContact=" + preferredTimesToContact +
                '}';
    }
}
