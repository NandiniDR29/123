package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigurePlansCoverages extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    private static final String NY_STAT = "NY Stat";
    private static final String NJ_STAT = "NJ Stat";
    private static final String STAT_NY = "Stat NY";
    private static final String SITUS_STATE_NY = "NY";
    private static final String SITUS_STATE_NJ = "NJ";
    private static final String STAT_NJ = "Stat NJ";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18026"}, component = POLICY_GROUPBENEFITS)
    public void testConfigurePlansCoveragesNY() {
        mainApp().open();

        LOGGER.info("REN-18026 Preconditions");
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTMasterPolicyData().adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), SITUS_STATE_NY), PlanDefinitionTab.class);
        assertSoftly(softly -> {
            LOGGER.info("REN-18026 Step 1");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN)).hasValue(NY_STAT);
            LOGGER.info("REN-18026 Step 12");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_NAME)).hasValue(STAT_NY);
            LOGGER.info("REN-18026 Step 2");
            ImmutableList.of("Initial Enrollment Underwriting Offer", "Annual Enrollment Underwriting Offer", "Underwriting Offer for Qualifying Life Event").forEach(text ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(text))).isAbsent());
            LOGGER.info("REN-18026 Step 3,4");
            policyInformationTab.navigateToTab();
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(SITUS_STATE_NJ);
            planDefinitionTab.navigateToTab();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN)).hasValue(NY_STAT);
            LOGGER.info("REN-18026 Step 5");
            policyInformationTab.navigateToTab();
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(SITUS_STATE_NY);
            planDefinitionTab.navigateToTab();
            LOGGER.info("REN-18026 Step 6");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN_NAME)).isPresent().isRequired().isDisabled().hasValue("Statutory New York");
            LOGGER.info("REN-18026 Step 7");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ADD_COVERAGE)).isPresent();
            LOGGER.info("REN-18026 Step 9 Verify Plan summary table columns");
            planDefinitionTab.getAssetList().getAsset(ADD_COVERAGE).click();
            softly.assertThat(planDefinitionTab.tableCoverageDefinition.getHeader().getValue().subList(1, 6)).hasSameElementsAs(Stream.of(TableConstants.CoverageDefinition.values())
                    .map(TableConstants.CoverageDefinition::getName).collect(Collectors.toList()));
            LOGGER.info("REN-18026 Step 10");
            softly.assertThat(planDefinitionTab.tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), STAT_NY)).isPresent();
            LOGGER.info("REN-18026 Step 9 Verify Plan summary table available actions");
            softly.assertThat(planDefinitionTab.tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), STAT_NY).getCell(7).controls.links.get(ActionConstants.CHANGE)).isPresent();
            softly.assertThat(planDefinitionTab.tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), STAT_NY).getCell(7).controls.links.get(ActionConstants.REMOVE)).isPresent();
            LOGGER.info("REN-18026 Step 11");
            planDefinitionTab.tableCoverageDefinition.getRows().forEach(row -> {
                row.getCell(7).controls.links.get(ActionConstants.CHANGE).click();
                softly.assertThat(row.getCell(TableConstants.CoverageDefinition.PLAN.getName()))
                        .hasValue(String.format(
                                "%s - %s",
                                planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).getValue().replaceAll("\\s", ""),
                                planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN_NAME).getValue()));
            });
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18028"}, component = POLICY_GROUPBENEFITS)
    public void testConfigurePlansCoveragesNJ() {
        mainApp().open();

        LOGGER.info("REN-18028 Preconditions");
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTMasterPolicyData().adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), SITUS_STATE_NJ).mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel())), PlanDefinitionTab.class);
        assertSoftly(softly -> {
            LOGGER.info("REN-18028 Step 1");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN)).hasValue(NJ_STAT);
            LOGGER.info("REN-18028 Step 2");
            ImmutableList.of("Initial Enrollment Underwriting Offer", "Annual Enrollment Underwriting Offer", "Underwriting Offer for Qualifying Life Event").forEach(text ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(text))).isAbsent());
            LOGGER.info("REN-18028 Step 3,4");
            policyInformationTab.navigateToTab();
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(SITUS_STATE_NY);
            planDefinitionTab.navigateToTab();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN)).hasValue(NJ_STAT);
            LOGGER.info("REN-18028 Step 5");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN_NAME)).isPresent().isRequired().isDisabled().hasValue("Statutory New Jersey");
            LOGGER.info("REN-18028 Step 6");
            softly.assertThat(PlanDefinitionTab.tableCoverageDefinition).isAbsent();
            LOGGER.info("REN-18028 Step 7");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_NAME)).hasValue(STAT_NJ);
            LOGGER.info("REN-18028 Step 9");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ADD_COVERAGE)).isAbsent();
            LOGGER.info("REN-18028 Step 10");
            String Plan = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).getValue().replaceAll("\\s", "");
            String planName = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN_NAME).getValue();
            classificationManagementMpTab.navigateToTab();
            softly.assertThat(ClassificationManagementTab.tablePlansAndCoverages.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), STAT_NJ).getCell(TableConstants.CoverageDefinition.PLAN.getName()).getValue()).isEqualTo(String.format(
                    "%s-%s", Plan, planName));
        });
    }
}