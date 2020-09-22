package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.modules.claim.gb_ltd.scenarios.ScenarioTestClaimPaymentCalcForCatastrophicDisability;
import org.testng.annotations.Test;

import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimPaymentCalcForCatastrophicDisabilityBenefit extends ScenarioTestClaimPaymentCalcForCatastrophicDisability {

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-25505", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPaymentCalcForCatastrophicDisabilityBenefit() {

        super.testClaimPaymentCalcForCatastrophicDisabilityBenefit(true, DATA_GATHER_CERTIFICATE);
    }
}