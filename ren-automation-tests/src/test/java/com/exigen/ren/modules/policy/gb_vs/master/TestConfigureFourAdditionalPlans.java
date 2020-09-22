package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ErrorConstants;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanVision.*;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.ELIGIBILITY;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetaData.BENEFIT_END_ON;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.CopayMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.FrequencyMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.OutOfNetworkCoverageMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.PlanBenefitsMetadata.CONTACT_LENSES_ALLOWANCE_UP_TO;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.PlanBenefitsMetadata.FRAMES_ALLOWANCE_UP_TO;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.RATE_TYPE;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.USE_BROCHURE_RATES;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructure.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.GROUP_IS_AN_ASSOCIATION;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigureFourAdditionalPlans extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    private static final String FIFTY = new Currency(50).toString();
    private static final AdvancedSelector coverageTiersAsset = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_TIERS);
    private static final String FAMILY_TIER = "Family Tier";
    private static final String AREA_TIER = "Area + Tier";
    private static final String CONTACTS_UP_TO_VALUE = "$105 ($210 if medically necessary)";
    private static final ImmutableList<String> EXAM_MATERIALS_LIST = ImmutableList.of("$0 combined", "$0/$5", "$0/$10", "$0/$15", "$0/$20", "$0/$25", "$5 combined", "$5/$5", "$5/$10", "$5/$15", "$5/$20", "$5/$25", "$10 combined", "$10/$10", "$10/$15", "$10/$20", "$10/$25", "$15 combined", "$15/$15", "$15/$20", "$15/$25", "$20 combined", "$20/$20", "$20/$25", "$25 combined", "$25/$25");
    private static final ImmutableList<String> MEDICALLY_NECESSARY_CONTACT_LENSES_LIST = ImmutableList.of("$0", "$5", "$10", "$15", "$20", "$25", "$5 combined", "$5", "$10", "$15", "$20", "$25", "$10 combined", "$10", "$15", "$20", "$25", "$15 combined", "$15", "$20", "$25", "$20 combined", "$20", "$25", "$25 combined", "$25");
    private static final ImmutableList<String> PARTICIPANT_COVERAGE_IN_RANGE_LIST = ImmutableList.of("0", "1", "100");
    private static final ImmutableList<String> PARTICIPANT_COVERAGE_OUT_OF_RANGE_LIST = ImmutableList.of("-1", "101");
    private static final ImmutableList<String> SPONSOR_CONTRIBUTION_MONTHLY_IN_RANGE_LIST = ImmutableList.of("0", "1", "1.01", "2000");
    private static final ImmutableList<String> SPONSOR_CONTRIBUTION_MONTHLY_OUT_OF_RANGE_LIST = ImmutableList.of("-1", "2001");
    private static final AssetList sponsorParticipantFundingStructure = planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE);
    private static final AssetList outOfNetworkCoverage = planDefinitionTab.getAssetList().getAsset(OUT_OF_NETWORK_COVERAGE);
    private static final AssetList copay = planDefinitionTab.getAssetList().getAsset(COPAY);
    private static final AssetList eligibility = planDefinitionTab.getAssetList().getAsset(ELIGIBILITY);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18907", "REN-18910", "REN-19364", "REN-19365", "REN-19366"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureFourAdditionalPlans() {

        LOGGER.info("Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass());
        planDefinitionTab.selectDefaultPlan();

        assertSoftly(softly -> {
            LOGGER.info("REN-18907 Step 2 Step 4");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_BROCHURE_RATES)).isPresent().isRequired().isEnabled().hasValue(VALUE_NO);

            LOGGER.info("REN-18910 Step 3");
            planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_BROCHURE_RATES).setValue(VALUE_YES);
            planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RATE_TYPE).setValue(AREA_TIER);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_BROCHURE_RATES)).isDisabled().hasValue(VALUE_NO);

            LOGGER.info("REN-18910 Step 8.2.1");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("AK");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(SITUS_STATE))
                    .hasWarningWithText(ErrorConstants.ErrorMessages.SITUS_STATE_ERROR_MESSAGE);

            LOGGER.info("REN-18910 Step 8.3.1");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN).getValue()).isEmpty();

            LOGGER.info("REN-19364 Step 3 Precondition-Step 2");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(A_LA_CARTE, PlanB, PlanC));

            PlanDefinitionTab.changePlanTo(PlanB);
            verifyCommonRulesForAlaCartePlanB();
            PlanDefinitionTab.changePlanTo(A_LA_CARTE);
            verifyCommonRulesForAlaCartePlanB();

            LOGGER.info("REN-19364 Step 3 REN-19365 Step 3 For Plan B");
            verifyCommonDefaultAndAvailableValueForPlans(PlanB);

            LOGGER.info("REN-19364 Step 4  REN-19365 Step 4 For A La Carte");
            verifyCommonDefaultAndAvailableValueForPlans(A_LA_CARTE);

            LOGGER.info("REN-19364 Step 5  REN-19365 Step 4 For PlanC");
            verifyCommonDefaultAndAvailableValueForPlans(PlanC);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN_BENEFITS).getAsset(FRAMES_ALLOWANCE_UP_TO)).hasValue("$150");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME)).hasValue("Plan C (12/12/12)");
            softly.assertThat(copay.getAsset(PROGRESSIVE_LENSES_IN_FULL)).hasValue(VALUE_NO);
            softly.assertThat(copay.getAsset(LENS_ENHANCEMENTS_STANDARD)).hasValue("$55");
            softly.assertThat(copay.getAsset(LENS_ENHANCEMENTS_CUSTOM)).hasValue("$150 - $175"); //correct expected value $150 - $175 according REN-43074 on 12.05.2020
            softly.assertThat(copay.getAsset(LENS_ENHANCEMENTS_PREMIUM)).hasValue("$95 - $105"); //correct expected value $95 - $105 according REN-43074 on 12.05.2020
            softly.assertThat(outOfNetworkCoverage.getAsset(FRAME_UP_TO)).hasValue(new Currency(70).toString());
            softly.assertThat(outOfNetworkCoverage.getAsset(SINGLE_VISION_LENSES_UP_TO)).hasValue(new Currency(30).toString());
            softly.assertThat(outOfNetworkCoverage.getAsset(LINED_BIFOCAL_LENSES_UP_TO)).hasValue(FIFTY);
            softly.assertThat(outOfNetworkCoverage.getAsset(LINED_TRIFOCAL_LENSES_UP_TO)).hasValue(new Currency(65).toString());
            softly.assertThat(outOfNetworkCoverage.getAsset(PROGRESSIVE_LENSES_UP_TO)).hasValue(FIFTY);
            softly.assertThat(outOfNetworkCoverage.getAsset(LENTICULAR_LENSES_UP_TO)).hasValue(new Currency(100).toString());

            LOGGER.info("REN-19364 Step 3,4 For Plan Benefits Section");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN_BENEFITS).getAsset(FRAMES_ALLOWANCE_UP_TO)).hasOptions("$120", "$130", "$150", "$180", "$200");


            LOGGER.info("REN-19366 Step 3");
            PlanDefinitionTab.changePlanTo(PlanB);
            sponsorParticipantFundingStructure.getAsset(CONTRIBUTION_TYPE).setValue("Sponsor/Participant Split");
            sponsorParticipantFundingStructure.getAsset(IS_CONTRIBUTION_PERCENTAGE_BASED).setValue(VALUE_YES);
            softly.assertThat(sponsorParticipantFundingStructure.getAsset(PARTICIPANT_CONTRIBUTION_PERCENTAGE_EMPLOYEE_COVERAGE)).isPresent();
            softly.assertThat(sponsorParticipantFundingStructure.getAsset(PARTICIPANT_CONTRIBUTION_PERCENTAGE_DEPENDENT_COVERAGE)).isPresent();

            LOGGER.info("REN-19366 Step 4 Range");

            PARTICIPANT_COVERAGE_IN_RANGE_LIST.forEach(value -> {
                sponsorParticipantFundingStructure.getAsset(PARTICIPANT_CONTRIBUTION_PERCENTAGE_EMPLOYEE_COVERAGE).setValue(value);
                softly.assertThat(sponsorParticipantFundingStructure.getAsset(PARTICIPANT_CONTRIBUTION_PERCENTAGE_EMPLOYEE_COVERAGE)).hasNoWarning();
            });

            PARTICIPANT_COVERAGE_IN_RANGE_LIST.forEach(value -> {
                sponsorParticipantFundingStructure.getAsset(PARTICIPANT_CONTRIBUTION_PERCENTAGE_DEPENDENT_COVERAGE).setValue(value);
                softly.assertThat(sponsorParticipantFundingStructure.getAsset(PARTICIPANT_CONTRIBUTION_PERCENTAGE_DEPENDENT_COVERAGE)).hasNoWarning();
            });

            LOGGER.info("REN-19366 Step 4 Out Of Range");

            PARTICIPANT_COVERAGE_OUT_OF_RANGE_LIST.forEach(value -> {
                sponsorParticipantFundingStructure.getAsset(PARTICIPANT_CONTRIBUTION_PERCENTAGE_EMPLOYEE_COVERAGE).setValue(value);
                softly.assertThat(sponsorParticipantFundingStructure.getAsset(PARTICIPANT_CONTRIBUTION_PERCENTAGE_EMPLOYEE_COVERAGE)).hasWarning();
            });

            PARTICIPANT_COVERAGE_OUT_OF_RANGE_LIST.forEach(value -> {
                sponsorParticipantFundingStructure.getAsset(PARTICIPANT_CONTRIBUTION_PERCENTAGE_DEPENDENT_COVERAGE).setValue(value);
                softly.assertThat(sponsorParticipantFundingStructure.getAsset(PARTICIPANT_CONTRIBUTION_PERCENTAGE_DEPENDENT_COVERAGE)).hasWarning();
            });

            LOGGER.info("REN-19366 Step 9 In Range");
            sponsorParticipantFundingStructure.getAsset(IS_CONTRIBUTION_PERCENTAGE_BASED).setValue(VALUE_NO);
            softly.assertThat(sponsorParticipantFundingStructure.getAsset(SPONSOR_CONTRIBUTION_AMOUNT_EMPLOYEE_COVERAGE_MONTHLY)).isPresent();
            softly.assertThat(sponsorParticipantFundingStructure.getAsset(SPONSOR_CONTRIBUTION_AMOUNT_DEPENDENT_COVERAGE_MONTHLY)).isPresent();

            LOGGER.info("REN-19366 Step 9 In Range");
            SPONSOR_CONTRIBUTION_MONTHLY_IN_RANGE_LIST.forEach(value -> {
                sponsorParticipantFundingStructure.getAsset(SPONSOR_CONTRIBUTION_AMOUNT_EMPLOYEE_COVERAGE_MONTHLY).setValue(value);
                softly.assertThat(sponsorParticipantFundingStructure.getAsset(SPONSOR_CONTRIBUTION_AMOUNT_EMPLOYEE_COVERAGE_MONTHLY)).hasNoWarning();
            });

            SPONSOR_CONTRIBUTION_MONTHLY_IN_RANGE_LIST.forEach(value -> {
                sponsorParticipantFundingStructure.getAsset(SPONSOR_CONTRIBUTION_AMOUNT_DEPENDENT_COVERAGE_MONTHLY).setValue(value);
                softly.assertThat(sponsorParticipantFundingStructure.getAsset(SPONSOR_CONTRIBUTION_AMOUNT_DEPENDENT_COVERAGE_MONTHLY)).hasNoWarning();
            });

            LOGGER.info("REN-19366 Step 9 Out Of Range");

            SPONSOR_CONTRIBUTION_MONTHLY_OUT_OF_RANGE_LIST.forEach(value -> {
                sponsorParticipantFundingStructure.getAsset(SPONSOR_CONTRIBUTION_AMOUNT_EMPLOYEE_COVERAGE_MONTHLY).setValue(value);
                softly.assertThat(sponsorParticipantFundingStructure.getAsset(SPONSOR_CONTRIBUTION_AMOUNT_EMPLOYEE_COVERAGE_MONTHLY)).hasWarning();
            });

            SPONSOR_CONTRIBUTION_MONTHLY_OUT_OF_RANGE_LIST.forEach(value -> {
                sponsorParticipantFundingStructure.getAsset(SPONSOR_CONTRIBUTION_AMOUNT_DEPENDENT_COVERAGE_MONTHLY).setValue(value);
                softly.assertThat(sponsorParticipantFundingStructure.getAsset(SPONSOR_CONTRIBUTION_AMOUNT_DEPENDENT_COVERAGE_MONTHLY)).hasWarning();
            });

            LOGGER.info("REN-19366 Step 10 In Range");

            PARTICIPANT_COVERAGE_IN_RANGE_LIST.forEach(value -> {
                sponsorParticipantFundingStructure.getAsset(ASSUMED_PARTICIPATION_PERCENTAGE).setValue(value);
                softly.assertThat(sponsorParticipantFundingStructure.getAsset(ASSUMED_PARTICIPATION_PERCENTAGE)).hasNoWarning();
            });

            LOGGER.info("REN-19366 Step 10 Out Of Range");

            PARTICIPANT_COVERAGE_OUT_OF_RANGE_LIST.forEach(value -> {
                sponsorParticipantFundingStructure.getAsset(ASSUMED_PARTICIPATION_PERCENTAGE).setValue(value);
                softly.assertThat(sponsorParticipantFundingStructure.getAsset(ASSUMED_PARTICIPATION_PERCENTAGE)).hasWarning();
            });


            LOGGER.info("REN-19366 Step 16 In Range");

            ImmutableList.of("2", "3", "999999").forEach(value -> {
                sponsorParticipantFundingStructure.getAsset(MINIMUM_NUMBER_OF_PARTICIPANTS).setValue(value);
                softly.assertThat(sponsorParticipantFundingStructure.getAsset(MINIMUM_NUMBER_OF_PARTICIPANTS)).hasNoWarning();
            });

            LOGGER.info("REN-19366 Step 16 Out Of Range");
            ImmutableList.of("1", "1000000").forEach(value -> {
                sponsorParticipantFundingStructure.getAsset(MINIMUM_NUMBER_OF_PARTICIPANTS).setValue(value);
                softly.assertThat(sponsorParticipantFundingStructure.getAsset(MINIMUM_NUMBER_OF_PARTICIPANTS)).hasWarning();
            });

            LOGGER.info("REN-19366 Step 19");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(GROUP_IS_AN_ASSOCIATION).setValue(VALUE_NO);
            planDefinitionTab.navigateToTab();
            eligibility.getAsset(DOES_MINIMUM_HOURLY_REQUIREMENT_APPLY).setValue(VALUE_YES);
            softly.assertThat(eligibility.getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK)).isPresent();

            LOGGER.info("REN-19366 Step 20 In Range With Warning");

            ImmutableList.of("10", "11", "11.1").forEach(value -> {
                eligibility.getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).setValue(value);
                softly.assertThat(eligibility.getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK)).hasWarningWithText("Proposal will require Underwriter approval because Minimum Hourly Requirement (hours per week) is 24 hours or fewer.");
            });

            LOGGER.info("REN-19366 Step 20 In Range Without Warning");
            eligibility.getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).setValue("40");
            softly.assertThat(eligibility.getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK)).hasNoWarning();


            LOGGER.info("REN-19366 Step 20 Out Of Range");

            ImmutableList.of("9", "41").forEach(value -> {
                eligibility.getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).setValue(value);
                softly.assertThat(eligibility.getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).getWarning().orElse(StringUtils.EMPTY)).contains("The range of Minimum Hourly Requirement (hours per week) should be [10, 40], and increment is 0.1.");
            });

            LOGGER.info("REN-19366 Step 23");
            ImmutableList.of("Amount and Mode Only", "First of the month coincident with or next following (amount and mode)", "First of the month following (amount and mode)").forEach(value ->
            {
                eligibility.getAsset(ELIGIBILITY_WAITING_PERIOD_DEFINITION).setValue(value);
                softly.assertThat(eligibility.getAsset(WAITING_PERIOD_AMOUNT)).isPresent();
            });

            LOGGER.info("REN-19366 Step 24 In Range");

            ImmutableList.of("0", "1", "365").forEach(value -> {
                eligibility.getAsset(WAITING_PERIOD_AMOUNT).setValue(value);
                softly.assertThat(eligibility.getAsset(WAITING_PERIOD_AMOUNT)).hasNoWarning();
            });

            LOGGER.info("REN-19366 Step 24 Out Of Range");

            ImmutableList.of("-1", "366").forEach(value -> {
                eligibility.getAsset(WAITING_PERIOD_AMOUNT).setValue(value);
                softly.assertThat(eligibility.getAsset(WAITING_PERIOD_AMOUNT)).hasWarning();
            });

            LOGGER.info("REN-19366 Step 27 In Range");

            ImmutableList.of("18", "19", "26").forEach(value -> {
                eligibility.getAsset(DEPENDENT_MAXIMUM_AGE).setValue(value);
                softly.assertThat(eligibility.getAsset(DEPENDENT_MAXIMUM_AGE)).hasNoWarning();
            });

            LOGGER.info("REN-19366 Step 27 Out Of Range");

            ImmutableList.of("17", "27").forEach(value -> {
                eligibility.getAsset(DEPENDENT_MAXIMUM_AGE).setValue(value);
                softly.assertThat(eligibility.getAsset(DEPENDENT_MAXIMUM_AGE)).hasWarning();
            });
        });
    }

    private void verifyCommonDefaultAndAvailableValueForPlans(String plan) {

        LOGGER.info("REN-19364 Step 3,4 For Plan Selection Section");
        PlanDefinitionTab.changePlanTo(plan);
        assertSoftly(softly -> {
            softly.assertThat(coverageTiersAsset.getValue()).hasSameElementsAs(ImmutableList.of(
                    "Employee + Spouse", "Employee Only", "Employee + Child(ren)", "Employee + Family"));

            coverageTiersAsset.buttonOpenPopup.click();
            coverageTiersAsset.buttonSearch.click();
            softly.assertThat(coverageTiersAsset.getAvailableItems()).hasSameElementsAs(ImmutableList.of(
                    "Employee + 1", "Composite tier"));
            coverageTiersAsset.buttonCancel.click();

            softly.assertThat(planDefinitionTab.getAssetList().getAsset(NETWORK)).hasOptions("Choice");
            softly.assertThat(sponsorParticipantFundingStructure.getAsset(CONTRIBUTION_TYPE)).hasValue("Voluntary").hasOptions(ImmutableList.of("Non-contributory", "Voluntary", "Sponsor/Participant Split"));
            softly.assertThat(sponsorParticipantFundingStructure.getAsset(MINIMUM_NUMBER_OF_PARTICIPANTS)).hasValue("2");
            sponsorParticipantFundingStructure.getAsset(CONTRIBUTION_TYPE).setValue("Sponsor/Participant Split");
            softly.assertThat(sponsorParticipantFundingStructure.getAsset(IS_CONTRIBUTION_PERCENTAGE_BASED)).hasValue(VALUE_YES);

            LOGGER.info("REN-19364 Step 3,4 For Rating Section");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_BROCHURE_RATES)).hasValue(VALUE_NO);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RATE_TYPE)).hasValue(FAMILY_TIER).hasOptions(FAMILY_TIER, AREA_TIER);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RatingMetadata.RATE_BASIS)).hasOptions("Monthly Tiered Price Per Participant");


            LOGGER.info("REN-19365 Step 3");
            LOGGER.info("Verify Medically Necessary Contact Lenses : available values are depend on the value of 'Exam / Materials'");
            EXAM_MATERIALS_LIST.forEach(value -> {
                copay.getAsset(EXAM_MATERIALS).setValue(value);
                softly.assertThat(copay.getAsset(MEDICALLY_NECESSARY_CONTACT_LENSES)).hasOptions(StringUtils.EMPTY, MEDICALLY_NECESSARY_CONTACT_LENSES_LIST.get(EXAM_MATERIALS_LIST.indexOf(value)));
            });

            LOGGER.info("REN-19365 Step 3 For Frequency Section");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(PLAN_LIMITATION)).hasOptions("Customer can choose either Eyeglasses or Contacts");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(FREQUENCY_DEFINITION)).hasOptions(StringUtils.EMPTY, "Calendar Year", "Service Year");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(CONTACTS)).hasOptions("12", "24");

            LOGGER.info("REN-19364 Step 3,4 For Out Of Network Coverage Section");
            softly.assertThat(outOfNetworkCoverage.getAsset(EXAM_UP_TO)).hasValue(new Currency(45).toString());

            eligibility.getAsset(ELIGIBILITY_WAITING_PERIOD_DEFINITION).setValue("Amount and Mode Only");
            softly.assertThat(eligibility.getAsset(ELIGIBILITY_WAITING_PERIOD_DEFINITION)).hasOptions(ImmutableList.of(StringUtils.EMPTY, "None", "Amount and Mode Only", "First of the month coincident with or next following (amount and mode)", "First of the month coincident with or next following date of hire", "First of the month following (amount and mode)", "First of the month following date of hire"));
            softly.assertThat(eligibility.getAsset(WAITING_PERIOD_MODE)).hasOptions(ImmutableList.of(StringUtils.EMPTY, "Days", "Weeks", "Months", "Years"));
            softly.assertThat(eligibility.getAsset(BENEFIT_END_ON)).hasOptions(ImmutableList.of(StringUtils.EMPTY, "Last day of the month when employment is terminated", "Last date of employment"));
        });
    }

    private void verifyCommonRulesForAlaCartePlanB() {
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.PLAN_TYPE)).hasOptions("Full Feature");
            softly.assertThat(outOfNetworkCoverage.getAsset(CONTACTS_UP_TO)).hasValue(CONTACTS_UP_TO_VALUE).hasOptions(StringUtils.EMPTY, CONTACTS_UP_TO_VALUE);
            softly.assertThat(copay.getAsset(EXAM_MATERIALS)).hasValue("$10/$25").hasOptions(EXAM_MATERIALS_LIST);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN_BENEFITS).getAsset(CONTACT_LENSES_ALLOWANCE_UP_TO)).hasValue("Same as Frames").hasOptions("Same as Frames");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME)).hasValue("Plan B (12/12/24)").hasOptions(ValueConstants.EMPTY, "Plan A (12/24/24)", "Plan B (12/12/24)", "Plan C (12/12/12)", "Plan D (24/24/24)");
            softly.assertThat(copay.getAsset(PROGRESSIVE_LENSES_IN_FULL)).hasValue(VALUE_NO);
            LOGGER.info("REN-19364 Step 3,4 For Plan Benefits Section");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN_BENEFITS).getAsset(FRAMES_ALLOWANCE_UP_TO)).hasValue("$130").hasOptions("$120", "$130", "$150", "$180", "$200");
            softly.assertThat(copay.getAsset(LENS_ENHANCEMENTS_STANDARD)).hasValue("$55");
            softly.assertThat(copay.getAsset(LENS_ENHANCEMENTS_CUSTOM)).hasValue("$150 - $175");
            softly.assertThat(copay.getAsset(LENS_ENHANCEMENTS_PREMIUM)).hasValue("$95 - $105");
            softly.assertThat(outOfNetworkCoverage.getAsset(FRAME_UP_TO)).hasValue(new Currency(70).toString());
            softly.assertThat(outOfNetworkCoverage.getAsset(SINGLE_VISION_LENSES_UP_TO)).hasValue(new Currency(30).toString());
            softly.assertThat(outOfNetworkCoverage.getAsset(LINED_BIFOCAL_LENSES_UP_TO)).hasValue(FIFTY);
            softly.assertThat(outOfNetworkCoverage.getAsset(LINED_TRIFOCAL_LENSES_UP_TO)).hasValue(new Currency(65).toString());
            softly.assertThat(outOfNetworkCoverage.getAsset(PROGRESSIVE_LENSES_UP_TO)).hasValue(FIFTY);
            softly.assertThat(outOfNetworkCoverage.getAsset(LENTICULAR_LENSES_UP_TO)).hasValue(new Currency(100).toString());
        });
    }
}
