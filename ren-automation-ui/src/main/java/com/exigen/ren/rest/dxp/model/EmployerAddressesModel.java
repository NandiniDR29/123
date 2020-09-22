package com.exigen.ren.rest.dxp.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployerAddressesModel extends Model {
    private String id;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String city;
    private String county;
    private String postalCode;
    private String stateProvCd;
    private String countryCd;
    private String addressTypeCd;
    private String preferredInd;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStateProvCd() {
        return stateProvCd;
    }

    public void setStateProvCd(String stateProvCd) {
        this.stateProvCd = stateProvCd;
    }

    public String getCountryCd() {
        return countryCd;
    }

    public void setCountryCd(String countryCd) {
        this.countryCd = countryCd;
    }

    public String getAddressTypeCd() {
        return addressTypeCd;
    }

    public void setAddressTypeCd(String addressTypeCd) {
        this.addressTypeCd = addressTypeCd;
    }

    public String getPreferredInd() {
        return preferredInd;
    }

    public void setPreferredInd(String preferredInd) {
        this.preferredInd = preferredInd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EmployerAddressesModel that = (EmployerAddressesModel) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(addressLine1, that.addressLine1) &&
                Objects.equals(addressLine2, that.addressLine2) &&
                Objects.equals(addressLine3, that.addressLine3) &&
                Objects.equals(city, that.city) &&
                Objects.equals(county, that.county) &&
                Objects.equals(postalCode, that.postalCode) &&
                Objects.equals(stateProvCd, that.stateProvCd) &&
                Objects.equals(countryCd, that.countryCd) &&
                Objects.equals(addressTypeCd, that.addressTypeCd) &&
                Objects.equals(preferredInd, that.preferredInd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, addressLine1, addressLine2, addressLine3, city, county, postalCode, stateProvCd, countryCd, addressTypeCd, preferredInd);
    }
}
