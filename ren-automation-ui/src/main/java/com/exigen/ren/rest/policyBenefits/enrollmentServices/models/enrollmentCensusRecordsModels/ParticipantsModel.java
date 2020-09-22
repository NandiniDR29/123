package com.exigen.ren.rest.policyBenefits.enrollmentServices.models.enrollmentCensusRecordsModels;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipantsModel extends Model {
    private String personOID;
    private String roleName;
    private String relationshipToInsured;

    public String getPersonOID() {
        return personOID;
    }

    public void setPersonOID(String personOID) {
        this.personOID = personOID;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRelationshipToInsured() {
        return relationshipToInsured;
    }

    public void setRelationshipToInsured(String relationshipToInsured) {
        this.relationshipToInsured = relationshipToInsured;
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
        ParticipantsModel that = (ParticipantsModel) o;
        return Objects.equals(personOID, that.personOID) &&
                Objects.equals(roleName, that.roleName) &&
                Objects.equals(relationshipToInsured, that.relationshipToInsured);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), personOID, roleName, relationshipToInsured);
    }
}
