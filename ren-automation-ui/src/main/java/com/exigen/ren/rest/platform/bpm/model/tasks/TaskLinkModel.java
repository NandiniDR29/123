/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.bpm.model.tasks;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskLinkModel extends RestError {

    private List<String> targetTaskIds = new ArrayList<>();
    private HashMap<String, String> validationDetails = new HashMap<>();
    private String linkTypeCd;
    private List<TaskLinkModel> fail = null;
    private List<TaskLinkModel> success = null;

    public TaskLinkModel() {
    }

    public TaskLinkModel(String linkTypeCd, String... ids) {
        this.linkTypeCd = linkTypeCd;
        this.targetTaskIds.addAll(Arrays.asList(ids));
    }

    public String getLinkTypeCd() {
        return linkTypeCd;
    }

    public void setLinkTypeCd(String linkTypeCd) {
        this.linkTypeCd = linkTypeCd;
    }


    public List<String> getTargetTaskIds() {
        return targetTaskIds;
    }

    public void setTargetTaskIds(List<String> targetTaskIds) {
        this.targetTaskIds = targetTaskIds;
    }

    @JsonAnyGetter
    public HashMap<String, String> getValidationDetails() {
        return validationDetails;
    }

    @JsonAnySetter
    public void setValidationDetails(Object detailsObject) {
        if (detailsObject instanceof List) {
            this.validationDetails.putAll(((List<HashMap<String, String>>) detailsObject).get(0));
        }
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

        TaskLinkModel that = (TaskLinkModel) o;

        if (targetTaskIds != null ? !targetTaskIds.containsAll(that.targetTaskIds) : that.targetTaskIds != null) {
            return false;
        }
        return linkTypeCd != null ? linkTypeCd.equals(that.linkTypeCd) : that.linkTypeCd == null;
    }

    @Override
    public int hashCode() {
        int result = targetTaskIds != null ? targetTaskIds.hashCode() : 0;
        result = 31 * result + (linkTypeCd != null ? linkTypeCd.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TaskLink{" +
                "targetTaskIds='" + StringUtils.join(targetTaskIds, ",") + '\'' +
                ", linkTypeCd='" + linkTypeCd + '\'' +
                '}';
    }

    @JsonGetter("FAIL")
    public List<TaskLinkModel> getFail() {
        return fail;
    }

    @JsonSetter("FAIL")
    public void setFail(List<TaskLinkModel> fail) {
        this.fail = fail;
    }

    @JsonGetter("SUCCESS")
    public List<TaskLinkModel> getSuccess() {
        return success;
    }

    @JsonSetter("SUCCESS")
    public void setSuccess(List<TaskLinkModel> success) {
        this.success = success;
    }
}
