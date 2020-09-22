package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.*;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitWaitingPeriodsMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalMaximumMetaData.MAXIMUM_EXPENSE_PERIOD;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.OrthodontiaMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.ASO_PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.CommonMethods.getRandomElementFromList;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAdditionalDentalRules extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {
    private final static String BENEFIT_YEAR = "Benefit Year";
    private final static String CALENDER_YEAR = "Calendar Year";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23230", "REN-13755", "REN-14689", "REN-16445", "REN-22094"}, component = POLICY_GROUPBENEFITS)
    public void testAdditionalDentalRules() {

        AssetList dentalMaximum = planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM);
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), policyInformationTab.getClass());
        assertSoftly(softly -> {
            LOGGER.info("REN-23230 TC01 step 1");
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(getRandomElementFromList(ImmutableList.of("TX", "LA", "GA", "MS")));
            policyInformationTab.getAssetList().getAsset(ASO_PLAN).setValue(VALUE_NO);

            LOGGER.info("REN-23230 TC01 step 2");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(ALACARTE));
            planDefinitionTab.getAssetList().getAsset(ORTHODONTIA).getAsset(ORTHO_COVERAGE).setValue(VALUE_YES);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ORTHODONTIA).getAsset(LIFETIME_MAXIMUM_IN_NETWORK)).isPresent().isRequired().hasValue("$1,000");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ORTHODONTIA).getAsset(LIFETIME_MAXIMUM_OUT_OF_NETWORK)).isPresent().isRequired().hasValue("$1,000");

            LOGGER.info("REN-23230 TC01 step 3");
            planDefinitionTab.getAssetList().getAsset(ORTHODONTIA).getAsset(LIFETIME_MAXIMUM_IN_NETWORK).setValue("$1,050");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ORTHODONTIA).getAsset(LIFETIME_MAXIMUM_IN_NETWORK)).hasNoWarning();

            LOGGER.info("REN-23230 TC01 step 6");
            planDefinitionTab.getAssetList().getAsset(ORTHODONTIA).getAsset(LIFETIME_MAXIMUM_IN_NETWORK).setValue("$1,000");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ORTHODONTIA).getAsset(LIFETIME_MAXIMUM_IN_NETWORK)).hasNoWarning();

            LOGGER.info("REN-13755 step 1");
            ImmutableList.of(ALACARTE, BASEPOS, MAJEPOS, FLEX_PLUS, GRADUATED).forEach(plans -> {
                planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(plans));
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_PLAN_DESIGN_CODE)).isPresent().isDisabled().isOptional();
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_INCLUDED_IN_PACKAGE)).isPresent();
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_INCLUDED_IN_PACKAGE).getAsset(CoveragePackageMetaData.STD)).isPresent().isOptional().hasValue(false);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_INCLUDED_IN_PACKAGE).getAsset(CoveragePackageMetaData.LTD)).isPresent().isOptional().hasValue(false);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_INCLUDED_IN_PACKAGE).getAsset(CoveragePackageMetaData.LIFE)).isPresent().isOptional().hasValue(false);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_INCLUDED_IN_PACKAGE).getAsset(CoveragePackageMetaData.VISION)).isPresent().isOptional().hasValue(false);

            });
            LOGGER.info("REN-13755 step 2");
            planDefinitionTab.fillTab(tdSpecific().getTestData("TestData"));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_PLAN_DESIGN_CODE)).hasValue("1A6X");

            LOGGER.info("REN-14689 step 3");
            planDefinitionTab.getAssetList().getAsset(BENEFIT_WAITING_PERIODS).getAsset(PREVENTIVE_WAITING_PERIOD).setValue("6 months");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_WAITING_PERIODS).getAsset(PREVENTIVE_WAITING_PERIOD)).hasNoWarning();
            ImmutableList.of(BASIC_WAITING_PERIOD, MAJOR_WAITING_PERIOD, PROSTHODONTICS_WAIT_PERIOD, RADIOGRAPHS_WAIT_PERIOD, EPCOS_WAITING_PERIOD).forEach(waitingSubgroup -> {
                planDefinitionTab.getAssetList().getAsset(BENEFIT_WAITING_PERIODS).getAsset(waitingSubgroup).setValue("None");
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_WAITING_PERIODS).getAsset(waitingSubgroup)).hasNoWarning();
            });

            LOGGER.info("REN-16445 step 1");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(FLEX_PLUS));
            dentalMaximum.getAsset(MAXIMUM_EXPENSE_PERIOD).setValue(BENEFIT_YEAR);
            planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE).getAsset(DentalDeductibleMetaData.DEDUCTIBLE_EXPENSE_PERIOD).setValue(BENEFIT_YEAR);
            planDefinitionTab.getAssetList().getAsset(BENEFIT_WAITING_PERIODS).getAsset(BENEFITS_WAIT_WAIVED_ENROLLEE).setValue(VALUE_NO);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(EligibilityMetaData.BENEFIT_PERIOD_BASED_ON_MASTER_POLICY)).isPresent().isDisabled().hasValue(VALUE_YES);

            LOGGER.info("REN-16445 step 2 and step 9");
            ImmutableList.of(ALACARTE, FLEX_PLUS).forEach(plans -> {
                planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(plans));
                dentalMaximum.getAsset(MAXIMUM_EXPENSE_PERIOD).setValue(CALENDER_YEAR);
                planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE).getAsset(DentalDeductibleMetaData.DEDUCTIBLE_EXPENSE_PERIOD).setValue(CALENDER_YEAR);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(EligibilityMetaData.BENEFIT_PERIOD_BASED_ON_MASTER_POLICY)).isAbsent();
            });

            LOGGER.info("REN-16445 step 8");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(ALACARTE));
            dentalMaximum.getAsset(MAXIMUM_EXPENSE_PERIOD).setValue(BENEFIT_YEAR);
            planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE).getAsset(DentalDeductibleMetaData.DEDUCTIBLE_EXPENSE_PERIOD).setValue(BENEFIT_YEAR);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(EligibilityMetaData.BENEFIT_PERIOD_BASED_ON_MASTER_POLICY)).isPresent().isEnabled().hasValue(VALUE_YES);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            Waiters.SLEEP(1000).go();
            policyInformationTab.getAssetList().getAsset(ASO_PLAN).setValue(VALUE_YES);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            ImmutableList.of(ASO, ASOALC).forEach(subgroup -> {
                planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(subgroup));
                dentalMaximum.getAsset(MAXIMUM_EXPENSE_PERIOD).setValue(BENEFIT_YEAR);
                planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE).getAsset(DentalDeductibleMetaData.DEDUCTIBLE_EXPENSE_PERIOD).setValue(BENEFIT_YEAR);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(EligibilityMetaData.BENEFIT_PERIOD_BASED_ON_MASTER_POLICY)).isPresent().isEnabled().hasValue(VALUE_YES);
                dentalMaximum.getAsset(MAXIMUM_EXPENSE_PERIOD).setValue(CALENDER_YEAR);
                planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE).getAsset(DentalDeductibleMetaData.DEDUCTIBLE_EXPENSE_PERIOD).setValue(CALENDER_YEAR);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(EligibilityMetaData.BENEFIT_PERIOD_BASED_ON_MASTER_POLICY)).isAbsent();
            });

            LOGGER.info("REN-16445 step 13");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(ASO_PLAN).setValue(VALUE_NO);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(GRADUATED));
            dentalMaximum.getAsset(MAXIMUM_EXPENSE_PERIOD).setValue(BENEFIT_YEAR);
            planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE).getAsset(DentalDeductibleMetaData.DEDUCTIBLE_EXPENSE_PERIOD).setValue(BENEFIT_YEAR);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(EligibilityMetaData.BENEFIT_PERIOD_BASED_ON_MASTER_POLICY)).isPresent().isDisabled().hasValue(VALUE_NO);

            LOGGER.info("REN-14689 step 4 and REN-22094 step 1");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultDNMasterPolicyData(), policyInformationTab.getClass(), premiumSummaryTab.getClass(), true);
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Area Factor Version"))).isAbsent();
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        });
    }
}
