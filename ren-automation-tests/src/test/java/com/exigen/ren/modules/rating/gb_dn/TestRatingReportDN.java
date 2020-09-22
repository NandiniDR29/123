package com.exigen.ren.modules.rating.gb_dn;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.rest.rating.model.gb_dn.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.RatingReportConstants.*;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryRatingReportTierColumns.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetaData.RATE_TYPE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.RatingReportView.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.RatingReportView.CalculateRateView.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRatingReportDN extends RatingDentalReportBaseTest {

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-28469"}, component = POLICY_GROUPBENEFITS)
    public void testRatingReportPlanAlaCarteFamilyTier() {
        verifyRatingReportDNFamilyTier(tdSpecific().getTestData("TestData_ALaCarte"), "REN-28469", PLAN_ALACARTE);
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-28469"}, component = POLICY_GROUPBENEFITS)
    public void testRatingReportPlanAlaCarteAreaTier() {
        verifyRatingReportDNAreaTier(tdSpecific().getTestData("TestData_ALaCarte")
                        .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", RATING.getLabel(), RATE_TYPE.getLabel()), "Area + Tier"),
                "REN-28469", PLAN_ALACARTE);
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-28729"}, component = POLICY_GROUPBENEFITS)
    public void testRatingReportPlanTripleAdvantageFamilyTier() {
        verifyRatingReportDNFamilyTier(tdSpecific().getTestData("TestData_PlanTripleAdvantage"), "REN-28729", PLAN_TRIP_ADV);
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-28729"}, component = POLICY_GROUPBENEFITS)
    public void testRatingReportPlanTripleAdvantageAreaTier() {
        verifyRatingReportDNAreaTier(tdSpecific().getTestData("TestData_PlanTripleAdvantage")
                        .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", RATING.getLabel(), RATE_TYPE.getLabel()), "Area + Tier"),
                "REN-28729", PLAN_TRIP_ADV);
    }

    private void verifyRatingReportDNFamilyTier(TestData tdQuote, String testCaseId, String planName) {
        String quoteNumber = initiateAndRateQuote(tdQuote);

        assertSoftly(softly -> {
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED);
            RatingReportDentalModel ratingReportDentalModel = getDentalRatingReportByPolicyNumber(quoteNumber).getModel();
            ManualClaimDetailModel manualClaimDetailModel = ratingReportDentalModel.getPlanCalculations().get(0).getManualClaimDetail();

            LOGGER.info(String.format("TEST: %s: Step 6", testCaseId));
            softly.assertThat(isAdminExpensesExpanded()).isFalse();
            softly.assertThat(isRatingDetailsExpanded()).isFalse();

            LOGGER.info(String.format("TEST: %s: Steps 8-10", testCaseId));
            verifyAdminExpenses(ratingReportDentalModel, quoteNumber, false);
            closeAdminExpenses();

            LOGGER.info(String.format("TEST: %s: Step 11", testCaseId));
            expandRatingDetails();
            verifySummarySectionValues(manualClaimDetailModel);

            LOGGER.info(String.format("TEST: %s: Steps 12-15", testCaseId));
            verifyAreaFactorsFamilyTier(manualClaimDetailModel, quoteNumber, planName);

            LOGGER.info(String.format("TEST: %s: Steps 16-17", testCaseId));
            verifyEnrolledByState(manualClaimDetailModel);

            LOGGER.info(String.format("TEST: %s: Step 18", testCaseId));
            openFactorsSection();
            softly.assertThat(getFactorsDataFromUI()).isEqualTo(ratingReportDentalModel.getPlanCalculations().get(0).getFactorsDataWithRoundedValues());

            LOGGER.info(String.format("TEST: %s: Step 19", testCaseId));
            txtVolLoad.setValue(TEST_VOL_LOAD_VALUE);
            lnkApplyFactors.click();
            softly.assertThat(txtVolLoad).hasValue(TEST_VOL_LOAD_VALUE);

            LOGGER.info(String.format("TEST: %s: Step 20", testCaseId));
            lnkResetFactors.click();
            softly.assertThat(txtVolLoad).hasValue(new BigDecimal(manualClaimDetailModel.getVoluntaryLoad()).setScale(4, RoundingMode.HALF_UP).toString());

            //return updated values to initial state and open Factors tab
            returnUpdatedValuesToInitialBySaveQuote();
            expandRatingDetails();
            openFactorsSection();

            LOGGER.info(String.format("TEST: %s: Step 21", testCaseId));
            verifyNetworkDataOnFactorsTab(manualClaimDetailModel);

            LOGGER.info(String.format("TEST: %s: Step 23-25, 27", testCaseId));
            checkRedistributionCalculatorTabFamilyTier(ImmutableMap.of(
                    UW_ADJUSTMENT_FIRST, "0.00",
                    UW_ADJUSTMENT_SECOND, "0.00",
                    UW_ADJUSTMENT_THIRD, "0.00",
                    CURRENT_RATES.getName(), "0",
                    RENEWAL_RATES.getName(), "0"), ratingReportDentalModel, ImmutableList.of(planName), false);
            checkApplyAdjustments();
            ratingReportDentalModel.getPlanCalculationModelByPlanName(planName).getTierRates().forEach(
                    tierRateModel -> {
                        String tier = tierRateModel.getTier();
                        softly.assertThat(getCellValueTierTable(tier, ADJUSTED_MANUAL_RATES.getName()))
                                .isNotEqualTo(new BigDecimal(tierRateModel.getAdjManualRate()).setScale(2, RoundingMode.HALF_UP).toString());
                        softly.assertThat(getCellValueTierTable(tier, ADJUSTED_FORMULA_RATES.getName()))
                                .isNotEqualTo(new BigDecimal(tierRateModel.getAdjFormulaRate()).setScale(2, RoundingMode.HALF_UP).toString());
                    });
            LOGGER.info(String.format("TEST: %s: Step 26", testCaseId));
            checkResetAdjustment();

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

            ImmutableList.of(EMPLOYEE_CHILD_REN, EMPLOYEE_FAMILY, EMPLOYEE_ONLY, EMPLOYEE_SPOUSE).forEach(
                    tierRow -> setCellValueTierTable(tierRow, CURRENT_ENROLLMENT.getName(), CURRENT_ENROLLMENT_TEST_VALUE));

            LOGGER.info(String.format("TEST: %s: Step 30", testCaseId));
            //update values on previous tabs and apply all changes
            //Admin Expenses
            expandAdminExpenses();
            txtNumberOfClaimsPerEmployee.applyValue(TEST_NUMBER_OF_CLAIMS_PER_EMPLOYEE);
            txtUwExceptionDollarAmount.applyValue(TEST_UW_EXCEPTION_DOLLAR_AMOUNT);
            txtRiskAdjustmentPercent.applyValue(TEST_RISK_ADJUSTMENT_PERCENT);
            txtUwExceptionPercent.applyValue(TEST_UW_EXCEPTION_PERCENT);
            //Demographic tab
            openDemographicSection();
            lnkEditAreaFactors.click();
            txtIndemnity.setValue(TEST_INDEMNITY_VALUE);
            txtPPO.setValue(TEST_PPO_VALUE);
            txtPPONetworkPenetration.setValue(formatValueToPct(TEST_PPO_NETWORK_PENETRATION_VALUE).replace("%", ""));
            if (PLAN_TRIP_ADV.equals(planName)) {
                txtEPOAreaFactor.setValue(TEST_EPO_AREA_FACTOR_VALUE);
                txtEPONetworkPenetration.setValue(formatValueToPct(TEST_EPO_NETWORK_PENETRATION_VALUE).replace("%", ""));
            }
            //Factors tab
            openFactorsSection();
            txtVolLoad.setValue(TEST_VOL_LOAD_VALUE);
            lnkApplyFactors.click();
            //Redistribution Calculator
            openRedistributionCalculatorSection();
            checkApplyAdjustments();
            btnApplyRates.click();

            LOGGER.info(String.format("TEST: %s: Step 31", testCaseId));
            //verify updated values
            RatingReportDentalModel updatedRatingReportDentalModel = getDentalRatingReportByPolicyNumber(quoteNumber).getModel();
            ManualClaimDetailModel updatedManualClaimDetailModel = updatedRatingReportDentalModel.getPlanCalculations().get(0).getManualClaimDetail();
            expandAdminExpenses();
            softly.assertThat(updatedRatingReportDentalModel.getAdminCostsWithRoundedValues()).isEqualTo(getFullyInsuredDataFromUI());
            softly.assertThat(txtNumberOfClaimsPerEmployee).hasValue(TEST_NUMBER_OF_CLAIMS_PER_EMPLOYEE);
            softly.assertThat(txtUwExceptionDollarAmount).hasValue(TEST_UW_EXCEPTION_DOLLAR_AMOUNT);
            softly.assertThat(txtRiskAdjustmentPercent).hasValue(TEST_RISK_ADJUSTMENT_PERCENT);
            softly.assertThat(txtUwExceptionPercent).hasValue(TEST_UW_EXCEPTION_PERCENT);
            //Demographic
            expandRatingDetails();
            //Summary
            verifySummarySectionValues(updatedRatingReportDentalModel.getPlanCalculations().get(0).getManualClaimDetail());
            //Area Factors
            softly.assertThat(lblIndemnity).hasValue(TEST_INDEMNITY_VALUE);
            softly.assertThat(lblPPO).hasValue(TEST_PPO_VALUE);
            softly.assertThat(lblPPONetworkPenetration).hasValue(formatValueToPct(TEST_PPO_NETWORK_PENETRATION_VALUE));
            //Enrolled By State
            verifyEnrolledByState(updatedManualClaimDetailModel);
            //Factors
            openFactorsSection();
            Map<String, String> factorsDataFromRest = updatedRatingReportDentalModel.getPlanCalculations().get(0).getFactorsDataWithRoundedValues();
            softly.assertThat(getFactorsDataFromUI()).isEqualTo(factorsDataFromRest);
            softly.assertThat(factorsDataFromRest.get(FactorsSectionFields.VOL_LOAD)).isEqualTo(TEST_VOL_LOAD_VALUE);
            verifyNetworkDataOnFactorsTab(updatedManualClaimDetailModel);
            //check Redistribution Calculator
            checkUpdatedRedistributionCalculatorSection(planName, CURRENT_ENROLLMENT_TEST_VALUE, updatedRatingReportDentalModel, true, false);
        });
    }

    private void verifyRatingReportDNAreaTier(TestData tdQuotem, String testCaseId, String planName) {
        LOGGER.info(String.format("TEST: %s: Step 35", testCaseId));
        String quoteNumber = initiateAndRateQuote(tdQuotem);
        assertSoftly(softly -> {
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED);

            LOGGER.info(String.format("TEST: %s: Step 36", testCaseId));
            RatingReportDentalModel ratingReportDentalModel = getDentalRatingReportByPolicyNumber(quoteNumber).getModel();
            ManualClaimDetailModel manualClaimDetailModel = ratingReportDentalModel.getPlanCalculationModelByPlanName(planName).getManualClaimDetail();
            softly.assertThat(isAdminExpensesExpanded()).isFalse();
            softly.assertThat(isRatingDetailsExpanded()).isFalse();

            LOGGER.info(String.format("TEST: %s: Step 37", testCaseId));
            verifyAdminExpenses(ratingReportDentalModel, quoteNumber, false);
            closeAdminExpenses();

            LOGGER.info(String.format("TEST: %s: Step 38-39", testCaseId));
            expandRatingDetails();
            //Check Demograpic tab
            //Summary section
            verifySummarySectionValues(manualClaimDetailModel);
            //Area Factors
            softly.assertThat(manualClaimDetailModel.getAreaFactorsWithRoundedValues(true)).isEqualTo(getAreaFactorsDataAreaTierFromUI());
            //Enrolled By State
            verifyEnrolledByState(manualClaimDetailModel);
            //Check Factors tab
            openFactorsSection();
            softly.assertThat(getFactorsDataFromUI()).isEqualTo(ratingReportDentalModel.getPlanCalculations().get(0).getFactorsDataWithRoundedValues());
            txtVolLoad.setValue(TEST_VOL_LOAD_VALUE);
            lnkApplyFactors.click();
            softly.assertThat(txtVolLoad).hasValue(TEST_VOL_LOAD_VALUE);
            lnkResetFactors.click();
            softly.assertThat(txtVolLoad).hasValue(new BigDecimal(manualClaimDetailModel.getVoluntaryLoad()).setScale(4, RoundingMode.HALF_UP).toString());

            //return updated values to initial state and open Factors tab
            returnUpdatedValuesToInitialBySaveQuote();
            expandRatingDetails();
            openFactorsSection();

            LOGGER.info(String.format("TEST: %s: Step 40", testCaseId));
            verifyNetworkDataOnFactorsTab(manualClaimDetailModel);

            LOGGER.info(String.format("TEST: %s: Steps 41-43", testCaseId));
            expandRatingDetails();
            checkRedistributionCalculatorTabAreaTier(ImmutableMap.of(
                    UW_ADJUSTMENT_FIRST, "0.00",
                    UW_ADJUSTMENT_SECOND, "0.00",
                    UW_ADJUSTMENT_THIRD, "0.00"),
                    ratingReportDentalModel, ImmutableList.of(planName));
            checkApplyAdjustments();
            softly.assertThat(ratingReportDentalModel.getPlanCalculations().get(0).getTierRateModelsWithAdaptedToExpectedUIValues())
                    .isNotEqualTo(getTierRateModelsFromRedistributionCalculatorTab());
            checkResetAdjustment();

            LOGGER.info(String.format("TEST: %s: Step 44", testCaseId));
            //update values on previous tabs and apply all changes
            //Admin Expenses
            expandAdminExpenses();
            txtNumberOfClaimsPerEmployee.applyValue(TEST_NUMBER_OF_CLAIMS_PER_EMPLOYEE);
            txtUwExceptionDollarAmount.applyValue(TEST_UW_EXCEPTION_DOLLAR_AMOUNT);
            txtRiskAdjustmentPercent.applyValue(TEST_RISK_ADJUSTMENT_PERCENT);
            txtUwExceptionPercent.applyValue(TEST_UW_EXCEPTION_PERCENT);
            closeAdminExpenses();
            //Factors tab
            expandRatingDetails();
            openFactorsSection();
            txtVolLoad.setValue(TEST_VOL_LOAD_VALUE);
            lnkApplyFactors.click();
            //Redistribution Calculator
            openRedistributionCalculatorSection();
            checkApplyAdjustments();
            btnApplyRates.click();

            LOGGER.info(String.format("TEST: %s: Step 45", testCaseId));
            //verify updated values
            RatingReportDentalModel updatedRatingReportDentalModel = getDentalRatingReportByPolicyNumber(quoteNumber).getModel();
            ManualClaimDetailModel updatedManualClaimDetailModel = updatedRatingReportDentalModel.getPlanCalculations().get(0).getManualClaimDetail();
            expandAdminExpenses();
            softly.assertThat(updatedRatingReportDentalModel.getAdminCostsWithRoundedValues()).isEqualTo(getFullyInsuredDataFromUI());
            softly.assertThat(txtNumberOfClaimsPerEmployee).hasValue(TEST_NUMBER_OF_CLAIMS_PER_EMPLOYEE);
            softly.assertThat(txtUwExceptionDollarAmount).hasValue(TEST_UW_EXCEPTION_DOLLAR_AMOUNT);
            softly.assertThat(txtRiskAdjustmentPercent).hasValue(TEST_RISK_ADJUSTMENT_PERCENT);
            softly.assertThat(txtUwExceptionPercent).hasValue(TEST_UW_EXCEPTION_PERCENT);
            closeAdminExpenses();
            //Demographic
            expandRatingDetails();
            //Summary
            verifySummarySectionValues(updatedRatingReportDentalModel.getPlanCalculations().get(0).getManualClaimDetail());
            //Area Factors
            softly.assertThat(updatedManualClaimDetailModel.getAreaFactorsWithRoundedValues(true)).isEqualTo(getAreaFactorsDataAreaTierFromUI());
            //Enrolled By State
            verifyEnrolledByState(updatedManualClaimDetailModel);
            //Factors
            openFactorsSection();
            Map<String, String> factorsDataFromRest = updatedRatingReportDentalModel.getPlanCalculations().get(0).getFactorsDataWithRoundedValues();
            softly.assertThat(getFactorsDataFromUI()).isEqualTo(factorsDataFromRest);
            softly.assertThat(factorsDataFromRest.get(FactorsSectionFields.VOL_LOAD)).isEqualTo(TEST_VOL_LOAD_VALUE);
            verifyNetworkDataOnFactorsTab(updatedManualClaimDetailModel);
            //check Redistribution Calculator
            checkUpdatedRedistributionCalculatorSection(planName, CURRENT_ENROLLMENT_TEST_VALUE, updatedRatingReportDentalModel, false, false);
        });
    }

}
