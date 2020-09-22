package com.exigen.ren.modules.claim.gb_std.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.tabs.ClaimChangeDateOfLossActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.ClaimHandlingSpecialHandlingTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitCoverageEvaluationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.Tab.buttonSaveAndExit;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.FNOL;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.ValueConstants.*;
import static com.exigen.ren.main.modules.claim.common.metadata.ClaimChangeDateOfLossActionTabMetaData.DATE_OF_LOSS;
import static com.exigen.ren.main.modules.claim.common.metadata.ClaimHandlingSpecialHandlingTabMetaData.PRE_EXISTING;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageDeterminationTabMetaData.INSURED_PERSON_COVERAGE_EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.PRE_EXISTING_CONDITIONS;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.PRE_EXISTING_CONDITION_CONTINUOUSLY_INSURED_PERIOD;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableLossEvent;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimAddPreExistingConditionFlag extends ClaimGroupBenefitsSTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-32392", component = CLAIMS_GROUPBENEFITS)
    public void testClaimAddPreExistingConditionFlag() {
        mainApp().open();
        LocalDate currentDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        TestData tdPolicyDataGather = shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_AllPlans")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), currentDate.minusMonths(1).format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NY")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel()),
                        new SimpleDataProvider().adjust(PRE_EXISTING_CONDITIONS.getLabel(), INCLUDED))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]",
                        BENEFIT_SCHEDULE.getLabel(), PRE_EXISTING_CONDITION_CONTINUOUSLY_INSURED_PERIOD.getLabel()), "6 Months")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[2]", BENEFIT_SCHEDULE.getLabel()),
                        new SimpleDataProvider().adjust(PRE_EXISTING_CONDITIONS.getLabel(), INCLUDED))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[2]",
                        BENEFIT_SCHEDULE.getLabel(), PRE_EXISTING_CONDITION_CONTINUOUSLY_INSURED_PERIOD.getLabel()), "6 Months")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[3]", BENEFIT_SCHEDULE.getLabel()),
                        new SimpleDataProvider().adjust(PRE_EXISTING_CONDITIONS.getLabel(), INCLUDED))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[3]",
                        BENEFIT_SCHEDULE.getLabel(), PRE_EXISTING_CONDITION_CONTINUOUSLY_INSURED_PERIOD.getLabel()), "6 Months")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[4]", BENEFIT_SCHEDULE.getLabel()),
                        new SimpleDataProvider().adjust(PRE_EXISTING_CONDITIONS.getLabel(), INCLUDED))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[4]",
                        BENEFIT_SCHEDULE.getLabel(), PRE_EXISTING_CONDITION_CONTINUOUSLY_INSURED_PERIOD.getLabel()), "6 Months").resolveLinks();

        shortTermDisabilityMasterPolicy.createPolicy(tdPolicyDataGather
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));

        LOGGER.info("Test. Steps 1-4");
        disabilityClaim.initiate(disabilityClaim.getSTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        disabilityClaim.getDefaultWorkspace().fillUpTo(disabilityClaim.getSTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), INSURED_PERSON_COVERAGE_EFFECTIVE_DATE.getLabel()), currentDate.minusDays(1).format(MM_DD_YYYY)), ClaimHandlingSpecialHandlingTab.class);

        assertThat(claimHandlingSpecialHandlingTab.getAssetList().getAsset(PRE_EXISTING)).isPresent().isOptional().hasValue(VALUE_YES);

        disabilityClaim.getDefaultWorkspace().fillFrom(disabilityClaim.getSTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY), ClaimHandlingSpecialHandlingTab.class);

        assertThat(ClaimSummaryPage.labelSpecialHandling).hasValue(ClaimConstants.SpecialHandlingStatus.PRE_EXISTING);

        LOGGER.info("Test. Step 5");
        toSubTab(FNOL);
        claimHandlingSpecialHandlingTab.navigate().getAssetList().getAsset(PRE_EXISTING).setValue(VALUE_NO);
        buttonSaveAndExit.click();

        assertThat(tableLossEvent).isPresent();

        toSubTab(FNOL);
        benefitCoverageDeterminationTab.navigateToTab().getAssetList().getAsset(INSURED_PERSON_COVERAGE_EFFECTIVE_DATE).setValue(currentDate.format(MM_DD_YYYY));
        claimHandlingSpecialHandlingTab.navigate().getAssetList().getAsset(PRE_EXISTING).setValue(VALUE_YES);
        buttonSaveAndExit.click();

        assertThat(ClaimSummaryPage.labelSpecialHandling).hasValue(ClaimConstants.SpecialHandlingStatus.PRE_EXISTING);

        LOGGER.info("Test. Step 6");
        toSubTab(FNOL);
        benefitCoverageDeterminationTab.navigateToTab().getAssetList().getAsset(INSURED_PERSON_COVERAGE_EFFECTIVE_DATE).setValue(currentDate.plusDays(1).format(MM_DD_YYYY));
        claimHandlingSpecialHandlingTab.navigate().getAssetList().getAsset(PRE_EXISTING).setValue(VALUE_NO);
        buttonSaveAndExit.click();

        assertThat(ClaimSummaryPage.labelSpecialHandling).hasValue(EMPTY);

        LOGGER.info("Test. Step 7");
        disabilityClaim.changeDateOfLossAction().perform(disabilityClaim.getSTDTestData().getTestData("ClaimChangeDateOfLoss", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(ClaimChangeDateOfLossActionTab.class.getSimpleName(), DATE_OF_LOSS.getLabel()), currentDate.plusMonths(6).format(MM_DD_YYYY)));

        assertThat(ClaimSummaryPage.labelSpecialHandling).hasValue(ClaimConstants.SpecialHandlingStatus.PRE_EXISTING);

        LOGGER.info("Test. Step 8");
        claim.updateBenefit().start(1);
        claim.updateBenefit().getWorkspace().getTab(BenefitCoverageEvaluationTab.class).navigateToTab();

        benefitCoverageDeterminationTab.getAssetList().getAsset(INSURED_PERSON_COVERAGE_EFFECTIVE_DATE).setValue(currentDate.format(MM_DD_YYYY));
        buttonSaveAndExit.click();
        toSubTab(OVERVIEW);

        assertThat(ClaimSummaryPage.labelSpecialHandling).hasValue(EMPTY);
    }
}