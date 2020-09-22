/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.efolder.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubFoldersDocumentModel extends Model {
    private String folderId;
    private String folderName;
    private List<SubFoldersDocumentModel> subFolders;
    private List<DocumentModel> documents;

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<SubFoldersDocumentModel> getSubFolders() {
        return subFolders;
    }

    public void setSubFolders(List<SubFoldersDocumentModel> subFolders) {
        this.subFolders = subFolders;
    }

    public List<DocumentModel> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentModel> documents) {
        this.documents = documents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        SubFoldersDocumentModel that = (SubFoldersDocumentModel) o;
        return Objects.equals(folderId, that.folderId) &&
                Objects.equals(folderName, that.folderName) &&
                Objects.equals(subFolders, that.subFolders) &&
                Objects.equals(documents, that.documents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), folderId, folderName, subFolders, documents);
    }
}


