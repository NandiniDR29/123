package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.PFL_NY;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.STAT_NY;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NJ_STAT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NY_STAT;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.ClassificationManagementTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.ANNUAL_TAXABLE_WAGE_PER_PERSON;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestTotalTaxableWageModificationVerification extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-25591", component = POLICY_GROUPBENEFITS)
    public void testTotalTaxableWageModificationVerification() {
        LOGGER.info("REN-25591 Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTMasterPolicyData(), PlanDefinitionTab.class, false);
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(NJ_STAT);

        LOGGER.info("Step#1, 2 verification");
        assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(ANNUAL_TAXABLE_WAGE_PER_PERSON)).isPresent().isDisabled().hasValue(new Currency("134900").toString()).isRequired(); // default value according to the specification

        LOGGER.info("Step#3 verification");
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(NY_STAT);
        planDefinitionTab.getAssetList().getAsset(COVERAGE_NAME).setValue(STAT_NY);
        planDefinitionTab.fillTab(getDefaultSTMasterPolicyData());

        LOGGER.info("Step#4 verification");
        planDefinitionTab.getAssetList().getAsset(ADD_COVERAGE).click();
        planDefinitionTab.getAssetList().getAsset(COVERAGE_NAME).setValue(PFL_NY);
        assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(ANNUAL_TAXABLE_WAGE_PER_PERSON)).isAbsent();
        planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("30");
        planDefinitionTab.getAssetList().getAsset(MEMBER_PAYMENT_MODE).setValue(ImmutableList.of("12"));
        Tab.buttonNext.click();

        LOGGER.info("Step#5 verification");
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillFrom(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_ClassificationManagementTab_Stat_Pfl_NY"), classificationManagementMpTab.getClass());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
    }
}