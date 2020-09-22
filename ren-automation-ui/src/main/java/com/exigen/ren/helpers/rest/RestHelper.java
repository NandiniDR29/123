/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.helpers.rest;

import com.exigen.istf.data.DataProviderFactory;
import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;

import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;

public class RestHelper {

    protected static final String REQUESTS_TD_KEY = "Requests";
    protected static final String RESPONSES_TD_KEY = "Responses";

    public static TestData prepareTestDataForRestTest(TestData testData) {
        return DataProviderFactory.emptyData().adjust("testDataNode", testData).resolveLinks();
    }

    public static Boolean convertStringDataToRestBoolean(String value) {
        if (value == null) {
            return null;
        }
        switch (value) {
            case (VALUE_YES):
                return true;
            case (VALUE_NO):
                return false;
            default:
                throw new IstfException(String.format("Value %s can not be converted to boolean value", value));
        }
    }

}
