package com.exigen.ren.modules.claim.gb_tl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantCoverageTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.ANNUAL_BASE_SALARY;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.ROUNDING_METHOD;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimLimitCalculationRulesChanges extends ClaimGroupBenefitsTLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-32502", component = CLAIMS_GROUPBENEFITS)
    public void testClaimLimitCalculationRulesChanges_Steps_1_13() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicySelfAdminData().adjust(tdSpecific().getTestData("TestData_Policy").resolveLinks()));

        LOGGER.info("Steps 1,2");
        TestData testDataClaim = termLifeClaim.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks());
        termLifeClaim.initiate(testDataClaim);
        termLifeClaim.getDefaultWorkspace().fillUpTo(testDataClaim, PolicyInformationParticipantParticipantCoverageTab.class, true);
        AbstractContainer<?, ?> assetListParticipantCoverage = policyInformationParticipantParticipantCoverageTab.getAssetList();
        assertThat(assetListParticipantCoverage.getAsset(LIMIT_AMOUNT)).hasValue("$50,000.00");

        LOGGER.info("Step 6");
        assetListParticipantCoverage.getAsset(COVERAGE_NAME).setValue("Employee Basic Accidental Death and Dismemberment Insurance - Basic Life + Voluntary");
        assertThat(assetListParticipantCoverage.getAsset(LIMIT_AMOUNT)).hasValue("$60,500.00");

        LOGGER.info("Step 7");
        policyInformationParticipantParticipantInformationTab.navigate();
        AbstractContainer<?, ?> assetListParticipantInfo = policyInformationParticipantParticipantInformationTab.getAssetList();
        assetListParticipantInfo.getAsset(ANNUAL_BASE_SALARY).setValue("$50,750.00");
        policyInformationParticipantParticipantCoverageTab.navigate();
        assertThat(assetListParticipantCoverage.getAsset(LIMIT_AMOUNT)).hasValue("$61,000.00");

        LOGGER.info("Step 9");
        assetListParticipantCoverage.getAsset(COVERAGE_NAME).setValue("Employee Voluntary Accidental Death and Dismemberment Insurance - Basic Life + Voluntary");
        assetListParticipantCoverage.getAsset(SALARY_MULTIPLE).setValue("1X");
        assertThat(assetListParticipantCoverage.getAsset(LIMIT_AMOUNT)).hasValue("$51,000.00");

        LOGGER.info("Step 10");
        policyInformationParticipantParticipantInformationTab.navigate();
        assetListParticipantInfo.getAsset(ANNUAL_BASE_SALARY).setValue("$50,999.99");
        policyInformationParticipantParticipantCoverageTab.navigate();
        assertThat(assetListParticipantCoverage.getAsset(LIMIT_AMOUNT)).hasValue("$51,000.00");

        LOGGER.info("Step 12");
        policyInformationParticipantParticipantInformationTab.navigate();
        assetListParticipantInfo.getAsset(ANNUAL_BASE_SALARY).setValue("$10099.99");
        policyInformationParticipantParticipantCoverageTab.navigate();
        assetListParticipantCoverage.getAsset(COVERAGE_NAME).setValue("Employee Voluntary Life Insurance - Basic Life + Voluntary");
        assertThat(assetListParticipantCoverage.getAsset(LIMIT_AMOUNT_COMBOBOX)).hasOptions("","10000.00","20000.00","30000.00","40000.00","50000.00");

        LOGGER.info("Step 13");
        policyInformationParticipantParticipantInformationTab.navigate();
        assetListParticipantInfo.getAsset(ANNUAL_BASE_SALARY).setValue("$10,100.01");
        policyInformationParticipantParticipantCoverageTab.navigate();
        assertThat(assetListParticipantCoverage.getAsset(LIMIT_AMOUNT_COMBOBOX)).hasOptions("","10000.00","20000.00","30000.00","40000.00","50000.00");
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-32502", component = CLAIMS_GROUPBENEFITS)
    public void testClaimLimitCalculationRulesChanges_Steps_15_19() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicySelfAdminData()
                .adjust(tdSpecific().getTestData("TestData_Policy").resolveLinks())
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[1]", BENEFIT_SCHEDULE.getLabel(), ROUNDING_METHOD.getLabel()),"None")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[2]", BENEFIT_SCHEDULE.getLabel(), ROUNDING_METHOD.getLabel()),"None")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[3]", BENEFIT_SCHEDULE.getLabel(), ROUNDING_METHOD.getLabel()),"None")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[4]", BENEFIT_SCHEDULE.getLabel(), ROUNDING_METHOD.getLabel()),"None"));

        LOGGER.info("Steps 15,16");
        TestData testDataClaim = termLifeClaim.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks());
        termLifeClaim.initiate(testDataClaim);
        termLifeClaim.getDefaultWorkspace().fillUpTo(testDataClaim, PolicyInformationParticipantParticipantCoverageTab.class, true);
        AbstractContainer<?, ?> assetListParticipantCoverage = policyInformationParticipantParticipantCoverageTab.getAssetList();
        assertThat(assetListParticipantCoverage.getAsset(LIMIT_AMOUNT)).hasValue("$50,499.99");

        LOGGER.info("Step 17");
        policyInformationParticipantParticipantCoverageTab.addCoverage("Basic Life + Voluntary", "Employee Basic Accidental Death and Dismemberment Insurance - Basic Life + Voluntary");
        assertThat(assetListParticipantCoverage.getAsset(LIMIT_AMOUNT)).hasValue("$60,499.99");

        LOGGER.info("Step 18");
        policyInformationParticipantParticipantCoverageTab.addCoverage("Basic Life + Voluntary", "Employee Voluntary Accidental Death and Dismemberment Insurance - Basic Life + Voluntary");
        assetListParticipantCoverage.getAsset(SALARY_MULTIPLE).setValue("1X");
        assertThat(assetListParticipantCoverage.getAsset(LIMIT_AMOUNT)).hasValue("$50,499.99");

        LOGGER.info("Step 19");
        policyInformationParticipantParticipantCoverageTab.addCoverage("Basic Life + Voluntary", "Employee Voluntary Life Insurance - Basic Life + Voluntary");
        assertThat(assetListParticipantCoverage.getAsset(LIMIT_AMOUNT_COMBOBOX)).hasOptions("", "10000.00", "20000.00", "30000.00", "40000.00", "50000.00", "60000.00", "70000.00", "80000.00", "90000.00", "100000.00",
                "110000.00", "120000.00", "130000.00", "140000.00", "150000.00", "160000.00", "170000.00", "180000.00", "190000.00", "200000.00", "210000.00", "220000.00", "230000.00", "240000.00", "250000.00");
    }

}
