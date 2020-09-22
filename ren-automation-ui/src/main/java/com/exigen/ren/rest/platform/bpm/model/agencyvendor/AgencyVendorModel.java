/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.bpm.model.agencyvendor;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgencyVendorModel extends Model {
    private String channelCd;
    private String code;
    private String commisionLevelType;
    private String locationTypeCd;
    private Boolean applicableForCommissionsExtract;
    private String paymentStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate effectiveDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;
    private String commissionStatementDeliveryMethod;
    private Boolean eligibleForAgencyBilling;
    private String nameAlternat;
    private String name;
    private String taxId;
    private AddressDataModel address;
    private AddressDataModel mailingAddress;

    public String getChannelCd() {
        return channelCd;
    }

    public Boolean getApplicableForCommissionsExtract() {
        return applicableForCommissionsExtract;
    }

    public void setApplicableForCommissionsExtract(Boolean applicableForCommissionsExtract) {
        this.applicableForCommissionsExtract = applicableForCommissionsExtract;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = LocalDate.parse(effectiveDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = LocalDate.parse(expirationDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getCommissionStatementDeliveryMethod() {
        return commissionStatementDeliveryMethod;
    }

    public void setCommissionStatementDeliveryMethod(String commissionStatementDeliveryMethod) {
        this.commissionStatementDeliveryMethod = commissionStatementDeliveryMethod;
    }

    public Boolean getEligibleForAgencyBilling() {
        return eligibleForAgencyBilling;
    }

    public void setEligibleForAgencyBilling(Boolean eligibleForAgencyBilling) {
        this.eligibleForAgencyBilling = eligibleForAgencyBilling;
    }

    public String getNameAlternat() {
        return nameAlternat;
    }

    public void setNameAlternat(String nameAlternat) {
        this.nameAlternat = nameAlternat;
    }

    public String getCode() {
        return code;
    }

    public String getCommisionLevelType() {
        return commisionLevelType;
    }

    public String getLocationTypeCd() {
        return locationTypeCd;
    }

    public String getName() {
        return name;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setChannelCd(String channelCd) {
        this.channelCd = channelCd;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setCommisionLevelType(String commisionLevelType) {
        this.commisionLevelType = commisionLevelType;
    }
    public void setLocationTypeCd(String locationTypeCd) {
        this.locationTypeCd = locationTypeCd;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public AddressDataModel getAddress() {
        return address;
    }

    public void setAddress(AddressDataModel address) {
        this.address = address;
    }

    public AddressDataModel getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(AddressDataModel mailingAddress) {
        this.mailingAddress = mailingAddress;
    }
}
