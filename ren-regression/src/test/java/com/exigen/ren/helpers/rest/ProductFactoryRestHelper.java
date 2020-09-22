/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.helpers.rest;

import com.exigen.istf.data.DataProviderFactory;
import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.rest.ResponseWrapper;
import com.exigen.ren.rest.JsonHelper;
import com.exigen.ren.rest.productfactory.ProductFactoryRestService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.util.Arrays;

public class ProductFactoryRestHelper extends RestHelper{

    private static ProductFactoryRestService productFactoryRestService = new ProductFactoryRestService();

    public static String getProducerInfoInstanceName(TestData data) {
        ResponseWrapper responseWrapper = productFactoryRestService.postProductOperation(data, "GET.ALL");
        String response = responseWrapper.getResponse().readEntity(String.class);
        String result = "";
        String policyNumberFromResponse = JsonHelper.getValue("$..policyNumber", responseWrapper).trim();
        if (!policyNumberFromResponse.equals(data.getValue("entityRefNo").trim())) {
            throw new IstfException(String.format("Incorrect get-all operation result. \n Requested for [%1$s] but received for [%2$s]", data.getValue("entityRefNo"), policyNumberFromResponse));
        }
        JSONObject jsonObject = new JSONObject(response);
        JSONArray operationsJsonArray = (JSONArray) jsonObject.get("operations");
        JSONObject operationJsonObject = (JSONObject) operationsJsonArray.get(0);
        JSONObject responseDataObject = (JSONObject) operationJsonObject.get("responseData");
        JSONObject metadataJsonObject = (JSONObject) responseDataObject.get("metadata");
        for (String instanceName : metadataJsonObject.keySet()) {
            JSONObject instance = (JSONObject) metadataJsonObject.get(instanceName);
            if ("ProducerInfo".equals(instance.get("referenceName"))) {
                result = instanceName;
            }
        }
        return result;
    }

    public static int postEndorsementRequest(TestData data) {
        TestData requestData = DataProviderFactory.emptyData().adjust("actionCd", "endorseWithWorkspace")
                .adjust("productCd", data.getValue("productCd"))
                .adjust("entityRefNo", data.getValue("policyNumber"))
                .adjust("entityType", "policy")
                .adjust("operations", Arrays.asList(
                        DataProviderFactory.dataOf("id", "0",
                                "operation", "add-or-update",
                                "referenceName", "PolicyEndorseAction",
                                "requestData", DataProviderFactory.dataOf("endorsementReason", "TBD1",
                                        "endorsementDate", data.getValue("endorsementDate"))),
                        DataProviderFactory.dataOf("id", "1",
                                "operation", "executeActionComponent",
                                "referenceName", "PolicyEndorseAction")
                        ));
        Response responseWrapper = productFactoryRestService.postProductOperation(requestData);
        return responseWrapper.getStatus();
    }

    public static int postCancelNoticeRequest(TestData data) {
        TestData requestData = DataProviderFactory.emptyData().adjust("actionCd", "cancelNotice")
                .adjust("productCd", data.getValue("productCd"))
                .adjust("entityRefNo", data.getValue("policyNumber"))
                .adjust("operations", Arrays.asList(
                        DataProviderFactory.dataOf("id", "add-or-update_PolicyCancelNoticeAction",
                                "operation", "add-or-update",
                                "referenceName", "PolicyCancelNoticeAction",
                                "requestData", DataProviderFactory.dataOf("policyTxInfo.daysOfNotice", data.getValue("policyTxInfo.daysOfNotice"),
                                        "policyTxInfo.txReasonCd", "1",
                                        "policyTxInfo.printNotice", "false",
                                        "policyTxInfo.supportingData", "string")),
                        DataProviderFactory.dataOf("id", "executeActionComponent_PolicyCancelNoticeAction",
                                "operation", "executeActionComponent",
                                "referenceName", "PolicyCancelNoticeAction")
                ));
        Response responseWrapper = productFactoryRestService.postProductOperation(requestData);
        return responseWrapper.getStatus();
    }

    public static ProductFactoryRestService getProductFactoryRestService() {
        return productFactoryRestService;
    }


}
