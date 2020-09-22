package com.exigen.ren.rest.rating.model.gb_vs;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassCalculationModel extends Model {

    private String classId;
    private List<SubGroupModel> subGroups;

    public String getClassId() {
        return classId;
    }

    public ClassCalculationModel setClassId(String classId) {
        this.classId = classId;
        return this;
    }

    public List<SubGroupModel> getSubGroups() {
        return subGroups;
    }

    public ClassCalculationModel setSubGroups(List<SubGroupModel> subGroups) {
        this.subGroups = subGroups;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassCalculationModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ClassCalculationModel that = (ClassCalculationModel) o;
        return Objects.equals(classId, that.classId) &&
                Objects.equals(subGroups, that.subGroups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), classId, subGroups);
    }

    @Override
    public String toString() {
        return "ClassCalculationModel{" +
                "classId='" + classId + '\'' +
                ", subGroups=" + subGroups +
                '}';
    }
}
