package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.PFL_NY;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NJ_STAT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NY_STAT;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.HistoricalClaimMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.PremiumAndRateHistoryMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.RateHistoryMaleMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyCommonExperienceBasedRatingSTAT extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23221", "REN-23304", "REN-23317", "REN-23319", "REN-23322"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyCommonExperienceBasedRatingSTAT() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());

        LOGGER.info("REN-23221 Step 2, 3");
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.UNDER_FIFTY_LIVES).setValue(VALUE_YES);
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(NJ_STAT);
        AssetList ratingAssetList = planDefinitionTab.getAssetList().getAsset(RATING);
        assertThat(ratingAssetList.getAsset(USE_EXPERIENCE_RATING)).isPresent().isDisabled().hasValue("No");

        assertSoftly(softly -> {
            LOGGER.info("REN-23304 Step 2");
            ImmutableList.of(PERIOD, PERIOD_BEGIN_DATE, PERIOD_END_DATE, PREMIUM, EXPERIENCE_PERIOD_ADJUSTMENT, COMPOSITE_RATE, ADD, REMOVE).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(PREMIUM_AND_RATE_HISTORY).getAsset(control)).isAbsent());

            LOGGER.info("REN-23317 Step 2");
            ImmutableList.of(RATE_1, NUMBER_OF_MONTHS_RATE_1, RATE_2, NUMBER_OF_MONTHS_RATE_2).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATE_HISTORY_MALE).getAsset(control)).isAbsent());

            LOGGER.info("REN-23319 Step 2");
            ImmutableList.of(RateHistoryFemaleMetaData.RATE_1, RateHistoryFemaleMetaData.NUMBER_OF_MONTHS_RATE_1, RateHistoryFemaleMetaData.RATE_2, RateHistoryFemaleMetaData.NUMBER_OF_MONTHS_RATE_2).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATE_HISTORY_FEMALE).getAsset(control)).isAbsent());

            LOGGER.info("REN-23322 Step 2");
            ImmutableList.of(CLAIMS, CLAIM_RESERVES, UNDERWRITER_ADJUSTMENT).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(HISTORICAL_CLAIM).getAsset(control)).isAbsent());
        });

        LOGGER.info("REN-23221 Step 4-6");
        policyInformationTab.navigateToTab();
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.UNDER_FIFTY_LIVES).setValue(VALUE_NO);
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(NY_STAT);
        planDefinitionTab.addCoverage(NY_STAT, PFL_NY);
        assertSoftly(softly -> {
            softly.assertThat(ratingAssetList.getAsset(USE_EXPERIENCE_RATING)).isPresent().isDisabled().hasValue("No");
            ImmutableList.of(NUMBER_OF_LIVES_MALE, NUMBER_OF_LIVES_FEMALE, INFORCE_RATE_MALE, INFORCE_RATE_FEMALE, ANNUAL_PREMIUM, ANNUAL_COVERED_PAYROLL, TOTAL_TAXABLE_WAGE).forEach(control ->
                    softly.assertThat(ratingAssetList.getAsset(control)).isAbsent());
        });

        LOGGER.info("REN-23221 Step 8, 9");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(NJ_STAT);
        assertSoftly(softly -> {
            softly.assertThat(ratingAssetList.getAsset(USE_EXPERIENCE_RATING)).isPresent().isEnabled().hasValue("Yes");
            ImmutableList.of(NUMBER_OF_LIVES_MALE, NUMBER_OF_LIVES_FEMALE, INFORCE_RATE_MALE, INFORCE_RATE_FEMALE, ANNUAL_PREMIUM, TOTAL_TAXABLE_WAGE).forEach(control -> {
                softly.assertThat(ratingAssetList.getAsset(control)).isPresent().hasValue(StringUtils.EMPTY);
            });
            ImmutableList.of(NUMBER_OF_LIVES_MALE, NUMBER_OF_LIVES_FEMALE, INFORCE_RATE_MALE, INFORCE_RATE_FEMALE, ANNUAL_PREMIUM).forEach(control ->
                    softly.assertThat(ratingAssetList.getAsset(control)).isEnabled());
            softly.assertThat(ratingAssetList.getAsset(TOTAL_TAXABLE_WAGE)).isDisabled();
        });

        LOGGER.info("REN-23304 Step 4");
        assertSoftly(softly -> {
            ImmutableList.of(PERIOD, PERIOD_BEGIN_DATE, PERIOD_END_DATE, PREMIUM, EXPERIENCE_PERIOD_ADJUSTMENT).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(PREMIUM_AND_RATE_HISTORY).getAsset(control)).isPresent().hasValue(StringUtils.EMPTY).isEnabled());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PREMIUM_AND_RATE_HISTORY).getAsset(COMPOSITE_RATE)).isPresent().isDisabled().hasValue(StringUtils.EMPTY);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PREMIUM_AND_RATE_HISTORY).getAsset(ADD)).isPresent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PREMIUM_AND_RATE_HISTORY).getAsset(REMOVE)).isAbsent();
        });

        assertSoftly(softly -> {
            LOGGER.info("REN-23317 Step 4");
            ImmutableList.of(RATE_1, NUMBER_OF_MONTHS_RATE_1, RATE_2, NUMBER_OF_MONTHS_RATE_2).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATE_HISTORY_MALE).getAsset(control)).isPresent().hasValue(StringUtils.EMPTY).isEnabled());

            LOGGER.info("REN-23317 Step 8");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATE_HISTORY_MALE).getAsset(NUMBER_OF_MONTHS_RATE_1)).hasOptions("", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATE_HISTORY_MALE).getAsset(NUMBER_OF_MONTHS_RATE_2)).hasOptions("", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");

            LOGGER.info("REN-23317 Step 12");
            planDefinitionTab.getAssetList().getAsset(RATE_HISTORY_MALE).getAsset(NUMBER_OF_MONTHS_RATE_1).setValue("6");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATE_HISTORY_MALE).getAsset(NUMBER_OF_MONTHS_RATE_2)).isDisabled().isOptional();
        });

        assertSoftly(softly -> {
            LOGGER.info("REN-23319 Step 4");
            ImmutableList.of(RateHistoryFemaleMetaData.RATE_1, RateHistoryFemaleMetaData.NUMBER_OF_MONTHS_RATE_1, RateHistoryFemaleMetaData.RATE_2, RateHistoryFemaleMetaData.NUMBER_OF_MONTHS_RATE_2).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATE_HISTORY_FEMALE).getAsset(control)).isPresent().hasValue(StringUtils.EMPTY).isEnabled());

            LOGGER.info("REN-23319 Step 8");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATE_HISTORY_FEMALE).getAsset(RateHistoryFemaleMetaData.NUMBER_OF_MONTHS_RATE_1)).hasOptions("", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATE_HISTORY_FEMALE).getAsset(RateHistoryFemaleMetaData.NUMBER_OF_MONTHS_RATE_2)).hasOptions("", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");

            LOGGER.info("REN-23319 Step 12");
            planDefinitionTab.getAssetList().getAsset(RATE_HISTORY_FEMALE).getAsset(RateHistoryFemaleMetaData.NUMBER_OF_MONTHS_RATE_1).setValue("6");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATE_HISTORY_FEMALE).getAsset(RateHistoryFemaleMetaData.NUMBER_OF_MONTHS_RATE_2)).isDisabled().isOptional();
        });

        assertSoftly(softly -> {
            LOGGER.info("REN-23322 Step 4");
            ImmutableList.of(CLAIMS, CLAIM_RESERVES, UNDERWRITER_ADJUSTMENT).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(HISTORICAL_CLAIM).getAsset(control)).isPresent().hasValue(StringUtils.EMPTY).isEnabled());
        });

        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(NY_STAT);
        assertThat(ratingAssetList.getAsset(ANNUAL_COVERED_PAYROLL)).isDisabled();
    }
}
