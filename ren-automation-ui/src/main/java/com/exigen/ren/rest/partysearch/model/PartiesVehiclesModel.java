/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.partysearch.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PartiesVehiclesModel extends Model {

    private String year;
    private String confidentialFlag;
    private List<String> roles;
    private String partyType;
    private Date lastUpdated;
    private String vin;
    private String model;
    private String partyId;
    private String make;
    private List<EntitiesModel> entities;
    public void setYear(String year) {
        this.year = year;
    }
    public String getYear() {
        return year;
    }

    public void setConfidentialFlag(String confidentialFlag) {
        this.confidentialFlag = confidentialFlag;
    }
    public String getConfidentialFlag() {
        return confidentialFlag;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    public List<String> getRoles() {
        return roles;
    }

    public void setPartyType(String partyType) {
        this.partyType = partyType;
    }
    public String getPartyType() {
        return partyType;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }
    public String getVin() {
        return vin;
    }

    public void setModel(String model) {
        this.model = model;
    }
    public String getModel() {
        return model;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }
    public String getPartyId() {
        return partyId;
    }

    public void setMake(String make) {
        this.make = make;
    }
    public String getMake() {
        return make;
    }

    public void setEntities(List<EntitiesModel> entities) {
        this.entities = entities;
    }
    public List<EntitiesModel> getEntities() {
        return entities;
    }
}
