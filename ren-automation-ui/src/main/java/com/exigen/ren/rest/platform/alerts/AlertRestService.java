/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.alerts;

import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.ipb.eisa.ws.rest.RestClient;
import com.exigen.ipb.eisa.ws.rest.conf.client.ConfigurationFactory;
import com.exigen.ipb.eisa.ws.rest.conf.client.JsonConfiguration;
import com.exigen.ipb.eisa.ws.rest.conf.metadata.InlineRequestContext;
import com.exigen.ipb.eisa.ws.rest.util.RestUtil;
import com.exigen.istf.data.TestData;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.platform.alerts.model.AlertModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;


public class AlertRestService {

    private RestClient client;
    private JsonConfiguration conf;

    public AlertRestService() {
        conf = ConfigurationFactory.get(JsonConfiguration.class);
        conf.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        conf.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        conf.getObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        client = new RestClient("alerts-rs", conf);
    }

    public ResponseContainer<AlertModel> postAlert(TestData data) {
        return new ResponseContainer<>(client.processRequest("ALERT.POST", RestClient.HttpMethod.POST,
                RestUtil.convert(data, AlertModel.class, conf)), AlertModel.class);
    }


    public Response deleteAlert(String alertId) {
        return client.processRequest("ALERT.DELETE", RestClient.HttpMethod.DELETE, null, InlineRequestContext.builder().pathParam("alertId", alertId).build());
    }

    public ResponseContainer<AlertModel> putAlert(TestData data, String alertId){
        return new ResponseContainer<>(client.processRequest("ALERT.PUT", RestClient.HttpMethod.PUT,
                RestUtil.convert(data, AlertModel.class, conf), InlineRequestContext.builder()
                        .pathParam("alertId", alertId).build()), AlertModel.class);
    }

    public List<AlertModel> getAlert(String customerNumber, String entityType) {
        return client.processRequest("ALERT.GET", RestClient.HttpMethod.GET,
                Entity.json(null), InlineRequestContext.builder()
                        .pathParam("entityType", entityType)
                        .pathParam("entityRefNo", customerNumber).build())
                        .readEntity(new GenericType<List<AlertModel>>() {
        });
    }

    public List<AlertModel> getAlert(String customerNumber, String entityType, User user) {
        return client.processRequest("ALERT.GET", RestClient.HttpMethod.GET,
                Entity.json(null), InlineRequestContext.builder().user(user)
                        .pathParam("entityType", entityType)
                        .pathParam("entityRefNo", customerNumber).build())
                .readEntity(new GenericType<List<AlertModel>>() {
                });
    }


}
