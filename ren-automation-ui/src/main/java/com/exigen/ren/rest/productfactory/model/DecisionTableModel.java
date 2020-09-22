/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.productfactory.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;

public class DecisionTableModel extends Model {
    private String name;
    private DimensionsDataModel dimension;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DimensionsDataModel getDimension() {
        return dimension;
    }

    public void setDimension(DimensionsDataModel dimension) {
        this.dimension = dimension;
    }
}
