package com.exigen.ren.rest.dxp.model.policy.policy_details_ac;

import com.exigen.ren.rest.model.RestError;

public class PreconfigGroupOptionalBenefitComponent extends RestError {

    private String optionalBenefitUnits;

    public String getOptionalBenefitUnits() {
        return optionalBenefitUnits;
    }

    public void setOptionalBenefitUnits(String optionalBenefitUnits) {
        this.optionalBenefitUnits = optionalBenefitUnits;
    }
}
