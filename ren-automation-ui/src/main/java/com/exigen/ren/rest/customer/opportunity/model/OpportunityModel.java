/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer.opportunity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpportunityModel extends OpportunitiesModel {
    private List<ProductsInfoModel> productsInfo;
    private List<String> opportunityAssociations;
    private OwnerModel owner;
    private ReferralModel referral;

    public OpportunityModel() {
    }

    public OpportunityModel(OpportunitiesModel opportunitiesModel) {
        super(opportunitiesModel);
    }

    public void setProductsInfo(List<ProductsInfoModel> productsInfo) {
        this.productsInfo = productsInfo;
    }

    public List<ProductsInfoModel> getProductsInfo() {
        return productsInfo;
    }

    public void setOpportunityAssociations(List<String> opportunityAssociations) {
        this.opportunityAssociations = opportunityAssociations;
    }

    public List<String> getOpportunityAssociations() {
        return opportunityAssociations;
    }

    public void setOwner(OwnerModel owner) {
        this.owner = owner;
    }

    public OwnerModel getOwner() {
        return owner;
    }

    public void setReferral(ReferralModel referral) {
        this.referral = referral;
    }

    public ReferralModel getReferral() {
        return referral;
    }

}