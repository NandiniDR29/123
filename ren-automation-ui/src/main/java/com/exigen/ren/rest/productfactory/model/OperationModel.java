/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.rest.productfactory.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperationModel extends Model {
    private String id;
    private String operation;
    private String referenceName;
    private RequestDataModel requestData;

    public String getId() {
        return id;
    }

    public OperationModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getOperation() {
        return operation;
    }

    public OperationModel setOperation(String operation) {
        this.operation = operation;
        return this;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public OperationModel setReferenceName(String referenceName) {
        this.referenceName = referenceName;
        return this;
    }

    public RequestDataModel getRequestData() {
        return requestData;
    }

    public OperationModel setRequestData(RequestDataModel requestData) {
        this.requestData = requestData;
        return this;
    }
}
