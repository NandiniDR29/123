package com.exigen.ren.rest.claim.model.common.claimbody.claim;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Arrays;
import java.util.Objects;

public class AddressModel extends Model {

    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String city;
    private String county;
    private Country countryCd;
    private String postalCode;
    private String stateProvCd;
    private String latitude;
    private String longitude;
    private String componentName;
    private AddressTypeCd addressTypeCd;
    private String displayValue;

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

    public Country getCountryCd() {
        return countryCd;
    }

    @JsonSetter("countryCd")
    public void setCountryCd(String countryCd) {
        this.countryCd = Country.getEntityByValue(countryCd);
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public AddressTypeCd getAddressTypeCd() {
        return addressTypeCd;
    }

    @JsonSetter("addressTypeCd")
    public void setAddressTypeCd(String addressTypeCd) {
        this.addressTypeCd = AddressTypeCd.getEntityByValue(addressTypeCd);
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AddressModel that = (AddressModel) o;
        return Objects.equals(addressLine1, that.addressLine1) &&
                Objects.equals(addressLine2, that.addressLine2) &&
                Objects.equals(addressLine3, that.addressLine3) &&
                Objects.equals(city, that.city) &&
                Objects.equals(county, that.county) &&
                Objects.equals(countryCd, that.countryCd) &&
                Objects.equals(postalCode, that.postalCode) &&
                Objects.equals(stateProvCd, that.stateProvCd) &&
                Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude) &&
                Objects.equals(componentName, that.componentName) &&
                Objects.equals(addressTypeCd, that.addressTypeCd) &&
                Objects.equals(displayValue, that.displayValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), addressLine1, addressLine2, addressLine3, city, county, countryCd, postalCode, stateProvCd, latitude, longitude, componentName, addressTypeCd, displayValue);
    }

    @JsonIgnoreType
    public enum Country {
        UNITED_STATES("United States", "US");

        private String label;
        private String value;

        Country(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getUIName() {
            return label;
        }

        public String getValue() {
            return value;
        }

        public static Country getEntityByValue(String value) {
            return Arrays.stream(Country.values()).filter(ent -> ent.getValue().equals(value)).findFirst().orElse(null);
        }

    }

    @JsonIgnoreType
    public enum AddressTypeCd {
        STREET_ADDRESS("Street Address", "contact");

        private String label;
        private String value;

        AddressTypeCd(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getUIName() {
            return label;
        }

        public String getValue() {
            return value;
        }

        public static AddressTypeCd getEntityByValue(String value) {
            return Arrays.stream(AddressTypeCd.values()).filter(ent -> ent.getValue().equals(value)).findFirst().orElse(null);
        }

    }

}
