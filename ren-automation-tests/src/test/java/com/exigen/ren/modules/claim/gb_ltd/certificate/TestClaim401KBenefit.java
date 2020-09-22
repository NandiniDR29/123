package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.modules.claim.gb_ltd.scenarios.ScenarioTestClaim401KBenefit;
import org.testng.annotations.Test;

import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageDeterminationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.FOUR_HUNDRED_ONE_K_CONTRIBUTION_DURING_DISABILITY;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.FOUR_HUNDRED_ONE_K_CONTRIBUTION_MONTHLY_MAXIMUM_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.RELATIONSHIP_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.RelationshipInformationMetaData.ANNUAL_EARNINGS;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaim401KBenefit extends ScenarioTestClaim401KBenefit {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33762", component = CLAIMS_GROUPBENEFITS)
    public void testClaimUIChangeFor401KContribution() {
        preconditions();
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY));
        claim.claimOpen().perform();
        testClaimUIChangeFor401KContributionCommonSteps();
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-33813", component = CLAIMS_GROUPBENEFITS)
    public void testClaimCalculatedChangeFor401KContribution() {
        preconditions();
        String todayPlus100Years = TimeSetterUtil.getInstance().getCurrentTime().plusYears(100).format(DateTimeUtils.MM_DD_YYYY);
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), todayPlus100Years));
        disabilityClaim.claimOpen().perform();
        testClaimCalculatedChangeFor401KContributionCommonSteps();
    }

    private void preconditions() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FOUR_HUNDRED_ONE_K_CONTRIBUTION_DURING_DISABILITY.getLabel()), "10%").resolveLinks()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FOUR_HUNDRED_ONE_K_CONTRIBUTION_MONTHLY_MAXIMUM_AMOUNT.getLabel()), "1000").resolveLinks());
        longTermDisabilityCertificatePolicy.createPolicyViaUI(getDefaultLTDCertificatePolicyDataGatherData()
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), RELATIONSHIP_INFORMATION.getLabel(), ANNUAL_EARNINGS.getLabel()), "12000")
                .adjust(longTermDisabilityCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));
    }

}
