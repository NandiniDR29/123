package com.exigen.ren.rest.platform.providers.model;

import com.exigen.ren.rest.model.RestError;

import java.util.Objects;

public class LicenceOptionsModel extends RestError {

    private String effective;
    private String expiration;
    private String licensePermitNumber;
    private String licenseStatusCd;
    private String licenseTypeCd;

    public String getEffective() {
        return effective;
    }

    public void setEffective(String effective) {
        this.effective = effective;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getLicensePermitNumber() {
        return licensePermitNumber;
    }

    public void setLicensePermitNumber(String licensePermitNumber) {
        this.licensePermitNumber = licensePermitNumber;
    }

    public String getLicenseStatusCd() {
        return licenseStatusCd;
    }

    public void setLicenseStatusCd(String licenseStatusCd) {
        this.licenseStatusCd = licenseStatusCd;
    }

    public String getLicenseTypeCd() {
        return licenseTypeCd;
    }

    public void setLicenseTypeCd(String licenseTypeCd) {
        this.licenseTypeCd = licenseTypeCd;
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
        LicenceOptionsModel that = (LicenceOptionsModel) o;
        return Objects.equals(effective, that.effective) &&
                Objects.equals(expiration, that.expiration) &&
                Objects.equals(licensePermitNumber, that.licensePermitNumber) &&
                Objects.equals(licenseStatusCd, that.licenseStatusCd) &&
                Objects.equals(licenseTypeCd, that.licenseTypeCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), effective, expiration, licensePermitNumber, licenseStatusCd, licenseTypeCd);
    }
}

