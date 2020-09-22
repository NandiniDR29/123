/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgencyModel extends Model {

    private String agencyCode;

    public AgencyModel() {
    }

    public AgencyModel(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public AgencyModel setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
        return this;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AgencyModel agencyModel = (AgencyModel) o;

        return agencyCode != null ? agencyCode.equals(agencyModel.agencyCode) : agencyModel.agencyCode == null;
    }

    @Override
    public int hashCode() {
        return agencyCode != null ? agencyCode.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Agency{" +
                "agencyCode='" + agencyCode + '\'' +
                '}';
    }
}
