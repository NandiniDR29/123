/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.providers.model;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZipApplicabilityRulesModel extends RestError {
    private String id;
    private String feeScheduleId;
    private List<ZipRulesModel> rules;
    private ZipRangeModel zipRange;
    private TermModel term;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFeeScheduleId(String feeScheduleId) {
        this.feeScheduleId = feeScheduleId;
    }

    public String getFeeScheduleId() {
        return feeScheduleId;
    }

    public void setRules(List<ZipRulesModel> rules) {
        this.rules = rules;
    }

    public List<ZipRulesModel> getRules() {
        return rules;
    }

    public ZipRangeModel getZipRange() {
        return zipRange;
    }

    public void setZipRange(ZipRangeModel zipRange) {
        this.zipRange = zipRange;
    }

    public TermModel getTerm() {
        return term;
    }

    public void setTerm(TermModel term) {
        this.term = term;
    }
}
