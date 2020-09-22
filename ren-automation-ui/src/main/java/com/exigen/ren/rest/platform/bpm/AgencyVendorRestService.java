/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.bpm;

import com.exigen.ipb.eisa.ws.rest.RestClient;
import com.exigen.ipb.eisa.ws.rest.conf.client.ConfigurationFactory;
import com.exigen.ipb.eisa.ws.rest.conf.client.JsonConfiguration;
import com.exigen.ipb.eisa.ws.rest.conf.metadata.InlineRequestContext;
import com.exigen.ipb.eisa.ws.rest.util.RestUtil;
import com.exigen.istf.data.TestData;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.platform.bpm.model.agencyvendor.AddressDataModel;
import com.exigen.ren.rest.platform.bpm.model.agencyvendor.AgencyVendorModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;

public class AgencyVendorRestService {
    private RestClient client;
    private JsonConfiguration conf;

    public AgencyVendorRestService() {
        conf = ConfigurationFactory.get(JsonConfiguration.class);
        conf.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        conf.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        client = new RestClient("thirdparty-rs", conf);
    }

    public ResponseContainer<AgencyVendorModel> createAgency(TestData data) {
        return new ResponseContainer<>(client.processRequest("AGENCY.CREATE", RestClient.HttpMethod.POST,
                RestUtil.convert(data, AgencyVendorModel.class, conf)), AgencyVendorModel.class);
    }

    public ResponseContainer<AgencyVendorModel> createAgency(AgencyVendorModel data) {
        return new ResponseContainer<>(client.processRequest("AGENCY.CREATE", RestClient.HttpMethod.POST,
                data), AgencyVendorModel.class);
    }


    public AgencyVendorModel putAgency(AgencyVendorModel agency, String agencyCode) {
        return client.processRequest("AGENCY.PUT", RestClient.HttpMethod.PUT, agency,
                InlineRequestContext.builder().pathParam("code", agencyCode).build())
                .readEntity(AgencyVendorModel.class);
    }

    public AddressDataModel putAgency(TestData address, String agencyCode) {
        return client.processRequest("AGENCY.PUT", RestClient.HttpMethod.PUT, address,
                InlineRequestContext.builder().pathParam("code", agencyCode).build())
                .readEntity(AddressDataModel.class);
    }
}
