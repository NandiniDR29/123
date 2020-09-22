package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.*;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.PlanGenericInfoTab.*;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupAccidentCoverages.BASIC_ACCIDENT;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupAccidentCoverages.ENHANCED_ACCIDENT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.BASE_BUY_UP;
import static com.exigen.ren.main.enums.TableConstants.CoverageDefinition.*;
import static com.exigen.ren.main.enums.TableConstants.Plans.COVERAGE_NAME;
import static com.exigen.ren.main.enums.TableConstants.Plans.COVERAGE_TIERS;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.ADD_COVERAGE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyCoveragesSubTabsConfiguration extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    private static TestData tdDataGatherTestData;
    private static String NO_PLAN_ERROR_MESSAGE = "At least one plan should be selected";
    private static ImmutableMap tabMapWithBasic = ImmutableMap.builder()
            .put(BASIC_BENEFITS, BasicBenefitsTab.planTable)
            .put(ENHANCED_BENEFITS_A_TO_C, EnhancedBenefitsAtoCTab.planTable)
            .put(ENHANCED_BENEFITS_D_TO_F, EnhancedBenefitsDtoFTab.planTable)
            .put(ENHANCED_BENEFITS_H_TO_L, EnhancedBenefitsHtoLTab.planTable)
            .put(ENHANCED_BENEFITS_M_TO_T, EnhancedBenefitsMtoTTab.planTable)
            .put(OPTIONAL_BENEFITS, OptionalBenefitTab.tablePlans).build();
    private static ImmutableMap tabMapWithoutBasic = ImmutableMap.builder()
            .put(ENHANCED_BENEFITS_A_TO_C, EnhancedBenefitsAtoCTab.planTable)
            .put(ENHANCED_BENEFITS_D_TO_F, EnhancedBenefitsDtoFTab.planTable)
            .put(ENHANCED_BENEFITS_H_TO_L, EnhancedBenefitsHtoLTab.planTable)
            .put(ENHANCED_BENEFITS_M_TO_T, EnhancedBenefitsMtoTTab.planTable)
            .put(OPTIONAL_BENEFITS, OptionalBenefitTab.tablePlans).build();

    private static ImmutableMap treeTabMapBasic = ImmutableMap.of(
            BASIC_BENEFITS, BasicBenefitsTab.planTable,
            OPTIONAL_BENEFITS, OptionalBenefitTab.tablePlans);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-14762", component = POLICY_GROUPBENEFITS)
    public void testPolicyCoveragesSubTabsConfiguration() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        tdDataGatherTestData = groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY);
        groupAccidentMasterPolicy.initiate(tdDataGatherTestData);
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(tdDataGatherTestData, PlanDefinitionTab.class);

        LOGGER.info("Test REN-14762 TC1 Step 1");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(BASE_BUY_UP));


        LOGGER.info("Test REN-14762 TC1 Step 2-4");
        verifyPlanGenericInfoSubTabs(tabMapWithoutBasic.keySet());
        verifyPlanSelectionTable(tabMapWithoutBasic);
        LOGGER.info("Test REN-14762 TC2 Step 1");
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().fill(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestDataWithBasicBenefitsTab"));
        PlanDefinitionTab.tableCoverageDefinition.getRow(COVERAGE_NAME.getName(), ENHANCED_ACCIDENT).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
        LOGGER.info("Test REN-14762 TC2 Step 2-4");
        verifyPlanGenericInfoSubTabs(tabMapWithBasic.keySet());
        assertThat(planDefinitionTab.getAssetList().getAsset(ADD_COVERAGE)).isAbsent();
        assertSoftly(softly -> tabMapWithBasic.forEach((treeTab, table) -> {
            NavigationPage.toLeftMenuTab(((NavigationEnum.PlanGenericInfoTab) treeTab).get());
            ImmutableMap.of(TableConstants.CoverageDefinition.COVERAGE_NAME, 2, PLAN, 3, COVERAGE_TIERS, 4, CONTRIBUTION_TYPE, 5, RATE_BASIS, 6).forEach((column, index) ->
                    softly.assertThat(((TableExtended) table).getColumn(column.getName()).getIndex()).isEqualTo(index));
        }));
        LOGGER.info("Test REN-14762 TC3 Step 1");
        planDefinitionTab.navigateToTab();
        PlanDefinitionTab.tableCoverageDefinition.getRow(COVERAGE_NAME.getName(), BASIC_ACCIDENT).getCell(7).controls.links.get(ActionConstants.CHANGE).click();

        LOGGER.info("Test REN-14762 TC3 Step 2");
        verifyPlanGenericInfoSubTabs(treeTabMapBasic.keySet());
        assertSoftly(softly ->
                ImmutableList.of(PLAN_DEFINITION, ENHANCED_BENEFITS_A_TO_C, ENHANCED_BENEFITS_D_TO_F, ENHANCED_BENEFITS_H_TO_L, ENHANCED_BENEFITS_M_TO_T).forEach(control ->
                        softly.assertThat(NavigationPage.isLeftMenuPresent(control.get())).isTrue()));

        LOGGER.info("Test REN-14762 TC3 Step 4");
        verifyPlanSelectionTable(treeTabMapBasic);

        LOGGER.info("Test REN-14762 TC5 Step 1");
        Tab.buttonSaveAndExit.click();
        groupAccidentMasterPolicy.initiate(tdDataGatherTestData);
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(tdDataGatherTestData, planDefinitionTab.getClass());
        assertSoftly(softly -> {
            ImmutableList.of(PLAN_DEFINITION)
                    .forEach(control ->
                            softly.assertThat(NavigationPage.isLeftMenuPresent(control.get())).isTrue());
            ImmutableList.of(BASIC_BENEFITS, OPTIONAL_BENEFITS, ENHANCED_BENEFITS_A_TO_C, ENHANCED_BENEFITS_D_TO_F, ENHANCED_BENEFITS_H_TO_L, ENHANCED_BENEFITS_M_TO_T)
                    .forEach(control ->
                            softly.assertThat(NavigationPage.isLeftMenuPresent(control.get())).isFalse());
        });
        LOGGER.info("Test REN-14762 TC5 Step 2");
        Tab.buttonNext.click();
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(NO_PLAN_ERROR_MESSAGE))).isPresent();
    }

    private void verifyPlanGenericInfoSubTabs(ImmutableSet<NavigationEnum.PlanGenericInfoTab> tabs) {
        assertSoftly(softly -> tabs.forEach(control ->
                softly.assertThat(NavigationPage.isLeftMenuPresent(control.get())).isTrue()));
    }

    private void verifyPlanSelectionTable(ImmutableMap map) {
        assertSoftly(softly -> {
            ImmutableMap.of(TableConstants.CoverageDefinition.COVERAGE_NAME, 2, PLAN, 3, COVERAGE_TIERS, 4, CONTRIBUTION_TYPE, 5, RATE_BASIS, 6).forEach((column, index) ->
                    softly.assertThat(PlanDefinitionTab.tableCoverageDefinition.getColumn(column.getName()).getIndex()).isEqualTo(index));
            if (PlanDefinitionTab.tableCoverageDefinition.getRow(PLAN.getName(), "null-null").isPresent()) {
                PlanDefinitionTab.tableCoverageDefinition.getRow(PLAN.getName(), "null-null").getCell(7).controls.links.get(ActionConstants.REMOVE).click();
                Page.dialogConfirmation.confirm();
            }
            map.forEach((treeTab, table) -> {
                NavigationPage.toLeftMenuTab(((NavigationEnum.PlanGenericInfoTab) treeTab).get());
                ImmutableMap.of(TableConstants.CoverageDefinition.COVERAGE_NAME, 2, PLAN, 3, COVERAGE_TIERS, 4, CONTRIBUTION_TYPE, 5, RATE_BASIS, 6).forEach((column, index) ->
                        softly.assertThat(((TableExtended) table).getColumn(column.getName()).getIndex()).isEqualTo(index));
            });
        });
    }
}
