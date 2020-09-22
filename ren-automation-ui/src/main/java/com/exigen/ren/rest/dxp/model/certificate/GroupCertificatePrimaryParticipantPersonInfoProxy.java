package com.exigen.ren.rest.dxp.model.certificate;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.exigen.ren.rest.billing.model.BillingAddressModel;
import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupCertificatePrimaryParticipantPersonInfoProxy extends RestError {

    private AddressModel groupCertificatePrimaryParticipantContactInfoProxy;
    private String lastName;
    private String gender;
    private String legalIdentification;
    private String dateOfBirth;
    private String suffix;
    private String titleCd;
    private String firstName;
    private String maritalStatusCd;
    private String middleName;

    public AddressModel getGroupCertificatePrimaryParticipantContactInfoProxy() {
        return groupCertificatePrimaryParticipantContactInfoProxy;
    }

    public void setGroupCertificatePrimaryParticipantContactInfoProxy(AddressModel groupCertificatePrimaryParticipantContactInfoProxy) {
        this.groupCertificatePrimaryParticipantContactInfoProxy = groupCertificatePrimaryParticipantContactInfoProxy;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLegalIdentification() {
        return legalIdentification;
    }

    public void setLegalIdentification(String legalIdentification) {
        this.legalIdentification = legalIdentification;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getTitleCd() {
        return titleCd;
    }

    public void setTitleCd(String titleCd) {
        this.titleCd = titleCd;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMaritalStatusCd() {
        return maritalStatusCd;
    }

    public void setMaritalStatusCd(String maritalStatusCd) {
        this.maritalStatusCd = maritalStatusCd;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public class AddressModel extends Model {

        private String addressLine1;
        private String addressLine2;
        private String addressLine3;
        private String city;
        private String stateProvCd;
        private String countryCd;
        private String postalCode;
        private String addressType;

        public String getAddressType() {
            return addressType;
        }

        public void setAddressType(String addressType) {
            this.addressType = addressType;
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

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }
    }
}