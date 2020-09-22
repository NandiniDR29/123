/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.partysearch.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityReferenceModel extends Model {

    private String objectName;
    private String objectId;
    private String objectNumber;
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
    public String getObjectName() {
        return objectName;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    public String getObjectId() {
        return objectId;
    }

    public void setObjectNumber(String objectNumber) {
        this.objectNumber = objectNumber;
    }
    public String getObjectNumber() {
        return objectNumber;
    }

}