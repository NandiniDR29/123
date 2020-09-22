package com.exigen.ren.rest.claim.model.dental.claimImage;

import com.exigen.ipb.eisa.ws.rest.model.Model;

import java.util.Objects;

public class ExtensionModel extends Model {
    private String dentistAdjustment;
    private String maximumApprovedFee;

    public String getDentistAdjustment() {
        return dentistAdjustment;
    }

    public void setDentistAdjustment(String dentistAdjustment) {
        this.dentistAdjustment = dentistAdjustment;
    }

    public String getMaximumApprovedFee() {
        return maximumApprovedFee;
    }

    public void setMaximumApprovedFee(String maximumApprovedFee) {
        this.maximumApprovedFee = maximumApprovedFee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExtensionModel that = (ExtensionModel) o;
        return Objects.equals(dentistAdjustment, that.dentistAdjustment) &&
                Objects.equals(maximumApprovedFee, that.maximumApprovedFee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dentistAdjustment, maximumApprovedFee);
    }
}
