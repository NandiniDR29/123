/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.integration.model.findProvider;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntegrationFindProviderModel extends RestError {

    private String dateOfService;
    private String assignmentOfBenefit;
    private String associationFound;
    private String callSuccessful;
    private String eftFound;
    private String focusReviewExist;
    private String hcfaSanctioned;
    private String licenseFound;
    private String networkCompliant;
    private String paymentOfficeFound;
    private String providerBusinessFound;
    private String serviceOfficeFound;
    private String specialtyFound;
    private LicenseModel license;
    private List<NetworkProductsListModel> networkProductList;
    private ServiceOfficeAddressModel paymentOfficeAddress;
    private ProviderBusinessModel providerBusiness;
    private ServiceOfficeModel serviceOffice;
    private List<SpecialityListModel> specialtyList;

    public String getDateOfService() {
        return dateOfService;
    }

    public void setDateOfService(String dateOfService) {
        this.dateOfService = dateOfService;
    }

    public String getAssignmentOfBenefit() {
        return assignmentOfBenefit;
    }

    public void setAssignmentOfBenefit(String assignmentOfBenefit) {
        this.assignmentOfBenefit = assignmentOfBenefit;
    }

    public String getAssociationFound() {
        return associationFound;
    }

    public void setAssociationFound(String associationFound) {
        this.associationFound = associationFound;
    }

    public String getCallSuccessful() {
        return callSuccessful;
    }

    public void setCallSuccessful(String callSuccessful) {
        this.callSuccessful = callSuccessful;
    }

    public String getEftFound() {
        return eftFound;
    }

    public void setEftFound(String eftFound) {
        this.eftFound = eftFound;
    }

    public String getFocusReviewExist() {
        return focusReviewExist;
    }

    public void setFocusReviewExist(String focusReviewExist) {
        this.focusReviewExist = focusReviewExist;
    }

    public String getHcfaSanctioned() {
        return hcfaSanctioned;
    }

    public void setHcfaSanctioned(String hcfaSanctioned) {
        this.hcfaSanctioned = hcfaSanctioned;
    }

    public String getLicenseFound() {
        return licenseFound;
    }

    public void setLicenseFound(String licenseFound) {
        this.licenseFound = licenseFound;
    }

    public String getNetworkCompliant() {
        return networkCompliant;
    }

    public void setNetworkCompliant(String networkCompliant) {
        this.networkCompliant = networkCompliant;
    }

    public String getPaymentOfficeFound() {
        return paymentOfficeFound;
    }

    public void setPaymentOfficeFound(String paymentOfficeFound) {
        this.paymentOfficeFound = paymentOfficeFound;
    }

    public String getProviderBusinessFound() {
        return providerBusinessFound;
    }

    public void setProviderBusinessFound(String providerBusinessFound) {
        this.providerBusinessFound = providerBusinessFound;
    }

    public String getServiceOfficeFound() {
        return serviceOfficeFound;
    }

    public void setServiceOfficeFound(String serviceOfficeFound) {
        this.serviceOfficeFound = serviceOfficeFound;
    }

    public String getSpecialtyFound() {
        return specialtyFound;
    }

    public void setSpecialtyFound(String specialtyFound) {
        this.specialtyFound = specialtyFound;
    }

    public LicenseModel getLicense() {
        return license;
    }

    public void setLicense(LicenseModel license) {
        this.license = license;
    }

    public List<NetworkProductsListModel> getNetworkProductList() {
        return networkProductList;
    }

    public void setNetworkProductList(List<NetworkProductsListModel> networkProductList) {
        this.networkProductList = networkProductList;
    }

    public ServiceOfficeAddressModel getPaymentOfficeAddress() {
        return paymentOfficeAddress;
    }

    public void setPaymentOfficeAddress(ServiceOfficeAddressModel paymentOfficeAddress) {
        this.paymentOfficeAddress = paymentOfficeAddress;
    }

    public ProviderBusinessModel getProviderBusiness() {
        return providerBusiness;
    }

    public void setProviderBusiness(ProviderBusinessModel providerBusiness) {
        this.providerBusiness = providerBusiness;
    }

    public ServiceOfficeModel getServiceOffice() {
        return serviceOffice;
    }

    public void setServiceOffice(ServiceOfficeModel serviceOffice) {
        this.serviceOffice = serviceOffice;
    }

    public List<SpecialityListModel> getSpecialtyList() {
        return specialtyList;
    }

    public void setSpecialtyList(List<SpecialityListModel> specialtyList) {
        this.specialtyList = specialtyList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        IntegrationFindProviderModel that = (IntegrationFindProviderModel) o;
        return Objects.equals(dateOfService, that.dateOfService) &&
                Objects.equals(assignmentOfBenefit, that.assignmentOfBenefit) &&
                Objects.equals(associationFound, that.associationFound) &&
                Objects.equals(callSuccessful, that.callSuccessful) &&
                Objects.equals(eftFound, that.eftFound) &&
                Objects.equals(focusReviewExist, that.focusReviewExist) &&
                Objects.equals(hcfaSanctioned, that.hcfaSanctioned) &&
                Objects.equals(licenseFound, that.licenseFound) &&
                Objects.equals(networkCompliant, that.networkCompliant) &&
                Objects.equals(paymentOfficeFound, that.paymentOfficeFound) &&
                Objects.equals(providerBusinessFound, that.providerBusinessFound) &&
                Objects.equals(serviceOfficeFound, that.serviceOfficeFound) &&
                Objects.equals(specialtyFound, that.specialtyFound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateOfService, assignmentOfBenefit, associationFound, callSuccessful, eftFound, focusReviewExist, hcfaSanctioned, licenseFound, networkCompliant, paymentOfficeFound, providerBusinessFound, serviceOfficeFound, specialtyFound);
    }
}
