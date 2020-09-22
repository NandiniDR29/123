/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.policy.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public class PolicyInfoModel extends Model {

    @JsonIgnore
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private String policyNumber;
    private Integer customerNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime effectiveDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime expirationDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime transactionEffectiveDate;
    private String lob;
    private String description;
    private boolean expired;
    private Double totalPremium;
    private String currencyCode;
    private String transactionType;
    private Integer revisionNumber;
    private String productCode;
    private Integer productVersion;
    private String transactionTypeCd;
    private String lobCd;
    private String policyStatusCd;
    private String instanceName;
    private String rootEntityType;
    private Integer pendingRevisionNumber;
    private String timedPolicyStatusCd;
    private String brandCd;
    private String producerCd;
    private String subProducerCd;


    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setCustomerNumber(Integer customerNumber) {
        this.customerNumber = customerNumber;
    }

    public Integer getCustomerNumber() { return customerNumber; }

    public void setEffectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDateTime getEffectiveDate() {
        return effectiveDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setTransactionEffectiveDate(LocalDateTime transactionEffectiveDate) {
        this.transactionEffectiveDate = transactionEffectiveDate;
    }

    public LocalDateTime getTransactionEffectiveDate() {
        return transactionEffectiveDate;
    }

    public void setLob(String lob) {
        this.lob = lob;
    }

    public String getLob() {
        return lob;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean getExpired() {
        return expired;
    }

    public void setTotalPremium(Double totalPremium) {
        this.totalPremium = totalPremium;
    }

    public Double getTotalPremium() {
        return totalPremium;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setRevisionNumber(Integer revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public Integer getRevisionNumber() {
        return revisionNumber;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductVersion(Integer productVersion) {
        this.productVersion = productVersion;
    }

    public Integer getProductVersion() {
        return productVersion;
    }

    public void setTransactionTypeCd(String transactionTypeCd) {
        this.transactionTypeCd = transactionTypeCd;
    }

    public String getTransactionTypeCd() {
        return transactionTypeCd;
    }

    public void setLobCd(String lobCd) {
        this.lobCd = lobCd;
    }

    public String getLobCd() {
        return lobCd;
    }

    public void setPolicyStatusCd(String policyStatusCd) {
        this.policyStatusCd = policyStatusCd;
    }

    public String getPolicyStatusCd() {
        return policyStatusCd;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public boolean isExpired() {
        return expired;
    }

    public String getRootEntityType() {
        return rootEntityType;
    }

    public void setRootEntityType(String rootEntityType) {
        this.rootEntityType = rootEntityType;
    }

    public Integer getPendingRevisionNumber() {
        return pendingRevisionNumber;
    }

    public void setPendingRevisionNumber(Integer pendingRevisionNumber) {
        this.pendingRevisionNumber = pendingRevisionNumber;
    }

    public String getTimedPolicyStatusCd() {
        return timedPolicyStatusCd;
    }

    public void setTimedPolicyStatusCd(String timedPolicyStatusCd) {
        this.timedPolicyStatusCd = timedPolicyStatusCd;
    }

    public String getBrandCd() {
        return brandCd;
    }

    public void setBrandCd(String brandCd) {
        this.brandCd = brandCd;
    }

    public String getProducerCd() {
        return producerCd;
    }

    public void setProducerCd(String producerCd) {
        this.producerCd = producerCd;
    }

    public String getSubProducerCd() {
        return subProducerCd;
    }

    public void setSubProducerCd(String subProducerCd) {
        this.subProducerCd = subProducerCd;
    }
}
