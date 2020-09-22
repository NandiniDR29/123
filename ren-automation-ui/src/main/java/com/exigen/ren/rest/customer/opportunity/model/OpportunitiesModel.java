/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer.opportunity.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpportunitiesModel extends Model {
    private Integer opportunityId;
    private String description;
    private String opportunityChannelCd;
    private String opportunityLikelihoodCd;
    private String status;
    private Date dateCreated;
    private Date dateModified;
    private String customerNumber;
    private Integer campaignId;
    private PotentialModel potential;
    private String closeReason;
    private String closeReasonDescription;

    public OpportunitiesModel() {
    }

    public OpportunitiesModel(OpportunitiesModel opportunitiesModel) {
       this.opportunityId = opportunitiesModel.getOpportunityId();
        this.description = opportunitiesModel.getDescription();
        this.opportunityChannelCd = opportunitiesModel.getOpportunityChannelCd();
        this.opportunityLikelihoodCd = opportunitiesModel.getOpportunityLikelihoodCd();
        this.status = opportunitiesModel.getStatus();
        this.dateCreated = opportunitiesModel.getDateCreated();
        this.dateModified = opportunitiesModel.getDateModified();
        this.customerNumber = opportunitiesModel.getCustomerNumber();
        this.campaignId = opportunitiesModel.getCampaignId();
        this.potential = opportunitiesModel.getPotential();
        this.closeReason = opportunitiesModel.getCloseReason();
        this.closeReasonDescription = opportunitiesModel.getCloseReasonDescription();
    }

    public void setOpportunityId(Integer opportunityId) {
        this.opportunityId = opportunityId;
    }

    public Integer getOpportunityId() {
        return opportunityId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setOpportunityChannelCd(String opportunityChannelCd) {
        this.opportunityChannelCd = opportunityChannelCd;
    }

    public String getOpportunityChannelCd() {
        return opportunityChannelCd;
    }

    public void setOpportunityLikelihoodCd(String opportunityLikelihoodCd) {
        this.opportunityLikelihoodCd = opportunityLikelihoodCd;
    }

    public String getOpportunityLikelihoodCd() {
        return opportunityLikelihoodCd;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }

    public Integer getCampaignId() {
        return campaignId;
    }

    public void setPotential(PotentialModel potential) {
        this.potential = potential;
    }

    public PotentialModel getPotential() {
        return potential;
    }

    public void setCloseReason(String closeReason) {
        this.closeReason = closeReason;
    }

    public String getCloseReason() {
        return closeReason;
    }

    public void setCloseReasonDescription(String closeReasonDescription) {
        this.closeReasonDescription = closeReasonDescription;
    }

    public String getCloseReasonDescription() {
        return closeReasonDescription;
    }

}