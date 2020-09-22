/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer.model;

import com.exigen.istf.data.TestData;
import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.*;

import java.util.Arrays;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelationshipModel extends RestError {

    private Integer id;
    private String relationshipCustomerNumber;
    private RelationRole relationshipRole;
    private String relationshipDescription;
    private RelationshipExtensionFieldsModel extensionFields = new RelationshipExtensionFieldsModel();

    public RelationshipModel() {

    }

    public RelationshipModel(TestData data) {
        setRelationshipRole(data.getValue("Relationship to Customer"));
    }

    public RelationshipModel(RelationRole role) {
        this.relationshipRole = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRelationshipCustomerNumber() {
        return relationshipCustomerNumber;
    }

    public void setRelationshipCustomerNumber(String relationshipCustomerNumber) {
        this.relationshipCustomerNumber = relationshipCustomerNumber;
    }

    public void setRelationshipRole(String relationshipRole) {
        this.relationshipRole = RelationRole.getEntityByLabel(relationshipRole);
    }

    public String getRelationshipDescription() {
        return relationshipDescription;
    }

    public void setRelationshipDescription(String relationshipDescription) {
        this.relationshipDescription = relationshipDescription;
    }

    @JsonIgnoreType
    public enum RelationRole {
        RELATIVE("Relative", "RELATIVE"),
        EMPLOYER("Employer", "EMPLOYER"),
        CLAIMS("Claims", "CLAIMS"),
        DIVISION("Division", "DIVISION"),
        OTHER("Other", "OTHER"),
        CEO("CEO", "CEO"),
        PARTNER("Partner", "PARTNER"),
        CHILD_DAUGHTER("Child - Daughter", "CHILD_DAUGHTER"),
        COLLEAGUE("Colleague", "COLLEAGUE"),
        SERVICE_ROLES("Service Role", "SERVICE_ROLES"),
        MEMBER_COMPANY("Member Company", "MEMBER_COMPANY");

        private String label;
        private String value;

        RelationRole(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getUIName() {
            return label;
        }

        public String getValue() {
            return value;
        }

        public static RelationRole getEntityByValue(String value) {
            return Arrays.stream(RelationRole.values()).filter(ent -> ent.getValue().equals(value)).findFirst().orElse(null);
        }

        public static RelationRole getEntityByLabel(String label) {
            return Arrays.stream(RelationRole.values()).filter(ent -> ent.getUIName().equals(label)).findFirst().orElse(null);
        }
    }

    @JsonIgnore
    public String getUIRelationshipRole() {
        return relationshipRole.getUIName();
    }

    @JsonGetter("relationshipRole")
    public String getRelationshipRoleJsonValue() {
        return relationshipRole == null ? null : relationshipRole.getValue();
    }

    @JsonSetter("relationshipRole")
    public void setRelationshipRoleByJsonValue(String relatioshipRole) {
        this.relationshipRole = RelationRole.getEntityByValue(relatioshipRole);
    }

    public RelationshipExtensionFieldsModel getExtensionFields() {
        return extensionFields;
    }

    public void setExtensionFields(RelationshipExtensionFieldsModel extensionFields) {
        this.extensionFields = extensionFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        RelationshipModel that = (RelationshipModel) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(relationshipCustomerNumber, that.relationshipCustomerNumber) &&
                relationshipRole == that.relationshipRole &&
                Objects.equals(relationshipDescription, that.relationshipDescription) &&
                Objects.equals(extensionFields, that.extensionFields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, relationshipCustomerNumber, relationshipRole, relationshipDescription, extensionFields);
    }

    @Override
    public String toString() {
        return "RelationshipModel{" +
                "id=" + id +
                ", relationshipCustomerNumber='" + relationshipCustomerNumber + '\'' +
                ", relationshipRole=" + relationshipRole +
                ", relationshipDescription='" + relationshipDescription + '\'' +
                ", extensionFields=" + extensionFields +
                '}';
    }
}
