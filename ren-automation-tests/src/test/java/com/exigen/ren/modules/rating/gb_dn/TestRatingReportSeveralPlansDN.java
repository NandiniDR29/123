package com.exigen.ren.modules.rating.gb_dn;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.rest.rating.model.gb_dn.ManualClaimDetailModel;
import com.exigen.ren.rest.rating.model.gb_dn.RatingReportDentalModel;
import com.exigen.ren.rest.rating.model.gb_dn.TierRateModel;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.RatingReportConstants.*;
import static com.exigen.ren.main.enums.RatingReportConstants.EMPLOYEE_ONLY;
import static com.exigen.ren.main.enums.RatingReportConstants.EMPLOYEE_SPOUSE;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryRatingReportTierColumns.*;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetaData.RATE_TYPE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.RatingReportView.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.RatingReportView.CalculateRateView.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.RATING_INTEGRATION;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestRatingReportSeveralPlansDN extends RatingDentalReportBaseTest {

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-28730"}, component = POLICY_GROUPBENEFITS)
    public void testRatingReportPlansBasicEPOSMajorEPOSFlexPlusFamilyTier() {
        verifyDentalRatingReportSeveralPlansWithFamilyTier(tdSpecific().getTestData("TestData_BasicEPOS_MajorEPOS_FlexPlus"),
                "REN-28730", ImmutableList.of(PLAN_MAJE_POS, PLAN_BASE_POS, PLAN_FLEX_PLUS), false);
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-28730"}, component = POLICY_GROUPBENEFITS)
    public void testRatingReportPlansBasicEPOSMajorEPOSFlexPlusAreaTier() {
        verifyDentalRatingReportSeveralPlansWithAreaTier(tdSpecific().getTestData("TestData_BasicEPOS_MajorEPOS_FlexPlus")
                        .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", RATING.getLabel(), RATE_TYPE.getLabel()), "Area + Tier")
                        .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[2]", RATING.getLabel(), RATE_TYPE.getLabel()), "Area + Tier")
                        .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[3]", RATING.getLabel(), RATE_TYPE.getLabel()), "Area + Tier"),
                "REN-28730", ImmutableList.of(PLAN_MAJE_POS, PLAN_BASE_POS, PLAN_FLEX_PLUS), false);
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-28730"}, component = POLICY_GROUPBENEFITS)
    public void testRatingReportPlansBasicEPOSMajorEPOSFlexPlusMultiOption() {
        String quoteNumber = initiateAndRateQuote(tdSpecific().getTestData("TestData_BasicEPOS_MajorEPOS_FlexPlus"));

        assertSoftly(softly -> {
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED);
            RatingReportDentalModel ratingReportDentalModel = getDentalRatingReportByPolicyNumber(quoteNumber).getModel();
            //Check Multi Option
            expandRatingDetails();
            openRedistributionCalculatorSection();
            LOGGER.info("TEST: REN-28730: Step 34");
            cbxRatingDetailsSelectPlan.setValue(PolicyConstants.PlanDental.BASEPOS);
            chbxMultiOption.setValue(true);
            softly.assertThat(cbxSelectPlanFirst).hasValue(PolicyConstants.PlanDental.BASEPOS);
            softly.assertThat(cbxSelectPlanSecond).hasValue(EMPTY);
            softly.assertThat(cbxSelectPlanFirst.getAllValuesExceptFirstCombineValue())
                    .isEqualTo(ImmutableList.of(PolicyConstants.PlanDental.BASEPOS, PolicyConstants.PlanDental.FLEX_PLUS, PolicyConstants.PlanDental.MAJEPOS));
            //check 'Dual  Option Redistribution' dropdown include: Current Spread (default) Manual Spread
            softly.assertThat(cbxDualOptionRedistribution.getAllValuesExceptFirstCombineValue())
                    .isEqualTo(ImmutableList.of(CURRENT_SPREAD, MANUAL_SPREAD));
            softly.assertThat(cbxDualOptionRedistribution).hasValue(CURRENT_SPREAD);
            //check first plan
            checkSelectPlanMultiOption(ratingReportDentalModel, PLAN_BASE_POS);

            LOGGER.info("TEST: REN-28730: Step 35");
            cbxSelectPlanSecond.setValue(PolicyConstants.PlanDental.MAJEPOS);
            softly.assertThat(cbxSelectPlanSecond.getValue()).contains(PolicyConstants.PlanDental.MAJEPOS);
            checkSelectPlanMultiOption(ratingReportDentalModel, PLAN_MAJE_POS);
            lnkAddPlan.click();
            softly.assertThat(cbxSelectPlanThird).hasValue(EMPTY);
            cbxSelectPlanThird.setValue(PolicyConstants.PlanDental.FLEX_PLUS);
            softly.assertThat(cbxSelectPlanThird.getValue()).contains(PolicyConstants.PlanDental.FLEX_PLUS);
            checkSelectPlanMultiOption(ratingReportDentalModel, PLAN_FLEX_PLUS);

            LOGGER.info("TEST: REN-28730: Step 36");
            cbxDualOptionRedistribution.setValue(MANUAL_SPREAD);
            ImmutableList.of(PLAN_BASE_POS, PLAN_FLEX_PLUS, PLAN_MAJE_POS).forEach(
                    planName -> overrideAdjustments(ratingReportDentalModel, planName)
            );
            updateValuesBeforeRedistributionCalculator(ImmutableList.of(PLAN_BASE_POS, PLAN_FLEX_PLUS, PLAN_MAJE_POS));
            openRedistributionCalculatorSection();
            btnApplyRates.click();

            LOGGER.info("TEST: REN-28730: Step 37"); //Note: steps 38-40  with 'Manual Spread' are verified for plans Aso Aso Alacarte REN-28731
            RatingReportDentalModel updatedRatingReportDentalModel = getDentalRatingReportByPolicyNumber(quoteNumber).getModel();
            checkUpdatedValuesFamilyTier(updatedRatingReportDentalModel, ImmutableList.of(PLAN_BASE_POS, PLAN_FLEX_PLUS, PLAN_MAJE_POS), false);
        });
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-28731"}, component = POLICY_GROUPBENEFITS)
    public void testRatingReportPlansAsoAsoAlacarteFamilyTier() {
        verifyDentalRatingReportSeveralPlansWithFamilyTier(tdSpecific().getTestData("TestData_Aso"),
                "REN-28731", ImmutableList.of(PLAN_ASO, PLAN_ASO_ALACARTE), true);
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-28731"}, component = POLICY_GROUPBENEFITS)
    public void testRatingReportPlansAsoAsoAlacarteAreaTier() {
        verifyDentalRatingReportSeveralPlansWithAreaTier(tdSpecific().getTestData("TestData_Aso")
                        .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", RATING.getLabel(), RATE_TYPE.getLabel()), "Area + Tier")
                        .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[2]", RATING.getLabel(), RATE_TYPE.getLabel()), "Area + Tier"),
                "REN-28731", ImmutableList.of(PLAN_ASO, PLAN_ASO_ALACARTE), true);
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-28731"}, component = POLICY_GROUPBENEFITS)
    public void testRatingReportPlansAsoAsoAlacarteMultiOption() {
        String quoteNumber = initiateAndRateQuote(tdSpecific().getTestData("TestData_Aso"));

        assertSoftly(softly -> {
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED);
            RatingReportDentalModel ratingReportDentalModel = getDentalRatingReportByPolicyNumber(quoteNumber).getModel();
            //Check Multi Option
            expandRatingDetails();
            openRedistributionCalculatorSection();
            LOGGER.info("TEST: REN-28731: Step 34");
            cbxRatingDetailsSelectPlan.setValue(PolicyConstants.PlanDental.ASO);
            chbxMultiOption.setValue(true);
            softly.assertThat(cbxSelectPlanFirst).hasValue(PolicyConstants.PlanDental.ASO);
            softly.assertThat(cbxSelectPlanSecond).hasValue(EMPTY);
            softly.assertThat(cbxSelectPlanFirst.getAllValuesExceptFirstCombineValue())
                    .isEqualTo(ImmutableList.of(PolicyConstants.PlanDental.ASO, PolicyConstants.PlanDental.ASOALC));
            //check 'Dual  Option Redistribution' dropdown include: Current Spread (default) Manual Spread
            softly.assertThat(cbxDualOptionRedistribution.getAllValuesExceptFirstCombineValue())
                    .isEqualTo(ImmutableList.of(CURRENT_SPREAD, MANUAL_SPREAD));
            softly.assertThat(cbxDualOptionRedistribution).hasValue(CURRENT_SPREAD);
            cbxDualOptionRedistribution.setValue(MANUAL_SPREAD);
            //check first plan
            checkSelectPlanMultiOption(ratingReportDentalModel, PLAN_ASO);

            LOGGER.info("TEST: REN-28731: Step 35");
            cbxSelectPlanSecond.setValue(PolicyConstants.PlanDental.ASOALC);
            softly.assertThat(cbxSelectPlanSecond.getValue()).contains(PolicyConstants.PlanDental.ASOALC);
            checkSelectPlanMultiOption(ratingReportDentalModel, PLAN_ASO_ALACARTE);

            LOGGER.info("TEST: REN-28731: Step 38"); //Note: steps 36-37 with 'Current Spread' are verified for plans Basic EPOS, Major EPOS, Flex Plus REN-28730
            ImmutableList.of(PLAN_ASO, PLAN_ASO_ALACARTE).forEach(
                    planName -> overrideAdjustments(ratingReportDentalModel, planName)
            );
            updateValuesBeforeRedistributionCalculator(ImmutableList.of(PLAN_ASO, PLAN_ASO_ALACARTE));
            openRedistributionCalculatorSection();
            btnApplyRates.click();

            LOGGER.info("TEST: REN-28731: Steps 39-40");
            RatingReportDentalModel updatedRatingReportDentalModel = getDentalRatingReportByPolicyNumber(quoteNumber).getModel();
            checkUpdatedValuesFamilyTier(updatedRatingReportDentalModel, ImmutableList.of(PLAN_ASO, PLAN_ASO_ALACARTE), true);
        });
    }

    private void verifyDentalRatingReportSeveralPlansWithFamilyTier(TestData tdQuote, String testCaseId, List<String> plans, boolean isAso) {
        String quoteNumber = initiateAndRateQuote(tdQuote);

        assertSoftly(softly -> {
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED);
            RatingReportDentalModel ratingReportDentalModel = getDentalRatingReportByPolicyNumber(quoteNumber).getModel();

            LOGGER.info(String.format("TEST: %s: Step 6", testCaseId));
            softly.assertThat(isAdminExpensesExpanded()).isFalse();
            softly.assertThat(isRatingDetailsExpanded()).isFalse();

            LOGGER.info(String.format("TEST: %s: Steps 8-10", testCaseId));
            verifyAdminExpenses(ratingReportDentalModel, quoteNumber, isAso);
            closeAdminExpenses();

            LOGGER.info(String.format("TEST: %s: Step 11", testCaseId));
            expandRatingDetails();
            plans.forEach(
                    plan -> {
                        cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(plan));
                        ManualClaimDetailModel manualClaimDetailModel = ratingReportDentalModel.getPlanCalculationModelByPlanName(plan).getManualClaimDetail();
                        verifySummarySectionValues(manualClaimDetailModel);

                        LOGGER.info(String.format("TEST: %s: Steps 12-15", testCaseId));
                        verifyAreaFactorsFamilyTier(manualClaimDetailModel, quoteNumber, plan);

                        LOGGER.info(String.format("TEST: %s: Steps 16-17", testCaseId));
                        verifyEnrolledByState(manualClaimDetailModel);

                    });

            LOGGER.info(String.format("TEST: %s: Step 18", testCaseId));
            openFactorsSection();
            plans.forEach(
                    plan -> {
                        cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(plan));
                        ManualClaimDetailModel manualClaimDetailModel = ratingReportDentalModel.getPlanCalculationModelByPlanName(plan).getManualClaimDetail();
                        softly.assertThat(getFactorsDataFromUI()).isEqualTo(ratingReportDentalModel.getPlanCalculationModelByPlanName(plan).getFactorsDataWithRoundedValues());

                        LOGGER.info(String.format("TEST: %s: Step 19", testCaseId));
                        txtVolLoad.setValue(TEST_VOL_LOAD_VALUE);
                        lnkApplyFactors.click();
                        softly.assertThat(txtVolLoad).hasValue(TEST_VOL_LOAD_VALUE);

                        LOGGER.info(String.format("TEST: %s: Step 20", testCaseId));
                        lnkResetFactors.click();
                        softly.assertThat(txtVolLoad).hasValue(new BigDecimal(manualClaimDetailModel.getVoluntaryLoad()).setScale(4, RoundingMode.HALF_UP).toString());
                    });
            //return updated values to initial state and open Factors tab
            returnUpdatedValuesToInitialBySaveQuote();
            expandRatingDetails();
            openFactorsSection();

            LOGGER.info(String.format("TEST: %s: Step 21", testCaseId));
            plans.forEach(
                    plan -> {
                        cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(plan));
                        ManualClaimDetailModel manualClaimDetailModel = ratingReportDentalModel.getPlanCalculationModelByPlanName(plan).getManualClaimDetail();
                        verifyNetworkDataOnFactorsTab(manualClaimDetailModel);
                    });

            LOGGER.info(String.format("TEST: %s: Step 23-25, 27", testCaseId));
            checkRedistributionCalculatorTabFamilyTier(ImmutableMap.of(
                    UW_ADJUSTMENT_FIRST, "0.00",
                    UW_ADJUSTMENT_SECOND, "0.00",
                    UW_ADJUSTMENT_THIRD, "0.00",
                    CURRENT_RATES.getName(), "0",
                    RENEWAL_RATES.getName(), "0"), ratingReportDentalModel, plans, true);
            plans.forEach(
                    plan -> {
                        cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(plan));
                        checkApplyAdjustments();
                        ratingReportDentalModel.getPlanCalculationModelByPlanName(plan).getTierRates().forEach(
                                tierRateModel -> {
                                    String tier = tierRateModel.getTier();
                                    softly.assertThat(getCellValueTierTable(tier, ADJUSTED_MANUAL_RATES.getName()))
                                            .isNotEqualTo(new BigDecimal(tierRateModel.getAdjManualRate()).setScale(2, RoundingMode.HALF_UP).toString());
                                    softly.assertThat(getCellValueTierTable(tier, ADJUSTED_FORMULA_RATES.getName()))
                                            .isNotEqualTo(new BigDecimal(tierRateModel.getAdjFormulaRate()).setScale(2, RoundingMode.HALF_UP).toString());
                                });

                        LOGGER.info(String.format("TEST: %s: Step 26", testCaseId));
                        checkResetAdjustment();
                    });

            plans.forEach(
                    plan -> {
                        cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(plan));
                        LOGGER.info(String.format("TEST: %s: Step 28, 32", testCaseId));
                        cbxRedistributeRate.setValue(PCT_FROM_CURRENT);
                        checkRedistributeRateButtonsRule(CURRENT_RATES.getName(),
                                ImmutableList.of(EMPLOYEE_CHILD_REN, EMPLOYEE_FAMILY, EMPLOYEE_ONLY, EMPLOYEE_SPOUSE),
                                CURRENT_RATES_TEST_VALUE);

                        LOGGER.info(String.format("TEST: %s: Step 29, 33", testCaseId));
                        cbxRedistributeRate.setValue(PCT_FROM_RENEWAL);
                        checkRedistributeRateButtonsRule(RENEWAL_RATES.getName(),
                                ImmutableList.of(EMPLOYEE_CHILD_REN, EMPLOYEE_FAMILY, EMPLOYEE_ONLY, EMPLOYEE_SPOUSE),
                                RENEWAL_RATES_TEST_VALUE);

                        LOGGER.info(String.format("TEST: %s: Step 30", testCaseId));
                        ImmutableList.of(EMPLOYEE_CHILD_REN, EMPLOYEE_FAMILY, EMPLOYEE_ONLY, EMPLOYEE_SPOUSE).forEach(
                                tierRow -> setCellValueTierTable(tierRow, CURRENT_ENROLLMENT.getName(), CURRENT_ENROLLMENT_TEST_VALUE));

                    });
            //update values on previous tabs and apply all changes
            updateValuesBeforeRedistributionCalculator(plans);
            //Redistribution Calculator
            openRedistributionCalculatorSection();
            plans.forEach(
                    plan -> {
                        cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(plan));
                        //fill adjustments and check new data applied
                        checkApplyAdjustments();
                    });
            //Apply all chenges
            btnApplyRates.click();

            LOGGER.info(String.format("TEST: %s: Step 31", testCaseId));
            //verify updated values
            RatingReportDentalModel updatedRatingReportDentalModel = getDentalRatingReportByPolicyNumber(quoteNumber).getModel();
            checkUpdatedValuesFamilyTier(updatedRatingReportDentalModel, plans, isAso);
        });
    }

    private void verifyDentalRatingReportSeveralPlansWithAreaTier(TestData tdQuote, String testCaseId, List<String> plans, boolean isAso) {
        LOGGER.info(String.format("TEST: %s: Steps 41-43", testCaseId));
        String quoteNumber = initiateAndRateQuote(tdQuote);

        assertSoftly(softly -> {
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED);
            RatingReportDentalModel ratingReportDentalModel = getDentalRatingReportByPolicyNumber(quoteNumber).getModel();

            LOGGER.info(String.format("TEST: %s: Step 6", testCaseId));
            softly.assertThat(isAdminExpensesExpanded()).isFalse();
            softly.assertThat(isRatingDetailsExpanded()).isFalse();

            LOGGER.info(String.format("TEST: %s: Step 44", testCaseId));
            verifyAdminExpenses(ratingReportDentalModel, quoteNumber, isAso);
            closeAdminExpenses();

            LOGGER.info(String.format("TEST: %s: Step 45-46", testCaseId));
            expandRatingDetails();
            plans.forEach(
                    plan -> {
                        cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(plan));
                        ManualClaimDetailModel manualClaimDetailModel = ratingReportDentalModel.getPlanCalculationModelByPlanName(plan).getManualClaimDetail();
                        verifySummarySectionValues(manualClaimDetailModel);
                        //Area Factors
                        softly.assertThat(manualClaimDetailModel.getAreaFactorsWithRoundedValues(true)).isEqualTo(getAreaFactorsDataAreaTierFromUI());
                        //Enrolled By State
                        verifyEnrolledByState(manualClaimDetailModel);
                    });

            openFactorsSection();
            plans.forEach(
                    plan -> {
                        cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(plan));
                        ManualClaimDetailModel manualClaimDetailModel = ratingReportDentalModel.getPlanCalculationModelByPlanName(plan).getManualClaimDetail();
                        softly.assertThat(getFactorsDataFromUI()).isEqualTo(ratingReportDentalModel.getPlanCalculationModelByPlanName(plan).getFactorsDataWithRoundedValues());
                        txtVolLoad.setValue(TEST_VOL_LOAD_VALUE);
                        lnkApplyFactors.click();
                        softly.assertThat(txtVolLoad).hasValue(TEST_VOL_LOAD_VALUE);
                        lnkResetFactors.click();
                        softly.assertThat(txtVolLoad).hasValue(new BigDecimal(manualClaimDetailModel.getVoluntaryLoad()).setScale(4, RoundingMode.HALF_UP).toString());
                    });
            //return updated values to initial state and open Factors tab
            returnUpdatedValuesToInitialBySaveQuote();
            expandRatingDetails();
            openFactorsSection();

            LOGGER.info(String.format("TEST: %s: Step 47", testCaseId));
            plans.forEach(
                    plan -> {
                        cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(plan));
                        ManualClaimDetailModel manualClaimDetailModel = ratingReportDentalModel.getPlanCalculationModelByPlanName(plan).getManualClaimDetail();
                        verifyNetworkDataOnFactorsTab(manualClaimDetailModel);
                    });

            LOGGER.info(String.format("TEST: %s: Steps 48-50", testCaseId));
            checkRedistributionCalculatorTabAreaTier(ImmutableMap.of(
                    UW_ADJUSTMENT_FIRST, "0.00",
                    UW_ADJUSTMENT_SECOND, "0.00",
                    UW_ADJUSTMENT_THIRD, "0.00"),
                    ratingReportDentalModel, plans);
            plans.forEach(
                    plan -> {
                        cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(plan));
                        checkApplyAdjustments();
                        //Check TIER Rates updated and not equal to initial values
                        List<TierRateModel> tierRateModelsFromREST = ratingReportDentalModel.getPlanCalculationModelByPlanName(plan).getTierRateModelsWithAdaptedToExpectedUIValues();
                        List<TierRateModel> tierRateModelsFromUI = getTierRateModelsFromRedistributionCalculatorTab();
                        softly.assertThat(tierRateModelsFromUI.size()).isEqualTo(tierRateModelsFromREST.size());
                        softly.assertThat(tierRateModelsFromUI).isNotEqualTo(tierRateModelsFromREST);
                        //Check Reset Adjustment
                        checkResetAdjustment();
                    });

            //update values on previous tabs and apply all changes
            //Admin Expenses
            expandAdminExpenses();
            txtNumberOfClaimsPerEmployee.applyValue(TEST_NUMBER_OF_CLAIMS_PER_EMPLOYEE);
            txtUwExceptionDollarAmount.applyValue(TEST_UW_EXCEPTION_DOLLAR_AMOUNT);
            txtRiskAdjustmentPercent.applyValue(TEST_RISK_ADJUSTMENT_PERCENT);
            txtUwExceptionPercent.applyValue(TEST_UW_EXCEPTION_PERCENT);
            closeAdminExpenses();
            expandRatingDetails();
            plans.forEach(
                    plan -> {
                        cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(plan));
                        //Factors tab
                        openFactorsSection();
                        txtVolLoad.setValue(TEST_VOL_LOAD_VALUE);
                        lnkApplyFactors.click();
                        //Redistribution Calculator
                        openRedistributionCalculatorSection();
                        checkApplyAdjustments();
                    });
            LOGGER.info(String.format("TEST: %s: Steps 51", testCaseId));
            //Apply all chenges
            btnApplyRates.click();

            LOGGER.info(String.format("TEST: %s: Steps 52-54", testCaseId));
            //verify updated values
            RatingReportDentalModel updatedRatingReportDentalModel = getDentalRatingReportByPolicyNumber(quoteNumber).getModel();
            //Admin Expenses
            expandAdminExpenses();
            if (isAso) {
                softly.assertThat(updatedRatingReportDentalModel.getAsoAdminCostWithRoundedValues()).isEqualTo(getAsoAdminCostModelFromUI());
            }
            else {
                softly.assertThat(tblASO).isAbsent();
            }
            softly.assertThat(updatedRatingReportDentalModel.getAdminCostsWithRoundedValues()).isEqualTo(getFullyInsuredDataFromUI());
            softly.assertThat(txtNumberOfClaimsPerEmployee).hasValue(TEST_NUMBER_OF_CLAIMS_PER_EMPLOYEE);
            softly.assertThat(txtUwExceptionDollarAmount).hasValue(TEST_UW_EXCEPTION_DOLLAR_AMOUNT);
            softly.assertThat(txtRiskAdjustmentPercent).hasValue(TEST_RISK_ADJUSTMENT_PERCENT);
            softly.assertThat(txtUwExceptionPercent).hasValue(TEST_UW_EXCEPTION_PERCENT);
            closeAdminExpenses();
            expandRatingDetails();
            plans.forEach(
                    plan -> {
                        cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(plan));
                        openDemographicSection();
                        //Demographic tab
                        //Summary
                        verifySummarySectionValues(updatedRatingReportDentalModel.getPlanCalculationModelByPlanName(plan).getManualClaimDetail());
                        //Area Factors
                        ManualClaimDetailModel updatedManualClaimDetailModel = updatedRatingReportDentalModel.getPlanCalculationModelByPlanName(plan).getManualClaimDetail();
                        softly.assertThat(updatedManualClaimDetailModel.getAreaFactorsWithRoundedValues(true)).isEqualTo(getAreaFactorsDataAreaTierFromUI());
                        //Enrolled By State
                        verifyEnrolledByState(updatedManualClaimDetailModel);
                        //Factors
                        openFactorsSection();
                        Map<String, String> factorsDataFromRest = updatedRatingReportDentalModel.getPlanCalculationModelByPlanName(plan).getFactorsDataWithRoundedValues();
                        softly.assertThat(getFactorsDataFromUI()).isEqualTo(factorsDataFromRest);
                        softly.assertThat(factorsDataFromRest.get(FactorsSectionFields.VOL_LOAD)).isEqualTo(TEST_VOL_LOAD_VALUE);
                        verifyNetworkDataOnFactorsTab(updatedManualClaimDetailModel);
                        //check Redistribution Calculator
                        checkUpdatedRedistributionCalculatorSection(plan, CURRENT_ENROLLMENT_TEST_VALUE, updatedRatingReportDentalModel, false, false);
                    });
        });
    }

    private void checkUpdatedValuesFamilyTier(RatingReportDentalModel updatedRatingReportDentalModel, List<String> plans, boolean isAso) {
        assertSoftly(softly -> {
            //Admin Expenses
            expandAdminExpenses();
            if (isAso) {
                softly.assertThat(updatedRatingReportDentalModel.getAsoAdminCostWithRoundedValues()).isEqualTo(getAsoAdminCostModelFromUI());
            }
            else {
                softly.assertThat(tblASO).isAbsent();
            }
            softly.assertThat(updatedRatingReportDentalModel.getAdminCostsWithRoundedValues()).isEqualTo(getFullyInsuredDataFromUI());
            softly.assertThat(txtNumberOfClaimsPerEmployee).hasValue(TEST_NUMBER_OF_CLAIMS_PER_EMPLOYEE);
            softly.assertThat(txtUwExceptionDollarAmount).hasValue(TEST_UW_EXCEPTION_DOLLAR_AMOUNT);
            softly.assertThat(txtRiskAdjustmentPercent).hasValue(TEST_RISK_ADJUSTMENT_PERCENT);
            softly.assertThat(txtUwExceptionPercent).hasValue(TEST_UW_EXCEPTION_PERCENT);
            closeAdminExpenses();
            //Rating Details
            expandRatingDetails();
            plans.forEach(
                    plan -> {
                        LOGGER.info(String.format("Check updated rating details for plan: %s", PLANS_MAPPING.get(plan)));
                        cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(plan));
                        //Demographic tab
                        //Summary
                        openDemographicSection();
                        verifySummarySectionValues(updatedRatingReportDentalModel.getPlanCalculationModelByPlanName(plan).getManualClaimDetail());
                        //Area Factors
                        softly.assertThat(lblIndemnity).hasValue(TEST_INDEMNITY_VALUE);
                        softly.assertThat(lblPPO).hasValue(TEST_PPO_VALUE);
                        softly.assertThat(lblPPONetworkPenetration).hasValue(formatValueToPct(TEST_PPO_NETWORK_PENETRATION_VALUE));
                        if (PLAN_ASO_ALACARTE.equals(plan)) {
                            softly.assertThat(lblEPOAreaFactor).hasValue(TEST_EPO_AREA_FACTOR_VALUE);
                            softly.assertThat(lblEPONetworkPenetration).hasValue(formatValueToPct(TEST_EPO_NETWORK_PENETRATION_VALUE));
                        }
                        //Enrolled By State
                        ManualClaimDetailModel updatedManualClaimDetailModel = updatedRatingReportDentalModel.getPlanCalculationModelByPlanName(plan).getManualClaimDetail();
                        verifyEnrolledByState(updatedManualClaimDetailModel);
                        //Factors
                        openFactorsSection();
                        Map<String, String> factorsDataFromRest = updatedRatingReportDentalModel.getPlanCalculationModelByPlanName(plan).getFactorsDataWithRoundedValues();
                        softly.assertThat(getFactorsDataFromUI()).isEqualTo(factorsDataFromRest);
                        softly.assertThat(factorsDataFromRest.get(FactorsSectionFields.VOL_LOAD)).isEqualTo(TEST_VOL_LOAD_VALUE);
                        verifyNetworkDataOnFactorsTab(updatedManualClaimDetailModel);
                        //check Redistribution Calculator
                        checkUpdatedRedistributionCalculatorSection(plan, CURRENT_ENROLLMENT_TEST_VALUE, updatedRatingReportDentalModel, true, true);
                    });
        });

    }

    private void updateValuesBeforeRedistributionCalculator(List<String> plans) {
        //Admin Expenses
        expandAdminExpenses();
        txtNumberOfClaimsPerEmployee.applyValue(TEST_NUMBER_OF_CLAIMS_PER_EMPLOYEE);
        txtUwExceptionDollarAmount.applyValue(TEST_UW_EXCEPTION_DOLLAR_AMOUNT);
        txtRiskAdjustmentPercent.applyValue(TEST_RISK_ADJUSTMENT_PERCENT);
        txtUwExceptionPercent.applyValue(TEST_UW_EXCEPTION_PERCENT);
        closeAdminExpenses();
        expandRatingDetails();
        plans.forEach(
                plan -> {
                    cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(plan));
                    //Demographic tab
                    openDemographicSection();
                    lnkEditAreaFactors.click();
                    txtIndemnity.setValue(TEST_INDEMNITY_VALUE);
                    txtPPO.setValue(TEST_PPO_VALUE);
                    txtPPONetworkPenetration.setValue(formatValueToPct(TEST_PPO_NETWORK_PENETRATION_VALUE).replace("%", ""));
                    if (PLAN_ASO_ALACARTE.equals(plan)) {
                        txtEPOAreaFactor.setValue(TEST_EPO_AREA_FACTOR_VALUE);
                        txtEPONetworkPenetration.setValue(formatValueToPct(TEST_EPO_NETWORK_PENETRATION_VALUE).replace("%", ""));
                    }
                    lnkSaveAreaFactors.click();
                    //Factors tab
                    openFactorsSection();
                    txtVolLoad.setValue(TEST_VOL_LOAD_VALUE);
                    lnkApplyFactors.click();
                });
    }


}
