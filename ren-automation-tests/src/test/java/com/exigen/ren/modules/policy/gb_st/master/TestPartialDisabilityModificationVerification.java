package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NJ_STAT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NY_STAT;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.ValueConstants.NONE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetadata.PARTIAL_DISABILITY;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.USE_EXPERIENCE_RATING;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPartialDisabilityModificationVerification extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-26283", component = POLICY_GROUPBENEFITS)
    public void testPartialDisabilityModificationVerification() {
        LOGGER.info("REN-26283 Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        quoteInitiationToPlanDefinitionTab(NY_STAT);

        LOGGER.info("Step#1 verification");
        planDefinitionTab.getAssetList().getAsset(COVERAGE_NAME).setValue(STAT_NY);
        assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(PARTIAL_DISABILITY)).isPresent().isDisabled().hasValue(NONE).isRequired();

        LOGGER.info("Step#2 verification");
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultSTMasterPolicyData(), PlanDefinitionTab.class, PremiumSummaryTab.class, true);
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("Step#7 verification");
        quoteInitiationToPlanDefinitionTab(NY_STAT);
        planDefinitionTab.getAssetList().getAsset(COVERAGE_NAME).setValue(ENHANCED_NY);
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_EXPERIENCE_RATING).setValue(VALUE_YES);
        assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(PARTIAL_DISABILITY)).isPresent().isDisabled().hasValue(NONE).isRequired();
        Tab.buttonSaveAndExit.click();

        LOGGER.info("Step#9 verification");
        quoteInitiationToPlanDefinitionTab(NY_STAT);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ADD_COVERAGE).click();
        planDefinitionTab.getAssetList().getAsset(COVERAGE_NAME).setValue(PFL_NY);
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_EXPERIENCE_RATING).setValue(VALUE_YES);
        assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(PARTIAL_DISABILITY)).isAbsent();
        Tab.buttonSaveAndExit.click();

        LOGGER.info("Step#13 verification");
        quoteInitiationToPlanDefinitionTab(NJ_STAT);
        assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(PARTIAL_DISABILITY)).isPresent().isDisabled().hasValue("Included").isRequired();

        LOGGER.info("Step#15 verification");
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NJ"),
                PlanDefinitionTab.class, PremiumSummaryTab.class, true);
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
    }

    private void quoteInitiationToPlanDefinitionTab(String planValue) {
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTMasterPolicyData(), PlanDefinitionTab.class, false);
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(planValue);
    }
}