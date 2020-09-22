package com.exigen.ren.rest.claim.model.common.claimbody.damages;

import com.exigen.ipb.eisa.ws.rest.model.Model;

public class DamageFeatureModel extends Model {

    DamageFeatureExtensionModel extension;

    public DamageFeatureExtensionModel getExtension() {
        return extension;
    }

    public void setExtension(DamageFeatureExtensionModel extension) {
        this.extension = extension;
    }

}
