package com.exigen.ren.rest.claim.model.dental.claiminfo;

import com.exigen.ipb.eisa.ws.rest.model.Model;

import java.util.Objects;

public class DentistModel extends Model {
    private String dentistID;
    private String state;
    private String inOutNetwork;
    private String netWorkName;
    private String pricingStrategyName;
    private String isFocusReviewExist;
    private String isInternationalProvider;

    public String getDentistID() {
        return dentistID;
    }

    public void setDentistID(String dentistID) {
        this.dentistID = dentistID;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getInOutNetwork() {
        return inOutNetwork;
    }

    public void setInOutNetwork(String inOutNetwork) {
        this.inOutNetwork = inOutNetwork;
    }

    public String getNetWorkName() {
        return netWorkName;
    }

    public void setNetWorkName(String netWorkName) {
        this.netWorkName = netWorkName;
    }

    public String getPricingStrategyName() {
        return pricingStrategyName;
    }

    public void setPricingStrategyName(String pricingStrategyName) {
        this.pricingStrategyName = pricingStrategyName;
    }

    public String getIsFocusReviewExist() {
        return isFocusReviewExist;
    }

    public void setIsFocusReviewExist(String isFocusReviewExist) {
        this.isFocusReviewExist = isFocusReviewExist;
    }

    public String getIsInternationalProvider() {
        return isInternationalProvider;
    }

    public void setIsInternationalProvider(String isInternationalProvider) {
        this.isInternationalProvider = isInternationalProvider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DentistModel that = (DentistModel) o;
        return Objects.equals(dentistID, that.dentistID) &&
                Objects.equals(state, that.state) &&
                Objects.equals(inOutNetwork, that.inOutNetwork) &&
                Objects.equals(netWorkName, that.netWorkName) &&
                Objects.equals(pricingStrategyName, that.pricingStrategyName) &&
                Objects.equals(isFocusReviewExist, that.isFocusReviewExist) &&
                Objects.equals(isInternationalProvider, that.isInternationalProvider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dentistID, state, inOutNetwork, netWorkName, pricingStrategyName, isFocusReviewExist, isInternationalProvider);
    }
}
