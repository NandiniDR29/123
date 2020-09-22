/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest;

import com.exigen.istf.rest.ResponseWrapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import javax.ws.rs.core.Response;

public class JsonHelper {

    public static String getValue(String jsonPath, ResponseWrapper response) {
        DocumentContext jsonContext = JsonPath.parse(response.getResponse().readEntity(String.class));

        Object value = jsonContext.read(jsonPath);
        if (value instanceof net.minidev.json.JSONArray) {
            value = ((net.minidev.json.JSONArray) value).get(0);
        }

        return String.valueOf(value);
    }

    public static String getValue(String jsonPath, Response response) {
        DocumentContext jsonContext = JsonPath.parse(response.readEntity(String.class));

        Object value = jsonContext.read(jsonPath);
        if (value instanceof net.minidev.json.JSONArray) {
            value = ((net.minidev.json.JSONArray) value).get(0);
        }

        return String.valueOf(value);
    }


    public static boolean pathExist(String jsonPath, ResponseWrapper response) {
        DocumentContext jsonContext = JsonPath.parse(response.getResponse().readEntity(String.class));
        boolean pathExists = false;

        try {
            Object context = jsonContext.read(jsonPath);
            if (context instanceof net.minidev.json.JSONArray) {
                pathExists = ((net.minidev.json.JSONArray) context).size() > 0;
            }
        } catch (PathNotFoundException ex) {
        }

        return pathExists;
    }
}
