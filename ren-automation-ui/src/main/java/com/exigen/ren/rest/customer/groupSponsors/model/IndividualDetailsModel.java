/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer.groupSponsors.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndividualDetailsModel extends Model {

    private String firstName;
    private String lastName;
    private String middleName;
    private String title;
    private String suffix;
    private String nickname;
    private String birthDate;
    private String genderCd;
    private String maritalStatusCd;
    private String taxId;
    private boolean deceased;
    private String deathDate;
    private boolean deathNotificationReceived;
    private String occupationCd;
    private String occupationDescription;
    private String tobaccoCd;
    private String citizenshipCd;
    private List<String> interests;
    private List<String> disabilities;
    private String designationCd;
    private String designationDescription;
    private ExtensionFieldsModel extensionFields;
    private boolean associateEmployments;
    private boolean associateProviders;
    private boolean associateBusinessEntity;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthDate() {
        return this.birthDate;
    }

    public void setGenderCd(String genderCd) {
        this.genderCd = genderCd;
    }

    public String getGenderCd() {
        return this.genderCd;
    }

    public void setMaritalStatusCd(String maritalStatusCd) {
        this.maritalStatusCd = maritalStatusCd;
    }

    public String getMaritalStatusCd() {
        return this.maritalStatusCd;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getTaxId() {
        return this.taxId;
    }

    public void setDeceased(boolean deceased) {
        this.deceased = deceased;
    }

    public boolean getDeceased() {
        return this.deceased;
    }

    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    public String getDeathDate() {
        return this.deathDate;
    }

    public void setDeathNotificationReceived(boolean deathNotificationReceived) {
        this.deathNotificationReceived = deathNotificationReceived;
    }

    public boolean getDeathNotificationReceived() {
        return this.deathNotificationReceived;
    }

    public void setOccupationCd(String occupationCd) {
        this.occupationCd = occupationCd;
    }

    public String getOccupationCd() {
        return this.occupationCd;
    }

    public void setOccupationDescription(String occupationDescription) {
        this.occupationDescription = occupationDescription;
    }

    public String getOccupationDescription() {
        return this.occupationDescription;
    }

    public void setTobaccoCd(String tobaccoCd) {
        this.tobaccoCd = tobaccoCd;
    }

    public String getTobaccoCd() {
        return this.tobaccoCd;
    }

    public void setCitizenshipCd(String citizenshipCd) {
        this.citizenshipCd = citizenshipCd;
    }

    public String getCitizenshipCd() {
        return this.citizenshipCd;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public List<String> getInterests() {
        return this.interests;
    }

    public void setDisabilities(List<String> disabilities) {
        this.disabilities = disabilities;
    }

    public List<String> getDisabilities() {
        return this.disabilities;
    }

    public void setDesignationCd(String designationCd) {
        this.designationCd = designationCd;
    }

    public String getDesignationCd() {
        return this.designationCd;
    }

    public void setDesignationDescription(String designationDescription) {
        this.designationDescription = designationDescription;
    }

    public String getDesignationDescription() {
        return this.designationDescription;
    }

    public void setExtensionFields(ExtensionFieldsModel extensionFields) {
        this.extensionFields = extensionFields;
    }

    public ExtensionFieldsModel getExtensionFields() {
        return this.extensionFields;
    }

    public void setAssociateEmployments(boolean associateEmployments) {
        this.associateEmployments = associateEmployments;
    }

    public boolean getAssociateEmployments() {
        return this.associateEmployments;
    }

    public void setAssociateProviders(boolean associateProviders) {
        this.associateProviders = associateProviders;
    }

    public boolean getAssociateProviders() {
        return this.associateProviders;
    }

    public void setAssociateBusinessEntity(boolean associateBusinessEntity) {
        this.associateBusinessEntity = associateBusinessEntity;
    }

    public boolean getAssociateBusinessEntity() {
        return this.associateBusinessEntity;
    }
}
