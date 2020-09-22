package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.ERROR_MESSAGE_MUST_BE_WITHIN_30;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.CO_INSURANCE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.CoInsuranceMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES;
import static com.exigen.ren.utils.CommonMethods.getRandomElementFromList;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteRulesVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static final String ERROR_MESSAGE_TEMPLATE_MUST_BE_EQUAL = "State Requirement: %s must be equal to %s.";
    private static final String ERROR_MESSAGE_TEMPLATE_MUST_BE_EQUAL_WITHOUT_POINT = "State Requirement: %s must be equal to %s";
    private static final String ERROR_MESSAGE_TEMPLATE_MUST_BE_WITHIN_20 = "State Requirement: %s must be within 20%% of %s.";
    private static final String ERROR_MESSAGE_TEMPLATE_MUST_BE_WITHIN_25 = "State Requirement: %s must be within 25%% of %s.";
    private static final String ERROR_MESSAGE_TEMPLATE_BOTH_MUST_BE_GREATER_THAN_0 = "State Requirement: If value for %s or %s is greater than 0, then both must be greater than 0.";

    // Create map with (xxx - Out of Network, xxx - In Network) controls
    // key: (xxx - Out of Network)
    // value: (xxx - In of Network)
    private static final ImmutableMap<AssetDescriptor<ComboBox>, AssetDescriptor<ComboBox>> CO_INSURANCE_PAIRS =
            ImmutableMap.<AssetDescriptor<ComboBox>, AssetDescriptor<ComboBox>>builder()
                    .put(PROSTHODONTICS_FIRST_YEAR_OUT_OF_NETWORK, PROSTHODONTICS_FIRST_YEAR_IN_NETWORK)
                    .put(PROSTHODONTICS_SECOND_YEAR_OUT_OF_NETWORK, PROSTHODONTICS_SECOND_YEAR_IN_NETWORK)
                    .put(PROSTHODONTICS_THIRD_YEAR_OUT_OF_NETWORK, PROSTHODONTICS_THIRD_YEAR_IN_NETWORK)
                    .put(PROSTHODONTICS_FOURTH_YEAR_OUT_OF_NETWORK, PROSTHODONTICS_FOURTH_YEAR_IN_NETWORK)
                    .put(RADIOGRAPHS_FIRST_YEAR_OUT_OF_NETWORK, RADIOGRAPHS_FIRST_YEAR_IN_NETWORK)
                    .put(RADIOGRAPHS_SECOND_YEAR_OUT_OF_NETWORK, RADIOGRAPHS_SECOND_YEAR_IN_NETWORK)
                    .put(RADIOGRAPHS_THIRD_YEAR_OUT_OF_NETWORK, RADIOGRAPHS_THIRD_YEAR_IN_NETWORK)
                    .put(RADIOGRAPHS_FOURTH_YEAR_OUT_OF_NETWORK, RADIOGRAPHS_FOURTH_YEAR_IN_NETWORK)
                    .build();


    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13828", component = POLICY_GROUPBENEFITS)
    public void testQuoteRulesVerificationSitusState_TX_LA_GA_MS() {
        preconditions(tdSpecific().getTestData("TestData_SitusStateTX"), getRandomElementFromList(ImmutableList.of("TX", "LA", "GA", "MS")));
        AssetList coInsuranceAssetList = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CO_INSURANCE);

        LOGGER.info("Step 1");
        setFieldsAndCheckErrorMessage(coInsuranceAssetList, "10%", CO_INSURANCE_PAIRS, ERROR_MESSAGE_TEMPLATE_MUST_BE_EQUAL);

        LOGGER.info("Step 9");
        planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(PlanDefinitionTabMetaData.CoInsuranceMetaData.IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_NO);
        setValuesToFieldsAndCheckNoWarning(coInsuranceAssetList, "15%", "15%");

        LOGGER.info("Step 11");
        setFieldsAndCheckErrorMessage(
                coInsuranceAssetList, "20%",
                ImmutableMap.of(
                        PROSTHODONTICS_OUT_OF_NETWORK, PROSTHODONTICS_IN_NETWORK,
                        RADIOGRAPHS_OUT_OF_NETWORK, RADIOGRAPHS_IN_NETWORK),
                ERROR_MESSAGE_TEMPLATE_MUST_BE_EQUAL_WITHOUT_POINT);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13831", component = POLICY_GROUPBENEFITS)
    public void testQuoteRulesVerificationSitusState_ME_MD_MA() {
        preconditions(tdSpecific().getTestData("TestData_SitusStateME"), getRandomElementFromList(ImmutableList.of("ME", "MD", "MA")));
        AssetList coInsuranceAssetList = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CO_INSURANCE);

        LOGGER.info("Step 1");
        setFieldsAndCheckErrorMessage(coInsuranceAssetList, "5%", CO_INSURANCE_PAIRS, ERROR_MESSAGE_TEMPLATE_MUST_BE_WITHIN_20);

        LOGGER.info("Step 16");
        planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(PlanDefinitionTabMetaData.CoInsuranceMetaData.IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_NO);
        setValuesToFieldsAndCheckNoWarning(coInsuranceAssetList, "30%", "15%");

        LOGGER.info("Step 21");
        setFieldsAndCheckErrorMessage(
                coInsuranceAssetList, "5%",
                ImmutableMap.of(
                        PROSTHODONTICS_OUT_OF_NETWORK, PROSTHODONTICS_IN_NETWORK,
                        RADIOGRAPHS_OUT_OF_NETWORK, RADIOGRAPHS_IN_NETWORK),
                ERROR_MESSAGE_TEMPLATE_MUST_BE_WITHIN_20);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13883", component = POLICY_GROUPBENEFITS)
    public void testQuoteRulesVerificationSitusStateKY() {
        preconditions(tdSpecific().getTestData("TestData_SitusStateKY"), "KY");
        AssetList coInsuranceAssetList = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CO_INSURANCE);

        LOGGER.info("Step 1");
        setFieldsAndCheckErrorMessage(coInsuranceAssetList, "20%", CO_INSURANCE_PAIRS, ERROR_MESSAGE_TEMPLATE_MUST_BE_WITHIN_25);

        LOGGER.info("Step 16");
        planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(PlanDefinitionTabMetaData.CoInsuranceMetaData.IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_NO);
        setValuesToFieldsAndCheckNoWarning(coInsuranceAssetList, "50%", "25%");

        LOGGER.info("Step 21");
        setFieldsAndCheckErrorMessage(
                coInsuranceAssetList, "5%",
                ImmutableMap.of(
                        PROSTHODONTICS_OUT_OF_NETWORK, PROSTHODONTICS_IN_NETWORK,
                        RADIOGRAPHS_OUT_OF_NETWORK, RADIOGRAPHS_IN_NETWORK),
                ERROR_MESSAGE_TEMPLATE_MUST_BE_WITHIN_25);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13884", component = POLICY_GROUPBENEFITS)
    public void testQuoteRulesVerificationSitusState_NV_NC_OK_NM() {
        preconditions(tdSpecific().getTestData("TestData_SitusStateNV"), getRandomElementFromList(ImmutableList.of("NV", "NC", "OK", "NM")));
        AssetList coInsuranceAssetList = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CO_INSURANCE);

        LOGGER.info("Step 1");
        setFieldsAndCheckErrorMessage(coInsuranceAssetList, "15%", CO_INSURANCE_PAIRS, ERROR_MESSAGE_MUST_BE_WITHIN_30);

        LOGGER.info("Step 9");
        policyInformationTab.navigateToTab();
        policyInformationTab.getAssetList().getAsset(SITUS_STATE).waitForAccessible(2000);
        policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("NV");
        planDefinitionTab.navigateToTab();
        planDefinitionTab.selectDefaultPlan();

        LOGGER.info("Step 16");
        setValueForPpoEpoPlanAndCheckDependentFields(coInsuranceAssetList);

        LOGGER.info("Step 17");
        setValuesToFieldsAndCheckNoWarning(coInsuranceAssetList, "100%", "80%");

        LOGGER.info("Step 21");
        setFieldsAndCheckErrorMessage(
                coInsuranceAssetList, "65%",
                ImmutableMap.of(
                        PROSTHODONTICS_OUT_OF_NETWORK, PROSTHODONTICS_IN_NETWORK,
                        RADIOGRAPHS_OUT_OF_NETWORK, RADIOGRAPHS_IN_NETWORK),
                ERROR_MESSAGE_MUST_BE_WITHIN_30);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13885", component = POLICY_GROUPBENEFITS)
    public void testQuoteRulesVerificationSitusState_CT_IL_NJ_NY() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        TestData tdDataGatherTestData = getDefaultDNMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_SitusStateCT").resolveLinks())
                .adjust(TestData.makeKeyPath(policyInformationTab.getClass().getSimpleName(), TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel()), "51")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), getRandomElementFromList(ImmutableList.of("CT", "IL", "NJ", "WA")));
        groupDentalMasterPolicy.initiate(tdDataGatherTestData);
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(tdDataGatherTestData, PlanDefinitionTab.class, true);
        AssetList coInsuranceAssetList = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CO_INSURANCE);

        LOGGER.info("Step 1");
        setFieldsAndCheckErrorMessage(coInsuranceAssetList, "0%", CO_INSURANCE_PAIRS, ERROR_MESSAGE_TEMPLATE_BOTH_MUST_BE_GREATER_THAN_0);

        LOGGER.info("Step 12");
        coInsuranceAssetList.getAsset(IS_IT_GRADED_CO_INSURANCE).setValue("No");
        ImmutableList.of(PROSTHODONTICS_IN_NETWORK, PROSTHODONTICS_OUT_OF_NETWORK, RADIOGRAPHS_IN_NETWORK, RADIOGRAPHS_OUT_OF_NETWORK).forEach(control ->
                coInsuranceAssetList.getAsset(control).setValue("90%"));
        Tab.buttonNext.click();
        assertSoftly(softly -> ImmutableList.of(PROSTHODONTICS_IN_NETWORK, PROSTHODONTICS_OUT_OF_NETWORK, RADIOGRAPHS_IN_NETWORK, RADIOGRAPHS_OUT_OF_NETWORK).forEach(control -> {
            softly.assertThat(coInsuranceAssetList.getAsset(control)).hasNoWarning();
        }));
    }

    private void preconditions(TestData testDataForAdjust, String situsState) {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        TestData tdDataGatherTestData = getDefaultDNMasterPolicyData().adjust(testDataForAdjust.resolveLinks())
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), situsState);
        groupDentalMasterPolicy.initiate(tdDataGatherTestData);
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(tdDataGatherTestData, PlanDefinitionTab.class, true);

    }

    private void setValueForPpoEpoPlanAndCheckDependentFields(AssetList coInsuranceAssetList) {
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PPO_EPO_PLAN).setValue(VALUE_YES);
        assertSoftly(softly -> {
            softly.assertThat(coInsuranceAssetList.getAsset(IS_IT_GRADED_CO_INSURANCE)).isDisabled().hasValue(VALUE_NO);
            softly.assertThat(coInsuranceAssetList.getAsset(NUMBER_OF_GRADED_YEARS)).isAbsent();
        });
    }

    private void setFieldsAndCheckErrorMessage(AssetList coInsuranceAssetList, String setValue,
                                               ImmutableMap<AssetDescriptor<ComboBox>, AssetDescriptor<ComboBox>> coInsurancePairs,
                                               String errorMessageTemplate) {
        assertSoftly(softly ->
                // Check error message for each control with name (xxx - Out of Network).
                // (value) uses for check error message template, ex. "State Requirement: (key) must be equal to (value)."
                coInsurancePairs.forEach((key, value) -> {
                    coInsuranceAssetList.getAsset(key).setValue(setValue);
                    // For each entry (xxx - In Network, xxx - Out of Network) compare error message with error message template
                    softly.assertThat(coInsuranceAssetList.getAsset(key).getWarning().orElse("")).contains(String.format(errorMessageTemplate, key.getLabel(), value.getLabel()));
                }));
    }

    private void setValuesToFieldsAndCheckNoWarning(AssetList coInsuranceAssetList, String valueForInNetworkField, String valueForOutOfNetworkField) {
        ImmutableList.of(PROSTHODONTICS_IN_NETWORK, RADIOGRAPHS_IN_NETWORK).forEach(asset ->
                coInsuranceAssetList.getAsset(asset).setValue(valueForInNetworkField));
        assertSoftly(softly ->
                ImmutableList.of(PROSTHODONTICS_OUT_OF_NETWORK, RADIOGRAPHS_OUT_OF_NETWORK).forEach(asset -> {
                    coInsuranceAssetList.getAsset(asset).setValue(valueForOutOfNetworkField);
                    softly.assertThat(coInsuranceAssetList.getAsset(asset)).hasNoWarning();
                }));
    }
}