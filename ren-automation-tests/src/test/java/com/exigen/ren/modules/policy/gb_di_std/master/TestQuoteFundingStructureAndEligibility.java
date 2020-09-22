package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetaData.MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetaData.WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteFundingStructureAndEligibility extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    private static final String ERROR_MESSAGE = "'Minimum Hourly Requirement (hours per week)' should be between 15 and 40 and increment is .5";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-6725", "REN-11437", "REN-11438", "REN-37678"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteDeclineByCustomer() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        initiateSTDQuoteAndFillToTab(getDefaultSTDMasterPolicyData(),
                PlanDefinitionTab.class, false);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of("NC"));

        //Assertion for REN-6725:
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY).getAsset(WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEE)).isOptional().hasValue(VALUE_YES);

        //Assertion for REN-11438:
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATE_BASIS))
                .hasOptions("Per $10 Total Weekly Benefit").doesNotContainOption("Per Month Per Employee");

        //Assertions for REN-11437:
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).setValue("15");
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK)).hasNoWarning();

        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).setValue("14.5");
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).getWarning().orElse(""))
                .contains(ERROR_MESSAGE);

        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).setValue("40");
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK)).hasNoWarning();

        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).setValue("40.5");
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).getWarning().orElse(""))
                .contains(ERROR_MESSAGE);

        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).setValue("15.5");
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK)).hasNoWarning();

        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).setValue("15.1");
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).getWarning().orElse(""))
                .contains(ERROR_MESSAGE);
    }
}
