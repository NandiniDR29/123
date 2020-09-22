package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteWaitingPeriodWaivedForCurrentEmployee extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-11908", "REN-11678", "REN-11679", "REN-11683"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteWaitingPeriodWaivedForCurrentEmployee() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        initiateQuoteAndFillToTab(getDefaultLTDMasterPolicyData(), planDefinitionTab.getClass(), false);
        planDefinitionTab.selectDefaultPlan();

        assertSoftly(softly -> {

            // Asserts for REN-11678
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(DEFINITION_OF_DISABILITY))
                    .hasValue("X months Regular Occ")
                    .hasOptions("X months Regular Occ", "Extended Regular Occ", "Extended Any Occ", "X months Regular Occ ADL", "Extended Regular Occ ADL");
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(PARTIAL_DISABILITY_BENEFIT))
                    .hasOptions("Direct", "Proportionate Loss");
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(EARNING_DEFINITION))
                    .hasOptions(
                            "Salary",
                            "Salary & Commission (12 mo)", "Salary & Commission (24 mo)", "Salary & Commission (36 mo)",
                            "Salary & Bonus (12 mo)", "Salary & Bonus (24 mo)", "Salary & Bonus (36 mo)",
                            "Salary, Commission & Bonus (12 mo)", "Salary, Commission & Bonus (24 mo)", "Salary, Commission & Bonus (36 mo)",
                            "W-2 (12 mo)", "W-2 (24 mo)", "W-2 (36 mo)",
                            "K-1 (12 mo)", "K-1 (24 mo)", "K-1 (36 mo)",
                            "Net Profit (12 months)", "Net Profit (24 months)", "Net Profit (36 months)");

            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(WIB_DURATION))
                    .hasValue("12 Months").hasOptions("None", "12 Months", "24 Months");
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(OWN_OCCUPATION_EARNINGS_TEST))
                    .hasOptions(ImmutableList.of("60%", "80%", "99%"));

            //In accordance with REN-20757
            planDefinitionTab.getBenefitScheduleAsset().getAsset(OWN_OCCUPATION_EARNINGS_TEST).setValue("99%");
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(ANY_OCCUPATION_EARNINGS_TEST))
                    .hasOptions(ImmutableList.of("60%", "80%", "85%", "99%"));

            planDefinitionTab.getBenefitScheduleAsset().getAsset(OWN_OCCUPATION_EARNINGS_TEST).setValue("60%");
            // Assert for REN-11679
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(SURVIVOR_BENEFIT_WAITING_PERIOD)).isDisabled();

            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY).getAsset(PlanDefinitionTabMetaData.EligibilityMetaData.WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES)).isOptional().hasValue(VALUE_YES);

            // Assert for 11683/#1
            planDefinitionTab.getBenefitScheduleAsset().getAsset(DEFINITION_OF_DISABILITY).setValue("Extended Regular Occ");
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(ANY_OCCUPATION_EARNINGS_TEST))
                    .isDisabled().hasValue(planDefinitionTab.getBenefitScheduleAsset().getAsset(OWN_OCCUPATION_EARNINGS_TEST).getValue());
            planDefinitionTab.getBenefitScheduleAsset().getAsset(DEFINITION_OF_DISABILITY).setValue("Extended Regular Occ ADL");
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(ANY_OCCUPATION_EARNINGS_TEST))
                    .isDisabled().hasValue(planDefinitionTab.getBenefitScheduleAsset().getAsset(OWN_OCCUPATION_EARNINGS_TEST).getValue());
            planDefinitionTab.getBenefitScheduleAsset().getAsset(DEFINITION_OF_DISABILITY).setValue("X months Regular Occ ADL");
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(ANY_OCCUPATION_EARNINGS_TEST)).isEnabled();
        });

        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultLTDMasterPolicyData(), planDefinitionTab.getClass(), premiumSummaryTab.getClass(), true);
        premiumSummaryTab.submitTab();
        proposeAcceptContractIssueWithDefaultTestData();
    }
}
