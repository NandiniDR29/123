/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.bpm.model.tasks;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TasksRequestModel extends Model {

    private String agencyCd;
    private String brandCd;
    private String entityType;
    private String userName;
    private String processKey;
    private String referenceId;
    private String name;
    private String id;
    private String dueTypeDate;
    private String dueDate;
    private String dueDateTime;
    private String dueDays;
    private String dueHours;
    private String dueMinutes;
    private String warningTypeDate;
    private String warningDate;
    private String warningDateTime;
    private String warningDays;
    private String warningHours;
    private String warningMinutes;
    @JsonProperty("taskDescription")
    private String description;
    private String priority;
    private String assignmentType;
    private String assignmentValue;
    private String queueCode;

    public String getAgencyCd() {
        return agencyCd;
    }

    public void setAgencyCd(String agencyCd) {
        this.agencyCd = agencyCd;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDueTypeDate() {
        return dueTypeDate;
    }

    public void setDueTypeDate(String dueTypeDate) {
        this.dueTypeDate = dueTypeDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueDateTime() {
        return dueDateTime;
    }

    public void setDueDateTime(String dueDateTime) {
        this.dueDateTime = dueDateTime;
    }

    public String getDueDays() {
        return dueDays;
    }

    public void setDueDays(String dueDays) {
        this.dueDays = dueDays;
    }

    public String getDueHours() {
        return dueHours;
    }

    public void setDueHours(String dueHours) {
        this.dueHours = dueHours;
    }

    public String getDueMinutes() {
        return dueMinutes;
    }

    public void setDueMinutes(String dueMinutes) {
        this.dueMinutes = dueMinutes;
    }

    public String getWarningTypeDate() {
        return warningTypeDate;
    }

    public void setWarningTypeDate(String warningTypeDate) {
        this.warningTypeDate = warningTypeDate;
    }

    public String getWarningDate() {
        return warningDate;
    }

    public void setWarningDate(String warningDate) {
        this.warningDate = warningDate;
    }

    public String getWarningDateTime() {
        return warningDateTime;
    }

    public void setWarningDateTime(String warningDateTime) {
        this.warningDateTime = warningDateTime;
    }

    public String getWarningDays() {
        return warningDays;
    }

    public void setWarningDays(String warningDays) {
        this.warningDays = warningDays;
    }

    public String getWarningHours() {
        return warningHours;
    }

    public void setWarningHours(String warningHours) {
        this.warningHours = warningHours;
    }

    public String getWarningMinutes() {
        return warningMinutes;
    }

    public void setWarningMinutes(String warningMinutes) {
        this.warningMinutes = warningMinutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
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

    public String getQueueCode() {
        return queueCode;
    }

    public void setQueueCode(String queueCode) {
        this.queueCode = queueCode;
    }

    public String getBrandCd() {
        return brandCd;
    }

    public void setBrandCd(String brandCd) {
        this.brandCd = brandCd;
    }
}
