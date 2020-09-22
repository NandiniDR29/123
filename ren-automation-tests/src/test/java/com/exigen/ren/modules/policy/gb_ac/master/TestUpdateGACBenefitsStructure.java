package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.PlanGAC.ENHANCED_10_UNITS;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.AccidentalDeathBenefitCommonCarrierMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.AccidentalDeathBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.BloodPlasmaPlateletsBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.ComaBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.ConcussionBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.SkinGraftBurnBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsDtoFTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsDtoFTabMetaData.DislocationBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsDtoFTabMetaData.EyeInjuryBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsDtoFTabMetaData.FractureBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.LACERATION_BENEFIT_INFO;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.LODGING_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.LacerationBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.LodgingBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.TRANSPORTATION_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.TransportationBenefitMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUpdateGACBenefitsStructure extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {
    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-15693", "REN-15770"}, component = POLICY_GROUPBENEFITS)
    public void testUpdateGACBenefitsStructure() {
        mainApp().open();
        LOGGER.info("Test REN-15693 Preconditions");
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData(), planDefinitionTab.getClass());
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(ENHANCED_10_UNITS));
        assertSoftly(softly -> {
            LOGGER.info("Test REN-15693 Step 1");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_A_TO_C.get());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT)).isPresent();
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(APPLY_BENEFIT_ACCIDENTAL_DEATH_BENEFIT)).isPresent().isEnabled().hasValue(true);
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(DISAPPEARANCE_INCLUDED_ACCIDENTAL_DEATH_BENEFIT)).isPresent().isEnabled().hasValue(true);
            ImmutableList.of(INCURRAL_PERIOD_ACCIDENTAL_DEATH_BENEFIT, ACCIDENTAL_DEATH_BENEFIT_AMOUNT_FOR_COVERED_PERSON, ACCIDENTAL_DEATH_BENEFIT_AMOUNT_FOR_COVERED_SPOUSE, ACCIDENTAL_DEATH_BENEFIT_AMOUNT_FOR_EACH_COVERED_DEPENDENT_CHILD).forEach(control ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(control)).isPresent().isRequired().isEnabled());

            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT_COMMON_CARRIER)).isPresent();
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT_COMMON_CARRIER).getAsset(APPLY_BENEFIT_COMMON_CARRIER)).isPresent().isEnabled().hasValue(true);
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT_COMMON_CARRIER).getAsset(DISAPPEARANCE_INCLUDED_COMMON_CARRIER)).isPresent().isEnabled().hasValue(true);
            ImmutableList.of(INCURRAL_PERIOD_COMMON_CARRIER, ACCIDENTAL_DEATH_BENEFIT_AMOUNT_COMMON_CARRIER_FOR_COVERED_PERSON, ACCIDENTAL_DEATH_BENEFIT_AMOUNT_COMMON_CARRIER_FOR_COVERED_SPOUSE, ACCIDENTAL_DEATH_BENEFIT_AMOUNT_COMMON_CARRIER_FOR_EACH_COVERED_DEPENDENT_CHILD).forEach(control ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT_COMMON_CARRIER).getAsset(control)).isPresent().isRequired().isEnabled());

            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(COMA_BENEFIT)).isPresent();
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(COMA_BENEFIT).getAsset(APPLY_BENEFIT_COMA_BENEFIT)).isPresent().isEnabled().hasValue(true);
            ImmutableList.of(INCURRAL_PERIOD_COMA_BENEFIT, COMA_BENEFIT_AMOUNT, NUMBER_OF_TIME_A_COMA_CAN_OCCUR, TIME_PERIOD_COMA_BENEFIT).forEach(control ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(COMA_BENEFIT).getAsset(control)).isPresent().isRequired().isEnabled());

            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(EnhancedBenefitsAtoCTabMetaData.CONCUSSION_BENEFIT)).isPresent();
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(CONCUSSION_BENEFIT).getAsset(APPLY_BENEFIT_CONCUSSION_BENEFIT)).isPresent().isEnabled().hasValue(true);
            ImmutableList.of(INCURRAL_PERIOD_HOURS, CONCUSSION_BENEFIT_AMOUNT, NUMBER_OF_TIME_A_CONCUSSION_CAN_OCCUR, TIME_PERIOD_CONCUSSION_BENEFIT).forEach(control ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(CONCUSSION_BENEFIT).getAsset(control)).isPresent().isRequired().isEnabled());

            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(EnhancedBenefitsAtoCTabMetaData.BLOOD_PLASMA_PLATELETS_BENEFIT)).isPresent();
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(BLOOD_PLASMA_PLATELETS_BENEFIT).getAsset(APPLY_BENEFIT_BLOOD_PLASMA_PLATELETS)).isPresent().isEnabled().hasValue(true);
            ImmutableList.of(INCURRAL_PERIOD_BLOOD_PLASMA_PLATELETS, BLOOD_PLASMA_PLATELETS_BENEFIT_AMOUNT, MAXIMUM_NUMBER_OF_TRANSFUSIONS_OF_THE_BLOOD_PLASMA_PLATELETS_BENEFIT, TIME_PERIOD_BLOOD_PLASMA_PLATELETS).forEach(control ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(BLOOD_PLASMA_PLATELETS_BENEFIT).getAsset(control)).isPresent().isRequired().isEnabled());

            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(EnhancedBenefitsAtoCTabMetaData.SKIN_GRAFT_BURN_BENEFIT)).isPresent();
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(SKIN_GRAFT_BURN_BENEFIT).getAsset(APPLY_BENEFIT_SKIN_GRAFT_BURN)).isPresent().isEnabled().hasValue(true);
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(SKIN_GRAFT_BURN_BENEFIT).getAsset(TREATMENT_PERIOD_DAYS)).isPresent().isRequired().isEnabled();
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(SKIN_GRAFT_BURN_BENEFIT).getAsset(SKIN_GRAFT_BURN_BENEFIT_AMOUNT)).isPresent().isRequired().isEnabled();
            LOGGER.info("Test REN-15693 Step 2");
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(SKIN_GRAFT_BURN_BENEFIT).getAsset(INCURRAL_TIME_PERIOD_SKIN_GRAFT_BURN)).isAbsent();

            LOGGER.info("Test REN-15693 Step 3, Test REN-15770 Step 1, Step 5");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_D_TO_F.get());

            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(DISLOCATION_BENEFIT)).isPresent();
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(DISLOCATION_BENEFIT).getAsset(DIAGNOSIS_PERIOD_DAYS_DISLOCATION)).isPresent().isRequired().isEnabled().hasValue("365");
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(DISLOCATION_BENEFIT).getAsset(TREATMENT_PERIOD_HOURS_DISLOCATION)).isPresent().isRequired().isEnabled();

            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EYE_INJURY_BENEFIT)).isPresent();
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EYE_INJURY_BENEFIT).getAsset(APPLY_BENEFIT_EYE_INJURY)).isPresent().isEnabled().hasValue(true);
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EYE_INJURY_BENEFIT).getAsset(TREATMENT__PERIOD_DAYS_EYE_INJURY)).isPresent().isRequired().isEnabled();

            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(FRACTURE_BENEFIT)).isPresent();
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(FRACTURE_BENEFIT).getAsset(DIAGNOSIS_PERIOD_DAYS_FRACTURE)).isPresent().isRequired().isEnabled().hasValue("365");
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(FRACTURE_BENEFIT).getAsset(TREATMENT_PERIOD_HOURS_FRACTURE)).isPresent().isRequired().isEnabled();

            LOGGER.info("Test REN-15693 Step 4");
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EYE_INJURY_BENEFIT).getAsset(INCURRAL_TIME_PERIOD_EYE_INJURY)).isAbsent();

            LOGGER.info("Test REN-15693 Step 5");
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(DISLOCATION_BENEFIT).getAsset(INCURRAL_TIME_PERIOD_DISLOCATION)).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Rib (Closed Reduction Amount)"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Rib (Open Reduction Amount)"))).isAbsent();

            LOGGER.info("Test REN-15693 Step 6");
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(FRACTURE_BENEFIT).getAsset(INCURRAL_TIME_PERIOD_FRACTURE)).isAbsent();

            LOGGER.info("Test REN-15693 Step 7");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_H_TO_L.get());

            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(EnhancedBenefitsHtoLTabMetaData.LACERATION_BENEFIT_INFO)).isPresent();
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LACERATION_BENEFIT_INFO).getAsset(APPLY_BENEFIT_LACERATION)).isPresent().isEnabled().hasValue(true);
            ImmutableList.of(TREATMENT_PERIOD_DAYS_LACERATION, REPAIRED_WITHOUT_STITCHES_BENEFIT_AMOUNT, BENEFIT_AMOUNT_FOR_LACERATION_LESS_THAN, BENEFIT_AMOUNT_FOR_LACERATION, BENEFIT_AMOUNT_FOR_LACERATION_OVER).forEach(control ->
                    softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LACERATION_BENEFIT_INFO).getAsset(control)).isPresent().isRequired().isEnabled());

            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(EnhancedBenefitsHtoLTabMetaData.LODGING_BENEFIT)).isPresent();
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LODGING_BENEFIT).getAsset(APPLY_BENEFIT_LODGING)).isPresent().isEnabled().hasValue(true);
            ImmutableList.of(CONFINEMENT_LODGING_BENEFIT_AMOUNT, MAXIMUM_NUMBER_OF_DAYS_FOR_THE_CONFINEMENT_LODGING_BENEFIT, TIME_PERIOD_LODGING).forEach(control ->
                    softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LODGING_BENEFIT).getAsset(control)).isPresent().isRequired().isEnabled());

            LOGGER.info("Test REN-15693 Step 8");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Maximum number of lacerations"))).isAbsent();

            LOGGER.info("Test REN-15693 Step 9");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Outpatient Surgery Lodging Benefit Amount"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Maximum number of days for Outpatient Surgery Lodging Benefit"))).isAbsent();

            LOGGER.info("Test REN-15693 Step 10");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_M_TO_T.get());

            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Option"))).isPresent();

            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(EnhancedBenefitsMtoTTabMetaData.TRANSPORTATION_BENEFIT)).isPresent();
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(TRANSPORTATION_BENEFIT).getAsset(APPLY_BENEFIT_TRANSPORTATION)).isPresent().isEnabled().hasValue(true);
            ImmutableList.of(TRANSPORTATION_BENEFIT_AMOUNT, MAXIMUM_NUMBER_OF_TIMES_FOR_THE_TRANSPORTATION_BENEFIT, TIME_PERIOD_TRANSPORTATION).forEach(control ->
                    softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(TRANSPORTATION_BENEFIT).getAsset(control)).isPresent().isEnabled().isRequired());
        });
    }
}