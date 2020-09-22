package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.ENHANCED_NY;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NJ_STAT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NY_STAT;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAnnualTaxableWageModificationVerification extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-25804", component = POLICY_GROUPBENEFITS)
    public void testAnnualTaxableWageModificationVerification() {
        LOGGER.info("REN-25804 Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        quoteInitiationToPlanDefinitionTab(NY_STAT);

        LOGGER.info("Step#1 verification");
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_EXPERIENCE_RATING).setValue(VALUE_NO);
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(ANNUAL_COVERED_PAYROLL)).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Annual Taxable Wages"))).isAbsent();
        });

        LOGGER.info("Step#2 verification");
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_EXPERIENCE_RATING).setValue(VALUE_YES);
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(ANNUAL_COVERED_PAYROLL)).isPresent().isOptional();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(TOTAL_TAXABLE_WAGE)).isAbsent();
        });

        LOGGER.info("Step#3 verification");
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultSTMasterPolicyData(), PlanDefinitionTab.class, PremiumSummaryTab.class, true);
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("Step#9 verification");
        quoteInitiationToPlanDefinitionTab(NY_STAT);
        planDefinitionTab.getAssetList().getAsset(COVERAGE_NAME).setValue(ENHANCED_NY);
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_EXPERIENCE_RATING).setValue(VALUE_YES);
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(ANNUAL_COVERED_PAYROLL)).isPresent().isOptional();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(TOTAL_TAXABLE_WAGE)).isAbsent();
        });
        Tab.buttonSaveAndExit.click();

        LOGGER.info("Step#14 verification");
        quoteInitiationToPlanDefinitionTab(NJ_STAT);
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(ANNUAL_COVERED_PAYROLL)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(TOTAL_TAXABLE_WAGE)).isPresent();
        });

        LOGGER.info("Step#16 verification");
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(statutoryDisabilityInsuranceMasterPolicy
                .getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NJ"), PlanDefinitionTab.class, PremiumSummaryTab.class, true);
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
    }

    private void quoteInitiationToPlanDefinitionTab(String planValue) {
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTMasterPolicyData(), PlanDefinitionTab.class, false);
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(planValue);
    }
}