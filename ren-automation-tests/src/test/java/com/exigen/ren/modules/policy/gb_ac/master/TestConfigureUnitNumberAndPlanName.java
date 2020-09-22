package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.DoubleTextBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.CoveragesConstants;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.BasicBenefitsTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.exigen.ipb.eisa.verification.CustomAssertionsExtended.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.*;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.BasicBenefitsTabMetaData.HospitalAdmissionBenefitMetaData.HOSPITAL_ADMISSION_BENEFIT_AMT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.AbdominalOrThoracicSurgeryBenefitMetaData.ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.AccidentFollowUpTreatmentBenefitMetadata.ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.AccidentalDeathBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.AirAmbulanceBenefitMetaData.AIR_AMBULANCE_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.ApplianceBenefitMetaData.APPLIANCE_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.BloodPlasmaPlateletsBenefitMetaData.BLOOD_PLASMA_PLATELETS_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.CatastrophicAccidentBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.ComaBenefitMetaData.COMA_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.ConcussionBenefitMetaData.CONCUSSION_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.GroundAmbulanceBenefitMetaData.GROUND_AMBULANCE_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsDtoFTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsDtoFTabMetaData.DislocationBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsDtoFTabMetaData.EmergencyRoomTreatmentBenefitMetaData.EMERGENCY_ROOM_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsDtoFTabMetaData.EyeInjuryBenefitMetaData.EYE_INJURY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsDtoFTabMetaData.FractureBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.HospitalAdmissionBenefitMetaData.HOSPITAL_ADMISSION_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.HospitalAdmissionBenefitMetaData.INTENSIVE_CARE_UNIT_ADMISSION_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.HospitalICUConfinementBenefitMetaData.INTENSIVE_CARE_UNIT_CONFINEMENT_BENEFIT_AMOUNT_PER_DAY;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.InternalOrganLossBenefitMetaData.INTERNAL_ORGAN_LOSS_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.LacerationBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.LodgingBenefitMetaData.CONFINEMENT_LODGING_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.LossOfFingerToeHandFootOrSightOfAnEyeBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.MajorDiagnosticBenefitMetaData.MAJOR_DIAGNOSTIC_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.PhysicalTherapyServiceBenefitMetaData.PHYSICAL_THERAPY_SERVICES_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.PhysiciansOfficeUrgentCareTreatmentBenefitMetaData.PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.ProstheticDeviceBenefitMetaData.MORE_THAN_ONE_PROSTHETIC_DEVICE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.ProstheticDeviceBenefitMetaData.ONE_PROSTHETIC_DEVICE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.RehabilitationUnitBenefitMetaData.IMPATIEMT_REHABILITATION;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.SportsInjuryBenefitMetaData.SPORTS_INJURY_BENEFIT_LESSER_OF_25PERCENT_OF_ALL_BENEFITS_PAID;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.SurgeryBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.TransportationBenefitMetaData.TRANSPORTATION_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.ACCIDENTAL_DEATH_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.AccidentalDeathBenefitMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.EnhancedEmergencyRoomTreatmentBenefitMetadata.APPLY_BENEFIT_EERTB;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.EnhancedEmergencyRoomTreatmentBenefitMetadata.ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.EnhancedPhysicianSofficeUrgentCareTreatmentBenefitMetadata.APPLY_BENEFIT_EPCTB;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.HOSPITAL_ICU_CONFINEMENT_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.EnhancedPhysicianSofficeUrgentCareTreatmentBenefitMetadata.ENHANCED_PHYSICIAN_OFFICE_URGENT_CARE_TREATMENT_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.HealthScreeningBenefitMetadata.HEALTH_SCREENING_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.HospitalConfinementBenefitMetadata.APPLY_BENEFIT_HCB;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.HospitalConfinementBenefitMetadata.HOSPITAL_CONFINEMENT_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.HospitalIcuConfinementBenefitMetadata.APPLY_BENEFIT_HICUCB;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.HospitalIcuConfinementBenefitMetadata.HOSPITAL_ICU_CONFINEMENT_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.NUMBER_OF_UNITS;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigureUnitNumberAndPlanName extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    private static final ImmutableMap<AssetDescriptor<TextBox>, Integer> tabMapWithenhancedBenefitsAtoCTabAccidental = ImmutableMap.<AssetDescriptor<TextBox>, Integer>builder()
            .put(ACCIDENTAL_DEATH_BENEFIT_AMOUNT_FOR_COVERED_PERSON, 5000)
            .put(ACCIDENTAL_DEATH_BENEFIT_AMOUNT_FOR_COVERED_SPOUSE, 5000)
            .put(ACCIDENTAL_DEATH_BENEFIT_AMOUNT_FOR_EACH_COVERED_DEPENDENT_CHILD, 1500).build();

    private static final ImmutableMap<AssetDescriptor<TextBox>, Integer> tabMapWithenhancedBenefitsAtoCTabCatastrophic = ImmutableMap.<AssetDescriptor<TextBox>, Integer>builder()
            .put(CATASTROPHIC_BENEFIT_AMOUNT_PRIOR_TO_AGE_70_EMPLOYEE, 10000)
            .put(CATASTROPHIC_BENEFIT_AMOUNT_PRIOR_TO_AGE_70_SPOUSE, 10000)
            .put(CATASTROPHIC_BENEFIT_AMOUNT_PRIOR_TO_AGE_70_CHILD, 5000)
            .put(CATASTROPHIC_BENEFIT_AMOUNT_ON_OR_AFTER_AGE_70_EMPLOYEE, 5000)
            .put(CATASTROPHIC_BENEFIT_AMOUNT_ON_OR_AFTER_AGE_70_SPOUSE, 5000).build();

    private static final ImmutableMap<AssetDescriptor<DoubleTextBox>, Integer> fractureBenefitMap = ImmutableMap.<AssetDescriptor<DoubleTextBox>, Integer>builder()
            .put(FACE_OR_NOSE_EXCEPT_MANDIBLE_OR_MAXILLA, 70)
            .put(SKULL_FRACTURE_DEPRESSED_EXCEPT_BONES_OF_FACE_AND_NOSE, 500)
            .put(SKULL_FRACTURE_NON_DEPRESSED_EXCEPT_BONES_OF_FACE_AND_NOSE, 200)
            .put(LOWER_JAW_MANDIBLE_EXCEPT_ALVEOLAR_PROCESS, 60)
            .put(UPPER_JAW_MAXILLA_EXCEPT_ALVEOLAR_PROCESS, 70)
            .put(UPPER_ARM_BETWEEN_ELBOW_AND_SHOULDER, 70)
            .put(SHOULDER_BLADE_SCAPULA_COLLARBONE_CLAVICLE_STERNUM, 60)
            .put(FOREARM_RADIUS_AND_OR_ULNA_HAND_WRIST_EXCEPT_FINGERS, 60)
            .put(RIB, 50)
            .put(FINGER_TOE, 10)
            .put(VERTEBRAE_BODY_OF_EXCLUDING_VERTEBRAL_PROCESSES, 160)
            .put(VERTEBRAL_PROCESSES, 60)
            .put(PELVIS_INCLUDES_ILIUM_ISCHIUM_PUBIS_ACETABULUM_EXCEPT_COCCYX, 160)
            .put(HIP_THIGH_FEMUR, 300)
            .put(COCCYX, 40)
            .put(LEG_TIBIA_AND_OR_FIBULA, 160)
            .put(KNEECAP_PATELLA, 60)
            .put(ANKLE, 60)
            .put(FOOT_EXCEPT_TOES, 60).build();

    private static final ImmutableMap<AssetDescriptor<DoubleTextBox>, Integer> dislocationBenefitMap = ImmutableMap.<AssetDescriptor<DoubleTextBox>, Integer>builder()
            .put(LOWER_JAW, 60)
            .put(COLLARBONE_STERNOCLAVICULAR, 100)
            .put(COLLARBONE_ACROMIOCLAVICULAR_AND_SEPARATION, 20)
            .put(SHOULDER, 60)
            .put(ELBOW, 60)
            .put(WRIST, 60)
            .put(BONE_BONES_OF_THE_HAND, 60)
            .put(HIP, 400)
            .put(KNEE, 200)
            .put(ANKLE_BONE_OR_BONES_OF_THE_FOOT, 160)
            .put(ONE_OR_MORE_FINGERS_OR_TOES, 20).build();

    private static final ImmutableMap<AssetDescriptor<TextBox>, Integer> lossOfFingerMap = ImmutableMap.<AssetDescriptor<TextBox>, Integer>builder()
            .put(LOSS_OF_ONE_FINGER_OR_ONE_TOE_EMPLOYEE, 150)
            .put(LOSS_OF_ONE_FINGER_OR_ONE_TOE_SPOUSE, 150)
            .put(LOSS_OF_ONE_FINGER_OR_ONE_TOE_CHILD, 50)
            .put(LOSS_OF_ONE_HAND_OR_ONE_FOOT_OR_SIGHT_OF_ONE_EYE_EMPLOYEE, 1500)
            .put(LOSS_OF_ONE_HAND_OR_ONE_FOOT_OR_SIGHT_OF_ONE_EYE_SPOUSE, 1500)
            .put(LOSS_OF_ONE_HAND_OR_ONE_FOOT_OR_SIGHT_OF_ONE_EYE_CHILD, 500)
            .put(LOSS_OF_TWO_OR_MORE_FINGERS_TWO_OR_MORE_TOES_OR_ANY_COMBINATION_OF_TWO_OR_MORE_FINGERS_TO_TOES_EMPLOYEE, 300)
            .put(LOSS_OF_TWO_OR_MORE_FINGERS_TWO_OR_MORE_TOES_OR_ANY_COMBINATION_OF_TWO_OR_MORE_FINGERS_TO_TOES_SPOUSE, 300)
            .put(LOSS_OF_TWO_OR_MORE_FINGERS_TWO_OR_MORE_TOES_OR_ANY_COMBINATION_OF_TWO_OR_MORE_FINGERS_TO_TOES_CHILD, 100)
            .put(LOSS_OF_BOTH_HANDS_OR_BOTH_FEET_OR_SIGHT_IN_BOTH_EYES_OR_ANY_COMBINATION_OF_TWO_EMPLOYEE, 3000)
            .put(LOSS_OF_BOTH_HANDS_OR_BOTH_FEET_OR_SIGHT_IN_BOTH_EYES_OR_ANY_COMBINATION_OF_TWO_SPOUSE, 3000)
            .put(LOSS_OF_BOTH_HANDS_OR_BOTH_FEET_OR_SIGHT_IN_BOTH_EYES_OR_ANY_COMBINATION_OF_TWO_CHILD, 1000).build();

    private static final ImmutableMap<AssetDescriptor<TextBox>, String> tabMapWithenhancedBenefitsHtoLTabLaceration = ImmutableMap.<AssetDescriptor<TextBox>, String>builder()
            .put(REPAIRED_WITHOUT_STITCHES_BENEFIT_AMOUNT, "2.5")
            .put(BENEFIT_AMOUNT_FOR_LACERATION_LESS_THAN, "5")
            .put(BENEFIT_AMOUNT_FOR_LACERATION, "20")
            .put(BENEFIT_AMOUNT_FOR_LACERATION_OVER, "40").build();

    private static final ImmutableMap<AssetDescriptor<TextBox>, Integer> tabMapWithenhancedBenefitsMtoTTabSurgery = ImmutableMap.<AssetDescriptor<TextBox>, Integer>builder()
            .put(HERNIATED_DISC_BENEFIT_AMOUNT, 40)
            .put(TORN_RUPTURED_OR_SEVERED_TENDON, 60)
            .put(TORN_RUPTURED_OR_SEVERED_OF_TWO_OR_MORE, 100)
            .put(TORN_KNEE_CARTILAGE_SURGERY_BENEFIT_AMOUNT, 80)
            .put(EXPLORATORY_SURGERY_FOR_TENDON, 20).build();

    private static final ImmutableMap<AssetDescriptor<TextBox>, Integer> tabMapWithOptionalBenefitsTab = ImmutableMap.<AssetDescriptor<TextBox>, Integer>builder()
            .put(ACCIDENTAL_DEATH_BENEFIT_AMOUNT_EMPLOYEE, 5000)
            .put(ACCIDENTAL_DEATH_BENEFIT_AMOUNT_SPOUSE, 5000)
            .put(ACCIDENTAL_DEATH_BENEFIT_AMOUNT_CHILD, 1000).build();


    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20148", "REN-20162"}, component = POLICY_GROUPBENEFITS)
    public void testAttributeVerification() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "GA"), planDefinitionTab.getClass());
        List<String> numberOfUnitsValues = IntStream.range(1, 21).mapToObj(String::valueOf).collect(Collectors.toList());

        assertSoftly(softly -> {
            LOGGER.info("REN-20148 Steps 5");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(BASE_BUY_UP, ENHANCED_10_UNITS));
            softly.assertThat(PlanDefinitionTab.tableCoverageDefinition).hasMatchingRows(TableConstants.CoverageDefinition.PLAN.getName(), "BASEBU-" + BASE_BUY_UP);
            softly.assertThat(PlanDefinitionTab.tableCoverageDefinition).hasMatchingRows(TableConstants.CoverageDefinition.PLAN.getName(), "ENHANCED10-" + ENHANCED_10_UNITS);

            LOGGER.info("REN-20162 Steps 2");
            PlanDefinitionTab.tableCoverageDefinition.getRow(ImmutableMap.of(TableConstants.CoverageDefinition.PLAN.getName(), PolicyConstants.PlanAccidentSelectionValues.PLAN_BASE_BUY_UP,
                    TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), CoveragesConstants.GroupAccidentCoverages.ENHANCED_ACCIDENT)).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN_NAME)).isPresent().isRequired().isEnabled().hasValue(BASE_BUY_UP);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS)).isPresent().isRequired().isEnabled().hasValue("10").hasOptions(numberOfUnitsValues);

            PlanDefinitionTab.tableCoverageDefinition.getRowContains(TableConstants.CoverageDefinition.PLAN.getName(), ENHANCED_10_UNITS).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN_NAME)).isPresent().isRequired().isEnabled().hasValue(ENHANCED_10_UNITS);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS)).isPresent().isRequired().isEnabled().hasValue("10").hasOptions(numberOfUnitsValues);
            softly.assertThat(PlanDefinitionTab.tableCoverageDefinition).hasMatchingRows(TableConstants.CoverageDefinition.PLAN.getName(), "ENHANCED10-" + ENHANCED_10_UNITS);

            LOGGER.info("REN-20162 Steps 6");
            planDefinitionTab.getAssetList().getAsset(PLAN_NAME).setValue(BASE_BUY_UP);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN_NAME)).hasWarningWithText("'Plan Name' is duplicate with existing one, please enter a different Plan Name.");

            LOGGER.info("REN-20148 Steps 8");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.OPTIONAL_BENEFITS.get());
            optionalBenefitTab.getAssetList().getAsset(OptionalBenefitTabMetaData.INCLUDE_OPTIONAL_BENEFITS).setValue(VALUE_YES);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(OptionalBenefitTabMetaData.NUMBER_OF_UNITS)).isPresent().isRequired().isEnabled().hasValue("1").hasOptions(numberOfUnitsValues);
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20188"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureUnitNumberAndPlanNameBasicAccident() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "GA"), planDefinitionTab.getClass());

        assertSoftly(softly -> {
            LOGGER.info("REN-20188 Steps 2");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(BASE_BUY_UP));
            softly.assertThat(PlanDefinitionTab.tableCoverageDefinition).hasMatchingRows(TableConstants.CoverageDefinition.PLAN.getName(), PolicyConstants.PlanAccidentSelectionValues.PLAN_BASE_BUY_UP);
            double numberOfUnitsFour = Double.parseDouble(planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS).getValue());

            LOGGER.info("REN-20188 Steps 8");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.BASIC_BENEFITS.get());
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(BasicBenefitsTabMetaData.HOSPITAL_ADMISSION_BENEFIT).getAsset(HOSPITAL_ADMISSION_BENEFIT_AMT)).hasValue(new Currency("500").multiply(numberOfUnitsFour).toString());
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(BasicBenefitsTabMetaData.HOSPITAL_ADMISSION_BENEFIT).getAsset(BasicBenefitsTabMetaData.HospitalAdmissionBenefitMetaData.INTENSIVE_CARE_UNIT_ADMISSION_BENEFIT_AMOUNT)).hasValue(new Currency("1000").multiply(numberOfUnitsFour).toString());
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(BasicBenefitsTabMetaData.MAJOR_DIAGNOSTIC_BENEFIT).getAsset(BasicBenefitsTabMetaData.MajorDiagnosticBenefitMetadata.MAJOR_DIAGNOSTIC_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsFour).toString());
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(BasicBenefitsTabMetaData.EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(BasicBenefitsTabMetaData.EmergencyRoomTreatmentMetaData.EMERGENCY_ROOM_BENEFIT_AMOUNT)).hasValue(new Currency("50").multiply(numberOfUnitsFour).toString());
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(BasicBenefitsTabMetaData.GROUND_AMBULANCE_BENEFIT).getAsset(BasicBenefitsTabMetaData.GroundAmbulanceBenefitMetaData.GROUND_AMBULANCE_BENEFIT_AMT)).hasValue(new Currency("100").multiply(numberOfUnitsFour).toString());
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(BasicBenefitsTabMetaData.PHYICAL_THERAPY_SERVICE_BENEFIT).getAsset(BasicBenefitsTabMetaData.PhysicalTherapyServiceBenefitMetaData.PHY_BENEFIT_AMT)).hasValue(new Currency("25").multiply(numberOfUnitsFour).toString());
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(BasicBenefitsTabMetaData.AIR_AMBULANCE_BENEFIT).getAsset(BasicBenefitsTabMetaData.AirAmbulanceBenefitMetaData.AIR_AMBULANCE_BENEFIT_AMT)).hasValue(new Currency("500").multiply(numberOfUnitsFour).toString());

            LOGGER.info("REN-20188 Steps 13");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.OPTIONAL_BENEFITS.get());
            optionalBenefitTab.getAssetList().getAsset(OptionalBenefitTabMetaData.INCLUDE_OPTIONAL_BENEFITS).setValue(VALUE_YES);
            double numberOfUnitsOptionalDefOne = Double.parseDouble(planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS).getValue());
            optionalBenefitTab.getAssetList().getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(APPLY_BENEFIT_EERTB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsOptionalDefOne).toString());
            optionalBenefitTab.getAssetList().getAsset(ENHANCED_PHYSICIAN_SOFFICE_URGENT_CARE_TREATMENT_BENEFIT).getAsset(APPLY_BENEFIT_EPCTB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_PHYSICIAN_SOFFICE_URGENT_CARE_TREATMENT_BENEFIT).getAsset(ENHANCED_PHYSICIAN_OFFICE_URGENT_CARE_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("25").multiply(numberOfUnitsOptionalDefOne).toString());
            optionalBenefitTab.getAssetList().getAsset(HOSPITAL_CONFINEMENT_BENEFIT).getAsset(APPLY_BENEFIT_HCB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HOSPITAL_CONFINEMENT_BENEFIT).getAsset(HOSPITAL_CONFINEMENT_BENEFIT_AMOUNT)).hasValue(new Currency("40").multiply(numberOfUnitsOptionalDefOne).toString());
            optionalBenefitTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(APPLY_BENEFIT_HICUCB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsOptionalDefOne).toString());
            optionalBenefitTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(APPLY_BENEFIT_ADB).setValue(true);

            tabMapWithOptionalBenefitsTab.forEach((asset, value1) ->
                    softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(asset)).hasValue(new Currency(value1).multiply(numberOfUnitsOptionalDefOne).toString()));

            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HEALTH_SCREENING_BENEFIT).getAsset(HEALTH_SCREENING_BENEFIT_AMOUNT)).hasValue(new Currency("25").multiply(numberOfUnitsOptionalDefOne).toString());

            LOGGER.info("REN-20188 Steps 20");
            planDefinitionTab.navigate();
            planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS).setValue("1");
            double numberOfUnitsPlanDefOne = Double.parseDouble(planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS).getValue());
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.BASIC_BENEFITS.get());
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(BasicBenefitsTabMetaData.HOSPITAL_ADMISSION_BENEFIT).getAsset(HOSPITAL_ADMISSION_BENEFIT_AMT)).hasValue(new Currency("500").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(BasicBenefitsTabMetaData.HOSPITAL_ADMISSION_BENEFIT).getAsset(BasicBenefitsTabMetaData.HospitalAdmissionBenefitMetaData.INTENSIVE_CARE_UNIT_ADMISSION_BENEFIT_AMOUNT)).hasValue(new Currency("1000").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(BasicBenefitsTabMetaData.MAJOR_DIAGNOSTIC_BENEFIT).getAsset(BasicBenefitsTabMetaData.MajorDiagnosticBenefitMetadata.MAJOR_DIAGNOSTIC_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(BasicBenefitsTabMetaData.EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(BasicBenefitsTabMetaData.EmergencyRoomTreatmentMetaData.EMERGENCY_ROOM_BENEFIT_AMOUNT)).hasValue(new Currency("50").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(BasicBenefitsTabMetaData.GROUND_AMBULANCE_BENEFIT).getAsset(BasicBenefitsTabMetaData.GroundAmbulanceBenefitMetaData.GROUND_AMBULANCE_BENEFIT_AMT)).hasValue(new Currency("100").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(BasicBenefitsTabMetaData.PHYICAL_THERAPY_SERVICE_BENEFIT).getAsset(BasicBenefitsTabMetaData.PhysicalTherapyServiceBenefitMetaData.PHY_BENEFIT_AMT)).hasValue(new Currency("25").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(BasicBenefitsTabMetaData.AIR_AMBULANCE_BENEFIT).getAsset(BasicBenefitsTabMetaData.AirAmbulanceBenefitMetaData.AIR_AMBULANCE_BENEFIT_AMT)).hasValue(new Currency("500").multiply(numberOfUnitsPlanDefOne).toString());

            LOGGER.info("REN-20188 Steps 21");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.OPTIONAL_BENEFITS.get());
            optionalBenefitTab.getAssetList().getAsset(OptionalBenefitTabMetaData.INCLUDE_OPTIONAL_BENEFITS).setValue(VALUE_YES);
            double numberOfUnitsOptionalDefTen = Double.parseDouble(planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS).getValue());
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsOptionalDefTen).toString());
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_PHYSICIAN_SOFFICE_URGENT_CARE_TREATMENT_BENEFIT).getAsset(ENHANCED_PHYSICIAN_OFFICE_URGENT_CARE_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("25").multiply(numberOfUnitsOptionalDefTen).toString());
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HOSPITAL_CONFINEMENT_BENEFIT).getAsset(HOSPITAL_CONFINEMENT_BENEFIT_AMOUNT)).hasValue(new Currency("40").multiply(numberOfUnitsOptionalDefTen).toString());
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsOptionalDefTen).toString());
            tabMapWithOptionalBenefitsTab.forEach((asset, value1) ->
                    softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(asset)).hasValue(new Currency(value1).multiply(numberOfUnitsOptionalDefTen).toString()));
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HEALTH_SCREENING_BENEFIT).getAsset(HEALTH_SCREENING_BENEFIT_AMOUNT)).hasValue(new Currency("25").multiply(numberOfUnitsOptionalDefTen).toString());
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20189"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureUnitNumberAndPlanNameBaseBuyUpPlan() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "GA"), planDefinitionTab.getClass());

        LOGGER.info("REN-20189 Steps 2");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(BASE_BUY_UP, ENHANCED_10_UNITS));
        assertThat(PlanDefinitionTab.tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.PLAN.getName(), PolicyConstants.PlanAccidentSelectionValues.PLAN_BASE_BUY_UP)).isPresent();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(BASE_BUY_UP));
        PlanDefinitionTab.tableCoverageDefinition.findRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), CoveragesConstants.GroupAccidentCoverages.ENHANCED_ACCIDENT)
                .getCell(7).controls.links.get(ActionConstants.CHANGE).click();
        double numberOfUnitsTen = Double.parseDouble(planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS).getValue());

        LOGGER.info("REN-20189 Steps 8");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_A_TO_C.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(APPLIANCE_BENEFIT).getAsset(APPLIANCE_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(CONCUSSION_BENEFIT).getAsset(CONCUSSION_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(COMA_BENEFIT).getAsset(COMA_BENEFIT_AMOUNT)).hasValue(new Currency("1250").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT).getAsset(ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT).getAsset(ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(BLOOD_PLASMA_PLATELETS_BENEFIT).getAsset(BLOOD_PLASMA_PLATELETS_BENEFIT_AMOUNT)).hasValue(new Currency("50").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT).getAsset(GROUND_AMBULANCE_BENEFIT_AMOUNT)).hasValue(new Currency("40").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(AIR_AMBULANCE_BENEFIT_AMOUNT)).hasValue(new Currency("120").multiply(numberOfUnitsTen).toString());

            tabMapWithenhancedBenefitsAtoCTabAccidental.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsTen).toString()));

            tabMapWithenhancedBenefitsAtoCTabCatastrophic.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(CATASTROPHIC_ACCIDENT_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsTen).toString()));
        });


        LOGGER.info("REN-20189 Steps 11");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_D_TO_F.get());
        assertSoftly(softly -> dislocationBenefitMap.forEach((asset, value) ->
                softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(DISLOCATION_BENEFIT).getAsset(asset))
                        .hasValue(ImmutableList.of(new Currency(value).multiply(numberOfUnitsTen).toString(), new Currency(value).multiply(2).multiply(numberOfUnitsTen).toString()))));

        assertSoftly(softly -> fractureBenefitMap.forEach((asset, value) -> {
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(FRACTURE_BENEFIT).getAsset(asset))
                    .hasValue(ImmutableList.of(new Currency(value).multiply(numberOfUnitsTen).toString(), new Currency(value).multiply(2).multiply(numberOfUnitsTen).toString()));
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(FRACTURE_BENEFIT).getAsset(MAXIMUM_FRACTURE_BENEFIT_AMOUNT)).hasValue(new Currency(1000).multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EYE_INJURY_BENEFIT).getAsset(EYE_INJURY_BENEFIT_AMOUNT)).hasValue(new Currency("20").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(EMERGENCY_ROOM_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsTen).toString());
        }));


        LOGGER.info("REN-20189 Steps 14");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_H_TO_L.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(HOSPITAL_ADMISSION_BENEFIT_AMOUNT)).hasValue(new Currency("200").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(INTENSIVE_CARE_UNIT_ADMISSION_BENEFIT_AMOUNT)).hasValue(new Currency("400").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(INTERNAL_ORGAN_LOSS_BENEFIT).getAsset(INTERNAL_ORGAN_LOSS_BENEFIT_AMOUNT)).hasValue(new Currency("250").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(INTENSIVE_CARE_UNIT_CONFINEMENT_BENEFIT_AMOUNT_PER_DAY)).hasValue(new Currency("50").multiply(numberOfUnitsTen).toString());

            tabMapWithenhancedBenefitsHtoLTabLaceration.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LACERATION_BENEFIT_INFO).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsTen).toString()));
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LODGING_BENEFIT).getAsset(CONFINEMENT_LODGING_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsTen).toString());

            lossOfFingerMap.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LOSS_OF_FINGER_TOE_HAND_FOOT_OR_SIGHT_OF_AN_EYE_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsTen).toString()));
        });

        LOGGER.info("REN-20189 Steps 17");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_M_TO_T.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(ONE_PROSTHETIC_DEVICE)).hasValue(new Currency("50").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(MORE_THAN_ONE_PROSTHETIC_DEVICE)).hasValue(new Currency("100").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(MAJOR_DIAGNOSTIC_BENEFIT).getAsset(MAJOR_DIAGNOSTIC_BENEFIT_AMOUNT)).hasValue(new Currency("60").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT).getAsset(PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SPORTS_INJURY_BENEFIT).getAsset(SPORTS_INJURY_BENEFIT_LESSER_OF_25PERCENT_OF_ALL_BENEFITS_PAID)).hasValue(new Currency("100").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(TRANSPORTATION_BENEFIT).getAsset(TRANSPORTATION_BENEFIT_AMOUNT)).hasValue(new Currency("30").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICAL_THERAPY_SERVICE_BENEFIT).getAsset(PHYSICAL_THERAPY_SERVICES_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(REHABILITATION_UNIT_BENEFIT).getAsset(IMPATIEMT_REHABILITATION)).hasValue(new Currency("20").multiply(numberOfUnitsTen).toString());

            tabMapWithenhancedBenefitsMtoTTabSurgery.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SURGERY_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsTen).toString()));
        });

        LOGGER.info("REN-20189 Steps 22");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.OPTIONAL_BENEFITS.get());
        double numberOfUnitsOne = Double.parseDouble(optionalBenefitTab.getAssetList().getAsset(NUMBER_OF_UNITS).getValue());
        assertSoftly(softly -> {
            optionalBenefitTab.getAssetList().getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(APPLY_BENEFIT_EERTB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsOne).toString());
            optionalBenefitTab.getAssetList().getAsset(ENHANCED_PHYSICIAN_SOFFICE_URGENT_CARE_TREATMENT_BENEFIT).getAsset(APPLY_BENEFIT_EPCTB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_PHYSICIAN_SOFFICE_URGENT_CARE_TREATMENT_BENEFIT).getAsset(ENHANCED_PHYSICIAN_OFFICE_URGENT_CARE_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("25").multiply(numberOfUnitsOne).toString());
            optionalBenefitTab.getAssetList().getAsset(HOSPITAL_CONFINEMENT_BENEFIT).getAsset(APPLY_BENEFIT_HCB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HOSPITAL_CONFINEMENT_BENEFIT).getAsset(HOSPITAL_CONFINEMENT_BENEFIT_AMOUNT)).hasValue(new Currency("40").multiply(numberOfUnitsOne).toString());
            optionalBenefitTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(APPLY_BENEFIT_HICUCB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsOne).toString());
            optionalBenefitTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(APPLY_BENEFIT_ADB).setValue(true);

            tabMapWithOptionalBenefitsTab.forEach((asset, value1) ->
                    softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(asset)).hasValue(new Currency(value1).multiply(numberOfUnitsOne).toString()));

            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HEALTH_SCREENING_BENEFIT).getAsset(HEALTH_SCREENING_BENEFIT_AMOUNT)).hasValue(new Currency("25").multiply(numberOfUnitsOne).toString());
        });

        LOGGER.info("REN-20189 Steps 29");
        planDefinitionTab.navigate();
        planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS).setValue("1");
        double numberOfUnitsPlanDefOne = Double.parseDouble(planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS).getValue());
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_A_TO_C.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(APPLIANCE_BENEFIT).getAsset(APPLIANCE_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(CONCUSSION_BENEFIT).getAsset(CONCUSSION_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(COMA_BENEFIT).getAsset(COMA_BENEFIT_AMOUNT)).hasValue(new Currency("1250").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT).getAsset(ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT).getAsset(ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(BLOOD_PLASMA_PLATELETS_BENEFIT).getAsset(BLOOD_PLASMA_PLATELETS_BENEFIT_AMOUNT)).hasValue(new Currency("50").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT).getAsset(GROUND_AMBULANCE_BENEFIT_AMOUNT)).hasValue(new Currency("40").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(AIR_AMBULANCE_BENEFIT_AMOUNT)).hasValue(new Currency("120").multiply(numberOfUnitsPlanDefOne).toString());

            tabMapWithenhancedBenefitsAtoCTabAccidental.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString()));

            tabMapWithenhancedBenefitsAtoCTabCatastrophic.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(CATASTROPHIC_ACCIDENT_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString()));
        });

        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_D_TO_F.get());

        assertSoftly(softly -> dislocationBenefitMap.forEach((asset, value) ->
                softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(DISLOCATION_BENEFIT).getAsset(asset))
                        .hasValue(ImmutableList.of(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString(), new Currency(value).multiply(2).multiply(numberOfUnitsPlanDefOne).toString()))));

        assertSoftly(softly -> {
            fractureBenefitMap.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(FRACTURE_BENEFIT).getAsset(asset))
                            .hasValue(ImmutableList.of(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString(), new Currency(value).multiply(2).multiply(numberOfUnitsPlanDefOne).toString())));

            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(FRACTURE_BENEFIT).getAsset(MAXIMUM_FRACTURE_BENEFIT_AMOUNT)).hasValue(new Currency(1000).multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EYE_INJURY_BENEFIT).getAsset(EYE_INJURY_BENEFIT_AMOUNT)).hasValue(new Currency("20").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(EMERGENCY_ROOM_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsPlanDefOne).toString());
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_H_TO_L.get());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(HOSPITAL_ADMISSION_BENEFIT_AMOUNT)).hasValue(new Currency("200").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(INTENSIVE_CARE_UNIT_ADMISSION_BENEFIT_AMOUNT)).hasValue(new Currency("400").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(INTERNAL_ORGAN_LOSS_BENEFIT).getAsset(INTERNAL_ORGAN_LOSS_BENEFIT_AMOUNT)).hasValue(new Currency("250").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(INTENSIVE_CARE_UNIT_CONFINEMENT_BENEFIT_AMOUNT_PER_DAY)).hasValue(new Currency("50").multiply(numberOfUnitsPlanDefOne).toString());

            tabMapWithenhancedBenefitsHtoLTabLaceration.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LACERATION_BENEFIT_INFO).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString()));
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LODGING_BENEFIT).getAsset(CONFINEMENT_LODGING_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsPlanDefOne).toString());
            lossOfFingerMap.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LOSS_OF_FINGER_TOE_HAND_FOOT_OR_SIGHT_OF_AN_EYE_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString()));
        });

        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_M_TO_T.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(ONE_PROSTHETIC_DEVICE)).hasValue(new Currency("50").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(MORE_THAN_ONE_PROSTHETIC_DEVICE)).hasValue(new Currency("100").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(MAJOR_DIAGNOSTIC_BENEFIT).getAsset(MAJOR_DIAGNOSTIC_BENEFIT_AMOUNT)).hasValue(new Currency("60").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT).getAsset(PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SPORTS_INJURY_BENEFIT).getAsset(SPORTS_INJURY_BENEFIT_LESSER_OF_25PERCENT_OF_ALL_BENEFITS_PAID)).hasValue(new Currency("100").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(TRANSPORTATION_BENEFIT).getAsset(TRANSPORTATION_BENEFIT_AMOUNT)).hasValue(new Currency("30").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICAL_THERAPY_SERVICE_BENEFIT).getAsset(PHYSICAL_THERAPY_SERVICES_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(REHABILITATION_UNIT_BENEFIT).getAsset(IMPATIEMT_REHABILITATION)).hasValue(new Currency("20").multiply(numberOfUnitsPlanDefOne).toString());

            tabMapWithenhancedBenefitsMtoTTabSurgery.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SURGERY_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString()));
        });

        LOGGER.info("REN-20189 Steps 30");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.OPTIONAL_BENEFITS.get());
        optionalBenefitTab.getAssetList().getAsset(NUMBER_OF_UNITS).setValue("10");
        double numberOfUnitsOptionalDefTen = Double.parseDouble(optionalBenefitTab.getAssetList().getAsset(NUMBER_OF_UNITS).getValue());
        assertSoftly(softly -> {
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsOptionalDefTen).toString());
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_PHYSICIAN_SOFFICE_URGENT_CARE_TREATMENT_BENEFIT).getAsset(ENHANCED_PHYSICIAN_OFFICE_URGENT_CARE_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("25").multiply(numberOfUnitsOptionalDefTen).toString());
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HOSPITAL_CONFINEMENT_BENEFIT).getAsset(HOSPITAL_CONFINEMENT_BENEFIT_AMOUNT)).hasValue(new Currency("40").multiply(numberOfUnitsOptionalDefTen).toString());
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsOptionalDefTen).toString());

            tabMapWithOptionalBenefitsTab.forEach((asset, value1) ->
                    softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(asset)).hasValue(new Currency(value1).multiply(numberOfUnitsOptionalDefTen).toString()));

            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HEALTH_SCREENING_BENEFIT).getAsset(HEALTH_SCREENING_BENEFIT_AMOUNT)).hasValue(new Currency("25").multiply(numberOfUnitsOptionalDefTen).toString());
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20191"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureUnitNumberAndPlanNameEnhancedPlan() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "GA"), planDefinitionTab.getClass());

        LOGGER.info("REN-20191 Steps 2");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(ENHANCED_10_UNITS, VOLUNTARY_10_UNITS));
        assertThat(PlanDefinitionTab.tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.PLAN.getName(), PolicyConstants.PlanAccidentSelectionValues.PLAN_ENHANCED_10_UNITS)).isPresent();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(ENHANCED_10_UNITS));
        double numberOfUnitsTen = Double.parseDouble(planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS).getValue());

        LOGGER.info("REN-20191 Steps 8");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_A_TO_C.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(APPLIANCE_BENEFIT).getAsset(APPLIANCE_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(CONCUSSION_BENEFIT).getAsset(CONCUSSION_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(COMA_BENEFIT).getAsset(COMA_BENEFIT_AMOUNT)).hasValue(new Currency("1250").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT).getAsset(ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT).getAsset(ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(BLOOD_PLASMA_PLATELETS_BENEFIT).getAsset(BLOOD_PLASMA_PLATELETS_BENEFIT_AMOUNT)).hasValue(new Currency("50").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT).getAsset(GROUND_AMBULANCE_BENEFIT_AMOUNT)).hasValue(new Currency("40").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(AIR_AMBULANCE_BENEFIT_AMOUNT)).hasValue(new Currency("120").multiply(numberOfUnitsTen).toString());

            tabMapWithenhancedBenefitsAtoCTabAccidental.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsTen).toString()));

            tabMapWithenhancedBenefitsAtoCTabCatastrophic.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(CATASTROPHIC_ACCIDENT_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsTen).toString()));
        });

        LOGGER.info("REN-20191 Steps 11");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_D_TO_F.get());

        assertSoftly(softly -> dislocationBenefitMap.forEach((asset, value) ->
                softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(DISLOCATION_BENEFIT).getAsset(asset))
                        .hasValue(ImmutableList.of(new Currency(value).multiply(numberOfUnitsTen).toString(), new Currency(value).multiply(2).multiply(numberOfUnitsTen).toString()))));

        assertSoftly(softly -> {
            fractureBenefitMap.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(FRACTURE_BENEFIT).getAsset(asset))
                            .hasValue(ImmutableList.of(new Currency(value).multiply(numberOfUnitsTen).toString(), new Currency(value).multiply(2).multiply(numberOfUnitsTen).toString())));

            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(FRACTURE_BENEFIT).getAsset(MAXIMUM_FRACTURE_BENEFIT_AMOUNT)).hasValue(new Currency(1000).multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EYE_INJURY_BENEFIT).getAsset(EYE_INJURY_BENEFIT_AMOUNT)).hasValue(new Currency("20").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(EMERGENCY_ROOM_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsTen).toString());
        });

        LOGGER.info("REN-20191 Steps 14");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_H_TO_L.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(HOSPITAL_ADMISSION_BENEFIT_AMOUNT)).hasValue(new Currency("200").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(INTENSIVE_CARE_UNIT_ADMISSION_BENEFIT_AMOUNT)).hasValue(new Currency("400").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(INTERNAL_ORGAN_LOSS_BENEFIT).getAsset(INTERNAL_ORGAN_LOSS_BENEFIT_AMOUNT)).hasValue(new Currency("250").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(INTENSIVE_CARE_UNIT_CONFINEMENT_BENEFIT_AMOUNT_PER_DAY)).hasValue(new Currency("50").multiply(numberOfUnitsTen).toString());

            tabMapWithenhancedBenefitsHtoLTabLaceration.forEach((asset1, value1) ->
                    softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LACERATION_BENEFIT_INFO).getAsset(asset1)).hasValue(new Currency(value1).multiply(numberOfUnitsTen).toString()));
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LODGING_BENEFIT).getAsset(CONFINEMENT_LODGING_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsTen).toString());
            lossOfFingerMap.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LOSS_OF_FINGER_TOE_HAND_FOOT_OR_SIGHT_OF_AN_EYE_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsTen).toString()));
        });


        LOGGER.info("REN-20191 Steps 17");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_M_TO_T.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(ONE_PROSTHETIC_DEVICE)).hasValue(new Currency("50").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(MORE_THAN_ONE_PROSTHETIC_DEVICE)).hasValue(new Currency("100").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(MAJOR_DIAGNOSTIC_BENEFIT).getAsset(MAJOR_DIAGNOSTIC_BENEFIT_AMOUNT)).hasValue(new Currency("60").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT).getAsset(PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SPORTS_INJURY_BENEFIT).getAsset(SPORTS_INJURY_BENEFIT_LESSER_OF_25PERCENT_OF_ALL_BENEFITS_PAID)).hasValue(new Currency("100").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(TRANSPORTATION_BENEFIT).getAsset(TRANSPORTATION_BENEFIT_AMOUNT)).hasValue(new Currency("30").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICAL_THERAPY_SERVICE_BENEFIT).getAsset(PHYSICAL_THERAPY_SERVICES_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(REHABILITATION_UNIT_BENEFIT).getAsset(IMPATIEMT_REHABILITATION)).hasValue(new Currency("20").multiply(numberOfUnitsTen).toString());

            tabMapWithenhancedBenefitsMtoTTabSurgery.forEach((asset1, value1) ->
                    softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SURGERY_BENEFIT).getAsset(asset1)).hasValue(new Currency(value1).multiply(numberOfUnitsTen).toString()));
        });

        LOGGER.info("REN-20191 Steps 22");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.OPTIONAL_BENEFITS.get());
        double numberOfUnitsOne = Double.parseDouble(optionalBenefitTab.getAssetList().getAsset(NUMBER_OF_UNITS).getValue());
        assertSoftly(softly -> {
            optionalBenefitTab.getAssetList().getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(APPLY_BENEFIT_EERTB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsOne).toString());
            optionalBenefitTab.getAssetList().getAsset(ENHANCED_PHYSICIAN_SOFFICE_URGENT_CARE_TREATMENT_BENEFIT).getAsset(APPLY_BENEFIT_EPCTB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_PHYSICIAN_SOFFICE_URGENT_CARE_TREATMENT_BENEFIT).getAsset(ENHANCED_PHYSICIAN_OFFICE_URGENT_CARE_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("25").multiply(numberOfUnitsOne).toString());
            optionalBenefitTab.getAssetList().getAsset(HOSPITAL_CONFINEMENT_BENEFIT).getAsset(APPLY_BENEFIT_HCB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HOSPITAL_CONFINEMENT_BENEFIT).getAsset(HOSPITAL_CONFINEMENT_BENEFIT_AMOUNT)).hasValue(new Currency("40").multiply(numberOfUnitsOne).toString());
            optionalBenefitTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(APPLY_BENEFIT_HICUCB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsOne).toString());
            optionalBenefitTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(APPLY_BENEFIT_ADB).setValue(true);

            tabMapWithOptionalBenefitsTab.forEach((asset, value1) ->
                    softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(asset)).hasValue(new Currency(value1).multiply(numberOfUnitsOne).toString()));

            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HEALTH_SCREENING_BENEFIT).getAsset(HEALTH_SCREENING_BENEFIT_AMOUNT)).hasValue(new Currency("25").multiply(numberOfUnitsOne).toString());
        });

        LOGGER.info("REN-20191 Steps 29");
        planDefinitionTab.navigate();
        planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS).setValue("1");
        double numberOfUnitsPlanDefOne = Double.parseDouble(planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS).getValue());
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_A_TO_C.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(APPLIANCE_BENEFIT).getAsset(APPLIANCE_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(CONCUSSION_BENEFIT).getAsset(CONCUSSION_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(COMA_BENEFIT).getAsset(COMA_BENEFIT_AMOUNT)).hasValue(new Currency("1250").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT).getAsset(ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT).getAsset(ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(BLOOD_PLASMA_PLATELETS_BENEFIT).getAsset(BLOOD_PLASMA_PLATELETS_BENEFIT_AMOUNT)).hasValue(new Currency("50").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT).getAsset(GROUND_AMBULANCE_BENEFIT_AMOUNT)).hasValue(new Currency("40").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(AIR_AMBULANCE_BENEFIT_AMOUNT)).hasValue(new Currency("120").multiply(numberOfUnitsPlanDefOne).toString());

            tabMapWithenhancedBenefitsAtoCTabAccidental.forEach((asset, value) -> {
                softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString());
            });

            tabMapWithenhancedBenefitsAtoCTabCatastrophic.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(CATASTROPHIC_ACCIDENT_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString()));
        });

        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_D_TO_F.get());
        assertSoftly(softly -> dislocationBenefitMap.forEach((asset, value) ->
                softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(DISLOCATION_BENEFIT).getAsset(asset))
                        .hasValue(ImmutableList.of(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString(), new Currency(value).multiply(2).multiply(numberOfUnitsPlanDefOne).toString()))));

        assertSoftly(softly -> {
            fractureBenefitMap.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(FRACTURE_BENEFIT).getAsset(asset))
                            .hasValue(ImmutableList.of(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString(), new Currency(value).multiply(2).multiply(numberOfUnitsPlanDefOne).toString())));

            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(FRACTURE_BENEFIT).getAsset(MAXIMUM_FRACTURE_BENEFIT_AMOUNT)).hasValue(new Currency(1000).multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EYE_INJURY_BENEFIT).getAsset(EYE_INJURY_BENEFIT_AMOUNT)).hasValue(new Currency("20").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(EMERGENCY_ROOM_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsPlanDefOne).toString());
        });

        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_H_TO_L.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(HOSPITAL_ADMISSION_BENEFIT_AMOUNT)).hasValue(new Currency("200").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(INTENSIVE_CARE_UNIT_ADMISSION_BENEFIT_AMOUNT)).hasValue(new Currency("400").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(INTERNAL_ORGAN_LOSS_BENEFIT).getAsset(INTERNAL_ORGAN_LOSS_BENEFIT_AMOUNT)).hasValue(new Currency("250").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(INTENSIVE_CARE_UNIT_CONFINEMENT_BENEFIT_AMOUNT_PER_DAY)).hasValue(new Currency("50").multiply(numberOfUnitsPlanDefOne).toString());

            tabMapWithenhancedBenefitsHtoLTabLaceration.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LACERATION_BENEFIT_INFO).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString()));
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LODGING_BENEFIT).getAsset(CONFINEMENT_LODGING_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsPlanDefOne).toString());
            lossOfFingerMap.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LOSS_OF_FINGER_TOE_HAND_FOOT_OR_SIGHT_OF_AN_EYE_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString()));
        });

        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_M_TO_T.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(ONE_PROSTHETIC_DEVICE)).hasValue(new Currency("50").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(MORE_THAN_ONE_PROSTHETIC_DEVICE)).hasValue(new Currency("100").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(MAJOR_DIAGNOSTIC_BENEFIT).getAsset(MAJOR_DIAGNOSTIC_BENEFIT_AMOUNT)).hasValue(new Currency("60").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT).getAsset(PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SPORTS_INJURY_BENEFIT).getAsset(SPORTS_INJURY_BENEFIT_LESSER_OF_25PERCENT_OF_ALL_BENEFITS_PAID)).hasValue(new Currency("100").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(TRANSPORTATION_BENEFIT).getAsset(TRANSPORTATION_BENEFIT_AMOUNT)).hasValue(new Currency("30").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICAL_THERAPY_SERVICE_BENEFIT).getAsset(PHYSICAL_THERAPY_SERVICES_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(REHABILITATION_UNIT_BENEFIT).getAsset(IMPATIEMT_REHABILITATION)).hasValue(new Currency("20").multiply(numberOfUnitsPlanDefOne).toString());

            tabMapWithenhancedBenefitsMtoTTabSurgery.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SURGERY_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString()));
        });

        LOGGER.info("REN-20191 Steps 30");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.OPTIONAL_BENEFITS.get());
        optionalBenefitTab.getAssetList().getAsset(OptionalBenefitTabMetaData.NUMBER_OF_UNITS).setValue("10");
        double numberOfUnitsOptionalDefTen = Double.parseDouble(optionalBenefitTab.getAssetList().getAsset(NUMBER_OF_UNITS).getValue());
        assertSoftly(softly -> {
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsOptionalDefTen).toString());
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_PHYSICIAN_SOFFICE_URGENT_CARE_TREATMENT_BENEFIT).getAsset(ENHANCED_PHYSICIAN_OFFICE_URGENT_CARE_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("25").multiply(numberOfUnitsOptionalDefTen).toString());
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HOSPITAL_CONFINEMENT_BENEFIT).getAsset(HOSPITAL_CONFINEMENT_BENEFIT_AMOUNT)).hasValue(new Currency("40").multiply(numberOfUnitsOptionalDefTen).toString());
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsOptionalDefTen).toString());

            tabMapWithOptionalBenefitsTab.forEach((asset, value1) ->
                    softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(asset)).hasValue(new Currency(value1).multiply(numberOfUnitsOptionalDefTen).toString()));

            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HEALTH_SCREENING_BENEFIT).getAsset(HEALTH_SCREENING_BENEFIT_AMOUNT)).hasValue(new Currency("25").multiply(numberOfUnitsOptionalDefTen).toString());
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20192"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureUnitNumberAndPlanNameVoluntaryPlan() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "GA"), planDefinitionTab.getClass());

        LOGGER.info("REN-20192 Steps 2");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(VOLUNTARY_10_UNITS, ENHANCED_10_UNITS));
        assertThat(PlanDefinitionTab.tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.PLAN.getName(), PolicyConstants.PlanAccidentSelectionValues.PLAN_VOLUNTARY_10_UNITS)).isPresent();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(VOLUNTARY_10_UNITS));
        double numberOfUnitsTen = Double.parseDouble(planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS).getValue());

        LOGGER.info("REN-20192 Steps 8");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_A_TO_C.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(APPLIANCE_BENEFIT).getAsset(APPLIANCE_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(CONCUSSION_BENEFIT).getAsset(CONCUSSION_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(COMA_BENEFIT).getAsset(COMA_BENEFIT_AMOUNT)).hasValue(new Currency("1250").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT).getAsset(ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT).getAsset(ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(BLOOD_PLASMA_PLATELETS_BENEFIT).getAsset(BLOOD_PLASMA_PLATELETS_BENEFIT_AMOUNT)).hasValue(new Currency("50").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT).getAsset(GROUND_AMBULANCE_BENEFIT_AMOUNT)).hasValue(new Currency("40").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(AIR_AMBULANCE_BENEFIT_AMOUNT)).hasValue(new Currency("120").multiply(numberOfUnitsTen).toString());

            tabMapWithenhancedBenefitsAtoCTabAccidental.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsTen).toString()));

            tabMapWithenhancedBenefitsAtoCTabCatastrophic.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(CATASTROPHIC_ACCIDENT_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsTen).toString()));
        });

        LOGGER.info("REN-20192 Steps 11");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_D_TO_F.get());

        assertSoftly(softly -> dislocationBenefitMap.forEach((asset, value) ->
                softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(DISLOCATION_BENEFIT).getAsset(asset))
                        .hasValue(ImmutableList.of(new Currency(value).multiply(numberOfUnitsTen).toString(), new Currency(value).multiply(2).multiply(numberOfUnitsTen).toString()))));

        assertSoftly(softly -> {
            fractureBenefitMap.forEach((asset, value) -> softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(FRACTURE_BENEFIT).getAsset(asset))
                    .hasValue(ImmutableList.of(new Currency(value).multiply(numberOfUnitsTen).toString(), new Currency(value).multiply(2).multiply(numberOfUnitsTen).toString())));

            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(FRACTURE_BENEFIT).getAsset(MAXIMUM_FRACTURE_BENEFIT_AMOUNT)).hasValue(new Currency(1000).multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EYE_INJURY_BENEFIT).getAsset(EYE_INJURY_BENEFIT_AMOUNT)).hasValue(new Currency("20").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(EMERGENCY_ROOM_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsTen).toString());
        });

        LOGGER.info("REN-20192 Steps 14");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_H_TO_L.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(HOSPITAL_ADMISSION_BENEFIT_AMOUNT)).hasValue(new Currency("200").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(INTENSIVE_CARE_UNIT_ADMISSION_BENEFIT_AMOUNT)).hasValue(new Currency("400").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(INTERNAL_ORGAN_LOSS_BENEFIT).getAsset(INTERNAL_ORGAN_LOSS_BENEFIT_AMOUNT)).hasValue(new Currency("250").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(INTENSIVE_CARE_UNIT_CONFINEMENT_BENEFIT_AMOUNT_PER_DAY)).hasValue(new Currency("50").multiply(numberOfUnitsTen).toString());

            tabMapWithenhancedBenefitsHtoLTabLaceration.forEach((asset1, value1) ->
                    softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LACERATION_BENEFIT_INFO).getAsset(asset1)).hasValue(new Currency(value1).multiply(numberOfUnitsTen).toString()));
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LODGING_BENEFIT).getAsset(CONFINEMENT_LODGING_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsTen).toString());
            lossOfFingerMap.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LOSS_OF_FINGER_TOE_HAND_FOOT_OR_SIGHT_OF_AN_EYE_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsTen).toString()));
        });

        LOGGER.info("REN-20192 Steps 17");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_M_TO_T.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(ONE_PROSTHETIC_DEVICE)).hasValue(new Currency("50").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(MORE_THAN_ONE_PROSTHETIC_DEVICE)).hasValue(new Currency("100").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(MAJOR_DIAGNOSTIC_BENEFIT).getAsset(MAJOR_DIAGNOSTIC_BENEFIT_AMOUNT)).hasValue(new Currency("60").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT).getAsset(PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SPORTS_INJURY_BENEFIT).getAsset(SPORTS_INJURY_BENEFIT_LESSER_OF_25PERCENT_OF_ALL_BENEFITS_PAID)).hasValue(new Currency("100").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(TRANSPORTATION_BENEFIT).getAsset(TRANSPORTATION_BENEFIT_AMOUNT)).hasValue(new Currency("30").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICAL_THERAPY_SERVICE_BENEFIT).getAsset(PHYSICAL_THERAPY_SERVICES_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsTen).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(REHABILITATION_UNIT_BENEFIT).getAsset(IMPATIEMT_REHABILITATION)).hasValue(new Currency("20").multiply(numberOfUnitsTen).toString());

            tabMapWithenhancedBenefitsMtoTTabSurgery.forEach((asset1, value1) ->
                    softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SURGERY_BENEFIT).getAsset(asset1)).hasValue(new Currency(value1).multiply(numberOfUnitsTen).toString()));
        });

        LOGGER.info("REN-20192 Steps 22");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.OPTIONAL_BENEFITS.get());
        double numberOfUnitsOne = Double.parseDouble(optionalBenefitTab.getAssetList().getAsset(OptionalBenefitTabMetaData.NUMBER_OF_UNITS).getValue());
        assertSoftly(softly -> {
            optionalBenefitTab.getAssetList().getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(APPLY_BENEFIT_EERTB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsOne).toString());
            optionalBenefitTab.getAssetList().getAsset(ENHANCED_PHYSICIAN_SOFFICE_URGENT_CARE_TREATMENT_BENEFIT).getAsset(APPLY_BENEFIT_EPCTB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_PHYSICIAN_SOFFICE_URGENT_CARE_TREATMENT_BENEFIT).getAsset(ENHANCED_PHYSICIAN_OFFICE_URGENT_CARE_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("25").multiply(numberOfUnitsOne).toString());
            optionalBenefitTab.getAssetList().getAsset(HOSPITAL_CONFINEMENT_BENEFIT).getAsset(APPLY_BENEFIT_HCB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HOSPITAL_CONFINEMENT_BENEFIT).getAsset(HOSPITAL_CONFINEMENT_BENEFIT_AMOUNT)).hasValue(new Currency("40").multiply(numberOfUnitsOne).toString());
            optionalBenefitTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(APPLY_BENEFIT_HICUCB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsOne).toString());
            optionalBenefitTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(APPLY_BENEFIT_ADB).setValue(true);

            tabMapWithOptionalBenefitsTab.forEach((asset, value1) ->
                    softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(asset)).hasValue(new Currency(value1).multiply(numberOfUnitsOne).toString()));

            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HEALTH_SCREENING_BENEFIT).getAsset(HEALTH_SCREENING_BENEFIT_AMOUNT)).hasValue(new Currency("25").multiply(numberOfUnitsOne).toString());
        });

        LOGGER.info("REN-20192 Steps 29");
        planDefinitionTab.navigate();
        planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS).setValue("1");
        double numberOfUnitsPlanDefOne = Double.parseDouble(planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS).getValue());
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_A_TO_C.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(APPLIANCE_BENEFIT).getAsset(APPLIANCE_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(CONCUSSION_BENEFIT).getAsset(CONCUSSION_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(COMA_BENEFIT).getAsset(COMA_BENEFIT_AMOUNT)).hasValue(new Currency("1250").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT).getAsset(ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT).getAsset(ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(BLOOD_PLASMA_PLATELETS_BENEFIT).getAsset(BLOOD_PLASMA_PLATELETS_BENEFIT_AMOUNT)).hasValue(new Currency("50").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT).getAsset(GROUND_AMBULANCE_BENEFIT_AMOUNT)).hasValue(new Currency("40").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(AIR_AMBULANCE_BENEFIT_AMOUNT)).hasValue(new Currency("120").multiply(numberOfUnitsPlanDefOne).toString());

            tabMapWithenhancedBenefitsAtoCTabAccidental.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString()));

            tabMapWithenhancedBenefitsAtoCTabCatastrophic.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(CATASTROPHIC_ACCIDENT_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString()));
        });

        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_D_TO_F.get());

        assertSoftly(softly -> dislocationBenefitMap.forEach((asset, value) ->
                softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(DISLOCATION_BENEFIT).getAsset(asset))
                        .hasValue(ImmutableList.of(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString(), new Currency(value).multiply(2).multiply(numberOfUnitsPlanDefOne).toString()))));

        assertSoftly(softly -> {
            fractureBenefitMap.forEach((asset, value) -> softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(FRACTURE_BENEFIT).getAsset(asset))
                    .hasValue(ImmutableList.of(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString(), new Currency(value).multiply(2).multiply(numberOfUnitsPlanDefOne).toString())));

            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(FRACTURE_BENEFIT).getAsset(MAXIMUM_FRACTURE_BENEFIT_AMOUNT)).hasValue(new Currency(1000).multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EYE_INJURY_BENEFIT).getAsset(EYE_INJURY_BENEFIT_AMOUNT)).hasValue(new Currency("20").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(EMERGENCY_ROOM_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsPlanDefOne).toString());
        });

        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_H_TO_L.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(HOSPITAL_ADMISSION_BENEFIT_AMOUNT)).hasValue(new Currency("200").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(INTENSIVE_CARE_UNIT_ADMISSION_BENEFIT_AMOUNT)).hasValue(new Currency("400").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(INTERNAL_ORGAN_LOSS_BENEFIT).getAsset(INTERNAL_ORGAN_LOSS_BENEFIT_AMOUNT)).hasValue(new Currency("250").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(INTENSIVE_CARE_UNIT_CONFINEMENT_BENEFIT_AMOUNT_PER_DAY)).hasValue(new Currency("50").multiply(numberOfUnitsPlanDefOne).toString());

            tabMapWithenhancedBenefitsHtoLTabLaceration.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LACERATION_BENEFIT_INFO).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString()));
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LODGING_BENEFIT).getAsset(CONFINEMENT_LODGING_BENEFIT_AMOUNT)).hasValue(new Currency("10").multiply(numberOfUnitsPlanDefOne).toString());
            lossOfFingerMap.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LOSS_OF_FINGER_TOE_HAND_FOOT_OR_SIGHT_OF_AN_EYE_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString()));
        });

        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_M_TO_T.get());
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(ONE_PROSTHETIC_DEVICE)).hasValue(new Currency("50").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(MORE_THAN_ONE_PROSTHETIC_DEVICE)).hasValue(new Currency("100").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(MAJOR_DIAGNOSTIC_BENEFIT).getAsset(MAJOR_DIAGNOSTIC_BENEFIT_AMOUNT)).hasValue(new Currency("60").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT).getAsset(PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SPORTS_INJURY_BENEFIT).getAsset(SPORTS_INJURY_BENEFIT_LESSER_OF_25PERCENT_OF_ALL_BENEFITS_PAID)).hasValue(new Currency("100").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(TRANSPORTATION_BENEFIT).getAsset(TRANSPORTATION_BENEFIT_AMOUNT)).hasValue(new Currency("30").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICAL_THERAPY_SERVICE_BENEFIT).getAsset(PHYSICAL_THERAPY_SERVICES_BENEFIT_AMOUNT)).hasValue(new Currency("5").multiply(numberOfUnitsPlanDefOne).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(REHABILITATION_UNIT_BENEFIT).getAsset(IMPATIEMT_REHABILITATION)).hasValue(new Currency("20").multiply(numberOfUnitsPlanDefOne).toString());

            tabMapWithenhancedBenefitsMtoTTabSurgery.forEach((asset, value) ->
                    softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SURGERY_BENEFIT).getAsset(asset)).hasValue(new Currency(value).multiply(numberOfUnitsPlanDefOne).toString()));
        });

        LOGGER.info("REN-20192 Steps 30");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.OPTIONAL_BENEFITS.get());
        optionalBenefitTab.getAssetList().getAsset(OptionalBenefitTabMetaData.NUMBER_OF_UNITS).setValue("10");
        Double numberOfUnitsOptionalDefTen = Double.parseDouble(optionalBenefitTab.getAssetList().getAsset(NUMBER_OF_UNITS).getValue());
        assertSoftly(softly -> {
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(new Currency(numberOfUnitsOptionalDefTen)).toString());
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_PHYSICIAN_SOFFICE_URGENT_CARE_TREATMENT_BENEFIT).getAsset(ENHANCED_PHYSICIAN_OFFICE_URGENT_CARE_TREATMENT_BENEFIT_AMOUNT)).hasValue(new Currency("25").multiply(new Currency(numberOfUnitsOptionalDefTen)).toString());
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HOSPITAL_CONFINEMENT_BENEFIT).getAsset(HOSPITAL_CONFINEMENT_BENEFIT_AMOUNT)).hasValue(new Currency("40").multiply(new Currency(numberOfUnitsOptionalDefTen)).toString());
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT_AMOUNT)).hasValue(new Currency("100").multiply(new Currency(numberOfUnitsOptionalDefTen)).toString());

            tabMapWithOptionalBenefitsTab.forEach((asset, value1) ->
                    softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT).getAsset(asset)).hasValue(new Currency(value1).multiply(numberOfUnitsOptionalDefTen).toString()));

            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(HEALTH_SCREENING_BENEFIT).getAsset(HEALTH_SCREENING_BENEFIT_AMOUNT)).hasValue(new Currency("25").multiply(new Currency(numberOfUnitsOptionalDefTen)).toString());
        });
    }
}
