package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.PlanVision.A_LA_CARTE;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.DATA_GATHERING;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.COVERAGE_TIERS;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructure.ASSUMED_PARTICIPATION_PERCENTAGE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestrictionRuleOnCoverageTierMultiSelect extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20407"}, component = POLICY_GROUPBENEFITS)
    public void testRestrictionRuleOnCoverageTierMultiSelect() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        assertSoftly(softly -> {
            LOGGER.info("REN-20407 TC1 Step 1 to 3");
            groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(),
                    planDefinitionTab.getClass());
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue
                    (ImmutableList.of(A_LA_CARTE));

            LOGGER.info("REN-20407 TC1 Step 4, 5");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_TIERS).buttonOpenPopup.click();
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).removeValue
                    (ImmutableList.of("Employee + Child(ren)",
                            "Employee + Family", "Employee + Spouse", "Employee Only"));

            LOGGER.info("REN-20407 TC1 Step 6");
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).buttonSearch.click();
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).addValue(ImmutableList.of
                    ("Composite tier"));
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).buttonSave.click();
            Page.dialogConfirmation.confirm();

            LOGGER.info("REN-20407 TC1 Step 7");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS)).hasValue(ImmutableList.of
                    ("Composite tier"));

            LOGGER.info("REN-20407 TC1 Step 8, 9");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_TIERS).buttonOpenPopup.click();
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).removeValue(ImmutableList.of("Composite tier"));
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).buttonSearch.click();
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).addValue(ImmutableList.of("Employee Only"));
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).buttonSave.click();
            Page.dialogConfirmation.confirm();
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE)
                    .getAsset(ASSUMED_PARTICIPATION_PERCENTAGE).setValue("100");
            Tab.buttonNext.click();
            NavigationPage.isSubTabPresent(NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_TIERS).buttonOpenPopup.click();
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).removeValue(ImmutableList.of("Employee Only"));
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).buttonSearch.click();
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).addValue(ImmutableList.of("Employee Only"));
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).buttonSave.click();
            Page.dialogConfirmation.confirm();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS)).hasValue(ImmutableList.of
                    ("Employee Only"));
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE)
                    .getAsset(ASSUMED_PARTICIPATION_PERCENTAGE).setValue("100");
            Tab.buttonNext.click();
            NavigationPage.isSubTabPresent(NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT);

            LOGGER.info("REN-20407 TC1 Step 10");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_TIERS).buttonOpenPopup.click();
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).removeValue(ImmutableList.of("Employee Only"));
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).buttonSearch.click();
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).addValue(ImmutableList.of
                    ("Employee + Family", "Employee Only"));
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).buttonSave.click();
            Page.dialogConfirmation.confirm();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS)).hasValue(ImmutableList.of
                    ("Employee + Family", "Employee Only"));
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE)
                    .getAsset(ASSUMED_PARTICIPATION_PERCENTAGE).setValue("100");
            Tab.buttonNext.click();
            NavigationPage.isSubTabPresent(NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT);

            LOGGER.info("REN-20407 TC1 Step 11");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_TIERS).buttonOpenPopup.click();
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).removeValue
                    (ImmutableList.of("Employee + Family", "Employee Only"));
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).buttonSearch.click();
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).addValue(ImmutableList.of
                    ("Employee + 1", "Employee + Family", "Employee Only"));
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).buttonSave.click();
            Page.dialogConfirmation.confirm();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS)).hasValue(ImmutableList.of
                    ("Employee + 1", "Employee + Family", "Employee Only"));
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE)
                    .getAsset(ASSUMED_PARTICIPATION_PERCENTAGE).setValue("100");
            Tab.buttonNext.click();
            NavigationPage.isSubTabPresent(NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT);

            LOGGER.info("REN-20407 TC1 Step 12");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_TIERS).buttonOpenPopup.click();
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).removeValue(ImmutableList.of
                    ("Employee + 1", "Employee + Family", "Employee Only"));
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).buttonSearch.click();
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).addValue(ImmutableList.of
                    ("Employee + Child(ren)", "Employee + Family", "Employee + Spouse", "Employee Only"));
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).buttonSave.click();
            Page.dialogConfirmation.confirm();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS)).hasValue(ImmutableList.of
                    ("Employee + Child(ren)", "Employee + Family", "Employee + Spouse", "Employee Only"));
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE)
                    .getAsset(ASSUMED_PARTICIPATION_PERCENTAGE).setValue("100");
            Tab.buttonNext.click();
            NavigationPage.isSubTabPresent(NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT);

            LOGGER.info("REN-20407 TC1 Step 13, 14");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData(),
                    planDefinitionTab.getClass(), GroupVisionMasterPolicyContext.premiumSummaryTab.getClass(), true);
            premiumSummaryTab.buttonRate.click();
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants
                    .PolicyStatus.PREMIUM_CALCULATED);

            LOGGER.info("REN-20407 TC1 Step 15");
            groupVisionMasterPolicy.dataGather().start();
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_TIERS).buttonOpenPopup.click();
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).removeValue(ImmutableList.of("Employee Only"));
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).buttonSearch.click();
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).addValue(ImmutableList.of("Composite tier",
                    "Employee Only"));
            planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS).buttonSave.click();
            Page.dialogConfirmation.confirm();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_TIERS))
                    .hasValue(ImmutableList.of("Composite tier", "Employee Only"));
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(DATA_GATHERING);

            LOGGER.info("REN-20407 TC1 Step 16");
            planDefinitionTab.buttonNext.click();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR
                    .format("The Combination of Coverage Tiers is not allowed. Please, change Coverage Tier selection."))).isPresent();
        });
    }
}
