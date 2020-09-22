package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ALACARTE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalMaximumMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteConfigureDentalMaximum extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private ETCSCoreSoftAssertions softly;
    private final static String PLAN_MAXIMUM_1000 = "$1,000";
    private final static String PLAN_MAXIMUM_900 = "$900";
    private final static String SITUS_STATE_AL = "AL";
    private final static String NUMBER_OF_GRADED_YEARS_4 = "4";

    private static final ImmutableList<String> SITUS_STATE_VALUES = ImmutableList.of("TX", "LA", "GA", "MS");

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13722", component = POLICY_GROUPBENEFITS)
    public void testQuoteConfigureDentalMaximum() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel()), "100"), PlanDefinitionTab.class);
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(ALACARTE));
        planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);

        assertSoftly(softly -> {
            this.softly = softly;

            LOGGER.info("---=={Step 1}==---");
            SITUS_STATE_VALUES.forEach(this::verifyStateRequirementFunctionalityForAttributesBySitusSite);

            LOGGER.info("7. After executing TC with all 'Situs State' values mentioned in preconditions, on Policy Information Tab set attribute 'Situs State' = AL");
            setSitusSiteAndReturnToPlanDefinitionTab(SITUS_STATE_AL);

            LOGGER.info("9. Set attribute 'Is it graded Maximum' = \"Yes\"");
            planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(IS_IT_GRADED_DENTAL_MAXIMUM).setValue(VALUE_YES);

            LOGGER.info("10. Set attribute 'Number of Graded Years' value = 4");
            planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(NUMBER_OF_GRADED_YEARS).setValue(NUMBER_OF_GRADED_YEARS_4);

            LOGGER.info("12.-12.1.Previous validation not applicable for other 'Situs State'");
            ImmutableList.of(
                    PLAN_MAXIMUM_FIRST_YEAR_IN_NETWORK, PLAN_MAXIMUM_SECOND_YEAR_IN_NETWORK, PLAN_MAXIMUM_THIRD_YEAR_IN_NETWORK, PLAN_MAXIMUM_FOURTH_YEAR_IN_NETWORK)
                    .forEach(control -> planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(control).setValue(PLAN_MAXIMUM_1000));

            ImmutableList.of(
                    PLAN_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_SECOND_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK)
                    .forEach(control -> {
                        planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(control).setValue(PLAN_MAXIMUM_900);
                        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(control)).hasNoWarning();
                    });

            LOGGER.info("---=={Step 2}==---");
            verifyThatDefaultCalculationRelatedToAttributeRollOverBenefitLimit();
        });
    }

    private void verifyStateRequirementFunctionalityForAttributesBySitusSite(String situsState) {

        LOGGER.info(String.format("Set Situs State = %s", situsState));
        setSitusSiteAndReturnToPlanDefinitionTab(situsState);

        LOGGER.info("1. Set attribute 'Is it graded Maximum' = \"Yes\"");
        planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(IS_IT_GRADED_DENTAL_MAXIMUM).setValue(VALUE_YES);

        LOGGER.info("1.1. attribute 'Number of Graded Years' is displayed and default value is blank;");
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(NUMBER_OF_GRADED_YEARS)).isPresent().isEnabled().isRequired().hasValue("3");

        LOGGER.info("2. Set attribute 'Number of Graded Years' value = 4");
        planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(NUMBER_OF_GRADED_YEARS).setValue(NUMBER_OF_GRADED_YEARS_4);

        LOGGER.info("2.1-2.3 All attributes like 'Plan Maximum (...) - Out of Network' and  'Plan Maximum (...) - In Network' are displayed and mandatory");
        ImmutableList.of(
                PLAN_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_SECOND_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK,
                PLAN_MAXIMUM_FIRST_YEAR_IN_NETWORK, PLAN_MAXIMUM_SECOND_YEAR_IN_NETWORK, PLAN_MAXIMUM_THIRD_YEAR_IN_NETWORK, PLAN_MAXIMUM_FOURTH_YEAR_IN_NETWORK).forEach(control ->
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(control)).isPresent().isEnabled().isRequired());
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(PLAN_MAXIMUM_FIRST_YEAR_IN_NETWORK)).hasValue("$750");
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(PLAN_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK)).hasValue("$750");
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(PLAN_MAXIMUM_SECOND_YEAR_IN_NETWORK)).hasValue("$1,000");
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(PLAN_MAXIMUM_SECOND_YEAR_OUT_OF_NETWORK)).hasValue("$1,000");
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(PLAN_MAXIMUM_THIRD_YEAR_IN_NETWORK)).hasValue("$1,500");
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(PLAN_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK)).hasValue("$1,500");
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(PLAN_MAXIMUM_FOURTH_YEAR_IN_NETWORK)).hasValue("$1,500");
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(PLAN_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK)).hasValue("$1,500");

        LOGGER.info("3. Set for all attributes like 'Plan Maximum (...) - In Network' value = 1000");
        ImmutableList.of(
                PLAN_MAXIMUM_FIRST_YEAR_IN_NETWORK, PLAN_MAXIMUM_SECOND_YEAR_IN_NETWORK, PLAN_MAXIMUM_THIRD_YEAR_IN_NETWORK, PLAN_MAXIMUM_FOURTH_YEAR_IN_NETWORK)
                .forEach(control -> planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(control).setValue(PLAN_MAXIMUM_1000));

        LOGGER.info("4-5. Set for all attributes like 'Plan Maximum (...) - Out of Network' = 900 and verify error message than Set for all attributes = 1000");
        ImmutableList.of(
                PLAN_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_SECOND_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK)
                .forEach(control -> {
                    planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(control).setValue(PLAN_MAXIMUM_900);
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(control).getWarning().orElse("")).contains(
                            String.format("State Requirement: %1$s - Out of Network must be equal to %1$s - In Network.", control.getLabel().split("-")[0].trim()));
                    planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(control).setValue(PLAN_MAXIMUM_1000);
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(control)).hasNoWarning();
                });

        planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(IS_IT_GRADED_DENTAL_MAXIMUM).setValue(VALUE_NO);
    }

    private void setSitusSiteAndReturnToPlanDefinitionTab(String situsState) {
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
        policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(situsState);
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        selectFirstPlanFromDNMasterPolicyDefaultTestData();
    }

    private void verifyThatDefaultCalculationRelatedToAttributeRollOverBenefitLimit() {

        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(MAXIMUM_ROLL_OVER)).isDisabled();
        LOGGER.info("1. Set attribute 'Is it Graded Maximum' = \"No\"");
        planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(IS_IT_GRADED_DENTAL_MAXIMUM).setValue(VALUE_NO);
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(MAXIMUM_ROLL_OVER)).isEnabled().isRequired();

        LOGGER.info("2.1. By default attribute 'Plan Maximum - in Network' = $1500");
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(PLAN_MAXIMUM_OUT_NETWORK)).isEnabled().isRequired().hasValue("$1,500");

        LOGGER.info("3. Set attribute 'Maximum Roll Over' = \"Yes\"");
        planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(MAXIMUM_ROLL_OVER).setValue(VALUE_YES);
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(ROLL_OVER_THRESHOLD)).isPresent().isEnabled().isRequired().hasValue(new Currency("750").toString());
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(ROLL_OVER_BENEFIT)).isPresent().isEnabled().isRequired().hasValue(new Currency("375").toString());
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(ROLL_OVER_BENEFIT_LIMIT)).isPresent().isEnabled().isRequired().hasValue(new Currency("1500").toString());

        LOGGER.info("4. Set attribute 'Maximum Roll Over' = \"No\"");
        planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(MAXIMUM_ROLL_OVER).setValue(VALUE_NO);
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(ROLL_OVER_THRESHOLD)).isAbsent();
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(ROLL_OVER_BENEFIT)).isAbsent();
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(ROLL_OVER_BENEFIT_LIMIT)).isAbsent();

        LOGGER.info("5. Set attribute 'Plan Maximum - in Network' = $2500");
        planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(PLAN_MAXIMUM_OUT_NETWORK).setValue("$2,500");

        LOGGER.info("6. Set attribute 'Maximum Roll Over' = \"Yes\"");
        planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(MAXIMUM_ROLL_OVER).setValue(VALUE_YES);

        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(ROLL_OVER_THRESHOLD)).isPresent().isEnabled().isRequired().hasValue(new Currency("750").toString());
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(ROLL_OVER_BENEFIT)).isPresent().isEnabled().isRequired().hasValue(new Currency("375").toString());
        softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(ROLL_OVER_BENEFIT_LIMIT)).isPresent().isEnabled().isRequired().hasValue(new Currency("1500").toString());
    }
}