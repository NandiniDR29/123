/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.bpm.model.tasks;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkDefinitionModel extends RestError {

    private Integer id;
    private String sourceProcessKey;
    private String sourceTaskKey;
    private String targetProcessKey;

    LinkDefinitionModel(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSourceProcessKey() {
        return sourceProcessKey;
    }

    public void setSourceProcessKey(String sourceProcessKey) {
        this.sourceProcessKey = sourceProcessKey;
    }

    public String getSourceTaskKey() {
        return sourceTaskKey;
    }

    public void setSourceTaskKey(String sourceTaskKey) {
        this.sourceTaskKey = sourceTaskKey;
    }

    public String getTargetProcessKey() {
        return targetProcessKey;
    }

    public void setTargetProcessKey(String targetProcessKey) {
        this.targetProcessKey = targetProcessKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RestError)) {
            return false;
        }

        if (getErrorCode() != null) {
            return super.equals(o);
        }

        if (getClass() != o.getClass()) {
            return false;
        }

        LinkDefinitionModel that = (LinkDefinitionModel) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }

        if (sourceProcessKey != null ? !sourceProcessKey.equals(that.sourceProcessKey) : that.sourceProcessKey != null) {
            return false;
        }

        if (sourceTaskKey != null ? !sourceTaskKey.equals(that.sourceTaskKey) : that.sourceTaskKey != null) {
            return false;
        }

        return targetProcessKey != null ? targetProcessKey.equals(that.targetProcessKey) : that.targetProcessKey == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (sourceProcessKey != null ? sourceProcessKey.hashCode() : 0);
        result = 31 * result + (sourceTaskKey != null ? sourceTaskKey.hashCode() : 0);
        result = 31 * result + (targetProcessKey != null ? targetProcessKey.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LinkDefinition{" +
                "id='" + id + '\'' +
                ", sourceProcessKey='" + sourceProcessKey + '\'' +
                ", sourceTaskKey='" + sourceTaskKey + '\'' +
                ", targetProcessKey='" + targetProcessKey + '\'' +
                '}';
    }
}
