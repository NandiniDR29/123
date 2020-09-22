package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PremiumSummaryTabMetaData.APPLY;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PremiumSummaryTabMetaData.SELECT_RATING_CENSUS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPlanDefinitionAdditionalRulesForEligibilityAttributes extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18604", "REN-18801"}, component = POLICY_GROUPBENEFITS)

    public void testPlanDefinitionAdditionalRulesForEligibilityAttributes() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.initiate(getDefaultSTDMasterPolicyData());
        shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTDMasterPolicyData(), planDefinitionTab.getClass());
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(CON, NC, SGR, VOL));
        assertSoftly(softly -> {
            PlanDefinitionTab.tableCoverageDefinition.getRows().forEach(tablerow -> {
                tablerow.getCell(7).controls.links.get(ActionConstants.CHANGE).click();
                LOGGER.info("Test REN-18604 Step 2 and Step 3");
                ImmutableList.of(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK, WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEE, ELIGIBILITY_WAITING_PERIOD_DEFINITION, WAITING_PERIOD_DEFINITION, WAITING_PERIOD_MODE_DEFINITION).forEach(control ->
                        softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(control)).isPresent().isOptional());
            });
            LOGGER.info("Test REN-18604 Step 4-Step 9");
            shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_AllPlans"), planDefinitionTab.getClass(), premiumSummaryTab.getClass());
            premiumSummaryTab.getAssetList().getAsset(SELECT_RATING_CENSUS).setValueByIndex(1);
            premiumSummaryTab.getAssetList().getAsset(APPLY).click();
            premiumSummaryTab.submitTab();

            shortTermDisabilityMasterPolicy.propose().perform(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY));
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);
            shortTermDisabilityMasterPolicy.acceptContract().perform(getDefaultSTDMasterPolicyData());
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);
            shortTermDisabilityMasterPolicy.issue().start();
            NavigationPage.toLeftMenuTab(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            LOGGER.info("Test REN-18801 Step 2");
            softly.assertThat(planDefinitionTab.tableCoverageDefinition.getHeader().getValue().subList(1, 6)).hasSameElementsAs(Stream.of(TableConstants.CoverageDefinition.values())
                    .map(TableConstants.CoverageDefinition::getName).collect(Collectors.toList()));
            LOGGER.info("Test REN-18801 Step 5");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME)).isPresent().isDisabled();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN_COMBOBOX)).isPresent().isDisabled();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN_NAME)).isPresent().isDisabled();
            LOGGER.info("Test REN-18801 Step 6");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY)).isPresent();
            LOGGER.info("Test REN-18801 Step 7");
            ImmutableList.of(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK, WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEE, ELIGIBILITY_WAITING_PERIOD_DEFINITION, WAITING_PERIOD_DEFINITION, WAITING_PERIOD_MODE_DEFINITION).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(control)).isPresent().isRequired());
        });
    }
}
