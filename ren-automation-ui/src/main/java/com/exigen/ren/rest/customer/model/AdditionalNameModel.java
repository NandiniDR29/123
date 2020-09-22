/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer.model;

import com.exigen.istf.data.TestData;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.*;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdditionalNameModel extends RestError {

    public enum Type {INDIVIDUAL, BUSINESS}

    private int id;
    private String nameDba;
    private String firstName;
    private String middleName;
    private String lastName;
    private String designationCd;
    private String designationDescription;
    private Type type;
    private Suffix suffix;
    private Salutation salutation;

    public AdditionalNameModel() {
    }

    public AdditionalNameModel(TestData data) {
        setSalutation(data.getValue(CustomerConstants.AdditionalNamesTable.SALUTATION));
        setFirstName(data.getValue(CustomerConstants.AdditionalNamesTable.FIRST_NAME));
        setMiddleName(data.getValue(CustomerConstants.AdditionalNamesTable.MIDDLE_NAME));
        setLastName(data.getValue(CustomerConstants.AdditionalNamesTable.LAST_NAME));
        setSuffix(data.getValue(CustomerConstants.AdditionalNamesTable.SUFFIX));
        setDesignationCd(data.getValue(CustomerConstants.AdditionalNamesTable.DESIGNATION) == null ? null : data.getValue(CustomerConstants.AdditionalNamesTable.DESIGNATION).toUpperCase());
        setNameDba(data.getValue(CustomerConstants.AdditionalNamesTable.NAME_DBA));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonSetter("salutation")
    public void setSalutation(int salutation) {
        this.salutation = Salutation.getEntityByValue(salutation);
    }

    public void setSalutation(String salutation) {
        this.salutation = Salutation.getEntityByLabel(salutation);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setSuffix(String suffix) {
        this.suffix = Suffix.getEntityByLabel(suffix);
    }

    @JsonSetter("suffix")
    public void setSuffix(Integer value) {
        this.suffix = Suffix.getEntityByValue(value);
    }

    public String getDesignationCd() {
        return designationCd;
    }

    @JsonGetter("designationCd")
    public String getDesignationCdForJson() {
        return designationCd == null ? null : designationCd.toUpperCase();
    }

    public void setDesignationCd(String designationCd) {
        this.designationCd = designationCd;
    }

    public String getDesignationDescription() {
        return designationDescription;
    }

    public void setDesignationDescription(String designationDescription) {
        this.designationDescription = designationDescription;
    }

    public String getNameDba() {
        return nameDba;
    }

    public void setNameDba(String nameDba) {
        this.nameDba = nameDba;
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

        AdditionalNameModel that = (AdditionalNameModel) o;

        if (salutation != null ? !salutation.equals(that.salutation) : that.salutation != null) {
            return false;
        }
        if (nameDba != null ? !nameDba.equals(that.nameDba) : that.nameDba != null) {
            return false;
        }
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) {
            return false;
        }
        if (middleName != null ? !middleName.equals(that.middleName) : that.middleName != null) {
            return false;
        }
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) {
            return false;
        }
        if (suffix != null ? !suffix.equals(that.suffix) : that.suffix != null) {
            return false;
        }
        if (designationCd != null ? !designationCd.equals(that.designationCd) : that.designationCd != null) {
            return false;
        }
        if (designationDescription != null ? !designationDescription.equals(that.designationDescription) : that.designationDescription != null) {
            return false;
        }
        return type != null ? type.equals(that.type) : that.type != null;
    }

    @Override
    public int hashCode() {
        int result = salutation != null ? salutation.hashCode() : 0;
        result = 31 * result + (nameDba != null ? nameDba.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (suffix != null ? suffix.hashCode() : 0);
        result = 31 * result + (designationCd != null ? designationCd.hashCode() : 0);
        result = 31 * result + (designationDescription != null ? designationDescription.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AdditionalName{" +
                "salutation='" + salutation + '\'' +
                ", nameDba='" + nameDba + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", suffix='" + suffix + '\'' +
                ", designationCd='" + designationCd + '\'' +
                ", designationDescription='" + designationDescription + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @JsonIgnoreType
    private enum Salutation {
        DOCTOR("Doctor", 1),
        ;

        private String label;
        private Integer value;

        Salutation(String label, int value) {
            this.label = label;
            this.value = value;
        }

        public String getUIName() {
            return label;
        }

        public Integer getValue() {
            return value;
        }

        public static Salutation getEntityByValue(Integer value) {
            return Arrays.stream(Salutation.values()).filter(ent -> ent.getValue().equals(value)).findFirst().orElse(null);
        }

        public static Salutation getEntityByLabel(String label) {
            return Arrays.stream(Salutation.values()).filter(ent -> ent.getUIName().equals(label)).findFirst().orElse(null);
        }
    }

    @JsonIgnore
    public String getUISalutation() {
        return salutation.getUIName();
    }

    @JsonGetter("salutation")
    public Integer getSalutationNumValue() {
        return salutation == null ? null : salutation.getValue();
    }

    @JsonIgnoreType
    private enum Suffix {
        JR("Jr.", 1);

        private String label;
        private Integer value;

        Suffix(String label, Integer value) {
            this.label = label;
            this.value = value;
        }

        public String getUIName() {
            return label;
        }

        public Integer getValue() {
            return value;
        }

        public static Suffix getEntityByValue(Integer value) {
            return Arrays.stream(Suffix.values()).filter(ent -> ent.getValue().equals(value)).findFirst().orElse(null);
        }

        public static Suffix getEntityByLabel(String label) {
            return Arrays.stream(Suffix.values()).filter(ent -> ent.getUIName().equals(label)).findFirst().orElse(null);
        }
    }

    @JsonIgnore
    public String getUISuffix() {
        return suffix.getUIName();
    }

    @JsonGetter("suffix")
    public Integer getSuffixNumValue() {
        return suffix == null ? null : suffix.getValue();
    }
}
