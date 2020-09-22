package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.EnhancedBenefitsAtoCTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_A_TO_C;
import static com.exigen.ren.common.enums.NavigationEnum.PlanGenericInfoTab.ENHANCED_BENEFITS_D_TO_F;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.BURN_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.BurnBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsDtoFTabMetaData.EMERGENCY_DENTAL_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsDtoFTabMetaData.EmergencyDentalBenefitMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestEnhancedBenefits extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18132", "REN-18134"}, component = POLICY_GROUPBENEFITS)
    public void testEnhancedBenefits() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData(), EnhancedBenefitsAtoCTab.class);
        assertSoftly(softly -> {
            LOGGER.info("Test REN-18132 Step 1 and REN-18134 TC1 Step 1");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.OPTIONAL_BENEFITS.get());
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Burn Benefit"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Emergency Dental Benefit"))).isAbsent();

            LOGGER.info("Test REN-18132 Step 2");
            NavigationPage.toLeftMenuTab(ENHANCED_BENEFITS_A_TO_C);
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(BURN_BENEFIT)).isPresent();

            LOGGER.info("Test REN-18132 Step 3");
            softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(BURN_BENEFIT).getAsset(APPLY_BENEFIT_BURN)).hasValue(true);
            ImmutableList.of(TREATMENT_PERIOD_HOURS, SECOND_DEGREE_BURNS, THIRD_DEGREE_BURNS_COVERS_LESS_INCHES, THIRD_DEGREE_BURNS_COVERS_MORE_INCHES).forEach(control ->
                    softly.assertThat(enhancedBenefitsAtoCTab.getAssetList().getAsset(BURN_BENEFIT).getAsset(control)).isPresent());

            LOGGER.info("Test REN-18134 TC1 Step 2 and TC2 Step 1");
            NavigationPage.toLeftMenuTab(ENHANCED_BENEFITS_D_TO_F);
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EMERGENCY_DENTAL_BENEFIT)).isPresent();

            LOGGER.info("Test REN-18134 TC1 Step 3, TC2 Step 2 and TC2 Step 3 ");
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EMERGENCY_DENTAL_BENEFIT).getAsset(APPLY_BENEFIT_EMERGENCY_DENTAL)).hasValue(true);
            ImmutableList.of(DENTAL_SERVICES, CROWN_BENEFIT_AMOUNT, EXTRACTION_BENEFIT_AMOUNT, NUMBER_OF_TIMES_EMERGENCY_DENTAL_PAYABLE, TIME_PERIOD).forEach(control ->
                    softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EMERGENCY_DENTAL_BENEFIT).getAsset(control)).isPresent());
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Crown Benefit Amount per tooth"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Extraction Benefit Amount per tooth"))).isAbsent();

            LOGGER.info("Test REN-18134 TC1 Step 4 and TC1 Step 5");
            softly.assertThat(enhancedBenefitsDtoFTab.getAssetList().getAsset(EMERGENCY_DENTAL_BENEFIT).getAsset(DENTAL_SERVICES)).isRequired().hasValue("365");
        });
    }
}
