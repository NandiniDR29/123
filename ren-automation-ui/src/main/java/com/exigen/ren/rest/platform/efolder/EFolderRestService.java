/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.efolder;

import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.ipb.eisa.ws.rest.RestClient;
import com.exigen.ipb.eisa.ws.rest.conf.client.ConfigurationFactory;
import com.exigen.ipb.eisa.ws.rest.conf.client.JsonConfiguration;
import com.exigen.ipb.eisa.ws.rest.conf.metadata.InlineRequestContext;
import com.exigen.ipb.eisa.ws.rest.conf.metadata.TestDataRequestContext;
import com.exigen.istf.data.TestData;
import com.exigen.ren.rest.platform.efolder.model.AttachmentsModel;
import com.exigen.ren.rest.platform.efolder.model.FileTypeModel;
import com.exigen.ren.rest.platform.efolder.model.FoldersModel;
import com.exigen.ren.rest.platform.efolder.model.ResponseDocumentThumbnailModel;
import com.fasterxml.jackson.databind.DeserializationFeature;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

public class EFolderRestService {

    private RestClient client;

    public EFolderRestService() {
        JsonConfiguration conf = ConfigurationFactory.get(JsonConfiguration.class);
        conf.getObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        client = new RestClient("efolder-rs", conf);
    }

    public List<FoldersModel> getFolders(TestData data) {
        return client.processRequest("FOLDERS", RestClient.HttpMethod.GET, null,
                TestDataRequestContext.of(data)).readEntity(new GenericType<List<FoldersModel>>() {
        });
    }

    public List<FoldersModel> getFolders(String entityRefNo, String entityType) {
        return client.processRequest("FOLDERS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam("entityRefNo", entityRefNo).pathParam("entityType", entityType).build()).readEntity(new GenericType<List<FoldersModel>>() {
        });
    }

    public List<AttachmentsModel> getDocument(String documentId, String agencyCd, String brandCd) {
        return client.processRequest("DOCUMENTS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().queryParam("uuids", documentId)
                        .queryParam("agencyCd", agencyCd).queryParam("brandCd", brandCd).build())
                .readEntity(new GenericType<List<AttachmentsModel>>() {
                });
    }

    public List<AttachmentsModel> getDocument(String documentId, String agencyCd, String brandCd, User user) {
        return client.processRequest("DOCUMENTS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().queryParam("uuids", documentId).user(user)
                        .queryParam("agencyCd", agencyCd).queryParam("brandCd", brandCd).build())
                .readEntity(new GenericType<List<AttachmentsModel>>() {
                });
    }

    public ResponseDocumentThumbnailModel getDocumentThumbnail(String documentId, User user){
        return client.processRequest("DOCUMENTS.THUMBNAIL", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam("uuid", documentId).user(user).build()).readEntity(new GenericType<ResponseDocumentThumbnailModel>() {
        });
    }
    public Response putDocument(String documentId, TestData td) {
        return client.processRequest("DOCUMENTS.UUID", RestClient.HttpMethod.PUT, td,
                InlineRequestContext.builder().pathParam("uuid", documentId).build());
    }

    public Response getDocumentByUUID(String documentId, User user){
        return client.processRequest("DOCUMENTS.UUID", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam("uuid", documentId).user(user).build());
    }

    public List<FoldersModel> getFolders(String entityRefNo, String entityType, User user) {
        return client.processRequest("FOLDERS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam("entityRefNo", entityRefNo).pathParam("entityType", entityType).user(user).build()).readEntity(new GenericType<List<FoldersModel>>() {
        });
    }

    public List<FileTypeModel> getAllowedDocumentTypes(User user){
        return client.processRequest("DOCUMENTS.ALLOWEDFILETYPES", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().user(user).build()).readEntity(new GenericType<List<FileTypeModel>>() {
        });
    }
}
