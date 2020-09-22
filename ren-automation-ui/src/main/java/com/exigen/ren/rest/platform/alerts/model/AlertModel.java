/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.alerts.model;

import com.exigen.ren.rest.model.RestError;

public class AlertModel extends RestError {
    private String entityType;
    private String entityRefNo;
    private String alert;
    private String category;
    private String created;
    private String updated;
    private String id;
    private String performerId;

    public String getPerformerId(){
        return this.performerId;
    }

    public void setPerformerId(String performerId){
        this.performerId = performerId;
    }

    public String getId(){
        return this.id;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setUpdated(String updated){
        this.updated = updated;
    }

    public String getUpdated(){
        return this.updated;
    }

    public String getCreated(){
        return this.created;
    }

    public void setCreated(String created){
        this.created = created;
    }

    public String getEntityType(){
        return entityType;
    }

    public String getEntityRefNo(){
        return entityRefNo;
    }

    public String getAlert(){
        return alert;
    }

    public String getCategory(){
        return category;
    }

    public void setEntityType(String entityType){
        this.entityType = entityType;
    }

    public void setEntityRefNo(String entityRefNo){
        this.entityRefNo = entityRefNo;
    }

    public void setAlert(String alert){
        this.alert = alert;
    }

    public void setCategory(String category){
        this.category = category;
    }

}
