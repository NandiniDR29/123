package com.exigen.ren.modules.rating.gb_vs;

import com.exigen.ipb.eisa.controls.ratingreport.RedistributionCalculatorComboBox;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.CaseProfileDetailsTab;
import com.exigen.ren.main.modules.caseprofile.tabs.FileIntakeManagementTab;
import com.exigen.ren.main.modules.caseprofile.tabs.ProductAndPlanManagementTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.rating.RatingReportBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.rating.model.gb_vs.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.RatingReportConstants.*;
import static com.exigen.ren.main.enums.RatingReportConstants.ANNUAL_PREMIUM;
import static com.exigen.ren.main.enums.RatingReportConstants.EMPLOYEE_ONLY;
import static com.exigen.ren.main.enums.RatingReportConstants.PCT_LOWERCASE_FROM_CURRENT;
import static com.exigen.ren.main.enums.RatingReportConstants.PCT_LOWERCASE_FROM_RENEWAL;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryRatingReportTierColumns.*;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData.GROUP_DOMICILE_STATE;
import static com.exigen.ren.main.modules.caseprofile.metadata.ProductAndPlanManagementTabMetaData.PRODUCT;
import static com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType.GB_VS;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab.RatingReportView.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab.RatingReportView.COMPOSITE_RATE;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab.RatingReportView.CalculateRateView.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab.RatingReportView.CalculateRateView.cbxRedistributeRate;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab.RatingReportView.enrolledByStateTable;

public class RatingVisionReportBaseTest extends RatingReportBaseTest implements CaseProfileContext, GroupVisionMasterPolicyContext {

    static final String PLAN_B = "PlanB";
    static final String PLAN_C = "PlanC";
    static final Map<String, String> PLANS_MAPPING = ImmutableMap.of(
            PLAN_B, PolicyConstants.PlanVision.PlanB,
            PLAN_C, PolicyConstants.PlanVision.PlanC,
            PLAN_ALACARTE, PolicyConstants.PlanVision.A_LA_CARTE,
            PLAN_ASO_ALACARTE, PolicyConstants.PlanVision.ASOALC_VIS
    );

    String initiateAndRateQuote(TestData td) {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile")
                .adjust(TestData.makeKeyPath(ProductAndPlanManagementTab.class.getSimpleName(), PRODUCT.getLabel()), GB_VS.getName())
                .adjust(TestData.makeKeyPath(CaseProfileDetailsTab.class.getSimpleName(), GROUP_DOMICILE_STATE.getLabel()), "NY")
                .adjust(TestData.makeKeyPath(FileIntakeManagementTab.class.getSimpleName() + "[0]", FileIntakeManagementTabMetaData.UPLOAD_FILE.getLabel(),
                        FileIntakeManagementTabMetaData.UploadFileDialog.FILE_UPLOAD.getLabel()), "$<file:Census_File_AllPlans_VIS.xlsx>"));
        quoteInitiateAndFillUpToTab(td, PremiumSummaryTab.class, true);
        premiumSummaryTab.rate();
        return QuoteSummaryPage.getQuoteNumber();
    }

    ResponseContainer<RatingReportVisionModel> getVisionRatingReportByPolicyNumber(String policyNumber) {
        String policyId = DBService.get().getValue(String.format("Select id from PolicySummary where policyNumber = '%s'", policyNumber)).get();
        return ratingReportRestService.getVisionRatingReport(policyId);
    }

    void checkAdminExpensesSection(AdminCostModel adminCostModel, boolean isAso) {
        assertSoftly(softly -> {
            expandAdminExpenses();
            if (isAso) {
                Map<String, String> asoData = getAsoData();
                softly.assertThat(new BigDecimal(adminCostModel.getExpensesNetCommission()).setScale(2, RoundingMode.HALF_UP).toString()).isEqualTo(asoData.get("Expenses Net Commissions"));
                softly.assertThat(new BigDecimal(adminCostModel.getCommission()).setScale(2, RoundingMode.HALF_UP).toString()).isEqualTo(asoData.get("Commission"));
                softly.assertThat(new BigDecimal(adminCostModel.getASOFee()).setScale(2, RoundingMode.HALF_UP).toString()).isEqualTo(asoData.get("ASO Fee"));
                softly.assertThat(new BigDecimal(adminCostModel.getInitialASOFeePEPM()).setScale(2, RoundingMode.HALF_UP).toString()).isEqualTo(asoData.get("Initial ASO Fee Per Employee"));
            }
            Map<String, String> fullyInsuredData = getFullyInsuredData();
            softly.assertThat(new BigDecimal(adminCostModel.getExpensesNetCommission()).setScale(2, RoundingMode.HALF_UP).toString()).isEqualTo(fullyInsuredData.get("Expenses Net Commissions"));
            softly.assertThat(new BigDecimal(adminCostModel.getCommission()).setScale(2, RoundingMode.HALF_UP).toString()).isEqualTo(fullyInsuredData.get("Commission"));
        });
    }

    void checkDemographicTab(RatingReportVisionModel ratingReportModel, List<String> plansNames) {
        expandRatingDetails();
        openDemographicSection();

        assertSoftly(softly -> plansNames.forEach(
                planName -> {
                    cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(planName));
                    PlanCalculationModel planCalculationModel = getPlanCalculationModelByPlanName(planName, ratingReportModel);
                    Map<String, String> areaFactorsData = getAreaFactorsData();
                    softly.assertThat(new BigDecimal(planCalculationModel.getAreaFactor()).setScale(4, RoundingMode.HALF_UP).toString()).as("Area Factor from UI is not equal AreaFactor from REST").isEqualTo(areaFactorsData.get("Area Factor"));
                    softly.assertThat(new BigDecimal(planCalculationModel.getTrend()).setScale(4, RoundingMode.HALF_UP).toString()).isEqualTo(areaFactorsData.get("Trend"));
                    List<String> enrollmentStates = new ArrayList<>();
                    planCalculationModel.getEnrollmentByState().forEach(
                            enrollmentByState -> {
                                if (!"0".equals(enrollmentByState.getNumberOfLives())) {
                                    enrollmentStates.add(enrollmentByState.getState());
                                }
                            }
                    );
                    if (enrollmentStates.isEmpty()) {
                        throw new IstfException("Could not find any states with participants");
                    }
                    //check enrolled by state
                    enrolledByStateTable.getRows().stream().filter(row -> row.getIndex() > 1).limit(10).forEach(row -> {
                        String stateEnrolled = row.getCell(2).getValue();
                        EnrollmentByStateModel enrollmentByStateModel = planCalculationModel.getEnrollmentByState()
                                .stream().filter(enrollment -> stateEnrolled.equals(enrollment.getState())).findFirst().get();
                        softly.assertThat(row.getCell(3)).hasValue(enrollmentByStateModel.getNumberOfLives());
                        softly.assertThat(row.getCell(4)).hasValue(formatValueToPct(enrollmentByStateModel.getEnrolledPct()));
                    });
                    //check Total (ELIGIBLE = 'planCalculation.totalEnrollment'; PERCENT = '100%' by default )
                    softly.assertThat(enrolledByStateTable.getRow(enrolledByStateTable.getRowsCount() + 1).getCell(3)).hasValue(planCalculationModel.getTotalEnrollment());
                    softly.assertThat(enrolledByStateTable.getRow(enrolledByStateTable.getRowsCount() + 1).getCell(4)).hasValue("100.00%");
                    //check counts of Enrolled States on UI equals counts of Enrolled states with participants from Rating Report
                    softly.assertThat(enrolledByStateTable.getRowsCount() - 1).isEqualTo(enrollmentStates.size());
                }));
    }

    void checkFactorsTab(RatingReportVisionModel ratingReportModel, List<String> plansNames) {
        openFactorsSection();
        assertSoftly(softly -> plansNames.forEach(
                planName -> {
                    cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(planName));
                    Map<String, String> visionFactorsData = getVisionFactorsData();
                    PlanCalculationModel planCalculationModel = getPlanCalculationModelByPlanName(planName, ratingReportModel);
                    FactorsModel factorModel = planCalculationModel.getFactors();
                    softly.assertThat(new BigDecimal(factorModel.getBaselineCost()).setScale(2, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Baseline Cost"));
                    softly.assertThat(new BigDecimal(factorModel.getPlanFrequency()).setScale(4, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Plan Frequency"));
                    softly.assertThat(new BigDecimal(factorModel.getArea()).setScale(4, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("State/Area Factor"));
                    softly.assertThat(new BigDecimal(factorModel.getContribution()).setScale(4, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Contribution"));
                    softly.assertThat(new BigDecimal(factorModel.getAllowance()).setScale(4, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Frame/contact Allowance"));
                    softly.assertThat(new BigDecimal(factorModel.getProduct()).setScale(4, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Product"));
                    softly.assertThat(new BigDecimal(factorModel.getNetwork()).setScale(4, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Network"));
                    softly.assertThat(new BigDecimal(factorModel.getCopay()).setScale(4, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Copay"));
                    softly.assertThat(new BigDecimal(factorModel.getLensOptions()).setScale(4, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Lens Options"));
                    softly.assertThat(new BigDecimal(factorModel.getScratchCoating()).setScale(4, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Scratch Coating Factor"));
                    softly.assertThat(new BigDecimal(factorModel.getSafetyGlasses()).setScale(4, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Safety Glasses Factor"));
                    softly.assertThat(new BigDecimal(factorModel.getPhotochromicLens()).setScale(4, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Photochromic Lens Factor"));
                    softly.assertThat(new BigDecimal(factorModel.getProgressiveLensesInFull()).setScale(4, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Progressive Lenses in Full"));
                    softly.assertThat(new BigDecimal(factorModel.getTrendMultiple()).setScale(4, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Trend Multiple"));
                    softly.assertThat(new BigDecimal(factorModel.getTexasAdjustment()).setScale(4, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Texas Plan"));
                    softly.assertThat(new BigDecimal(factorModel.getTotalCost()).setScale(2, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Total Cost"));
                    softly.assertThat(new BigDecimal(planCalculationModel.getTlr()).setScale(2, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Targeted Loss Ratio"));
                    softly.assertThat(new BigDecimal(planCalculationModel.getNeededPremium()).setScale(2, RoundingMode.HALF_UP).toString()).isEqualTo(visionFactorsData.get("Needed Premium"));
                }));
    }

    void checkRedistributionCalculatorTab(Map<String, String> valuesForCheck, RatingReportVisionModel ratingReportModel, List<String> plansNames, boolean isMultiOption) {
        openRedistributionCalculatorSection();
        String overrideFirstUWAdjustment = valuesForCheck.get(UW_ADJUSTMENT_FIRST);
        String overrideSecondUWAdjustment = valuesForCheck.get(UW_ADJUSTMENT_SECOND);
        String currentRates = valuesForCheck.get(CURRENT_RATES.getName());
        String renewalRates = valuesForCheck.get(RENEWAL_RATES.getName());

        assertSoftly(softly -> {
            if (isMultiOption) {
                softly.assertThat(chbxMultiOption).isPresent();
            }
            else {
                softly.assertThat(chbxMultiOption).isAbsent();
            }
            plansNames.forEach(
                    planName -> {
                        PlanModel planModel = null;
                        cbxRatingDetailsSelectPlan.setValue(PLANS_MAPPING.get(planName));
                        PlanCalculationModel planCalculationModel = getPlanCalculationModelByPlanName(planName, ratingReportModel);

                        softly.assertThat(txtOverrideFirstUWAdjustment).hasValue(overrideFirstUWAdjustment);
                        softly.assertThat(txtOverrideSecondUWAdjustment).hasValue(overrideSecondUWAdjustment);

                        softly.assertThat(cbxReasonFirstUWAdjustment.getAllValuesExceptFirstCombineValue())
                                .isEqualTo(ImmutableList.of(DUAL_OPTION, PARTICIPATION_ADJ, INDIVIDUAL_ADJ, ACTUARIAL_BENEFIT_ADJ, OTHER));
                        softly.assertThat(cbxReasonSecondUWAdjustment.getAllValuesExceptFirstCombineValue())
                                .isEqualTo(ImmutableList.of(DUAL_OPTION, PARTICIPATION_ADJ, INDIVIDUAL_ADJ, ACTUARIAL_BENEFIT_ADJ, OTHER));
                        softly.assertThat(cbxRedistributeRate.getAllValuesExceptFirstCombineValue())
                                .isEqualTo(ImmutableList.of(MANUAL, PCT_FROM_CURRENT, PCT_FROM_RENEWAL));
                        //Check Composite Rate
                        softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, ADJUSTED_FORMULA_RATES.getName()))
                                .isEqualTo(new BigDecimal(planCalculationModel.getFinalCompositeRate()).setScale(2, RoundingMode.HALF_UP).toString());
                        softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, CURRENT_RATES.getName()))
                                .isEqualTo(currentRates);//0
                        softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, RENEWAL_RATES.getName()))
                                .isEqualTo(renewalRates);
                        softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, REDISTRIBUTED_RATES.getName()))
                                .isEqualTo(new BigDecimal(planCalculationModel.getProposedCompositeRate()).setScale(2, RoundingMode.HALF_UP).toString());
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
                            planModel = getPlanModelByPlanName(planName, ratingReportModel);
                        }
                        //Check Tier Rates
                        final PlanModel finalPlanModel = planModel;
                        planCalculationModel.getTierRates().forEach(
                                tierRateModel -> {
                                    String tier = tierRateModel.getTier();
                                    softly.assertThat(getCellValueTierTable(tier, ADJUSTED_MANUAL_RATES.getName()))
                                            .isEqualTo(new BigDecimal(tierRateModel.getAdjustedManualRate()).setScale(2, RoundingMode.HALF_UP).toString());
                                    softly.assertThat(getCellValueTierTable(tier, ADJUSTED_FORMULA_RATES.getName()))
                                            .isEqualTo(new BigDecimal(tierRateModel.getFinalRate()).setScale(2, RoundingMode.HALF_UP).toString());
                                    softly.assertThat(getCellValueTierTable(tier, REDISTRIBUTED_RATES.getName()))
                                            .isEqualTo(new BigDecimal(tierRateModel.getProposedRate()).setScale(2, RoundingMode.HALF_UP).toString());
                                    if ("0".equals(currentRates) && "0".equals(renewalRates)) {
                                        softly.assertThat(getCellValueTierTable(tier, CURRENT_RATES.getName()))
                                                .isEqualTo(EMPTY);
                                        softly.assertThat(getCellValueTierTable(tier, RENEWAL_RATES.getName()))
                                                .isEqualTo(EMPTY);
                                        setCellValueTierTable(tier, CURRENT_ENROLLMENT.getName(), getCellValueTierTable(EMPLOYEE_ONLY, CURRENT_ENROLLMENT.getName()));
                                        setCellValueTierTable(tier, CURRENT_RATES.getName(), EMPTY);
                                        setCellValueTierTable(tier, RENEWAL_RATES.getName(), EMPTY);
                                    }
                                    else {
                                        CurrentRatesBreakdownModel currentRatesBreakdownModel = getCurrentRatesBreakdownModelByTierName(tier, finalPlanModel);
                                        softly.assertThat(getCellValueTierTable(tier, CURRENT_ENROLLMENT.getName())).isEqualTo(currentRatesBreakdownModel.getNumberOfLives());
                                        softly.assertThat(getCellValueTierTable(tier, CURRENT_RATES.getName())).isEqualTo(currentRatesBreakdownModel.getCurrentRate()).isEqualTo(currentRates);
                                        softly.assertThat(getCellValueTierTable(tier, RENEWAL_RATES.getName())).isEqualTo(currentRatesBreakdownModel.getRenewalRate()).isEqualTo(renewalRates);
                                    }
                                });
                    });
        });
    }

    void checkSelectPlanMultiOption(RatingReportVisionModel ratingReportModel, String planName) {
        assertSoftly(softly -> {
            StaticElement parentElementForMultiPlan = new StaticElement(ByT.xpath(PARENT_LOCATOR_MULTIOPTION).format(PLANS_MAPPING.get(planName)));
            PlanCalculationModel planCalculationModel = getPlanCalculationModelByPlanName(planName, ratingReportModel);

            softly.assertThat(new TextBox(parentElementForMultiPlan, txtOverrideFirstUWAdjustment.getLocator())).hasValue("0.00");
            softly.assertThat(new TextBox(parentElementForMultiPlan, txtOverrideSecondUWAdjustment.getLocator())).hasValue("0.00");
            softly.assertThat(new RedistributionCalculatorComboBox(parentElementForMultiPlan, cbxReasonFirstUWAdjustment.getLocator()).getAllValuesExceptFirstCombineValue())
                    .isEqualTo(ImmutableList.of(DUAL_OPTION, PARTICIPATION_ADJ, INDIVIDUAL_ADJ, ACTUARIAL_BENEFIT_ADJ, OTHER));
            softly.assertThat(new RedistributionCalculatorComboBox(parentElementForMultiPlan, cbxReasonSecondUWAdjustment.getLocator()).getAllValuesExceptFirstCombineValue())
                    .isEqualTo(ImmutableList.of(DUAL_OPTION, PARTICIPATION_ADJ, INDIVIDUAL_ADJ, ACTUARIAL_BENEFIT_ADJ, OTHER));
            softly.assertThat(new RedistributionCalculatorComboBox(parentElementForMultiPlan, cbxRedistributeRate.getLocator()).getAllValuesExceptFirstCombineValue())
                    .isEqualTo(ImmutableList.of(MANUAL, PCT_FROM_CURRENT, PCT_FROM_RENEWAL));
            //Check Composite Rate
            softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, ADJUSTED_FORMULA_RATES.getName(), PLANS_MAPPING.get(planName)))
                    .isEqualTo(new BigDecimal(planCalculationModel.getFinalCompositeRate()).setScale(2, RoundingMode.HALF_UP).toString());
            softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, CURRENT_RATES.getName(), PLANS_MAPPING.get(planName)))
                    .isEqualTo("0");
            softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, RENEWAL_RATES.getName(), PLANS_MAPPING.get(planName)))
                    .isEqualTo("0");
            softly.assertThat(getCellValueTierTable(COMPOSITE_RATE, REDISTRIBUTED_RATES.getName(), PLANS_MAPPING.get(planName)))
                    .isEqualTo(new BigDecimal(planCalculationModel.getProposedCompositeRate()).setScale(2, RoundingMode.HALF_UP).toString());
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
                                .isEqualTo(new BigDecimal(tierRateModel.getAdjustedManualRate()).setScale(2, RoundingMode.HALF_UP).toString());
                        softly.assertThat(getCellValueTierTable(tierRateModel.getTier(), ADJUSTED_FORMULA_RATES.getName(), PLANS_MAPPING.get(planName)))
                                .isEqualTo(new BigDecimal(tierRateModel.getFinalRate()).setScale(2, RoundingMode.HALF_UP).toString());
                        softly.assertThat(getCellValueTierTable(tierRateModel.getTier(), REDISTRIBUTED_RATES.getName(), PLANS_MAPPING.get(planName)))
                                .isEqualTo(new BigDecimal(tierRateModel.getProposedRate()).setScale(2, RoundingMode.HALF_UP).toString());
                        softly.assertThat(getCellValueTierTable(tierRateModel.getTier(), CURRENT_RATES.getName(), PLANS_MAPPING.get(planName)))
                                .isEqualTo(EMPTY);
                        softly.assertThat(getCellValueTierTable(tierRateModel.getTier(), RENEWAL_RATES.getName(), PLANS_MAPPING.get(planName)))
                                .isEqualTo(EMPTY);
                        setCellValueTierTable(tierRateModel.getTier(), CURRENT_ENROLLMENT.getName(), getCellValueTierTable(EMPLOYEE_ONLY, CURRENT_ENROLLMENT.getName(), PLANS_MAPPING.get(planName)));
                        setCellValueTierTable(tierRateModel.getTier(), CURRENT_RATES.getName(), EMPTY, PLANS_MAPPING.get(planName));
                        setCellValueTierTable(tierRateModel.getTier(), RENEWAL_RATES.getName(), EMPTY, PLANS_MAPPING.get(planName));
                    });
        });
    }

    TierRateModel getTierRateModelByTierName(String tierName, PlanCalculationModel planCalculationModel) {
        List<TierRateModel> listTierRateModels = planCalculationModel.getTierRates();
        TierRateModel tierRateModel = null;
        for (TierRateModel model : listTierRateModels) {
            if (model.getTier().equals(tierName)) {
                tierRateModel = model;
            }
        }
        if (tierRateModel == null) {
            throw new IstfException(String.format("Tier Rate Model with name '%s' isn't exist in Plan Calculation Model. Check tier name", tierName));
        }
        return tierRateModel;
    }

    PlanCalculationModel getPlanCalculationModelByPlanName(String planName, RatingReportVisionModel ratingReportModel) {
        List<PlanCalculationModel> planCalculationModels = ratingReportModel.getPlanCalculation();
        PlanCalculationModel planCalculationModel = null;
        for (PlanCalculationModel model : planCalculationModels) {
            if (model.getPlanName().equals(planName)) {
                planCalculationModel = model;
            }
        }
        if (planCalculationModel == null) {
            throw new IstfException(String.format("Plan Calculation Model with name '%s' isn't exist in Rating Report VisionModel. Check Plan name", planName));
        }
        return planCalculationModel;
    }

    private PlanModel getPlanModelByPlanName(String planName, RatingReportVisionModel ratingReportModel) {
        List<PlanModel> planModels = ratingReportModel.getReports().getPlans();
        PlanModel planModel = null;
        for (PlanModel model : planModels) {
            if (model.getPlanName().equals(planName)) {
                planModel = model;
            }
        }
        if (planModel == null) {
            throw new IstfException(String.format("Plan Model with name '%s' isn't exist in Rating Report Vision Model. Check Plan Model name", planName));
        }
        return planModel;
    }

    private CurrentRatesBreakdownModel getCurrentRatesBreakdownModelByTierName(String tierName, PlanModel planModel) {
        List<CurrentRatesBreakdownModel> currentRatesBreakdownModels = planModel.getCurrentRatesBreakdown();
        CurrentRatesBreakdownModel currentRatesBreakdownModel = null;
        for (CurrentRatesBreakdownModel model : currentRatesBreakdownModels) {
            if (model.getTier().equals(tierName)) {
                currentRatesBreakdownModel = model;
            }
        }
        if (currentRatesBreakdownModel == null) {
            throw new IstfException(String.format("Current Rates Breakdown Model with name '%s' isn't exist in Plan Model. Check Tier name", tierName));
        }
        return currentRatesBreakdownModel;
    }
}
