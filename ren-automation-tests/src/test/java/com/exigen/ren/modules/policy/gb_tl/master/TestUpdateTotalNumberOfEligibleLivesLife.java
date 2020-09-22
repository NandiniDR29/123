package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.common.StartEndorsementActionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.netty.util.internal.StringUtil;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.Tab.buttonRate;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN_PLUS_VOLUNTARY;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.enums.TableConstants.PlansAndCoverages.COVERAGE_NAME;
import static com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementActionTabMetaData.ENDORSEMENT_DATE;
import static com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementActionTabMetaData.ENDORSEMENT_REASON;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab.changeCoverageTo;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUpdateTotalNumberOfEligibleLivesLife extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-39881"}, component = POLICY_GROUPBENEFITS)
    public void testVerifyTotalNumberOfEligibleLivesLife() {
        LOGGER.info("REN-39881 General Precondition");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        LOGGER.info("REN-39881 STEP#1");
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PlanDefinitionTab.class);
        assertThat(planDefinitionTab.isTabSelected()).withFailMessage("%s is not opened", planDefinitionTab.getMetaKey()).isTrue();

        LOGGER.info("REN-39881 STEP#2");
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASIC_LIFE_PLAN_PLUS_VOLUNTARY));
        planDefinitionTab.addCoverage(BASIC_LIFE_PLAN_PLUS_VOLUNTARY, DEP_ADD);
        assertSoftly(softly -> {
            softly.assertThat(PlanDefinitionTab.tableCoverageDefinition)
                    .hasRowsThatContain(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), BTL)
                    .hasRowsThatContain(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), ADD)
                    .hasRowsThatContain(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), DEP_BTL)
                    .hasRowsThatContain(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), VOL_BTL)
                    .hasRowsThatContain(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), VOL_ADD)
                    .hasRowsThatContain(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), SP_VOL_BTL)
                    .hasRowsThatContain(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), DEP_VOL_BTL)
                    .hasRowsThatContain(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), DEP_VOL_ADD)
                    .hasRowsThatContain(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), DEP_ADD);
        });

        LOGGER.info("REN-39881 STEP#3");
        ImmutableList.of(BTL, VOL_BTL)
                .forEach(plan -> {
                    PlanDefinitionTab.changeCoverageTo(plan);
                    assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).isPresent();
                });

        LOGGER.info("REN-39881 STEP#5");
        ImmutableList.of(BTL, VOL_BTL)
                .forEach(plan -> {
                    PlanDefinitionTab.changeCoverageTo(plan);
                    planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE).setValue("Eligible");
                    assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).hasValue(StringUtil.EMPTY_STRING);
                });

        ImmutableMap<String, String> planDefinitionMap = new ImmutableMap.Builder<String, String>()
                .put(BTL, "BTL")
                .put(ADD, "ADD")
                .put(DEP_BTL, "DEP_BTL")
                .put(VOL_BTL, "VOL_BTL")
                .put(VOL_ADD, "VOL_ADD")
                .put(SP_VOL_BTL, "SP_VOL_BTL")
                .put(DEP_VOL_BTL, "DEP_VOL_BTL")
                .put(DEP_VOL_ADD, "DEP_VOL_ADD")
                .put(DEP_ADD, "DEP_ADD")
                .build();

        planDefinitionMap.forEach((coverage, tdKey) -> {
            changeCoverageTo(coverage);
            planDefinitionTab.fillTab(tdSpecific().getTestData(tdKey));
        });

        LOGGER.info("REN-39881 STEP#6");
        classificationManagementMpTab.navigateToTab();
        classificationManagementMpTab.fillTab(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));
        classificationManagementMpTab.submitTab();
        assertThat(premiumSummaryTab.isTabSelected()).withFailMessage("%s is not opened", premiumSummaryTab.getMetaKey()).isTrue();

        LOGGER.info("REN-39881 STEP#7");
        premiumSummaryTab.getAssetList().fill(getDefaultTLMasterPolicyData());
        buttonRate.click();
        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("REN-39881 STEP#8");
        ImmutableMap<String, String> coveragesMap1 = getCoveragesAndParticipantsFromPremiumSummaryTab();

        LOGGER.info("REN-39881 STEP#9");
        checkParticipantsOnClassificationTab(coveragesMap1);

        LOGGER.info("REN-39881 STEP#10");
        planDefinitionTab.navigateToTab();
        coveragesMap1.forEach((coverage, participants) -> {
            PlanDefinitionTab.changeCoverageTo(coverage);
            assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).hasValue(participants);
        });

        LOGGER.info("REN-39881 STEP#12");
        removeCensusFileAndCheckParticipantsOnPlanDefTab(coveragesMap1);

        LOGGER.info("REN-39881 STEP#13,17");
        ImmutableMap<String, String> coveragesMap2 = new ImmutableMap.Builder<String, String>()
                .put(BTL, "11")
                .put(VOL_BTL, "12")
                .build();
        coveragesMap2.forEach((coverage, participants) -> {
            PlanDefinitionTab.changeCoverageTo(coverage);
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE).setValue("Enrolled");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue(participants);
        });
        premiumSummaryTab.navigateToTab();
        premiumSummaryTab.getAssetList().fill(getDefaultTLMasterPolicyData());
        buttonRate.click();
        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);


        LOGGER.info("REN-39881 STEP#20");
        premiumSummaryTab.submitTab();
        termLifeInsuranceMasterPolicy.propose().perform(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.acceptContract().perform(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.issue().perform(getDefaultTLMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);

        LOGGER.info("REN-39881 STEP#21");
        String policyEffectiveDateValue = PolicySummaryPage.labelPolicyEffectiveDate.getValue();
        termLifeInsuranceMasterPolicy.endorse().start();
        termLifeInsuranceMasterPolicy.endorse().getWorkspace().getTab(StartEndorsementActionTab.class).getAssetList().getAsset(ENDORSEMENT_DATE)
                .setValue(policyEffectiveDateValue);
        termLifeInsuranceMasterPolicy.endorse().getWorkspace().getTab(StartEndorsementActionTab.class).getAssetList().getAsset(ENDORSEMENT_REASON).setValue("Acquisition or Merger");
        Tab.buttonOk.click();
        Page.dialogConfirmation.confirm();
        planDefinitionTab.navigateToTab();
        coveragesMap2.forEach((coverage, participants) -> {
            PlanDefinitionTab.changeCoverageTo(coverage);
            assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).hasValue(participants);
        });

        LOGGER.info("REN-39881 STEP#22");
        coveragesMap2.forEach((coverage, participants) -> {
            PlanDefinitionTab.changeCoverageTo(coverage);
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE).setValue("Eligible");
            assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).hasValue(StringUtil.EMPTY_STRING);
        });

        LOGGER.info("REN-39881 STEP#23");
        premiumSummaryTab.navigateToTab();
        buttonRate.click();
        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("REN-39881 STEP#24");
        ImmutableMap<String, String> coveragesMap3 = getCoveragesAndParticipantsFromPremiumSummaryTab();

        LOGGER.info("REN-39881 STEP#25");
        checkParticipantsOnClassificationTab(coveragesMap3);

        LOGGER.info("REN-39881 STEP#26");
        removeCensusFileAndCheckParticipantsOnPlanDefTab(coveragesMap3);
    }

    private ImmutableMap<String, String> getCoveragesAndParticipantsFromPremiumSummaryTab(){
        String btlParticipants = PremiumSummaryTab.tableCoveragesName.getRow(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName(), BTL)
                .getCell(TableConstants.PremiumSummaryCoveragesTable.PARTICIPANTS.getName()).getValue();
        String volBtlParticipants = PremiumSummaryTab.tableCoveragesName.getRow(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName(), VOL_BTL)
                .getCell(TableConstants.PremiumSummaryCoveragesTable.PARTICIPANTS.getName()).getValue();
        return new ImmutableMap.Builder<String, String>()
                .put(BTL, btlParticipants)
                .put(VOL_BTL, volBtlParticipants)
                .build();
    }

    private void checkParticipantsOnClassificationTab(ImmutableMap<String, String> coveragesMap){
        classificationManagementMpTab.navigateToTab();
        coveragesMap.forEach((coverage, participants) -> {
            ClassificationManagementTab.coveragesTable.getRow(COVERAGE_NAME.getName(), coverage).getCell(COVERAGE_NAME.getName()).click();
            assertThat(ClassificationManagementTab.tableCoverageRelationships.getRow(1).getCell(TableConstants.CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName()))
                    .hasValue(participants);
        });
    }

    private void removeCensusFileAndCheckParticipantsOnPlanDefTab(ImmutableMap<String, String> coveragesMap){
        premiumSummaryTab.navigateToTab();
        PremiumSummaryTab.buttonRemoveRatingCensus.click();
        planDefinitionTab.navigateToTab();
        coveragesMap.forEach((coverage, participants) -> {
            PlanDefinitionTab.changeCoverageTo(coverage);
            assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).hasValue(StringUtil.EMPTY_STRING);
        });
    }
}
