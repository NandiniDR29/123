package com.exigen.ren.rest.claim.model.common.claimbody.claim;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PartyModel extends Model {
    private String firstName;
    private String lastName;
    private RelationToPrimaryInsured relationShipToInsuredCd;
    private String birthDt;
    private String oid;
    private String componentName;
    private PartyExtensionModel extension;
    private List<AddressModel> addresses;
    private List<PartyRoleModel> roles;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public RelationToPrimaryInsured getRelationShipToInsuredCd() {
        return relationShipToInsuredCd;
    }

    @JsonSetter("relationShipToInsuredCd")
    public void setRelationShipToInsuredCd(String relationShipToInsuredCd) {
        this.relationShipToInsuredCd = RelationToPrimaryInsured.getEntityByValue(relationShipToInsuredCd);
    }

    public String getBirthDt() {
        return birthDt;
    }

    public void setBirthDt(String birthDt) {
        this.birthDt = birthDt;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public List<AddressModel> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressModel> addresses) {
        this.addresses = addresses;
    }

    public PartyExtensionModel getExtension() {
        return extension;
    }

    public void setExtension(PartyExtensionModel extension) {
        this.extension = extension;
    }

    public List<PartyRoleModel> getRoles() {
        return roles;
    }

    public void setRoles(List<PartyRoleModel> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        PartyModel that = (PartyModel) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(relationShipToInsuredCd, that.relationShipToInsuredCd) &&
                Objects.equals(birthDt, that.birthDt) &&
                Objects.equals(oid, that.oid) &&
                Objects.equals(componentName, that.componentName) &&
                Objects.equals(extension, that.extension) &&
                Objects.equals(addresses, that.addresses) &&
                Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName, relationShipToInsuredCd, birthDt, oid, componentName, extension, addresses, roles);
    }

    @JsonIgnoreType
    public enum RelationToPrimaryInsured {
        CHILD_SON("Child - Son", "CHILD_SON");

        private String label;
        private String value;

        RelationToPrimaryInsured(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getUIName() {
            return label;
        }

        public String getValue() {
            return value;
        }

        public static RelationToPrimaryInsured getEntityByValue(String value) {
            return Arrays.stream(RelationToPrimaryInsured.values()).filter(ent -> ent.getValue().equals(value)).findFirst().orElse(null);
        }

    }

}
