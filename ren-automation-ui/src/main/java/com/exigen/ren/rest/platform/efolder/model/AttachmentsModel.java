/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.efolder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AttachmentsModel {
    private String attachmentName;

    private String attachmentFullPath;

    private String entityRefNo;

    private String entityType;

    private String attachmentType;

    private String agencyCd;

    private String brandCd;

    private String fileComments;

    private CreatedByModel createdByModel;

    private String creationDate;

    private ModifiedByModel modifiedBy;

    private String modificationDate;

    private String fileType;

    private String fileSize;

    private String fileReference;

    private ActionsModel actionsModel;

    private String externalUrl;

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentName() {
        return this.attachmentName;
    }

    public void setAttachmentFullPath(String attachmentFullPath) {
        this.attachmentFullPath = attachmentFullPath;
    }

    public String getAttachmentFullPath() {
        return this.attachmentFullPath;
    }

    public void setEntityRefNo(String entityRefNo) {
        this.entityRefNo = entityRefNo;
    }

    public String getEntityRefNo() {
        return this.entityRefNo;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityType() {
        return this.entityType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getAttachmentType() {
        return this.attachmentType;
    }

    public void setAgencyCd(String agencyCd) {
        this.agencyCd = agencyCd;
    }

    public String getAgencyCd() {
        return this.agencyCd;
    }

    public void setBrandCd(String brandCd) {
        this.brandCd = brandCd;
    }

    public String getBrandCd() {
        return this.brandCd;
    }

    public void setFileComments(String fileComments) {
        this.fileComments = fileComments;
    }

    public String getFileComments() {
        return this.fileComments;
    }

    public void setCreatedByModel(CreatedByModel createdByModel) {
        this.createdByModel = createdByModel;
    }

    public CreatedByModel getCreatedByModel() {
        return this.createdByModel;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationDate() {
        return this.creationDate;
    }

    public void setModifiedBy(ModifiedByModel modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public ModifiedByModel getModifiedBy() {
        return this.modifiedBy;
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getModificationDate() {
        return this.modificationDate;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileSize() {
        return this.fileSize;
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    public String getFileReference() {
        return this.fileReference;
    }

    public void setActionsModel(ActionsModel actionsModel) {
        this.actionsModel = actionsModel;
    }

    public ActionsModel getActionsModel() {
        return this.actionsModel;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public String getExternalUrl() {
        return this.externalUrl;
    }
}
