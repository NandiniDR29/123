package com.exigen.ren.rest.claim.model.common.claimbody.damages;

import com.exigen.ipb.eisa.ws.rest.model.Model;

import java.util.Objects;

public class LossExtensionModel extends Model {
    private String procedureDate;
    private String quantity;
    private String toothLetter;
    private String oralCavityArea;
    private String reportedFee;
    private String lossDescription;
    private String toothSurface;

    public String getProcedureDate() {
        return procedureDate;
    }

    public void setProcedureDate(String procedureDate) {
        this.procedureDate = procedureDate;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getToothLetter() {
        return toothLetter;
    }

    public void setToothLetter(String toothLetter) {
        this.toothLetter = toothLetter;
    }

    public String getOralCavityArea() {
        return oralCavityArea;
    }

    public void setOralCavityArea(String oralCavityArea) {
        this.oralCavityArea = oralCavityArea;
    }

    public String getReportedFee() {
        return reportedFee;
    }

    public void setReportedFee(String reportedFee) {
        this.reportedFee = reportedFee;
    }

    public String getLossDescription() {
        return lossDescription;
    }

    public void setLossDescription(String lossDescription) {
        this.lossDescription = lossDescription;
    }

    public String getToothSurface() {
        return toothSurface;
    }

    public void setToothSurface(String toothSurface) {
        this.toothSurface = toothSurface;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        LossExtensionModel that = (LossExtensionModel) o;
        return Objects.equals(procedureDate, that.procedureDate) &&
                Objects.equals(quantity, that.quantity) &&
                Objects.equals(toothLetter, that.toothLetter) &&
                Objects.equals(oralCavityArea, that.oralCavityArea) &&
                Objects.equals(reportedFee, that.reportedFee) &&
                Objects.equals(lossDescription, that.lossDescription) &&
                Objects.equals(toothSurface, that.toothSurface);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), procedureDate, quantity, toothLetter, oralCavityArea, reportedFee, lossDescription, toothSurface);
    }
}
