package com.exigen.ren.rest.claim.model.common.claimbody.claim;

import com.exigen.ipb.eisa.ws.rest.model.Model;

import java.util.Objects;

public class PaidToDateInfoEntityModel extends Model {
    private String gracePeriod;
    private String paidToDateWithGracePeriod;
    private String paidToDate;

    public String getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(String gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public String getPaidToDateWithGracePeriod() {
        return paidToDateWithGracePeriod;
    }

    public void setPaidToDateWithGracePeriod(String paidToDateWithGracePeriod) {
        this.paidToDateWithGracePeriod = paidToDateWithGracePeriod;
    }

    public String getPaidToDate() {
        return paidToDate;
    }

    public void setPaidToDate(String paidToDate) {
        this.paidToDate = paidToDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PaidToDateInfoEntityModel that = (PaidToDateInfoEntityModel) o;
        return Objects.equals(gracePeriod, that.gracePeriod) &&
                Objects.equals(paidToDateWithGracePeriod, that.paidToDateWithGracePeriod) &&
                Objects.equals(paidToDate, that.paidToDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gracePeriod, paidToDateWithGracePeriod, paidToDate);
    }

}
