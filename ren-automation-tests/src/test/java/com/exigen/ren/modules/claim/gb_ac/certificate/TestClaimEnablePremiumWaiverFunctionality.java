package com.exigen.ren.modules.claim.gb_ac.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.modules.claim.gb_ac.scenarios.ScenarioTestClaimEnablePremiumWaiverFunctionality;
import org.testng.annotations.Test;

import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimEnablePremiumWaiverFunctionality extends ScenarioTestClaimEnablePremiumWaiverFunctionality {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20871", component = CLAIMS_GROUPBENEFITS)
    public void testClaimEnablePremiumWaiverFunctionalityTC01() {

        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_AC);
        accHealthClaim.initiate(accHealthClaim.getGbACTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY));

        super.testClaimEnablePremiumWaiverFunctionalityTC01(DATA_GATHER_CERTIFICATE, false);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20872", component = CLAIMS_GROUPBENEFITS)
    public void testClaimEnablePremiumWaiverFunctionalityTC02() {

        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_AC);
        accHealthClaim.create(accHealthClaim.getGbACTestData().getTestData(DATA_GATHER_CERTIFICATE, "TestData_With_PremiumWaiverBenefit"));

        super.testClaimEnablePremiumWaiverFunctionalityTC02();
    }
}