package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.SITUS_STATE_ERROR_MESSAGE;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.*;
import static com.exigen.ren.main.enums.ValueConstants.INCLUDED;
import static com.exigen.ren.main.enums.ValueConstants.NOT_INCLUDED;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OffsetsMetaData.INDIVIDUAL_DISABILITY_PLAN;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.CONTRIBUTION_TYPE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigurePlanDefinitionPlansAndCoverage extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private static StaticElement planWarningMessage = new StaticElement(By.xpath("//form[@id='searchForm_plansSearch']//span[@class='error_message']"));


    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20809"}, component = POLICY_GROUPBENEFITS)
    public void testConfigurePlanDefinitionPlansAndCoverages() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), "CT")
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel())), planDefinitionTab.getClass());

        LOGGER.info("Test Case REN-20809 Step 1");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).buttonOpenPopup.click();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).buttonSearch.click();
        assertThat(planDefinitionTab.getAssetList().getAsset(PLAN).getAvailableItems()).
                hasSameElementsAs(ImmutableList.of(CON, NC, SGR, VOL));
        planDefinitionTab.getAssetList().getAsset(PLAN).addValue(ImmutableList.of(NC));
        planDefinitionTab.getAssetList().getAsset(PLAN).buttonSave.click();

        LOGGER.info("Test Case REN-20809 Step 2-6");
        verifyStepsThreeToSixForPlan(NC);
        assertThat(planDefinitionTab.getSponsorParticipantFundingStructureAsset().getAsset(CONTRIBUTION_TYPE)).hasValue("Non-contributory");

        LOGGER.info("Test Case REN-20809 Step 9");
        verifyStepsThreeToSixForPlan(CON);
        assertThat(planDefinitionTab.getSponsorParticipantFundingStructureAsset().getAsset(CONTRIBUTION_TYPE)).hasValue("Contributory");

        LOGGER.info("Test Case REN-20809 Step 12");
        verifyStepsThreeToSixForPlan(VOL);
        assertThat(planDefinitionTab.getSponsorParticipantFundingStructureAsset().getAsset(CONTRIBUTION_TYPE)).hasValue("Voluntary");

        assertSoftly(softly -> {
            LOGGER.info("Test Case REN-20809 Step 16");
            planDefinitionTab.navigateToTab();
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of("SGR"));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE)).hasValue("Eligible");
            softly.assertThat(planDefinitionTab.getSponsorParticipantFundingStructureAsset().getAsset(CONTRIBUTION_TYPE)).hasValue("Non-contributory");

            LOGGER.info("Test Case REN-20809 Step 17");
            ImmutableList.of(MINIMUM_HOURLY_REQUIREMENT, ELIGIBILITY_WAITING_PERIOD_DEFINITION, WAITING_PERIOD, WAITING_PERIOD_MODE).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(control)).hasValue(StringUtils.EMPTY));

            LOGGER.info("Test Case REN-20809 Step 18");
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(PRE_EXISTING_CONDITIONS)).hasValue(NOT_INCLUDED);
            planDefinitionTab.getBenefitScheduleAsset().getAsset(PRE_EXISTING_CONDITIONS).setValue(INCLUDED);
            ImmutableList.of(PRE_EXISTING_CONDITION_LOOK_BACK_PERIOD, PRE_EXISTING_CONDITION_CONTINUOUSLY_INSURED_PERIOD, PRE_EXISTING_CONDITION_TREATMENT_FREE_PERIOD).forEach(control ->
                    softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(control)).isPresent());
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(PRE_EXISTING_CONDITION_LOOK_BACK_PERIOD)).hasValue("12 Months")
                    .hasOptions("30 days", "3 Months", "6 Months", "12 Months");
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(PRE_EXISTING_CONDITION_CONTINUOUSLY_INSURED_PERIOD)).hasValue("24 Months");
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(SURVIVOR_FAMILY_INCOME_BENEFIT_TYPE))
                    .hasValue("3 Months Survivor Income Benefit")
                    .hasOptions(ValueConstants.NONE, "3 Months Survivor Income Benefit", "6 Months Survivor Income Benefit", "1 Year Family Income Benefit", "2 Year Family Income Benefit");
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(NUMBER_OF_MONTH)).hasOptions("12 months", "24 months", "36 months", "60 months");

            LOGGER.info("Test Case REN-20809 Step 19");
            softly.assertThat(planDefinitionTab.getOptionsAsset().getAsset(SPECIAL_CONDITIONS_LIMITATION)).hasValue(ValueConstants.NONE);
            softly.assertThat(planDefinitionTab.getOptionsAsset().getAsset(COMBINED_LIMIT)).hasValue(false);

            LOGGER.info("Test Case REN-20809 Step 20");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(OFFSETS).getAsset(INDIVIDUAL_DISABILITY_PLAN)).hasValue(NOT_INCLUDED);
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20824"}, component = POLICY_GROUPBENEFITS)
    public void testConfigurePlanDefinitionPlansAndCoverages_2() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), "CT")
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel())), planDefinitionTab.getClass());

        LOGGER.info("Test Case REN-20824 TC1 Step 1");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(NC));

        assertSoftly(softly -> {
            LOGGER.info("Test Case REN-20824 TC1 Step 2  TC2 Step 1");
            softly.assertThat(planDefinitionTab.getOptionsAsset().getAsset(STUDENT_LOAN_REPAYMENT_AMOUNT)).isEnabled();
            ImmutableList.of(CHILD_EDUCATION_BENEFIT, PRESUMPTIVE_DISABILITY).forEach(control ->
                    softly.assertThat(planDefinitionTab.getOptionsAsset().getAsset(control)).isEnabled());
        });

        LOGGER.info("Test Case REN-20824 TC2 Step 2");
        policyInformationTab.changeSitusStateValue("FL");
        LOGGER.info("Test Case REN-20824 TC2 Step 2.1");
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.SITUS_STATE)).hasWarningWithText(SITUS_STATE_ERROR_MESSAGE);

        LOGGER.info("Test Case REN-20824 TC2 Step 2.3");
        planDefinitionTab.navigateToTab();
        assertThat(planDefinitionTab.getAssetList().getAsset(PLAN).getValue()).isEmpty();
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(CON));

        assertSoftly(softly -> {
            LOGGER.info("Test Case REN-20824 TC2 Step 3");
            softly.assertThat(planDefinitionTab.getOptionsAsset().getAsset(STUDENT_LOAN_REPAYMENT_AMOUNT)).isDisabled();
            ImmutableList.of(CHILD_EDUCATION_BENEFIT, PRESUMPTIVE_DISABILITY).forEach(control ->
                    softly.assertThat(planDefinitionTab.getOptionsAsset().getAsset(control)).isDisabled());
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20830", "REN-40196"}, component = POLICY_GROUPBENEFITS)
    public void testConfigurePlanDefinitionPlansAndCoverages_3() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());

        AdvancedSelector planSelector = planDefinitionTab.getAssetList().getAsset(PLAN);
        assertSoftly(softly -> {
            LOGGER.info("Test Case REN-20830 Step 1-4, REN-40196: TC01");
            ImmutableList.of("CA", "ND", "SD", "MT").forEach(situsSate -> {
                policyInformationTab.changeSitusStateValue(situsSate);
                planDefinitionTab.navigateToTab();
                planSelector.buttonOpenPopup.click();
                planSelector.buttonSearch.click();
                assertThat(planWarningMessage).hasValue("   No Plans Found!");
                softly.assertThat(planSelector.getAvailableItems()).isEmpty();
                planSelector.buttonCancel.click();
            });
        });

        LOGGER.info("Test Case REN-20830 Step 5");
        policyInformationTab.changeSitusStateValue("VT");
        planDefinitionTab.navigateToTab();
        planSelector.buttonOpenPopup.click();
        planSelector.buttonSearch.click();
        assertThat(planSelector.getAvailableItems()).hasSameElementsAs(ImmutableList.of("CON", "NC", "SGR", "VOL"));
        planSelector.buttonCancel.click();

        assertSoftly(softly -> {
            LOGGER.info("Test Case REN-20830 Step 6, REN-40196: TC02");
            ImmutableList.of("GA", "MA").forEach(situsSate -> {
                policyInformationTab.changeSitusStateValue(situsSate);
                planDefinitionTab.navigateToTab();
                planSelector.buttonOpenPopup.click();
                planSelector.buttonSearch.click();
                softly.assertThat(planSelector
                        .listboxAvailableItems.getAllValues()).containsExactlyInAnyOrder("CON", "NC", "SGR", "VOL");
                planSelector.buttonCancel.click();
            });
        });

        LOGGER.info("Test Case REN-20809 Step 15");
        policyInformationTab.changeSitusStateValue("CT");
        assertThat(policyInformationTab.getAssetList().getAsset(SITUS_STATE))
                .hasWarningWithText(SITUS_STATE_ERROR_MESSAGE);

    }

    private void verifyStepsThreeToSixForPlan(String plan) {
        assertSoftly(softly -> {
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(plan));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE)).hasValue("Eligible").hasOptions("Enrolled", "Eligible");
            ImmutableList.of(MINIMUM_HOURLY_REQUIREMENT, ELIGIBILITY_WAITING_PERIOD_DEFINITION, WAITING_PERIOD, WAITING_PERIOD_MODE).forEach(control ->
                    softly.assertThat(planDefinitionTab.getEligibilityAsset().getAsset(control)).hasValue(StringUtils.EMPTY));
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(PRE_EXISTING_CONDITIONS)).hasValue(NOT_INCLUDED);
            planDefinitionTab.getBenefitScheduleAsset().getAsset(PRE_EXISTING_CONDITIONS).setValue(INCLUDED);
            ImmutableList.of(PRE_EXISTING_CONDITION_LOOK_BACK_PERIOD, PRE_EXISTING_CONDITION_CONTINUOUSLY_INSURED_PERIOD, PRE_EXISTING_CONDITION_TREATMENT_FREE_PERIOD).forEach(control ->
                    softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(control)).isPresent());
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(PRE_EXISTING_CONDITION_LOOK_BACK_PERIOD)).hasValue("3 Months")
                    .hasOptions("30 days", "3 Months", "6 Months", "12 Months");
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(PRE_EXISTING_CONDITION_CONTINUOUSLY_INSURED_PERIOD)).hasValue("12 Months");
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(SURVIVOR_FAMILY_INCOME_BENEFIT_TYPE))
                    .hasValue("3 Months Survivor Income Benefit")
                    .hasOptions(ValueConstants.NONE, "3 Months Survivor Income Benefit", "6 Months Survivor Income Benefit", "1 Year Family Income Benefit", "2 Year Family Income Benefit");
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(NUMBER_OF_MONTH)).hasOptions("12 months", "24 months", "36 months", "60 months");
            softly.assertThat(planDefinitionTab.getOptionsAsset().getAsset(SPECIAL_CONDITIONS_LIMITATION)).hasValue(ValueConstants.NONE);
            softly.assertThat(planDefinitionTab.getOptionsAsset().getAsset(COMBINED_LIMIT)).hasValue(false);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(OFFSETS).getAsset(INDIVIDUAL_DISABILITY_PLAN)).hasValue(NOT_INCLUDED);
        });
    }
}