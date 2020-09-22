package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.INCLUDED;
import static com.exigen.ren.main.enums.ValueConstants.NOT_INCLUDED;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetaData.MINIMUM_HOURLY_REQUIREMENT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.EmployerBenefitsMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteEmployerBenefits extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private final static String BENEFIT_PERCENT_ERROR_MESSAGE = "'Workplace modification benefit %' should be within [50, 100], and the increment is 1";
    private final static String BENEFIT_MAXIMUM_ERROR_MESSAGE = "'Workplace modification benefit Maximum' should be within [$1,000.00, $25,000.00], and the increment is $1,000.00";
    private final static String MINIMUM_HOURLY_ERROR_MESSAGE = "The range of Minimum Hourly Requirement (hours per week) should be [15, 40], and increment is 0.5";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-10416", "REN-11955"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteEmployerBenefits() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        initiateQuoteAndFillToTab(getDefaultLTDMasterPolicyData(),
                planDefinitionTab.getClass(), false);
        policyInformationTab.changeSitusStateValue("CT");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.PLAN_DEFINITION.get());

        planDefinitionTab.selectDefaultPlan();
        assertSoftly(softly -> {

            // Asserts for REN-11955
            softly.assertThat(planDefinitionTab.getEligibilityAsset().getAsset(MINIMUM_HOURLY_REQUIREMENT)).hasValue("");
            ImmutableList.of("15", "30", "40").forEach(value -> {
                planDefinitionTab.getEligibilityAsset().getAsset(MINIMUM_HOURLY_REQUIREMENT).setValue(value);
                softly.assertThat(planDefinitionTab.getEligibilityAsset().getAsset(MINIMUM_HOURLY_REQUIREMENT)).hasValue(String.format("%s.00", value));
            });

            planDefinitionTab.getEligibilityAsset().getAsset(MINIMUM_HOURLY_REQUIREMENT).setValue("30.5");
            softly.assertThat(planDefinitionTab.getEligibilityAsset().getAsset(MINIMUM_HOURLY_REQUIREMENT)).hasValue("30.50");

            ImmutableList.of("14.5", "15.49", "40.5").forEach(value -> {
                planDefinitionTab.getEligibilityAsset().getAsset(MINIMUM_HOURLY_REQUIREMENT).setValue(value);
                softly.assertThat(planDefinitionTab.getEligibilityAsset().getAsset(MINIMUM_HOURLY_REQUIREMENT).getWarning().orElse(""))
                        .contains(MINIMUM_HOURLY_ERROR_MESSAGE);
            });

            planDefinitionTab.getEmployerBenefitsAsset().getAssets(
                    WORKPLACE_MODIFICATION_BENEFIT,
                    WORKPLACE_MODIFICATION_BENEFIT_PERCENT,
                    WORKPLACE_MODIFICATION_BENEFIT_MAXIMUM)
                    .forEach(asset -> softly.assertThat(asset).isPresent());

            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(NONE)).hasValue(true).isEnabled();

            ImmutableList.of(
                    OWNER,
                    SOLE_PROPRIETOR,
                    PARTNER,
                    SHAREHOLDER,
                    DIRECTOR,
                    MANAGER)
                    .forEach(asset -> softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(asset)).hasValue(false).isDisabled());

            planDefinitionTab.getEmployerBenefitsAsset().getAsset(NONE).setValue(false);
            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(REVENUE_PROTECTION_BENEFIT_DURATION)).isPresent();
            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(REVENUE_PROTECTION_BENEFIT_PERCENT)).isPresent();
        });

        policyInformationTab.changeSitusStateValue("NY");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.PLAN_DEFINITION.get());
        planDefinitionTab.selectDefaultPlan();
        assertSoftly(softly -> {

            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(NONE)).hasValue(true).isDisabled();

            ImmutableList.of(
                    OWNER,
                    SOLE_PROPRIETOR,
                    PARTNER,
                    SHAREHOLDER,
                    DIRECTOR,
                    MANAGER)
                    .forEach(asset -> softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(asset)).hasValue(false).isDisabled());

            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(REVENUE_PROTECTION_BENEFIT_DURATION)).isAbsent();
            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(REVENUE_PROTECTION_BENEFIT_PERCENT)).isAbsent();
        });

        policyInformationTab.changeSitusStateValue("PA");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.PlanGenericInfoTab.PLAN_DEFINITION.get());
        planDefinitionTab.selectDefaultPlan();
        assertSoftly(softly -> {

            planDefinitionTab.getEmployerBenefitsAsset().getAsset(NONE).setValue(true);
            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(REVENUE_PROTECTION_BENEFIT_DURATION)).isAbsent();
            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(REVENUE_PROTECTION_BENEFIT_PERCENT)).isAbsent();

            planDefinitionTab.getEmployerBenefitsAsset().getAsset(NONE).setValue(false);
            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(REVENUE_PROTECTION_BENEFIT_PERCENT))
                    .isPresent().isRequired().hasValue("5%").hasOptions(ImmutableList.of("5%", "10%", "15%", "20%", "25%"));
            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(REVENUE_PROTECTION_BENEFIT_DURATION))
                    .isPresent().isRequired().hasValue("3 Months").hasOptions(
                            ImmutableList.of("3 Months", "4 Months", "5 Months", "6 Months", "7 Months", "8 Months", "9 Months", "10 Months", "11 Months", "12 Months", "18 Months", "24 Months"));
            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(WORKPLACE_MODIFICATION_BENEFIT))
                    .isPresent().isRequired().hasValue(INCLUDED).hasOptions(ImmutableList.of(INCLUDED, NOT_INCLUDED));

            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(WORKPLACE_MODIFICATION_BENEFIT_PERCENT))
                    .isPresent().isRequired().hasValue("100");

            ImmutableList.of("50", "75", "100").forEach(value -> {
                planDefinitionTab.getEmployerBenefitsAsset().getAsset(WORKPLACE_MODIFICATION_BENEFIT_PERCENT).setValue(value);
                softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(WORKPLACE_MODIFICATION_BENEFIT_PERCENT))
                        .hasValue(value);
            });

            ImmutableList.of("49", "50.5", "101").forEach(value -> {
                planDefinitionTab.getEmployerBenefitsAsset().getAsset(WORKPLACE_MODIFICATION_BENEFIT_PERCENT).setValue(value);
                softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(WORKPLACE_MODIFICATION_BENEFIT_PERCENT).getWarning().orElse(""))
                        .contains(BENEFIT_PERCENT_ERROR_MESSAGE);
            });

            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(WORKPLACE_MODIFICATION_BENEFIT_MAXIMUM))
                    .isPresent().isRequired().hasValue(new Currency("2000").toString());

            ImmutableList.of("1000", "15000", "25000").forEach(value -> {
                planDefinitionTab.getEmployerBenefitsAsset().getAsset(WORKPLACE_MODIFICATION_BENEFIT_MAXIMUM).setValue(value);
                softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(WORKPLACE_MODIFICATION_BENEFIT_MAXIMUM))
                        .hasValue(new Currency(value).toString());
            });

            ImmutableList.of("999", "1500", "25001", "26000").forEach(value -> {
                planDefinitionTab.getEmployerBenefitsAsset().getAsset(WORKPLACE_MODIFICATION_BENEFIT_MAXIMUM).setValue(value);
                softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(WORKPLACE_MODIFICATION_BENEFIT_MAXIMUM).getWarning().orElse(""))
                        .contains(BENEFIT_MAXIMUM_ERROR_MESSAGE);
            });

            planDefinitionTab.getEmployerBenefitsAsset().getAsset(WORKPLACE_MODIFICATION_BENEFIT).setValue(NOT_INCLUDED);
            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(WORKPLACE_MODIFICATION_BENEFIT_PERCENT)).isAbsent();
            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(WORKPLACE_MODIFICATION_BENEFIT_MAXIMUM)).isAbsent();
        });
    }
}