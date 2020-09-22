package com.exigen.ren.rest.claim.model.common.claimbody.claim;

import com.exigen.ipb.eisa.ws.rest.model.Model;

public class PatientHistoryModel extends Model {
    private String toothArea;
    private String oralCavityArea;

    public String getToothArea() {
        return toothArea;
    }

    public void setToothArea(String toothArea) {
        this.toothArea = toothArea;
    }

    public String getOralCavityArea() {
        return oralCavityArea;
    }

    public void setOralCavityArea(String oralCavityArea) {
        this.oralCavityArea = oralCavityArea;
    }

}
