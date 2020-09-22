/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer.account.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountModel extends Model {
    private String accountNumber;
    private String accountName;
    private String specialHandling;
    private Boolean confidential;
    private List<String> designatedContacts;
    private List<String> affinityGroups;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getSpecialHandling() {
        return specialHandling;
    }

    public void setSpecialHandling(String specialHandling) {
        this.specialHandling = specialHandling;
    }

    public Boolean getConfidential() {
        return confidential;
    }

    public void setConfidential(Boolean confidential) {
        this.confidential = confidential;
    }

    public List getDesignatedContacts() {
        return designatedContacts;
    }

    public void setDesignatedContacts(List<String> designatedContacts) {
        this.designatedContacts = designatedContacts;
    }

    public List getAffinityGroups() {
        return affinityGroups;
    }

    public void setAffinityGroups(List<String> affinityGroups) {
        this.affinityGroups = affinityGroups;
    }
}
