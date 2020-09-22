/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.bpm.model.tasks;

import com.exigen.istf.data.TestData;
import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignmentModel extends RestError {

    private String assignmentType;
    private String assignmentValue;
    private String displayValue;
    private String note;
    private String queueCode;
    private String queueDisplayValue;
    private String userName;
    private String userDisplayValue;

    public AssignmentModel() {
    }

    public AssignmentModel(String queueCode, String userName) {
        this.queueCode = queueCode;
        this.userName = userName;
    }

    public AssignmentModel(TestData testData) {
        this.assignmentType = testData.getValue("assignmentType");
        this.assignmentValue = testData.getValue("assignmentValue");
        this.displayValue = testData.getValue("displayValue");
        this.note = testData.getValue("note");
        this.queueCode = testData.getValue("queueCode");
        this.queueDisplayValue = testData.getValue("queueDisplayValue");
        this.userName = testData.getValue("userName");
        this.userDisplayValue = testData.getValue("userDisplayValue");
    }

    public AssignmentModel fromQueueCode(String queueCode) {
        this.queueCode = queueCode;
        return this;
    }

    public AssignmentModel fromUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
    }

    public String getAssignmentValue() {
        return assignmentValue;
    }

    public void setAssignmentValue(String assignmentValue) {
        this.assignmentValue = assignmentValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getQueueCode() {
        return queueCode;
    }

    public void setQueueCode(String queueCode) {
        this.queueCode = queueCode;
    }

    public String getQueueDisplayValue() {
        return queueDisplayValue;
    }

    public void setQueueDisplayValue(String queueDisplayValue) {
        this.queueDisplayValue = queueDisplayValue;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayValue() {
        return userDisplayValue;
    }

    public void setUserDisplayValue(String userDisplayValue) {
        this.userDisplayValue = userDisplayValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || !(o instanceof RestError)) {
            return false;
        }

        if (getErrorCode() != null) {
            return super.equals(o);
        }

        if (getClass() != o.getClass()) {
            return false;
        }

        AssignmentModel that = (AssignmentModel) o;

        if (assignmentType != null ? !assignmentType.equals(that.assignmentType) : that.assignmentType != null) {
            return false;
        }
        if (assignmentValue != null ? !assignmentValue.equals(that.assignmentValue) : that.assignmentValue != null) {
            return false;
        }
        if (displayValue != null ? !displayValue.equals(that.displayValue) : that.displayValue != null) {
            return false;
        }
        if (note != null ? !note.equals(that.note) : that.note != null) {
            return false;
        }
        if (queueCode != null ? !queueCode.equals(that.queueCode) : that.queueCode != null) {
            return false;
        }
        if (queueDisplayValue != null ? !queueDisplayValue.equals(that.queueDisplayValue) : that.queueDisplayValue != null) {
            return false;
        }
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) {
            return false;
        }
        return userDisplayValue != null ? userDisplayValue.equals(that.userDisplayValue) : that.userDisplayValue == null;
    }

    @Override
    public int hashCode() {
        int result = assignmentType != null ? assignmentType.hashCode() : 0;
        result = 31 * result + (assignmentValue != null ? assignmentValue.hashCode() : 0);
        result = 31 * result + (displayValue != null ? displayValue.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        result = 31 * result + (queueCode != null ? queueCode.hashCode() : 0);
        result = 31 * result + (queueDisplayValue != null ? queueDisplayValue.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (userDisplayValue != null ? userDisplayValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "assignmentType='" + assignmentType + '\'' +
                ", assignmentValue='" + assignmentValue + '\'' +
                ", displayValue='" + displayValue + '\'' +
                ", note='" + note + '\'' +
                ", queueCode='" + queueCode + '\'' +
                ", queueDisplayValue='" + queueDisplayValue + '\'' +
                ", userName='" + userName + '\'' +
                ", userDisplayValue='" + userDisplayValue + '\'' +
                '}';
    }
}
