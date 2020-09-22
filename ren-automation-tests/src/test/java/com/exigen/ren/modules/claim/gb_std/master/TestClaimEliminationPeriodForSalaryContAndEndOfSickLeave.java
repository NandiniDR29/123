package com.exigen.ren.modules.claim.gb_std.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.metadata.OtherIncomeBenefitActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.OtherIncomeBenefitActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.SingleBenefitCalculationActionTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimCalculateSingleBenefitTab.OTHER_INCOME_BENEFIT;
import static com.exigen.ren.main.enums.ActionConstants.CHANGE;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.OPEN;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.NC;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.claim.common.tabs.OtherIncomeBenefitActionTab.ListOfOtherIncomeBenefitTable;
import static com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantCoverageTab.buttonAddCoverage;
import static com.exigen.ren.main.modules.claim.common.tabs.SingleBenefitCalculationActionTab.EliminationQualificationPeriod.ELIMINATION_PERIOD_END_DATE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.labelClaimStatus;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimEliminationPeriodForSalaryContAndEndOfSickLeave extends ClaimGroupBenefitsSTDBaseTest {

    private static String endOfSalaryContinuation = "End of Salary Continuation";
    private static String endOfAccumulatedSickLeave = "End of Accumulated Sick Leave";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-23325", "REN-23367"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimsIntegrationEndOfSalaryContAndEndOfAccumulatedSickLeaveAndRecalculation() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel()),
                        new SimpleDataProvider().adjust(PolicyInformationParticipantParticipantCoverageTabMetaData.END_OF_SALARY_CONTINUATION.getLabel(), VALUE_YES))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]",
                        PlanDefinitionTabMetaData.BENEFIT_SCHEDULE.getLabel(), PolicyInformationParticipantParticipantCoverageTabMetaData.END_OF_ACCUMULATED_SICK_LEAVE.getLabel()), VALUE_YES));

        LOGGER.info("REN-23325: Steps 1-3");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getSTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY), policyInformationParticipantParticipantCoverageTab.getClass(), false);
        buttonAddCoverage.click();
        policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.PLAN).setValue(NC);
        policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.COVERAGE_NAME).setValue("STD Core - NC");

        assertSoftly(softly -> {
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.END_OF_SALARY_CONTINUATION)).isPresent().isDisabled().hasValue(VALUE_YES);
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.END_OF_ACCUMULATED_SICK_LEAVE)).isPresent().isDisabled().hasValue(VALUE_YES);
        });

        LOGGER.info("REN-23325: Step 4");
        policyInformationParticipantParticipantCoverageTab.submitTab();
        disabilityClaim.getDefaultWorkspace().fillFrom(disabilityClaim.getSTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY),
                policyInformationSponsorTab.getClass());

        disabilityClaim.claimOpen().perform();
        assertThat(labelClaimStatus).hasValue(OPEN);

        disabilityClaim.calculateSingleBenefitAmount().start(1);
        claim.calculateSingleBenefitAmount().getWorkspace().fillUpTo(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_STD"), OtherIncomeBenefitActionTab.class);

        OtherIncomeBenefitActionTab otherIncomeBenefitActionTab = claim.calculateSingleBenefitAmount().getWorkspace().getTab(OtherIncomeBenefitActionTab.class);

        assertThat(otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.TYPE_OF_OFFSET))
                .containsAllOptions(endOfSalaryContinuation, endOfAccumulatedSickLeave)
                .doesNotContainOption("Salary Continuation");

        LOGGER.info("REN-23325: Step 5");
        assertThat(OtherIncomeBenefitActionTab.talbeListOfOtherIncomeBenefit.getRow(ListOfOtherIncomeBenefitTable.TYPE_OF_OFFSET.getName(), endOfSalaryContinuation)).exists();

        OtherIncomeBenefitActionTab.talbeListOfOtherIncomeBenefit.getRow(ImmutableMap.of(ListOfOtherIncomeBenefitTable.TYPE_OF_OFFSET.getName(), endOfSalaryContinuation)).getCell(7).controls.links.get(CHANGE).click();

        assertSoftly(softly -> {
            softly.assertThat(otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.BEGINNING_DATE)).isRequired();
            softly.assertThat(otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.THROUGH_DATE)).isRequired();
        });

        LOGGER.info("REN-23325: Step 7");
        assertThat(OtherIncomeBenefitActionTab.talbeListOfOtherIncomeBenefit.getRow(ListOfOtherIncomeBenefitTable.TYPE_OF_OFFSET.getName(), endOfAccumulatedSickLeave)).exists();

        OtherIncomeBenefitActionTab.talbeListOfOtherIncomeBenefit.getRow(ImmutableMap.of(ListOfOtherIncomeBenefitTable.TYPE_OF_OFFSET.getName(), endOfAccumulatedSickLeave)).getCell(7).controls.links.get(CHANGE).click();
        assertSoftly(softly -> {
            softly.assertThat(otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.BEGINNING_DATE)).isRequired();
            softly.assertThat(otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.THROUGH_DATE)).isRequired();
        });

        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        String throughDateSalaryContinuation = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).format(DateTimeUtils.MM_DD_YYYY);
        String throughDateSalaryContinuationNew = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).plusDays(7).format(DateTimeUtils.MM_DD_YYYY);
        String throughDateAccumulatedSickLeave = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).plusDays(5).format(DateTimeUtils.MM_DD_YYYY);

        LOGGER.info("REN-23367: Step 2");
        OtherIncomeBenefitActionTab.talbeListOfOtherIncomeBenefit.getRow(ImmutableMap.of(ListOfOtherIncomeBenefitTable.TYPE_OF_OFFSET.getName(), endOfSalaryContinuation)).getCell(7).controls.links.get(CHANGE).click();
        fillOtherIncomeBenefit(currentDate, throughDateSalaryContinuation);

        LOGGER.info("REN-23367: Step 3");
        OtherIncomeBenefitActionTab.talbeListOfOtherIncomeBenefit.getRow(ImmutableMap.of(ListOfOtherIncomeBenefitTable.TYPE_OF_OFFSET.getName(), endOfAccumulatedSickLeave)).getCell(7).controls.links.get(CHANGE).click();
        fillOtherIncomeBenefit(currentDate, throughDateAccumulatedSickLeave);

        LOGGER.info("REN-23367: Step 4");
        Tab.buttonSaveAndExit.click();
        assertThat(SingleBenefitCalculationActionTab.eliminationQualificationPeriodTable.getRow(1).getCell(ELIMINATION_PERIOD_END_DATE.getName())).hasValue(throughDateAccumulatedSickLeave);

        LOGGER.info("REN-23367: Step 6");
        disabilityClaim.calculateSingleBenefitAmount().start(1);
        claim.calculateSingleBenefitAmount().getWorkspace().fillUpTo(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_STD"), OtherIncomeBenefitActionTab.class);

        OtherIncomeBenefitActionTab.talbeListOfOtherIncomeBenefit.getRow(ImmutableMap.of(ListOfOtherIncomeBenefitTable.TYPE_OF_OFFSET.getName(), endOfSalaryContinuation)).getCell(7).controls.links.get(CHANGE).click();
        fillOtherIncomeBenefit(currentDate, throughDateSalaryContinuationNew);

        OtherIncomeBenefitActionTab.talbeListOfOtherIncomeBenefit.getRow(ImmutableMap.of(ListOfOtherIncomeBenefitTable.TYPE_OF_OFFSET.getName(), endOfAccumulatedSickLeave)).getCell(7).controls.links.get(CHANGE).click();
        fillOtherIncomeBenefit(currentDate, throughDateAccumulatedSickLeave);

        Tab.buttonSaveAndExit.click();

        assertThat(SingleBenefitCalculationActionTab.eliminationQualificationPeriodTable.getRow(1).getCell(ELIMINATION_PERIOD_END_DATE.getName())).hasValue(throughDateSalaryContinuationNew);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23370", component = CLAIMS_GROUPBENEFITS)
    public void testClaimsValidateEliminationPeriodEndDateCalculationTC1() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel()),
                        new SimpleDataProvider().adjust(PolicyInformationParticipantParticipantCoverageTabMetaData.END_OF_SALARY_CONTINUATION.getLabel(), VALUE_YES))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]",
                        PlanDefinitionTabMetaData.BENEFIT_SCHEDULE.getLabel(), PolicyInformationParticipantParticipantCoverageTabMetaData.END_OF_ACCUMULATED_SICK_LEAVE.getLabel()), VALUE_NO));

        LOGGER.info("Test: Step 2");
        createDefaultShortTermDisabilityClaimForMasterPolicy();

        disabilityClaim.claimOpen().perform();
        assertThat(labelClaimStatus).hasValue(OPEN);

        disabilityClaim.calculateSingleBenefitAmount().start(1);
        claim.calculateSingleBenefitAmount().getWorkspace().fillUpTo(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_STD"), OtherIncomeBenefitActionTab.class);

        OtherIncomeBenefitActionTab otherIncomeBenefitActionTab = claim.calculateSingleBenefitAmount().getWorkspace().getTab(OtherIncomeBenefitActionTab.class);

        assertSoftly(softly -> {
            softly.assertThat(otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.TYPE_OF_OFFSET)).hasValue(endOfSalaryContinuation);
            softly.assertThat(otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.BEGINNING_DATE)).isRequired();
            softly.assertThat(otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.THROUGH_DATE)).isRequired();
        });

        LOGGER.info("Test: Step 3");
        otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.THROUGH_DATE)
                .setValue(TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).format(DateTimeUtils.MM_DD_YYYY));

        assertThat(otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.TYPE_OF_OFFSET)).doesNotContainOption(endOfAccumulatedSickLeave);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23379", component = CLAIMS_GROUPBENEFITS)
    public void testClaimsValidateEliminationPeriodEndDateCalculationTC2() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel()),
                        new SimpleDataProvider().adjust(PolicyInformationParticipantParticipantCoverageTabMetaData.END_OF_SALARY_CONTINUATION.getLabel(), VALUE_NO))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]",
                        PlanDefinitionTabMetaData.BENEFIT_SCHEDULE.getLabel(), PolicyInformationParticipantParticipantCoverageTabMetaData.END_OF_ACCUMULATED_SICK_LEAVE.getLabel()), VALUE_YES));

        LOGGER.info("Test: Step 2");
        createDefaultShortTermDisabilityClaimForMasterPolicy();

        disabilityClaim.claimOpen().perform();
        assertThat(labelClaimStatus).hasValue(OPEN);

        disabilityClaim.calculateSingleBenefitAmount().start(1);
        claim.calculateSingleBenefitAmount().getWorkspace().fillUpTo(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_STD"), OtherIncomeBenefitActionTab.class);

        OtherIncomeBenefitActionTab otherIncomeBenefitActionTab = claim.calculateSingleBenefitAmount().getWorkspace().getTab(OtherIncomeBenefitActionTab.class);

        assertSoftly(softly -> {
            softly.assertThat(otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.TYPE_OF_OFFSET)).hasValue(endOfAccumulatedSickLeave);
            softly.assertThat(otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.BEGINNING_DATE)).isRequired();
            softly.assertThat(otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.THROUGH_DATE)).isRequired();
        });

        LOGGER.info("Test: Step 3");
        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        String throughDateAccumulatedSickLeave = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).plusDays(5).format(DateTimeUtils.MM_DD_YYYY);
        String throughDateAccumulatedSickLeaveNew = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).plusDays(8).format(DateTimeUtils.MM_DD_YYYY);

        fillOtherIncomeBenefit(currentDate, throughDateAccumulatedSickLeave);

        assertThat(otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.TYPE_OF_OFFSET)).doesNotContainOption(endOfSalaryContinuation);

        LOGGER.info("Test: Step 4");
        Tab.buttonSaveAndExit.click();

        assertThat(SingleBenefitCalculationActionTab.eliminationQualificationPeriodTable.getRow(1).getCell(ELIMINATION_PERIOD_END_DATE.getName())).hasValue(throughDateAccumulatedSickLeave);

        LOGGER.info("Test: Step 6");
        claim.recalculateSingleBenefitAmount().start(1);
        NavigationPage.toLeftMenuTab(OTHER_INCOME_BENEFIT);
        otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.THROUGH_DATE).setValue(throughDateAccumulatedSickLeaveNew);
        Tab.buttonSaveAndExit.click();

        assertThat(SingleBenefitCalculationActionTab.eliminationQualificationPeriodTable.getRow(1).getCell(ELIMINATION_PERIOD_END_DATE.getName())).hasValue(throughDateAccumulatedSickLeaveNew);
    }

    private void fillOtherIncomeBenefit(String beginningDate, String throughDate) {
        OtherIncomeBenefitActionTab otherIncomeBenefitActionTab = claim.calculateSingleBenefitAmount().getWorkspace().getTab(OtherIncomeBenefitActionTab.class);
        otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.BEGINNING_DATE).setValue(beginningDate);
        otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.THROUGH_DATE).setValue(throughDate);
        otherIncomeBenefitActionTab.getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.PRORATING_RATE).setValue("index=1");
    }
}