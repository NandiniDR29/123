package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.ENHANCED_NY;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.PFL_NY;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NJ_STAT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NY_STAT;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.UNDER_FIFTY_LIVES;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteValidateUseExperienceRating extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21581"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteValidateUseExperienceRating() {
        LOGGER.info("REN-21581: TC1");
        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTMasterPolicyData()
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), UNDER_FIFTY_LIVES.getLabel()), ValueConstants.VALUE_YES),
                planDefinitionTab.getClass());

        LOGGER.info("REN-21581: TC1, Step 1");
        assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_EXPERIENCE_RATING))
                .isPresent().isDisabled().hasValue(ValueConstants.VALUE_NO);

        LOGGER.info("REN-21581: TC1, Step 2");
        policyInformationTab.navigateToTab();
        policyInformationTab.navigateToTab().getAssetList().getAsset(UNDER_FIFTY_LIVES).setValue(ValueConstants.VALUE_NO);

        LOGGER.info("REN-21581: TC1, Step 3-5");
        planDefinitionTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_EXPERIENCE_RATING))
                    .isPresent().isEnabled().hasValue(ValueConstants.VALUE_YES);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(TOTAL_TAXABLE_WAGE)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(STATEWIDE_MAX_COVERED_PAYROLL)).isAbsent();
        });
        planDefinitionTab.getAssetList().fill(getDefaultSTMasterPolicyData()
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey(), RATING.getLabel(), USE_EXPERIENCE_RATING.getLabel())));

        LOGGER.info("REN-21581: TC1, Step 6");
        planDefinitionTab.addCoverage(NY_STAT, ENHANCED_NY);
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_EXPERIENCE_RATING))
                    .isPresent().isEnabled().hasValue(ValueConstants.VALUE_YES);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(TOTAL_TAXABLE_WAGE)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(STATEWIDE_MAX_COVERED_PAYROLL)).isAbsent();
        });

        policyInformationTab.navigateToTab();
        policyInformationTab.navigateToTab().getAssetList().getAsset(UNDER_FIFTY_LIVES).setValue(ValueConstants.VALUE_YES);
        planDefinitionTab.navigateToTab();
        assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_EXPERIENCE_RATING))
                .isPresent().isDisabled().hasValue(ValueConstants.VALUE_NO);

        LOGGER.info("REN-21581: TC1, Step 7");
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultSTMasterPolicyData()
                .adjust(tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()), planDefinitionTab.getClass(), premiumSummaryTab.getClass());
        premiumSummaryTab.fillTab(getDefaultSTMasterPolicyData());
        planDefinitionTab.navigate();
        planDefinitionTab.tableCoverageDefinition.getRow(1).getCell(7).controls.links.get("Change").click();
        premiumSummaryTab.navigate();
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21582"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteValidateUseExperienceRatingPFL() {
        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTMasterPolicyData()
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), UNDER_FIFTY_LIVES.getLabel()), ValueConstants.VALUE_YES),
                planDefinitionTab.getClass());

        LOGGER.info("Step 1-2");
        planDefinitionTab.addCoverage(NY_STAT, PFL_NY);
        assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_EXPERIENCE_RATING))
                .isPresent().isDisabled().hasValue(ValueConstants.VALUE_NO);

        LOGGER.info("Step 3-5");
        policyInformationTab.navigateToTab();
        policyInformationTab.navigateToTab().getAssetList().getAsset(UNDER_FIFTY_LIVES).setValue(ValueConstants.VALUE_NO);
        planDefinitionTab.navigateToTab();
        assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_EXPERIENCE_RATING))
                .isPresent().isDisabled().hasValue(ValueConstants.VALUE_NO);

        LOGGER.info("Step 6-8");
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(STATEWIDE_MAX_COVERED_PAYROLL))
                    .isPresent().isDisabled().hasValue(new Currency(72860.84).toString());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(TOTAL_TAXABLE_WAGE)).isAbsent();
        });

        LOGGER.info("Step 9");
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultSTMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_PFL_NY").resolveLinks()), planDefinitionTab.getClass(), premiumSummaryTab.getClass());
        premiumSummaryTab.fillTab(getDefaultSTMasterPolicyData()).submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
    }


    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21583"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteValidateUseExperienceRatingNJ() {
        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTMasterPolicyData()
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), UNDER_FIFTY_LIVES.getLabel()), ValueConstants.VALUE_YES),
                planDefinitionTab.getClass());

        LOGGER.info("REN-21583: TC1, Step 1-3");
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(NJ_STAT);
        assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_EXPERIENCE_RATING))
                .isPresent().isDisabled().hasValue(ValueConstants.VALUE_NO);

        LOGGER.info("REN-21583: TC1, Step 4");
        policyInformationTab.navigateToTab();
        policyInformationTab.navigateToTab().getAssetList().getAsset(UNDER_FIFTY_LIVES).setValue(ValueConstants.VALUE_NO);

        LOGGER.info("REN-21583: TC1, Step 5-9");
        planDefinitionTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_EXPERIENCE_RATING))
                    .isPresent().isEnabled().hasValue(ValueConstants.VALUE_YES);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(EMPLOYER_MAXIMUM_ANNUAL_TAXABLE_WAGE_PER_PERSON))
                    .isPresent().isDisabled().isRequired().hasValue(new Currency("35300").toString());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(STATEWIDE_MAX_COVERED_PAYROLL)).isAbsent();
        });

        LOGGER.info("REN-21583: TC1, Step 12");
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(
                statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NJ"), planDefinitionTab.getClass(), premiumSummaryTab.getClass());
        premiumSummaryTab.fillTab(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NJ")).submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
    }
}