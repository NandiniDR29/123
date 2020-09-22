/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.partysearch.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PartyModel extends Model {

    private ParentNavigationReferenceModel parentNavigationReference;
    private List<PartiesModel> parties;
    public void setParentNavigationReference(ParentNavigationReferenceModel parentNavigationReference) {
        this.parentNavigationReference = parentNavigationReference;
    }
    public ParentNavigationReferenceModel getParentNavigationReference() {
        return parentNavigationReference;
    }

    public void setParties(List<PartiesModel> parties) {
        this.parties = parties;
    }
    public List<PartiesModel> getParties() {
        return parties;
    }
}
