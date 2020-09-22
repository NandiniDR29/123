package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.modules.claim.gb_ltd.scenarios.ScenarioTestClaimPaymentCalcForRecoveryIncomeBenefitIPDE;
import org.testng.annotations.Test;

import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimPaymentCalcForRecoveryIncomeBenefitIPDE extends ScenarioTestClaimPaymentCalcForRecoveryIncomeBenefitIPDE {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36934", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPaymentCalcForRecoveryIncomeBenefit() {

        super.testClaimPaymentCalcForRecoveryIncomeBenefit(false, DATA_GATHER);
    }
}