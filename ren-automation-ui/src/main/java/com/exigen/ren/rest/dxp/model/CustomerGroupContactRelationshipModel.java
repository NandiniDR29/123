package com.exigen.ren.rest.dxp.model;

import com.exigen.ren.rest.model.RestError;

import java.util.List;
import java.util.Objects;

public class CustomerGroupContactRelationshipModel extends RestError {
    private String contactRelationshipId;
    private String customerNumber;
    private List<String> serviceRoleCds;

    public String getContactRelationshipId() {
        return contactRelationshipId;
    }

    public void setContactRelationshipId(String contactRelationshipId) {
        this.contactRelationshipId = contactRelationshipId;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public List<String> getServiceRoleCds() {
        return serviceRoleCds;
    }

    public void setServiceRoleCds(List<String> serviceRoleCds) {
        this.serviceRoleCds = serviceRoleCds;
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
        CustomerGroupContactRelationshipModel that = (CustomerGroupContactRelationshipModel) o;
        return Objects.equals(contactRelationshipId, that.contactRelationshipId) &&
                Objects.equals(customerNumber, that.customerNumber) &&
                Objects.equals(serviceRoleCds, that.serviceRoleCds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contactRelationshipId, customerNumber, serviceRoleCds);
    }
}
