/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer.model;

import com.exigen.istf.data.TestData;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactMethodEmailModel extends ContactMethodModel{

    private String emailId;
    private String consentStatus;
    private String consentDate;

    public ContactMethodEmailModel() {
    }

    public ContactMethodEmailModel(ContactMethodEmailModel contactMethod) {
        super(contactMethod);
        this.emailId = contactMethod.getEmailId();
        this.consentStatus = contactMethod.getConsentStatus();
        this.consentDate = contactMethod.getConsentDate();
    }

    public ContactMethodEmailModel(TestData data) {
        setContactMethod(Type.EMAIL);
        switch (data.getValue("Email Type")) {
            case "Personal":
                setContactType("PERS");
                break;
            case "Common":
                setContactType("COM");
                break;
            default:
                setContactType(data.getValue("Email Type").toUpperCase());
                break;
        }
        setEmailId(data.getValue(GeneralTabMetaData.EmailDetailsMetaData.EMAIL_ADDRESS.getLabel()));
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ContactMethodEmailModel that = (ContactMethodEmailModel) o;

        if (emailId != null ? !emailId.equals(that.emailId) : that.emailId != null) {
            return false;
        }
        if (consentStatus != null ? !consentStatus.equals(that.consentStatus) : that.consentStatus != null) {
            return false;
        }
        return consentDate != null ? getConsentDate().equals(that.getConsentDate()) : that.consentDate == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (emailId != null ? emailId.hashCode() : 0);
        result = 31 * result + (consentStatus != null ? consentStatus.hashCode() : 0);
        result = 31 * result + (consentDate != null ? consentDate.hashCode() : 0);
        return result;
    }
}
