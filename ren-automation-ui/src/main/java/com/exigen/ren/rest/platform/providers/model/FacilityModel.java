/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.providers.model;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacilityModel extends RestError {
    private String id;
    private String oid;
    private String facilityName;
    private AddressDataModel addressDTO;
    private String externalId;
    private String phoneNumber;
    private TermModel term;
    private String serviceFacilityOID;
    private String latitude;
    private String longitude;
    private String effective;
    private String expiration;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public AddressDataModel getAddressDTO() {
        return addressDTO;
    }

    public void setAddressDTO(AddressDataModel addressDTO) {
        this.addressDTO = addressDTO;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public TermModel getTerm() {
        return term;
    }

    public void setTerm(TermModel term) {
        this.term = term;
    }

    public void setServiceFacilityOID(String serviceFacilityOID) {
        this.serviceFacilityOID = serviceFacilityOID;
    }

    public String getServiceFacilityOID() {
        return serviceFacilityOID;
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

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public void setEffective(String effective) {
        this.effective = effective;
    }

    public String getExpiration() {
        return expiration;
    }

    public String getEffective() {
        return effective;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FacilityModel facilityModel = (FacilityModel) o;

        if (oid == null) {
            if (facilityModel.oid != null) {
                return false;
            }
        } else if (!oid.equals(facilityModel.oid)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, oid, facilityName, addressDTO, externalId, phoneNumber,
                term, serviceFacilityOID, latitude, longitude, effective, expiration);
    }
}
