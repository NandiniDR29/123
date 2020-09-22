/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.partysearch.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EntitiesModel extends Model {

    private String entityClass;
    private String entityType;
    private String entityLob;
    private String partyRole;
    private String entityId;
    private String eisNavigationReference;
    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }
    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    public String getEntityType() {
        return entityType;
    }

    public void setEntityLob(String entityLob) {
        this.entityLob = entityLob;
    }
    public String getEntityLob() {
        return entityLob;
    }

    public void setPartyRole(String partyRole) {
        this.partyRole = partyRole;
    }
    public String getPartyRole() {
        return partyRole;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
    public String getEntityId() {
        return entityId;
    }

    public void setEisNavigationReference(String eisNavigationReference) {
        this.eisNavigationReference = eisNavigationReference;
    }
    public String getEisNavigationReference() {
        return eisNavigationReference;
    }
}
