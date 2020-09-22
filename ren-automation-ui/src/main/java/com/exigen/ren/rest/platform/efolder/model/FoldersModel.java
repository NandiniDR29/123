/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.efolder.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FoldersModel extends Model {
    private String folderId;

    private String folderName;

    private List<SubFoldersModel> subFolders;

    private ActionsModel actionsModel;

    private DocumentTypesModel documentTypes;

    private List<AttachmentsModel> attachments;

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderId() {
        return this.folderId;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return this.folderName;
    }

    public void setSubFolders(List<SubFoldersModel> subFolders) {
        this.subFolders = subFolders;
    }

    public List<SubFoldersModel> getSubFolders() {
        return this.subFolders;
    }

    public void setActionsModel(ActionsModel actionsModel) {
        this.actionsModel = actionsModel;
    }

    public ActionsModel getActionsModel() {
        return this.actionsModel;
    }

    public void setDocumentTypes(DocumentTypesModel documentTypes) {
        this.documentTypes = documentTypes;
    }

    public DocumentTypesModel getDocumentTypes() {
        return this.documentTypes;
    }

    public void setAttachments(List<AttachmentsModel> attachments) {
        this.attachments = attachments;
    }

    public List<AttachmentsModel> getAttachments() {
        return this.attachments;
    }
}
