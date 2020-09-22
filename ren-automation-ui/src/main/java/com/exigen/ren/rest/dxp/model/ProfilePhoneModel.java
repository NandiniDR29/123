package com.exigen.ren.rest.dxp.model;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfilePhoneModel extends RestError {

    private String phone;
    private String id;
    private String phoneTypeCd;
    private Boolean preferredInd;

    public String getPhone() {
        return phone;
    }

    @JsonIgnore
    public String getFormattedPhone() {
        return phone.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneTypeCd() {
        return phoneTypeCd;
    }

    public void setPhoneTypeCd(String phoneTypeCd) {
        this.phoneTypeCd = phoneTypeCd;
    }

    public Boolean getPreferredInd() {
        return preferredInd;
    }

    public void setPreferredInd(Boolean preferredInd) {
        this.preferredInd = preferredInd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        ProfilePhoneModel that = (ProfilePhoneModel) o;
        return Objects.equals(phone, that.phone) &&
                Objects.equals(id, that.id) &&
                Objects.equals(phoneTypeCd, that.phoneTypeCd) &&
                Objects.equals(preferredInd, that.preferredInd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), phone, id, phoneTypeCd, preferredInd);
    }
}
