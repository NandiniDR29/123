package com.exigen.ren.rest.claim.model.recoveries;

import java.util.Objects;

public class RecoveryPayeeModel {

    private String typeCd;
    private String refId;
    private String displayValue;

    public String getTypeCd() {
        return typeCd;
    }

    public void setTypeCd(String typeCd) {
        this.typeCd = typeCd;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecoveryPayeeModel that = (RecoveryPayeeModel) o;
        return Objects.equals(typeCd, that.typeCd) &&
                Objects.equals(refId, that.refId) &&
                Objects.equals(displayValue, that.displayValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeCd, refId, displayValue);
    }

}
