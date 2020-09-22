/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.efolder.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDocumentThumbnailModel extends Model {
    private String errorCode;
    private String message;

    public void setErrorCode(String errorCode){
        this.errorCode = errorCode;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getErrorCode(){
        return this.errorCode;
    }

    public String getMessage(){
        return this.message;
    }
}
