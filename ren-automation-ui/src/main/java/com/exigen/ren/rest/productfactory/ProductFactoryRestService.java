/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.productfactory;

import com.exigen.ipb.eisa.ws.rest.RestClient;
import com.exigen.ipb.eisa.ws.rest.conf.client.ConfigurationFactory;
import com.exigen.ipb.eisa.ws.rest.conf.client.JsonConfiguration;
import com.exigen.ipb.eisa.ws.rest.entity.FreemarkerEntity;
import com.exigen.ipb.eisa.ws.rest.util.RestUtil;
import com.exigen.istf.data.DataProviderFactory;
import com.exigen.istf.data.TestData;
import com.exigen.istf.rest.ResponseWrapper;
import com.exigen.ren.rest.productfactory.model.ActionRequestModel;

import javax.ws.rs.core.Response;

public class ProductFactoryRestService {
    private RestClient client;

    public ProductFactoryRestService(){
        client = new RestClient("product-rt-rs", ConfigurationFactory.get(JsonConfiguration.class));
    }

    /**
     * @param productCd product code
     * @param policyNumber policy number
     * @return get-all policy image
     */
    public ResponseWrapper getAllOperation(String productCd, String policyNumber) {
        return postProductOperation(DataProviderFactory.dataOf("productCd", productCd, "entityRefNo", policyNumber),
                "GET.ALL");
    }

    public ResponseWrapper postProductOperation(TestData testData, String template) {
        return new ResponseWrapper(client.processRequest("PRODUCT-OPERATIONS", RestClient.HttpMethod.POST,
                FreemarkerEntity.from("product-rt-rs/" + template, testData)));
    }

    public Response postProductOperation(TestData testData) {
        Response response = client.processRequest("PRODUCT-OPERATIONS",
                RestClient.HttpMethod.POST, RestUtil.convert(testData, ActionRequestModel.class));
        return response;
    }
}
