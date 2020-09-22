package com.exigen.ren.modules.rating.gb_st;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.helpers.logging.RatingLogGrabber;
import com.exigen.ren.helpers.logging.RatingLogHolder;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants.ClassificationSubGroupsAndRatingColumns;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.ClassificationManagementTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PremiumSummaryTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.rating.RatingBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.PFL_NY;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.STAT_NJ;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.STAT_NY;
import static com.exigen.ren.main.enums.PolicyConstants.SubGroups.*;
import static com.exigen.ren.main.enums.TableConstants.*;
import static com.exigen.ren.main.enums.TableConstants.ClassificationSubGroupsAndRatingColumns.NUMBER_OF_PARTICIPANTS;
import static com.exigen.ren.main.enums.TableConstants.ClassificationSubGroupsAndRatingColumns.TOTAL_VOLUME;
import static com.exigen.ren.main.enums.TableConstants.Plans.COVERAGE_NAME;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryCoveragesTable.*;
import static com.exigen.ren.main.enums.TableConstants.RateDetailsTable.*;
import static com.exigen.ren.main.enums.ValueConstants.*;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs.ViewRateDetailsDialog.tableRateDetails;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.ClassificationManagementTabMetaData.RATE;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab.tableClassificationGroupCoverageRelationships;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab.tablePlansAndCoverages;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PlanDefinitionTab.tableCoverageDefinition;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PremiumSummaryTab.openViewRateDetailsByCoverageName;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PremiumSummaryTab.premiumSummaryCoveragesTable;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.RATING_INTEGRATION;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestRatingIntegrationST extends RatingBaseTest implements StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-43705"}, component = POLICY_GROUPBENEFITS)
    public void testRatingIntegrationPlanNYStatWithCoverageStatNY() {
        TestData tdQuote = tdSpecific().getTestData("TestData_NY");
        TestData tdRequest = tdSpecific().getTestData("RequestNY");
        TestData tdResponse = tdSpecific().getTestData("ResponseNY");

        LOGGER.info("TEST: Step: Quote creation and grab rating log steps");
        String quoteNumber = initiateAndRateQuote(tdQuote);
        tdRequest.adjust("policyID", quoteNumber);

        RatingLogHolder ratingLogHolder = new RatingLogGrabber().grabRatingLog(quoteNumber);
        LOGGER.info(String.format("Request from rating log:\n %s", ratingLogHolder.getRequestLog().getFormattedLogContent()));
        LOGGER.info(String.format("Response from rating log:\n %s", ratingLogHolder.getResponseLog().getFormattedLogContent()));
        Map<String, String> requestFromLog = ratingLogHolder.getRequestLog().getOpenLFieldsMap();
        Map<String, String> responseFromLog = ratingLogHolder.getResponseLog().getOpenLFieldsMap();

        LOGGER.info("TEST: Step: RQ/RS verification steps");
        verifyAttributesFromRQAndRS(requestFromLog, responseFromLog, tdRequest, tdResponse, true);

        assertSoftly(softly -> {
            LOGGER.info("TEST: Verification expected response premium values that were provided by OpenL Team (hardcoded)");
            softly.assertThat(responseFromLog).containsAllEntriesOf(getCheckRatingPremiumData());
            //check 'nature' value
            softly.assertThat(PremiumSummaryTab.txtRatingFormula).hasValue(responseFromLog.get("nature"));

            LOGGER.info("TEST: Step: Verification UI values on 'Premium Summary' tab");
            String numberOfParticipantsFromCensus = "9"; // known number of participans from Census file 'REN_Rating_Census_File_ST.xlsx'
            String totalVolumeFromRS = new Currency(responseFromLog.get("planCalcs[0].estimatedVolume"), RoundingMode.HALF_UP).toString();
            String rateFromRS = responseFromLog.get("planCalcs[0].premiumRate");
            Row rowPremiumSummaryCoverageTable = premiumSummaryCoveragesTable.getRow(1);
            softly.assertThat(rowPremiumSummaryCoverageTable.getCell(PARTICIPANTS.getName())).hasValue(numberOfParticipantsFromCensus);
            softly.assertThat(rowPremiumSummaryCoverageTable.getCell(VOLUME.getName())).hasValue(totalVolumeFromRS);
            softly.assertThat(rowPremiumSummaryCoverageTable.getCell(MANUAL_RATE.getName())).hasValue(new BigDecimal(responseFromLog.get("planCalcs[0].manualRate")).setScale(8, RoundingMode.HALF_EVEN).toString());
            softly.assertThat(rowPremiumSummaryCoverageTable.getCell(EXPERIENCE_RATE.getName())).hasValue(new BigDecimal(responseFromLog.get("planCalcs[0].experienceCalc.experienceRate")).setScale(8, RoundingMode.HALF_EVEN).toString());
            softly.assertThat(rowPremiumSummaryCoverageTable.getCell(FORMULA_RATE.getName())).hasValue(new BigDecimal(rateFromRS).setScale(8, RoundingMode.HALF_EVEN).toString());

            // check View Rate Details
            openViewRateDetailsByCoverageName(STAT_NY);
            // check Male
            String[] subGroupMaleIndexes = getFirstMatchedIndexes(ImmutableList.of("planCalcs", "stepRates", "subGroupID"), MALE, responseFromLog);
            BigDecimal rateMaleFromResponse = new BigDecimal(responseFromLog.get(String.format("planCalcs[%s].stepRates[%s].rate", subGroupMaleIndexes[0], subGroupMaleIndexes[1])));
            softly.assertThat(tableRateDetails.getRow(SUB_GROUP_NAME.getName(), MALE).getCell(RateDetailsTable.RATE.getName())).hasValue(rateMaleFromResponse.setScale(5, RoundingMode.HALF_UP).toString());
            // check Female
            String[] subGroupFemaleIndexes = getFirstMatchedIndexes(ImmutableList.of("planCalcs", "stepRates", "subGroupID"), FEMALE, responseFromLog);
            BigDecimal rateFemaleFromResponse = new BigDecimal(responseFromLog.get(String.format("planCalcs[%s].stepRates[%s].rate", subGroupFemaleIndexes[0], subGroupFemaleIndexes[1])));
            softly.assertThat(tableRateDetails.getRow(SUB_GROUP_NAME.getName(), FEMALE).getCell(RateDetailsTable.RATE.getName())).hasValue(rateFemaleFromResponse.setScale(5, RoundingMode.HALF_UP).toString());
            // check Proprietor
            String[] subGroupProprietorIndexes = getFirstMatchedIndexes(ImmutableList.of("planCalcs", "stepRates", "subGroupID"), PROPRIETOR, responseFromLog);
            BigDecimal rateProprietorFromResponse = new BigDecimal(responseFromLog.get(String.format("planCalcs[%s].stepRates[%s].rate", subGroupProprietorIndexes[0], subGroupProprietorIndexes[1])));
            softly.assertThat(tableRateDetails.getRow(SUB_GROUP_NAME.getName(), PROPRIETOR).getCell(RateDetailsTable.RATE.getName())).hasValue(rateProprietorFromResponse.setScale(5, RoundingMode.HALF_UP).toString());
            RateDialogs.ViewRateDetailsDialog.close();

            LOGGER.info("TEST: Step: Verification UI values on 'Classification Managemant' tab");
            classificationManagementMpTab.navigateToTab();
            Row rowClassificationGroupCoverageRelationshipsTable = tableClassificationGroupCoverageRelationships.getRow(1);
            softly.assertThat(rowClassificationGroupCoverageRelationshipsTable.getCell(NUMBER_OF_PARTICIPANTS.getName())).hasValue(numberOfParticipantsFromCensus);
            softly.assertThat(rowClassificationGroupCoverageRelationshipsTable.getCell(TOTAL_VOLUME.getName())).hasValue(totalVolumeFromRS);

            // check Male
            Row rowMale = tableClassificationSubGroupsAndRatingInfo.getRow(ClassificationSubGroupsAndRatingColumns.CLASSIFICATION_SUB_GROUP_NAME.getName(), MALE);
            String totalVolumeMaleFromResponse = new Currency(responseFromLog.get(String.format("planCalcs[%s].stepRates[%s].volume", subGroupMaleIndexes[0], subGroupMaleIndexes[1])), RoundingMode.HALF_UP).toString();
            softly.assertThat(rowMale.getCell(ClassificationSubGroupsAndRatingColumns.RATE.getName())).hasValue(rateMaleFromResponse.setScale(8, RoundingMode.HALF_UP).toString());
            softly.assertThat(rowMale.getCell(TOTAL_VOLUME.getName())).hasValue(totalVolumeMaleFromResponse);
            rowMale.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(RATE)).hasValue(rateMaleFromResponse.setScale(8, RoundingMode.HALF_UP).toString());
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.TOTAL_VOLUME)).hasValue(totalVolumeMaleFromResponse);

            // check Female
            Row rowFemale = tableClassificationSubGroupsAndRatingInfo.getRow(ClassificationSubGroupsAndRatingColumns.CLASSIFICATION_SUB_GROUP_NAME.getName(), FEMALE);
            String totalVolumeFemaleFromResponse = new Currency(responseFromLog.get(String.format("planCalcs[%s].stepRates[%s].volume", subGroupFemaleIndexes[0], subGroupFemaleIndexes[1])), RoundingMode.HALF_UP).toString();
            softly.assertThat(rowFemale.getCell(ClassificationSubGroupsAndRatingColumns.RATE.getName())).hasValue(rateFemaleFromResponse.setScale(8, RoundingMode.HALF_UP).toString());
            softly.assertThat(rowFemale.getCell(TOTAL_VOLUME.getName())).hasValue(totalVolumeFemaleFromResponse);
            rowFemale.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(RATE)).hasValue(rateFemaleFromResponse.setScale(8, RoundingMode.HALF_UP).toString());
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.TOTAL_VOLUME)).hasValue(totalVolumeFemaleFromResponse);
            // check Proprietor
            Row rowProprietor = tableClassificationSubGroupsAndRatingInfo.getRow(ClassificationSubGroupsAndRatingColumns.CLASSIFICATION_SUB_GROUP_NAME.getName(), PROPRIETOR);
            String totalVolumeeProprietorFromResponse = new Currency(responseFromLog.get(String.format("planCalcs[%s].stepRates[%s].volume", subGroupProprietorIndexes[0], subGroupProprietorIndexes[1])), RoundingMode.HALF_UP).toString();
            softly.assertThat(rowProprietor.getCell(ClassificationSubGroupsAndRatingColumns.RATE.getName())).hasValue(rateProprietorFromResponse.setScale(8, RoundingMode.HALF_UP).toString());
            rowProprietor.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(RATE)).hasValue(rateProprietorFromResponse.setScale(8, RoundingMode.HALF_UP).toString());
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.TOTAL_VOLUME)).hasValue(totalVolumeeProprietorFromResponse);

            LOGGER.info("TEST: Step: Verification UI values on 'Plan Definition' tab");
            planDefinitionTab.navigateToTab();
            String annualTaxableWagesfromDb = new Currency(getAnnualTaxableWagesFromDb(quoteNumber)).toString();
            String annualTaxableWagesfromUI = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.ANNUAL_COVERED_PAYROLL).getValue();
            String annualTaxableWagesfromRS = new Currency(responseFromLog.get("planCalcs[0].annualVolume"), RoundingMode.HALF_UP).toString();
            softly.assertThat(annualTaxableWagesfromDb).isEqualTo(annualTaxableWagesfromUI).isEqualTo(annualTaxableWagesfromRS);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PREMIUM_AND_RATE_HISTORY).getAsset(PlanDefinitionTabMetaData.PremiumAndRateHistoryMetaData.COMPOSITE_RATE))
                    .hasValue(new Currency(responseFromLog.get("planCalcs[0].experienceCalc.historyPeriods[0].rateHistory"), RoundingMode.HALF_UP).toString());
        });
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-43705"}, component = POLICY_GROUPBENEFITS)
    public void testRatingIntegrationPlanNJ() {
        TestData tdQuote = tdSpecific().getTestData("TestData_NJ");
        TestData tdRequest = tdSpecific().getTestData("RequestNJ");
        TestData tdResponse = tdSpecific().getTestData("ResponseNJ");

        LOGGER.info("TEST: Step: Quote creation and grab rating log steps");
        String quoteNumber = initiateAndRateQuote(tdQuote);
        tdRequest.adjust("policyID", quoteNumber);

        RatingLogHolder ratingLogHolder = new RatingLogGrabber().grabRatingLog(quoteNumber);
        LOGGER.info(String.format("Request from rating log:\n %s", ratingLogHolder.getRequestLog().getFormattedLogContent()));
        LOGGER.info(String.format("Response from rating log:\n %s", ratingLogHolder.getResponseLog().getFormattedLogContent()));
        Map<String, String> requestFromLog = ratingLogHolder.getRequestLog().getOpenLFieldsMap();
        Map<String, String> responseFromLog = ratingLogHolder.getResponseLog().getOpenLFieldsMap();

        LOGGER.info("TEST: Step: RQ/RS verification steps");
        verifyAttributesFromRQAndRS(requestFromLog, responseFromLog, tdRequest, tdResponse, true);

        assertSoftly(softly -> {
            LOGGER.info("TEST: Step: Verification UI values on 'Premium Summary' tab");
            String numberOfParticipantsFromCensus = "5"; // known number of participans from Census file 'REN_Rating_Census_File_ST.xlsx'
            String rateFromRS = responseFromLog.get("planCalcs[0].premiumRate");
            Row rowPremiumSummaryCoverageTable = premiumSummaryCoveragesTable.getRow(1);
            softly.assertThat(rowPremiumSummaryCoverageTable.getCell(PARTICIPANTS.getName())).hasValue(numberOfParticipantsFromCensus);
            softly.assertThat(rowPremiumSummaryCoverageTable.getCell(MANUAL_RATE.getName())).hasValue(new BigDecimal(responseFromLog.get("planCalcs[0].manualRate")).setScale(8, RoundingMode.HALF_EVEN).toString());
            softly.assertThat(rowPremiumSummaryCoverageTable.getCell(EXPERIENCE_RATE.getName())).hasValue(new BigDecimal(responseFromLog.get("planCalcs[0].experienceCalc.experienceRate")).setScale(8, RoundingMode.HALF_EVEN).toString());
            softly.assertThat(rowPremiumSummaryCoverageTable.getCell(FORMULA_RATE.getName())).hasValue(new BigDecimal(rateFromRS).setScale(8, RoundingMode.HALF_EVEN).toString());

            openViewRateDetailsByCoverageName(STAT_NJ);
            softly.assertThat(tableRateDetails.getRow(1).getCell(RateDetailsTable.RATE.getName())).hasValue(new BigDecimal(responseFromLog.get("planCalcs[0].tierRates[0].rate")).setScale(5, RoundingMode.HALF_EVEN).toString());
            softly.assertThat(tableRateDetails.getRow(2).getCell(RateDetailsTable.RATE.getName())).hasValue(new BigDecimal(responseFromLog.get("planCalcs[0].tierRates[1].rate")).setScale(5, RoundingMode.HALF_EVEN).toString());
            RateDialogs.ViewRateDetailsDialog.close();

            LOGGER.info("TEST: Step: Verification UI values on 'Classification Managemant' tab");
            classificationManagementMpTab.navigateToTab();
            Row rowClassificationGroupCoverageRelationshipsTable = tableClassificationGroupCoverageRelationships.getRow(1);
            softly.assertThat(rowClassificationGroupCoverageRelationshipsTable.getCell(CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName())).hasValue(numberOfParticipantsFromCensus);
            softly.assertThat(rowClassificationGroupCoverageRelationshipsTable.getCell(ClassificationSubGroupsAndRatingColumns.RATE.getName())).hasValue(new BigDecimal(rateFromRS).setScale(8, RoundingMode.HALF_EVEN).toString());
            ClassificationManagementTab.tablePlanTierAndRatingInfo.getRows().forEach(
                    coverageTier -> {
                        coverageTier.getCell(5).controls.links.get(ActionConstants.CHANGE).click();
                        softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.PLAN_TIER_AND_RATING_INFO).getAsset(ClassificationManagementTabMetaData.PlanTierAndRatingInfoMetaData.NUMBER_OF_PARTICIPANTS))
                                .hasValue(numberOfParticipantsFromCensus);
                        softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.PLAN_TIER_AND_RATING_INFO).getAsset(ClassificationManagementTabMetaData.PlanTierAndRatingInfoMetaData.VOLUME))
                                .hasValue(new Currency(responseFromLog.get(String.format("planCalcs[0].tierRates[%s].estimatedVolume", coverageTier.getIndex() - 1)), RoundingMode.HALF_UP).toString());
                        softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.PLAN_TIER_AND_RATING_INFO).getAsset(ClassificationManagementTabMetaData.PlanTierAndRatingInfoMetaData.RATE))
                                .hasValue(new BigDecimal(responseFromLog.get(String.format("planCalcs[0].tierRates[%s].rate", coverageTier.getIndex() - 1))).setScale(8, RoundingMode.HALF_EVEN).toString());
                    });

            LOGGER.info("TEST: Step: Verification UI values on 'Plan Definition' tab");
            planDefinitionTab.navigateToTab();
            String annualTaxableWagesfromDb = new Currency(getAnnualTaxableWagesFromDb(quoteNumber)).toString();
            String annualTaxableWagesfromUI = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.TOTAL_TAXABLE_WAGE).getValue();
            String annualTaxableWagesfromRS = new Currency(responseFromLog.get("planCalcs[0].estimatedVolume"), RoundingMode.HALF_EVEN).toString();
            softly.assertThat(annualTaxableWagesfromDb).isEqualTo(annualTaxableWagesfromUI).isEqualTo(annualTaxableWagesfromRS);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PREMIUM_AND_RATE_HISTORY).getAsset(PlanDefinitionTabMetaData.PremiumAndRateHistoryMetaData.COMPOSITE_RATE))
                    .hasValue(new Currency(responseFromLog.get("planCalcs[0].experienceCalc.historyPeriods[0].rateHistory"), RoundingMode.HALF_EVEN).toString());
        });
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-43705"}, component = POLICY_GROUPBENEFITS)
    public void testRatingIntegrationPlanNYStatWithCoveragePflNY() {
        TestData tdQuote = tdSpecific().getTestData("TestData_NYPFL");
        TestData tdRequest = tdSpecific().getTestData("RequestNYPFL");
        TestData tdResponse = tdSpecific().getTestData("ResponseNYPFL");

        LOGGER.info("TEST: Step: Quote creation and grab rating log steps");
        String quoteNumber = initiateAndRateQuote(tdQuote);
        tdRequest.adjust("policyID", quoteNumber);

        RatingLogHolder ratingLogHolder = new RatingLogGrabber().grabRatingLog(quoteNumber);
        LOGGER.info(String.format("Request from rating log:\n %s", ratingLogHolder.getRequestLog().getFormattedLogContent()));
        LOGGER.info(String.format("Response from rating log:\n %s", ratingLogHolder.getResponseLog().getFormattedLogContent()));
        Map<String, String> requestFromLog = ratingLogHolder.getRequestLog().getOpenLFieldsMap();
        Map<String, String> responseFromLog = ratingLogHolder.getResponseLog().getOpenLFieldsMap();

        LOGGER.info("TEST: Step: RQ/RS verification steps");
        verifyAttributesFromRQAndRS(requestFromLog, responseFromLog, tdRequest, tdResponse, false);
        //Verification 'manualRate', 'experienceCalc', 'premiumRate' RS attributes for case 'Stat NY' + 'PFL NY' + 'Under 50 Lives? = Yes' + 'Use Expirience Rating = No'
        String coverageNYStatIndex = getFirstMatchedIndexes(ImmutableList.of("planCalcs", "coverageType"), "New York DBL", responseFromLog)[0];
        String coverageNYPFLIndex = getFirstMatchedIndexes(ImmutableList.of("planCalcs", "coverageType"), "New York PFL", responseFromLog)[0];
        //check that null is returned by the Rater 'planCalcs[].manualRate' 1) Stat NY: 'Under 50 Lives?' = Yes 2) Always for PFL NY"
        assertThat("null").isEqualTo(responseFromLog.get(String.format("planCalcs[%s].manualRate", coverageNYStatIndex)));
        assertThat("null").isEqualTo(responseFromLog.get(String.format("planCalcs[%s].manualRate", coverageNYPFLIndex)));
        //check that null is returned by the Rater 'planCalcs[].experienceCalc' 1) Stat NY: 'Use Expirience Rating' = No 2) Always for PFL NY"
        assertThat("null").isEqualTo(responseFromLog.get(String.format("planCalcs[%s].experienceCalc", coverageNYStatIndex)));
        assertThat("null").isEqualTo(responseFromLog.get(String.format("planCalcs[%s].experienceCalc", coverageNYPFLIndex)));
        //check that null is returned by the Rater 'planCalcs[].premiumRate' 1) Stat NY: 'Under 50 Lives?' = Yes
        assertThat("null").isEqualTo(responseFromLog.get(String.format("planCalcs[%s].premiumRate", coverageNYStatIndex)));

        assertSoftly(softly -> {
            LOGGER.info("TEST: Step: Verification UI values on 'Premium Summary' tab");
            //Verification Stat NY
            // get expected number of participants for Stat NY from rating response
            String expectedNumberOfParticipantsStatNY = requestFromLog.get(String.format("plans[%s].totalEnrolled", coverageNYStatIndex));

            // check Coverage table
            Row rowCoverageStatNY = premiumSummaryCoveragesTable.getRow(PremiumSummaryCoveragesTable.COVERAGE_NAME.getName(), STAT_NY);
            softly.assertThat(rowCoverageStatNY.getCell(PARTICIPANTS.getName())).hasValue(expectedNumberOfParticipantsStatNY);
            softly.assertThat(rowCoverageStatNY.getCell(MANUAL_RATE.getName())).hasValue(EMPTY);
            softly.assertThat(rowCoverageStatNY.getCell(EXPERIENCE_RATE.getName())).hasValue(EMPTY);
            softly.assertThat(rowCoverageStatNY.getCell(FORMULA_RATE.getName())).hasValue(EMPTY);

            // check View Rate Details
            openViewRateDetailsByCoverageName(STAT_NY);
            // check Male
            String[] subGroupMaleIndexes = getRatingLogIndexes(ImmutableList.of("planCalcs", "stepRates", "subGroupID"), MALE, responseFromLog).get(Integer.parseInt(coverageNYStatIndex));
            String rateMaleStatNYFromResponse = new Currency(responseFromLog.get(String.format("planCalcs[%s].stepRates[%s].rate", subGroupMaleIndexes[0], subGroupMaleIndexes[1])), RoundingMode.HALF_UP).toString();
            softly.assertThat(tableRateDetails.getRow(SUB_GROUP_NAME.getName(), MALE).getCell(RateDetailsTable.RATE.getName())).hasValue(rateMaleStatNYFromResponse);
            // check Female
            String[] subGroupFemaleIndexes = getRatingLogIndexes(ImmutableList.of("planCalcs", "stepRates", "subGroupID"), FEMALE, responseFromLog).get(Integer.parseInt(coverageNYStatIndex));
            String rateFemaleStatNYFromResponse = new Currency(responseFromLog.get(String.format("planCalcs[%s].stepRates[%s].rate", subGroupFemaleIndexes[0], subGroupFemaleIndexes[1])), RoundingMode.HALF_UP).toString();
            softly.assertThat(tableRateDetails.getRow(SUB_GROUP_NAME.getName(), FEMALE).getCell(RateDetailsTable.RATE.getName())).hasValue(rateFemaleStatNYFromResponse);
            // check Proprietor
            String[] subGroupProprietorIndexes = getRatingLogIndexes(ImmutableList.of("planCalcs", "stepRates", "subGroupID"), PROPRIETOR, responseFromLog).get(Integer.parseInt(coverageNYStatIndex));
            String rateProprietorStatNYFromResponse = new Currency(responseFromLog.get(String.format("planCalcs[%s].stepRates[%s].rate", subGroupProprietorIndexes[0], subGroupProprietorIndexes[1])), RoundingMode.HALF_UP).toString();
            softly.assertThat(tableRateDetails.getRow(SUB_GROUP_NAME.getName(), PROPRIETOR).getCell(RateDetailsTable.RATE.getName())).hasValue(rateProprietorStatNYFromResponse);
            RateDialogs.ViewRateDetailsDialog.close();

            //Verification PFL NY
            // get expected number of participants for PFL NY from rating response
            String expectedNumberOfParticipantsPFLNY = requestFromLog.get(String.format("plans[%s].totalEnrolled", coverageNYPFLIndex));

            // check Coverage table
            Row rowCoveragePFLNY = premiumSummaryCoveragesTable.getRow(PremiumSummaryCoveragesTable.COVERAGE_NAME.getName(), PFL_NY);
            softly.assertThat(rowCoveragePFLNY.getCell(PARTICIPANTS.getName())).hasValue(expectedNumberOfParticipantsPFLNY);
            softly.assertThat(rowCoveragePFLNY.getCell(MANUAL_RATE.getName())).hasValue(EMPTY);
            softly.assertThat(rowCoveragePFLNY.getCell(EXPERIENCE_RATE.getName())).hasValue(EMPTY);
            softly.assertThat(rowCoveragePFLNY.getCell(FORMULA_RATE.getName())).hasValue(EMPTY);

            // check View Rate Details
            openViewRateDetailsByCoverageName(PFL_NY);
            // check Male
            String[] subGroupMaleIndexesPFL = getRatingLogIndexes(ImmutableList.of("planCalcs", "stepRates", "subGroupID"), MALE, responseFromLog).get(Integer.parseInt(coverageNYPFLIndex));
            BigDecimal rateMalePFLNYFromResponse = new BigDecimal(responseFromLog.get(String.format("planCalcs[%s].stepRates[%s].rate", subGroupMaleIndexesPFL[0], subGroupMaleIndexesPFL[1])));
            softly.assertThat(tableRateDetails.getRow(SUB_GROUP_NAME.getName(), MALE).getCell(RateDetailsTable.RATE.getName())).hasValue(rateMalePFLNYFromResponse.setScale(5, RoundingMode.HALF_UP).toString());
            // check Female
            String[] subGroupFemaleIndexesPFL = getRatingLogIndexes(ImmutableList.of("planCalcs", "stepRates", "subGroupID"), FEMALE, responseFromLog).get(Integer.parseInt(coverageNYPFLIndex));
            BigDecimal rateFemalePFLNYFromResponse = new BigDecimal(responseFromLog.get(String.format("planCalcs[%s].stepRates[%s].rate", subGroupFemaleIndexesPFL[0], subGroupFemaleIndexesPFL[1])));
            softly.assertThat(tableRateDetails.getRow(SUB_GROUP_NAME.getName(), FEMALE).getCell(RateDetailsTable.RATE.getName())).hasValue(rateFemalePFLNYFromResponse.setScale(5, RoundingMode.HALF_UP).toString());
            // check Other/Not Specified
            String[] subGroupOtherNotSpecifiedIndexesPFL = getFirstMatchedIndexes(ImmutableList.of("planCalcs", "stepRates", "subGroupID"), OTHER_NOT_SPECIFIED, responseFromLog);
            BigDecimal rateOtherNotSpecifiedPFLNYromResponse = new BigDecimal(responseFromLog.get(String.format("planCalcs[%s].stepRates[%s].rate", subGroupOtherNotSpecifiedIndexesPFL[0], subGroupOtherNotSpecifiedIndexesPFL[1])));
            softly.assertThat(tableRateDetails.getRow(SUB_GROUP_NAME.getName(), OTHER_NOT_SPECIFIED).getCell(RateDetailsTable.RATE.getName())).hasValue(rateOtherNotSpecifiedPFLNYromResponse.setScale(5, RoundingMode.HALF_UP).toString());
            RateDialogs.ViewRateDetailsDialog.close();

            //Verification common values
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(PremiumSummaryTabMetaData.CREDIBILITY_FACTOR))
                    .hasValue(new BigDecimal(requestFromLog.get("credibilityOverride")).setScale(1, RoundingMode.HALF_EVEN).toString());

            LOGGER.info("TEST: Step: Verification UI values on 'Classification Management' tab");
            classificationManagementMpTab.navigateToTab();
            //Verification Stat NY
            tablePlansAndCoverages.getRow(COVERAGE_NAME.getName(), STAT_NY).getCell(COVERAGE_NAME.getName()).click();
            softly.assertThat(tableClassificationGroupCoverageRelationships.getRow(1).getCell(CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName()))
                    .hasValue(expectedNumberOfParticipantsStatNY);

            // check Male
            Row rowMaleStatNY = tableClassificationSubGroupsAndRatingInfo.getRow(ClassificationSubGroupsAndRatingColumns.CLASSIFICATION_SUB_GROUP_NAME.getName(), MALE);
            softly.assertThat(rowMaleStatNY.getCell(ClassificationSubGroupsAndRatingColumns.RATE.getName())).hasValue(rateMaleStatNYFromResponse);
            rowMaleStatNY.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(RATE)).hasValue(rateMaleStatNYFromResponse);

            // check Female
            Row rowFemaleStatNY = tableClassificationSubGroupsAndRatingInfo.getRow(ClassificationSubGroupsAndRatingColumns.CLASSIFICATION_SUB_GROUP_NAME.getName(), FEMALE);
            softly.assertThat(rowFemaleStatNY.getCell(ClassificationSubGroupsAndRatingColumns.RATE.getName())).hasValue(rateFemaleStatNYFromResponse);
            rowFemaleStatNY.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(RATE)).hasValue(rateFemaleStatNYFromResponse);

            // check Proprietor
            Row rowProprietorStatNY = tableClassificationSubGroupsAndRatingInfo.getRow(ClassificationSubGroupsAndRatingColumns.CLASSIFICATION_SUB_GROUP_NAME.getName(), PROPRIETOR);
            softly.assertThat(rowProprietorStatNY.getCell(ClassificationSubGroupsAndRatingColumns.RATE.getName())).hasValue(rateProprietorStatNYFromResponse);
            rowProprietorStatNY.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(RATE)).hasValue(rateProprietorStatNYFromResponse);

            //Verification PFL NY
            tablePlansAndCoverages.getRow(COVERAGE_NAME.getName(), PFL_NY).getCell(COVERAGE_NAME.getName()).click();
            softly.assertThat(tableClassificationGroupCoverageRelationships.getRow(1).getCell(CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName()))
                    .hasValue(expectedNumberOfParticipantsPFLNY);

            // check Male
            Row rowMalePFLNY = tableClassificationSubGroupsAndRatingInfo.getRow(ClassificationSubGroupsAndRatingColumns.CLASSIFICATION_SUB_GROUP_NAME.getName(), MALE);
            String totalVolumeMalePFLNYFromResponse = new Currency(responseFromLog.get(String.format("planCalcs[%s].stepRates[%s].volume", subGroupMaleIndexesPFL[0], subGroupMaleIndexesPFL[1])), RoundingMode.HALF_UP).toString();
            softly.assertThat(rowMalePFLNY.getCell(ClassificationSubGroupsAndRatingColumns.RATE.getName())).hasValue(rateMalePFLNYFromResponse.setScale(8, RoundingMode.HALF_UP).toString());
            softly.assertThat(rowMalePFLNY.getCell(TOTAL_VOLUME.getName())).hasValue(totalVolumeMalePFLNYFromResponse);
            rowMalePFLNY.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(RATE)).hasValue(rateMalePFLNYFromResponse.setScale(8, RoundingMode.HALF_UP).toString());
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.TOTAL_VOLUME)).hasValue(totalVolumeMalePFLNYFromResponse);

            // check Female
            Row rowFemalePFLNY = tableClassificationSubGroupsAndRatingInfo.getRow(ClassificationSubGroupsAndRatingColumns.CLASSIFICATION_SUB_GROUP_NAME.getName(), FEMALE);
            String totalVolumeFemalePFLNYFromResponse = new Currency(responseFromLog.get(String.format("planCalcs[%s].stepRates[%s].volume", subGroupFemaleIndexesPFL[0], subGroupFemaleIndexesPFL[1])), RoundingMode.HALF_UP).toString();
            softly.assertThat(rowFemalePFLNY.getCell(ClassificationSubGroupsAndRatingColumns.RATE.getName())).hasValue(rateFemalePFLNYFromResponse.setScale(8, RoundingMode.HALF_UP).toString());
            softly.assertThat(rowFemalePFLNY.getCell(TOTAL_VOLUME.getName())).hasValue(totalVolumeFemalePFLNYFromResponse);
            rowFemalePFLNY.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(RATE)).hasValue(rateFemalePFLNYFromResponse.setScale(8, RoundingMode.HALF_UP).toString());
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.TOTAL_VOLUME)).hasValue(totalVolumeFemalePFLNYFromResponse);

            // check Other/Not Specified
            Row rowOtherNotSpecifiedPFLNY = tableClassificationSubGroupsAndRatingInfo.getRow(ClassificationSubGroupsAndRatingColumns.CLASSIFICATION_SUB_GROUP_NAME.getName(), OTHER_NOT_SPECIFIED);
            String totalVolumeOtherNotSpecifiedPFLNYFromResponse = new Currency(responseFromLog.get(String.format("planCalcs[%s].stepRates[%s].volume", subGroupOtherNotSpecifiedIndexesPFL[0], subGroupOtherNotSpecifiedIndexesPFL[1])), RoundingMode.HALF_UP).toString();
            softly.assertThat(rowOtherNotSpecifiedPFLNY.getCell(ClassificationSubGroupsAndRatingColumns.RATE.getName())).hasValue(rateOtherNotSpecifiedPFLNYromResponse.setScale(8, RoundingMode.HALF_UP).toString());
            softly.assertThat(rowOtherNotSpecifiedPFLNY.getCell(TOTAL_VOLUME.getName())).hasValue(totalVolumeOtherNotSpecifiedPFLNYFromResponse);
            rowOtherNotSpecifiedPFLNY.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(RATE)).hasValue(rateOtherNotSpecifiedPFLNYromResponse.setScale(8, RoundingMode.HALF_UP).toString());
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.TOTAL_VOLUME)).hasValue(totalVolumeOtherNotSpecifiedPFLNYFromResponse);

            LOGGER.info("TEST: Step: Verification UI values on 'Plan Definition' tab");
            planDefinitionTab.navigateToTab();
            //Verification Stat NY
            tableCoverageDefinition.getRow(COVERAGE_NAME.getName(), STAT_NY).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.USE_EXPERIENCE_RATING)).hasValue("No");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.TOTAL_TAXABLE_WAGE)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.ANNUAL_COVERED_PAYROLL)).isAbsent();
            //Verification PFL NY
            tableCoverageDefinition.getRow(COVERAGE_NAME.getName(), PFL_NY).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.USE_EXPERIENCE_RATING)).hasValue("No");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.TOTAL_TAXABLE_WAGE)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.ANNUAL_COVERED_PAYROLL)).isAbsent();
        });
    }

    private String initiateAndRateQuote(TestData tdQuote) {
        TestData tdCaseProfile = tdSpecific().getTestData("CaseProfile");
        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData()
                .adjust(makeKeyPath(GeneralTab.class.getSimpleName(), AGENCY_ASSIGNMENT.getLabel() + "[0]", GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(),
                        GeneralTabMetaData.AddAgencyMetaData.AGENCY_CODE.getLabel()), "39B00"));
        caseProfile.create(tdCaseProfile);
        if (VALUE_YES.equals(tdQuote.getTestData(PolicyInformationTab.class.getSimpleName()).getValue(PolicyInformationTabMetaData.UNDER_FIFTY_LIVES.getLabel()))) {
            initiateSTQuoteAndFillUpToTab(tdQuote, ClassificationManagementTab.class, false);
            policyInformationTab.navigateToTab();
            //Set 'Under 50 Lives Workflow?' field to 'Yes' on 'Policy Information' tab. Possible just after select plan 'NY' on 'PlanDefinition' tab
            policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.UNDER_FIFTY_LIVES_WORKFLOW).setValue(VALUE_YES);
            classificationManagementMpTab.navigateToTab();
            statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(tdQuote, ClassificationManagementTab.class, PremiumSummaryTab.class, true);
        }
        else {
            initiateSTQuoteAndFillUpToTab(tdQuote, PremiumSummaryTab.class, true);
        }
        premiumSummaryTab.rate();
        assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED);
        return QuoteSummaryPage.getQuoteNumber();
    }

    private String getAnnualTaxableWagesFromDb(String quoteNumber) {
        String query = "select gcd.annualTaxableWages from PolicySummary ps join PolicyDetail pd on ps.policyDetail_id = pd.id \n" +
                "join GroupCoverageDefinition gcd on gcd.POLICYDETAIL_ID = pd.id where ps.policyNumber = '%s';";
        return DBService.get().getRow(String.format(query, quoteNumber)).get("annualTaxableWages");
    }

    private Map<String, String> createExpectedRequestData(TestData tdRequest, Map<String, String> requestFromLog, boolean isCensusFileApplied) {
        Map<String, String> result;
        Collection<String> list = tdRequest.getKeys();
        result = list.stream().collect(Collectors.toMap(Function.identity(), s -> tdRequest.getValue(String.valueOf(s)), (a, b) -> b, LinkedHashMap::new));
        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (isCensusFileApplied) {
            //add known data for employee with id=27  from Census file 'REN_Rating_Census_File_ST.xlsx' to expected RQ data
            String employeeID = "27";
            String[] plansIndexes = getFirstMatchedIndexes(ImmutableList.of("plans", "censusEmployees", "employeeID"), employeeID, requestFromLog);
            result.put(String.format("plans[%s].censusEmployees[%s].employeeID", plansIndexes[0], plansIndexes[1]), employeeID);
            result.put(String.format("plans[%s].censusEmployees[%s].classID", plansIndexes[0], plansIndexes[1]), "1");
            result.put(String.format("plans[%s].censusEmployees[%s].dob", plansIndexes[0], plansIndexes[1]), "1973-01-01");
            result.put(String.format("plans[%s].censusEmployees[%s].age", plansIndexes[0], plansIndexes[1]), "46");
            result.put(String.format("plans[%s].censusEmployees[%s].gender", plansIndexes[0], plansIndexes[1]), "Female");
            result.put(String.format("plans[%s].censusEmployees[%s].state", plansIndexes[0], plansIndexes[1]), "CA");
            result.put(String.format("plans[%s].censusEmployees[%s].zip", plansIndexes[0], plansIndexes[1]), "94203");
            result.put(String.format("plans[%s].censusEmployees[%s].salaryAmt", plansIndexes[0], plansIndexes[1]), "10914.00");
            result.put(String.format("plans[%s].censusEmployees[%s].salaryUnit", plansIndexes[0], plansIndexes[1]), "Salaried");
            result.put("plans[0].censusDate", currentDate);
        }
        //add effective and request date to expected RQ data
        result.put("effectiveDate", currentDate);
        result.put("requestDate", currentDate);

        return result;
    }

    private void verifyAttributesFromRQAndRS(Map<String, String> requestFromLog, Map<String, String> responseFromLog, TestData tdRequest, TestData tdResponse, boolean isCensusFileApplied) {
        Map<String, String> expectedRequest = createExpectedRequestData(tdRequest, requestFromLog, isCensusFileApplied);
        assertThat(requestFromLog).containsAllEntriesOf(expectedRequest);
        Collection<String> list = tdResponse.getKeys();
        assertThat(responseFromLog.keySet()).containsAll(list);
    }

}
