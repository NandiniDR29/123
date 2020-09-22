package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PremiumSummaryTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioAlignAffectsRatingFieldsVerification;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;

public class TestAlignAffectsRatingFieldsVerification extends ScenarioAlignAffectsRatingFieldsVerification implements ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITH_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-40989"}, component = POLICY_GROUPBENEFITS)
    public void testAlignAffectsRatingFieldsVerification() {
        String quoteNumber = initiateAndRateQuote(GroupBenefitsMasterPolicyType.GB_DI_STD, tdSpecific().getTestData("TestData"),
                PremiumSummaryTab.class);
        //verification steps
        //MP → Policy Information
        verificationField(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.RATE_GUARANTEE_MONTHS), policyInformationTab, "24", true);
        //MP → Plan Definition → Plan Selection
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_BASIS), planDefinitionTab, "24-hour Coverage", false);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ASSUMED_PARTICIPATION), planDefinitionTab, "80%", true);
        //MP → Plan Definition → Benefit Schedule
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.BENEFIT_PERCENTAGE), planDefinitionTab, "65%", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.MAXIMUM_WEEKLY_BENEFIT_AMOUNT), planDefinitionTab, "1100", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.ELIMINATION_PERIOD_INJURY), planDefinitionTab, "14", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.ELIMINATION_PERIOD_SICKNESS), planDefinitionTab, "7", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.MAXIMUM_PAYMENT_DURATION), planDefinitionTab, "14", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.MINIMUM_WEEKLY_BENEFIT_AMOUNT), planDefinitionTab, "$50", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.PARTIAL_DISABILITY), planDefinitionTab, "Work Incentive Benefit", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.PRE_EXISTING_CONDITIONS), planDefinitionTab, "Not Included", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.BENEFIT_TYPE), planDefinitionTab, "Specified Weekly Benefit Amount - Single Value", true);
        //MP → Plan Definition → Offsets
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.SOCIAL_SECURITY_INTEGRATION_METHOD), planDefinitionTab, "Family", false);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.WORKERS_COMPENSATION), planDefinitionTab, "Included", false);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.STAT_OFFSET), planDefinitionTab, "Not Included", true);
        //MP → Plan Definition → Options
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.RESIDUAL), planDefinitionTab, "Not Included", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.FIRST_DAY_HOSPITALIZATION), planDefinitionTab, "Included", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.FICA_MATCH), planDefinitionTab, "None", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.W2), planDefinitionTab, "Not Included", true);
        //MP → Plan Definition → Plan Selection 'Census Type' (not checked as usual way as it requires filling in additional fields)
        LOGGER.info("Step 1 verification for 'Census Type' field");
        planDefinitionTab.navigateToTab();
        assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE).setValue("Enrolled");
        assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.DATA_GATHERING);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("25");
        checkQuoteStatusAfterSetNewValue(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE), planDefinitionTab, true);

        //Save quote and TS: current date + 1d before check 'Rate Request Date'
        String updatedRateRequestDate = saveQuoteAndShiftDateForwardBeforeCheckRateRequestDate(quoteNumber, GroupBenefitsMasterPolicyType.GB_DI_STD);
        verificationField(premiumSummaryTab.getAssetList().getAsset(PremiumSummaryTabMetaData.RATE_SECTION).getAsset(PremiumSummaryTabMetaData.RateMetaData.RATE_REQUEST_DATE), premiumSummaryTab, updatedRateRequestDate, true);

        //MP → Plan Definition check 'Plan' field (checked last to avoid filling in other required fields after changing the plan)
        LOGGER.info("Step 1 verification for 'Plan' field");
        planDefinitionTab.navigateToTab();
        assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of("NC"));
        LOGGER.info("Step 2 verification for 'Plan' field");
        assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.DATA_GATHERING);
        LOGGER.info("Step 3 verification for 'Plan' field");
        premiumSummaryTab.navigateToTab();
        assertThat(PremiumSummaryTab.premiumSummaryCoveragesTable.getRow(1).getCell(1)).hasValue(NO_PREMIUM_INFORMATION);
    }
}
