package com.exigen.ren.rest.customer;

import com.exigen.ipb.eisa.ws.rest.RestClient;
import com.exigen.ipb.eisa.ws.rest.conf.client.ConfigurationFactory;
import com.exigen.ipb.eisa.ws.rest.conf.client.JsonConfiguration;
import com.exigen.ipb.eisa.ws.rest.conf.metadata.InlineRequestContext;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.CustomerModel;
import com.exigen.ren.rest.customer.model.RelationshipModel;
import com.fasterxml.jackson.databind.DeserializationFeature;

import javax.ws.rs.core.GenericType;
import java.util.List;

public class RenCustomerRestService {
    private RestClient client;
    private static final String CUSTOMER_NUMBER_KEY = "customerNumber";
    private JsonConfiguration conf;

    public RenCustomerRestService() {
        conf = ConfigurationFactory.get(JsonConfiguration.class);
        conf.getObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        client = new RestClient("ren-customercore-rs", conf);
    }

    public ResponseContainer<List<RelationshipModel>> getDerivedRelationship(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.DERIVED-RELATIONSHIPS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<List<RelationshipModel>>() {});
    }


    public ResponseContainer<CustomerModel> updatesContactsForCustomer(String customerNumber, CustomerModel customerModel) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.CONTACTS", RestClient.HttpMethod.PUT, customerModel,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<CustomerModel>() {
                });
    }

    public ResponseContainer<CustomerModel> updatesPaperlessForCustomer(String customerNumber, CustomerModel customerModel) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.PAPERLESS", RestClient.HttpMethod.PUT, customerModel,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<CustomerModel>() {
        });
    }

}
