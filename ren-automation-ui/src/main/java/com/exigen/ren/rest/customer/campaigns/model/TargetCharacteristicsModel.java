/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer.campaigns.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TargetCharacteristicsModel extends Model {

    private List<String> leadTargetSource;
    private List<String> countryCd;
    private boolean excludeActivePolicyholders;

    private List<String> status;
    private List<String> maritalStatusCd;

    public void setLeadTargetSource(List<String> leadTargetSource) {
        this.leadTargetSource = leadTargetSource;
    }

    public List<String> getLeadTargetSource() {
        return leadTargetSource;
    }

    public void setCountryCd(List<String> countryCd) {
        this.countryCd = countryCd;
    }

    public List<String> getCountryCd() {
        return countryCd;
    }

    public void setExcludeActivePolicyholders(boolean excludeActivePolicyholders) {
        this.excludeActivePolicyholders = excludeActivePolicyholders;
    }

    public boolean getExcludeActivePolicyholders() {
        return excludeActivePolicyholders;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

    public List<String> getStatus() {
        return status;
    }

    public void setMaritalStatusCd(List<String> maritalStatusCd) {
        this.maritalStatusCd = maritalStatusCd;
    }

    public List<String> getMaritalStatusCd() {
        return maritalStatusCd;
    }
}
