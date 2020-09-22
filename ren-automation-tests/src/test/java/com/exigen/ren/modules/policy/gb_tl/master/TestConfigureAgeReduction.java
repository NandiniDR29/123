package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.AgeReductionScheduleDetailMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab.tableAgeReductionScheduleDetail;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;


public class TestConfigureAgeReduction extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    private MultiAssetList ageReductionScheduleDetail;

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-13899", "REN-14705", "REN-13903"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureAgeReduction() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PlanDefinitionTab.class);
        planDefinitionTab.selectDefaultPlan();
        fieldValidations();
        ageReductionScheduleDetails();
    }

    private void fieldValidations() {
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION)).isPresent();
            LOGGER.info("REN 13899 Step 3");
            planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION_CHECKBOX).setValue(false);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION_SCHEDULE)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CENSUS_VOLUME_REDUCED)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN_TERMINATION_ATTAINED_AGE)).isAbsent();
            LOGGER.info("REN 13899 Step 1");
            LOGGER.info("REN 13903 Step 1");
            planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION_CHECKBOX).setValue(true);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION_SCHEDULE)).isPresent().hasOptions("50%@70", "ADEA1", "ADEA2", "65%@65 50%@70", "50%@70 35%@75", "CUSTOM").hasValue("65%@65 50%@70");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CENSUS_VOLUME_REDUCED)).isPresent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN_TERMINATION_ATTAINED_AGE)).isPresent();
            softly.assertThat(tableAgeReductionScheduleDetail.getColumn("Age")).hasValue(ImmutableList.of("65", "70"));
            softly.assertThat(tableAgeReductionScheduleDetail.getColumn("Reduced To %")).hasValue(ImmutableList.of("65%", "50%"));
            softly.assertThat(tableAgeReductionScheduleDetail.getColumn("Maximum")).hasValue(ImmutableList.of("", ""));
            softly.assertThat(tableAgeReductionScheduleDetail.getRow(1).getCell(5).controls.links.get(ActionConstants.CHANGE)).isPresent();
            softly.assertThat(tableAgeReductionScheduleDetail.getRow(2).getCell(5).controls.links.get(ActionConstants.CHANGE)).isPresent();
            planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION_SCHEDULE).setValue("ADEA2");
            softly.assertThat(tableAgeReductionScheduleDetail.getColumn("Age")).hasValue(ImmutableList.of("70", "75", "80"));
            softly.assertThat(tableAgeReductionScheduleDetail.getColumn("Reduced To %")).hasValue(ImmutableList.of("55%", "35%", "25%"));
            softly.assertThat(tableAgeReductionScheduleDetail.getColumn("Maximum")).hasValue(ImmutableList.of("", "", ""));
            softly.assertThat(tableAgeReductionScheduleDetail.getRow(1).getCell(5).controls.links.get(ActionConstants.CHANGE)).isPresent();
            softly.assertThat(tableAgeReductionScheduleDetail.getRow(2).getCell(5).controls.links.get(ActionConstants.CHANGE)).isPresent();
            softly.assertThat(tableAgeReductionScheduleDetail.getRow(3).getCell(5).controls.links.get(ActionConstants.CHANGE)).isPresent();
        });
    }

    private void ageReductionScheduleDetails() {
        assertSoftly(softly -> {
            LOGGER.info("REN 14705 Step 1-5");
            LOGGER.info("REN 13903 Step 1");
            ageReductionScheduleDetail = planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION_SCHEDULE_DETAILS);
            softly.assertThat(ageReductionScheduleDetail).isPresent();
            tableAgeReductionScheduleDetail.getRow(3).getCell(5).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(ageReductionScheduleDetail.getAsset(AGE_DROP_DOWN)).isDisabled();
            softly.assertThat(ageReductionScheduleDetail.getAsset(REDUCED_TO_PERCENTAGE)).isDisabled();
            softly.assertThat(ageReductionScheduleDetail.getAsset(MAXIMUM_TEXTBOX)).isPresent().isEnabled();
            softly.assertThat(ageReductionScheduleDetail.getAsset(ADD_SCHEDULE_DETAILS)).isAbsent();
            LOGGER.info("REN 14705 Step 6,7");
            LOGGER.info("REN 13903 Step 2");
            softly.assertThat(tableAgeReductionScheduleDetail.getRow(1).getCell(5).controls.links.get(ActionConstants.REMOVE)).isAbsent();
            planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION_SCHEDULE).setValue("CUSTOM");
            ImmutableList.of(AGE_DROP_DOWN, REDUCED_TO_PERCENTAGE, MAXIMUM_TEXTBOX, ADD_SCHEDULE_DETAILS).forEach(control -> {
                softly.assertThat(ageReductionScheduleDetail.getAsset(control)).isPresent().isEnabled();
            });
            softly.assertThat(tableAgeReductionScheduleDetail.getColumn("Age")).hasValue(ImmutableList.of(""));
            softly.assertThat(tableAgeReductionScheduleDetail.getColumn("Reduced To %")).hasValue(ImmutableList.of(""));
            softly.assertThat(tableAgeReductionScheduleDetail.getColumn("Maximum")).hasValue(ImmutableList.of(""));
            softly.assertThat(tableAgeReductionScheduleDetail.getRow(1).getCell(5).controls.links.get(ActionConstants.CHANGE)).isPresent();
            softly.assertThat(ageReductionScheduleDetail.getAsset(REDUCED_TO_PERCENTAGE)).hasValue("");
            softly.assertThat(ageReductionScheduleDetail.getAsset(AGE_DROP_DOWN)).hasOptions("", "65", "70", "75", "80", "85", "90", "95");
            ageReductionScheduleDetail.getAsset(AGE_DROP_DOWN).setValue("65");
            softly.assertThat(ageReductionScheduleDetail.getAsset(REDUCED_TO_PERCENTAGE)).hasOptions("", "25%", "30%", "35%", "40%", "45%", "50%", "55%", "60%", "65%", "70%", "75%");
            ageReductionScheduleDetail.getAsset(REDUCED_TO_PERCENTAGE).setValue("25%");
            softly.assertThat(tableAgeReductionScheduleDetail.getColumn("Age")).hasValue(ImmutableList.of("65"));
            softly.assertThat(tableAgeReductionScheduleDetail.getColumn("Reduced To %")).hasValue(ImmutableList.of("25%"));
            softly.assertThat(tableAgeReductionScheduleDetail.getColumn("Maximum")).hasValue(ImmutableList.of(""));
            softly.assertThat(tableAgeReductionScheduleDetail.getRow(1).getCell(5).controls.links.get(ActionConstants.CHANGE)).isPresent();
            ageReductionScheduleDetail.getAsset(ADD_SCHEDULE_DETAILS).click();
            ageReductionScheduleDetail.getAsset(AGE_DROP_DOWN).setValue("70");
            ageReductionScheduleDetail.getAsset(REDUCED_TO_PERCENTAGE).setValue("30%");
            softly.assertThat(tableAgeReductionScheduleDetail.getColumn("Age")).hasValue(ImmutableList.of("65", "70"));
            softly.assertThat(tableAgeReductionScheduleDetail.getColumn("Reduced To %")).hasValue(ImmutableList.of("25%", "30%"));
            softly.assertThat(tableAgeReductionScheduleDetail.getColumn("Maximum")).hasValue(ImmutableList.of("", ""));
            softly.assertThat(tableAgeReductionScheduleDetail.getRow(1).getCell(5).controls.links.get(ActionConstants.CHANGE)).isPresent();
            softly.assertThat(tableAgeReductionScheduleDetail.getRow(1).getCell(5).controls.links.get(ActionConstants.REMOVE)).isPresent();
            softly.assertThat(tableAgeReductionScheduleDetail.getRow(2).getCell(5).controls.links.get(ActionConstants.CHANGE)).isPresent();
            softly.assertThat(tableAgeReductionScheduleDetail.getRow(2).getCell(5).controls.links.get(ActionConstants.REMOVE)).isPresent();
            tableAgeReductionScheduleDetail.getRow(2).getCell(5).controls.links.get(ActionConstants.CHANGE).click();
            tableAgeReductionScheduleDetail.getRow(2).getCell(5).controls.links.get(ActionConstants.REMOVE).click();
            Page.dialogConfirmation.confirm();
            softly.assertThat(tableAgeReductionScheduleDetail.getRow(2)).isAbsent();
            LOGGER.info("REN 13903 Step 3");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(AGE_REDUCTION)).isPresent();
            ageReductionScheduleDetail.getAsset(MAXIMUM_TEXTBOX).setValue("12345678901234567.00");
            softly.assertThat(ImmutableList.of("12345678901234567.00").stream().allMatch(num -> tableAgeReductionScheduleDetail.getColumn("Maximum").getValue().contains(num))).isTrue();
            ageReductionScheduleDetail.getAsset(MAXIMUM_TEXTBOX).setValue("12345678901234567890123.00");
            softly.assertThat(ageReductionScheduleDetail.getAsset(MAXIMUM_TEXTBOX)).hasWarningWithText("12345678901234567890123.00: Integer-part of value cannot exceed 17 digits");
        });
    }
}