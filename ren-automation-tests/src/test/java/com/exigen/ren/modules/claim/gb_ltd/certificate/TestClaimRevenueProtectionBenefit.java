package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.modules.claim.gb_ltd.scenarios.ScenarioTestClaimRevenueProtectionBenefit;
import org.testng.annotations.Test;

import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageDeterminationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.RELATIONSHIP_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.RelationshipInformationMetaData.ANNUAL_EARNINGS;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimRevenueProtectionBenefit extends ScenarioTestClaimRevenueProtectionBenefit {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33861", component = CLAIMS_GROUPBENEFITS)
    public void testClaimRevenueProtectionBenefitSinglePaymentCalculator() {
        preconditions();
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY));
        disabilityClaim.claimOpen().perform();
        testClaimRevenueProtectionBenefitSinglePaymentCalculatorCommonSteps();
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-34427", component = CLAIMS_GROUPBENEFITS)
    public void testClaimRevenueProtectionBenefitSingleBenefitDeductions() {
        preconditions();
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY));
        disabilityClaim.claimOpen().perform();
        testClaimRevenueProtectionBenefitSingleBenefitDeductionsCommonSteps();
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-33961", component = CLAIMS_GROUPBENEFITS)
    public void testClaimRevenueProtectionBenefitPaymentSeriesCalculator() {
        preconditions();
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().plusYears(100).format(DateTimeUtils.MM_DD_YYYY)));
        disabilityClaim.claimOpen().perform();
        testClaimRevenueProtectionBenefitPaymentSeriesCalculatorCommonSteps();
    }

    private void preconditions() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData().adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks()));
        longTermDisabilityCertificatePolicy.createPolicyViaUI(getDefaultLTDCertificatePolicyDataGatherData()
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), RELATIONSHIP_INFORMATION.getLabel(), ANNUAL_EARNINGS.getLabel()), "12000")
                .adjust(longTermDisabilityCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));
    }

}
