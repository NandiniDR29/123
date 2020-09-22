package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.general.dblookups.DBLookupsContext;
import com.exigen.ren.admin.modules.general.dblookups.pages.DBLookupsPage;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.OptionalBenefitTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.exigen.ipb.eisa.verification.CustomAssertionsExtended.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.TableConstants.Plans.*;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.AccidentalDeathBenefitMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.EnhancedEmergencyRoomTreatmentBenefitMetadata.APPLY_BENEFIT_EERTB;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.EnhancedEmergencyRoomTreatmentBenefitMetadata.ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.EnhancedPhysicianSofficeUrgentCareTreatmentBenefitMetadata.APPLY_BENEFIT_EPCTB;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.EnhancedPhysicianSofficeUrgentCareTreatmentBenefitMetadata.ENHANCED_PHYSICIAN_OFFICE_URGENT_CARE_TREATMENT_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.HospitalConfinementBenefitMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.HospitalIcuConfinementBenefitMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.WaiverOfPremiumBenefitMetadata.APPLY_BENEFIT_WPB;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.WaiverOfPremiumBenefitMetadata.ELIMINATION_BENEFIT_PERIOD;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteConfigureOptionalBenefits extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext, DBLookupsContext {

    private static final String LABEL_SECTION_PATTERN = "//div[@id='policyDataGatherForm:componentViewForm_body']//*[normalize-space(text())=\"%s\"]";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-13331", "REN-13758", "REN-13771", "REN-13772", "REN-13729", "REN-13754", "REN-13757", "REN-13760"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteConfigureOptionalBenefits() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        TestData defaultTestData = groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_TwoCoveragesNonContributory");
        groupAccidentMasterPolicy.initiate(defaultTestData);
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(defaultTestData, planDefinitionTab.getClass(), true);
        List<Map<String, String>> planDefinitionPlans = getAllRowsWithSpecificColumns(PlanDefinitionTab.tableCoverageDefinition);
        planDefinitionTab.submitTab();
        groupAccidentMasterPolicy.getDefaultWorkspace().fillFromTo(defaultTestData, basicBenefitsTab.getClass(), optionalBenefitTab.getClass());

        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.OPTIONAL_BENEFITS.get());
        List<Map<String, String>> optionalBenefitPlans = getAllRowsWithSpecificColumns(OptionalBenefitTab.tablePlans);

        LOGGER.info("Assert for REN-13331/#1");
        assertThat(planDefinitionPlans).isEqualTo(optionalBenefitPlans);

        LOGGER.info("Assert for REN-13333/#1");
        assertSoftly(softly -> {
            softly.assertThat(new StaticElement(ByT.xpath(LABEL_SECTION_PATTERN).format("Optional Benefit"))).isPresent();
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(OptionalBenefitTabMetaData.INCLUDE_OPTIONAL_BENEFITS)).isRequired().hasValue(VALUE_YES);
        });

        optionalBenefitTab.getAssetList().getAsset(OptionalBenefitTabMetaData.INCLUDE_OPTIONAL_BENEFITS).setValue(VALUE_NO);
        LOGGER.info("Asserts for REN-13758/#1, REN-13771/#1, REN-13772/#1, REN-13729/#1");
        assertSoftly(softly -> {
            ImmutableList.of(
                    "Accidental Death Benefit",
                    "Enhanced Emergency Room Treatment Benefit",
                    "Enhanced Physician's Office/Urgent Care Treatment Benefit",
                    "Hospital Confinement Benefit",
                    "Hospital ICU Confinement Benefit",
                    "Waiver Of Premium Benefit").forEach(section ->
                    softly.assertThat(new StaticElement(ByT.xpath(LABEL_SECTION_PATTERN).format(section))).isAbsent());
        });

        LOGGER.info("Assert for REN-13333/#1");
        optionalBenefitTab.getAssetList().getAsset(OptionalBenefitTabMetaData.INCLUDE_OPTIONAL_BENEFITS).setValue(VALUE_YES);
        assertSoftly(softly -> {
            List<String> rangeFieldValues = getStringRange();
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(OptionalBenefitTabMetaData.NUMBER_OF_UNITS)).isRequired().hasValue("1")
                    .hasOptions(rangeFieldValues);
        });

        LOGGER.info("Asserts for REN-13758/#1, REN-13771/#1, REN-13772/#1, REN-13729/#1");
        assertSoftly(softly -> {
            ImmutableList.of(
                    "Accidental Death Benefit",
                    "Enhanced Emergency Room Treatment Benefit",
                    "Enhanced Physician's Office/Urgent Care Treatment Benefit",
                    "Hospital Confinement Benefit",
                    "Hospital ICU Confinement Benefit",
                    "Waiver Of Premium Benefit").forEach(section ->
                    softly.assertThat(new StaticElement(ByT.xpath(LABEL_SECTION_PATTERN).format(section))).isPresent());
        });

        LOGGER.info("Assert for REN-13758/#1");
        assertSoftly(softly -> {
            AssetList accidentalDeathBenefit = optionalBenefitTab.getAssetList().getAsset(ACCIDENTAL_DEATH_BENEFIT);
            softly.assertThat(accidentalDeathBenefit.getAsset(APPLY_BENEFIT_ADB)).isPresent().isEnabled().isOptional().hasValue(false);
            softly.assertThat(accidentalDeathBenefit.getAsset(ACCIDENTAL_DEATH_BENEFIT_AMOUNT_CHILD)).isAbsent();
            softly.assertThat(accidentalDeathBenefit.getAsset(ACCIDENTAL_DEATH_BENEFIT_AMOUNT_EMPLOYEE)).isAbsent();
            softly.assertThat(accidentalDeathBenefit.getAsset(ACCIDENTAL_DEATH_BENEFIT_AMOUNT_SPOUSE)).isAbsent();

            accidentalDeathBenefit.getAsset(APPLY_BENEFIT_ADB).setValue(true);
            softly.assertThat(accidentalDeathBenefit.getAsset(ACCIDENTAL_DEATH_BENEFIT_AMOUNT_CHILD)).isPresent().isRequired().hasValue(new Currency("1000").toString());
            softly.assertThat(accidentalDeathBenefit.getAsset(ACCIDENTAL_DEATH_BENEFIT_AMOUNT_EMPLOYEE)).isPresent().isRequired().hasValue(new Currency("5000").toString());
            softly.assertThat(accidentalDeathBenefit.getAsset(ACCIDENTAL_DEATH_BENEFIT_AMOUNT_SPOUSE)).isPresent().isRequired().hasValue(new Currency("5000").toString());
        });

        LOGGER.info("Assert for REN-13771/#1");
        assertSoftly(softly -> {
            AssetList enhancedEmergencyRoomTreatmentBenefit = optionalBenefitTab.getAssetList().getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT);
            softly.assertThat(enhancedEmergencyRoomTreatmentBenefit.getAsset(APPLY_BENEFIT_EERTB)).isPresent().isEnabled().isOptional().hasValue(false);
            softly.assertThat(enhancedEmergencyRoomTreatmentBenefit.getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT_AMOUNT)).isAbsent();
            enhancedEmergencyRoomTreatmentBenefit.getAsset(APPLY_BENEFIT_EERTB).setValue(true);
            softly.assertThat(enhancedEmergencyRoomTreatmentBenefit.getAsset(ENHANCED_EMERGENCY_ROOM_TREATMENT_BENEFIT_AMOUNT)).isPresent().isRequired().hasValue(new Currency(100).toString());
        });

        LOGGER.info("Assert for REN-13772/#1");
        assertSoftly(softly -> {
            AssetList enhancedPhysicianSofficeUrgentCareTreatmentBenefit = optionalBenefitTab.getAssetList().getAsset(ENHANCED_PHYSICIAN_SOFFICE_URGENT_CARE_TREATMENT_BENEFIT);
            softly.assertThat(enhancedPhysicianSofficeUrgentCareTreatmentBenefit.getAsset(APPLY_BENEFIT_EPCTB)).isPresent().isEnabled().isOptional().hasValue(false);
            softly.assertThat(enhancedPhysicianSofficeUrgentCareTreatmentBenefit.getAsset(ENHANCED_PHYSICIAN_OFFICE_URGENT_CARE_TREATMENT_BENEFIT_AMOUNT)).isAbsent();
            enhancedPhysicianSofficeUrgentCareTreatmentBenefit.getAsset(APPLY_BENEFIT_EPCTB).setValue(true);
            softly.assertThat(enhancedPhysicianSofficeUrgentCareTreatmentBenefit.getAsset(ENHANCED_PHYSICIAN_OFFICE_URGENT_CARE_TREATMENT_BENEFIT_AMOUNT)).isPresent().isRequired().hasValue(new Currency(25).toString());
        });

        LOGGER.info("Assert for REN-13754/#1");
        assertSoftly(softly -> {
            AssetList hospitalConfinementBenefit = optionalBenefitTab.getAssetList().getAsset(HOSPITAL_CONFINEMENT_BENEFIT);
            softly.assertThat(hospitalConfinementBenefit.getAsset(APPLY_BENEFIT_HCB)).isPresent().isEnabled().isOptional().hasValue(false);
            softly.assertThat(hospitalConfinementBenefit.getAsset(HOSPITAL_CONFINEMENT_BENEFIT_AMOUNT)).isAbsent();
            softly.assertThat(hospitalConfinementBenefit.getAsset(MAXIMUM_BENEFIT_PERIOD_DAYS_HCB)).isAbsent();
            hospitalConfinementBenefit.getAsset(APPLY_BENEFIT_HCB).setValue(true);
            softly.assertThat(hospitalConfinementBenefit.getAsset(HOSPITAL_CONFINEMENT_BENEFIT_AMOUNT)).isPresent().isRequired().hasValue(new Currency(40).toString());
            softly.assertThat(hospitalConfinementBenefit.getAsset(MAXIMUM_BENEFIT_PERIOD_DAYS_HCB)).isPresent().isRequired().hasValue("365");
        });

        LOGGER.info("Assert for REN-13757/#1");
        assertSoftly(softly -> {
            AssetList hospitalIcuConfinementBenefit = optionalBenefitTab.getAssetList().getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT);
            softly.assertThat(hospitalIcuConfinementBenefit.getAsset(APPLY_BENEFIT_HICUCB)).isPresent().isEnabled().isOptional().hasValue(false);
            softly.assertThat(hospitalIcuConfinementBenefit.getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT_AMOUNT)).isAbsent();
            softly.assertThat(hospitalIcuConfinementBenefit.getAsset(MAXIMUM_BENEFIT_PERIOD_DAYS_HICUCB)).isAbsent();
            hospitalIcuConfinementBenefit.getAsset(APPLY_BENEFIT_HICUCB).setValue(true);
            softly.assertThat(hospitalIcuConfinementBenefit.getAsset(HOSPITAL_ICU_CONFINEMENT_BENEFIT_AMOUNT)).isPresent().isRequired().hasValue(new Currency(100).toString());
            softly.assertThat(hospitalIcuConfinementBenefit.getAsset(MAXIMUM_BENEFIT_PERIOD_DAYS_HICUCB)).isPresent().isRequired().hasValue("30");
        });

        LOGGER.info("Assert for REN-13760/#1");
        assertSoftly(softly -> {
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(WAIVER_OF_PREMIUM_BENEFIT).getAsset(APPLY_BENEFIT_WPB)).isPresent().isEnabled().isOptional().hasValue(true);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(WAIVER_OF_PREMIUM_BENEFIT).getAsset(ELIMINATION_BENEFIT_PERIOD)).isPresent().hasValue("30/6");
            optionalBenefitTab.getAssetList().getAsset(WAIVER_OF_PREMIUM_BENEFIT).getAsset(APPLY_BENEFIT_WPB).setValue(false);
            softly.assertThat(optionalBenefitTab.getAssetList().getAsset(WAIVER_OF_PREMIUM_BENEFIT).getAsset(ELIMINATION_BENEFIT_PERIOD)).isAbsent();
            optionalBenefitTab.getAssetList().getAsset(WAIVER_OF_PREMIUM_BENEFIT).getAsset(APPLY_BENEFIT_WPB).setValue(true);
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13333", component = POLICY_GROUPBENEFITS)
    public void testRenNumberOfUnitsLookup() {
        adminApp().open();
        dbLookups.initiate("RenNumberOfUnits");
        DBLookupsPage.tableSearchResults.getRow(1).getCell(DBLookupsPage.DBLookupsSearchResults.NAME.getName()).controls.links.getFirst().click();

        // Get values from each column
        List<String> codeColumnValues = DBLookupsPage.tableLookupValues.getColumn(DBLookupsPage.DBLookupsValues.CODE).getValue();
        List<String> valueColumnValues = DBLookupsPage.tableLookupValues.getColumn(DBLookupsPage.DBLookupsValues.VALUE).getValue();
        List<String> orderColumnNoValues = DBLookupsPage.tableLookupValues.getColumn(DBLookupsPage.DBLookupsValues.ORDER_NO).getValue();

        LOGGER.info("Assert for REN-13333/#4");
        assertSoftly(softly -> {
            // Compare values from columns
            softly.assertThat(codeColumnValues).isEqualTo(valueColumnValues);
            softly.assertThat(codeColumnValues).isEqualTo(orderColumnNoValues);
            softly.assertThat(valueColumnValues).isEqualTo(orderColumnNoValues);

            softly.assertThat(
                    // Parse string list to integer list and sort it
                    codeColumnValues.stream().map(Integer::parseInt).sorted().collect(Collectors.toList()))
                    .isEqualTo(
                            // Assert with range from 1 to 20
                            IntStream.rangeClosed(1, 20).boxed().collect(Collectors.toList()));
        });
    }

    /**
     * Get all rows with specific columns from table.
     *
     * @param table Table with rows
     * @return List of rows with specific columns
     */
    private List<Map<String, String>> getAllRowsWithSpecificColumns(Table table) {
        return table.getRows().stream()
                .map(row -> row.getPartialValue(COVERAGE_NAME.getName(), PLAN.getName(), COVERAGE_TIERS.getName(), CONTRIBUTION_TYPE.getName(), RATE_BASIS.getName()))
                .collect(Collectors.toList());
    }

    private List<String> getStringRange() {
        // For each element from range (ex. 1, 2, 3, ...) parse integer element to string and collect it to list
        return IntStream.rangeClosed(1, 20).mapToObj(Integer::toString).collect(Collectors.toList());
    }
}