/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.partysearch.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartiesModel extends Model {
    private String partyType;
    private AttributesModel attributes;
    private ReferencesModel references;
    private List<RelationshipsModel> relationships;

    public void setPartyType(String partyType) {
        this.partyType = partyType;
    }
    public String getPartyType() {
        return partyType;
    }

    public void setAttributes(AttributesModel attributes) {
        this.attributes = attributes;
    }
    public AttributesModel getAttributes() {
        return attributes;
    }

    public void setReferences(ReferencesModel references) {
        this.references = references;
    }
    public ReferencesModel getReferences() {
        return references;
    }

    public void setRelationships(List<RelationshipsModel> relationships) {
        this.relationships = relationships;
    }
    public List<RelationshipsModel> getRelationships() {
        return relationships;
    }

}