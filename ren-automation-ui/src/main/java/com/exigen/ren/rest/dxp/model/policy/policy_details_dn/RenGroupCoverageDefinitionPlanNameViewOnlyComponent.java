package com.exigen.ren.rest.dxp.model.policy.policy_details_dn;

import com.exigen.ren.rest.model.RestError;

public class RenGroupCoverageDefinitionPlanNameViewOnlyComponent extends RestError {

    private String packageName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
