package com.exigen.ren.modules.rating.gb_dn;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.DataProviderFactory;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.helpers.logging.RatingLogGrabber;
import com.exigen.ren.helpers.logging.RatingLogHolder;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.rating.RatingBaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ActionConstants.VIEW_RATE_DETAILS;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupDentalCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSelectionValues.*;
import static com.exigen.ren.main.enums.TableConstants.PlanTierAndRatingSelection.*;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryASOFeeTable.UNDERWRITTEN_ASO_FEE;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryCoveragesTable.*;
import static com.exigen.ren.main.enums.TableConstants.RateDetailsTable.SUB_GROUP_NAME;
import static com.exigen.ren.main.enums.TableConstants.RateDetailsTable.TIER;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetaData.RATE_TYPE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab.planTable;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.premiumSummaryASOFeeTable;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.premiumSummaryCoveragesTable;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRatingIntegrationDN extends RatingBaseTest implements GroupDentalMasterPolicyContext {

    private static final String REN_42615_TC1 = "REN-42615_TC1";
    private static final String REN_42615_TC2 = "REN-42615_TC2";
    private static final String REN_42615_TC3 = "REN-42615_TC3";
    private static final String REN_42615_TC4 = "REN-42615_TC4";
    private static final String REN_43687_TC1 = "REN-43687_TC1";
    private static final String REN_43687_TC2 = "REN-43687_TC2";
    private static final String REN_43687_TC3 = "REN-43687_TC3";
    private static final String REN_43687_TC4 = "REN-43687_TC4";
    private static final String EMPLOYEE_ONLY_ID = "Employee only";

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-42615"}, component = POLICY_GROUPBENEFITS)
    public void testRatingIntegrationPlanAlacarteCensusAppliedFamilyTier() {
        TestData tdQuote = tdSpecific().getTestData("TestData_PlanAlacarte");
        TestData tdRequest = tdSpecific().getTestData("RequestData_PlanAlacarte");
        testRatingIntegration(REN_42615_TC1, tdQuote, tdRequest);
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-42615"}, component = POLICY_GROUPBENEFITS)
    public void testRatingIntegrationPlanAlacarteCensusNotAppliedFamilyTier() {
        TestData tdQuote = tdSpecific().getTestData("TestData_PlanAlacarte").adjust(PremiumSummaryTab.class.getSimpleName(), DataProviderFactory.emptyData());
        TestData tdRequest = tdSpecific().getTestData("RequestData_PlanAlacarte");
        testRatingIntegration(REN_42615_TC2, tdQuote, tdRequest);
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-42615"}, component = POLICY_GROUPBENEFITS)
    public void testRatingIntegrationPlanAlacarteCensusAppliedAreaTier() {
        TestData tdQuote = tdSpecific().getTestData("TestData_PlanAlacarte")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", RATING.getLabel(), RATE_TYPE.getLabel()), "Area + Tier");
        TestData tdRequest = tdSpecific().getTestData("RequestData_PlanAlacarte");
        testRatingIntegration(REN_42615_TC3, tdQuote, tdRequest);
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-42615"}, component = POLICY_GROUPBENEFITS)
    public void testPlanAlacarteCensusNotAppliedFamilyTierParticipantsNull() {
        TestData tdQuote = tdSpecific().getTestData("TestData_PlanAlacarte")
                .adjust(classificationManagementMpTab.getMetaKey(), tdSpecific().getTestData("ClassificationManagementTabALACARTE_ParticipantsNull"))
                .adjust(PremiumSummaryTab.class.getSimpleName(), DataProviderFactory.emptyData());
        TestData tdRequest = tdSpecific().getTestData("RequestData_PlanAlacarte");
        testRatingIntegration(REN_42615_TC4, tdQuote, tdRequest);
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-43687"}, component = POLICY_GROUPBENEFITS)
    public void testRatingIntegrationPlanTripleAdvantage() {
        TestData tdQuote = tdSpecific().getTestData("TestData_PlanTripleAdvantage");
        TestData tdRequest = tdSpecific().getTestData("RequestData_PlanTripleAdvantage");
        testRatingIntegration(REN_43687_TC1, tdQuote, tdRequest);
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-43687"}, component = POLICY_GROUPBENEFITS)
    public void testRatingIntegrationPlansBasicEposMajorEposFlexPlus() {
        TestData tdQuote = tdSpecific().getTestData("TestData_PlansBasicEposMajorEposFlexPlus");
        TestData tdRequest = tdSpecific().getTestData("RequestData_PlansBasicEposMajorEposFlexPlus");
        testRatingIntegration(REN_43687_TC2, tdQuote, tdRequest);
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-43687"}, component = POLICY_GROUPBENEFITS)
    public void testRatingIntegrationPlanWA1() {
        TestData tdQuote = tdSpecific().getTestData("TestData_PlanWA1");
        TestData tdRequest = tdSpecific().getTestData("RequestData_PlanWA1");
        testRatingIntegration(REN_43687_TC3, tdQuote, tdRequest);
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-43687"}, component = POLICY_GROUPBENEFITS)
    public void testRatingIntegrationPlanAso() {
        TestData tdQuote = tdSpecific().getTestData("TestData_PlanAso");
        TestData tdRequest = tdSpecific().getTestData("RequestData_PlanAso");
        testRatingIntegration(REN_43687_TC4, tdQuote, tdRequest);
    }

    private void testRatingIntegration(String testCaseId, TestData tdQuote, TestData tdExpectedRequest) {
        TestData tdExpectedResponse = new SimpleDataProvider();
        AtomicReference<Map<String, String>> expectedRequest = new AtomicReference<>(new HashMap<>());
        List<String> coverageTiers = new ArrayList<>();
        List<String> plans;

        LOGGER.info(String.format("TEST: %s: Steps 1-2", testCaseId));
        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData()
                .adjust(makeKeyPath(GeneralTab.class.getSimpleName(), AGENCY_ASSIGNMENT.getLabel() + "[0]", GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(),
                        GeneralTabMetaData.AddAgencyMetaData.AGENCY_CODE.getLabel()), "39B00"));
        caseProfile.create(tdSpecific().getTestData("CaseProfile"));
        initiateQuoteAndFillUpToTab(tdQuote, PremiumSummaryTab.class, true);
        premiumSummaryTab.rate();
        assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED);
        String quoteNumber = QuoteSummaryPage.getQuoteNumber();
        String totalNumberParticipants = "not specified";

        LOGGER.info(String.format("TEST: %s: Step 3", testCaseId));
        RatingLogHolder ratingLogHolder = new RatingLogGrabber().grabRatingLog(quoteNumber);
        LOGGER.info(String.format("Request from rating log:\n %s", ratingLogHolder.getRequestLog().getFormattedLogContent()));
        LOGGER.info(String.format("Response from rating log:\n %s", ratingLogHolder.getResponseLog().getFormattedLogContent()));
        Map<String, String> requestFromLog = ratingLogHolder.getRequestLog().getOpenLFieldsMap();
        Map<String, String> responseFromLog = ratingLogHolder.getResponseLog().getOpenLFieldsMap();
        tdExpectedRequest.adjust("policyID", quoteNumber);
        switch (testCaseId) {
            case REN_42615_TC1: {
                final String employeeID = "84";
                final String[] plansIndexes = getFirstMatchedIndexes(ImmutableList.of("plans", "census", "employeeID"), employeeID, requestFromLog);
                final String[] tier1Indexes = getFirstMatchedIndexes(ImmutableList.of("plans", "enrollmentBreakdown", "tier"), EMPLOYEE_ONLY_ID, requestFromLog);
                final String[] tier2Indexes = getFirstMatchedIndexes(ImmutableList.of("plans", "enrollmentBreakdown", "tier"), EMPLOYEE_SPOUSE, requestFromLog);
                final String[] tier3Indexes = getFirstMatchedIndexes(ImmutableList.of("plans", "enrollmentBreakdown", "tier"), EMPLOYEE_CHILD_REN, requestFromLog);
                final String[] tier4Indexes = getFirstMatchedIndexes(ImmutableList.of("plans", "enrollmentBreakdown", "tier"), EMPLOYEE_FAMILY, requestFromLog);
                tdExpectedRequest
                        .adjust("plans[0].assumedParticipationPctOverride", "0.20000000")
                        //Known data for employee with id=84  from Census file 'Census_File_AllPlans_DEN.xlsx
                        .adjust(String.format("plans[%s].census[%s].employeeID", plansIndexes[0], plansIndexes[1]), employeeID)
                        .adjust(String.format("plans[%s].census[%s].classID", plansIndexes[0], plansIndexes[1]), "1")
                        .adjust(String.format("plans[%s].census[%s].dob", plansIndexes[0], plansIndexes[1]), "1970-01-01")
                        .adjust(String.format("plans[%s].census[%s].elected", plansIndexes[0], plansIndexes[1]), "true")
                        .adjust(String.format("plans[%s].census[%s].age", plansIndexes[0], plansIndexes[1]), "49")
                        .adjust(String.format("plans[%s].census[%s].gender", plansIndexes[0], plansIndexes[1]), "F")
                        .adjust(String.format("plans[%s].census[%s].state", plansIndexes[0], plansIndexes[1]), "MS")
                        .adjust(String.format("plans[%s].census[%S].zip", plansIndexes[0], plansIndexes[1]), "38901")
                        .adjust(String.format("plans[%s].census[%s].tier", plansIndexes[0], plansIndexes[1]), "Employee + Family")
                        //Known data for tiers from Census file 'Census_File_AllPlans_DEN.xlsx. Tier can be under random index in range 0 to 3
                        .adjust(String.format("plans[%s].enrollmentBreakdown[%s].tier", tier1Indexes[0], tier1Indexes[1]), EMPLOYEE_ONLY_ID)
                        .adjust(String.format("plans[%s].enrollmentBreakdown[%s].numberOfLives", tier1Indexes[0], tier1Indexes[1]), "2.0")
                        .adjust(String.format("plans[%s].enrollmentBreakdown[%s].tier", tier2Indexes[0], tier2Indexes[1]), EMPLOYEE_SPOUSE)
                        .adjust(String.format("plans[%s].enrollmentBreakdown[%s].numberOfLives", tier2Indexes[0], tier2Indexes[1]), "2.0")
                        .adjust(String.format("plans[%s].enrollmentBreakdown[%s].tier", tier3Indexes[0], tier3Indexes[1]), EMPLOYEE_CHILD_REN)
                        .adjust(String.format("plans[%s].enrollmentBreakdown[%s].numberOfLives", tier3Indexes[0], tier3Indexes[1]), "2.0")
                        .adjust(String.format("plans[%s].enrollmentBreakdown[%s].tier", tier4Indexes[0], tier4Indexes[1]), EMPLOYEE_FAMILY)
                        .adjust(String.format("plans[%s].enrollmentBreakdown[%s].numberOfLives", tier4Indexes[0], tier4Indexes[1]), "2.0")
                        .adjust("experienceClaimAmount", "150.00")
                        .adjust("experienceCredibility", "0.0030000");
                expectedRequest.set(createExpectedRequestData(tdExpectedRequest));
                tdExpectedResponse = tdSpecific().getTestData("ResponseData_PlanAlacarte");
                totalNumberParticipants = new BigDecimal(responseFromLog.get("planCalculations[0].manualClaimDetail.totalEnrolled")).setScale(0, RoundingMode.HALF_EVEN).toString();
                assertThat(totalNumberParticipants).isEqualTo("8"); // 8 - known numbers of participants from Census file
                coverageTiers = ImmutableList.of(EMPLOYEE_ONLY, EMPLOYEE_SPOUSE, EMPLOYEE_CHILD_REN, EMPLOYEE_FAMILY);
                plans = ImmutableList.of(PLAN_ALACARTE);
                break;
            }
            case REN_42615_TC2: {
                expectedRequest.set(createExpectedRequestData(tdExpectedRequest));
                tdExpectedResponse = tdSpecific().getTestData("ResponseData_PlanAlacarte");
                coverageTiers = ImmutableList.of(EMPLOYEE_ONLY, EMPLOYEE_SPOUSE, EMPLOYEE_CHILD_REN, EMPLOYEE_FAMILY);
                plans = ImmutableList.of(PLAN_ALACARTE);
                totalNumberParticipants = getNumberOfParticipantsFromTD(tdQuote.getTestData(ClassificationManagementTab.class.getSimpleName()));
                break;
            }
            case REN_42615_TC3: {
                tdExpectedRequest.adjust("plans[0].rateType", "Area + Tier Rating")
                        //Known data for 'Assumed Participation %' from Census file 'Census_File_AllPlans_DEN.xlsx
                        .adjust("plans[0].assumedParticipationPctOverride", "0.20000000");
                expectedRequest.set(createExpectedRequestData(tdExpectedRequest));
                totalNumberParticipants = new BigDecimal(responseFromLog.get("planCalculations[0].manualClaimDetail.totalEnrolled")).setScale(0, RoundingMode.HALF_EVEN).toString();
                plans = ImmutableList.of(PLAN_ALACARTE);
                break;
            }
            case REN_42615_TC4: {
                expectedRequest.set(createExpectedRequestData(tdExpectedRequest));
                tdExpectedResponse = tdSpecific().getTestData("ResponseData_PlanAlacarte");
                coverageTiers = ImmutableList.of(EMPLOYEE_ONLY, EMPLOYEE_SPOUSE, EMPLOYEE_CHILD_REN, EMPLOYEE_FAMILY);
                plans = ImmutableList.of(PLAN_ALACARTE);
                totalNumberParticipants = new BigDecimal(tdQuote.getTestData("PolicyInformationTab").getValue("Total Number of Eligible Lives"))
                        .multiply(new BigDecimal(responseFromLog.get("planCalculations[0].manualClaimDetail.assumedParticipationPct"))).setScale(0, RoundingMode.HALF_UP).toString();
                break;
            }
            case REN_43687_TC1: {
                expectedRequest.set(createExpectedRequestData(tdExpectedRequest));
                tdExpectedResponse = tdSpecific().getTestData("ResponseData_PlanTripleAdvantage");
                coverageTiers = ImmutableList.of(COMPOSITE);
                plans = ImmutableList.of(PLAN_TRIPLE_ADVANTAGE);
                totalNumberParticipants = getNumberOfParticipantsFromTD(tdQuote.getTestData(ClassificationManagementTab.class.getSimpleName()));
                break;
            }
            case REN_43687_TC2: {
                expectedRequest.set(createExpectedRequestData(tdExpectedRequest));
                tdExpectedResponse = tdSpecific().getTestData("ResponseData_PlansBasicEposMajorEposFlexPlus");
                coverageTiers = ImmutableList.of(EMPLOYEE_ONLY, EMPLOYEE_SPOUSE, EMPLOYEE_CHILD_REN, EMPLOYEE_FAMILY);
                plans = ImmutableList.of(PLAN_BASEPOS, PLAN_FLEX_PLUS, PLAN_MAJEPOS);
                break;
            }
            case REN_43687_TC4: {
                expectedRequest.set(createExpectedRequestData(tdExpectedRequest));
                tdExpectedResponse = tdSpecific().getTestData("ResponseData_PlanAso");
                coverageTiers = ImmutableList.of(EMPLOYEE_ONLY, EMPLOYEE_SPOUSE, EMPLOYEE_CHILD_REN, EMPLOYEE_FAMILY);
                totalNumberParticipants = getNumberOfParticipantsFromTD(tdQuote.getTestData(ClassificationManagementTab.class.getSimpleName()));
                plans = ImmutableList.of(PLAN_ASO);
                break;
            }
            case REN_43687_TC3: {
                expectedRequest.set(createExpectedRequestData(tdExpectedRequest));
                tdExpectedResponse = tdSpecific().getTestData("ResponseData_PlanWA1");
                coverageTiers = ImmutableList.of(EMPLOYEE_ONLY, EMPLOYEE_ONE, EMPLOYEE_FAMILY);
                plans = ImmutableList.of(PLAN_WA_ONE);
                totalNumberParticipants = getNumberOfParticipantsFromTD(tdQuote.getTestData(ClassificationManagementTab.class.getSimpleName()));
                break;
            }
            default:
                throw new IstfException("Test Case id isn't correct");
        }
        assertThat(requestFromLog).containsAllEntriesOf(expectedRequest.get());
        Collection<String> list = tdExpectedResponse.getKeys();

        LOGGER.info(String.format("TEST: %s: Step 4", testCaseId));
        assertThat(responseFromLog.keySet()).containsAll(list);
        String finalTotalNumberParticipants = totalNumberParticipants;

        List<String> finalCoverageTiers = coverageTiers;
        List<String> finalPlans = plans;
        assertSoftly(softly -> {
            if(REN_42615_TC1.equals(testCaseId)) {
                LOGGER.info("TEST: Verification expected response premium values that were provided by OpenL Team (hardcoded)");
                softly.assertThat(responseFromLog).containsAllEntriesOf(getCheckRatingPremiumData());
            }

            LOGGER.info(String.format("TEST: %s: Step 6", testCaseId));
            softly.assertThat(PremiumSummaryTab.txtRatingFormula).hasValue(responseFromLog.get("nature"));

            Map<String, String> plansMapping = getPlansMapping();
            Map<String, String> coveragesMapping = getCoveragesMapping();
            finalPlans.forEach(
                    plan -> {

                        LOGGER.info(String.format("TEST: %s: Step 5", testCaseId));
                        Row row = premiumSummaryCoveragesTable.getRow(PLAN.getName(), plan);
                        String plansIndex = getFirstMatchedIndexes(ImmutableList.of("plans", "planName"), plansMapping.get(plan), requestFromLog)[0];
                        checkNumberOfParticipantsPremiumSummary(tdQuote, testCaseId, plan, finalTotalNumberParticipants, row);
                        softly.assertThat(row.getCell(MANUAL_RATE.getName())).hasValue(new Currency(responseFromLog.get(String.format("planCalculations[%s].manualCompositeRate", plansIndex))).toString());
                        softly.assertThat(row.getCell(EXPERIENCE_RATE.getName())).hasValue(new Currency(responseFromLog.get(String.format("planCalculations[%s].experienceRate", plansIndex))).toString());
                        softly.assertThat(row.getCell(EXPERIENCE_ADJUSTMENT_FACTOR.getName())).hasValue(new BigDecimal(responseFromLog.get(String.format("planCalculations[%s].experienceAdjFactor", plansIndex))).setScale(3, RoundingMode.HALF_UP).toString());
                        softly.assertThat(row.getCell(FORMULA_RATE.getName())).hasValue(new Currency(responseFromLog.get(String.format("planCalculations[%s].proposedCompositeRate", plansIndex))).toString());
                        row.getCell(10).controls.buttons.get(VIEW_RATE_DETAILS).click();

                        LOGGER.info(String.format("TEST: %s: Step 7", testCaseId));
                        if (REN_42615_TC3.equals(testCaseId)) {
                            RateDialogs.ViewRateDetailsDialog.tableRateDetails.getRows().forEach(
                                    rowRateDetails -> {
                                        String areaNumber = rowRateDetails.getCell(SUB_GROUP_NAME.getName()).getValue();
                                        String finalAreaNumber = areaNumber.substring(areaNumber.length() - 1);
                                        String coverage = rowRateDetails.getCell(TIER.getName()).getValue();
                                        List<String[]> indexes = getRatingLogIndexes(ImmutableList.of("planCalculations", "tierRates", "tier"), coveragesMapping.get(coverage), responseFromLog);
                                        indexes.forEach(
                                                index -> {
                                                    LOGGER.info(String.format("Rate for Area with rating log path 'planCalculations[0].tierRates[%s].area' will be verified", index[1]));
                                                    if (responseFromLog.get(String.format("planCalculations[0].tierRates[%s].area", index[1])).equals(finalAreaNumber)) {
                                                        softly.assertThat(rowRateDetails.getCell(TableConstants.RateDetailsTable.RATE.getName())).hasValue(new Currency(responseFromLog.get(String.format("planCalculations[0].tierRates[%s].proposedRate", index[1])), RoundingMode.HALF_EVEN).toString());
                                                    }
                                                });
                                    });
                        }
                        else {
                            finalCoverageTiers.forEach(
                                    coverage -> {
                                        String coverageIndex = getFirstMatchedIndexes(ImmutableList.of("planCalculations", "manualClaimDetail.enrollmentBreakdown", "tier"), coveragesMapping.get(coverage), responseFromLog)[1];
                                        softly.assertThat(RateDialogs.ViewRateDetailsDialog.tableRateDetails.getRow(ImmutableMap.of(TIER.getName(), coverage)).getCell(TableConstants.RateDetailsTable.RATE.getName()))
                                                .hasValue(new Currency(responseFromLog.get(String.format("planCalculations[%s].tierRates[%s].proposedRate", plansIndex, coverageIndex)), RoundingMode.HALF_EVEN).toString());
                                    });
                        }
                        RateDialogs.ViewRateDetailsDialog.close();
                    });
            if (REN_43687_TC4.equals(testCaseId)) {
                softly.assertThat(premiumSummaryASOFeeTable.getRow(1).getCell(UNDERWRITTEN_ASO_FEE.getName()))
                        .hasValue(new Currency(responseFromLog.get("asoAdminCost.asoFinalPEPM"), RoundingMode.HALF_EVEN).toString());
                softly.assertThat(new BigDecimal(getASOPreCommissionPEPMFromDb(quoteNumber)).setScale(2, RoundingMode.HALF_UP).toString())
                        .isEqualTo(new BigDecimal(responseFromLog.get("asoAdminCost.asoPreCommisionPEPM")).setScale(2, RoundingMode.HALF_UP).toString());
            }

            LOGGER.info(String.format("TEST: %s: Step 8", testCaseId));
            planDefinitionTab.navigate();
            finalPlans.forEach(
                    plan -> {
                        Row row = planTable.getRow(PLAN.getName(), plan);
                        String plansIndex = getFirstMatchedIndexes(ImmutableList.of("plans", "planName"), plansMapping.get(plan), requestFromLog)[0];
                        row.getCell(6).controls.links.get(ActionConstants.CHANGE).click();

                        softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.REQUIRED_PARTICIPATION_PCT))
                                .hasValue(new BigDecimal(responseFromLog.get(String.format("planCalculations[%s].manualClaimDetail.requiredParticipationPct", plansIndex))).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_EVEN).toString());
                        softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.MINIMUM_NO_OF_PARTICIPANTS))
                                .hasValue(responseFromLog.get(String.format("planCalculations[%s].manualClaimDetail.minNumberOfParticipants", plansIndex)));
                        softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.ASSUMED_PARTICIPATION_PCT))
                                .hasValue(new BigDecimal(responseFromLog.get(String.format("planCalculations[%s].manualClaimDetail.assumedParticipationPct", plansIndex))).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_EVEN).toString());
                    });

            LOGGER.info(String.format("TEST: %s: Step 9", testCaseId));
            classificationManagementMpTab.navigate();
            finalPlans.forEach(
                    plan -> {
                        Row row = coveragesTable.getRow(PLAN.getName(), plan);
                        String plansIndex = getFirstMatchedIndexes(ImmutableList.of("plans", "planName"), plansMapping.get(plan), requestFromLog)[0];
                        row.getCell(PLAN.getName()).click();
                        checkNumberOfParticipantsClassificationManagement(tdQuote, testCaseId, plan, finalTotalNumberParticipants);
                        finalCoverageTiers.forEach(
                                coverage -> {
                                    String coverageIndex = getFirstMatchedIndexes(ImmutableList.of("planCalculations", "manualClaimDetail.enrollmentBreakdown", "tier"), coveragesMapping.get(coverage), responseFromLog)[1];
                                    softly.assertThat(planTierAndRatingInfoTable.getRow(ImmutableMap.of(COVERAGE_TIER.getName(), coverage)).getCell(NUMBER_OF_PARTICIPANTS.getName()))
                                            .hasValue(responseFromLog.get(String.format("planCalculations[%s].manualClaimDetail.enrollmentBreakdown[%s].numberOfLives", plansIndex, coverageIndex)));
                                    softly.assertThat(planTierAndRatingInfoTable.getRow(ImmutableMap.of(COVERAGE_TIER.getName(), coverage)).getCell(RATE.getName()))
                                            .hasValue(new Currency(responseFromLog.get(String.format("planCalculations[%s].tierRates[%s].proposedRate", plansIndex, coverageIndex)), RoundingMode.HALF_EVEN).toString());
                                });
                    });
        });

    }

    private Map<String, String> createExpectedRequestData(TestData tdRequest) {
        Map<String, String> result;
        Collection<String> list = tdRequest.getKeys();
        result = list.stream().collect(Collectors.toMap(Function.identity(), s -> tdRequest.getValue(String.valueOf(s)), (a, b) -> b, LinkedHashMap::new));
        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        result.put("effectiveDate", currentDate);
        result.put("requestDate", currentDate);
        return result;
    }

    private Map<String, String> getCoveragesMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put(EMPLOYEE_ONLY, "Employee only");
        mapping.put(EMPLOYEE_SPOUSE, EMPLOYEE_SPOUSE);
        mapping.put(EMPLOYEE_CHILD_REN, EMPLOYEE_CHILD_REN);
        mapping.put(EMPLOYEE_FAMILY, EMPLOYEE_FAMILY);
        mapping.put(EMPLOYEE_ONE, EMPLOYEE_ONE);
        mapping.put(COMPOSITE, COMPOSITE);
        return mapping;
    }

    private Map<String, String> getPlansMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put(PLAN_BASEPOS, "BASEPOS");
        mapping.put(PLAN_MAJEPOS, "MAJEPOS");
        mapping.put(PLAN_FLEX_PLUS, "FLEX");
        mapping.put(PLAN_ALACARTE, "ALACARTE");
        mapping.put(PLAN_TRIPLE_ADVANTAGE, "TRIPADV");
        mapping.put(PLAN_DHMO, "DHMO");
        mapping.put(PLAN_WA_ONE, "WA1");
        mapping.put(PLAN_ASO, "ASO");
        return mapping;
    }

    private String getASOPreCommissionPEPMFromDb(String quoteNumber) {
        String query = "SELECT * FROM (\n" +
                "select sum(T.estimatedParticipantCount)as Participants from PolicySummary S \n" +
                "inner join PolicyDetail D on S.policyDetail_id = D.id \n" +
                "inner join GroupCoverageDefinition G on D.id = G.POLICYDETAIL_ID\n" +
                "inner join ClassGrpCoverageRelationship C on G.id = C.groupCoverageDefinition_id\n" +
                "inner join ClassSubgroupRatingInfo R on C.id = R.classGrpCovRelationship_id\n" +
                "inner join TierRatingInfo T on R.id = T.classSubgroupRatingInfo_id\n" +
                "where S.policyNumber = '%s') A\t\n" +
                "CROSS JOIN\n" +
                "(select (select displayValue from LookupValue v where LOOKUPLIST_ID IN \n" +
                "(select id from LookupList where lookupName = 'ASOFeeBasis') and productCd = 'GB_DN' and code = p.asoRateBasis) asoRateBasis --,\n" +
                "from PolicySummary p \n" +
                "where p.policyNumber = '%s') B\n" +
                "CROSS JOIN\n" +
                "(select SUM(CASE r.rateType WHEN 'MANUAL' THEN r.rate ELSE 0 END) AS 'ASO pre-commission PEPM',\n" +
                "SUM(CASE r.rateType WHEN 'ORIGINAL'THEN r.rate ELSE 0 END) AS 'Underwritten ASO Fee',\n" +
                "SUM(CASE r.rateType WHEN 'ORIGINAL'THEN r.annualPremium ELSE 0 END) AS 'Underwritten Annual ASO Fee',\n" +
                "SUM(CASE r.rateType WHEN 'PROPOSED'THEN r.rate ELSE 0 END) AS 'Proposed ASO Fee',\n" +
                "SUM(CASE r.rateType WHEN 'PROPOSED'THEN r.annualPremium ELSE 0 END) AS 'Proposed Annual ASO Fee'\n" +
                "from RateDetail r\n" +
                "inner join PolicySummary p on p.id = r.masterPolicy_ID\n" +
                "and p.policyNumber = '%s') C";
        return DBService.get().getRow(String.format(query, quoteNumber, quoteNumber, quoteNumber)).get("ASO pre-commission PEPM");
    }

    private String getNumberOfParticipantsFromTD(TestData td) {
        return String.valueOf(td.getTestDataList("Plan Tier And Rating Info")
                .stream().mapToInt(tdPlanAndTierRatingInfo -> Integer.parseInt(tdPlanAndTierRatingInfo.getValue("Number of Participants"))).sum());
    }

    private void checkNumberOfParticipantsPremiumSummary(TestData tdQuote, String testCaseId, String plan, String finalTotalNumberParticipants, Row row) {
        assertSoftly(softly -> {
            if (!REN_43687_TC2.equals(testCaseId)) {
                softly.assertThat(row.getCell(PARTICIPANTS.getName())).as("Number of Participants isn't correct").hasValue(finalTotalNumberParticipants);
            }
            else { //Get number of participants for each plan from REN_43687_TC2 and check that the same number on UI
                switch (plan) {
                    case PLAN_BASEPOS: {
                        softly.assertThat(row.getCell(PARTICIPANTS.getName())).hasValue(getNumberOfParticipantsFromTD(tdQuote.getTestDataList(ClassificationManagementTab.class.getSimpleName()).get(0)));
                        break;
                    }
                    case PLAN_FLEX_PLUS: {
                        softly.assertThat(row.getCell(PARTICIPANTS.getName())).hasValue(getNumberOfParticipantsFromTD(tdQuote.getTestDataList(ClassificationManagementTab.class.getSimpleName()).get(1)));
                        break;
                    }
                    case PLAN_MAJEPOS: {
                        softly.assertThat(row.getCell(PARTICIPANTS.getName())).hasValue(getNumberOfParticipantsFromTD(tdQuote.getTestDataList(ClassificationManagementTab.class.getSimpleName()).get(2)));
                        break;
                    }
                    default:
                        throw new IstfException(String.format("Plan name isn't correct. Can't get number of participants for test %s", testCaseId));
                }
            }
        });
    }

    private void checkNumberOfParticipantsClassificationManagement(TestData tdQuote, String testCaseId, String plan, String finalTotalNumberParticipants) {
        assertSoftly(softly -> {
            if (!REN_43687_TC2.equals(testCaseId)) {
                softly.assertThat(classificationGroupPlanRelationshipsTable.getRow(1).getCell(NUMBER_OF_PARTICIPANTS.getName())).hasValue(finalTotalNumberParticipants);
            }
            else { //Get number of participants for each plan from REN_43687_TC2 and check that the same number on UI
                switch (plan) {
                    case PLAN_BASEPOS: {
                        softly.assertThat(classificationGroupPlanRelationshipsTable.getRow(1).getCell(NUMBER_OF_PARTICIPANTS.getName())).hasValue(getNumberOfParticipantsFromTD(tdQuote.getTestDataList(ClassificationManagementTab.class.getSimpleName()).get(0)));
                        break;
                    }
                    case PLAN_FLEX_PLUS: {
                        softly.assertThat(classificationGroupPlanRelationshipsTable.getRow(1).getCell(NUMBER_OF_PARTICIPANTS.getName())).hasValue(getNumberOfParticipantsFromTD(tdQuote.getTestDataList(ClassificationManagementTab.class.getSimpleName()).get(1)));
                        break;
                    }
                    case PLAN_MAJEPOS: {
                        softly.assertThat(classificationGroupPlanRelationshipsTable.getRow(1).getCell(NUMBER_OF_PARTICIPANTS.getName())).hasValue(getNumberOfParticipantsFromTD(tdQuote.getTestDataList(ClassificationManagementTab.class.getSimpleName()).get(2)));
                        break;
                    }
                    default:
                        throw new IstfException(String.format("Plan name isn't correct. Can't get number of participants for test %s", testCaseId));
                }
            }
        });
    }
}
