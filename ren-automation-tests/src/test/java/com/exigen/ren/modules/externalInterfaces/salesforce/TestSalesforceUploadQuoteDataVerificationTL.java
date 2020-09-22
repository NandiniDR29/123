package com.exigen.ren.modules.externalInterfaces.salesforce;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.salesforce.model.SalesforceQuoteModel;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.*;
import static com.exigen.ren.main.enums.SalesforceConstants.SF_TL_BASIC_LIFE_AND_ADD;
import static com.exigen.ren.main.enums.TableConstants.CoverageDefinition.PLAN;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.AgeReductionScheduleDetailMetaData.AGE_DROP_DOWN;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.AgeReductionScheduleDetailMetaData.REDUCED_TO_PERCENTAGE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.BENEFIT_MAXIMUM_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.FLAT_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.GuaranteedIssueMetaData.GI_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab.tableCoverageDefinition;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab.tableCoveragesName;
import static com.exigen.ren.utils.components.Components.INTEGRATION;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestSalesforceUploadQuoteDataVerificationTL extends SalesforceBaseTest implements CustomerContext, ProfileContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {WITHOUT_TS, REGRESSION, INTEGRATION})
    @TestInfo(testCaseId = {"REN-20399"}, component = INTEGRATION)
    public void testSalesforceUploadQuoteDataVerificationTL() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(termLifeInsuranceMasterPolicy.getType()));
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        String priorCarrierName = "Sun Life";

        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NY")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PRIOR_LIFE_COVERAGE.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PRIOR_CARRIER_NAME.getLabel()), priorCarrierName)
                .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks()).resolveLinks(), premiumSummaryTab.getClass(), true);

        planDefinitionTab.navigateToTab();
        tableCoverageDefinition.getRow(ImmutableMap.of(PLAN.getName(), "VFF-Volunteer Fire Fighters")).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
        planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION_SCHEDULE_DETAILS).getAsset(AGE_DROP_DOWN).setValue("65");
        planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION_SCHEDULE_DETAILS).getAsset(REDUCED_TO_PERCENTAGE).setValue("55%");
        premiumSummaryTab.navigateToTab();
        premiumSummaryTab.rate();

        Row rowBl = tableCoveragesName.getRowContains(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(), BASIC_LIFE_PLAN);
        Row rowBLV = tableCoveragesName.getRowContains(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(), BASIC_LIFE_PLAN_PLUS_VOLUNTARY);
        Row rowVFF = tableCoveragesName.getRowContains(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(), VOLUNTEER_FIRE_FIGHTERS);
        Row rowVL = tableCoveragesName.getRowContains(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(), VOLUNTARY_LIFE_PLAN);

        String participantNumberBL = rowBl.getCell(TableConstants.PremiumSummaryCoveragesTable.PARTICIPANTS.getName()).getValue().concat(".0");
        String totalVolumeBL = new Currency(rowBl.getCell(TableConstants.PremiumSummaryCoveragesTable.VOLUME.getName()).getValue()).toPlainString().replace(".00", ".0");
        String rateBL = rowBl.getCell(TableConstants.PremiumSummaryCoveragesTable.MANUAL_RATE.getName()).getValue().substring(0, 5);

        String participantNumberBVL = rowBLV.getCell(TableConstants.PremiumSummaryCoveragesTable.PARTICIPANTS.getName()).getValue().concat(".0");
        String totalVolumeBVL = new Currency(rowBLV.getCell(TableConstants.PremiumSummaryCoveragesTable.VOLUME.getName()).getValue()).toPlainString().replace(".00", ".0");
        String rateBVL = rowBLV.getCell(TableConstants.PremiumSummaryCoveragesTable.MANUAL_RATE.getName()).getValue().substring(0, 5);

        String participantNumberVFF = rowVFF.getCell(TableConstants.PremiumSummaryCoveragesTable.PARTICIPANTS.getName()).getValue().concat(".0");
        String totalVolumeVFF = new Currency(rowVFF.getCell(TableConstants.PremiumSummaryCoveragesTable.VOLUME.getName()).getValue()).toPlainString().replace(".00", ".0");
        String rateVFF = rowVFF.getCell(TableConstants.PremiumSummaryCoveragesTable.MANUAL_RATE.getName()).getValue().substring(0, 5);

        String participantNumberVL = rowVL.getCell(TableConstants.PremiumSummaryCoveragesTable.PARTICIPANTS.getName()).getValue().concat(".0");
        String totalVolumeVL = new Currency(rowVL.getCell(TableConstants.PremiumSummaryCoveragesTable.VOLUME.getName()).getValue()).toPlainString().replace(".00", ".0");
        String rateVL = rowVL.getCell(TableConstants.PremiumSummaryCoveragesTable.MANUAL_RATE.getName()).getValue().substring(0, 5);

        Tab.buttonSaveAndExit.click();
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        String quoteIdCoverageBL = String.format("%s_%s_%s", quoteNumber, BASIC_LIFE_PLAN, SF_TL_BASIC_LIFE_AND_ADD);
        String quoteIdCoverageBLV = String.format("%s_%s_%s", quoteNumber, BASIC_LIFE_PLAN_PLUS_VOLUNTARY, SF_TL_BASIC_LIFE_AND_ADD);
        String quoteIdCoverageVFF = String.format("%s_%s_%s", quoteNumber, VOLUNTEER_FIRE_FIGHTERS, SF_TL_BASIC_LIFE_AND_ADD);
        String quoteIdCoverageVL = String.format("%s_%s_%s", quoteNumber, VOLUNTARY_LIFE_PLAN, SF_TL_BASIC_LIFE_AND_ADD);

        LOGGER.info("Steps#1, 2, 3 verification for Basic Life");
        ResponseContainer<SalesforceQuoteModel> responseQuoteBL = getResponseQuote(quoteIdCoverageBL);

        assertSoftly(softly -> {
            softly.assertThat(responseQuoteBL.getModel().getStage()).isEqualTo("Being Worked");
            softly.assertThat(responseQuoteBL.getModel().getPolicyType()).isEqualTo("REN");
            softly.assertThat(responseQuoteBL.getModel().getAgeReduction()).isEqualTo("Age 70, reduce to 50%");
            softly.assertThat(responseQuoteBL.getModel().getSitusStateRelated()).isEqualTo("NY");
            softly.assertThat(responseQuoteBL.getModel().getEmployerContribution()).isEqualTo("100.0");
            softly.assertThat(responseQuoteBL.getModel().getQuoteRecordTypeHolder()).isEqualTo(SF_TL_BASIC_LIFE_AND_ADD);
            softly.assertThat(responseQuoteBL.getModel().getProduct()).isEqualTo("LIFE");
            softly.assertThat(responseQuoteBL.getModel().getWaiverOfPremium()).isEqualTo("false");
            softly.assertThat(responseQuoteBL.getModel().getEnrolled()).isEqualTo(participantNumberBL);
            softly.assertThat(responseQuoteBL.getModel().getVolume()).isEqualTo(totalVolumeBL);
            softly.assertThat(responseQuoteBL.getModel().getFormulaRate()).isEqualTo(rateBL);
            softly.assertThat(responseQuoteBL.getModel().getPriorCarrierName()).isEqualTo(priorCarrierName);
            softly.assertThat(responseQuoteBL.getModel().getQuoteName()).isEqualTo(String.format("%s_%s_%s", quoteNumber, "Term Life Insurance", BASIC_LIFE_PLAN));
            softly.assertThat(responseQuoteBL.getModel().getSicCode()).isEqualTo(tdSpecific().getValue("PlanDefinitionBasicLifeInsurance", SIC_CODE.getLabel()));
            softly.assertThat(responseQuoteBL.getModel().getGuaranteeIssueAmount()).isEqualTo(tdSpecific().getValue("PlanDefinitionBasicLifeInsurance", GUARANTEED_ISSUE.getLabel(), GI_AMOUNT.getLabel()));
            softly.assertThat(responseQuoteBL.getModel().getFlatBenefitAmount()).isEqualTo(tdSpecific().getValue("PlanDefinitionBasicLifeInsurance", BENEFIT_SCHEDULE.getLabel(), FLAT_BENEFIT_AMOUNT.getLabel()));
            softly.assertThat(responseQuoteBL.getModel().getEmployeesEligible()).isEqualTo(tdSpecific().getValue("PlanDefinitionBasicLifeInsurance", TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel()).concat(".0"));
        });

        LOGGER.info("Steps#1, 2, 3 verification for Volunteer Fire Fighters");
        ResponseContainer<SalesforceQuoteModel> responseQuoteVFF = getResponseQuote(quoteIdCoverageVFF);

            assertSoftly(softly -> {
            softly.assertThat(responseQuoteVFF.getModel().getStage()).isEqualTo("Being Worked");
            softly.assertThat(responseQuoteVFF.getModel().getPolicyType()).isEqualTo("REN");
            softly.assertThat(responseQuoteVFF.getModel().getAgeReduction()).isEqualTo("Other");
            softly.assertThat(responseQuoteVFF.getModel().getOtherAgeReduction()).isEqualTo("Age 65, reduce to 55.00");
            softly.assertThat(responseQuoteVFF.getModel().getSitusStateRelated()).isEqualTo("NY");
            softly.assertThat(responseQuoteVFF.getModel().getEmployerContribution()).isEqualTo("100.0");
            softly.assertThat(responseQuoteVFF.getModel().getQuoteRecordTypeHolder()).isEqualTo(SF_TL_BASIC_LIFE_AND_ADD);
            softly.assertThat(responseQuoteVFF.getModel().getProduct()).isEqualTo("LIFE");
            softly.assertThat(responseQuoteVFF.getModel().getWaiverOfPremium()).isEqualTo("false");
            softly.assertThat(responseQuoteVFF.getModel().getEnrolled()).isEqualTo(participantNumberVFF);
            softly.assertThat(responseQuoteVFF.getModel().getVolume()).isEqualTo(totalVolumeVFF);
            softly.assertThat(responseQuoteVFF.getModel().getFormulaRate()).isEqualTo(rateVFF);
            softly.assertThat(responseQuoteVFF.getModel().getPriorCarrierName()).isEqualTo(priorCarrierName);
            softly.assertThat(responseQuoteVFF.getModel().getQuoteName()).isEqualTo(String.format("%s_%s_%s", quoteNumber, "Term Life Insurance", VOLUNTEER_FIRE_FIGHTERS));
            softly.assertThat(responseQuoteVFF.getModel().getSicCode()).isEqualTo(tdSpecific().getValue("PlanDefinitionBasicLifeInsuranceVoluntaryVFF", SIC_CODE.getLabel()));
            softly.assertThat(responseQuoteVFF.getModel().getGuaranteeIssueAmount()).isEqualTo(tdSpecific().getValue("PlanDefinitionBasicLifeInsuranceVoluntaryVFF", GUARANTEED_ISSUE.getLabel(), GI_AMOUNT.getLabel()));
            softly.assertThat(responseQuoteVFF.getModel().getFlatBenefitAmount()).isEqualTo(tdSpecific().getValue("PlanDefinitionBasicLifeInsuranceVoluntaryVFF", BENEFIT_SCHEDULE.getLabel(), FLAT_BENEFIT_AMOUNT.getLabel()));
            softly.assertThat(responseQuoteVFF.getModel().getEmployeesEligible()).isEqualTo(tdSpecific().getValue("PlanDefinitionBasicLifeInsuranceVoluntaryVFF", TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel()).concat(".0"));
        });

        LOGGER.info("Steps#1, 2, 3 verification for Voluntary Life");
        ResponseContainer<SalesforceQuoteModel> responseQuoteVL = getResponseQuote(quoteIdCoverageVL);

        assertSoftly(softly -> {
            softly.assertThat(responseQuoteVL.getModel().getStage()).isEqualTo("Being Worked");
            softly.assertThat(responseQuoteVL.getModel().getPolicyType()).isEqualTo("REN");
            softly.assertThat(responseQuoteVL.getModel().getAgeReduction()).isEqualTo("Age 65, reduce to 65%");
            softly.assertThat(responseQuoteVL.getModel().getSitusStateRelated()).isEqualTo("NY");
            softly.assertThat(responseQuoteVL.getModel().getEmployerContribution()).isEqualTo("50.0");
            softly.assertThat(responseQuoteVL.getModel().getQuoteRecordTypeHolder()).isEqualTo(SF_TL_BASIC_LIFE_AND_ADD);
            softly.assertThat(responseQuoteVL.getModel().getProduct()).isEqualTo("LIFE");
            softly.assertThat(responseQuoteVL.getModel().getWaiverOfPremium()).isEqualTo("true");
            softly.assertThat(responseQuoteVL.getModel().getEnrolled()).isEqualTo(participantNumberVL);
            softly.assertThat(responseQuoteVL.getModel().getVolume()).isEqualTo(totalVolumeVL);
            softly.assertThat(responseQuoteVL.getModel().getFormulaRate()).isEqualTo(rateVL);
            softly.assertThat(responseQuoteVL.getModel().getPriorCarrierName()).isEqualTo(priorCarrierName);
            softly.assertThat(responseQuoteVL.getModel().getQuoteName()).isEqualTo(String.format("%s_%s_%s", quoteNumber, "Term Life Insurance", VOLUNTARY_LIFE_PLAN));
            softly.assertThat(responseQuoteVL.getModel().getSicCode()).isEqualTo(tdSpecific().getValue("PlanDefinitionEmployeeVoluntaryLifeInsuranceVL", SIC_CODE.getLabel()));
            softly.assertThat(responseQuoteVL.getModel().getGuaranteeIssueAmount()).isEqualTo(tdSpecific().getValue("PlanDefinitionEmployeeVoluntaryLifeInsuranceVL", GUARANTEED_ISSUE.getLabel(), GI_AMOUNT.getLabel()));
            softly.assertThat(responseQuoteVL.getModel().getFlatBenefitAmount()).isEqualTo(tdSpecific().getValue("PlanDefinitionEmployeeVoluntaryLifeInsuranceVL", BENEFIT_SCHEDULE.getLabel(), BENEFIT_MAXIMUM_AMOUNT.getLabel()));
            softly.assertThat(responseQuoteVL.getModel().getEmployeesEligible()).isEqualTo(tdSpecific().getValue("PlanDefinitionEmployeeVoluntaryLifeInsuranceVL", TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel()).concat(".0"));
        });

        LOGGER.info("Steps#1, 2, 3 verification for Basic Life + Voluntary");
        ResponseContainer<SalesforceQuoteModel> responseQuoteBVL = getResponseQuote(quoteIdCoverageBLV);

        assertSoftly(softly -> {
            softly.assertThat(responseQuoteBVL.getModel().getStage()).isEqualTo("Being Worked");
            softly.assertThat(responseQuoteBVL.getModel().getPolicyType()).isEqualTo("REN");
            softly.assertThat(responseQuoteBVL.getModel().getAgeReduction()).isEqualTo("ADEA1");
            softly.assertThat(responseQuoteBVL.getModel().getSitusStateRelated()).isEqualTo("NY");
            softly.assertThat(responseQuoteBVL.getModel().getEmployerContribution()).isEqualTo("50.0");
            softly.assertThat(responseQuoteBVL.getModel().getQuoteRecordTypeHolder()).isEqualTo(SF_TL_BASIC_LIFE_AND_ADD);
            softly.assertThat(responseQuoteBVL.getModel().getProduct()).isEqualTo("LIFE");
            softly.assertThat(responseQuoteBVL.getModel().getWaiverOfPremium()).isEqualTo("false");
            softly.assertThat(responseQuoteBVL.getModel().getEnrolled()).isEqualTo(participantNumberBVL);
            softly.assertThat(responseQuoteBVL.getModel().getVolume()).isEqualTo(totalVolumeBVL);
            softly.assertThat(responseQuoteBVL.getModel().getFormulaRate()).isEqualTo(rateBVL);
            softly.assertThat(responseQuoteBVL.getModel().getPriorCarrierName()).isEqualTo(priorCarrierName);
            softly.assertThat(responseQuoteBVL.getModel().getQuoteName()).isEqualTo(String.format("%s_%s_%s", quoteNumber, "Term Life Insurance", BASIC_LIFE_PLAN_PLUS_VOLUNTARY));
            softly.assertThat(responseQuoteBVL.getModel().getSicCode()).isEqualTo(tdSpecific().getValue("PlanDefinitionBasicLifeInsuranceVoluntary", SIC_CODE.getLabel()));
            softly.assertThat(responseQuoteBVL.getModel().getGuaranteeIssueAmount()).isEqualTo(tdSpecific().getValue("PlanDefinitionBasicLifeInsuranceVoluntary", GUARANTEED_ISSUE.getLabel(), GI_AMOUNT.getLabel()));
            softly.assertThat(responseQuoteBVL.getModel().getFlatBenefitAmount()).isEqualTo(tdSpecific().getValue("PlanDefinitionBasicLifeInsuranceVoluntary", BENEFIT_SCHEDULE.getLabel(), BENEFIT_MAXIMUM_AMOUNT.getLabel()));
            softly.assertThat(responseQuoteBVL.getModel().getEmployeesEligible()).isEqualTo(tdSpecific().getValue("PlanDefinitionBasicLifeInsuranceVoluntary", TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel()).concat(".0"));
        });

        LOGGER.info("Steps#4 execution");
        termLifeInsuranceMasterPolicy.dataGather().start();
        planDefinitionTab.navigateToTab();

        tableCoverageDefinition.getRow(ImmutableMap.of(PLAN.getName(), "BL-Basic Life")).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
        planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("9");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(FLAT_BENEFIT_AMOUNT).setValue("11000.0");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.GUARANTEED_ISSUE).getAsset(GI_AMOUNT).setValue("210000.0");
        planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION_SCHEDULE).setValue("ADEA2");
        planDefinitionTab.getAssetList().getAsset(WAIVER_OF_PREMIUM).setValue("Included");

        tableCoverageDefinition.getRow(ImmutableMap.of(PLAN.getName(), "BLV-Basic Life + Voluntary")).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
        planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("6");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(BENEFIT_MAXIMUM_AMOUNT).setValue("470000.0");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.GUARANTEED_ISSUE).getAsset(GI_AMOUNT).setValue("200000.0");
        planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION_SCHEDULE).setValue("CUSTOM");
        planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION_SCHEDULE_DETAILS).getAsset(AGE_DROP_DOWN).setValue("65");
        planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION_SCHEDULE_DETAILS).getAsset(REDUCED_TO_PERCENTAGE).setValue("55%");
        planDefinitionTab.getAssetList().getAsset(WAIVER_OF_PREMIUM).setValue("Not Included");

        tableCoverageDefinition.getRow(ImmutableMap.of(PLAN.getName(), "VFF-Volunteer Fire Fighters")).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
        planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("3");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(FLAT_BENEFIT_AMOUNT).setValue("12000.0");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.GUARANTEED_ISSUE).getAsset(GI_AMOUNT).setValue("220000.0");
        planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION_SCHEDULE).setValue("50%@70 35%@75");
        planDefinitionTab.getAssetList().getAsset(WAIVER_OF_PREMIUM).setValue("Included");

        tableCoverageDefinition.getRow(ImmutableMap.of(PLAN.getName(), "VL-Voluntary Life")).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
        planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("7");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(BENEFIT_MAXIMUM_AMOUNT).setValue("450000.0");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.GUARANTEED_ISSUE).getAsset(GI_AMOUNT).setValue("160000.0");
        planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION_SCHEDULE).setValue("ADEA1");
        planDefinitionTab.getAssetList().getAsset(WAIVER_OF_PREMIUM).setValue("Not Included");

        planDefinitionTab.submitTab();
        classificationManagementMpTab.submitTab();
        premiumSummaryTab.rate();
        Tab.buttonSaveAndExit.click();

        LOGGER.info("Steps#4 verification for Basic Life");
        ResponseContainer<SalesforceQuoteModel> responseQuoteUpdatedBL = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getWaiverOfPremium().equals("true"),
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageBL),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertSoftly(softly -> {
            softly.assertThat(responseQuoteUpdatedBL.getModel().getAgeReduction()).isEqualTo("ADEA2");
            softly.assertThat(responseQuoteUpdatedBL.getModel().getWaiverOfPremium()).isEqualTo("true");
            softly.assertThat(responseQuoteUpdatedBL.getModel().getGuaranteeIssueAmount()).isEqualTo("210000.0");
            softly.assertThat(responseQuoteUpdatedBL.getModel().getFlatBenefitAmount()).isEqualTo("11000.0");
            softly.assertThat(responseQuoteUpdatedBL.getModel().getEmployeesEligible()).isEqualTo("9.0");
        });

        LOGGER.info("Steps#4 verification for Volunteer Fire Fighters");
        ResponseContainer<SalesforceQuoteModel> responseQuoteUpdatedVFF = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getWaiverOfPremium().equals("true"),
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageVFF),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertSoftly(softly -> {
            softly.assertThat(responseQuoteUpdatedVFF.getModel().getAgeReduction()).isEqualTo("Age 70, reduce to 50%");
            softly.assertThat(responseQuoteUpdatedVFF.getModel().getWaiverOfPremium()).isEqualTo("true");
            softly.assertThat(responseQuoteUpdatedVFF.getModel().getGuaranteeIssueAmount()).isEqualTo("220000.0");
            softly.assertThat(responseQuoteUpdatedVFF.getModel().getFlatBenefitAmount()).isEqualTo("12000.0");
            softly.assertThat(responseQuoteUpdatedVFF.getModel().getEmployeesEligible()).isEqualTo("3.0");
        });

        LOGGER.info("Steps#4 verification for Voluntary Life");
        ResponseContainer<SalesforceQuoteModel> responseQuoteUpdatedVL = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getWaiverOfPremium().equals("false"),
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageVL),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertSoftly(softly -> {
            softly.assertThat(responseQuoteUpdatedVL.getModel().getAgeReduction()).isEqualTo("ADEA1");
            softly.assertThat(responseQuoteUpdatedVL.getModel().getWaiverOfPremium()).isEqualTo("false");
            softly.assertThat(responseQuoteUpdatedVL.getModel().getGuaranteeIssueAmount()).isEqualTo("160000.0");
            softly.assertThat(responseQuoteUpdatedVL.getModel().getFlatBenefitAmount()).isEqualTo("450000.0");
            softly.assertThat(responseQuoteUpdatedVL.getModel().getEmployeesEligible()).isEqualTo("7.0");
        });

        LOGGER.info("Steps#4 verification for Basic Life + Voluntary");
        ResponseContainer<SalesforceQuoteModel> responseQuoteUpdatedBVL = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getWaiverOfPremium().equals("false"),
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageBLV),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertSoftly(softly -> {
            softly.assertThat(responseQuoteUpdatedBVL.getModel().getOtherAgeReduction()).isEqualTo("Age 65, reduce to 55.00");
            softly.assertThat(responseQuoteUpdatedBVL.getModel().getWaiverOfPremium()).isEqualTo("false");
            softly.assertThat(responseQuoteUpdatedBVL.getModel().getGuaranteeIssueAmount()).isEqualTo("200000.0");
            softly.assertThat(responseQuoteUpdatedBVL.getModel().getFlatBenefitAmount()).isEqualTo("470000.0");
            softly.assertThat(responseQuoteUpdatedBVL.getModel().getEmployeesEligible()).isEqualTo("6.0");
        });
    }
}
