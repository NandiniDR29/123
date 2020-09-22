package com.exigen.ren.rest.integration.model.findProvider;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetworkProductsListModel extends RestError {

    private String nonPar;
    private String participating;
    private String regionOverrideFound;
    private String nationalNetworkBrand;
    private String networkBrandId;
    private String networkBrandName;
    private ParticipationNetworkBrandPricing participationNetworkBrandPricing;

    public String getNonPar() {
        return nonPar;
    }

    public void setNonPar(String nonPar) {
        this.nonPar = nonPar;
    }

    public String getParticipating() {
        return participating;
    }

    public void setParticipating(String participating) {
        this.participating = participating;
    }

    public String getRegionOverrideFound() {
        return regionOverrideFound;
    }

    public void setRegionOverrideFound(String regionOverrideFound) {
        this.regionOverrideFound = regionOverrideFound;
    }

    public String getNationalNetworkBrand() {
        return nationalNetworkBrand;
    }

    public void setNationalNetworkBrand(String nationalNetworkBrand) {
        this.nationalNetworkBrand = nationalNetworkBrand;
    }

    public String getNetworkBrandId() {
        return networkBrandId;
    }

    public void setNetworkBrandId(String networkBrandId) {
        this.networkBrandId = networkBrandId;
    }

    public String getNetworkBrandName() {
        return networkBrandName;
    }

    public void setNetworkBrandName(String networkBrandName) {
        this.networkBrandName = networkBrandName;
    }

    public ParticipationNetworkBrandPricing getParticipationNetworkBrandPricing() {
        return participationNetworkBrandPricing;
    }

    public void setParticipationNetworkBrandPricing(ParticipationNetworkBrandPricing participationNetworkBrandPricing) {
        this.participationNetworkBrandPricing = participationNetworkBrandPricing;
    }
}

