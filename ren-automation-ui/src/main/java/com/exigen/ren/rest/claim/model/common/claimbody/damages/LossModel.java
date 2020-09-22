package com.exigen.ren.rest.claim.model.common.claimbody.damages;


import com.exigen.ipb.eisa.ws.rest.model.Model;

import java.util.Objects;

public class LossModel extends Model {
    private String associatedRiskItemOid;
    private LossExtensionModel extension;

    public String getAssociatedRiskItemOid() {
        return associatedRiskItemOid;
    }

    public void setAssociatedRiskItemOid(String associatedRiskItemOid) {
        this.associatedRiskItemOid = associatedRiskItemOid;
    }

    public LossExtensionModel getExtension() {
        return extension;
    }

    public void setExtension(LossExtensionModel extension) {
        this.extension = extension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        LossModel lossModel = (LossModel) o;
        return Objects.equals(associatedRiskItemOid, lossModel.associatedRiskItemOid) &&
                Objects.equals(extension, lossModel.extension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), associatedRiskItemOid, extension);
    }
}

