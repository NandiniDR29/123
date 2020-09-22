package com.exigen.ren.rest.dxp.model;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployerGroupsModel extends RestError {

    private String groupCustomerNumber;
    private String legalName;
    private String legalId;
    private String billingAccountNumber;
    private String billingAccountName;
    private String groupType;
    private List<MasterPolicy> masterPolicies;


    public String getGroupCustomerNumber() {
        return groupCustomerNumber;
    }

    public void setGroupCustomerNumber(String groupCustomerNumber) {
        this.groupCustomerNumber = groupCustomerNumber;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getLegalId() {
        return legalId;
    }

    public void setLegalId(String legalId) {
        this.legalId = legalId;
    }

    public String getBillingAccountNumber() {
        return billingAccountNumber;
    }

    public void setBillingAccountNumber(String billingAccountNumber) {
        this.billingAccountNumber = billingAccountNumber;
    }

    public String getBillingAccountName() {
        return billingAccountName;
    }

    public void setBillingAccountName(String billingAccountName) {
        this.billingAccountName = billingAccountName;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public List<MasterPolicy> getMasterPolicies() {
        return masterPolicies;
    }

    public void setMasterPolicies(List<MasterPolicy> masterPolicies) {
        this.masterPolicies = masterPolicies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        EmployerGroupsModel that = (EmployerGroupsModel) o;
        return Objects.equals(groupCustomerNumber, that.groupCustomerNumber) &&
                Objects.equals(legalName, that.legalName) &&
                Objects.equals(legalId, that.legalId) &&
                Objects.equals(billingAccountNumber, that.billingAccountNumber) &&
                Objects.equals(billingAccountName, that.billingAccountName) &&
                Objects.equals(groupType, that.groupType) &&
                Objects.equals(masterPolicies, that.masterPolicies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), groupCustomerNumber, legalName, legalId, billingAccountNumber, billingAccountName, groupType, masterPolicies);
    }
}


