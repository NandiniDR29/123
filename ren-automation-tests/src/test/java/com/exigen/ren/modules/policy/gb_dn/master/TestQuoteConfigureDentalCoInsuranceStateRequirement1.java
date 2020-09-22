package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
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
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.CoInsuranceMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.PPO_EPO_PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES;
import static com.exigen.ren.utils.CommonMethods.getRandomElementFromList;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteConfigureDentalCoInsuranceStateRequirement1 extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static AssetList coInsuranceAssetList = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CO_INSURANCE);
    private static final ImmutableList<String> SITUS_STATE_VALUES = ImmutableList.of("TX", "LA", "GA", "MS");
    private static final String MESSAGE = "State Requirement: %1$s - Out of Network must be equal to %1$s - In Network.";

    private static final ImmutableList<AssetDescriptor<ComboBox>> CO_INSURANCE_PREVENTIVE_IN_NETWORK_VALUES =
            ImmutableList.of(PREVENTIVE_FIRST_YEAR_IN_NETWORK, PREVENTIVE_SECOND_YEAR_IN_NETWORK, PREVENTIVE_THIRD_YEAR_IN_NETWORK, PREVENTIVE_FOURTH_YEAR_IN_NETWORK);
    private static final ImmutableList<AssetDescriptor<ComboBox>> CO_INSURANCE_PREVENTIVE_OUT_OF_NETWORK_VALUES =
            ImmutableList.of(PREVENTIVE_FIRST_YEAR_OUT_OF_NETWORK, PREVENTIVE_SECOND_YEAR_OUT_OF_NETWORK, PREVENTIVE_THIRD_YEAR_OUT_OF_NETWORK, PREVENTIVE_FOURTH_YEAR_OUT_OF_NETWORK);


    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13199", component = POLICY_GROUPBENEFITS)
    public void testQuoteConfigureDentalCoInsuranceStateRequirement1() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel()), "100"), planDefinitionTab.getClass());
        selectFirstPlanFromDNMasterPolicyDefaultTestData();
        planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);

        assertSoftly(softly -> {
            verifyCoInsuranceFieldsBySitusState(getRandomElementFromList(SITUS_STATE_VALUES), softly);

            LOGGER.info("---=={Step 8}==---");
            setSitusSiteAndReturnToPlanDefinitionTab("AL");
            planDefinitionTab.getAssetList().fill(getDefaultDNMasterPolicyData());
            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);
            coInsuranceAssetList.getAsset(IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_YES);
            coInsuranceAssetList.getAsset(NUMBER_OF_GRADED_YEARS).setValue("4");

            CO_INSURANCE_PREVENTIVE_IN_NETWORK_VALUES.forEach(control -> coInsuranceAssetList.getAsset(control).setValue("40%"));
            CO_INSURANCE_PREVENTIVE_OUT_OF_NETWORK_VALUES.forEach(control -> coInsuranceAssetList.getAsset(control).setValue("30%"));
            planDefinitionTab.submitTab();
            classificationManagementMpTab.fillTab(getDefaultDNMasterPolicyData());
            classificationManagementMpTab.submitTab();
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            LOGGER.info("---=={Step 9}==---");
            groupDentalMasterPolicy.dataGather().start();
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            coInsuranceAssetList.getAsset(IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_NO);

            coInsuranceAssetList.getAsset(PREVENTIVE_IN_NETWORK).setValue("40%");
            coInsuranceAssetList.getAsset(PREVENTIVE_OUT_OF_NETWORK).setValue("30%");
            planDefinitionTab.submitTab();

            premiumSummaryTab.navigate();
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        });
    }

    private void verifyCoInsuranceFieldsBySitusState(String state, ETCSCoreSoftAssertions softly) {
        setSitusSiteAndReturnToPlanDefinitionTab(state);

        LOGGER.info("---=={Step 1}==---");
        coInsuranceAssetList.getAsset(IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_YES);
        softly.assertThat(coInsuranceAssetList.getAsset(NUMBER_OF_GRADED_YEARS)).hasValue("3");

        planDefinitionTab.getAssetList().fill(getDefaultDNMasterPolicyData());
        planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);
        coInsuranceAssetList.getAsset(IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_YES);
        coInsuranceAssetList.getAsset(NUMBER_OF_GRADED_YEARS).setValue("4");
        verifyDisplayedControls(CO_INSURANCE_PREVENTIVE_IN_NETWORK_VALUES, softly);
        verifyDisplayedControls(CO_INSURANCE_PREVENTIVE_OUT_OF_NETWORK_VALUES, softly);

        LOGGER.info("---=={Step 2}==---");
        CO_INSURANCE_PREVENTIVE_IN_NETWORK_VALUES.forEach(control -> coInsuranceAssetList.getAsset(control).setValue("40%"));
        CO_INSURANCE_PREVENTIVE_OUT_OF_NETWORK_VALUES.forEach(control -> coInsuranceAssetList.getAsset(control).setValue("30%"));
        premiumSummaryTab.navigate();
        premiumSummaryTab.rate();

        CO_INSURANCE_PREVENTIVE_OUT_OF_NETWORK_VALUES.forEach(control -> softly.assertThat(ErrorPage.tableError).hasMatchingRows(ErrorPage.TableError.MESSAGE.getName(),
                String.format(MESSAGE, control.getLabel().split("-")[0].trim())));
        Tab.buttonCancel.click();
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());

        LOGGER.info("---=={Step 3}==---");
        CO_INSURANCE_PREVENTIVE_OUT_OF_NETWORK_VALUES.forEach(control -> coInsuranceAssetList.getAsset(control).setValue("40%"));
        planDefinitionTab.submitTab();
        classificationManagementMpTab.fillTab(getDefaultDNMasterPolicyData());
        classificationManagementMpTab.submitTab();
        premiumSummaryTab.submitTab();
        softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("---=={Step 4}==---");
        groupDentalMasterPolicy.dataGather().start();
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        coInsuranceAssetList.getAsset(IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_NO);
        verifyNotDisplayedControls(CO_INSURANCE_PREVENTIVE_IN_NETWORK_VALUES, softly);
        verifyNotDisplayedControls(CO_INSURANCE_PREVENTIVE_OUT_OF_NETWORK_VALUES, softly);
        verifyDisplayedControls(ImmutableList.of(PREVENTIVE_IN_NETWORK, PREVENTIVE_OUT_OF_NETWORK), softly);

        LOGGER.info("---=={Step 5}==---");
        coInsuranceAssetList.getAsset(PREVENTIVE_IN_NETWORK).setValue("40%");
        coInsuranceAssetList.getAsset(PREVENTIVE_OUT_OF_NETWORK).setValue("30%");

        softly.assertThat(coInsuranceAssetList.getAsset(PREVENTIVE_OUT_OF_NETWORK)).hasWarningWithText(
                String.format(MESSAGE, PREVENTIVE_OUT_OF_NETWORK.getLabel().split("-")[0].trim()));

        premiumSummaryTab.navigate();
        premiumSummaryTab.rate();

        softly.assertThat(ErrorPage.tableError).hasMatchingRows(ErrorPage.TableError.MESSAGE.getName(),
                String.format(MESSAGE, PREVENTIVE_OUT_OF_NETWORK.getLabel().split("-")[0].trim()));
        Tab.buttonCancel.click();
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());

        LOGGER.info("---=={Step 6}==---");
        coInsuranceAssetList.getAsset(PREVENTIVE_OUT_OF_NETWORK).setValue("40%");
        premiumSummaryTab.navigate();
        premiumSummaryTab.submitTab();
        softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        groupDentalMasterPolicy.dataGather().start();
    }

    private void verifyDisplayedControls(ImmutableList<AssetDescriptor<ComboBox>> list, ETCSCoreSoftAssertions softly) {
        list.forEach(control -> softly.assertThat(coInsuranceAssetList.getAsset(control)).isPresent().isEnabled().isRequired().hasValue("100%"));
    }

    private void verifyNotDisplayedControls(ImmutableList<AssetDescriptor<ComboBox>> list, ETCSCoreSoftAssertions softly) {
        list.forEach(control -> softly.assertThat(coInsuranceAssetList.getAsset(control)).isAbsent());
    }

    private void setSitusSiteAndReturnToPlanDefinitionTab(String situsState) {
        LOGGER.info(String.format("Set Situs State = %s", situsState));
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
        policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(situsState);
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        selectFirstPlanFromDNMasterPolicyDefaultTestData();
    }
}