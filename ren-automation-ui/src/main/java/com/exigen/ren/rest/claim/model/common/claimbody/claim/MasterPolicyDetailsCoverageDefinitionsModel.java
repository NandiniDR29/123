package com.exigen.ren.rest.claim.model.common.claimbody.claim;

import com.exigen.ipb.eisa.ws.rest.model.Model;

public class MasterPolicyDetailsCoverageDefinitionsModel extends Model {

    private String packageCd;
    private boolean procedureOverrideInd;
    private boolean selfAdministered;

    public String getPackageCd() {
        return packageCd;
    }

    public void setPackageCd(String packageCd) {
        this.packageCd = packageCd;
    }

    public boolean isProcedureOverrideInd() {
        return procedureOverrideInd;
    }

    public void setProcedureOverrideInd(boolean procedureOverrideInd) {
        this.procedureOverrideInd = procedureOverrideInd;
    }

    public boolean isSelfAdministered() {
        return selfAdministered;
    }

    public void setSelfAdministered(boolean selfAdministered) {
        this.selfAdministered = selfAdministered;
    }

}
