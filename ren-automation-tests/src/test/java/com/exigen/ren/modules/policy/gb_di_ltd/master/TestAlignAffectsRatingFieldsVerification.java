package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioAlignAffectsRatingFieldsVerification;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAlignAffectsRatingFieldsVerification extends ScenarioAlignAffectsRatingFieldsVerification implements LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-40578"}, component = POLICY_GROUPBENEFITS)
    public void testAlignAffectsRatingFieldsVerification() {
        initiateAndRateQuote(GroupBenefitsMasterPolicyType.GB_DI_LTD, tdSpecific().getTestData("TestData"), PremiumSummaryTab.class);
        //verification steps
        //MP → Policy Information
        verificationField(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.RATE_GUARANTEE_MONTHS), policyInformationTab, "24", true);
        //MP → Plan Definition → Plan Selection
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ASSUMED_PARTICIPATION), planDefinitionTab, "80%", true);
        //MP → Plan Definition → Coverage Included in Package
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_INCLUDED_IN_PACKAGE)
                .getAsset(PlanDefinitionTabMetaData.CoverageIncludedInPackageMetaData.STD_ADMINISTERED), planDefinitionTab, true, false);
        //MP → Plan Definition → Sponsor/Participant Funding Structure
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE)
                .getAsset(PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.PARTICIPANT_CONTRIBUTION), planDefinitionTab, "25", true);

        //MP → Plan Definition → Benefit Schedule
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.MAX_MONTHLY_BENEFIT_AMOUNT), planDefinitionTab, "5000", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.BENEFIT_PERCENTAGE), planDefinitionTab, "65%", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.MINIMUM_MONTHLY_BENEFIT_PERCENTAGE), planDefinitionTab, "15%", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.MINIMUM_MONTHLY_BENEFIT_AMOUNT), planDefinitionTab, "$200", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.MAXIMUM_BENEFIT_PERIOD), planDefinitionTab, "To Age 70", false);

        //Verification Elimination period also is checked 'Accumulation Period' field (rule is GB_DI_LTD160216-CvLiY --> Accumulation Period is disabled but = Elimination Period (days) * 2).
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.ELIMINATION_PERIOD_DAYS), planDefinitionTab, "90", true);

        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.DEFINITION_OF_DISABILITY), planDefinitionTab, "X months Regular Occ ADL", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.TEST_DEFINITION), planDefinitionTab, "Loss of Duties or Earnings", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.NUMBER_OF_MONTH), planDefinitionTab, "36 months", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.RESIDUAL), planDefinitionTab, "Not Included", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.PARTIAL_DISABILITY_BENEFIT), planDefinitionTab, "Proportionate Loss", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.WIB_DURATION), planDefinitionTab, "12 Months", false);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.SPECIALTY_OWN_OCCUPATION), planDefinitionTab, false, true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.OWN_OCCUPATION_EARNINGS_TEST), planDefinitionTab, "99%", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.ANY_OCCUPATION_EARNINGS_TEST), planDefinitionTab, "80%", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.PRE_EXISTING_CONDITION_LOOK_BACK_PERIOD), planDefinitionTab, "6 Months", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.PRE_EXISTING_CONDITION_CONTINUOUSLY_INSURED_PERIOD), planDefinitionTab, "24 Months", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.PRE_EXISTING_CONDITION_TREATMENT_FREE_PERIOD), planDefinitionTab, "12 Months", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.PRE_EXISTING_CONDITIONS), planDefinitionTab, "Not Included", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.SURVIVOR_FAMILY_INCOME_BENEFIT_TYPE), planDefinitionTab, "6 Months Survivor Income Benefit", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE)
                .getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.PAY_SURVIVOR_BENEFIT_GROSS), planDefinitionTab, false, true);

        //MP → Plan Definition → Offsets
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OFFSETS)
                .getAsset(PlanDefinitionTabMetaData.OffsetsMetaData.SOCIAL_SECURITY_INTEGRATION_METHOD), planDefinitionTab, "All Sources", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OFFSETS)
                .getAsset(PlanDefinitionTabMetaData.OffsetsMetaData.INTEGRATION_PERCENT), planDefinitionTab, "60%", true);

        //MP → Plan Definition → Options
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.PRUDENT_PERSON), planDefinitionTab, "Yes", false);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.MENTAL_ILLNESS_LIMITATION), planDefinitionTab, "24 Months", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.SPECIAL_CONDITIONS_LIMITATION), planDefinitionTab, "12 Months", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.SUBSTANCE_ABUSE_LIMITATION), planDefinitionTab, "None", false);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.SELF_REPORTED_CONDITIONS_LIMITATION), planDefinitionTab, "24 Months", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.TERMINAL_ILLNESS_BENEFIT), planDefinitionTab, "3 Months", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.COST_OF_LIVING_ADJUSTMENT_BENEFIT), planDefinitionTab, "3%", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.NUMBER_OF_ADJUSTMENTS), planDefinitionTab, "10 Years", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.COBRA_PREMIUM_REIMB_AMOUNT), planDefinitionTab, "600", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.RECOVERY_INCOME_BENEFIT), planDefinitionTab, "6 Months", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.PRESUMPTIVE_DISABILITY), planDefinitionTab, "90 days", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.CATASTROPHIC_DISABILITY_BENEFIT), planDefinitionTab, "10%", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.CHILD_EDUCATION_BENEFIT), planDefinitionTab, "Not Included", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.FOUR_HUNDRED_ONE_K_CONTRIBUTION_DURING_DISABILITY), planDefinitionTab, "11%", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)
                .getAsset(PlanDefinitionTabMetaData.OptionsMetaData.FICA_MATCH), planDefinitionTab, "None", true);

        //MP → Plan Definition → Benefit Termination Option
        verificationField(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_TERM_OPTION)
                .getAsset(PlanDefinitionTabMetaData.BenefitTerminationOptionMetaData.MANDATORY_REHAB), planDefinitionTab, "Included", false);

        //MP → Plan Definition → Plan Selection 'Census Type' (not checked as usual way as it requires filling in additional fields)
        LOGGER.info("Step 1 verification for 'Census Type' field");
        planDefinitionTab.navigateToTab();
        assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE).setValue("Enrolled");
        assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.DATA_GATHERING);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("25");
        checkQuoteStatusAfterSetNewValue(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE), planDefinitionTab, true);
    }

}
