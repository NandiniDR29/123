/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NavigationLinkModel extends Model {

    private String id;
    private String sourceNumber;
    private String targetNumber;
    private String linkType;

    public NavigationLinkModel() {
    }

    public NavigationLinkModel(String sourceNumber, String targetNumber, String linkType) {
        this.sourceNumber = sourceNumber;
        this.targetNumber = targetNumber;
        this.linkType = linkType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceNumber() {
        return sourceNumber;
    }

    public void setSourceNumber(String sourceNumber) {
        this.sourceNumber = sourceNumber;
    }

    public String getTargetNumber() {
        return targetNumber;
    }

    public void setTargetNumber(String targetNumber) {
        this.targetNumber = targetNumber;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NavigationLinkModel that = (NavigationLinkModel) o;

        if (sourceNumber != null ? !sourceNumber.equals(that.sourceNumber) : that.sourceNumber != null) {
            return false;
        }
        if (targetNumber != null ? !targetNumber.equals(that.targetNumber) : that.targetNumber != null) {
            return false;
        }
        return linkType != null ? linkType.equals(that.linkType) : that.linkType == null;
    }

    @Override
    public int hashCode() {
        int result = sourceNumber != null ? sourceNumber.hashCode() : 0;
        result = 31 * result + (targetNumber != null ? targetNumber.hashCode() : 0);
        result = 31 * result + (linkType != null ? linkType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NavigationLink{" +
                "id='" + id + '\'' +
                ", sourceNumber='" + sourceNumber + '\'' +
                ", targetNumber='" + targetNumber + '\'' +
                ", linkType='" + linkType + '\'' +
                '}';
    }
}
