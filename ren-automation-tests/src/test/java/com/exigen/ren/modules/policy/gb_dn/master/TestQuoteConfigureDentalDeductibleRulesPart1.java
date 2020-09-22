package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DENTAL_DEDUCTIBLE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalDeductibleMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.PPO_EPO_PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.CommonMethods.getRandomElementFromList;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteConfigureDentalDeductibleRulesPart1 extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private final static String NUMBER_OF_GRADED_YEARS_4 = "4";
    private static AssetList assetListDeductible;
    private static final ImmutableList<String> SITUS_STATE_VALUES = ImmutableList.of("TX", "LA", "GA", "MS");
    private static final ImmutableList<AssetDescriptor<ComboBox>> DEDUCTIBLE_CONTROLS_LIST = ImmutableList.of(
            DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK,
            DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK, DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK);

    private final static String DEDUCTIBLE_IN_NETWORK_MESSAGE = "State Requirement: Deductible - Out of Network must be equal to Deductible - In Network.";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13732", component = POLICY_GROUPBENEFITS)
    public void testConfigureDentalDeductibleRulesScenario1() {
        preconditions();
        assertSoftly(softly -> verifySitusState(getRandomElementFromList(SITUS_STATE_VALUES), softly));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13732", component = POLICY_GROUPBENEFITS)
    public void testConfigureDentalDeductibleRulesScenario2() {
        preconditions();

        assertSoftly(softly -> {
            setSitusSiteAndReturnToPlanDefinitionTab("NC");
            LOGGER.info("---=={Step 7-11}==---");
            assetListDeductible.getAsset(IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_YES);
            assetListDeductible.getAsset(NUMBER_OF_GRADED_YEARS).setValue(NUMBER_OF_GRADED_YEARS_4);

            ImmutableList.of(DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK).
                    forEach(control -> assetListDeductible.getAsset(control).setValue("$25"));
            ImmutableList.of(DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK).
                    forEach(control -> {
                        assetListDeductible.getAsset(control).setValue("$25");
                        Tab.buttonNext.click();
                        softly.assertThat(assetListDeductible.getAsset(control)).hasNoWarning();
                    });

            LOGGER.info("---=={Step 18-22}==---");
            planDefinitionTab.getAssetList().fill(getDefaultDNMasterPolicyData());
            assetListDeductible.getAsset(IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_NO);
            assetListDeductible.getAsset(DEDUCTIBLE_IN_NETWORK).setValue("$100");
            assetListDeductible.getAsset(DEDUCTIBLE_OUT_OF_NETWORK).setValue("$100");
            Tab.buttonNext.click();
            classificationManagementMpTab.fillTab(getDefaultDNMasterPolicyData());
            premiumSummaryTab.navigate();
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            LOGGER.info("---=={Step 40-44}==---");
            groupDentalMasterPolicy.dataGather().start();
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            assetListDeductible.getAsset(DEDUCTIBLE_IN_NETWORK).setValue("$0");
            assetListDeductible.getAsset(DEDUCTIBLE_OUT_OF_NETWORK).setValue("$250");

            Tab.buttonNext.click();
            premiumSummaryTab.navigate();
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        });

    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13732", component = POLICY_GROUPBENEFITS)
    public void testConfigureDentalDeductibleRulesScenario3() {
        preconditions();

        assertSoftly(softly -> {
            setSitusSiteAndReturnToPlanDefinitionTab("OK");
            LOGGER.info("---=={Step 7-11}==---");
            assetListDeductible.getAsset(IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_YES);
            assetListDeductible.getAsset(NUMBER_OF_GRADED_YEARS).setValue(NUMBER_OF_GRADED_YEARS_4);

            ImmutableList.of(DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK).
                    forEach(control -> assetListDeductible.getAsset(control).setValue("$25"));
            ImmutableList.of(DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK).
                    forEach(control -> {
                        assetListDeductible.getAsset(control).setValue("$75");
                        Tab.buttonNext.click();
                        softly.assertThat(assetListDeductible.getAsset(control)).hasNoWarning();
                    });

            LOGGER.info("---=={Step 18-22}==---");
            planDefinitionTab.getAssetList().fill(getDefaultDNMasterPolicyData());
            assetListDeductible.getAsset(IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_NO);
            assetListDeductible.getAsset(DEDUCTIBLE_IN_NETWORK).setValue("$100");
            assetListDeductible.getAsset(DEDUCTIBLE_OUT_OF_NETWORK).setValue("$300");
            Tab.buttonNext.click();
            classificationManagementMpTab.fillTab(getDefaultDNMasterPolicyData());
            premiumSummaryTab.navigate();
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        });

    }

    private void setSitusSiteAndReturnToPlanDefinitionTab(String situsState) {
        LOGGER.info(String.format("Set Situs State = %s", situsState));
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
        policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(situsState);
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        selectFirstPlanFromDNMasterPolicyDefaultTestData();
    }

    private void verifySitusState(String state, ETCSCoreSoftAssertions softly){

        setSitusSiteAndReturnToPlanDefinitionTab(state);
        LOGGER.info("---=={Step 2}==---");
        assetListDeductible.getAsset(IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_YES);

        LOGGER.info("---=={Step 3}==---");
        assetListDeductible.getAsset(NUMBER_OF_GRADED_YEARS).setValue(NUMBER_OF_GRADED_YEARS_4);

        LOGGER.info("---=={Step 4}==---");
        DEDUCTIBLE_CONTROLS_LIST.forEach(control-> assetListDeductible.getAsset(control).setValue("$200"));
        assetListDeductible.getAsset(FAMILY_DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK).setValue("Unlimited");
        assetListDeductible.getAsset(FAMILY_DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK).setValue("Unlimited");
        assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE).setValue(VALUE_YES);
        assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_IN_NETWORK).setValue("$50");
        ImmutableList.of(DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK).
                forEach(control-> {
                    assetListDeductible.getAsset(control).setValue("$100");
                    premiumSummaryTab.navigate();
                    premiumSummaryTab.rate();
                    softly.assertThat(ErrorPage.tableError).hasRowsThatContain(1, ImmutableMap.of(ErrorPage.TableError.MESSAGE.getName(), control.getLabel()));
                    ErrorPage.tableError.getRowContains(
                            ImmutableMap.of(ErrorPage.TableError.MESSAGE.getName(), control.getLabel()))
                            .getCell(ErrorPage.TableError.CODE.getName()).controls.links.getFirst().click();
                    assetListDeductible.getAsset(control).setValue("$200");
                });

        LOGGER.info("---=={Step 8-10}==---");
        Tab.buttonNext.click();
        ImmutableList.of(DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK).
                forEach(control->
                        softly.assertThat(assetListDeductible.getAsset(control)).hasNoWarning());

        LOGGER.info("---=={Step 11}==---");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        assetListDeductible.getAsset(IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_NO);

        DEDUCTIBLE_CONTROLS_LIST.forEach(control -> softly.assertThat(assetListDeductible.getAsset(control)).isAbsent());
        softly.assertThat(assetListDeductible.getAsset(NUMBER_OF_GRADED_YEARS)).isAbsent();
        ImmutableList.of(DEDUCTIBLE_IN_NETWORK, DEDUCTIBLE_OUT_OF_NETWORK).forEach(control->
                softly.assertThat(assetListDeductible.getAsset(control)).isPresent());

        LOGGER.info("---=={Step 12}==---");
        assetListDeductible.getAsset(DEDUCTIBLE_IN_NETWORK).setValue("$200");
        assetListDeductible.getAsset(DEDUCTIBLE_OUT_OF_NETWORK).setValue("$300");

        LOGGER.info("---=={Step 14-17}==---");
        premiumSummaryTab.navigate();
        premiumSummaryTab.rate();
        softly.assertThat(ErrorPage.tableError).hasRowsThatContain(ImmutableMap.of(ErrorPage.TableError.MESSAGE.getName(), DEDUCTIBLE_IN_NETWORK_MESSAGE));
        ErrorPage.tableError.getRowContains(
                ImmutableMap.of(ErrorPage.TableError.MESSAGE.getName(), DEDUCTIBLE_IN_NETWORK_MESSAGE))
                .getCell(ErrorPage.TableError.CODE.getName()).controls.links.getFirst().click();
    }

    private void preconditions(){
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), PremiumSummaryTab.class);

        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        assetListDeductible = planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE);
        planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);
    }
}