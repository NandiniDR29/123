package com.exigen.ren.modules.claim.gb_ac.without_policy;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.modules.claim.gb_ac.scenarios.ScenarioTestClaimEnablePremiumWaiverFunctionality;
import org.testng.annotations.Test;

import static com.exigen.ren.TestDataKey.DATA_GATHER_WITHOUT_POLICY;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimEnablePremiumWaiverFunctionality extends ScenarioTestClaimEnablePremiumWaiverFunctionality {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20871", component = CLAIMS_GROUPBENEFITS)
    public void testClaimEnablePremiumWaiverFunctionalityTC01() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        accHealthClaim.initiateWithoutPolicy(accHealthClaim.getGbACTestData().getTestData(DATA_GATHER_WITHOUT_POLICY, "TestData_With_PremiumWaiverBenefit"));

        super.testClaimEnablePremiumWaiverFunctionalityTC01(DATA_GATHER_WITHOUT_POLICY, false);
    }
}