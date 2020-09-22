/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.billing.model.billing_accounts_invoices;

import com.exigen.ren.rest.billing.model.billing_accounts_invoices_regenerate.BillingValue;
import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingAccountsInvoicesCoveragesParticipants extends RestError {

    private String participantId;
    private String fullName;
    private String certificateNumber;
    private BillingValue volume;
    private String rate;
    private String rateBasis;
    private BillingValue premium;


    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public BillingValue getVolume() {
        return volume;
    }

    public void setVolume(BillingValue volume) {
        this.volume = volume;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getRateBasis() {
        return rateBasis;
    }

    public void setRateBasis(String rateBasis) {
        this.rateBasis = rateBasis;
    }

    public BillingValue getPremium() {
        return premium;
    }

    public void setPremium(BillingValue premium) {
        this.premium = premium;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BillingAccountsInvoicesCoveragesParticipants that = (BillingAccountsInvoicesCoveragesParticipants) o;
        return Objects.equals(participantId, that.participantId) &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(certificateNumber, that.certificateNumber) &&
                Objects.equals(volume, that.volume) &&
                Objects.equals(rate, that.rate) &&
                Objects.equals(rateBasis, that.rateBasis) &&
                Objects.equals(premium, that.premium);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), participantId, fullName, certificateNumber, volume, rate, rateBasis, premium);
    }
}

