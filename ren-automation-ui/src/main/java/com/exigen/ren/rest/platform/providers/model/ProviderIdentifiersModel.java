/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.providers.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;

public class ProviderIdentifiersModel extends Model {
    private String id;
    private String oid;
    private IdentitiesModel identities;
    private String taxId;
    private String vendorTypeCd;

    public String getVendorTypeCd() {
        return vendorTypeCd;
    }

    public void setVendorTypeCd(String vendorTypeCd) {
        this.vendorTypeCd = vendorTypeCd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public IdentitiesModel getIdentities() {
        return identities;
    }

    public void setIdentities(IdentitiesModel identities) {
        this.identities = identities;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }
}
