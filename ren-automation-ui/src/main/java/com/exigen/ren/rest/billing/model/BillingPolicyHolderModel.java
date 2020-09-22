/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.billing.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillingPolicyHolderModel extends Model {
    private String id;
    private String name;
    private String description;
    private String reasonCd;
    private List<String> holdTypesCds;
    private List<PolicyModel> policies;

    @JsonIgnore
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime effectiveDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime expirationDate;

    public String getName() {
        return name;
    }

    public BillingPolicyHolderModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public BillingPolicyHolderModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getReasonCd() {
        return reasonCd;
    }

    public BillingPolicyHolderModel setReasonCd(String reasonCd) {
        this.reasonCd = reasonCd;
        return this;
    }

    public List<String> getHoldTypesCds() {
        return holdTypesCds;
    }

    public BillingPolicyHolderModel setHoldTypesCds(List<String> holdTypesCds) {
        this.holdTypesCds = holdTypesCds;
        return this;
    }

    public List<PolicyModel> getPolicies() {
        return policies;
    }

    public BillingPolicyHolderModel setPolicies(List<PolicyModel> policies) {
        this.policies = policies;
        return this;
    }

    public LocalDateTime getEffectiveDate() {
        return effectiveDate;
    }

    public BillingPolicyHolderModel setEffectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
        return this;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public BillingPolicyHolderModel setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    public String getId() {
        return id;
    }

    public BillingPolicyHolderModel setId(String id) {
        this.id = id;
        return this;
    }
}
