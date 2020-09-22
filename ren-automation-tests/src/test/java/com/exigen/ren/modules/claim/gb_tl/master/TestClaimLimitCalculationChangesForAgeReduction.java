package com.exigen.ren.modules.claim.gb_tl.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantInformationTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN_PLUS_VOLUNTARY;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimLimitCalculationChangesForAgeReduction extends ClaimGroupBenefitsTLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33726", component = CLAIMS_GROUPBENEFITS)
    public void testClaimLimitCalculationChangesForAgeReduction_TC01_1() {
        List<String> coverages = ImmutableList.of(BTL_BASIC_LIFE_PLAN_PLUS_VOLUNTARY, ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY, VOL_BTL_BASIC_LIFE_PLAN_PLUS_VOLUNTARY, VOL_ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicySelfAdminData()
                .adjust(tdSpecific().getTestData("TestData_Policy1_REN_33726").resolveLinks()));

        LOGGER.info("Test: Step 1");
        initiateTLClaimWithPolicyAndFillToTab(termLifeClaim.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim1")), PolicyInformationParticipantParticipantInformationTab.class, true);

        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(CURRENT_AGE)).hasValue("64");

        LOGGER.info("Test: Steps 2-5");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(ANNUAL_BASE_SALARY).setValue("86954");

        verifyLimitAmounts(coverages, "174000", true);

        LOGGER.info("Test: Step 6");
        changeDateOfBirthAndVerifyCurrentAge(69);

        LOGGER.info("Test: Step 7");
        verifyLimitAmounts(coverages, "100000", false);

        LOGGER.info("Test: Step 8");
        changeDateOfBirthAndVerifyCurrentAge(70);

        LOGGER.info("Test: Step 9");
        verifyLimitAmounts(coverages, "87000", false);

        LOGGER.info("Test: Step 10");
        changeDateOfBirthAndVerifyCurrentAge(75);

        LOGGER.info("Test: Step 11");
        verifyLimitAmounts(coverages, "0", false);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33726", component = CLAIMS_GROUPBENEFITS)
    public void testClaimLimitCalculationChangesForAgeReduction_TC01_2() {
        List<String> coverages = ImmutableList.of(BTL_BASIC_LIFE_PLAN_PLUS_VOLUNTARY, ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY, VOL_BTL_BASIC_LIFE_PLAN_PLUS_VOLUNTARY, VOL_ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicySelfAdminData()
                .adjust(tdSpecific().getTestData("TestData_Policy2_REN_33726").resolveLinks()));

        LOGGER.info("Test: Step 13");
        initiateTLClaimWithPolicyAndFillToTab(termLifeClaim.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim2")), PolicyInformationParticipantParticipantInformationTab.class, true);

        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(CURRENT_AGE)).hasValue("74");

        LOGGER.info("Test: Steps 14-17");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(ANNUAL_BASE_SALARY).setValue("86954");

        verifyLimitAmounts(coverages, "87000", true);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33758", component = CLAIMS_GROUPBENEFITS)
    public void testClaimLimitCalculationChangesForAgeReduction_TC02_1() {
        List<String> coverages = ImmutableList.of(VOL_BTL_BASIC_LIFE_PLAN_PLUS_VOLUNTARY, VOL_ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicySelfAdminData()
                .adjust(tdSpecific().getTestData("TestData_Policy1_REN-33758").resolveLinks()));

        LOGGER.info("Test: Step 1");
        initiateTLClaimWithPolicyAndFillToTab(termLifeClaim.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim1")), PolicyInformationParticipantParticipantInformationTab.class, true);

        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(CURRENT_AGE)).hasValue("64");

        LOGGER.info("Test: Steps 2-5");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(ANNUAL_BASE_SALARY).setValue("86954");

        verifyLimitAmounts(coverages, "2X", "174000", true);

        LOGGER.info("Test: Step 6");
        changeDateOfBirthAndVerifyCurrentAge(69);

        LOGGER.info("Test: Step 7");
        verifyLimitAmounts(coverages, "100000", false);

        LOGGER.info("Test: Step 8");
        changeDateOfBirthAndVerifyCurrentAge(70);

        LOGGER.info("Test: Step 9");
        verifyLimitAmounts(coverages, "87000", false);

        LOGGER.info("Test: Step 10");
        changeDateOfBirthAndVerifyCurrentAge(75);

        LOGGER.info("Test: Step 11");
        verifyLimitAmounts(coverages, "0", false);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33758", component = CLAIMS_GROUPBENEFITS)
    public void testClaimLimitCalculationChangesForAgeReduction_TC02_2() {
        List<String> coverages = ImmutableList.of(VOL_BTL_BASIC_LIFE_PLAN_PLUS_VOLUNTARY, VOL_ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicySelfAdminData()
                .adjust(tdSpecific().getTestData("TestData_Policy2_REN-33758").resolveLinks()));

        LOGGER.info("Test: Step 13");
        initiateTLClaimWithPolicyAndFillToTab(termLifeClaim.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim2")), PolicyInformationParticipantParticipantInformationTab.class, true);

        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(CURRENT_AGE)).hasValue("74");

        LOGGER.info("Test: Steps 14-15");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(ANNUAL_BASE_SALARY).setValue("86954");

        verifyLimitAmounts(coverages, "2X", "87000", true);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33768", component = CLAIMS_GROUPBENEFITS)
    public void testClaimLimitCalculationChangesForAgeReduction_TC03_1() {
        List<String> coverages = ImmutableList.of(BTL_BASIC_LIFE_PLAN_PLUS_VOLUNTARY, ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicySelfAdminData()
                .adjust(tdSpecific().getTestData("TestData_Policy1_REN_33768").resolveLinks()));

        LOGGER.info("Test: Step 1");
        initiateTLClaimWithPolicyAndFillToTab(termLifeClaim.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim1")), PolicyInformationParticipantParticipantInformationTab.class, true);

        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(CURRENT_AGE)).hasValue("64");

        LOGGER.info("Test: Steps 2-3");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(ANNUAL_BASE_SALARY).setValue("86954");

        verifyLimitAmounts(coverages, "184000", true);

        LOGGER.info("Test: Step 4");
        changeDateOfBirthAndVerifyCurrentAge(69);

        LOGGER.info("Test: Step 5");
        verifyLimitAmounts(coverages, "100000", false);

        LOGGER.info("Test: Step 6");
        changeDateOfBirthAndVerifyCurrentAge(70);

        LOGGER.info("Test: Step 7");
        verifyLimitAmounts(coverages, "92000", false);

        LOGGER.info("Test: Step 8");
        changeDateOfBirthAndVerifyCurrentAge(75);

        LOGGER.info("Test: Step 9");
        verifyLimitAmounts(coverages, "0", false);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33768", component = CLAIMS_GROUPBENEFITS)
    public void testClaimLimitCalculationChangesForAgeReduction_TC03_2() {
        List<String> coverages = ImmutableList.of(BTL_BASIC_LIFE_PLAN_PLUS_VOLUNTARY, ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicySelfAdminData()
                .adjust(tdSpecific().getTestData("TestData_Policy2_REN_33768").resolveLinks()));

        LOGGER.info("Test: Step 9");
        initiateTLClaimWithPolicyAndFillToTab(termLifeClaim.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim2")), PolicyInformationParticipantParticipantInformationTab.class, true);

        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(CURRENT_AGE)).hasValue("74");

        LOGGER.info("Test: Steps 10-11");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(ANNUAL_BASE_SALARY).setValue("86954");

        verifyLimitAmounts(coverages, "92000", true);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33772", component = CLAIMS_GROUPBENEFITS)
    public void testClaimLimitCalculationChangesForAgeReduction_TC04_1() {
        List<String> coverages = ImmutableList.of(VOL_BTL_BASIC_LIFE_PLAN_PLUS_VOLUNTARY, VOL_ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicySelfAdminData()
                .adjust(tdSpecific().getTestData("TestData_Policy1_REN_33772").resolveLinks()));

        LOGGER.info("Test: Step 1");
        initiateTLClaimWithPolicyAndFillToTab(termLifeClaim.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim1")), PolicyInformationParticipantParticipantInformationTab.class, true);

        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(CURRENT_AGE)).hasValue("64");

        LOGGER.info("Test: Steps 2-3");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(ANNUAL_BASE_SALARY).setValue("86954");
        List<String> limitAmountValuesExpected = ImmutableList.of("", "10000.00", "20000.00", "30000.00", "40000.00", "50000.00", "60000.00", "70000.00", "80000.00", "90000.00", "100000.00", "110000.00", "120000.00", "130000.00", "140000.00", "150000.00", "160000.00", "170000.00");
        verifyLimitAmounts(coverages, limitAmountValuesExpected, true);

        LOGGER.info("Test: Step 4");
        changeDateOfBirthAndVerifyCurrentAge(69);

        LOGGER.info("Test: Step 5");
        limitAmountValuesExpected = ImmutableList.of("", "10000.00", "20000.00", "30000.00", "40000.00", "50000.00", "60000.00", "70000.00", "80000.00", "90000.00", "100000.00");
        verifyLimitAmounts(coverages, limitAmountValuesExpected, false);

        LOGGER.info("Test: Step 6");
        changeDateOfBirthAndVerifyCurrentAge(70);

        LOGGER.info("Test: Step 7");
        limitAmountValuesExpected = ImmutableList.of("", "10000.00", "20000.00", "30000.00", "40000.00", "50000.00", "60000.00", "70000.00", "80000.00");
        verifyLimitAmounts(coverages, limitAmountValuesExpected, false);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33772", component = CLAIMS_GROUPBENEFITS)
    public void testClaimLimitCalculationChangesForAgeReduction_TC04_2() {
        List<String> coverages = ImmutableList.of(VOL_BTL_BASIC_LIFE_PLAN_PLUS_VOLUNTARY, VOL_ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicySelfAdminData()
                .adjust(tdSpecific().getTestData("TestData_Policy2_REN_33772").resolveLinks()));

        LOGGER.info("Test: Step 9");
        initiateTLClaimWithPolicyAndFillToTab(termLifeClaim.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim2")), PolicyInformationParticipantParticipantInformationTab.class, true);

        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(CURRENT_AGE)).hasValue("74");

        LOGGER.info("Test: Steps 10-11");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(ANNUAL_BASE_SALARY).setValue("86954");

        LOGGER.info("Test: Steps 10-11");
        List<String> limitAmountValuesExpected = ImmutableList.of("", "10000.00", "20000.00", "30000.00", "40000.00", "50000.00", "60000.00", "70000.00", "80000.00");

        verifyLimitAmounts(coverages, limitAmountValuesExpected, true);
    }

    private void changeDateOfBirthAndVerifyCurrentAge(int age) {
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        policyInformationParticipantParticipantInformationTab.navigateToTab();
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(DATE_OF_BIRTH).setValue(currentDate.minusYears(age).format(MM_DD_YYYY));

        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(CURRENT_AGE)).hasValue(String.valueOf(age));
    }

    private void verifyLimitAmounts(List<String> coverages, String limitAmountExpected, boolean addCoverage) {
        policyInformationParticipantParticipantCoverageTab.navigateToTab();

        coverages.forEach(coverage -> {
            if (addCoverage) {
                policyInformationParticipantParticipantCoverageTab.addCoverage(BASIC_LIFE_PLAN_PLUS_VOLUNTARY, coverage);
            }
            else {
                policyInformationParticipantParticipantCoverageTab.changeCoverageTo(coverage);
            }
            assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(LIMIT_AMOUNT)).hasValue(new Currency(limitAmountExpected).toString());
        });
    }

    private void verifyLimitAmounts(List<String> coverages, List<String> limitAmountExpected, boolean addCoverage) {
        policyInformationParticipantParticipantCoverageTab.navigateToTab();

        coverages.forEach(coverage -> {
            if (addCoverage) {
                policyInformationParticipantParticipantCoverageTab.addCoverage(BASIC_LIFE_PLAN_PLUS_VOLUNTARY, coverage);
            }
            else {
                policyInformationParticipantParticipantCoverageTab.changeCoverageTo(coverage);
            }
            assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(LIMIT_AMOUNT_COMBOBOX)).hasOptions(limitAmountExpected);
        });
    }

    private void verifyLimitAmounts(List<String> coverages, String salaryMultiple, String limitAmountExpected, boolean addCoverage) {
        policyInformationParticipantParticipantCoverageTab.navigateToTab();

        coverages.forEach(coverage -> {
            if (addCoverage) {
                policyInformationParticipantParticipantCoverageTab.addCoverage(BASIC_LIFE_PLAN_PLUS_VOLUNTARY, coverage);
            }
            else {
                policyInformationParticipantParticipantCoverageTab.changeCoverageTo(coverage);
            }
            policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(SALARY_MULTIPLE).setValue(salaryMultiple);
            assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(LIMIT_AMOUNT)).hasValue(new Currency(limitAmountExpected).toString());
        });
    }
}