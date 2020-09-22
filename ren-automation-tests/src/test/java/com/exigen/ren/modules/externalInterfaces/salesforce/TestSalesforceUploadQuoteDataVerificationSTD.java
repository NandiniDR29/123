package com.exigen.ren.modules.externalInterfaces.salesforce;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PremiumSummaryTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.salesforce.model.SalesforceQuoteModel;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSTATAvailableBenefits.SHORT_TERM_DISABILITY;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.*;
import static com.exigen.ren.main.enums.SalesforceConstants.SF_STD;
import static com.exigen.ren.main.enums.SalesforceConstants.SF_STD_VOLUNTARY;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.ClassificationManagementTab.ClassificationSubGroupsAndRatingColumns.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.ClassificationManagementTab.tablePlansAndCoverages;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetaData.SIC_CODE;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.PRIOR_CARRIER_NAME;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PremiumSummaryTabMetaData.EXPERIENCE_RATING;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PlanDefinitionTab.openAddedPlan;
import static com.exigen.ren.utils.components.Components.INTEGRATION;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestSalesforceUploadQuoteDataVerificationSTD extends SalesforceBaseTest implements CustomerContext, ProfileContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {WITHOUT_TS, REGRESSION, INTEGRATION})
    @TestInfo(testCaseId = {"REN-20783"}, component = INTEGRATION)
    public void testSalesforceUploadQuoteDataVerification_STD() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(shortTermDisabilityMasterPolicy.getType()));
        String priorCarrierName = "Sun Life";

        shortTermDisabilityMasterPolicy.createQuote(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_AllPlans")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NY")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PRIOR_CARRIER_NAME.getLabel()), priorCarrierName)
                .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks()).resolveLinks());
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        String conContribution = PolicySummaryPage.tableCoverageSummary.getRowContains("Plan", CON).getCell("Contribution %").getValue().replace("00 %", "0");
        String ncContribution = PolicySummaryPage.tableCoverageSummary.getRowContains("Plan", NC).getCell("Contribution %").getValue().replace("00 %", "0");

        String quoteIdCoverageCON = String.format("%s_%s_%s", quoteNumber, CON, SF_STD);
        String quoteIdCoverageNC = String.format("%s_%s_%s", quoteNumber, NC, SF_STD);
        String quoteIdCoverageVOL = String.format("%s_%s_%s", quoteNumber, VOL, SF_STD_VOLUNTARY);
        String quoteIdCoverageSGR = String.format("%s_%s_%s", quoteNumber, SGR, SF_STD_VOLUNTARY);

        LOGGER.info("Steps#1, 2, 3 verification");
        ResponseContainer<SalesforceQuoteModel> responseQuoteCON = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getBenefitType() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageCON),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        assertSoftly(softly -> {
            softly.assertThat(responseQuoteCON.getModel().getBenefitType()).isEqualTo("Percent of Salary");
            softly.assertThat(responseQuoteCON.getModel().getSicCode()).isEqualTo(tdSpecific().getValue("PlanDefinitionTab_CON", RATING.getLabel(), SIC_CODE.getLabel()));
            softly.assertThat(responseQuoteCON.getModel().getBenefitMaximum()).isEqualTo("1000.0");
            softly.assertThat(responseQuoteCON.getModel().getEmployerContribution()).isEqualTo(conContribution);
            softly.assertThat(responseQuoteCON.getModel().getPercentOfSalary()).isEqualTo("Other");
            softly.assertThat(responseQuoteCON.getModel().getPercentDescriptionOther()).isEqualTo(tdSpecific().getValue("PlanDefinitionTab_CON", BENEFIT_SCHEDULE.getLabel(), BENEFIT_PERCENTAGE.getLabel()).substring(0, 2).concat(".0"));
            softly.assertThat(responseQuoteCON.getModel().getPolicyType()).isEqualTo("REN");
            softly.assertThat(responseQuoteCON.getModel().getQuoteName()).isEqualTo(String.format("%s_%s_%s", quoteNumber, SHORT_TERM_DISABILITY, CON));
            softly.assertThat(responseQuoteCON.getModel().getSitusStateRelated()).isEqualTo("NY");
            softly.assertThat(responseQuoteCON.getModel().getMaximumPaymentDuration()).isEqualTo(String.format("%s Weeks", tdSpecific().getValue("PlanDefinitionTab_CON", BENEFIT_SCHEDULE.getLabel(), MAXIMUM_PAYMENT_DURATION.getLabel())));
            softly.assertThat(responseQuoteCON.getModel().getEliminationPeriodInjury()).isEqualTo("1");
            softly.assertThat(responseQuoteCON.getModel().getEliminationPeriodSickness()).isEqualTo("4");
            softly.assertThat(responseQuoteCON.getModel().getPriorCarrierName()).isEqualTo(priorCarrierName);
        });

        ResponseContainer<SalesforceQuoteModel> responseQuoteNC = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getBenefitType() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageNC),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        assertSoftly(softly -> {
            softly.assertThat(responseQuoteNC.getModel().getBenefitType()).isEqualTo("Flat Benefit");
            softly.assertThat(responseQuoteNC.getModel().getSicCode()).isEqualTo(tdSpecific().getValue("PlanDefinitionTab_NC", RATING.getLabel(), SIC_CODE.getLabel()));
            softly.assertThat(responseQuoteNC.getModel().getBenefitMaximum()).isEqualTo(tdSpecific().getValue("PlanDefinitionTab_NC", BENEFIT_SCHEDULE.getLabel(), WEEKLY_BENEFIT_AMOUNT.getLabel()));
            softly.assertThat(responseQuoteNC.getModel().getEmployerContribution()).isEqualTo(ncContribution);
            softly.assertThat(responseQuoteNC.getModel().getPolicyType()).isEqualTo("REN");
            softly.assertThat(responseQuoteNC.getModel().getQuoteName()).isEqualTo(String.format("%s_%s_%s", quoteNumber, SHORT_TERM_DISABILITY, NC));
            softly.assertThat(responseQuoteNC.getModel().getSitusStateRelated()).isEqualTo("NY");
            softly.assertThat(responseQuoteNC.getModel().getMaximumPaymentDuration()).isEqualTo(String.format("%s Weeks", tdSpecific().getValue("PlanDefinitionTab_NC", BENEFIT_SCHEDULE.getLabel(), MAXIMUM_PAYMENT_DURATION.getLabel())));
            softly.assertThat(responseQuoteNC.getModel().getEliminationPeriodInjury()).isEqualTo("Other");
            softly.assertThat(responseQuoteNC.getModel().getEliminationPeriodSickness()).isEqualTo("Other");
            softly.assertThat(responseQuoteNC.getModel().getEliminationPeriodInjuryOther()).isEqualTo(tdSpecific().getValue("PlanDefinitionTab_NC", BENEFIT_SCHEDULE.getLabel(), ELIMINATION_PERIOD_INJURY.getLabel()));
            softly.assertThat(responseQuoteNC.getModel().getEliminationPeriodSicknessOther()).isEqualTo(tdSpecific().getValue("PlanDefinitionTab_NC", BENEFIT_SCHEDULE.getLabel(), ELIMINATION_PERIOD_SICKNESS.getLabel()));
            softly.assertThat(responseQuoteNC.getModel().getPriorCarrierName()).isEqualTo(priorCarrierName);
        });

        ResponseContainer<SalesforceQuoteModel> responseQuoteVOL = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getBenefitType() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageVOL),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        assertSoftly(softly -> {
            softly.assertThat(responseQuoteVOL.getModel().getBenefitType()).isEqualTo("Percent of Salary");
            softly.assertThat(responseQuoteVOL.getModel().getSicCode()).isEqualTo(tdSpecific().getValue("PlanDefinitionTab_VOL", RATING.getLabel(), SIC_CODE.getLabel()));
            softly.assertThat(responseQuoteVOL.getModel().getBenefitMaximum()).isEqualTo("1000.0");
            softly.assertThat(responseQuoteVOL.getModel().getPercentOfSalary()).isEqualTo("Other");
            softly.assertThat(responseQuoteVOL.getModel().getPercentDescriptionOther()).isEqualTo(tdSpecific().getValue("PlanDefinitionTab_VOL", BENEFIT_SCHEDULE.getLabel(), BENEFIT_PERCENTAGE.getLabel()).substring(0, 2).concat(".0"));
            softly.assertThat(responseQuoteVOL.getModel().getPolicyType()).isEqualTo("REN");
            softly.assertThat(responseQuoteVOL.getModel().getQuoteName()).isEqualTo(String.format("%s_%s_%s", quoteNumber, SHORT_TERM_DISABILITY, VOL));
            softly.assertThat(responseQuoteVOL.getModel().getSitusStateRelated()).isEqualTo("NY");
            softly.assertThat(responseQuoteVOL.getModel().getMaximumPaymentDuration()).isEqualTo(String.format("%s Weeks", tdSpecific().getValue("PlanDefinitionTab_VOL", BENEFIT_SCHEDULE.getLabel(), MAXIMUM_PAYMENT_DURATION.getLabel())));
            softly.assertThat(responseQuoteVOL.getModel().getEliminationPeriodInjury()).isEqualTo("4");
            softly.assertThat(responseQuoteVOL.getModel().getEliminationPeriodSickness()).isEqualTo("8");
            softly.assertThat(responseQuoteVOL.getModel().getPriorCarrierName()).isEqualTo(priorCarrierName);
        });

        ResponseContainer<SalesforceQuoteModel> responseQuoteSGR = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getBenefitType() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageSGR),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        assertSoftly(softly -> {
            softly.assertThat(responseQuoteSGR.getModel().getBenefitType()).isEqualTo("Flat Benefit");
            softly.assertThat(responseQuoteSGR.getModel().getSicCode()).isEqualTo(tdSpecific().getValue("PlanDefinitionTab_SGR", RATING.getLabel(), SIC_CODE.getLabel()));
            softly.assertThat(responseQuoteSGR.getModel().getBenefitMaximum()).isEqualTo(tdSpecific().getValue("PlanDefinitionTab_SGR", BENEFIT_SCHEDULE.getLabel(), WEEKLY_BENEFIT_AMOUNT.getLabel()));
            softly.assertThat(responseQuoteSGR.getModel().getPolicyType()).isEqualTo("REN");
            softly.assertThat(responseQuoteSGR.getModel().getQuoteName()).isEqualTo(String.format("%s_%s_%s", quoteNumber, SHORT_TERM_DISABILITY, SGR));
            softly.assertThat(responseQuoteSGR.getModel().getSitusStateRelated()).isEqualTo("NY");
            softly.assertThat(responseQuoteSGR.getModel().getMaximumPaymentDuration()).isEqualTo("Other");
            softly.assertThat(responseQuoteSGR.getModel().getMaximumPaymentDurationOther()).isEqualTo(String.format("%s Weeks", tdSpecific().getValue("PlanDefinitionTab_SGR", BENEFIT_SCHEDULE.getLabel(), MAXIMUM_PAYMENT_DURATION.getLabel())));
            softly.assertThat(responseQuoteSGR.getModel().getEliminationPeriodInjury()).isEqualTo(tdSpecific().getValue("PlanDefinitionTab_SGR", BENEFIT_SCHEDULE.getLabel(), ELIMINATION_PERIOD_INJURY.getLabel()));
            softly.assertThat(responseQuoteSGR.getModel().getEliminationPeriodSickness()).isEqualTo(tdSpecific().getValue("PlanDefinitionTab_SGR", BENEFIT_SCHEDULE.getLabel(), ELIMINATION_PERIOD_SICKNESS.getLabel()));
            softly.assertThat(responseQuoteNC.getModel().getPriorCarrierName()).isEqualTo(priorCarrierName);
        });

        LOGGER.info("Steps#4, 5, 6, 7 execution");
        shortTermDisabilityMasterPolicy.dataGather().start();
        planDefinitionTab.navigateToTab();

        openAddedPlan(CON);
        planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("9");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(BENEFIT_PERCENTAGE).setValue("70%");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(MAXIMUM_PAYMENT_DURATION).setValue("11");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(ELIMINATION_PERIOD_INJURY).setValue("14");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(ELIMINATION_PERIOD_SICKNESS).setValue("14");

        openAddedPlan(NC);
        planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("11");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(WEEKLY_BENEFIT_AMOUNT).setValue("750");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(MAXIMUM_PAYMENT_DURATION).setValue("26");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(ELIMINATION_PERIOD_INJURY).setValue("30");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(ELIMINATION_PERIOD_SICKNESS).setValue("30");

        openAddedPlan(VOL);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(BENEFIT_PERCENTAGE).setValue("50%");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(MAXIMUM_PAYMENT_DURATION).setValue("10");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(ELIMINATION_PERIOD_INJURY).setValue("7");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(ELIMINATION_PERIOD_SICKNESS).setValue("7");

        openAddedPlan(SGR);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(WEEKLY_BENEFIT_AMOUNT).setValue("250");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(MAXIMUM_PAYMENT_DURATION).setValue("25");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(ELIMINATION_PERIOD_INJURY).setValue("3");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(ELIMINATION_PERIOD_SICKNESS).setValue("3");
        premiumSummaryTab.navigateToTab();

        premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(PremiumSummaryTabMetaData.ExperienceRatingMetaData.CREDIBILITY_FACTOR).setValue("0.01");
        premiumSummaryTab.rate();

        classificationManagementMpTab.navigateToTab();
        Row rowFor20_24 = tableClassificationSubGroupsAndRatingInfo.getRowContains(CLASSIFICATION_SUB_GROUP_NAME.getName(), "0-24");

        tablePlansAndCoverages.getRowContains(TableConstants.PlansAndCoverages.PLAN.getName(), "CON-CON").getCell(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName()).click();
        String participantsNumberCON = rowFor20_24.getCell(NUMBER_OF_PARTICIPANTS.getName()).getValue().concat(".0");
        String totalVolumeCON = new Currency(rowFor20_24.getCell(TOTAL_VOLUME.getName()).getValue()).toPlainString();
        String rateCON = rowFor20_24.getCell(RATE.getName()).getValue().substring(0, 3);

        tablePlansAndCoverages.getRowContains(TableConstants.PlansAndCoverages.PLAN.getName(), "NC-NC").getCell(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName()).click();
        String participantsNumberNC = rowFor20_24.getCell(NUMBER_OF_PARTICIPANTS.getName()).getValue().concat(".0");
        String totalVolumeNC = new Currency(rowFor20_24.getCell(TOTAL_VOLUME.getName()).getValue()).toPlainString().substring(0, 6);
        String rateNC = rowFor20_24.getCell(RATE.getName()).getValue().substring(0, 3);

        tablePlansAndCoverages.getRowContains(TableConstants.PlansAndCoverages.PLAN.getName(), "VOL-VOL").getCell(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName()).click();
        String participantsNumberVOL = rowFor20_24.getCell(NUMBER_OF_PARTICIPANTS.getName()).getValue().concat(".0");
        String totalVolumeVOL = new Currency(rowFor20_24.getCell(TOTAL_VOLUME.getName()).getValue()).toPlainString().substring(0, 3);
        String rateVOL = rowFor20_24.getCell(RATE.getName()).getValue().substring(0, 3);

        tablePlansAndCoverages.getRowContains(TableConstants.PlansAndCoverages.PLAN.getName(), "SGR-SGR").getCell(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName()).click();
        String participantsNumberSGR = rowFor20_24.getCell(NUMBER_OF_PARTICIPANTS.getName()).getValue().concat(".0");
        String totalVolumeSGR = new Currency(rowFor20_24.getCell(TOTAL_VOLUME.getName()).getValue()).toPlainString().substring(0, 3);
        String rateSGR = rowFor20_24.getCell(RATE.getName()).getValue().substring(0, 3);
        Tab.buttonSaveAndExit.click();

        ResponseContainer<SalesforceQuoteModel> responseQuoteUpdatedCON = RetryService.run(
                predicate -> !predicate.getModel().getBenefitMaximum().equals("6000.0"),
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageCON),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(15, TimeUnit.SECONDS));
        assertSoftly(softly -> {

            LOGGER.info("Steps#4 for CON verification");
            softly.assertThat(responseQuoteUpdatedCON.getModel().getPercentOfSalary()).isEqualTo("70%");
            softly.assertThat(responseQuoteUpdatedCON.getModel().getMaximumPaymentDuration()).isEqualTo("11 Weeks");
            softly.assertThat(responseQuoteUpdatedCON.getModel().getEliminationPeriodInjury()).isEqualTo("15");
            softly.assertThat(responseQuoteUpdatedCON.getModel().getEliminationPeriodSickness()).isEqualTo("15");
            softly.assertThat(responseQuoteUpdatedCON.getModel().getTotalNumberOfEligibleLivesStd()).isEqualTo("9.0");

            LOGGER.info("Steps#5 for CON verification");
            softly.assertThat(responseQuoteUpdatedCON.getModel().getParticipants20_24()).isEqualTo(participantsNumberCON);
            softly.assertThat(responseQuoteUpdatedCON.getModel().getRate20_24()).isEqualTo(rateCON);
            softly.assertThat(responseQuoteUpdatedCON.getModel().getTotalVolume20_24()).isEqualTo(totalVolumeCON);
            softly.assertThat(responseQuoteUpdatedCON.getModel().getClassificationGroup()).isEqualTo("1");

            LOGGER.info("Steps#6 for CON verification");
            softly.assertThat(responseQuoteUpdatedCON.getModel().getCredibilityFactor()).isEqualTo("1.0");
            softly.assertThat(responseQuoteUpdatedCON.getModel().getStage()).isEqualTo("Being Worked");
        });

        ResponseContainer<SalesforceQuoteModel> responseQuoteUpdatedNC = RetryService.run(
                predicate -> !predicate.getModel().getBenefitMaximum().equals("6000.0"),
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageNC),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(15, TimeUnit.SECONDS));
        assertSoftly(softly -> {

            LOGGER.info("Steps#4 for NC verification");
            softly.assertThat(responseQuoteUpdatedNC.getModel().getBenefitMaximum()).isEqualTo("750.0");
            softly.assertThat(responseQuoteUpdatedNC.getModel().getMaximumPaymentDuration()).isEqualTo("26 Weeks");
            softly.assertThat(responseQuoteUpdatedNC.getModel().getEliminationPeriodInjury()).isEqualTo("30");
            softly.assertThat(responseQuoteUpdatedNC.getModel().getEliminationPeriodSickness()).isEqualTo("30");
            softly.assertThat(responseQuoteUpdatedNC.getModel().getEliminationPeriodInjuryOther()).isEqualTo(null);
            softly.assertThat(responseQuoteUpdatedNC.getModel().getEliminationPeriodSicknessOther()).isEqualTo(null);
            softly.assertThat(responseQuoteUpdatedNC.getModel().getTotalNumberOfEligibleLivesStd()).isEqualTo("11.0");

            LOGGER.info("Steps#5 for NC verification");
            softly.assertThat(responseQuoteUpdatedNC.getModel().getParticipants20_24()).isEqualTo(participantsNumberNC);
            softly.assertThat(responseQuoteUpdatedNC.getModel().getRate20_24()).isEqualTo(rateNC);
            softly.assertThat(responseQuoteUpdatedNC.getModel().getTotalVolume20_24()).isEqualTo(totalVolumeNC);
            softly.assertThat(responseQuoteUpdatedNC.getModel().getClassificationGroup()).isEqualTo("1");

            LOGGER.info("Steps#6 for NC verification");
            softly.assertThat(responseQuoteUpdatedNC.getModel().getCredibilityFactor()).isEqualTo("1.0");
            softly.assertThat(responseQuoteUpdatedNC.getModel().getStage()).isEqualTo("Being Worked");
        });

        ResponseContainer<SalesforceQuoteModel> responseQuoteUpdatedVOL = RetryService.run(
                predicate -> !predicate.getModel().getBenefitMaximum().equals("6000.0"),
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageVOL),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(15, TimeUnit.SECONDS));
        assertSoftly(softly -> {

            LOGGER.info("Steps#4 for VOL verification");
            softly.assertThat(responseQuoteUpdatedVOL.getModel().getPercentOfSalary()).isEqualTo("50%");
            softly.assertThat(responseQuoteUpdatedVOL.getModel().getMaximumPaymentDuration()).isEqualTo("Other");
            softly.assertThat(responseQuoteUpdatedVOL.getModel().getMaximumPaymentDurationOther()).isEqualTo("10 Weeks");
            softly.assertThat(responseQuoteUpdatedVOL.getModel().getEliminationPeriodInjury()).isEqualTo("8");
            softly.assertThat(responseQuoteUpdatedVOL.getModel().getEliminationPeriodSickness()).isEqualTo("8");

            LOGGER.info("Steps#5 for VOL verification");
            softly.assertThat(responseQuoteUpdatedVOL.getModel().getParticipants20_24()).isEqualTo(participantsNumberVOL);
            softly.assertThat(responseQuoteUpdatedVOL.getModel().getRate20_24()).isEqualTo(rateVOL);
            softly.assertThat(responseQuoteUpdatedVOL.getModel().getTotalVolume20_24()).isEqualTo(totalVolumeVOL);
            softly.assertThat(responseQuoteUpdatedVOL.getModel().getClassificationGroup()).isEqualTo("1");

            LOGGER.info("Steps#6 for VOL verification");
            softly.assertThat(responseQuoteUpdatedVOL.getModel().getCredibilityFactor()).isEqualTo("1.0");
            softly.assertThat(responseQuoteUpdatedVOL.getModel().getStage()).isEqualTo("Being Worked");
        });

        ResponseContainer<SalesforceQuoteModel> responseQuoteUpdatedSGR = RetryService.run(
                predicate -> !predicate.getModel().getBenefitMaximum().equals("6000.0"),
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageSGR),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(15, TimeUnit.SECONDS));
        assertSoftly(softly -> {

            LOGGER.info("Steps#4 for SGR verification");
            softly.assertThat(responseQuoteUpdatedSGR.getModel().getBenefitMaximum()).isEqualTo("250.0");
            softly.assertThat(responseQuoteUpdatedSGR.getModel().getMaximumPaymentDuration()).isEqualTo("25 Weeks");
            softly.assertThat(responseQuoteUpdatedSGR.getModel().getEliminationPeriodInjury()).isEqualTo("4");
            softly.assertThat(responseQuoteUpdatedSGR.getModel().getEliminationPeriodSickness()).isEqualTo("4");
            softly.assertThat(responseQuoteUpdatedSGR.getModel().getEliminationPeriodInjuryOther()).isEqualTo(null);
            softly.assertThat(responseQuoteUpdatedSGR.getModel().getEliminationPeriodSicknessOther()).isEqualTo(null);

            LOGGER.info("Steps#5 for SGR verification");
            softly.assertThat(responseQuoteUpdatedSGR.getModel().getParticipants20_24()).isEqualTo(participantsNumberSGR);
            softly.assertThat(responseQuoteUpdatedSGR.getModel().getRate20_24()).isEqualTo(rateSGR);
            softly.assertThat(responseQuoteUpdatedSGR.getModel().getTotalVolume20_24()).isEqualTo(totalVolumeSGR);
            softly.assertThat(responseQuoteUpdatedSGR.getModel().getClassificationGroup()).isEqualTo("1");

            LOGGER.info("Steps#6 for SGR verification");
            softly.assertThat(responseQuoteUpdatedVOL.getModel().getCredibilityFactor()).isEqualTo("1.0");
            softly.assertThat(responseQuoteUpdatedVOL.getModel().getStage()).isEqualTo("Being Worked");
        });

        LOGGER.info("Steps#8 verification");
        shortTermDisabilityMasterPolicy.declineByCustomerQuote().perform(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DECLINE_BY_CUSTOMER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_DECLINED);

        ResponseContainer<SalesforceQuoteModel> responseQuoteCONDeclined = RetryService.run(
                predicate -> !predicate.getModel().getStage().equals("Being Worked"),
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageCON),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertThat(responseQuoteCONDeclined.getModel().getStage()).isEqualTo("Closed");

        ResponseContainer<SalesforceQuoteModel> responseQuoteNCDeclined = RetryService.run(
                predicate -> !predicate.getModel().getStage().equals("Being Worked"),
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageNC),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertThat(responseQuoteNCDeclined.getModel().getStage()).isEqualTo("Closed");

        ResponseContainer<SalesforceQuoteModel> responseQuoteVOLDeclined = RetryService.run(
                predicate -> !predicate.getModel().getStage().equals("Being Worked"),
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageVOL),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertThat(responseQuoteVOLDeclined.getModel().getStage()).isEqualTo("Closed");

        ResponseContainer<SalesforceQuoteModel> responseQuoteSRGDeclined = RetryService.run(
                predicate -> !predicate.getModel().getStage().equals("Being Worked"),
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageSGR),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertThat(responseQuoteSRGDeclined.getModel().getStage()).isEqualTo("Closed");
    }
}
