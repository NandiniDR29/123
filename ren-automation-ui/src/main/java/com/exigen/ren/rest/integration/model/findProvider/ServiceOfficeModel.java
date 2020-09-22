package com.exigen.ren.rest.integration.model.findProvider;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceOfficeModel extends RestError {

    private ServiceOfficeAddressModel address;
    private String mobileUnit;
    private String npi;
    private String officeNumber;
    private String phoneNumber;

    @JsonProperty("FQHCLocationInd")
    private String fqhcLocationInd;

    @JsonProperty("IHSLocationInd")
    private String ihsLocationInd;

    @JsonProperty("RHCLocationInd")
    private String rhcLocationInd;

    public ServiceOfficeAddressModel getAddress() {
        return address;
    }

    public void setAddress(ServiceOfficeAddressModel address) {
        this.address = address;
    }

    public String getMobileUnit() {
        return mobileUnit;
    }

    public void setMobileUnit(String mobileUnit) {
        this.mobileUnit = mobileUnit;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    public String getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFqhcLocationInd() {
        return fqhcLocationInd;
    }

    public void setFqhcLocationInd(String fqhcLocationInd) {
        this.fqhcLocationInd = fqhcLocationInd;
    }

    public String getIhsLocationInd() {
        return ihsLocationInd;
    }

    public void setIhsLocationInd(String ihsLocationInd) {
        this.ihsLocationInd = ihsLocationInd;
    }

    public String getRhcLocationInd() {
        return rhcLocationInd;
    }

    public void setRhcLocationInd(String rhcLocationInd) {
        this.rhcLocationInd = rhcLocationInd;
    }
}
