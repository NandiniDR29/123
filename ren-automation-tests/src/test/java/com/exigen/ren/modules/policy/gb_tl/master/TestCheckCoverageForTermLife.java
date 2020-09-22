package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.pages.ErrorPage.TableError.MESSAGE;
import static com.exigen.ren.common.pages.ErrorPage.tableError;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.VOLUNTARY_LIFE_PLAN;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.TableConstants.Plans.COVERAGE_NAME;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PlanDefinitionTab.tableCoverageDefinition;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PremiumSummaryTabMetaData.APPLY;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PremiumSummaryTabMetaData.SELECT_RATING_CENSUS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab.buttonRate;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.ClassificationManagementTabMetaData.ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.ClassificationManagementTabMetaData.CLASSIFICATION_GROUP_NAME;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.ClassificationManagementTab.coveragesTable;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab.removeCoverage;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelQuoteStatus;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckCoverageForTermLife extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {
    private static final String PLAN_WHERE_DEPENDENT_BASIC_AD_D_IS_INCLUDED = "Dependent Basic Life coverage must be included on any plan where Dependent Basic AD&D is included.";
    private static final String PLAN_WHERE_DEPENDENT_VOLUNTARY_AD_D_IS_INCLUDED = "Spouse Voluntary Life OR Child Voluntary Life coverage must be included on any plan where Dependent Voluntary AD&D is included.";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37784", component = POLICY_GROUPBENEFITS)
    public void testCheckCoverageDBADD() {
        initiateQuoteForGroupTermLife();

        LOGGER.info("TEST REN-37784: Step 2");
        planDefinitionTab.fillTab(getDefaultTLMasterPolicyData());
        assertThat(tableCoverageDefinition.getColumn(COVERAGE_NAME.getName()).getValue())
                .as("Only listed coverages should present")
                .containsExactlyInAnyOrder(BTL, ADD, DEP_BTL);

        LOGGER.info("TEST REN-37784: Step 3");
        removeCoverage(DEP_BTL);
        assertThat(tableCoverageDefinition.getColumn(COVERAGE_NAME.getName()).getValue())
                .as("Only listed coverages should present")
                .containsExactlyInAnyOrder(BTL, ADD);

        LOGGER.info("TEST REN-37784: Step 4");
        addCoverage(BASIC_LIFE_PLAN, DEP_ADD);
        planDefinitionTab.getAssetList().getAsset(ENHANCED_AD_D).setValue("No");
        PlanDefinitionTab.buttonNext.click();
        ImmutableList.of(BTL, ADD, DEP_ADD).forEach(this::fillClassificationManagementTab);
        ClassificationManagementTab.buttonNext.click();

        premiumSummaryTab.getAssetList().getAsset(SELECT_RATING_CENSUS).setValueByIndex(1);
        premiumSummaryTab.getAssetList().getAsset(APPLY).click();
        PremiumSummaryTab.buttonRate.click();
        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), PLAN_WHERE_DEPENDENT_BASIC_AD_D_IS_INCLUDED);
        ErrorPage.buttonCancel.click();

        LOGGER.info("TEST REN-37784: Step 5, 6");
        addCoverage(BASIC_LIFE_PLAN, DEP_BTL);
        assertThat(tableCoverageDefinition.getColumn(COVERAGE_NAME.getName()).getValue())
                .as("Only listed coverages should present")
                .containsExactlyInAnyOrder(BTL, ADD, DEP_ADD, DEP_BTL);
        PlanDefinitionTab.buttonNext.click();
        rateQuote();
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37793", component = POLICY_GROUPBENEFITS)
    public void testCheckCoverageDVADD() {
        initiateQuoteForGroupTermLife();

        LOGGER.info("TEST REN-37793: Step 2");
        planDefinitionTab.fillTab(tdSpecific().getTestData("TestDataVoluntaryLife"));
        assertThat(tableCoverageDefinition.getColumn(COVERAGE_NAME.getName()).getValue())
                .as("Only listed coverages should present")
                .containsExactlyInAnyOrder(VOL_BTL, VOL_ADD, SP_VOL_BTL, DEP_VOL_BTL, DEP_VOL_ADD);

        LOGGER.info("TEST REN-37793: Step 3, 4");
        removeCoverage(SP_VOL_BTL);
        assertThat(tableCoverageDefinition.getColumn(COVERAGE_NAME.getName()).getValue())
                .as("Only listed coverages should present")
                .containsExactlyInAnyOrder(VOL_BTL, VOL_ADD, DEP_VOL_BTL, DEP_VOL_ADD);

        PlanDefinitionTab.buttonNext.click();
        ImmutableList.of(VOL_BTL, VOL_ADD, DEP_VOL_BTL, DEP_VOL_ADD).forEach(this::fillClassificationManagementTab);

        ClassificationManagementTab.buttonNext.click();
        premiumSummaryTab.getAssetList().getAsset(SELECT_RATING_CENSUS).setValueByIndex(1);
        premiumSummaryTab.getAssetList().getAsset(APPLY).click();
        rateQuote();

        LOGGER.info("TEST REN-37793: Step 5, 6");
        addCoverage(VOLUNTARY_LIFE_PLAN, SP_VOL_BTL);
        assertThat(tableCoverageDefinition.getColumn(COVERAGE_NAME.getName()).getValue())
                .as("Only listed coverages should present")
                .containsExactlyInAnyOrder(VOL_BTL, VOL_ADD, SP_VOL_BTL, DEP_VOL_BTL, DEP_VOL_ADD);
        PlanDefinitionTab.buttonNext.click();
        rateQuote();

        LOGGER.info("TEST REN-37793: Step 7");
        planDefinitionTab.navigateToTab();
        removeCoverage(DEP_VOL_BTL);
        rateQuote();

        LOGGER.info("TEST REN-37793: Step 8");
        planDefinitionTab.navigateToTab();
        removeCoverage(SP_VOL_BTL);

        premiumSummaryTab.navigateToTab();
        premiumSummaryTab.rate();
        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), PLAN_WHERE_DEPENDENT_VOLUNTARY_AD_D_IS_INCLUDED);
    }

    private void initiateQuoteForGroupTermLife() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        LOGGER.info("TEST: Master Policy Creation");
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PlanDefinitionTab.class);
        assertThat(planDefinitionTab.getAssetList().getAsset(PLAN)).isPresent();
    }

    private void fillClassificationManagementTab(String coverage) {
        coveragesTable.getRow(COVERAGE_NAME.getName(), coverage).getCell(COVERAGE_NAME.getName()).click();
        classificationManagementMpTab.getAssetList().getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        classificationManagementMpTab.getAssetList().getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
    }

    private void addCoverage(String plan, String coverage) {
        planDefinitionTab.navigateToTab();
        planDefinitionTab.addCoverage(plan, coverage);

        planDefinitionTab.getAssetList().getAsset(REQUIRED_PARTICIPATION).setValue("5%");
        planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE).setValue("Eligible");
    }
    private void rateQuote() {
        premiumSummaryTab.navigateToTab();
        premiumSummaryTab.rate();
        assertThat(labelQuoteStatus).valueContains(PREMIUM_CALCULATED);
    }
}
