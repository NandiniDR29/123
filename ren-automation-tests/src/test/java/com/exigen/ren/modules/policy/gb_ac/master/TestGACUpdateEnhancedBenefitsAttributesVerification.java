package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.CoveragesConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.HospitalAdmissionBenefitMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.pages.NavigationPage.PolicyNavigation.leftMenu;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.BASE_BUY_UP;
import static com.exigen.ren.main.enums.TableConstants.CoverageDefinition.COVERAGE_NAME;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.AirAmbulanceBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.ApplianceBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.GroundAmbulanceBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsDtoFTabMetaData.EMERGENCY_ROOM_TREATMENT_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsDtoFTabMetaData.EmergencyRoomTreatmentBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.HOSPITAL_ADMISSION_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.HOSPITAL_ICU_CONFINEMENT_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.HospitalAdmissionBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.HospitalICUConfinementBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.MajorDiagnosticBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.PhysicalTherapyServiceBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.ProstheticDeviceBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.RehabilitationUnitBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.SurgeryBenefitMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestGACUpdateEnhancedBenefitsAttributesVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-16784", "REN-16786", "REN-16787", "REN-16788", "REN-16791", "REN-16792", "REN-16793", "REN-16794"}, component = POLICY_GROUPBENEFITS)
    public void testGACUpdateEnhancedBenefitsAttributesVerification() {
        mainApp().open();
        LOGGER.info("Test REN-16784, REN-16786, REN-16787, REN-16788 Preconditions");
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData(), planDefinitionTab.getClass());
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(BASE_BUY_UP));
        planDefinitionTab.tableCoverageDefinition.findRow(COVERAGE_NAME.getName(), CoveragesConstants.GroupAccidentCoverages.ENHANCED_ACCIDENT).getCell(7).controls.links.get(ActionConstants.CHANGE).click();

        LOGGER.info("Test REN-16784 Step 1");
        leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_A_TO_C.get());

        LOGGER.info("Test REN-16784 Step 2, REN-16791 TC1 Step 1");
        assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT)).isPresent();
        assertSoftly(softly -> {
            LOGGER.info("Test REN-16784 Step 3 and STEP 4, REN-16791 TC1 Step 2 and Step 3");
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(APPLY_BENEFIT_AIR_AMBULANCE)).isPresent().isOptional().isEnabled().hasValue(true);
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(INCURRAL_PERIOD_AIR_AMBULANCE)).isPresent().isRequired().isEnabled().hasValue("48");
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(TYPE_OF_INCURRAL_PERIOD_AIR_AMBULANCE)).isPresent().isRequired().isEnabled().hasOptions("Hours");
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(AIR_AMBULANCE_BENEFIT_AMOUNT)).isPresent().isRequired().isEnabled().hasValue(new Currency("120").multiply(10).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(MAXIMUM_NUMBER_OF_TRANSPORTS_FOR_THE_AIR_AMBULANCE_BENEFIT)).isPresent().isRequired().isEnabled().hasOptions("1");
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(TIME_PERIOD_AIR_AMBULANCE)).isPresent().isRequired().isEnabled().hasOptions("Covered Accident");
        });

        LOGGER.info("REN-16791 TC1 Step 7");
        enhancedBenefitsAtoCTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(APPLY_BENEFIT_AIR_AMBULANCE).setValue(false);
        assertSoftly(softly -> {
            ImmutableList.of(INCURRAL_PERIOD_AIR_AMBULANCE, TYPE_OF_INCURRAL_PERIOD_AIR_AMBULANCE, AIR_AMBULANCE_BENEFIT_AMOUNT, MAXIMUM_NUMBER_OF_TRANSPORTS_FOR_THE_AIR_AMBULANCE_BENEFIT, TIME_PERIOD_AIR_AMBULANCE).forEach(label ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(label))).isAbsent());
        });

        LOGGER.info("Test REN-16784 Step 5, REN-16791 TC2 Step 1");
        assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT)).isPresent();

        LOGGER.info("Test REN-16784 Step 6 and STEP 7, REN-16791 TC2 Step 2 and Step 3");
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT).getAsset(APPLY_BENEFIT_GROUND_AMBULANCE)).isPresent().isOptional().isEnabled().hasValue(true);
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT).getAsset(INCURRAL_PERIOD_GROUND_AMBULANCE)).isPresent().isRequired().isEnabled().hasValue("90");
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT).getAsset(TYPE_OF_INCURRAL_PERIOD_GROUND_AMBULANCE)).isPresent().isRequired().isEnabled().hasOptions("Days");
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT).getAsset(GROUND_AMBULANCE_BENEFIT_AMOUNT)).isPresent().isRequired().isEnabled().hasValue(new Currency("40").multiply(10).toString());
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT).getAsset(MAXIMUM_NUMBER_OF_TRANSPORTS_FOR_THE_GROUND_AMBULANCE_BENEFIT)).isPresent().isRequired().isEnabled().hasOptions("1");
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT).getAsset(TIME_PERIOD_GROUND_AMBULANCE)).isPresent().isRequired().isEnabled().hasOptions("Covered Accident");
        });

        LOGGER.info("REN-16791 TC2 Step 7");
        enhancedBenefitsAtoCTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT).getAsset(APPLY_BENEFIT_GROUND_AMBULANCE).setValue(false);
        assertSoftly(softly -> {
            ImmutableList.of(INCURRAL_PERIOD_GROUND_AMBULANCE, TYPE_OF_INCURRAL_PERIOD_GROUND_AMBULANCE, GROUND_AMBULANCE_BENEFIT_AMOUNT, MAXIMUM_NUMBER_OF_TRANSPORTS_FOR_THE_GROUND_AMBULANCE_BENEFIT, TIME_PERIOD_GROUND_AMBULANCE).forEach(label ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(label))).isAbsent());
        });

        LOGGER.info("Test REN-16784 Step 8");
        assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(APPLIANCE_BENEFIT)).isPresent();

        LOGGER.info("Test REN-16784 Step 9 and Step 11");
        assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(APPLIANCE_BENEFIT).getAsset(APPLY_BENEFIT_APPLIANCE_BENEFIT)).isPresent().isOptional().isEnabled().hasValue(true);
        assertSoftly(softly -> {
            ImmutableList.of(USE_OF_APPLIANCE_MUST_BEGIN_WITH_DAYS, APPLIANCE_BENEFIT_AMOUNT, TIME_PERIOD_APPLIANCE_BENEFIT).forEach(control ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(APPLIANCE_BENEFIT).getAsset(control)).isPresent().isEnabled().isRequired());
        });

        LOGGER.info("Test REN-16784 Step 10");
        assertSoftly(softly -> {
            ImmutableList.of("Cane Benefit Amount", "Crutches Benefit Amount", "Walking Boot Benefit Amount", "Wheelchair or Motorized Scooter Benefit Amount", "Other Medical Device Used for Mobility Benefit Option Amount", "Maximum benefit amount for the Medical Appliance Benefit per Time Period").forEach(label ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(label))).isAbsent());
        });

        LOGGER.info("Test REN-16786 Step 1, REN-16792 Step 1");
        leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_D_TO_F.get());

        LOGGER.info("Test REN-16786 Step 3 and Step 4, REN-16792 Step 2 and Step 3");
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(APPLY_BENEFIT_EMERGENCY)).isPresent().isOptional().isEnabled().hasValue(true);
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(INCURRAL_TIME_PERIOD_EMERGENCY)).isPresent().isRequired().isEnabled().hasValue("72");
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(EMERGENCY_ROOM_BENEFIT_AMOUNT)).isPresent().isRequired().isEnabled().hasValue(new Currency("5").multiply(10).toString());
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(TIME_PERIOD_EMERGENCY)).isPresent().isRequired().isEnabled().hasOptions("Covered Accident");
        });


        LOGGER.info("REN-16792 Step 7");
        enhancedBenefitsDtoFTab.getAssetList().getAsset(EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(APPLY_BENEFIT_EMERGENCY).setValue(false);
        assertSoftly(softly -> {
            ImmutableList.of(INCURRAL_TIME_PERIOD_EMERGENCY, EMERGENCY_ROOM_BENEFIT_AMOUNT, TIME_PERIOD_EMERGENCY).forEach(label ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(label))).isAbsent());
        });

        LOGGER.info("Test REN-16787 Step 1, REN-16793 Step 1");
        leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_H_TO_L.get());

        LOGGER.info("Test REN-16787 Step 2");
        assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT)).isPresent();

        LOGGER.info("Test REN-16787 Step 3 and Step 4, REN-16793  TC1 Step 2 and Step 3");
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(APPLY_BENEFIT_HOSPITAL_ADMISSION)).isPresent().isOptional().isEnabled().hasValue(true);
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(INCURRAL_PERIOD_HOSPITAL_ADMISSION)).isPresent().isRequired().isEnabled().hasValue("365");
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(HOSPITAL_ADMISSION_BENEFIT_AMOUNT)).isPresent().isRequired().isEnabled().hasValue(new Currency("200").multiply(10).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(INTENSIVE_CARE_UNIT_ADMISSION_BENEFIT_AMOUNT)).isPresent().isRequired().isEnabled().hasValue(new Currency("400").multiply(10).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(HospitalAdmissionBenefitMetaData.TIME_PERIOD)).isPresent().isRequired().isEnabled().hasOptions("Covered Accident", "Calendar Year");
        });

        LOGGER.info("REN-16793 TC1 Step 7");
        enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT).getAsset(APPLY_BENEFIT_HOSPITAL_ADMISSION).setValue(false);
        assertSoftly(softly -> {
            ImmutableList.of(INCURRAL_PERIOD_HOSPITAL_ADMISSION, HOSPITAL_ADMISSION_BENEFIT_AMOUNT, INTENSIVE_CARE_UNIT_ADMISSION_BENEFIT_AMOUNT, HospitalAdmissionBenefitMetaData.TIME_PERIOD).forEach(label ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(label))).isAbsent());
        });

        LOGGER.info("Test REN-16787 Step 5");
        assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT)).isPresent();

        LOGGER.info("Test REN-16787 Step 6 and Step 8");
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(APPLY_BENEFIT_HOSPITAL_ICU)).isPresent().isOptional().isEnabled().hasValue(true);
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(INCURRAL_PERIOD_HOSPITAL_ICU)).isPresent().isRequired().isEnabled().hasValue("365");
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(INTENSIVE_CARE_UNIT_CONFINEMENT_BENEFIT_AMOUNT_PER_DAY)).isPresent().isRequired().isEnabled().hasValue(new Currency("50").multiply(10).toString());
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(MAXIMUM_ICU_CONFINEMENT_PERIOD)).isPresent().isRequired().isEnabled().hasValue("30");
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(EnhancedBenefitsHtoLTabMetaData.HospitalICUConfinementBenefitMetaData.TIME_PERIOD)).isPresent().isRequired().isEnabled().hasOptions("Covered Accident");
        });

        LOGGER.info("REN-16793 TC2 Step 7");
        enhancedBenefitsHtoLTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT).getAsset(APPLY_BENEFIT_HOSPITAL_ICU).setValue(false);
        assertSoftly(softly -> {
            ImmutableList.of(INCURRAL_PERIOD_HOSPITAL_ICU, INTENSIVE_CARE_UNIT_CONFINEMENT_BENEFIT_AMOUNT_PER_DAY, MAXIMUM_ICU_CONFINEMENT_PERIOD, EnhancedBenefitsHtoLTabMetaData.HospitalICUConfinementBenefitMetaData.TIME_PERIOD).forEach(label ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(label))).isAbsent());
        });

        LOGGER.info("Test REN-16787 Step 7");
        assertSoftly(softly -> {
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Hospital Confinement Benefit Amount per day"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Maximum Hospital Confinement Benefit Amount"))).isAbsent();
        });

        LOGGER.info("Test REN-16788 Step 1, REN-16794 Step 1");
        leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_M_TO_T.get());

        LOGGER.info("Test REN-16788 Step 2");
        AssetList applyBenefitMajorDiagnostic = enhancedBenefitsMtoTTab.getAssetList().getAsset(MAJOR_DIAGNOSTIC_BENEFIT);

        LOGGER.info("Test REN-16788 Step 3 and Step 4, REN-16794 TC1 Step 2 and Step 3");
        Tab.doubleWaiter.go();
        assertSoftly(softly -> {
            softly.assertThat(applyBenefitMajorDiagnostic.getAsset(APPLY_BENEFIT_MAJOR_DIAGNOSTIC)).isPresent().isOptional().isEnabled().hasValue(true);
            softly.assertThat(applyBenefitMajorDiagnostic.getAsset(TYPE_OF_INCURRAL_PERIOD_MAJOR_DIAGNOSTIC)).isPresent().isRequired().isEnabled().hasOptions("Days");
            softly.assertThat(applyBenefitMajorDiagnostic.getAsset(INCURRAL_PERIOD_MAJOR_DIAGNOSTIC)).isPresent().isRequired().isEnabled().hasValue("90");
            softly.assertThat(applyBenefitMajorDiagnostic.getAsset(MAJOR_DIAGNOSTIC_BENEFIT_AMOUNT)).isPresent().isRequired().isEnabled().hasValue(new Currency("60").multiply(10).toString());
            softly.assertThat(applyBenefitMajorDiagnostic.getAsset(MAXIMUM_NUMBER_OF_TESTS_FOR_THE_MAJOR_DIAGNOSTIC_BENEFIT)).isPresent().isRequired().isEnabled().hasOptions("1");
            softly.assertThat(applyBenefitMajorDiagnostic.getAsset(TIME_PERIOD_MAJOR_DIAGNOSTIC)).isPresent().isRequired().isEnabled().hasOptions("Covered Accident");
        });

        LOGGER.info("REN-16794 TC1 Step 7");
        applyBenefitMajorDiagnostic.getAsset(APPLY_BENEFIT_MAJOR_DIAGNOSTIC).setValue(false);
        assertSoftly(softly -> {
            ImmutableList.of(TYPE_OF_INCURRAL_PERIOD_MAJOR_DIAGNOSTIC, INCURRAL_PERIOD_MAJOR_DIAGNOSTIC, MAJOR_DIAGNOSTIC_BENEFIT_AMOUNT, MAXIMUM_NUMBER_OF_TESTS_FOR_THE_MAJOR_DIAGNOSTIC_BENEFIT, TIME_PERIOD_MAJOR_DIAGNOSTIC).forEach(label ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(label))).isAbsent());
        });


        LOGGER.info("Test REN-16788 Step 5");
        assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICAL_THERAPY_SERVICE_BENEFIT)).isPresent();

        LOGGER.info("Test REN-16788 Step 6 and Step 7, REN-16794 TC2 Step 2 and Step 3");
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICAL_THERAPY_SERVICE_BENEFIT).getAsset(APPLY_BENEFIT_PHYSICAL_THERAPY)).isPresent().isOptional().isEnabled().hasValue(true);
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICAL_THERAPY_SERVICE_BENEFIT).getAsset(PHYSICAL_THERAPY_SERVICES_TREATMENT_PERIOD)).isPresent().isRequired().isEnabled().hasValue("365");
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICAL_THERAPY_SERVICE_BENEFIT).getAsset(PHYSICAL_THERAPY_SERVICES_BENEFIT_AMOUNT)).isPresent().isRequired().isEnabled().hasValue(new Currency("5").multiply(10).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICAL_THERAPY_SERVICE_BENEFIT).getAsset(MAXIMUM_NUMBER_OF_SERVICES_FOR_THE_MAXIMUM_SERVICES)).isPresent().isRequired().isEnabled().hasValue("6");
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICAL_THERAPY_SERVICE_BENEFIT).getAsset(TIME_PERIOD_PHYSICAL_THERAPY)).isPresent().isRequired().isEnabled().hasOptions("Covered Accident");
        });

        LOGGER.info("REN-16794 TC2 Step 7");
        assertSoftly(softly -> {
            enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICAL_THERAPY_SERVICE_BENEFIT).getAsset(APPLY_BENEFIT_PHYSICAL_THERAPY).setValue(false);
            ImmutableList.of(PHYSICAL_THERAPY_SERVICES_TREATMENT_PERIOD, PHYSICAL_THERAPY_SERVICES_BENEFIT_AMOUNT, MAXIMUM_NUMBER_OF_SERVICES_FOR_THE_MAXIMUM_SERVICES, TIME_PERIOD_PHYSICAL_THERAPY).forEach(label ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(label))).isAbsent());
        });

        LOGGER.info("Test REN-16788 Step 8");
        assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT)).isPresent();

        LOGGER.info("Test REN-16788 Step 9 and Step 10, REN-16794 TC3 Step 2 and Step 3");
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(APPLY_BENEFIT_PROSTHETIC_DEVICE)).isPresent().isOptional().isEnabled().hasValue(true);
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(TREATMENT_PERIOD_PROSTHETIC_DEVICE)).isPresent().isRequired().isEnabled().hasValue("365");
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(ONE_PROSTHETIC_DEVICE)).isPresent().isRequired().isEnabled().hasValue(new Currency("50").multiply(10).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(MORE_THAN_ONE_PROSTHETIC_DEVICE)).isPresent().isRequired().isEnabled().hasValue(new Currency("100").multiply(10).toString());
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(TIME_PERIOD_PROSTHETIC_DEVICE)).isPresent().isRequired().isEnabled().hasOptions("Covered Accident");
        });

        LOGGER.info("REN-16794 TC3 Step 7");
        assertSoftly(softly -> {
            enhancedBenefitsMtoTTab.getAssetList().getAsset(PROSTHETIC_DEVICE_BENEFIT).getAsset(APPLY_BENEFIT_PROSTHETIC_DEVICE).setValue(false);
            ImmutableList.of(TREATMENT_PERIOD_PROSTHETIC_DEVICE, ONE_PROSTHETIC_DEVICE, MORE_THAN_ONE_PROSTHETIC_DEVICE, TIME_PERIOD_PROSTHETIC_DEVICE).forEach(label ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(label))).isAbsent());
        });

        LOGGER.info("Test REN-16788 Step 11");
        assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SURGERY_BENEFIT)).isPresent();

        LOGGER.info("Test REN-16788 Step 12 and Step 14");
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SURGERY_BENEFIT).getAsset(APPLY_BENEFIT_SURGERY)).isPresent().isOptional().isEnabled().hasValue(true);
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SURGERY_BENEFIT).getAsset(SURGERY_PERFORMANCE_PERIOD)).isPresent().isEnabled().isRequired();
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SURGERY_BENEFIT).getAsset(TIME_PERIOD_SURGERY)).isPresent().isEnabled().isRequired();
            ImmutableList.of(TORN_RUPTURED_OR_SEVERED_TENDON, TORN_RUPTURED_OR_SEVERED_OF_TWO_OR_MORE, TORN_KNEE_CARTILAGE_SURGERY_BENEFIT_AMOUNT, EXPLORATORY_SURGERY_FOR_TENDON).forEach(control ->
                    softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(SURGERY_BENEFIT).getAsset(control)).isPresent().isOptional().isEnabled());
        });

        assertSoftly(softly -> {
            LOGGER.info("Test REN-16788 Step 13");
            ImmutableList.of("Cranial Surgery Benefit Amount", "Thoracic Cavity or Abdominal Pelvic Cavity Surgery Benefit Amount", "Ruptured Disc Repair Surgery Amount", "Torn Rotator Cuff Surgery Benefit Amount", "Arthroscopy Without Surgical Repair Benefit Amount", "Miscellaneous Surgery Benefit Amount", "General Anesthesia Benefit Amount", "Maximum Surgery Benefit Amount").forEach(label ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(label))).isAbsent());

            LOGGER.info("Test REN-16788 Step 15");
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(REHABILITATION_UNIT_BENEFIT)).isPresent();

            LOGGER.info("Test REN-16788 Step 16 and Step 18");
            softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(REHABILITATION_UNIT_BENEFIT).getAsset(APPLY_BENEFIT_REHABILITATION)).isPresent().isOptional().isEnabled().hasValue(true);
            ImmutableList.of(IMPATIEMT_REHABILITATION, MAXIMUM_NUMBER_OF_DAYS_FOR_REHABILITATION, TIME_PERIOD_REHABILITATION).forEach(control ->
                    softly.assertThat(enhancedBenefitsMtoTTab.getAssetList().getAsset(REHABILITATION_UNIT_BENEFIT).getAsset(control)).isPresent().isEnabled().isRequired());

            LOGGER.info("Test REN-16788 Step 17");
            ImmutableList.of("Inpatient Therapy Services must begin within (days)", "Activity the Inpatient Therapy Services must follow", "Mental Health").forEach(label ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(label))).isAbsent());
        });
    }
}
