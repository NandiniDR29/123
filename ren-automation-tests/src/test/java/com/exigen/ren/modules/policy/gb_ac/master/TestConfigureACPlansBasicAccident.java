package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupAccidentCoverages.BASIC_ACCIDENT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.BASE_BUY_UP;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.BasicBenefitsTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.BasicBenefitsTabMetaData.AirAmbulanceBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.BasicBenefitsTabMetaData.HospitalAdmissionBenefitMetaData.HOSP_INCURRAL_PERIOD_DAYS;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.BasicBenefitsTabMetaData.HospitalAdmissionBenefitMetaData.INTENSIVE_CARE_UNIT_ADMISSION_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.BasicBenefitsTabMetaData.MajorDiagnosticBenefitMetadata.APPLY_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.BasicBenefitsTabMetaData.MajorDiagnosticBenefitMetadata.INCURRAL_PERIOD_DAYS;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.AccidentalDeathBenefitMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.EnhancedEmergencyRoomTreatmentBenefitMetadata.APPLY_BENEFIT_EERTB;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.EnhancedEmergencyRoomTreatmentBenefitMetadata.ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.EnhancedPhysicianSofficeUrgentCareTreatmentBenefitMetadata.APPLY_BENEFIT_EPCTB;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.EnhancedPhysicianSofficeUrgentCareTreatmentBenefitMetadata.ENHANCED_PHYSICIAN_OFFICE_URGENT_CARE_TREATMENT_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.HospitalConfinementBenefitMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.HospitalIcuConfinementBenefitMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.WaiverOfPremiumBenefitMetadata.ELIMINATION_BENEFIT_PERIOD;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.NUMBER_OF_UNITS;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigureACPlansBasicAccident extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21307", "REN-21318"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureAcPlansBasicAccident() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "GA"), planDefinitionTab.getClass());

        LOGGER.info("Step 3");
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASE_BUY_UP));
        planDefinitionTab.getAssetList().getAsset(NUMBER_OF_UNITS).setValue("1");

        LOGGER.info("REN-21307, REN-21318. Step 4");
        basicBenefitsTab.navigateToTab();
        LOGGER.info("Checking 'Hospital Admission Benefit' section");
        assertSoftly(softly -> {
            AssetList hospitalAdmissionBenefit = basicBenefitsTab.getAssetList().getAsset(HOSPITAL_ADMISSION_BENEFIT);
            softly.assertThat(hospitalAdmissionBenefit.getAsset(HOSP_INCURRAL_PERIOD_DAYS)).hasValue("365");
            softly.assertThat(hospitalAdmissionBenefit.getAsset(INTENSIVE_CARE_UNIT_ADMISSION_BENEFIT_AMOUNT)).hasValue(new Currency(1000).toString());
            softly.assertThat(hospitalAdmissionBenefit.getAsset(HospitalAdmissionBenefitMetaData.TIME_PERIOD)).hasValue("Covered Accident");
        });

        LOGGER.info("Checking 'Major Diagnostic Benefit' section");
        assertSoftly(softly -> {
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(MAJOR_DIAGNOSTIC_BENEFIT).getAsset(APPLY_BENEFIT)).hasValue(true);
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(MAJOR_DIAGNOSTIC_BENEFIT).getAsset(INCURRAL_PERIOD_DAYS)).hasValue("365");
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(MAJOR_DIAGNOSTIC_BENEFIT).getAsset(MajorDiagnosticBenefitMetadata.TYPE_OF_INCURRAL_PERIOD)).hasValue("Days");
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(MAJOR_DIAGNOSTIC_BENEFIT).getAsset(MajorDiagnosticBenefitMetadata.MAXIMUM_NUMBER_OF_TESTS_FOR_THE_MAJOR_DIAGNOSTIC_BENEFIT)).hasValue("1");
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(MAJOR_DIAGNOSTIC_BENEFIT).getAsset(MajorDiagnosticBenefitMetadata.TIME_PERIOD)).hasValue("Covered Accident");
        });

        LOGGER.info("Checking 'Ground Ambulance Benefit' section");
        assertSoftly(softly -> {
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT).getAsset(GroundAmbulanceBenefitMetaData.TYPE_OF_INCURRAL_PERIOD)).hasValue("Days");
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT).getAsset(GroundAmbulanceBenefitMetaData.GROUND_MAX_TRANSPORT_NO)).hasValue("1");
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(GROUND_AMBULANCE_BENEFIT).getAsset(GroundAmbulanceBenefitMetaData.TIME_PERIOD)).hasValue("Covered Accident");
        });

        LOGGER.info("Checking 'Emergency Room Treatment Benefit' section");
        assertSoftly(softly -> {
            AssetList emergencyRoomTreatmentBenefit = basicBenefitsTab.getAssetList().getAsset(EMERGENCY_ROOM_TREATMENT_BENEFIT);
            softly.assertThat(emergencyRoomTreatmentBenefit.getAsset(EmergencyRoomTreatmentMetaData.APPLY_BENEFIT)).hasValue(true);
            softly.assertThat(emergencyRoomTreatmentBenefit.getAsset(EmergencyRoomTreatmentMetaData.INCURRAL_PERIOD_HOURS)).hasValue("72");
            softly.assertThat(emergencyRoomTreatmentBenefit.getAsset(EmergencyRoomTreatmentMetaData.TIME_PERIOD)).hasValue("Covered Accident");
            softly.assertThat(emergencyRoomTreatmentBenefit.getAsset(EmergencyRoomTreatmentMetaData.EMERGENCY_ROOM_BENEFIT_AMOUNT)).hasValue(new Currency(50).toString());
        });

        LOGGER.info("Checking 'Physical Therapy Service Benefit' section");
        assertSoftly(softly -> {
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(PHYICAL_THERAPY_SERVICE_BENEFIT).getAsset(PhysicalTherapyServiceBenefitMetaData.APPLY_BENEFIT)).hasValue(true);
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(PHYICAL_THERAPY_SERVICE_BENEFIT).getAsset(PhysicalTherapyServiceBenefitMetaData.PHY_MAX_NO_SERVICES)).hasValue("6");
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(PHYICAL_THERAPY_SERVICE_BENEFIT).getAsset(PhysicalTherapyServiceBenefitMetaData.TIME_PERIOD)).hasValue("Covered Accident");
        });

        LOGGER.info("Checking 'Air Ambulance Benefit' section");
        assertSoftly(softly -> {
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(TYPE_OF_INCURRAL_PERIOD)).hasValue("Hours");
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(INCURRAL_PERIOD_HOURS)).hasValue("48");
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(MAX_TRANSPORT_NO)).hasValue("1");
            softly.assertThat(basicBenefitsTab.getAssetList().getAsset(AIR_AMBULANCE_BENEFIT).getAsset(AirAmbulanceBenefitMetaData.TIME_PERIOD)).hasValue("Covered Accident");
        });

        LOGGER.info("Step 6");
        optionalBenefitTab.navigateToTab();
        optionalBenefitTab.getAssetList().getAsset(INCLUDE_OPTIONAL_BENEFITS).setValue(VALUE_YES);
        assertThat(optionalBenefitTab.getAssetList().getAsset(OptionalBenefitTabMetaData.NUMBER_OF_UNITS)).hasValue("1");

        LOGGER.info("Step 8");
        assertSoftly(softly -> {
            optionalBenefitTab.getAssetList().getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(APPLY_BENEFIT_EERTB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT).getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT_AMOUNT))
                    .isPresent().isRequired().hasValue(new Currency(100).toString());
        });

        assertSoftly(softly -> {
            optionalBenefitTab.getAssetList().getAsset(ENHANCED_PHYSICIAN_SOFFICE_URGENT_CARE_TREATMENT_BENEFIT).getAsset(APPLY_BENEFIT_EPCTB).setValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(ENHANCED_PHYSICIAN_SOFFICE_URGENT_CARE_TREATMENT_BENEFIT).getAsset(ENHANCED_PHYSICIAN_OFFICE_URGENT_CARE_TREATMENT_BENEFIT_AMOUNT))
                    .isPresent().isRequired().hasValue(new Currency(25).toString());
        });

        assertSoftly(softly -> {
            AssetList hospitalIcuConfinementBenefit = optionalBenefitTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT);
            hospitalIcuConfinementBenefit.getAsset(APPLY_BENEFIT_HICUCB).setValue(true);
            softly.assertThat(hospitalIcuConfinementBenefit.getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT_AMOUNT)).isPresent().isRequired().hasValue(new Currency(100).toString());
            softly.assertThat(hospitalIcuConfinementBenefit.getAsset(MAXIMUM_BENEFIT_PERIOD_DAYS_HICUCB)).isPresent().isRequired().hasValue("30");
        });

        assertSoftly(softly -> {
            AssetList hospitalConfinementBenefit = optionalBenefitTab.getAssetList().getAsset(HOSPITAL_CONFINEMENT_BENEFIT);
            hospitalConfinementBenefit.getAsset(APPLY_BENEFIT_HCB).setValue(true);
            softly.assertThat(hospitalConfinementBenefit.getAsset(HOSPITAL_CONFINEMENT_BENEFIT_AMOUNT)).isPresent().isRequired().hasValue(new Currency(40).toString());
            softly.assertThat(hospitalConfinementBenefit.getAsset(MAXIMUM_BENEFIT_PERIOD_DAYS_HCB)).isPresent().isRequired().hasValue("365");
        });

        assertSoftly(softly -> {
            AssetList accidentalDeathBenefit = optionalBenefitTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT);
            accidentalDeathBenefit.getAsset(APPLY_BENEFIT_ADB).setValue(true);
            softly.assertThat(accidentalDeathBenefit.getAsset(ACCIDENTAL_DEATH_BENEFIT_AMOUNT_EMPLOYEE)).isPresent().isRequired().hasValue(new Currency("5000").toString());
            softly.assertThat(accidentalDeathBenefit.getAsset(ACCIDENTAL_DEATH_BENEFIT_AMOUNT_SPOUSE)).isPresent().isRequired().hasValue(new Currency("5000").toString());
            softly.assertThat(accidentalDeathBenefit.getAsset(ACCIDENTAL_DEATH_BENEFIT_AMOUNT_CHILD)).isPresent().isRequired().hasValue(new Currency("1000").toString());
        });

        LOGGER.info("REN-21318 Step 7");
        assertThat(optionalBenefitTab.getAssetList().getAsset(WAIVER_OF_PREMIUM_BENEFIT).getAsset(ELIMINATION_BENEFIT_PERIOD)).isPresent()
                .hasValue("30/6").hasOptions("30/6", "30/9", "30/12");

    }
}
