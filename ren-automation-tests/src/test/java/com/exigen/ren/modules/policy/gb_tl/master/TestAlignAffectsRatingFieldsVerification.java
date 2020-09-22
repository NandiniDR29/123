package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PremiumSummaryTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioAlignAffectsRatingFieldsVerification;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PremiumSummaryTabMetaData.RateMetaData.RATE_REQUEST_DATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAlignAffectsRatingFieldsVerification extends ScenarioAlignAffectsRatingFieldsVerification implements TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITH_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-40852"}, component = POLICY_GROUPBENEFITS)
    public void testAlignAffectsRatingFieldsVerification() {
        String quoteNumber = initiateAndRateQuote(GroupBenefitsMasterPolicyType.GB_TL, tdSpecific().getTestData("TestData"),
                PremiumSummaryTab.class);
        //verification steps
        //MP → Policy Information
        verificationField(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.RATE_GUARANTEE_MONTHS), policyInformationTab, "24", true);
        verificationField(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.PRIOR_LIFE_COVERAGE), policyInformationTab, "Yes", true);
        //return 'Prior Life Coverage' to 'No' for verification next fields and check again
        verificationField(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.PRIOR_LIFE_COVERAGE), policyInformationTab, "No", true);


        //MP → Plan Definition → Plan Selection
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.POPULATION_TYPE), planDefinitionTab, "Disabled", true);
        //return 'Population Type' to 'Active' and check again
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.POPULATION_TYPE), planDefinitionTab, "Active", true);

        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ASSUMED_PARTICIPATION), planDefinitionTab, "90%", true);

        //MP → Plan Definition → Coverage Included in Package
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_INCLUDED_IN_PACKAGE)
                .getAsset(PlanDefinitionTabMetaData.CoverageIncludedInPackageMetaData.LTD), planDefinitionTab, true, false);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_INCLUDED_IN_PACKAGE)
                .getAsset(PlanDefinitionTabMetaData.CoverageIncludedInPackageMetaData.STD), planDefinitionTab, true, false);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_INCLUDED_IN_PACKAGE)
                .getAsset(PlanDefinitionTabMetaData.CoverageIncludedInPackageMetaData.DENTAL), planDefinitionTab, true, false);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_INCLUDED_IN_PACKAGE)
                .getAsset(PlanDefinitionTabMetaData.CoverageIncludedInPackageMetaData.VISION), planDefinitionTab, true, false);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.AGE_REDUCTION_CHECKBOX), planDefinitionTab, false, true);

        //MP → Plan Definition → Age Reduction
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PORTABILITY), planDefinitionTab, "Not Included", false);

        //MP → Plan Definition → Options
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.WAIVER_DISABLED_PRIOR_TO_AGE), planDefinitionTab, "65", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.TERMINATION_AGE), planDefinitionTab, "70", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PREMIUM_WAIVER_ELIMINATION_PERIOD), planDefinitionTab, "9 Months", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.WAIVER_OF_PREMIUM), planDefinitionTab, "Not Included", true);

        //Save quote and TS: current date + 1d before check 'Rate Request Date'
        String updatedRateRequestDate = saveQuoteAndShiftDateForwardBeforeCheckRateRequestDate(quoteNumber, GroupBenefitsMasterPolicyType.GB_TL);

        //MP → Premium Summary → Rate
        verificationField(premiumSummaryTab.getAssetList().getAsset(PremiumSummaryTabMetaData.RATE_SECTION)
                .getAsset(RATE_REQUEST_DATE), premiumSummaryTab, updatedRateRequestDate, true);

        //MP → Plan Definition check 'Plan' field (checked last to avoid filling in other required fields after changing the plan)
        LOGGER.info("Step 1 verification for 'Plan' field");
        planDefinitionTab.navigateToTab();
        assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of("Voluntary Life"));
        LOGGER.info("Step 2 verification for 'Plan' field");
        assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.DATA_GATHERING);
        LOGGER.info("Step 3 verification for 'Plan' field");
        premiumSummaryTab.navigateToTab();
        assertThat(PremiumSummaryTab.tableCoveragesName.getRow(1).getCell(1)).hasValue(NO_PREMIUM_INFORMATION);
    }
}
