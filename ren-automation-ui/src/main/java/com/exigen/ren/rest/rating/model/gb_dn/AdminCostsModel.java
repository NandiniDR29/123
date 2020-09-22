package com.exigen.ren.rest.rating.model.gb_dn;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminCostsModel extends Model {

    private String adminCategory;
    private String annualAmt;
    private String perClaimAmt;
    private String perEmployeeAmt;
    private String premiumPct;

    public String getAdminCategory() {
        return adminCategory;
    }

    public AdminCostsModel setAdminCategory(String adminCategory) {
        this.adminCategory = adminCategory;
        return this;
    }

    public String getAnnualAmt() {
        return annualAmt;
    }

    public AdminCostsModel setAnnualAmt(String annualAmt) {
        this.annualAmt = annualAmt;
        return this;
    }

    public String getPerClaimAmt() {
        return perClaimAmt;
    }

    public AdminCostsModel setPerClaimAmt(String perClaimAmt) {
        this.perClaimAmt = perClaimAmt;
        return this;
    }

    public String getPerEmployeeAmt() {
        return perEmployeeAmt;
    }

    public AdminCostsModel setPerEmployeeAmt(String perEmployeeAmt) {
        this.perEmployeeAmt = perEmployeeAmt;
        return this;
    }

    public String getPremiumPct() {
        return premiumPct;
    }

    public AdminCostsModel setPremiumPct(String premiumPct) {
        this.premiumPct = premiumPct;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AdminCostsModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AdminCostsModel that = (AdminCostsModel) o;
        return Objects.equals(adminCategory, that.adminCategory) &&
                Objects.equals(annualAmt, that.annualAmt) &&
                Objects.equals(perClaimAmt, that.perClaimAmt) &&
                Objects.equals(perEmployeeAmt, that.perEmployeeAmt) &&
                Objects.equals(premiumPct, that.premiumPct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), adminCategory, annualAmt, perClaimAmt, perEmployeeAmt, premiumPct);
    }

    @Override
    public String toString() {
        return "AdminCostsModel{" +
                "adminCategory='" + adminCategory + '\'' +
                ", annualAmt='" + annualAmt + '\'' +
                ", perClaimAmt='" + perClaimAmt + '\'' +
                ", perEmployeeAmt='" + perEmployeeAmt + '\'' +
                ", premiumPct='" + premiumPct + '\'' +
                '}';
    }
}
