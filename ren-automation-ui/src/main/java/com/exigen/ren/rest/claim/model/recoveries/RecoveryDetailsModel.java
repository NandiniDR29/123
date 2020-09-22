package com.exigen.ren.rest.claim.model.recoveries;

import java.util.Objects;

public class RecoveryDetailsModel {

    private String type;
    private RecoveryDetailsChequeModel cheque;
    private RecoveryDetailsEFTModel eft;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RecoveryDetailsChequeModel getCheque() {
        return cheque;
    }

    public void setCheque(RecoveryDetailsChequeModel cheque) {
        this.cheque = cheque;
    }

    public RecoveryDetailsEFTModel getEft() {
        return eft;
    }

    public void setEft(RecoveryDetailsEFTModel eft) {
        this.eft = eft;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecoveryDetailsModel that = (RecoveryDetailsModel) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(cheque, that.cheque) &&
                Objects.equals(eft, that.eft);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, cheque, eft);
    }

}
