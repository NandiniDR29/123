package com.exigen.ren.rest.salesforce.model;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesforceOpportunityModel extends RestError {

    @JsonProperty("Id")
    private String id;
    @JsonProperty("AccountId")
    private String accountName;
    @JsonProperty("CloseDate")
    private String closeDate;
    @JsonProperty("Date_Due__c")
    private String dateDueOpp;
    @JsonProperty("Effective_Date__c")
    private String effectiveDate;
    @JsonProperty("Name")
    private String opportunityName;
    @JsonProperty("Opportunity_Type__c")
    private String opportunityType;
    @JsonProperty("StageName")
    private String stage;
    @JsonProperty("Sales_Support_Contact__c")
    private String salesSupportContact;
    @JsonProperty("Total_Eligible__c")
    private String totalEligible;
    @JsonProperty("Date_Received__c")
    private String dateReceived;
    @JsonProperty("EIS_Case_ID__c")
    private String eisCaseId;
    @JsonProperty("Opportunity_Owner_Temp__c")
    private String opportunityOwner;
    @JsonProperty("OwnerId")
    private String ownerID;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public String getDateDueOpp() {
        return dateDueOpp;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public String getOpportunityName() {
        return opportunityName;
    }

    public String getOpportunityType() {
        return opportunityType;
    }

    public String getStage() {
        return stage;
    }

    public String getSalesSupportContact() {
        return salesSupportContact;
    }

    public String getTotalEligible() {
        return totalEligible;
    }

    public String getDateReceived() {
        return dateReceived;
    }

    public String getEisCaseId() {
        return eisCaseId;
    }

    public String getOpportunityOwner() {
        return opportunityOwner;
    }

    public String getOpportunityOwnerID() {
        return ownerID;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
    }

    public void setDateDueOpp(String dateDueOpp) {
        this.dateDueOpp = dateDueOpp;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public void setOpportunityName(String opportunityName) {
        this.opportunityName = opportunityName;
    }

    public void setOpportunityType(String opportunityType) {
        this.opportunityType = opportunityType;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public void setSalesSupportContact(String salesSupportContact) {
        this.salesSupportContact = salesSupportContact;
    }

    public void setTotalEligible(String totalEligible) {
        this.totalEligible = totalEligible;
    }

    public void setDateReceived(String dateReceived) {
        this.dateReceived = dateReceived;
    }

    public void setEisCaseId(String eisCaseId) {
        this.eisCaseId = eisCaseId;
    }

    public void setOpportunityOwner(String eisCaseId) {
        this.opportunityOwner = eisCaseId;
    }

    public SalesforceOpportunityModel() {
    }

    public String setOpportunityOwnerID() {
        return ownerID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        SalesforceOpportunityModel that = (SalesforceOpportunityModel) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(accountName, that.accountName) &&
                Objects.equals(closeDate, that.closeDate) &&
                Objects.equals(dateDueOpp, that.dateDueOpp) &&
                Objects.equals(effectiveDate, that.effectiveDate) &&
                Objects.equals(opportunityName, that.opportunityName) &&
                Objects.equals(opportunityType, that.opportunityType) &&
                Objects.equals(stage, that.stage) &&
                Objects.equals(salesSupportContact, that.salesSupportContact) &&
                Objects.equals(totalEligible, that.totalEligible) &&
                Objects.equals(dateReceived, that.dateReceived) &&
                Objects.equals(eisCaseId, that.eisCaseId) &&
                Objects.equals(ownerID, that.ownerID) &&
                Objects.equals(opportunityOwner, that.opportunityOwner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, accountName, closeDate, dateDueOpp, effectiveDate, opportunityName, opportunityType, stage, salesSupportContact, totalEligible, dateReceived, eisCaseId, opportunityOwner, ownerID);
    }
}
