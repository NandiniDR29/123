package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.*;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.SITUS_STATE_ERROR_MESSAGE;
import static com.exigen.ren.main.enums.ValueConstants.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.RESIDUAL;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigurePlanDefination extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    private static final String ELIGIBLE = "Eligible";
    private static final String ENROLLED = "Enrolled";
    private static final String CONTRIBUTORY = "Contributory";
    private static final String NON_CONTRIBUTORY = "Non-contributory";
    private static final String VOLUNTARY = "Voluntary";
    private static final String THREE_MONTHS = "3 Months";
    private static final String TWELVE_MONTHS = "12 Months";
    private static final String NY_STATE = "NY";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-19203", "REN-19262", "REN-19264", "REN-19265", "REN-19266"}, component = POLICY_GROUPBENEFITS)
    public void testConfigurePlanDefination() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.initiate(getDefaultSTDMasterPolicyData());
        shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTDMasterPolicyData(), planDefinitionTab.getClass());
        assertSoftly(softly -> {

            LOGGER.info("Test REN-19203 Step 2");
            planDefinitionTab.getAssetList().getAsset(PLAN).buttonOpenPopup.click();
            planDefinitionTab.getAssetList().getAsset(PLAN).buttonSearch.click();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN).getAvailableItems()).hasSameElementsAs(ImmutableList.of(CON, NC, SGR, VOL));
            planDefinitionTab.getAssetList().getAsset(PLAN).buttonCancel.click();

            LOGGER.info("Test REN-19203 Step 10");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(CON));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE)).hasValue(ELIGIBLE)
                    .hasOptions(ImmutableList.of(ENROLLED, ELIGIBLE));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CONTRIBUTION_TYPE))
                    .hasValue(CONTRIBUTORY).hasOptions(NON_CONTRIBUTORY, CONTRIBUTORY, VOLUNTARY);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE)
                    .getAsset(PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.PREMIUM_PAID_POST_TAX))
                    .hasValue(VALUE_YES);
            planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(PRE_EXISTING_CONDITIONS)
                    .setValue(INCLUDED);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE)
                    .getAsset(PRE_EXISTING_CONDITION_LOOK_BACK_PERIOD)).hasValue(THREE_MONTHS).hasOptions("30 Days", THREE_MONTHS, "6 Months");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE)
                    .getAsset(PRE_EXISTING_CONDITION_TREATMENT_FREE_PERIOD)).hasValue(CHECKBOX_NOT_AVAILABLE).hasOptions("N/A", THREE_MONTHS, "6 Months");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE)
                    .getAsset(PRE_EXISTING_CONDITION_CONTINUOUSLY_INSURED_PERIOD)).hasValue(TWELVE_MONTHS).hasOptions("5 days", "6 Months", TWELVE_MONTHS);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(STAT_OFFSET)).hasValue(INCLUDED).hasOptions(StringUtils.EMPTY, INCLUDED, NOT_INCLUDED);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(OPTIONS).getAsset(RESIDUAL))
                    .hasValue(INCLUDED).hasOptions(INCLUDED, NOT_INCLUDED);

            LOGGER.info("Test REN-19262, REN-19264, REN-19265, REN-19266 Step 4");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(CON, NC, SGR, VOL));
            PlanDefinitionTab.tableCoverageDefinition.getRows().forEach(tablerow -> {
                tablerow.getCell(7).controls.links.get(ActionConstants.CHANGE).click();
                ImmutableList.of(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK, ELIGIBILITY_WAITING_PERIOD_DEFINITION, WAITING_PERIOD_DEFINITION, WAITING_PERIOD_MODE_DEFINITION).forEach(control ->
                        softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(control)).hasValue(StringUtils.EMPTY));
            });

            LOGGER.info("Test REN-19262 Step 4");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(NC));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE)).hasValue(ELIGIBLE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CONTRIBUTION_TYPE)).hasValue(NON_CONTRIBUTORY);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE)
                    .getAsset(PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.PREMIUM_PAID_POST_TAX))
                    .isPresent().isRequired().isEnabled().hasValue(VALUE_NO);
            planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(PRE_EXISTING_CONDITIONS)
                    .setValue(INCLUDED);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE)
                    .getAsset(PRE_EXISTING_CONDITION_LOOK_BACK_PERIOD)).hasValue(THREE_MONTHS);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE)
                    .getAsset(PRE_EXISTING_CONDITION_TREATMENT_FREE_PERIOD)).hasValue(CHECKBOX_NOT_AVAILABLE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE)
                    .getAsset(PRE_EXISTING_CONDITION_CONTINUOUSLY_INSURED_PERIOD)).hasValue(TWELVE_MONTHS);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(STAT_OFFSET)).hasValue(INCLUDED);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(OPTIONS).getAsset(RESIDUAL)).hasValue(INCLUDED);

            LOGGER.info("Test REN-19262 Step 11, 12");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("NV");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(SITUS_STATE))
                    .hasWarningWithText(SITUS_STATE_ERROR_MESSAGE);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN).getValue()).isEmpty();

            LOGGER.info("Test REN-19264 Step 4");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(NY_STATE);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(CON));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE)).hasValue(ELIGIBLE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CONTRIBUTION_TYPE)).hasValue(CONTRIBUTORY);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE)
                    .getAsset(PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.PREMIUM_PAID_POST_TAX))
                    .isPresent().isRequired().isEnabled().hasValue(VALUE_YES);
            planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(PRE_EXISTING_CONDITIONS)
                    .setValue(INCLUDED);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE)
                    .getAsset(PRE_EXISTING_CONDITION_LOOK_BACK_PERIOD)).hasValue(THREE_MONTHS);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE)
                    .getAsset(PRE_EXISTING_CONDITION_TREATMENT_FREE_PERIOD)).hasValue(CHECKBOX_NOT_AVAILABLE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE)
                    .getAsset(PRE_EXISTING_CONDITION_CONTINUOUSLY_INSURED_PERIOD)).hasValue(TWELVE_MONTHS);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(STAT_OFFSET)).hasValue(INCLUDED);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(OPTIONS).getAsset(RESIDUAL)).hasValue(INCLUDED);

            LOGGER.info("Test REN-19264 Step 11, 12");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("LA");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(SITUS_STATE))
                    .hasWarningWithText(SITUS_STATE_ERROR_MESSAGE);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN).getValue()).isEmpty();

            LOGGER.info("Test REN-19265 Step 4");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(NY_STATE);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(VOL));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE)).hasValue(ELIGIBLE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CONTRIBUTION_TYPE)).hasValue(VOLUNTARY);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE)
                    .getAsset(PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.PREMIUM_PAID_POST_TAX))
                    .isPresent().isRequired().isEnabled().hasValue(VALUE_YES);
            planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(PRE_EXISTING_CONDITIONS)
                    .setValue(INCLUDED);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE)
                    .getAsset(PRE_EXISTING_CONDITION_LOOK_BACK_PERIOD)).hasValue(THREE_MONTHS);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE)
                    .getAsset(PRE_EXISTING_CONDITION_TREATMENT_FREE_PERIOD)).hasValue(CHECKBOX_NOT_AVAILABLE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE)
                    .getAsset(PRE_EXISTING_CONDITION_CONTINUOUSLY_INSURED_PERIOD)).hasValue(TWELVE_MONTHS);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(STAT_OFFSET)).hasValue(INCLUDED);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(OPTIONS).getAsset(RESIDUAL)).hasValue(INCLUDED);

            LOGGER.info("Test REN-19265 Step 11, 12");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("MS");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(SITUS_STATE))
                    .hasWarningWithText(SITUS_STATE_ERROR_MESSAGE);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN).getValue()).isEmpty();

            LOGGER.info("Test REN-19266 Step 4");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(NY_STATE);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(SGR));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE)).hasValue(ELIGIBLE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CONTRIBUTION_TYPE)).hasValue(NON_CONTRIBUTORY);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE)
                    .getAsset(PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.PREMIUM_PAID_POST_TAX))
                    .isPresent().isRequired().isEnabled().hasValue(VALUE_YES);
            planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(PRE_EXISTING_CONDITIONS)
                    .setValue("Included");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE)
                    .getAsset(PRE_EXISTING_CONDITION_LOOK_BACK_PERIOD)).hasValue(THREE_MONTHS);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE)
                    .getAsset(PRE_EXISTING_CONDITION_TREATMENT_FREE_PERIOD)).hasValue(CHECKBOX_NOT_AVAILABLE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE)
                    .getAsset(PRE_EXISTING_CONDITION_CONTINUOUSLY_INSURED_PERIOD)).hasValue(TWELVE_MONTHS);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(STAT_OFFSET)).hasValue(INCLUDED);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(OPTIONS).getAsset(RESIDUAL)).hasValue(INCLUDED);

            LOGGER.info("Test REN-19266 Step 11, 12");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("GA");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(SITUS_STATE))
                    .hasWarningWithText(SITUS_STATE_ERROR_MESSAGE);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN).getValue()).isEmpty();
        });
    }
}