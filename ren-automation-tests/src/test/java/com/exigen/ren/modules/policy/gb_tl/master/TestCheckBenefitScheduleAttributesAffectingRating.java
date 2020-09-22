package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.BenefitType.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.VOLUNTARY_LIFE_PLAN;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.VOLUNTEER_FIRE_FIGHTERS;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.ProductConstants.StatusWhileCreating.DATA_GATHERING;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckBenefitScheduleAttributesAffectingRating extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {
    private final String NO_PREMIUM_INFORMATION = "No premium information is available.";
    private Currency benefitAmount = new Currency(10000);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37136", component = POLICY_GROUPBENEFITS)
    public void testCheckBenefitScheduleEBL() {
        initiateQuote();

        LOGGER.info("TEST REN-37136: Step 1");
        planDefinitionTab.selectDefaultPlan();
        PlanDefinitionTab.removeCoverage(ADD);
        PlanDefinitionTab.removeCoverage(DEP_BTL);

        checkBenefitScheduleForBasicLife(tdSpecific().getTestData("TestDataEBL"), BTL, 1);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37136", component = POLICY_GROUPBENEFITS)
    public void testCheckBenefitScheduleEBADD() {
        initiateQuote();

        LOGGER.info("TEST REN-37136: Step 10");
        planDefinitionTab.selectDefaultPlan();
        PlanDefinitionTab.removeCoverage(DEP_BTL);

        checkBenefitScheduleForBasicLife(tdSpecific().getTestData("TestDataEBADD"), ADD, 2);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37167", component = POLICY_GROUPBENEFITS)
    public void testCheckBenefitScheduleCVL() {
        initiateQuote();

        LOGGER.info("TEST REN-37167: Step 1-3");
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(VOLUNTARY_LIFE_PLAN));
        PlanDefinitionTab.removeCoverage(DEP_VOL_ADD);
        PlanDefinitionTab.removeCoverage(SP_VOL_BTL);
        PlanDefinitionTab.removeCoverage(VOL_ADD);
        PlanDefinitionTab.changeCoverageTo(DEP_VOL_BTL);

        checkBenefitScheduleForVoluntaryLife(tdSpecific().getTestData("TestDataCVL"), DEP_VOL_BTL, 2);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37167", component = POLICY_GROUPBENEFITS)
    public void testCheckBenefitScheduleDVADD() {
        initiateQuote();

        LOGGER.info("TEST REN-37167: Step 1-3");
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(VOLUNTARY_LIFE_PLAN));
        PlanDefinitionTab.removeCoverage(SP_VOL_BTL);
        PlanDefinitionTab.changeCoverageTo(DEP_VOL_ADD);

        checkBenefitScheduleForVoluntaryLife(tdSpecific().getTestData("TestDataDVADD"), DEP_VOL_ADD, 4);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37247", component = POLICY_GROUPBENEFITS)
    public void testCheckBenefitScheduleEVL() {
        initiateQuote();

        LOGGER.info("TEST REN-37247: Step 1");
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(VOLUNTARY_LIFE_PLAN));
        PlanDefinitionTab.removeCoverage(DEP_VOL_ADD);
        PlanDefinitionTab.removeCoverage(SP_VOL_BTL);
        PlanDefinitionTab.removeCoverage(VOL_ADD);
        PlanDefinitionTab.removeCoverage(DEP_VOL_BTL);

        checkBenefitScheduleForWithoutChild(tdSpecific().getTestData("TestDataEVL"), VOL_BTL, 1);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37247", component = POLICY_GROUPBENEFITS)
    public void testCheckBenefitScheduleEVADD() {
        initiateQuote();

        LOGGER.info("TEST REN-37247: Step 1");
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(VOLUNTARY_LIFE_PLAN));
        PlanDefinitionTab.removeCoverage(DEP_VOL_ADD);
        PlanDefinitionTab.removeCoverage(SP_VOL_BTL);
        PlanDefinitionTab.removeCoverage(DEP_VOL_BTL);
        PlanDefinitionTab.changeCoverageTo(VOL_ADD);

        checkBenefitScheduleForWithoutChild(tdSpecific().getTestData("TestDataEVADD"), VOL_ADD, 2);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37256", component = POLICY_GROUPBENEFITS)
    public void testCheckBenefitScheduleVFF() {
        initiateQuote();

        LOGGER.info("TEST REN-37256: Step 1");
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(VOLUNTEER_FIRE_FIGHTERS));
        PlanDefinitionTab.removeCoverage(ADD);
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_TYPE)).hasValue(SPECIFIED_AMOUNT_SINGLE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.LINE_OF_DUTY_BENEFIT)).isPresent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.FLAT_BENEFIT_AMOUNT)).hasValue(benefitAmount.toString());
        });

        LOGGER.info("TEST REN-37256: Step 2, 3");
        planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.LINE_OF_DUTY_BENEFIT).setValueByIndex(1);
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(tdSpecific().getTestData("TestDataVFF"), PlanDefinitionTab.class, PremiumSummaryTab.class, true);
        PremiumSummaryTab.buttonRate.click();
        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);

        LOGGER.info("TEST REN-37256: Step 4");
        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.LINE_OF_DUTY_BENEFIT).setValueByIndex(1);
        changeBenefitFieldAndCheckPremiumTab(BTL, 1);
    }

    private void initiateQuote() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PlanDefinitionTab.class);
    }

    private void checkBenefitScheduleForBasicLife(TestData testData, String coverage, int rowsNumber) {
        LOGGER.info("TEST REN-37136: Step 2, 3");
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_TYPE))
                    .hasValue(SPECIFIED_AMOUNT_SINGLE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.FLAT_BENEFIT_AMOUNT))
                    .hasValue(benefitAmount.toString());
        });

        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(testData, PlanDefinitionTab.class, PremiumSummaryTab.class, true);
        PremiumSummaryTab.buttonRate.click();
        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);

        LOGGER.info("TEST REN-37136: Step 4, 5");
        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.FLAT_BENEFIT_AMOUNT).setValue("$11,000.00");
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        LOGGER.info("TEST REN-37136: Step 6");
        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_TYPE).setValue(SALARY_MULTIPLIER_SINGLE);
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.SALARY_MULTIPLE)).hasValue("1x");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.ADDITIONAL_AMOUNT)).hasValue(benefitAmount.toString());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_MINIMUM_AMOUNT)).hasValue(benefitAmount.toString());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_MAXIMUM_AMOUNT)).hasValue("$200,000.00");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.EARNING_DEFINITION)).hasValue("Salary");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.ROUNDING_METHOD)).isPresent();
        });
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        LOGGER.info("TEST REN-37136: Step 7, 8, 9");
        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.SALARY_MULTIPLE).setValueByIndex(4);
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.ADDITIONAL_AMOUNT).setValue("$12,000.00");
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_MINIMUM_AMOUNT).setValue("$12,000.00");
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_MAXIMUM_AMOUNT).setValue("$220,000.00");
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.EARNING_DEFINITION).setValueByIndex(8);
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);
    }

    private void checkBenefitScheduleForVoluntaryLife(TestData testData, String coverage, int rowsNumber) {
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.BENEFIT_TYPE)).hasValue(SPECIFIED_AMOUNT_RANGE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.MAXIMUM_BENEFIT_AMOUNT)).hasValue(benefitAmount.toString());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.MINIMUM_BENEFIT_AMOUNT)).hasValue("$1,000.00");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.INCREMENT)).hasValue("$1,000.00");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.COVERAGE_UP_TO_MAX_OF_EMPLOYEE_COVERAGE)).hasValue("50%");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.LIVE_BIRTH_TO_FOURTEEN_DAYS)).hasValue("$500.00");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.FIFTEEN_DAYS_TO_SIX_MONTH)).hasValue("$500.00");
        });

        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(testData, PlanDefinitionTab.class, PremiumSummaryTab.class, true);
        PremiumSummaryTab.buttonRate.click();
        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);

        LOGGER.info("TEST REN-37167: Step 4, 5");
        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.MINIMUM_BENEFIT_AMOUNT).setValue("$2,000.00");
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.MAXIMUM_BENEFIT_AMOUNT).setValue("$20,000.00");
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.INCREMENT).setValue("$2,000.00");
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.COVERAGE_UP_TO_MAX_OF_EMPLOYEE_COVERAGE).setValue("100%");
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.LIVE_BIRTH_TO_FOURTEEN_DAYS).setValue("$400.00");
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.FIFTEEN_DAYS_TO_SIX_MONTH).setValue("$400.00");
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        LOGGER.info("TEST REN-37167: Step 6-8");
        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.BENEFIT_TYPE).setValue(SPECIFIED_AMOUNT_SINGLE);
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.COVERAGE_UP_TO_MAX_OF_EMPLOYEE_COVERAGE)).hasValue("100%");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.LIVE_BIRTH_TO_FOURTEEN_DAYS)).hasValue("$400.00");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.FIFTEEN_DAYS_TO_SIX_MONTH)).hasValue("$400.00");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.SIX_MONTHS_TO_MAX_AGE)).hasValue(benefitAmount.toString());
        });
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        LOGGER.info("TEST REN-37167: Step 9, 10");
        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.SIX_MONTHS_TO_MAX_AGE).setValue("$11,000.00");
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        LOGGER.info("TEST REN-37167: Step 11-13");
        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.BENEFIT_TYPE).setValue(SPECIFIED_AMOUNT_MULTIPLE);
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.COVERAGE_UP_TO_MAX_OF_EMPLOYEE_COVERAGE)).hasValue("100%");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.LIVE_BIRTH_TO_FOURTEEN_DAYS)).hasValue("$400.00");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.FIFTEEN_DAYS_TO_SIX_MONTH)).hasValue("$400.00");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.SPECIFIED_AMOUNTS)).isPresent();
        });
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        LOGGER.info("TEST REN-37167: Step 14, 15");
        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.SPECIFIED_AMOUNTS).setValue(ImmutableList.of("$6,000"));
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        LOGGER.info("TEST REN-37167: Step 16-18");
        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.BENEFIT_TYPE).setValue(PERCENTAGE_OF_EMPLOYEE_AMOUNT);
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);
    }

    private void checkBenefitScheduleForWithoutChild(TestData testData, String coverage, int rowsNumber) {
        LOGGER.info("TEST REN-37247: Step 2, 3");
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_TYPE)).hasValue(SPECIFIED_AMOUNT_RANGE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.MAXIMUM_SALARY_MULTIPLE)).hasValue("5x");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_MINIMUM_AMOUNT)).hasValue(benefitAmount.toString());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_MAXIMUM_AMOUNT)).hasValue("$500,000.00");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.EARNING_DEFINITION)).hasValue("Salary");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.INCREMENT)).hasValue(benefitAmount.toString());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.ROUNDING_METHOD)).isPresent();
        });

        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(testData, PlanDefinitionTab.class, PremiumSummaryTab.class, true);
        PremiumSummaryTab.buttonRate.click();
        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);

        LOGGER.info("TEST REN-37247: Step 4, 5");
        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_MINIMUM_AMOUNT).setValue("$12,000.00");
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_MAXIMUM_AMOUNT).setValue("$600,000.00");
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.INCREMENT).setValue("$5,000.00");
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.ROUNDING_METHOD).setValueByIndex(3);
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.EARNING_DEFINITION).setValueByIndex(8);
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        LOGGER.info("TEST REN-37247: Step 6-8");
        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_TYPE).setValue(SALARY_MULTIPLIER_MULTIPLE);
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.SALARY_MULTIPLES)).isPresent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_MINIMUM_AMOUNT)).hasValue("$12,000.00");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_MAXIMUM_AMOUNT)).hasValue("$600,000.00");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.EARNING_DEFINITION)).isPresent();
        });
        planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.SALARY_MULTIPLES).setValue(ImmutableList.of("3x"));
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        LOGGER.info("TEST REN-37247: Step 9, 10");
        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_TYPE).setValue(SPECIFIED_AMOUNT_MULTIPLE);
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.MAXIMUM_SALARY_MULTIPLE)).hasValue("5x");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.EARNING_DEFINITION)).isPresent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.SPECIFIED_AMOUNTS)).isPresent();
        });
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);

        LOGGER.info("TEST REN-37247: Step 11");
        planDefinitionTab.navigate().getAssetList().getAsset(BENEFIT_SCHEDULE_CHILD).getAsset(BenefitScheduleChildMetaData.SPECIFIED_AMOUNTS).setValue(ImmutableList.of("$6,000"));
        changeBenefitFieldAndCheckPremiumTab(coverage, rowsNumber);
    }

    private void changeBenefitFieldAndCheckPremiumTab(String coverage, int rowsNumber) {
        premiumSummaryTab.navigate();
        checkPremiumTab(DATA_GATHERING, NO_PREMIUM_INFORMATION, 1);
        PremiumSummaryTab.buttonRate.click();
        checkPremiumTab(PREMIUM_CALCULATED, coverage, rowsNumber);
    }

    private void checkPremiumTab(String quoteStatus, String coverage, int rowsNumber) {
        assertSoftly(softly -> {
            softly.assertThat(PremiumSummaryTab.tableCoveragesName.getRow(rowsNumber)).hasCellWithValue(COVERAGE_NAME.getName(), coverage);
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(quoteStatus);
        });
    }
}
