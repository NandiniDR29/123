package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN;
import static com.exigen.ren.main.enums.TableConstants.CoverageDefinition.*;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.COVERAGE_INCLUDED_IN_PACKAGE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.CoverageIncludedInPackageMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab.removeCoverage;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab.tableCoverageDefinition;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;


public class TestConfigurePlanCoverages extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {
    private AssetList coverageIncludedInPackage = planDefinitionTab.getAssetList().getAsset(COVERAGE_INCLUDED_IN_PACKAGE);
    private static final String planName = "BL-Basic Life";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-14706", "REN-14918", "REN-14920", "REN-14953", "REN-14954"}, component = POLICY_GROUPBENEFITS)
    public void testConfigurePlanCoverages() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PlanDefinitionTab.class);
        planDefinitionTab.selectDefaultPlan();
        assertSoftly(softly -> {
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ADD_COVERAGE).click();
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN_SELECT).setValue(BASIC_LIFE_PLAN);
            LOGGER.info("REN-14706 Step 1 & 2");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME)).containsAllOptions("", DEP_ADD, VOL_BTL);
            removeCoverage("");
            tableCoverageDefinition.getRow(ImmutableMap.of(COVERAGE_NAME.getName(), BTL)).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
            LOGGER.info("REN-14706 Step 3 & 4 & 5");
            LOGGER.info("REN-14918 Step 1 & 2 & 3");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN_NAME)).isEnabled().isRequired().hasValue(BASIC_LIFE_PLAN);
            LOGGER.info("REN-14920 Step 1-2-3");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ENROLLMENT_UNDERWRITING_OFFER)).isEnabled().isPresent().hasOptions("N/A", "Open Enrollment", "1 up opportunity", "Changing with EOI Only", "Other");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ENROLLMENT_UNDERWRITING_OFFER).setValue("Other");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ENROLLMENT_UNDERWRITING_OFFER_OTHER_DESCRIPTION)).isPresent().isEnabled().isRequired().hasValue("");
            LOGGER.info("REN-14706 Step 6");
            LOGGER.info("REN-14953 Step 1-2");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).isPresent().isEnabled().isRequired().hasValue("");
            LOGGER.info("REN-14953 Step 3");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE).setValue("Enrolled");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).isPresent().isEnabled().isRequired().hasValue("");
            LOGGER.info("REN-14953 Step 9");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("12");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE).setValue("Eligible");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).isPresent().isEnabled().isOptional().hasValue("");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ADD_COVERAGE).click();
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN_SELECT).setValue(BASIC_LIFE_PLAN);
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME).setValue(VOL_BTL);
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE).setValue("Eligible");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CONTRIBUTION_TYPE).setValue("Voluntary");
            LOGGER.info("REN-14706 Step 7");
            LOGGER.info("REN-14954 Step 1-2-3");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ASSUMED_PARTICIPATION)).isPresent().isEnabled()
                    .hasOptions(ImmutableList.of("", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%", "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%"));
            LOGGER.info("REN-14706 Step 8");
            ImmutableList.of(LTD, STD, DENTAL, VISION).forEach(control -> {
                softly.assertThat(coverageIncludedInPackage.getAsset(control)).isEnabled().isPresent().hasValue(false);
            });
            LOGGER.info("REN-14954 Step 6-7");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ADD_COVERAGE).click();
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN_SELECT).setValue(BASIC_LIFE_PLAN);
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME).setValue(SP_VOL_BTL);
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE).setValue("Eligible");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ASSUMED_PARTICIPATION)).isAbsent();
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE).setValue("Enrolled");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ASSUMED_PARTICIPATION)).isAbsent();
            LOGGER.info("REN-14954 Step 9");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ADD_COVERAGE).click();
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN_SELECT).setValue(BASIC_LIFE_PLAN);
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME).setValue(VOL_ADD);
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE).setValue("Enrolled");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CONTRIBUTION_TYPE).setValue("Non-contributory");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ASSUMED_PARTICIPATION)).isPresent().isEnabled();
            ImmutableList.of(SP_VOL_BTL, VOL_ADD).forEach(PlanDefinitionTab::removeCoverage);
            LOGGER.info("REN-14706 Step 9");
            softly.assertThat(tableCoverageDefinition.getColumn(COVERAGE_NAME.getName()).getValue()).hasSameElementsAs(ImmutableList.of(ADD, BTL, VOL_BTL, DEP_BTL));
            softly.assertThat(tableCoverageDefinition.getColumn(PLAN.getName())).hasValue(ImmutableList.of(planName, planName, planName, planName));
            ImmutableList.of(CONTRIBUTION_TYPE, RATE_BASIS, BENEFIT_TYPE).forEach(control -> {
                softly.assertThat(tableCoverageDefinition.getColumn(control.getName())).isPresent();
            });
            tableCoverageDefinition.getRow(ImmutableMap.of(COVERAGE_NAME.getName(), BTL)).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
            LOGGER.info("REN-14918 Step 1-2");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN_NAME)).isEnabled().isRequired().hasValue(BASIC_LIFE_PLAN);
            LOGGER.info("REN-14918 Step 6");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN_NAME).setValue("Plan2");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN_NAME)).hasNoWarning();
            softly.assertThat(PlanDefinitionTab.tableCoverageDefinition.getColumn(PLAN.getName())).hasValue(ImmutableList.of(planName, planName, planName, "BL-Plan2"));
            LOGGER.info("REN-14920 Step 5");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ENROLLMENT_UNDERWRITING_OFFER_OTHER_DESCRIPTION).setValue("Test");
            LOGGER.info("REN-14953 Step 11");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("2");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.SPONSOR_PAYMENT_MODE).setValue("1");
            tableCoverageDefinition.getRow(ImmutableMap.of(COVERAGE_NAME.getName(), VOL_BTL)).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
            LOGGER.info("REN-14954 Step 15");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ASSUMED_PARTICIPATION).setValue("40%");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.MEMBER_PAYMENT_MODE).setValue(ImmutableList.of("1"));
            tableCoverageDefinition.getRow(ImmutableMap.of(COVERAGE_NAME.getName(), ADD)).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.SPONSOR_PAYMENT_MODE).setValue("1");
            removeCoverage(VOL_BTL);
            termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultTLMasterPolicyData(), PlanDefinitionTab.class, PremiumSummaryTab.class, true);
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        });
    }
}
