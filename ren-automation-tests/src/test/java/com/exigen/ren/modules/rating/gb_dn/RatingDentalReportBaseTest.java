package com.exigen.ren.modules.rating.gb_dn;

import com.exigen.ipb.eisa.controls.ratingreport.RedistributionCalculatorComboBox;
import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.Tab;
import com.exigen.ren.helpers.logging.RatingLogGrabber;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.CaseProfileDetailsTab;
import com.exigen.ren.main.modules.caseprofile.tabs.FileIntakeManagementTab;
import com.exigen.ren.main.modules.caseprofile.tabs.ProductAndPlanManagementTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.rating.RatingReportBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.rating.model.gb_dn.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.Tab.doubleWaiter;
import static com.exigen.ren.main.enums.RatingReportConstants.*;
import static com.exigen.ren.main.enums.RatingReportConstants.DemographicSection.PPO;
import static com.exigen.ren.main.enums.RatingReportConstants.Networks.EPO;
import static com.exigen.ren.main.enums.RatingReportConstants.Networks.NON_PARTICIPATING;
import static com.exigen.ren.main.enums.RatingReportConstants.NetworksTier.CHILD;
import static com.exigen.ren.main.enums.RatingReportConstants.NetworksTier.EMPLOYEE;
import static com.exigen.ren.main.enums.RatingReportConstants.NetworksTier.SPOUSE;
import static com.exigen.ren.main.enums.RatingReportConstants.PCT_LOWERCASE_FROM_RENEWAL;
import static com.exigen.ren.main.enums.RatingReportConstants.RedistributionCalculatorTierTable.TIER;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryRatingReportTierColumns.*;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData.GROUP_DOMICILE_STATE;
import static com.exigen.ren.main.modules.caseprofile.metadata.ProductAndPlanManagementTabMetaData.PRODUCT;
import static com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType.GB_DN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.RatingReportView.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.RatingReportView.CalculateRateView.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.RatingReportView.CalculateRateView.cbxRedistributeRate;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.RatingReportView.getFullyInsuredDataFromUI;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.RatingReportView.txtUwExceptionPercent;

public class RatingDentalReportBaseTest extends RatingReportBaseTest implements CaseProfileContext, GroupDentalMasterPolicyContext {
    //test values
    static final String TEST_NUMBER_OF_CLAIMS_PER_EMPLOYEE = "20.00";
    static final String TEST_UW_EXCEPTION_DOLLAR_AMOUNT = "10.00";
    static final String TEST_RISK_ADJUSTMENT_PERCENT = "15.02";
    static final String TEST_UW_EXCEPTION_PERCENT = "16.02";
    static final String TEST_INDEMNITY_VALUE = "0.9000";
    static final String TEST_PPO_VALUE = "0.7000";
    static final String TEST_PPO_NETWORK_PENETRATION_VALUE = "0.45";
    static final String TEST_EPO_AREA_FACTOR_VALUE = "0.5000";
    static final String TEST_EPO_NETWORK_PENETRATION_VALUE = "0.21";
    static final String TEST_VOL_LOAD_VALUE = "1.9000";
    private static final String UW_ADJUSTMENT_THIRD_TEST_VALUE = "400.00";
    private static final String TEST_COMMENT_THIRD = "Test Comment Third";
    private static final String UW_REASON_THIRD_TEST_VALUE = FLEX_PLAN;

    static final String UW_ADJUSTMENT_THIRD = "UW Adjustment 3";
    static final String PLAN_ALACARTE = "ALACARTE";
    static final String PLAN_TRIP_ADV = "TRIPADV";
    static final String PLAN_MAJE_POS = "MAJEPOS";
    static final String PLAN_BASE_POS = "BASEPOS";
    static final String PLAN_FLEX_PLUS = "FLEX";
    static final String PLAN_ASO = "ASO";
    static final String PLAN_ASO_ALACARTE = "ASOALC";
    static final ImmutableMap<String, String> PLANS_MAPPING = new ImmutableMap.Builder()
            .put(PLAN_ALACARTE, PolicyConstants.PlanDental.ALACARTE)
            .put(PLAN_ASO_ALACARTE, PolicyConstants.PlanDental.ASOALC)
            .put(PLAN_TRIP_ADV, PolicyConstants.PlanDental.TRIP_ADVANTAGE)
            .put(PLAN_MAJE_POS, PolicyConstants.PlanDental.MAJEPOS)
            .put(PLAN_BASE_POS, PolicyConstants.PlanDental.BASEPOS)
            .put(PLAN_FLEX_PLUS, PolicyConstants.PlanDental.FLEX_PLUS)
            .put(PLAN_ASO, PolicyConstants.PlanDental.ASO)
            .build();
    private static final List<String> UW_ADJUSTMENT_REASON_OPTIONS = ImmutableList.of
            (DUAL_OPTION, PARTICIPATION_ADJ, FLEX_PLAN, WAIVE_WAITING_PERIOD, LIFETIME_DEDUCTIBLE,
                    YEARLY_ORTHO_MAXIMUM, INDIVIDUAL_ADJ, ACTUARIAL_BENEFIT_ADJ, OTHER);

    String initiateAndRateQuote(TestData td) {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_AutoSubGroup")
                .adjust(TestData.makeKeyPath(CaseProfileDetailsTab.class.getSimpleName(), GROUP_DOMICILE_STATE.getLabel()), "NY")
                .adjust(TestData.makeKeyPath(ProductAndPlanManagementTab.class.getSimpleName(), PRODUCT.getLabel()), GB_DN.getName())
                .adjust(TestData.makeKeyPath(FileIntakeManagementTab.class.getSimpleName() + "[0]", FileIntakeManagementTabMetaData.UPLOAD_FILE.getLabel(),
                        FileIntakeManagementTabMetaData.UploadFileDialog.FILE_UPLOAD.getLabel()), "$<file:Census_File_AllPlans_DEN.xlsx>"));
        initiateQuoteAndFillUpToTab(td, PremiumSummaryTab.class, true);
        premiumSummaryTab.rate();
        return QuoteSummaryPage.getQuoteNumber();
    }

    ResponseContainer<RatingReportDentalModel> getDentalRatingReportByPolicyNumber(String policyNumber) {
        String policyId = DBService.get().getValue(String.format("Select id from PolicySummary where policyNumber = '%s'", policyNumber)).get();
        return ratingReportRestService.getDentalRatingReport(policyId);
    }

    void verifyAdminExpenses(RatingReportDentalModel ratingReportDentalModel, String quoteNumber, boolean isAso) {
        expandAdminExpenses();
        assertSoftly(softly -> {
            if (isAso) {
                softly.assertThat(ratingReportDentalModel.getAsoAdminCostWithRoundedValues()).isEqualTo(getAsoAdminCostModelFromUI());
            }
            else {
                softly.assertThat(tblASO).isAbsent();
            }
            //check that values from rating report log correspond to values from app UI and raing log before apply new values
            List<AdminCostsModel> fullyInsuredDataFromUI = getFullyInsuredDataFromUI();
            List<AdminCostsModel> fullyInsuredDataFromRatingLog = getFullyInsuredDataFromRatingLog(new RatingLogGrabber().grabRatingLog(quoteNumber).getResponseLog().getOpenLFieldsMap());
            softly.assertThat(ratingReportDentalModel.getAdminCostsWithRoundedValues()).isEqualTo(fullyInsuredDataFromUI).isEqualTo(fullyInsuredDataFromRatingLog);

            //set updated Fully Insured data and check that values properly updated
            txtNumberOfClaimsPerEmployee.applyValue(TEST_NUMBER_OF_CLAIMS_PER_EMPLOYEE);
            softly.assertThat(txtNumberOfClaimsPerEmployee).hasValue(TEST_NUMBER_OF_CLAIMS_PER_EMPLOYEE);
            txtUwExceptionDollarAmount.applyValue(TEST_UW_EXCEPTION_DOLLAR_AMOUNT);
            softly.assertThat(txtUwExceptionDollarAmount).hasValue(TEST_UW_EXCEPTION_DOLLAR_AMOUNT);
            txtRiskAdjustmentPercent.applyValue(TEST_RISK_ADJUSTMENT_PERCENT);
            softly.assertThat(txtRiskAdjustmentPercent).hasValue(TEST_RISK_ADJUSTMENT_PERCENT);
            txtUwExceptionPercent.applyValue(TEST_UW_EXCEPTION_PERCENT);
            softly.assertThat(txtUwExceptionPercent).hasValue(TEST_UW_EXCEPTION_PERCENT);

            List<AdminCostsModel> updatedFullyInsuredDataFromUI = getFullyInsuredDataFromUI();
            List<AdminCostsModel> updatedFullyInsuredDataFromRatingLog = getFullyInsuredDataFromRatingLog(new RatingLogGrabber().grabRatingLog(quoteNumber).getResponseLog().getOpenLFieldsMap());

            //check that values were updated in rating log and isn't equals to UI values before update
            softly.assertThat(fullyInsuredDataFromUI).isNotEqualTo(updatedFullyInsuredDataFromUI);

            //check that updated values from rating log equals to updated UI values
            softly.assertThat(updatedFullyInsuredDataFromRatingLog).isEqualTo(updatedFullyInsuredDataFromUI);
        });
    }

    void verifyNetworkDataOnFactorsTab(ManualClaimDetailModel manualClaimDetailModel) {
        assertSoftly(softly -> {
            List<String> networksName = new ArrayList<>();
            manualClaimDetailModel.getNetworkDetails().forEach(
                    network -> networksName.add(network.getNetwork()));
            String planName = manualClaimDetailModel.getPlanName();
            if (PLAN_TRIP_ADV.equals(planName) || PLAN_ASO_ALACARTE.equals(planName)) {
                softly.assertThat(networksName).containsExactlyInAnyOrder(PPO, NON_PARTICIPATING, EPO);
            }
            else {
                softly.assertThat(networksName).containsExactlyInAnyOrder(PPO, NON_PARTICIPATING);
            }
            manualClaimDetailModel.getNetworkDetails().forEach(
                    networkDetailsModel -> {
                        String network = networkDetailsModel.getNetwork();
                        LOGGER.info(String.format("TEST: verification '%s' network details on Rating Report Factors tab", network));
                        //check common data
                        softly.assertThat(manualClaimDetailModel.getNetworkDataByNetworkName(network)).isEqualTo(getNetworkCommonDataFromUI(network));
                        //check Employee details
                        verifyNetworkEmloyeeDetailsData(manualClaimDetailModel.getNetworkDetailsModelByNetworkName(network), getNetworkEmloyeeDetailsDataFromUI(network));
                        //check Spouse
                        verifyNetworkSpouseDetailsData(manualClaimDetailModel.getNetworkDetailsModelByNetworkName(network), getNetworkSpouseDetailsDataFromUI(network));
                        //check Child
                        verifyNetworkChildDetailsData(manualClaimDetailModel.getNetworkDetailsModelByNetworkName(network), getNetworkChildDetailsDataFromUI(network));
                    });
        });
    }

    void checkRedistributionCalculatorTabFamilyTier(Map<String, String> valuesForCheck, RatingReportDentalModel ratingReportModel, List<String> plansNames, boolean isMultiOption) {
        checkRedistributionCalculatorCalculateRateSection(valuesForCheck, plansNames, isMultiOption);
        String currentRates = valuesForCheck.get(CURRENT_RATES.getName());
        String renewalRates = valuesForCheck.get(RENEWAL_RATES.getName());

        assertSoftly(softly -> plansNames.forEach(
                planName -> {
                    cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(planName));
                    PlanModel planModel = null;
                    PlanCalculationModel planCalculationModel = ratingReportModel.getPlanCalculationModelByPlanName(planName);
                    softly.assertThat(cbxRedistributeRate.getAllValuesExceptFirstCombineValue())
                            .isEqualTo(ImmutableList.of(MANUAL, PCT_FROM_CURRENT, PCT_FROM_RENEWAL));
                    //Check Composite Rate
                    softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, ADJUSTED_FORMULA_RATES.getName()))
                            .isEqualTo(new DecimalFormat("#,###,##0.00").format(new BigDecimal(planCalculationModel.getAdjFormulaCompositeRate()).setScale(2, RoundingMode.HALF_UP)));
                    softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, CURRENT_RATES.getName()))
                            .isEqualTo(currentRates);
                    softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, RENEWAL_RATES.getName()))
                            .isEqualTo(renewalRates);
                    softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, REDISTRIBUTED_RATES.getName()))
                            .isEqualTo(new DecimalFormat("#,###,##0.00").format(new BigDecimal(planCalculationModel.getProposedCompositeRate()).setScale(2, RoundingMode.HALF_UP)));
                    //check 'Annual Premium' values
                    softly.assertThat(getCellValueTierTable(ANNUAL_PREMIUM, ADJUSTED_FORMULA_RATES.getName()))
                            .isEqualTo(formatValueToCurrency(planCalculationModel.getFinalAnnualPremium()));
                    softly.assertThat(getCellValueTierTable(ANNUAL_PREMIUM, REDISTRIBUTED_RATES.getName()))
                            .isEqualTo(formatValueToCurrency(planCalculationModel.getProposedAnnualPremium()));
                    if ("0".equals(currentRates) && "0".equals(renewalRates)) {
                        softly.assertThat(getCellValueTierTable(ANNUAL_PREMIUM, CURRENT_RATES.getName()))
                                .isEqualTo("0");
                        softly.assertThat(getCellValueTierTable(ANNUAL_PREMIUM, RENEWAL_RATES.getName()))
                                .isEqualTo("0");
                    }
                    else {
                        softly.assertThat(getCellValueTierTable(ANNUAL_PREMIUM, CURRENT_RATES.getName()))
                                .isEqualTo(formatValueToCurrency(planCalculationModel.getCurrentAnnualPremium()));
                        softly.assertThat(getCellValueTierTable(ANNUAL_PREMIUM, RENEWAL_RATES.getName()))
                                .isEqualTo(formatValueToCurrency(planCalculationModel.getRenewalAnnualPremium()));
                        //check '% from Current' values
                        softly.assertThat(getCellValueTierTable(PCT_LOWERCASE_FROM_CURRENT, ADJUSTED_FORMULA_RATES.getName()))
                                .isEqualTo(formatValueToPct(planCalculationModel.getPctFinalFromCurrent()));
                        softly.assertThat(getCellValueTierTable(PCT_LOWERCASE_FROM_CURRENT, RENEWAL_RATES.getName()))
                                .isEqualTo(formatValueToPct(planCalculationModel.getPctRenewalFromCurrent()));
                        softly.assertThat(getCellValueTierTable(PCT_LOWERCASE_FROM_CURRENT, REDISTRIBUTED_RATES.getName()))
                                .isEqualTo(formatValueToPct(planCalculationModel.getPctProposedFromCurrent()));
                        //check '% from Renewal' values
                        softly.assertThat(getCellValueTierTable(PCT_LOWERCASE_FROM_RENEWAL, ADJUSTED_FORMULA_RATES.getName()))
                                .isEqualTo(formatValueToPct(planCalculationModel.getPctFinalFromRenewal()));
                        softly.assertThat(getCellValueTierTable(PCT_LOWERCASE_FROM_RENEWAL, REDISTRIBUTED_RATES.getName()))
                                .isEqualTo(formatValueToPct(planCalculationModel.getPctProposedFromRenewal()));
                        planModel = ratingReportModel.getReports().getPlanModelByPlanName(planName);
                    }
                    //Check Tier Rates
                    final PlanModel finalPlanModel = planModel;
                    planCalculationModel.getTierRates().forEach(
                            tierRateModel -> {
                                String tier = tierRateModel.getTier();
                                softly.assertThat(getCellValueTierTable(tier, ADJUSTED_MANUAL_RATES.getName()))
                                        .isEqualTo(new DecimalFormat("#,###,##0.00").format(new BigDecimal(tierRateModel.getAdjManualRate()).setScale(2, RoundingMode.HALF_UP)));
                                softly.assertThat(getCellValueTierTable(tier, ADJUSTED_FORMULA_RATES.getName()))
                                        .isEqualTo(new DecimalFormat("#,###,##0.00").format(new BigDecimal(tierRateModel.getAdjFormulaRate()).setScale(2, RoundingMode.HALF_UP)));
                                softly.assertThat(getCellValueTierTable(tier, REDISTRIBUTED_RATES.getName()))
                                        .isEqualTo(new DecimalFormat("#,###,##0.00").format(new BigDecimal(tierRateModel.getProposedRate()).setScale(2, RoundingMode.HALF_UP)));
                                if ("0".equals(currentRates) && "0".equals(renewalRates)) {
                                    softly.assertThat(getCellValueTierTable(tier, CURRENT_RATES.getName()))
                                            .isEqualTo(EMPTY);
                                    softly.assertThat(getCellValueTierTable(tier, RENEWAL_RATES.getName()))
                                            .isEqualTo(EMPTY);
                                }
                                else { //check enrollment and rates that were updated
                                    CurrentRatesBreakdownModel currentRatesBreakdownModel = finalPlanModel.getCurrentRatesBreakdownModelByTierName(tier);
                                    softly.assertThat(getCellValueTierTable(tier, CURRENT_ENROLLMENT.getName())).isEqualTo(currentRatesBreakdownModel.getNumberOfLives());
                                    softly.assertThat(getCellValueTierTable(tier, CURRENT_RATES.getName())).isEqualTo(currentRatesBreakdownModel.getCurrentRate()).isEqualTo(currentRates);
                                    softly.assertThat(getCellValueTierTable(tier, RENEWAL_RATES.getName())).isEqualTo(currentRatesBreakdownModel.getRenewalRate()).isEqualTo(renewalRates);
                                }
                            });
                }));
    }

    void checkRedistributionCalculatorTabAreaTier(Map<String, String> valuesForCheck, RatingReportDentalModel ratingReportModel, List<String> plansNames) {
        checkRedistributionCalculatorCalculateRateSection(valuesForCheck, plansNames, false);
        assertSoftly(softly -> plansNames.forEach(
                planName -> {
                    cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(planName));
                    PlanCalculationModel planCalculationModel = ratingReportModel.getPlanCalculationModelByPlanName(planName);
                    softly.assertThat(cbxRedistributeRate).isAbsent();
                    softly.assertThat(btnRedistributionRates).isAbsent();
                    softly.assertThat(btnResetRedistribution).isAbsent();
                    //Check TIER Rate
                    List<TierRateModel> tierRateModelsFromREST = planCalculationModel.getTierRateModelsWithAdaptedToExpectedUIValues();
                    List<TierRateModel> tierRateModelsFromUI = getTierRateModelsFromRedistributionCalculatorTab();
                    softly.assertThat(tierRateModelsFromUI.size()).isEqualTo(tierRateModelsFromREST.size());
                    softly.assertThat(tierRateModelsFromUI).containsAll(tierRateModelsFromREST);
                    //Check Comosite Tier
                    softly.assertThat(tblTierCompositeRateAndAnnualPremium.getRow(TIER.getName(), COMPOSITE_RATE).getCell(ADJUSTED_FORMULA_RATES.getName()).getValue())
                            .isEqualTo(new DecimalFormat("#,###,##0.00").format(new BigDecimal(planCalculationModel.getAdjFormulaCompositeRate()).setScale(2, RoundingMode.HALF_UP)));
                    softly.assertThat(tblTierCompositeRateAndAnnualPremium.getRow(TIER.getName(), COMPOSITE_RATE).getCell(REDISTRIBUTED_RATES.getName()).getValue())
                            .isEqualTo(new DecimalFormat("#,###,##0.00").format(new BigDecimal(planCalculationModel.getProposedCompositeRate()).setScale(2, RoundingMode.HALF_UP)));
                    //Check Annual Premium
                    softly.assertThat(tblTierCompositeRateAndAnnualPremium.getRow(TIER.getName(), ANNUAL_PREMIUM).getCell(ADJUSTED_FORMULA_RATES.getName()).getValue())
                            .isEqualTo(new DecimalFormat("#,###,##0.00").format(new BigDecimal(planCalculationModel.getFinalAnnualPremium()).setScale(2, RoundingMode.HALF_UP)));
                    softly.assertThat(tblTierCompositeRateAndAnnualPremium.getRow(TIER.getName(), ANNUAL_PREMIUM).getCell(REDISTRIBUTED_RATES.getName()).getValue())
                            .isEqualTo(new DecimalFormat("#,###,##0.00").format(new BigDecimal(planCalculationModel.getProposedAnnualPremium()).setScale(2, RoundingMode.HALF_UP)));
                    //Check Current Enrollment, Current Rates, Renewal Rates not displayed when Rate Type = Area Tier
                    getListOfAreaTierTables().forEach(
                            table -> softly.assertThat(table.getHeader().getValue()).doesNotContain(CURRENT_ENROLLMENT.getName(), CURRENT_RATES.getName(), RENEWAL_RATES.getName())
                    );
                    //Check '% from Current' and '% from Renewal' not displayed when Rate Type = Area Tier
                    softly.assertThat(tblTierCompositeRateAndAnnualPremium.getColumn(TIER.getName()).getValue()).doesNotContain(PCT_LOWERCASE_FROM_CURRENT, PCT_LOWERCASE_FROM_RENEWAL);
                }));
    }

    void checkApplyAdjustments() {
        assertSoftly(softly -> {
            softly.assertThat(txtOverrideFirstUWAdjustment).hasValue("0.00");
            softly.assertThat(txtOverrideSecondUWAdjustment).hasValue("0.00");
            softly.assertThat(txtOverrideThirdUWAdjustment).hasValue("0.00");
            txtOverrideFirstUWAdjustment.setValue(UW_ADJUSTMENT_FIRST_TEST_VALUE);
            txtOverrideSecondUWAdjustment.setValue(UW_ADJUSTMENT_SECOND_TEST_VALUE);
            txtOverrideThirdUWAdjustment.setValue(UW_ADJUSTMENT_THIRD_TEST_VALUE);
            cbxReasonFirstUWAdjustment.setValue(UW_REASON_FIRST_TEST_VALUE);
            cbxReasonSecondUWAdjustment.setValue(UW_REASON_SECOND_TEST_VALUE);
            cbxReasonThirdUWAdjustment.setValue(UW_REASON_THIRD_TEST_VALUE);
            btnOverrideFirstAddNote.click();
            txtOverrideFirstComment.setValue(TEST_COMMENT_FIRST);
            btnOverrideSecondAddNote.click();
            txtOverrideSecondComment.setValue(TEST_COMMENT_SECOND);
            btnOverrideThirdAddNote.click();
            txtOverrideThirdComment.setValue(TEST_COMMENT_THIRD);
            btnApplyAdjustments.click();

            softly.assertThat(txtOverrideFirstUWAdjustment).hasValue(UW_ADJUSTMENT_FIRST_TEST_VALUE);
            softly.assertThat(txtOverrideSecondUWAdjustment).hasValue(UW_ADJUSTMENT_SECOND_TEST_VALUE);
            softly.assertThat(txtOverrideThirdUWAdjustment).hasValue(UW_ADJUSTMENT_THIRD_TEST_VALUE);
            softly.assertThat(cbxReasonFirstUWAdjustment).hasValue(UW_REASON_FIRST_TEST_VALUE);
            softly.assertThat(cbxReasonSecondUWAdjustment).hasValue(UW_REASON_SECOND_TEST_VALUE);
            softly.assertThat(cbxReasonThirdUWAdjustment).hasValue(UW_REASON_THIRD_TEST_VALUE);
            softly.assertThat(txtOverrideFirstComment).hasValue(TEST_COMMENT_FIRST);
            softly.assertThat(txtOverrideSecondComment).hasValue(TEST_COMMENT_SECOND);
            softly.assertThat(txtOverrideThirdComment).hasValue(TEST_COMMENT_THIRD);
        });
    }

    void checkUpdatedRedistributionCalculatorSection(String planName, String currentEnrollmentValue, RatingReportDentalModel ratingReportDentalModel, boolean isFamilyTier, boolean isMultiOption) {
        PlanModel planModel = ratingReportDentalModel.getReports().getPlanModelByPlanName(planName);
        UwManualAdjustmentsModel uwManualAdjustmentsFirstModel = planModel.getUwManualAdjustments().get(0);
        UwManualAdjustmentsModel uwManualAdjustmentsSecondModel = planModel.getUwManualAdjustments().get(1);
        UwManualAdjustmentsModel uwManualAdjustmentsThirdModel = planModel.getUwManualAdjustments().get(2);
        assertSoftly(softly -> {
            openRedistributionCalculatorSection();
            softly.assertThat(txtOverrideFirstUWAdjustment).hasValue(formatValueToPct(uwManualAdjustmentsFirstModel.getPercentage()).replace("%", ""));
            softly.assertThat(txtOverrideSecondUWAdjustment).hasValue(formatValueToPct(uwManualAdjustmentsSecondModel.getPercentage()).replace("%", ""));
            softly.assertThat(txtOverrideThirdUWAdjustment).hasValue(formatValueToPct(uwManualAdjustmentsThirdModel.getPercentage()).replace("%", ""));
            softly.assertThat(txtOverrideFirstComment).hasValue(TEST_COMMENT_FIRST).hasValue(uwManualAdjustmentsFirstModel.getComment());
            softly.assertThat(txtOverrideSecondComment).hasValue(TEST_COMMENT_SECOND).hasValue(uwManualAdjustmentsSecondModel.getComment());
            softly.assertThat(txtOverrideThirdComment).hasValue(TEST_COMMENT_THIRD).hasValue(uwManualAdjustmentsThirdModel.getComment());
            softly.assertThat(cbxReasonFirstUWAdjustment).hasValue(UW_REASON_FIRST_TEST_VALUE).hasValue(OVERRIDE_REASON_MAPPING.get(uwManualAdjustmentsFirstModel.getReasonCd()));
            softly.assertThat(cbxReasonSecondUWAdjustment).hasValue(UW_REASON_SECOND_TEST_VALUE).hasValue(OVERRIDE_REASON_MAPPING.get(uwManualAdjustmentsSecondModel.getReasonCd()));
            softly.assertThat(cbxReasonThirdUWAdjustment).hasValue(UW_REASON_THIRD_TEST_VALUE).hasValue(OVERRIDE_REASON_MAPPING.get(uwManualAdjustmentsThirdModel.getReasonCd()));
            if (isFamilyTier) { //Family tier specific verification
                checkRedistributionCalculatorTabFamilyTier(ImmutableMap.of(
                        UW_ADJUSTMENT_FIRST, UW_ADJUSTMENT_FIRST_TEST_VALUE,
                        UW_ADJUSTMENT_SECOND, UW_ADJUSTMENT_SECOND_TEST_VALUE,
                        UW_ADJUSTMENT_THIRD, UW_ADJUSTMENT_THIRD_TEST_VALUE,
                        CURRENT_RATES.getName(), CURRENT_RATES_TEST_VALUE,
                        RENEWAL_RATES.getName(), RENEWAL_RATES_TEST_VALUE), ratingReportDentalModel, ImmutableList.of(planName), isMultiOption);
                planModel.getCurrentRatesBreakdown().forEach(
                        tier -> {
                            String tierName = tier.getTier();
                            softly.assertThat(getCellValueTierTable(tierName, CURRENT_ENROLLMENT.getName())).isEqualTo(currentEnrollmentValue);
                        }
                );
            }
            else { //Area tier specific verification
                checkRedistributionCalculatorTabAreaTier(ImmutableMap.of(
                        UW_ADJUSTMENT_FIRST, UW_ADJUSTMENT_FIRST_TEST_VALUE,
                        UW_ADJUSTMENT_SECOND, UW_ADJUSTMENT_SECOND_TEST_VALUE,
                        UW_ADJUSTMENT_THIRD, UW_ADJUSTMENT_THIRD_TEST_VALUE),
                        ratingReportDentalModel, ImmutableList.of(planName));
            }
        });
    }

    void checkSelectPlanMultiOption(RatingReportDentalModel ratingReportModel, String planName) {
        assertSoftly(softly -> {
            StaticElement parentElementForMultiPlan = new StaticElement(ByT.xpath(PARENT_LOCATOR_MULTIOPTION).format(PLANS_MAPPING.get(planName)));
            PlanCalculationModel planCalculationModel = ratingReportModel.getPlanCalculationModelByPlanName(planName);

            softly.assertThat(new TextBox(parentElementForMultiPlan, SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "1", OVERRIDE_TXT_PATH))).hasValue("0.00");
            softly.assertThat(new TextBox(parentElementForMultiPlan, SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "2", OVERRIDE_TXT_PATH))).hasValue("0.00");
            softly.assertThat(new TextBox(parentElementForMultiPlan, SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "3", OVERRIDE_TXT_PATH))).hasValue("0.00");
            softly.assertThat(new RedistributionCalculatorComboBox(parentElementForMultiPlan, REDISTRIBUTION_CALCULATOR_LOCATOR.format("", "UW Adjustments", "1", "Reason")).getAllValuesExceptFirstCombineValue())
                    .isEqualTo(UW_ADJUSTMENT_REASON_OPTIONS);
            softly.assertThat(new RedistributionCalculatorComboBox(parentElementForMultiPlan, REDISTRIBUTION_CALCULATOR_LOCATOR.format("", "UW Adjustments", "2", "Reason")).getAllValuesExceptFirstCombineValue())
                    .isEqualTo(UW_ADJUSTMENT_REASON_OPTIONS);
            softly.assertThat(new RedistributionCalculatorComboBox(parentElementForMultiPlan, REDISTRIBUTION_CALCULATOR_LOCATOR.format("", "UW Adjustments", "3", "Reason")).getAllValuesExceptFirstCombineValue())
                    .isEqualTo(UW_ADJUSTMENT_REASON_OPTIONS);
            softly.assertThat(new RedistributionCalculatorComboBox(new StaticElement(ByT.xpath("//h3[text()='%s']//following-sibling::div[%s]").format(PLANS_MAPPING.get(planName), "2")), REDISTRIBUTION_RATE_LOCATOR.format("REDISTRIBUTE RATE")).getAllValuesExceptFirstCombineValue())
                    .isEqualTo(ImmutableList.of(MANUAL, PCT_FROM_CURRENT, PCT_FROM_RENEWAL));
            //Check Composite Rate
            softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, ADJUSTED_FORMULA_RATES.getName(), PLANS_MAPPING.get(planName)))
                    .isEqualTo(new DecimalFormat("#,###,##0.00").format(new BigDecimal(planCalculationModel.getAdjFormulaCompositeRate()).setScale(2, RoundingMode.HALF_UP)));
            softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, CURRENT_RATES.getName(), PLANS_MAPPING.get(planName)))
                    .isEqualTo("0");
            softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, RENEWAL_RATES.getName(), PLANS_MAPPING.get(planName)))
                    .isEqualTo("0");
            softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, REDISTRIBUTED_RATES.getName(), PLANS_MAPPING.get(planName)))
                    .isEqualTo(new DecimalFormat("#,###,##0.00").format(new BigDecimal(planCalculationModel.getProposedCompositeRate()).setScale(2, RoundingMode.HALF_UP)));
            //check 'Annual Premium' values
            softly.assertThat(getCellValueTierTable(ANNUAL_PREMIUM, ADJUSTED_FORMULA_RATES.getName(), PLANS_MAPPING.get(planName)))
                    .isEqualTo(formatValueToCurrency(planCalculationModel.getFinalAnnualPremium()));
            softly.assertThat(getCellValueTierTable(ANNUAL_PREMIUM, CURRENT_RATES.getName(), PLANS_MAPPING.get(planName)))
                    .isEqualTo("0");
            softly.assertThat(getCellValueTierTable(ANNUAL_PREMIUM, RENEWAL_RATES.getName(), PLANS_MAPPING.get(planName)))
                    .isEqualTo("0");
            softly.assertThat(getCellValueTierTable(ANNUAL_PREMIUM, REDISTRIBUTED_RATES.getName(), PLANS_MAPPING.get(planName)))
                    .isEqualTo(formatValueToCurrency(planCalculationModel.getProposedAnnualPremium()));
            //Check Tier Rates
            planCalculationModel.getTierRates().forEach(
                    tierRateModel -> {
                        softly.assertThat(getCellValueTierTable(tierRateModel.getTier(), ADJUSTED_MANUAL_RATES.getName(), PLANS_MAPPING.get(planName)))
                                .isEqualTo(new BigDecimal(tierRateModel.getAdjManualRate()).setScale(2, RoundingMode.HALF_UP).toString());
                        softly.assertThat(getCellValueTierTable(tierRateModel.getTier(), ADJUSTED_FORMULA_RATES.getName(), PLANS_MAPPING.get(planName)))
                                .isEqualTo(new BigDecimal(tierRateModel.getAdjFormulaRate()).setScale(2, RoundingMode.HALF_UP).toString());
                        softly.assertThat(getCellValueTierTable(tierRateModel.getTier(), REDISTRIBUTED_RATES.getName(), PLANS_MAPPING.get(planName)))
                                .isEqualTo(new BigDecimal(tierRateModel.getProposedRate()).setScale(2, RoundingMode.HALF_UP).toString());
                        softly.assertThat(getCellValueTierTable(tierRateModel.getTier(), CURRENT_RATES.getName(), PLANS_MAPPING.get(planName)))
                                .isEqualTo(EMPTY);
                        softly.assertThat(getCellValueTierTable(tierRateModel.getTier(), RENEWAL_RATES.getName(), PLANS_MAPPING.get(planName)))
                                .isEqualTo(EMPTY);
                    });
        });
    }

    void checkRedistributeRateButtonsRule(String ratesName, List<String> tiers, String ratesValue) {
        assertSoftly(softly -> {
            softly.assertThat(btnApplyAdjustments.getAttribute("class")).contains("disable");
            softly.assertThat(btnRedistributionRates.getAttribute("class")).contains("disable");
            softly.assertThat(btnApplyRates.getAttribute("class")).contains("disable");
            tiers.forEach(
                    tierName -> setCellValueTierTable(tierName, ratesName, ratesValue)
            );
            softly.assertThat(btnApplyAdjustments.getAttribute("class")).doesNotContain("disable");
            softly.assertThat(btnRedistributionRates.getAttribute("class")).doesNotContain("disable");
            softly.assertThat(btnApplyRates.getAttribute("class")).doesNotContain("disable");
            btnRedistributionRates.click();
            softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, ratesName)).isEqualTo(ratesValue);
            btnResetRedistribution.click();
            softly.assertThat(cbxRedistributeRate).hasValue(MANUAL);
        });
    }

    void checkResetAdjustment() {
        assertSoftly(softly -> {
            btnResetAdjustments.click();
            softly.assertThat(txtOverrideFirstUWAdjustment).hasValue("0.00");
            softly.assertThat(txtOverrideSecondUWAdjustment).hasValue("0.00");
            softly.assertThat(txtOverrideThirdUWAdjustment).hasValue("0.00");
            softly.assertThat(cbxReasonFirstUWAdjustment).hasValue(EMPTY);
            softly.assertThat(cbxReasonSecondUWAdjustment).hasValue(EMPTY);
            softly.assertThat(cbxReasonThirdUWAdjustment).hasValue(EMPTY);
            softly.assertThat(txtOverrideFirstComment).isAbsent();
            softly.assertThat(txtOverrideSecondComment).isAbsent();
            softly.assertThat(txtOverrideThirdComment).isAbsent();
        });
    }

    void verifySummarySectionValues(ManualClaimDetailModel manualClaimDetailModel) {
        assertSoftly(softly -> {
            //check 'BY AGE' part
            String totalNumberOfLivesByAgeFromRest = manualClaimDetailModel.getAgeBreakDownTotalNumberOfLives();
            //check value in Summary Pie diagram
            softly.assertThat(getTotalByAgeFromSummarySection()).isEqualTo(totalNumberOfLivesByAgeFromRest);
            //check list of values
            Map<String, String> ageBreakdownMapFromRest = new HashMap<>();
            manualClaimDetailModel.getAgeBreakdown().forEach(
                    model -> {
                        String age = model.getAgeBand();
                        if ("0 - 24".equals(age)) {
                            ageBreakdownMapFromRest.put("<25", getNumberWithoutDecimal(model.getNumberOfLives()));
                        }
                        else {
                            ageBreakdownMapFromRest.put(age.replaceAll("\\s+", ""), getNumberWithoutDecimal(model.getNumberOfLives()));
                        }
                    });
            Map<String, String> byAgeDataFromSummarySection = getByAgeDataFromSummarySection();
            softly.assertThat(byAgeDataFromSummarySection.get("Total")).isEqualTo(totalNumberOfLivesByAgeFromRest);
            byAgeDataFromSummarySection.remove("Total");
            softly.assertThat(byAgeDataFromSummarySection).isEqualTo(ageBreakdownMapFromRest);

            //check 'BY ENROLLMENT' part
            String totalNumberOfLivesByEnrollmentFromRest = manualClaimDetailModel.getEnrollmentBreakdownReportTotalNumberOfLives();
            //check value in Summary Pie diagram
            softly.assertThat(getTotalByEnrollmentFromSummarySection()).isEqualTo(totalNumberOfLivesByEnrollmentFromRest);
            //check list of values
            Map<String, String> enrollmentMapFromRest = new HashMap<>();
            manualClaimDetailModel.getEnrollmentBreakdownReport().forEach(
                    model -> enrollmentMapFromRest.put(model.getTier(), model.getNumberOfLives()));
            Map<String, String> byEnrollmentDataFromSummarySection = getByEnrollmentDataFromSummarySection();
            softly.assertThat(byEnrollmentDataFromSummarySection.get("Total")).isEqualTo(totalNumberOfLivesByEnrollmentFromRest);
            byEnrollmentDataFromSummarySection.remove("Total");
            softly.assertThat(byEnrollmentDataFromSummarySection).isEqualTo(enrollmentMapFromRest);
        });
    }

    void verifyAreaFactorsFamilyTier(ManualClaimDetailModel manualClaimDetailModel, String quoteNumber, String planName) {
        assertSoftly(softly -> {
            String expectedInitialIndemnityValue = new BigDecimal(manualClaimDetailModel.getIndemnityAreaFactor()).setScale(4, RoundingMode.HALF_UP).toString();
            String expectedInitialPpoValue = new BigDecimal(manualClaimDetailModel.getPpoAreaFactor()).setScale(4, RoundingMode.HALF_UP).toString();
            String expectedInitialPPONetworkPenetrationValue = formatValueToPct(manualClaimDetailModel.getPpoPenetration());
            String expectedInitialEPONetworkPenetrationValue = null;
            String expectedInitialEPOAreaFactorValue = null;
            //'EPO Area Factor' and 'EPO Network Penetration' is available only benefitConfigurations[].network = EPO -> Triple Advantage, Aso Alacarte test plans
            if (PLAN_TRIP_ADV.equals(planName) || PLAN_ASO_ALACARTE.equals(planName)) {
                expectedInitialEPONetworkPenetrationValue = formatValueToPct(manualClaimDetailModel.getEpoPenetration());
                expectedInitialEPOAreaFactorValue = new BigDecimal(manualClaimDetailModel.getEpoAreaFactor()).setScale(4, RoundingMode.HALF_UP).toString();
            }
            softly.assertThat(lblIndemnity).hasValue(expectedInitialIndemnityValue);
            softly.assertThat(lblPPO).hasValue(expectedInitialPpoValue);
            softly.assertThat(lblPPONetworkPenetration).hasValue(expectedInitialPPONetworkPenetrationValue);
            if (PLAN_TRIP_ADV.equals(planName) || PLAN_ASO_ALACARTE.equals(planName)) {
                softly.assertThat(lblEPOAreaFactor).hasValue(expectedInitialEPOAreaFactorValue);
                softly.assertThat(lblEPONetworkPenetration).hasValue(expectedInitialEPONetworkPenetrationValue);
            }
            //check 'Cancel' link
            lnkEditAreaFactors.click();
            txtIndemnity.setValue(TEST_INDEMNITY_VALUE);
            txtPPO.setValue(TEST_PPO_VALUE);
            txtPPONetworkPenetration.setValue(formatValueToPct(TEST_PPO_NETWORK_PENETRATION_VALUE).replace("%", ""));
            if (PLAN_TRIP_ADV.equals(planName) || PLAN_ASO_ALACARTE.equals(planName)) {
                txtEPOAreaFactor.setValue(TEST_EPO_AREA_FACTOR_VALUE);
                txtEPONetworkPenetration.setValue(formatValueToPct(TEST_EPO_NETWORK_PENETRATION_VALUE).replace("%", ""));
            }
            //check that updated values not saved after Cancel
            lnkCancelAreaFactors.click();
            softly.assertThat(lblIndemnity).hasValue(expectedInitialIndemnityValue);
            softly.assertThat(lblPPO).hasValue(expectedInitialPpoValue);
            softly.assertThat(lblPPONetworkPenetration).hasValue(expectedInitialPPONetworkPenetrationValue);
            if (PLAN_TRIP_ADV.equals(planName) || PLAN_ASO_ALACARTE.equals(planName)) {
                softly.assertThat(lblEPOAreaFactor).hasValue(expectedInitialEPOAreaFactorValue);
                softly.assertThat(lblEPONetworkPenetration).hasValue(expectedInitialEPONetworkPenetrationValue);
            }
            //check 'Edit' link
            lnkEditAreaFactors.click();
            txtIndemnity.setValue(TEST_INDEMNITY_VALUE);
            txtPPO.setValue(TEST_PPO_VALUE);
            txtPPONetworkPenetration.setValue(formatValueToPct(TEST_PPO_NETWORK_PENETRATION_VALUE).replace("%", ""));
            if (PLAN_TRIP_ADV.equals(planName) || PLAN_ASO_ALACARTE.equals(planName)) {
                txtEPOAreaFactor.setValue(TEST_EPO_AREA_FACTOR_VALUE);
                txtEPONetworkPenetration.setValue(formatValueToPct(TEST_EPO_NETWORK_PENETRATION_VALUE).replace("%", ""));
            }
            lnkSaveAreaFactors.click();

            //check that updated values exist in rating request
            Map<String, String> requestFromRatingLog = new RatingLogGrabber().grabRatingLog(quoteNumber).getRequestLog().getOpenLFieldsMap();
            //get index for plan from rating log
            String planIndex = getPlansIndexesFromRatingLog(requestFromRatingLog).get(planName);
            softly.assertThat(lblIndemnity).hasValue(TEST_INDEMNITY_VALUE).hasValue(requestFromRatingLog.get(String.format("plans[%s].indemnityOverride", planIndex)));
            softly.assertThat(lblPPO).hasValue(TEST_PPO_VALUE).hasValue(requestFromRatingLog.get(String.format("plans[%s].areaFactorOverridePPO", planIndex)));
            softly.assertThat(lblPPONetworkPenetration).hasValue(formatValueToPct(TEST_PPO_NETWORK_PENETRATION_VALUE))
                    .hasValue(formatValueToPct(requestFromRatingLog.get(String.format("plans[%s].networkPenetrationOverridePPO", planIndex))));
            if (PLAN_TRIP_ADV.equals(planName) || PLAN_ASO_ALACARTE.equals(planName)) {
                softly.assertThat(lblEPOAreaFactor).hasValue(TEST_EPO_AREA_FACTOR_VALUE).hasValue(requestFromRatingLog.get(String.format("plans[%s].areaFactorOverrideEPO", planIndex)));
                softly.assertThat(lblEPONetworkPenetration).hasValue(formatValueToPct(TEST_EPO_NETWORK_PENETRATION_VALUE))
                        .hasValue(formatValueToPct(requestFromRatingLog.get(String.format("plans[%s].networkPenetrationOverrideEPO", planIndex))));
            }
            //return initial values
            lnkEditAreaFactors.click();
            txtIndemnity.setValue(expectedInitialIndemnityValue);
            txtPPO.setValue(expectedInitialPpoValue);
            txtPPONetworkPenetration.setValue(expectedInitialPPONetworkPenetrationValue.replace("%", ""));
            if (PLAN_TRIP_ADV.equals(planName) || PLAN_ASO_ALACARTE.equals(planName)) {
                txtEPOAreaFactor.setValue(TEST_EPO_AREA_FACTOR_VALUE);
                txtEPONetworkPenetration.setValue(formatValueToPct(TEST_EPO_NETWORK_PENETRATION_VALUE).replace("%", ""));
            }
            lnkSaveAreaFactors.click();
        });
    }

    void verifyEnrolledByState(ManualClaimDetailModel manualClaimDetailModel) {
        assertSoftly(softly -> {
            List<String> enrollmentStates = new ArrayList<>();
            manualClaimDetailModel.getEnrollmentByState().forEach(
                    enrollmentByState -> {
                        if (!"0".equals(enrollmentByState.getNumberOfLives())) {
                            enrollmentStates.add(enrollmentByState.getState());
                        }
                    }
            );
            if (enrollmentStates.isEmpty()) {
                throw new IstfException("Could not find any states with participants");
            }
            tblEnrolledByState.getColumn(EnrolledByStateTable.STATE.getName()).getValue().forEach(
                    state -> {
                        EnrollmentByStateModel enrollmentByStateModel = manualClaimDetailModel.getEnrollmentByState()
                                .stream().filter(enrollment -> state.equals(enrollment.getState())).findFirst().get();
                        softly.assertThat(tblEnrolledByState.getRow(EnrolledByStateTable.STATE.getName(), state).getCell(EnrolledByStateTable.ELIGIBLE.getName())).hasValue(getNumberWithoutDecimal(enrollmentByStateModel.getNumberOfLives()));
                        softly.assertThat(tblEnrolledByState.getRow(EnrolledByStateTable.STATE.getName(), state).getCell(EnrolledByStateTable.PERCENT.getName())).hasValue(formatValueToPct(enrollmentByStateModel.getEnrolledPct()));
                    }
            );
            //check Expand All
            enrollmentStates.forEach(
                    state -> softly.assertThat(new Table(TBL_NESTED_ENROLLED_BY_STATE_LOCATOR.format(state))).isAbsent());
            lnkExpandAllEnrolledByState.click();
            enrollmentStates.forEach(
                    state -> softly.assertThat(new Table(TBL_NESTED_ENROLLED_BY_STATE_LOCATOR.format(state))).isPresent());
            lnkExpandAllEnrolledByState.click();
            enrollmentStates.forEach(
                    state -> softly.assertThat(new Table(TBL_NESTED_ENROLLED_BY_STATE_LOCATOR.format(state))).isAbsent());
        });
    }

    void overrideAdjustments(RatingReportDentalModel ratingReportDentalModel, String planName) {
        PlanCalculationModel planCalculationModel = ratingReportDentalModel.getPlanCalculationModelByPlanName(planName);
        String parentLocatorPath = String.format(PARENT_LOCATOR_MULTIOPTION, PLANS_MAPPING.get(planName));

        new TextBox(SECTION_LOCATOR.format(parentLocatorPath, UW_ADJUSTMENTS, "1", OVERRIDE_TXT_PATH)).setValue(UW_ADJUSTMENT_FIRST_TEST_VALUE);
        new TextBox(SECTION_LOCATOR.format(parentLocatorPath, UW_ADJUSTMENTS, "2", OVERRIDE_TXT_PATH)).setValue(UW_ADJUSTMENT_SECOND_TEST_VALUE);
        new TextBox(SECTION_LOCATOR.format(parentLocatorPath, UW_ADJUSTMENTS, "3 ", OVERRIDE_TXT_PATH)).setValue(UW_ADJUSTMENT_THIRD_TEST_VALUE);
        new RedistributionCalculatorComboBox(REDISTRIBUTION_CALCULATOR_LOCATOR.format(parentLocatorPath, UW_ADJUSTMENTS, "1", "Reason")).setValue(UW_REASON_FIRST_TEST_VALUE, doubleWaiter);
        new RedistributionCalculatorComboBox(REDISTRIBUTION_CALCULATOR_LOCATOR.format(parentLocatorPath, UW_ADJUSTMENTS, "2", "Reason")).setValue(UW_REASON_SECOND_TEST_VALUE, doubleWaiter);
        new RedistributionCalculatorComboBox(REDISTRIBUTION_CALCULATOR_LOCATOR.format(parentLocatorPath, UW_ADJUSTMENTS, "3", "Reason")).setValue(UW_REASON_THIRD_TEST_VALUE, doubleWaiter);
        new Button(SECTION_LOCATOR.format(parentLocatorPath, UW_ADJUSTMENTS, "1", ADD_NOTE_PATH)).click();
        new TextBox(SECTION_LOCATOR.format(parentLocatorPath, UW_ADJUSTMENTS, "1", "//textarea")).setValue(TEST_COMMENT_FIRST);
        new Button(SECTION_LOCATOR.format(parentLocatorPath, UW_ADJUSTMENTS, "2", ADD_NOTE_PATH)).click();
        new TextBox(SECTION_LOCATOR.format(parentLocatorPath, UW_ADJUSTMENTS, "2", "//textarea")).setValue(TEST_COMMENT_SECOND);
        new Button(SECTION_LOCATOR.format(parentLocatorPath, UW_ADJUSTMENTS, "3", ADD_NOTE_PATH)).click();
        new TextBox(SECTION_LOCATOR.format(parentLocatorPath, UW_ADJUSTMENTS, "3", "//textarea")).setValue(TEST_COMMENT_THIRD);
        new Button(SECTION_LOCATOR.format(parentLocatorPath, UW_ADJUSTMENTS, "4", "//a[text()='APPLY ADJUSTMENTS']")).click();

        planCalculationModel.getTierRates().forEach(
                tierRateModel -> {
                    setCellValueTierTable(tierRateModel.getTier(), CURRENT_RATES.getName(), CURRENT_RATES_TEST_VALUE, PLANS_MAPPING.get(planName));
                    setCellValueTierTable(tierRateModel.getTier(), RENEWAL_RATES.getName(), RENEWAL_RATES_TEST_VALUE, PLANS_MAPPING.get(planName));
                    setCellValueTierTable(tierRateModel.getTier(), CURRENT_ENROLLMENT.getName(), CURRENT_ENROLLMENT_TEST_VALUE, PLANS_MAPPING.get(planName));
                });
        btnRedistributeMultiOption.click(); //according REN-47585
        assertSoftly(softly -> planCalculationModel.getTierRates().forEach(
                tierRateModel -> {
                    softly.assertThat(getCellValueTierTable(tierRateModel.getTier(), ADJUSTED_MANUAL_RATES.getName(), PLANS_MAPPING.get(planName)))
                            .isNotEqualTo(new BigDecimal(tierRateModel.getAdjManualRate()).setScale(2, RoundingMode.HALF_UP).toString());
                    softly.assertThat(getCellValueTierTable(tierRateModel.getTier(), ADJUSTED_FORMULA_RATES.getName(), PLANS_MAPPING.get(planName)))
                            .isNotEqualTo(new BigDecimal(tierRateModel.getAdjFormulaRate()).setScale(2, RoundingMode.HALF_UP).toString());
                    softly.assertThat(getCellValueTierTable(tierRateModel.getTier(), REDISTRIBUTED_RATES.getName(), PLANS_MAPPING.get(planName)))
                            .isNotEqualTo(new BigDecimal(tierRateModel.getProposedRate()).setScale(2, RoundingMode.HALF_UP).toString());
                    softly.assertThat(getCellValueTierTable(tierRateModel.getTier(), CURRENT_RATES.getName(), PLANS_MAPPING.get(planName)))
                            .isEqualTo(CURRENT_RATES_TEST_VALUE);
                    softly.assertThat(getCellValueTierTable(tierRateModel.getTier(), RENEWAL_RATES.getName(), PLANS_MAPPING.get(planName)))
                            .isEqualTo(RENEWAL_RATES_TEST_VALUE);
                })
        );
    }

    void returnUpdatedValuesToInitialBySaveQuote() {
        Tab.buttonTopSave.click(doubleWaiter);
    }

    private void checkRedistributionCalculatorCalculateRateSection(Map<String, String> valuesForCheck, List<String> plansNames, boolean isMultiOption) {
        openRedistributionCalculatorSection();
        String overrideFirstUWAdjustment = valuesForCheck.get(UW_ADJUSTMENT_FIRST);
        String overrideSecondUWAdjustment = valuesForCheck.get(UW_ADJUSTMENT_SECOND);
        String overrideThirdUWAdjustment = valuesForCheck.get(UW_ADJUSTMENT_THIRD);

        assertSoftly(softly -> {
            if (isMultiOption) {
                softly.assertThat(chbxMultiOption).isPresent();
            }
            else {
                softly.assertThat(chbxMultiOption).isAbsent();
            }
            plansNames.forEach(
                    planName -> {
                        cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(planName));
                        softly.assertThat(txtOverrideFirstUWAdjustment).hasValue(overrideFirstUWAdjustment);
                        softly.assertThat(txtOverrideSecondUWAdjustment).hasValue(overrideSecondUWAdjustment);
                        softly.assertThat(txtOverrideThirdUWAdjustment).hasValue(overrideThirdUWAdjustment);
                        softly.assertThat(cbxReasonFirstUWAdjustment.getAllValuesExceptFirstCombineValue())
                                .isEqualTo(UW_ADJUSTMENT_REASON_OPTIONS);
                        softly.assertThat(cbxReasonSecondUWAdjustment.getAllValuesExceptFirstCombineValue())
                                .isEqualTo(UW_ADJUSTMENT_REASON_OPTIONS);
                        softly.assertThat(cbxReasonThirdUWAdjustment.getAllValuesExceptFirstCombineValue())
                                .isEqualTo(UW_ADJUSTMENT_REASON_OPTIONS);
                    });
        });
    }

    private List<AdminCostsModel> getFullyInsuredDataFromRatingLog(Map<String, String> responseFromLog) {
        List<AdminCostsModel> adminCostsModelsFromRatingLog = new ArrayList<>();
        final String regExp = "adminCosts\\[\\d{1,3}]\\.adminCategory";
        LOGGER.info(String.format("Search indexes for rating attribute by reg exp: %s", regExp));
        responseFromLog.forEach((key, value) -> {
            if (key.matches(regExp)) {
                String index = key.replaceAll("[^0-9]+", "");
                AdminCostsModel model = new AdminCostsModel();
                model.setAdminCategory(responseFromLog.get(String.format("adminCosts[%s].adminCategory", index)));
                model.setAnnualAmt(new Currency(responseFromLog.get(String.format("adminCosts[%s].annualAmt", index)), RoundingMode.HALF_UP).toString());
                model.setPremiumPct(formatValueToPct(responseFromLog.get(String.format("adminCosts[%s].premiumPct", index))));
                model.setPerClaimAmt(new Currency(responseFromLog.get(String.format("adminCosts[%s].perClaimAmt", index)), RoundingMode.HALF_UP).toString());
                model.setPerEmployeeAmt(new Currency(responseFromLog.get(String.format("adminCosts[%s].perEmployeeAmt", index)), RoundingMode.HALF_UP).toString());
                adminCostsModelsFromRatingLog.add(model);
            }
        });
        if (adminCostsModelsFromRatingLog.isEmpty()) {
            throw new IstfException("No 'adminCosts' records in rating log found");
        }
        return adminCostsModelsFromRatingLog;
    }

    private Map<String, String> getPlansIndexesFromRatingLog(Map<String, String> responseFromLog) {
        Map<String, String> result = new HashMap<>();
        final String regExp = "plans\\[\\d{1,3}]\\.planName";
        LOGGER.info(String.format("Search indexes for rating attribute by reg exp: %s", regExp));
        responseFromLog.forEach((key, value) -> {
            if (key.matches(regExp)) {
                String index = key.replaceAll("[^0-9]+", "");
                result.put(value, index);
            }
        });
        return result;
    }

    private void verifyNetworkEmloyeeDetailsData(NetworkDetailsModel networkDetailsModel, List<EmployeeDetailsModel> employeeDetailsFromUI) {
        assertSoftly(softly -> {
            softly.assertThat(networkDetailsModel.getEmployeeDetails().size()).isEqualTo(employeeDetailsFromUI.size());
            employeeDetailsFromUI.forEach(modelFromUI -> {
                //get appropriate 'EmployeeDetailsModel' from rest
                EmployeeDetailsModel modelFromRest = networkDetailsModel.getEmployeeDetailsModelByBenefitCategory(modelFromUI.getBenefitCategory());
                //checks
                softly.assertThat(modelFromUI.getBase()).isEqualTo(new Currency(modelFromRest.getBase(), RoundingMode.HALF_UP).toString());
                softly.assertThat(modelFromUI.getDeductibleCredit()).isEqualTo(new Currency(modelFromRest.getDeductibleCredit(), RoundingMode.HALF_UP).toString());
                softly.assertThat(modelFromUI.getDeductibleUtil()).isEqualTo(new BigDecimal(modelFromRest.getDeductibleUtil()).setScale(4, RoundingMode.HALF_UP).toString());
                softly.assertThat(modelFromUI.getClaimsRate()).isEqualTo(new Currency(modelFromRest.getClaimsRate(), RoundingMode.HALF_UP).toString());
                softly.assertThat(modelFromUI.getRateCalc()).isEqualTo(new Currency(modelFromRest.getRateCalc(), RoundingMode.HALF_UP).toString());
            });
            //Check Total -> ONE YEAR CLAIMS RATE
            BigDecimal sumOfClaimsRate = new BigDecimal("0");
            for (EmployeeDetailsModel model : networkDetailsModel.getEmployeeDetails()) {
                sumOfClaimsRate = sumOfClaimsRate.add(new BigDecimal(model.getClaimsRate()));
            }
            softly.assertThat(getNetworkDetailsOneYearClaimsRateTotalValue(networkDetailsModel.getNetwork(), EMPLOYEE).replace("$", ""))
                    .as("Network Details 'Total One Claims rate' UI value  should be calculated as sum of not rounded benefits values from REST")
                    .isEqualTo(sumOfClaimsRate.setScale(2, RoundingMode.HALF_UP).toString());
            //Check Total -> ONE YEAR WEIGHTED
            softly.assertThat(getNetworkDetailsOneYearWeightedTotalValue(networkDetailsModel.getNetwork(), EMPLOYEE))
                    .as("Network Details 'Total One Year Weighted' from REST isn't equal 'Total One Year Weighted' from UI")
                    .isEqualTo(new Currency(networkDetailsModel.getRateCalcEmp(), RoundingMode.HALF_UP).toString());
        });
    }

    private void verifyNetworkSpouseDetailsData(NetworkDetailsModel networkDetailsModel, List<SpouseDetailsModel> spouseDetailsFromUI) {
        assertSoftly(softly -> {
            softly.assertThat(networkDetailsModel.getSpouseDetails().size()).isEqualTo(spouseDetailsFromUI.size());
            spouseDetailsFromUI.forEach(modelFromUI -> {
                //get appropriate 'SpouseDetailsModel' from rest
                SpouseDetailsModel modelFromRest = networkDetailsModel.getSpouseDetailsModelByBenefitCategory(modelFromUI.getBenefitCategory());
                //checks
                softly.assertThat(modelFromUI.getBase()).isEqualTo(new Currency(modelFromRest.getBase(), RoundingMode.HALF_UP).toString());
                softly.assertThat(modelFromUI.getDeductibleCredit()).isEqualTo(new Currency(modelFromRest.getDeductibleCredit(), RoundingMode.HALF_UP).toString());
                softly.assertThat(modelFromUI.getDeductibleUtil()).isEqualTo(new BigDecimal(modelFromRest.getDeductibleUtil()).setScale(4, RoundingMode.HALF_UP).toString());
                softly.assertThat(modelFromUI.getClaimsRate()).isEqualTo(new Currency(modelFromRest.getClaimsRate(), RoundingMode.HALF_UP).toString());
                softly.assertThat(modelFromUI.getRateCalc()).isEqualTo(new Currency(modelFromRest.getRateCalc(), RoundingMode.HALF_UP).toString());
            });
            //Check Total -> ONE YEAR CLAIMS RATE
            BigDecimal sumOfClaimsRate = new BigDecimal("0");
            for (SpouseDetailsModel model : networkDetailsModel.getSpouseDetails()) {
                sumOfClaimsRate = sumOfClaimsRate.add(new BigDecimal(model.getClaimsRate()));
            }
            softly.assertThat(getNetworkDetailsOneYearClaimsRateTotalValue(networkDetailsModel.getNetwork(), SPOUSE).replace("$", ""))
                    .as("Network Details 'Total One Claims rate' UI value  should be calculated as sum of not rounded benefits values from REST")
                    .isEqualTo(sumOfClaimsRate.setScale(2, RoundingMode.HALF_UP).toString());
            //Check Total -> ONE YEAR WEIGHTED
            softly.assertThat(getNetworkDetailsOneYearWeightedTotalValue(networkDetailsModel.getNetwork(), SPOUSE))
                    .as("Network Details 'Total One Year Weighted' from REST isn't equal 'Total One Year Weighted' from UI")
                    .isEqualTo(new Currency(networkDetailsModel.getRateCalcSp(), RoundingMode.HALF_UP).toString());
        });
    }

    private void verifyNetworkChildDetailsData(NetworkDetailsModel networkDetailsModel, List<ChildDetailsModel> childDetailsFromUI) {
        assertSoftly(softly -> {
            softly.assertThat(networkDetailsModel.getChildDetails().size()).isEqualTo(childDetailsFromUI.size());
            childDetailsFromUI.forEach(modelFromUI -> {
                //get appropriate 'ChildDetailsModel' from rest
                ChildDetailsModel modelFromRest = networkDetailsModel.getChildDetailsModelByBenefitCategory(modelFromUI.getBenefitCategory());
                //checks
                softly.assertThat(modelFromUI.getBase()).isEqualTo(new Currency(modelFromRest.getBase(), RoundingMode.HALF_UP).toString());
                softly.assertThat(modelFromUI.getDeductibleCredit()).isEqualTo(new Currency(modelFromRest.getDeductibleCredit(), RoundingMode.HALF_UP).toString());
                softly.assertThat(modelFromUI.getDeductibleUtil()).isEqualTo(new BigDecimal(modelFromRest.getDeductibleUtil()).setScale(4, RoundingMode.HALF_UP).toString());
                softly.assertThat(modelFromUI.getClaimsRate()).isEqualTo(new Currency(modelFromRest.getClaimsRate(), RoundingMode.HALF_UP).toString());
                softly.assertThat(modelFromUI.getRateCalc()).isEqualTo(new Currency(modelFromRest.getRateCalc(), RoundingMode.HALF_UP).toString());
            });
            //Check Total -> ONE YEAR CLAIMS RATE
            BigDecimal sumOfClaimsRate = new BigDecimal("0");
            for (ChildDetailsModel model : networkDetailsModel.getChildDetails()) {
                sumOfClaimsRate = sumOfClaimsRate.add(new BigDecimal(model.getClaimsRate()));
            }
            softly.assertThat(getNetworkDetailsOneYearClaimsRateTotalValue(networkDetailsModel.getNetwork(), CHILD).replace("$", ""))
                    .as("Network Details 'Total One Claims rate' UI value  should be calculated as sum of not rounded benefits values from REST")
                    .isEqualTo(sumOfClaimsRate.setScale(2, RoundingMode.HALF_UP).toString());
            //Check Total -> ONE YEAR WEIGHTED
            softly.assertThat(getNetworkDetailsOneYearWeightedTotalValue(networkDetailsModel.getNetwork(), CHILD))
                    .as("Network Details 'Total One Year Weighted' from REST isn't equal 'Total One Year Weighted' from UI")
                    .isEqualTo(new Currency(networkDetailsModel.getRateCalcCh(), RoundingMode.HALF_UP).toString());
        });
    }

}
