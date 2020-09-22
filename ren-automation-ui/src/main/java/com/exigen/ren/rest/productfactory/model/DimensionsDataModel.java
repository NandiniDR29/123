/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.productfactory.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;

import java.util.ArrayList;

public class DimensionsDataModel extends Model {
    private ArrayList<String> countryCd;
    private ArrayList<String> stateProvinceCode;

    public ArrayList<String> getCountryCd() {
        return countryCd;
    }

    public void setCountryCd(ArrayList<String> countryCd) {
        this.countryCd = countryCd;
    }

    public ArrayList<String> getStateProvinceCode() {
        return stateProvinceCode;
    }

    public void setStateProvinceCode(ArrayList<String> stateProvinceCode) {
        this.stateProvinceCode = stateProvinceCode;
    }
}
