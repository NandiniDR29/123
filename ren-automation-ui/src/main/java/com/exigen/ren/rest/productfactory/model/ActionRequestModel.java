/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.rest.productfactory.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;

import java.util.List;

public class ActionRequestModel extends Model {
    private String actionCd;
    private String productCd;
    private String entityRefNo;
    private String entityType;
    private List<OperationModel> operations;

    public String getActionCd() {
        return actionCd;
    }

    public ActionRequestModel setActionCd(String actionCd) {
        this.actionCd = actionCd;
        return this;
    }

    public String getProductCd() {
        return productCd;
    }

    public ActionRequestModel setProductCd(String productCd) {
        this.productCd = productCd;
        return this;
    }

    public String getEntityRefNo() {
        return entityRefNo;
    }

    public ActionRequestModel setEntityRefNo(String entityRefNo) {
        this.entityRefNo = entityRefNo;
        return this;
    }

    public String getEntityType() {
        return entityType;
    }

    public ActionRequestModel setEntityType(String entityType) {
        this.entityType = entityType;
        return this;
    }

    public List<OperationModel> getOperations() {
        return operations;
    }

    public ActionRequestModel setOperations(List<OperationModel> operations) {
        this.operations = operations;
        return this;
    }
}
