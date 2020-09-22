package com.exigen.ren.modules.policy.gb_ac.master;


import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.EnhancedBenefitsAtoCTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.EnhancedBenefitsMtoTTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.istf.webdriver.controls.waiters.Waiters.SLEEP;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.AbdominalOrThoracicSurgeryBenefitMetaData.ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.AbdominalOrThoracicSurgeryBenefitMetaData.TREATMENT_PERIOD_HOURS;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.AccidentFollowUpTreatmentBenefitMetadata.ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.AccidentFollowUpTreatmentBenefitMetadata.TREATMENT_PERIOD_DAYS;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.PhysiciansOfficeUrgentCareTreatmentBenefitMetaData.PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.PhysiciansOfficeUrgentCareTreatmentBenefitMetaData.TREATMENT_PERIOD_DAYS_PHY_OFC_URGENT_CARE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsMtoTTabMetaData.SPORTS_INJURY_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.NUMBER_OF_UNITS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUpdateEnhancedBenefits extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {
    private AssetList enhancedBenefitAtoC1 = enhancedBenefitsAtoCTab.getAssetList().getAsset(ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT);
    private AssetList enhancedBenefitAtoC2 = enhancedBenefitsAtoCTab.getAssetList().getAsset(ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT);
    private AssetList enhancedBenefitMtoT1 = enhancedBenefitsMtoTTab.getAssetList().getAsset(PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT);
    private AssetList enhancedBenefitMtoT2 = enhancedBenefitsMtoTTab.getAssetList().getAsset(SPORTS_INJURY_BENEFIT);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-16233", "REN-16236"}, component = POLICY_GROUPBENEFITS)

    public void testUpdateEnhancedBenefits() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        TestData defaultTestData = groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_AllPlans").
                adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName()+"[1]", NUMBER_OF_UNITS.getLabel()), "1")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName()+"[2]", NUMBER_OF_UNITS.getLabel()), "1")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName()+"[3]", NUMBER_OF_UNITS.getLabel()), "1")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName()+"[4]", NUMBER_OF_UNITS.getLabel()), "1");
        groupAccidentMasterPolicy.initiate(defaultTestData);
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(defaultTestData, enhancedBenefitsAtoCTab.getClass());
        EnhancedBenefitsAtoCTab.planTable.getRows().forEach(row -> {
            row.getCell(7).controls.links.get(ActionConstants.CHANGE).click();
            LOGGER.info("REN-16233 TC 1 Step 1-4");
            abdominalOrThoracicSurgeryBenefit();
            LOGGER.info("REN-16233 TC 2 Step 1-4");
            accidentFollowUpTreatmentBenefit();
        });
        LOGGER.info("Navigating directly to Enhanced Benefit M to T tab");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_M_TO_T.get());

        //sleep to avoid issue with page elements loading
        SLEEP(5000).go();
        EnhancedBenefitsMtoTTab.planTable.getRows().forEach(row -> {
            row.getCell(7).controls.links.get(ActionConstants.CHANGE).click();
            LOGGER.info("REN-16236 TC 1 Step 1-4");
            physiciansOfficeUrgentCareBenefit();
            LOGGER.info("REN-16236 TC 2 Step 1-4");
            sportsInjuryBenefit();
        });
    }

    private void abdominalOrThoracicSurgeryBenefit() {
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitAtoC1).isPresent();
            softly.assertThat(enhancedBenefitAtoC1.getAsset(EnhancedBenefitsAtoCTabMetaData.AbdominalOrThoracicSurgeryBenefitMetaData.APPLY_BENEFIT)).isPresent().hasValue(true);
            enhancedBenefitAtoC1.getAsset(EnhancedBenefitsAtoCTabMetaData.AbdominalOrThoracicSurgeryBenefitMetaData.APPLY_BENEFIT).setValue(false);
            ImmutableList.of(ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT_AMOUNT, TREATMENT_PERIOD_HOURS).forEach(control ->
            {
                softly.assertThat(enhancedBenefitAtoC1.getAsset(control)).isAbsent();
            });
            enhancedBenefitAtoC1.getAsset(EnhancedBenefitsAtoCTabMetaData.AbdominalOrThoracicSurgeryBenefitMetaData.APPLY_BENEFIT).setValue(true);
            softly.assertThat(enhancedBenefitAtoC1.getAsset(ABDOMINAL_OR_THORACIC_SURGERY_BENEFIT_AMOUNT)).isPresent().isRequired().hasValue(new Currency("100").toString());
            softly.assertThat(enhancedBenefitAtoC1.getAsset(TREATMENT_PERIOD_HOURS)).isPresent().isRequired().hasValue("72");

        });
    }

    private void accidentFollowUpTreatmentBenefit() {
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitAtoC2).isPresent();
            softly.assertThat(enhancedBenefitAtoC2.getAsset(EnhancedBenefitsAtoCTabMetaData.AccidentFollowUpTreatmentBenefitMetadata.APPLY_BENEFIT)).isPresent().hasValue(true);
            enhancedBenefitAtoC2.getAsset(EnhancedBenefitsAtoCTabMetaData.AccidentFollowUpTreatmentBenefitMetadata.APPLY_BENEFIT).setValue(false);
            ImmutableList.of(ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT_AMOUNT, TREATMENT_PERIOD_DAYS).forEach(control -> {
                softly.assertThat(enhancedBenefitAtoC2.getAsset(control)).isAbsent();
            });
            enhancedBenefitAtoC2.getAsset(EnhancedBenefitsAtoCTabMetaData.AccidentFollowUpTreatmentBenefitMetadata.APPLY_BENEFIT).setValue(true);
            softly.assertThat(enhancedBenefitAtoC2.getAsset(ACCIDENT_FOLLOW_UP_TREATMENT_BENEFIT_AMOUNT)).isPresent().isRequired().hasValue(new Currency("5").toString());
            softly.assertThat(enhancedBenefitAtoC2.getAsset(TREATMENT_PERIOD_DAYS)).isPresent().isRequired().hasValue("365");
        });
    }

    private void physiciansOfficeUrgentCareBenefit() {
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitMtoT1).isPresent();
            softly.assertThat(enhancedBenefitMtoT1.getAsset(EnhancedBenefitsMtoTTabMetaData.PhysiciansOfficeUrgentCareTreatmentBenefitMetaData.APPLY_BENEFIT)).isPresent().hasValue(true);
            enhancedBenefitMtoT1.getAsset(EnhancedBenefitsMtoTTabMetaData.PhysiciansOfficeUrgentCareTreatmentBenefitMetaData.APPLY_BENEFIT).setValue(false);
            ImmutableList.of(PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT_AMOUNT, TREATMENT_PERIOD_DAYS).forEach(control -> {
                softly.assertThat(enhancedBenefitMtoT1.getAsset(control)).isAbsent();
            });
            enhancedBenefitMtoT1.getAsset(EnhancedBenefitsMtoTTabMetaData.PhysiciansOfficeUrgentCareTreatmentBenefitMetaData.APPLY_BENEFIT).setValue(true);
            softly.assertThat(enhancedBenefitMtoT1.getAsset(PHYSICIANS_OFFICE_URGENT_CARE_BENEFIT_AMOUNT)).isPresent().isRequired().hasValue(new Currency("5").toString());
            softly.assertThat(enhancedBenefitMtoT1.getAsset(TREATMENT_PERIOD_DAYS_PHY_OFC_URGENT_CARE)).isPresent().isRequired().hasValue("365");
        });
    }
    private void sportsInjuryBenefit() {
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitMtoT2).isPresent();
            softly.assertThat(enhancedBenefitMtoT2.getAsset(EnhancedBenefitsMtoTTabMetaData.SportsInjuryBenefitMetaData.APPLY_BENEFIT)).isPresent().hasValue(true);
            enhancedBenefitMtoT2.getAsset(EnhancedBenefitsMtoTTabMetaData.SportsInjuryBenefitMetaData.APPLY_BENEFIT).setValue(false);
            softly.assertThat(enhancedBenefitMtoT2.getAsset(EnhancedBenefitsMtoTTabMetaData.SportsInjuryBenefitMetaData.SPORTS_INJURY_BENEFIT_LESSER_OF_25PERCENT_OF_ALL_BENEFITS_PAID)).isAbsent();
            enhancedBenefitMtoT2.getAsset(EnhancedBenefitsMtoTTabMetaData.SportsInjuryBenefitMetaData.APPLY_BENEFIT).setValue(true);
            softly.assertThat(enhancedBenefitMtoT2.getAsset(EnhancedBenefitsMtoTTabMetaData.SportsInjuryBenefitMetaData.SPORTS_INJURY_BENEFIT_LESSER_OF_25PERCENT_OF_ALL_BENEFITS_PAID))
                    .isPresent().isRequired().hasValue(new Currency("100").toString());
        });
    }

}
