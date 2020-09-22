package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.AbstractEditableStringElement;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.master.IssueActionTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ActionConstants.CHANGE;
import static com.exigen.ren.main.enums.PolicyConstants.PlanLTD.LTD_CON;
import static com.exigen.ren.main.enums.PolicyConstants.PlanLTD.LTD_NC;
import static com.exigen.ren.main.enums.TableConstants.CoverageDefinition.PLAN;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.COVERAGE_NAME;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.ELIGIBILITY;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPlanDefinitionAdditionalRulesForEligibilityAttributes extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private MultiAssetList planDefinitionTabAssetList;
    private static final String PLAN_TEST_NAME = "PlanTestName";
    private static final String ERROR_PATTERN = "'%s' is required";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18025", "REN-18167"}, component = POLICY_GROUPBENEFITS)
    public void testPlanDefinitionAdditionalRulesForEligibilityAttributes() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_TwoPlans"), planDefinitionTab.getClass());

        assertSoftly(softly -> {
            LOGGER.info("REN-18025 Step 1, Step 2 and REN-18167 Step 1, Step 2");
            planDefinitionTabAssetList = (MultiAssetList) planDefinitionTab.getAssetList();
            planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(LTD_NC, LTD_CON));

            PlanDefinitionTab.openAddedPlan(PolicyConstants.PlanLTD.LTD_NC);
            softly.assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.PLAN_NAME)).isPresent().isEnabled();
            ImmutableList.of(MINIMUM_HOURLY_REQUIREMENT, ELIGIBILITY_WAITING_PERIOD_DEFINITION, WAITING_PERIOD, WAITING_PERIOD_MODE).forEach(eligibility -> {
                softly.assertThat((AbstractEditableStringElement)planDefinitionTabAssetList.getAsset(ELIGIBILITY).getAsset(eligibility)).isPresent().isOptional().hasValue(StringUtils.EMPTY);
            });
            softly.assertThat(planDefinitionTabAssetList.getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES)).isPresent().isOptional().hasValue(VALUE_YES);

            LOGGER.info("REN-18025 Step 3, Step 4 and REN-18167 Step 3, Step 4");
            PlanDefinitionTab.openAddedPlan(LTD_CON);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN_NAME)).isPresent().isEnabled();

            ImmutableList.of(MINIMUM_HOURLY_REQUIREMENT, ELIGIBILITY_WAITING_PERIOD_DEFINITION, WAITING_PERIOD, WAITING_PERIOD_MODE).forEach(eligibility -> {
                softly.assertThat((AbstractEditableStringElement)planDefinitionTabAssetList.getAsset(ELIGIBILITY).getAsset(eligibility)).isPresent().isOptional().hasValue(StringUtils.EMPTY);
            });
            softly.assertThat(planDefinitionTabAssetList.getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES)).isPresent().isOptional().hasValue(VALUE_YES);
        });

        LOGGER.info("REN-18025 fill mandatory fields, fill Eligibility section for CON plan, leave Eligibility section empty for NC plan");
        planDefinitionTab.fillTab(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_TwoPlans")
                .mask(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[1]", PlanDefinitionTabMetaData.ELIGIBILITY.getLabel()))
                .mask(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[2]", PlanDefinitionTabMetaData.ELIGIBILITY.getLabel())));

        PlanDefinitionTab.openAddedPlan(LTD_CON);
        planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.PLAN_NAME).setValue(PLAN_TEST_NAME);
        planDefinitionTabAssetList.getAsset(ELIGIBILITY).fill(tdSpecific());
        planDefinitionTab.submitTab();

        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillFrom(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_TwoPlans"), classificationManagementMpTab.getClass());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("REN-18025 Step 5, Step 6 and REN-18167 Step 6, Step 7");

        longTermDisabilityMasterPolicy.propose().perform(getDefaultLTDMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);
        longTermDisabilityMasterPolicy.acceptContract().perform(getDefaultLTDMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        LOGGER.info("REN-18025 Step 7, Step 8 and REN-18167 Step 8, Step 9");

        longTermDisabilityMasterPolicy.issue().start();
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        assertThat(PlanDefinitionTab.tableCoverageDefinition).hasRowsThatContain(PLAN.getName(), PLAN_TEST_NAME);
        PlanDefinitionTab.tableCoverageDefinition.getRowContains(PLAN.getName(), PLAN_TEST_NAME).getCell(7).controls.links.get(CHANGE).click();

        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.PLAN_COMBOBOX)).isPresent().isDisabled();
            softly.assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.PLAN_NAME)).isPresent().isDisabled().hasValue(PLAN_TEST_NAME);
            softly.assertThat(planDefinitionTabAssetList.getAsset(COVERAGE_NAME)).isPresent().isDisabled();
            softly.assertThat(planDefinitionTabAssetList.getAsset(ELIGIBILITY)).hasPartialValue(tdSpecific().getTestData(ELIGIBILITY.getLabel()));

            PlanDefinitionTab.openAddedPlan(PolicyConstants.PlanLTD.LTD_NC);
            softly.assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.PLAN_NAME)).isPresent().isDisabled();
            softly.assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.PLAN_COMBOBOX)).isPresent().isDisabled();
            ImmutableList.of(MINIMUM_HOURLY_REQUIREMENT, ELIGIBILITY_WAITING_PERIOD_DEFINITION, WAITING_PERIOD, WAITING_PERIOD_MODE).forEach(eligibility -> {
                softly.assertThat((AbstractEditableStringElement)planDefinitionTabAssetList.getAsset(ELIGIBILITY).getAsset(eligibility)).isPresent().isRequired().hasValue(StringUtils.EMPTY);
            });
            softly.assertThat(planDefinitionTabAssetList.getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES)).isPresent().isRequired().hasValue(VALUE_YES);

            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.ISSUE.get());
            longTermDisabilityMasterPolicy.issue().getWorkspace().getTab(IssueActionTab.class).fillTab(getDefaultLTDMasterPolicyData());
            longTermDisabilityMasterPolicy.issue().getWorkspace().getTab(IssueActionTab.class).submitTab();

            ImmutableList.of(MINIMUM_HOURLY_REQUIREMENT, ELIGIBILITY_WAITING_PERIOD_DEFINITION, WAITING_PERIOD, WAITING_PERIOD_MODE).forEach(eligibility -> {
                softly.assertThat(ErrorPage.tableError).with(ErrorPage.TableError.MESSAGE, String.format(ERROR_PATTERN, eligibility.getLabel())).hasMatchingRows(1);
            });
        });
    }
}