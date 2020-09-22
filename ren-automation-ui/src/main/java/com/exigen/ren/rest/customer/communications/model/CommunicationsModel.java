/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer.communications.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommunicationsModel extends Model {
    private String categoryCd;
    private String subCategoryCd;
    private String sourceCd;
    private String languageCd;
    private String internalCallerCd;
    private String subject;
    private String statusCd;
    private String channelCd;
    private String directionCd;
    private String entityTypeCd;
    private String entityReferenceId;

    public CommunicationsModel(){}

    public String getCategoryCd() {
        return categoryCd;
    }

    public void setCategoryCd(String categoryCd) {
        this.categoryCd = categoryCd;
    }

    public String getSubCategoryCd() {
        return subCategoryCd;
    }

    public void setSubCategoryCd(String subCategoryCd) {
        this.subCategoryCd = subCategoryCd;
    }

    public String getSourceCd() {
        return sourceCd;
    }

    public void setSourceCd(String sourceCd) {
        this.sourceCd = sourceCd;
    }

    public String getLanguageCd() {
        return languageCd;
    }

    public void setLanguageCd(String languageCd) {
        this.languageCd = languageCd;
    }

    public String getInternalCallerCd() {
        return internalCallerCd;
    }

    public void setInternalCallerCd(String internalCallerCd) {
        this.internalCallerCd = internalCallerCd;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getChannelCd() {
        return channelCd;
    }

    public void setChannelCd(String channelCd) {
        this.channelCd = channelCd;
    }

    public String getDirectionCd() {
        return directionCd;
    }

    public void setDirectionCd(String directionCd) {
        this.directionCd = directionCd;
    }

    public String getEntityTypeCd() {
        return entityTypeCd;
    }

    public void setEntityTypeCd(String entityTypeCd) {
        this.entityTypeCd = entityTypeCd;
    }

    public String getEntityReferenceId() {
        return entityReferenceId;
    }

    public void setEntityReferenceId(String entityReferenceId) {
        this.entityReferenceId = entityReferenceId;
    }


}
