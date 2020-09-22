/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.policy;

import com.exigen.ipb.eisa.ws.rest.RestClient;
import com.exigen.ipb.eisa.ws.rest.conf.client.ConfigurationFactory;
import com.exigen.ipb.eisa.ws.rest.conf.client.JsonConfiguration;
import com.exigen.ipb.eisa.ws.rest.conf.metadata.InlineRequestContext;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.policy.model.PolicyInfoModel;
import com.exigen.ren.rest.policy.model.TransactionInfoModel;

import javax.ws.rs.core.GenericType;
import java.util.List;
import java.util.Map;

public class PolicyRestService {

    private RestClient client;

    public enum SortBy {
        TRANSACTION_EFFECTIVE_DATE("transactionEffectiveDate"), EFFECTIVE_DATE("effectiveDate"), EXPIRATION_DATE("expirationDate");

        private final String sortByValue;

        SortBy(final String value) { sortByValue = value;}

        public String toString() {return sortByValue;}
    }

    public PolicyRestService() {
       client = new RestClient("policy-rs", ConfigurationFactory.get(JsonConfiguration.class));
    }

    public ResponseContainer<List<PolicyInfoModel>> getPolicyInfo(String policyNumber) {
        return new ResponseContainer<>(client.processRequest("POLICY.GET", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().queryParam("policyNumber", policyNumber).build()), new GenericType<List<PolicyInfoModel>>(){});
    }

    public ResponseContainer<List<PolicyInfoModel>> getQuoteInfo(String policyNumber) {
        return new ResponseContainer<>(client.processRequest("QUOTE.GET", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().queryParam("policyNumber", policyNumber).build()), new GenericType<List<PolicyInfoModel>>(){});
    }

    public ResponseContainer<List<TransactionInfoModel>> getTransactionsInfo(Map<String, String> searchValues) {
        return new ResponseContainer<>(client.processRequest("TRANSACTIONS.GET", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().queryParams(searchValues).build()), new GenericType<List<TransactionInfoModel>>(){});
    }
}
