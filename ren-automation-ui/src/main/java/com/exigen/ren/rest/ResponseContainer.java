/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.exigen.ren.rest;

import com.exigen.istf.exceptions.IstfException;
import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Objects;

import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.PARTIAL_CONTENT;

public class ResponseContainer<T> {
    private Response response;
    private Object entity;
    private RestError error;

    public ResponseContainer(Response response, GenericType<T> genericType){
        this.response = response;
        try {
            this.entity = response.readEntity(genericType);
        } catch(ProcessingException pe) {
            processErrors(pe);
        }
    }

    public ResponseContainer(Response response, Class<T> clazz){
        this.response = response;
        try {
            this.entity = response.readEntity(clazz);
        } catch(ProcessingException pe) {
            processErrors(pe);
        }
    }

    public ResponseContainer(Response response, Object entity){
        this.response = response;
        try {
            this.entity = entity;
        } catch(ProcessingException pe) {
            processErrors(pe);
        }
    }

    private void processErrors(Exception exception) {
        if (Objects.nonNull(exception.getCause()) && exception.getCause() instanceof MismatchedInputException && !isResponseSuccess()) {
            try {
                this.error = response.readEntity(RestError.class);
            } catch(ProcessingException pe) {
                //Errors from Salesforce come as array
                List<RestError> errors = response.readEntity(new GenericType<List<RestError>>(){});
                this.error = new RestError();
                this.error.setErrors(errors);
            }
        } else {
            throw new IstfException("Unhandled deserialization exception", exception);
        }
    }

    /**
     * @return true if http status in range of 200
     */
    public boolean isResponseSuccess() {
        return PARTIAL_CONTENT.getStatusCode() >= response.getStatus() &&
                OK.getStatusCode() <= response.getStatus();
    }

    public Response getResponse(){
        return response;
    }

    @SuppressWarnings("unchecked")
    public T getModel() {
        return (T) entity;
    }

    public RestError getError(){
        return error!=null ? error : (RestError) entity;
    }
}
