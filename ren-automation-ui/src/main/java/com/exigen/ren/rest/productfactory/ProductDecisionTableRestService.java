/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.productfactory;

import com.exigen.ipb.eisa.ws.rest.RestClient;
import com.exigen.ipb.eisa.ws.rest.conf.client.ConfigurationFactory;
import com.exigen.ipb.eisa.ws.rest.conf.client.JsonConfiguration;
import com.exigen.ipb.eisa.ws.rest.conf.metadata.InlineRequestContext;
import com.exigen.ipb.eisa.ws.rest.util.RestUtil;
import com.exigen.istf.data.TestData;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.productfactory.model.DecisionTableModel;
import com.exigen.ren.rest.productfactory.model.DecisionTablePackageModel;
import com.exigen.ren.rest.productfactory.model.DecisionTablePackagesInfoModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

public class ProductDecisionTableRestService {
    private RestClient client;
    private JsonConfiguration conf;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDecisionTableRestService.class);

    public ProductDecisionTableRestService() {
        conf = ConfigurationFactory.get(JsonConfiguration.class);
        conf.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        conf.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        client =  new RestClient("product-decision-tables", conf);
    }

    public ResponseContainer<List<DecisionTablePackagesInfoModel>> postPrecIdhmoPackages(TestData data) {
        Response response = client.processRequest("PREC-IDHMO.PACKAGES",
                RestClient.HttpMethod.POST, RestUtil.convert(data, DecisionTablePackageModel.class, conf));
        return new ResponseContainer<>(response, new GenericType<List<DecisionTablePackagesInfoModel>>() {});
    }

    public ResponseContainer<List<DecisionTableModel>> postPrecIdhmoPackagesTables(TestData data, String code) {
        Response response = client.processRequest("PREC-IDHMO.PACKAGES.TABLES",
                RestClient.HttpMethod.POST, RestUtil.convert(data, DecisionTablePackageModel.class, conf),
                InlineRequestContext.builder().pathParam("code", code).build());

        //TODO(ymaliarevich): Investigation REST PF regression problem
        LOGGER.info("Response status:{}, body:{}", response.getStatus(), response.readEntity(String.class));
        return new ResponseContainer<>(response, new GenericType<List<DecisionTableModel>>() {});
    }

}


