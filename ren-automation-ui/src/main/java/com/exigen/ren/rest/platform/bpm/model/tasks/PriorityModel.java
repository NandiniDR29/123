/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.bpm.model.tasks;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.exigen.istf.data.TestData;

public class PriorityModel extends Model {

    private String key;
    private String displayValue;

    public PriorityModel() {
    }

    public PriorityModel(TestData testData) {
        this.key = testData.getValue("key");
        this.displayValue = testData.getValue("displayValue");
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PriorityModel priorityModel = (PriorityModel) o;

        if (key != null ? !key.equals(priorityModel.key) : priorityModel.key != null) {
            return false;
        }
        return displayValue != null ? displayValue.equals(priorityModel.displayValue) : priorityModel.displayValue == null;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (displayValue != null ? displayValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Priority{" +
                "key='" + key + '\'' +
                ", displayValue='" + displayValue + '\'' +
                '}';
    }
}
