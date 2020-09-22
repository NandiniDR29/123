package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.ErrorConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.BenefitType.SPECIFIED_AMOUNT_SINGLE;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.*;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab.tableCoverageDefinition;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPlanCoveragesConfigureAdditionalThreePlans extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {
    private MultiAssetList planDefinitionTabAssetList;

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20218", "REN-20241"}, component = POLICY_GROUPBENEFITS)

    public void testPlanCoveragesConfigureAdditionalThreePlans() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        planDefinitionTabAssetList = (MultiAssetList) planDefinitionTab.getAssetList();

        assertSoftly(softly -> {
            termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PolicyInformationTab.class);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(SITUS_STATE)).isPresent().isEnabled().isRequired();
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("AK");
            termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultTLMasterPolicyData().mask(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), COUNTY_CODE.getLabel())), PolicyInformationTab.class, PlanDefinitionTab.class);

            LOGGER.info("REN-20218 Step 1 , step 2");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASIC_LIFE_PLAN));
            ImmutableList.of(BTL, ADD, DEP_BTL).forEach(coverage1 -> {
                softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), coverage1)).isPresent();
                PlanDefinitionTab.changeCoverageTo(coverage1);
            });
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ADD_COVERAGE)).isPresent().isEnabled();
            softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), BTL).getCell(7).controls.links.get(ActionConstants.CHANGE)).isPresent();
            softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), ADD).getCell(7).controls.links.get(ActionConstants.CHANGE)).isPresent();
            softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), ADD).getCell(7).controls.links.get(ActionConstants.REMOVE)).isPresent();
            softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), DEP_BTL).getCell(7).controls.links.get(ActionConstants.CHANGE)).isPresent();
            softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), DEP_BTL).getCell(7).controls.links.get(ActionConstants.REMOVE)).isPresent();

            LOGGER.info("REN-20218 Step 3");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ADD_COVERAGE).click();
            planDefinitionTab.getAssetList().getAsset(PLAN_COMBOBOX).setValue(BASIC_LIFE_PLAN);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_NAME)).hasOptions("", DEP_ADD, VOL_BTL);

            LOGGER.info("REN-20218 Step 4");
            planDefinitionTab.getAssetList().getAsset(COVERAGE_NAME).setValue(DEP_ADD);
            ImmutableList.of(BTL, ADD, DEP_BTL, DEP_ADD).forEach(coverage2 -> {
                softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), coverage2)).isPresent();
            });
            softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), BTL).getCell(7).controls.links.get(ActionConstants.CHANGE)).isPresent();
            softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), ADD).getCell(7).controls.links.get(ActionConstants.CHANGE)).isPresent();
            softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), DEP_BTL).getCell(7).controls.links.get(ActionConstants.CHANGE)).isPresent();
            softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), DEP_BTL).getCell(7).controls.links.get(ActionConstants.REMOVE)).isPresent();
            softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), DEP_ADD).getCell(7).controls.links.get(ActionConstants.CHANGE)).isPresent();
            softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), DEP_ADD).getCell(7).controls.links.get(ActionConstants.REMOVE)).isPresent();

            PlanDefinitionTab.changeCoverageTo(BTL);
            softly.assertThat(planDefinitionTab.tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), BTL)).isPresent();
            softly.assertThat(tableCoverageDefinition.getRow(ImmutableMap.of(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), BTL)).getCell(7).controls.links.get(ActionConstants.CHANGE)).isPresent();

            LOGGER.info("REN-20218 Step 5");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(VOLUNTARY_LIFE_PLAN));
            verifyCoverages(ImmutableList.of(VOL_BTL, VOL_ADD, SP_VOL_BTL, DEP_VOL_BTL, DEP_VOL_ADD));

            LOGGER.info("REN-20218 Step 8");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASIC_LIFE_PLAN_PLUS_VOLUNTARY));
            verifyCoverages(ImmutableList.of(BTL, ADD, DEP_BTL, VOL_BTL, VOL_ADD, SP_VOL_BTL, DEP_VOL_BTL, DEP_VOL_ADD));

            LOGGER.info("REN-20218 Step 10");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("NY");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(SITUS_STATE)).hasWarningWithText(ErrorConstants.ErrorMessages.SITUS_STATE_ERROR_MESSAGE);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASIC_LIFE_PLAN, BASIC_LIFE_PLAN_PLUS_VOLUNTARY, VOLUNTARY_LIFE_PLAN, VOLUNTEER_FIRE_FIGHTERS));
            softly.assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.PLAN)).hasValue(ImmutableList.of(BASIC_LIFE_PLAN, BASIC_LIFE_PLAN_PLUS_VOLUNTARY, VOLUNTARY_LIFE_PLAN, VOLUNTEER_FIRE_FIGHTERS)).isPresent();

            LOGGER.info("REN-20218 Step 11");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(VOLUNTEER_FIRE_FIGHTERS));
            ImmutableList.of(ADD, BTL).forEach(coverage3 -> {
                softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), coverage3)).isPresent();
                PlanDefinitionTab.changeCoverageTo(coverage3);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ADD_COVERAGE)).isAbsent();
            });

            LOGGER.info("REN-20241 Step 1 and Step 2");
            planDefinitionTab.getAssetList().getAsset(PLAN).buttonOpenPopup.click();
            planDefinitionTab.getAssetList().getAsset(PLAN).removeValue(ImmutableList.of(VOLUNTEER_FIRE_FIGHTERS));
            planDefinitionTab.getAssetList().getAsset(PLAN).buttonSearch.click();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN).getAvailableItems()).hasSameElementsAs(ImmutableList.of(BASIC_LIFE_PLAN, BASIC_LIFE_PLAN_PLUS_VOLUNTARY, VOLUNTARY_LIFE_PLAN, VOLUNTEER_FIRE_FIGHTERS));
            planDefinitionTab.getAssetList().getAsset(PLAN).buttonCancel.click();
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(VOLUNTEER_FIRE_FIGHTERS));
            ImmutableList.of(ADD, BTL).forEach(coverage4 -> {
                softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), coverage4)).isPresent();
            });

            LOGGER.info("REN-20241 Step 3");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_TYPE)).isPresent().isRequired().hasOptions(SPECIFIED_AMOUNT_SINGLE);
            planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_TYPE).setValue(SPECIFIED_AMOUNT_SINGLE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.FLAT_BENEFIT_AMOUNT)).isPresent().isRequired();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.LINE_OF_DUTY_BENEFIT)).isPresent().hasValue("").hasOptions("", "Standard", "Extended");
            planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.LINE_OF_DUTY_BENEFIT).setValue("Standard");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ADD_COVERAGE)).isAbsent();

            LOGGER.info("REN-20241 Step 5");
            PlanDefinitionTab.changeCoverageTo(ADD);
            softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), ADD)).isPresent();

            LOGGER.info("REN-20241 Step 6");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_TYPE)).isPresent().isRequired().hasOptions(SPECIFIED_AMOUNT_SINGLE);
            planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_TYPE).setValue(SPECIFIED_AMOUNT_SINGLE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.FLAT_BENEFIT_AMOUNT)).isPresent();

            LOGGER.info("REN-20241 Step 9");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("NV");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(SITUS_STATE)).hasWarningWithText(ErrorConstants.ErrorMessages.SITUS_STATE_ERROR_MESSAGE);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PLAN).buttonOpenPopup.click();
            planDefinitionTab.getAssetList().getAsset(PLAN).buttonSearch.click();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN).getAvailableItems()).hasSameElementsAs(ImmutableList.of(BASIC_LIFE_PLAN, BASIC_LIFE_PLAN_PLUS_VOLUNTARY, VOLUNTARY_LIFE_PLAN));

            LOGGER.info("REN-20241 Step 10");
            planDefinitionTab.getAssetList().getAsset(PLAN).buttonCancel.click();
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASIC_LIFE_PLAN));
            PlanDefinitionTab.changeCoverageTo(BTL);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.BENEFIT_TYPE)).isPresent().isRequired();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.FLAT_BENEFIT_AMOUNT)).isPresent().isRequired();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BenefitScheduleMetaData.LINE_OF_DUTY_BENEFIT)).isAbsent();
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("NY");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(SITUS_STATE)).hasWarningWithText(ErrorConstants.ErrorMessages.SITUS_STATE_ERROR_MESSAGE);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN).getValue()).isEmpty();
        });
    }

    private void verifyCoverages(ImmutableList<String> fieldsList) {
        assertSoftly(softly -> {
            fieldsList.forEach(coverage ->
                    softly.assertThat(planDefinitionTab.tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), coverage)).isPresent());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ADD_COVERAGE)).isPresent().isEnabled();
        });
    }
}