package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PremiumSummaryTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableMap;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.pages.ErrorPage.TableError.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.*;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PremiumSummaryTabMetaData.APPLIED_RATING_CENSUS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PlanDefinitionTab.tableCoverageDefinition;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestTotalNumberOfEligibleLivesRulesVerification extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private static final String TOTAL_NUMBER_IS_REQUIRED_ERROR_MESSAGE = "'Total Number of Eligible Lives' is required";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42579", component = POLICY_GROUPBENEFITS)
    public void testTotalNumberOfEligibleLivesRulesVerification() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        LOGGER.info("TEST: Step 1");
        initiateQuoteAndFillToTab(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "FL")
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel())), PlanDefinitionTab.class, false);

        LOGGER.info("TEST: Step 2");
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(CON, NC, SGR, VOL));

        LOGGER.info("TEST: Step 3");
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE)).hasValue("Eligible");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).isEnabled().isRequired().hasValue(EMPTY);
        });

        LOGGER.info("TEST: Step 4");
        planDefinitionTab.submitTab();
        assertThat(planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).hasWarningWithText(TOTAL_NUMBER_IS_REQUIRED_ERROR_MESSAGE);

        LOGGER.info("TEST: Step 7");
        fillPlanDefinitionTabForPlan("TestData_CON", "Eligible", EMPTY);

        LOGGER.info("TEST: Step 8");
        fillPlanDefinitionTabForPlan(DEFAULT_TEST_DATA_KEY, "Eligible", "10");

        LOGGER.info("TEST: Step 9");
        fillPlanDefinitionTabForPlan("TestData_SGR", "Enrolled", EMPTY);

        LOGGER.info("TEST: Step 10");
        fillPlanDefinitionTabForPlan("TestData_VOL", "Enrolled", "10");

        LOGGER.info("TEST: Steps 11-12");
        planDefinitionTab.submitTab();
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(longTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_AllPlans"),
                classificationManagementMpTab.getClass(), premiumSummaryTab.getClass(), true);

        LOGGER.info("TEST: Step 13");
        planDefinitionTab.navigateToTab();

        ImmutableMap.of("CON-CON", "6", "NC-NC", "6", "SGR-SGR", EMPTY, "VOL-VOL", "10").forEach((plan, value) -> {
            tableCoverageDefinition.getRow(PolicyConstants.PolicyPlanTable.PLAN, plan).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
            assertThat(planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).isEnabled().isRequired().hasValue(value);

            LOGGER.info("TEST: Step 14");
            planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue(EMPTY);
        });

        premiumSummaryTab.navigateToTab();
        assertThat(APPLIED_RATING_CENSUS).isPresent();

        LOGGER.info("TEST: Step 15");
        PremiumSummaryTab.buttonRate.click();

        assertThat(ErrorPage.tableError)
                .with(CODE, "REN_LTD0002m")
                .with(SEVERITY, "Error")
                .with(MESSAGE, TOTAL_NUMBER_IS_REQUIRED_ERROR_MESSAGE)
                .hasMatchingRows(4);

        LOGGER.info("TEST: Step 16");
        ErrorPage.buttonBack.click();
        planDefinitionTab.navigateToTab();
        ImmutableList.of("CON-CON", "NC-NC", "SGR-SGR", "VOL-VOL").forEach((plan) -> {
            tableCoverageDefinition.getRow(PolicyConstants.PolicyPlanTable.PLAN, plan).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
            planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("31");
        });
        premiumSummaryTab.navigateToTab();
        LOGGER.info("TEST: Step 17");
        premiumSummaryTab.getAssetList().getAsset(PremiumSummaryTabMetaData.REMOVE).click();

        assertThat(APPLIED_RATING_CENSUS).isAbsent();

        LOGGER.info("TEST: Step 18");
        planDefinitionTab.navigateToTab();

        ImmutableMap.of("CON-CON", EMPTY, "NC-NC", EMPTY, "SGR-SGR", "31", "VOL-VOL", "31").forEach((plan, value) -> {
            tableCoverageDefinition.getRow(PolicyConstants.PolicyPlanTable.PLAN, plan).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
            assertThat(planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).isEnabled().isRequired().hasValue(value);
        });

        LOGGER.info("TEST: Step 19");
        premiumSummaryTab.navigateToTab();
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillFrom(longTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_AllPlans"), premiumSummaryTab.getClass());

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
    }

    private void fillPlanDefinitionTabForPlan(String testDatakey, String censusTypeValue, String totalNumberValue) {
        planDefinitionTab.fillTab(longTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, testDatakey)
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", CENSUS_TYPE.getLabel()), censusTypeValue)
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel()), totalNumberValue)
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[0]", PLAN.getLabel())));
    }
}