package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.ERROR_MESSAGE_MUST_BE_WITHIN_30;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.CoInsuranceMetaData.*;
import static com.exigen.ren.utils.CommonMethods.getRandomElementFromList;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteBasicMajorInOutNetwork extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    // Create map with (xxx - Out of Network, xxx - In Network) controls
    // key: (xxx - Out of Network)
    // value: (xxx - In of Network)
    private static final ImmutableMap<AssetDescriptor<ComboBox>, AssetDescriptor<ComboBox>> CO_INSURANCE_PAIRS =
            ImmutableMap.<AssetDescriptor<ComboBox>, AssetDescriptor<ComboBox>>builder()
                    .put(BASIC_FIRST_YEAR_OUT_OF_NETWORK, BASIC_FIRST_YEAR_IN_NETWORK)
                    .put(BASIC_SECOND_YEAR_OUT_OF_NETWORK, BASIC_SECOND_YEAR_IN_NETWORK)
                    .put(BASIC_THIRD_YEAR_OUT_OF_NETWORK, BASIC_THIRD_YEAR_IN_NETWORK)
                    .put(BASIC_FOURTH_YEAR_OUT_OF_NETWORK, BASIC_FOURTH_YEAR_IN_NETWORK)
                    .put(MAJOR_FIRST_YEAR_OUT_OF_NETWORK, MAJOR_FIRST_YEAR_IN_NETWORK)
                    .put(MAJOR_SECOND_YEAR_OUT_OF_NETWORK, MAJOR_SECOND_YEAR_IN_NETWORK)
                    .put(MAJOR_THIRD_YEAR_OUT_OF_NETWORK, MAJOR_THIRD_YEAR_IN_NETWORK)
                    .put(MAJOR_FOURTH_YEAR_OUT_OF_NETWORK, MAJOR_FOURTH_YEAR_IN_NETWORK)
                    .build();

    private static final String ERROR_MESSAGE_TEMPLATE_MUST_BE_EQUAL = "State Requirement: %s must be equal to %s.";
    private static final String ERROR_MESSAGE_TEMPLATE_MUST_BE_WITHIN_20 = "State Requirement: %s must be within 20%% of %s.";
    private static final String ERROR_MESSAGE_TEMPLATE_MUST_BE_WITHIN_25 = "State Requirement: %s must be within 25%% of %s.";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13200", component = POLICY_GROUPBENEFITS)
    public void testQuoteBasicMajorInOutNetwork_TX() {
        preconditions(tdSpecific().getTestData("TestData_With_SitusState"), getRandomElementFromList(ImmutableList.of("TX", "LA", "GA", "MS")));
        AssetList coInsuranceAssetList = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CO_INSURANCE);

        LOGGER.info("Step 1");
        setFieldsAndCheckErrorMessage(coInsuranceAssetList, "50%", CO_INSURANCE_PAIRS, ERROR_MESSAGE_TEMPLATE_MUST_BE_EQUAL);

        LOGGER.info("Step 3");
        checkValidationErrorsOnPremiumSummaryTab(CO_INSURANCE_PAIRS, ERROR_MESSAGE_TEMPLATE_MUST_BE_EQUAL);

        LOGGER.info("Step 12");
        assertSoftly(softly ->
                CO_INSURANCE_PAIRS.keySet().forEach((key) -> {
                    coInsuranceAssetList.getAsset(key).setValue("60%");
                    softly.assertThat(coInsuranceAssetList.getAsset(key)).hasNoWarning();
                }));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13234", component = POLICY_GROUPBENEFITS)
    public void testQuoteBasicMajorInOutNetwork_ME() {
        preconditions(tdSpecific().getTestData("TestData_With_SitusState"), getRandomElementFromList(ImmutableList.of("ME" ,"MD" ,"MA")));
        AssetList coInsuranceAssetList = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CO_INSURANCE);

        LOGGER.info("Step 1");
        setFieldsAndCheckErrorMessage(coInsuranceAssetList, "20%", CO_INSURANCE_PAIRS, ERROR_MESSAGE_TEMPLATE_MUST_BE_WITHIN_20);

        LOGGER.info("Step 3");
        checkValidationErrorsOnPremiumSummaryTab(CO_INSURANCE_PAIRS, ERROR_MESSAGE_TEMPLATE_MUST_BE_WITHIN_20);

        LOGGER.info("Step 12");
        assertSoftly(softly ->
                CO_INSURANCE_PAIRS.keySet().forEach((key) -> {
                    coInsuranceAssetList.getAsset(key).setValue("60%");
                    softly.assertThat(coInsuranceAssetList.getAsset(key)).hasNoWarning();
                    coInsuranceAssetList.getAsset(key).setValue("40%");
                    softly.assertThat(coInsuranceAssetList.getAsset(key)).hasNoWarning();
                }));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13286", component = POLICY_GROUPBENEFITS)
    public void testQuoteBasicMajorInOutNetwork_KY() {
        preconditions(tdSpecific().getTestData("TestData_With_SitusState"), "KY");
        AssetList coInsuranceAssetList = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CO_INSURANCE);

        LOGGER.info("Step 1");
        setFieldsAndCheckErrorMessage(coInsuranceAssetList, "20%", CO_INSURANCE_PAIRS, ERROR_MESSAGE_TEMPLATE_MUST_BE_WITHIN_25);

        LOGGER.info("Step 3");
        checkValidationErrorsOnPremiumSummaryTab(CO_INSURANCE_PAIRS, ERROR_MESSAGE_TEMPLATE_MUST_BE_WITHIN_25);

        LOGGER.info("Step 12");
        assertSoftly(softly ->
                CO_INSURANCE_PAIRS.keySet().forEach((key) -> {
                    coInsuranceAssetList.getAsset(key).setValue("60%");
                    softly.assertThat(coInsuranceAssetList.getAsset(key)).hasNoWarning();
                    coInsuranceAssetList.getAsset(key).setValue("40%");
                    softly.assertThat(coInsuranceAssetList.getAsset(key)).hasNoWarning();
                }));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13287", component = POLICY_GROUPBENEFITS)
    public void testQuoteBasicMajorInOutNetwork_NV() {
        preconditions(tdSpecific().getTestData("TestData_With_SitusState"), getRandomElementFromList(ImmutableList.of("NV", "NC", "OK", "NM")));
        AssetList coInsuranceAssetList = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CO_INSURANCE);

        LOGGER.info("Step 1");
        setFieldsAndCheckErrorMessage(coInsuranceAssetList, "20%", CO_INSURANCE_PAIRS, ERROR_MESSAGE_MUST_BE_WITHIN_30);

        LOGGER.info("Step 3");
        checkValidationErrorsOnPremiumSummaryTab(CO_INSURANCE_PAIRS, ERROR_MESSAGE_MUST_BE_WITHIN_30);

        LOGGER.info("Step 12");
        assertSoftly(softly ->
                CO_INSURANCE_PAIRS.keySet().forEach((key) -> {
                    coInsuranceAssetList.getAsset(key).setValue("60%");
                    softly.assertThat(coInsuranceAssetList.getAsset(key)).hasNoWarning();
                }));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-12996", component = POLICY_GROUPBENEFITS)
    public void testQuoteInOutEPONetwork() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        TestData adjustedTestData = getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NV");
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(adjustedTestData, PlanDefinitionTab.class);
        selectFirstPlanFromDNMasterPolicyDefaultTestData();
        AssetList coInsuranceAssetList = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CO_INSURANCE);

        LOGGER.info("Step 1");
        assertSoftly(softly -> {
            coInsuranceAssetList.getAsset(IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_YES);
            ImmutableList.of(BASIC_IN_NETWORK, BASIC_OUT_OF_NETWORK).forEach(asset ->
                    softly.assertThat(coInsuranceAssetList.getAsset(asset)).isAbsent());
            coInsuranceAssetList.getAsset(IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_NO);
            ImmutableList.of(BASIC_IN_NETWORK, BASIC_OUT_OF_NETWORK).forEach(asset ->
                    softly.assertThat(coInsuranceAssetList.getAsset(asset)).isPresent());
        });

        LOGGER.info("Step 3");
        assertSoftly(softly -> {
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PPO_EPO_PLAN).setValue(VALUE_YES);
            ImmutableList.of(BASIC_IN_NETWORK_EPO, MAJOR_IN_NETWORK_EPO).forEach(asset ->
                    softly.assertThat(coInsuranceAssetList.getAsset(asset))
                            .isPresent().isRequired().containsAllOptions(getRangeListWithPercent(0, 100, 5)));
            softly.assertThat(coInsuranceAssetList.getAsset(BASIC_IN_NETWORK_EPO)).hasValue("100%");
            softly.assertThat(coInsuranceAssetList.getAsset(MAJOR_IN_NETWORK_EPO)).hasValue("80%");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PPO_EPO_PLAN).setValue(VALUE_NO);
            ImmutableList.of(BASIC_IN_NETWORK_EPO, MAJOR_IN_NETWORK_EPO).forEach(asset ->
                    softly.assertThat(coInsuranceAssetList.getAsset(asset)).isAbsent());
        });

        LOGGER.info("Step 2");
        groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(adjustedTestData, PlanDefinitionTab.class, PremiumSummaryTab.class);
        Tab.buttonSaveAndExit.click();
        assertSoftly(softly ->
                ImmutableList.of(BASIC_IN_NETWORK, BASIC_OUT_OF_NETWORK).forEach(asset -> {
                    groupDentalMasterPolicy.calculatePremium();
                    groupDentalMasterPolicy.dataGather().start();
                    NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
                    coInsuranceAssetList.getAsset(asset).setValue("75%");
                    Tab.buttonSaveAndExit.click();
                    softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);
        }));

        LOGGER.info("Step 4");
        assertSoftly(softly ->
                ImmutableList.of(BASIC_IN_NETWORK_EPO, MAJOR_IN_NETWORK_EPO).forEach(asset -> {
                    groupDentalMasterPolicy.calculatePremium();
                    groupDentalMasterPolicy.dataGather().start();
                    NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
                    planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PPO_EPO_PLAN).setValue(VALUE_YES);
                    softly.assertThat(coInsuranceAssetList.getAsset(asset)).isPresent();
                    coInsuranceAssetList.getAsset(asset).setValue("75%");
                    Tab.buttonSaveAndExit.click();
                    softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);
        }));

        LOGGER.info("Step 5");
        groupDentalMasterPolicy.calculatePremium();
        groupDentalMasterPolicy.propose().perform(adjustedTestData);
        groupDentalMasterPolicy.acceptContract().perform(adjustedTestData);
        groupDentalMasterPolicy.issue().perform(adjustedTestData);
        groupDentalMasterPolicy.createEndorsement(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Endorsement_InNetworkEPO").resolveLinks())
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.EXISTENT_BILLING_ACCOUNT).resolveLinks()));
        PolicySummaryPage.TransactionHistory.selectTransactionsToCompare("Endorsement", "Issue");
        PolicySummaryPage.TransactionHistoryDifferences.expandDifferencesTable();
        assertSoftly(softly ->
                ImmutableList.of(BASIC_IN_NETWORK_EPO, MAJOR_IN_NETWORK_EPO).forEach(asset ->
                        softly.assertThat(PolicySummaryPage.TransactionHistoryDifferences.tableDifferences).hasRowsThatContain("Description", asset.getLabel())));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-12996", component = POLICY_GROUPBENEFITS)
    public void testQuoteYearInOutNetwork() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData().adjust(tdSpecific().getTestData("TestData_With_SitusState").resolveLinks()));
        AssetList coInsuranceAssetList = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CO_INSURANCE);
        ImmutableList<AssetDescriptor<ComboBox>> yearInOutList = ImmutableList.of(
                BASIC_FIRST_YEAR_OUT_OF_NETWORK, BASIC_FIRST_YEAR_IN_NETWORK,
                BASIC_SECOND_YEAR_OUT_OF_NETWORK, BASIC_SECOND_YEAR_IN_NETWORK,
                BASIC_THIRD_YEAR_OUT_OF_NETWORK, BASIC_THIRD_YEAR_IN_NETWORK,
                BASIC_FOURTH_YEAR_OUT_OF_NETWORK, BASIC_FOURTH_YEAR_IN_NETWORK,
                MAJOR_FIRST_YEAR_OUT_OF_NETWORK, MAJOR_FIRST_YEAR_IN_NETWORK,
                MAJOR_SECOND_YEAR_OUT_OF_NETWORK, MAJOR_SECOND_YEAR_IN_NETWORK,
                MAJOR_THIRD_YEAR_OUT_OF_NETWORK, MAJOR_THIRD_YEAR_IN_NETWORK,
                MAJOR_FOURTH_YEAR_OUT_OF_NETWORK, MAJOR_FOURTH_YEAR_IN_NETWORK);

        LOGGER.info("Step 7");
        assertSoftly(softly ->
                yearInOutList.forEach(asset -> {
                    groupDentalMasterPolicy.dataGather().start();
                    NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
                    coInsuranceAssetList.getAsset(asset).setValue("100%");
                    Tab.buttonSaveAndExit.click();
                    softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);
                    groupDentalMasterPolicy.calculatePremium();
                }));

        LOGGER.info("Step 8, 9");
        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.acceptContract().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.issue().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.createEndorsement(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Endorsement_YearInOut").resolveLinks())
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.EXISTENT_BILLING_ACCOUNT).resolveLinks()));
        PolicySummaryPage.TransactionHistory.selectTransactionsToCompare("Endorsement", "Issue");
        PolicySummaryPage.TransactionHistoryDifferences.expandDifferencesTable();
        assertSoftly(softly ->
                yearInOutList.forEach(asset ->
                        softly.assertThat(PolicySummaryPage.TransactionHistoryDifferences.tableDifferences).hasRowsThatContain("Description", asset.getLabel())));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13180", component = POLICY_GROUPBENEFITS)
    public void testQuoteFirstSecondThirdFourthYearInOutNetwork() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), PlanDefinitionTab.class);
        selectFirstPlanFromDNMasterPolicyDefaultTestData();
        AssetList coInsuranceAssetList = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CO_INSURANCE);

        LOGGER.info("Step 1");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PPO_EPO_PLAN).setValue(VALUE_NO);
        coInsuranceAssetList.getAsset(IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_YES);

        checkCoInsuranceDisplayedAndHiddenControls("2",
                ImmutableList.of(
                        BASIC_FIRST_YEAR_OUT_OF_NETWORK, BASIC_FIRST_YEAR_IN_NETWORK,
                        BASIC_SECOND_YEAR_OUT_OF_NETWORK, BASIC_SECOND_YEAR_IN_NETWORK,
                        MAJOR_FIRST_YEAR_OUT_OF_NETWORK, MAJOR_FIRST_YEAR_IN_NETWORK,
                        MAJOR_SECOND_YEAR_OUT_OF_NETWORK, MAJOR_SECOND_YEAR_IN_NETWORK),
                ImmutableList.of(
                        BASIC_THIRD_YEAR_OUT_OF_NETWORK, BASIC_THIRD_YEAR_IN_NETWORK,
                        BASIC_FOURTH_YEAR_OUT_OF_NETWORK, BASIC_FOURTH_YEAR_IN_NETWORK,
                        MAJOR_THIRD_YEAR_OUT_OF_NETWORK, MAJOR_THIRD_YEAR_IN_NETWORK,
                        MAJOR_FOURTH_YEAR_OUT_OF_NETWORK, MAJOR_FOURTH_YEAR_IN_NETWORK));

        checkCoInsuranceDisplayedAndHiddenControls("3",
                ImmutableList.of(
                        BASIC_FIRST_YEAR_OUT_OF_NETWORK, BASIC_FIRST_YEAR_IN_NETWORK,
                        BASIC_SECOND_YEAR_OUT_OF_NETWORK, BASIC_SECOND_YEAR_IN_NETWORK,
                        BASIC_THIRD_YEAR_OUT_OF_NETWORK, BASIC_THIRD_YEAR_IN_NETWORK,
                        MAJOR_FIRST_YEAR_OUT_OF_NETWORK, MAJOR_FIRST_YEAR_IN_NETWORK,
                        MAJOR_SECOND_YEAR_OUT_OF_NETWORK, MAJOR_SECOND_YEAR_IN_NETWORK,
                        MAJOR_THIRD_YEAR_OUT_OF_NETWORK, MAJOR_THIRD_YEAR_IN_NETWORK),
                ImmutableList.of(
                        BASIC_FOURTH_YEAR_OUT_OF_NETWORK, BASIC_FOURTH_YEAR_IN_NETWORK,
                        MAJOR_FOURTH_YEAR_OUT_OF_NETWORK, MAJOR_FOURTH_YEAR_IN_NETWORK));

        checkCoInsuranceDisplayedAndHiddenControls("4",
                ImmutableList.of(
                        BASIC_FIRST_YEAR_OUT_OF_NETWORK, BASIC_FIRST_YEAR_IN_NETWORK,
                        BASIC_SECOND_YEAR_OUT_OF_NETWORK, BASIC_SECOND_YEAR_IN_NETWORK,
                        BASIC_THIRD_YEAR_OUT_OF_NETWORK, BASIC_THIRD_YEAR_IN_NETWORK,
                        BASIC_FOURTH_YEAR_OUT_OF_NETWORK, BASIC_FOURTH_YEAR_IN_NETWORK,
                        MAJOR_FIRST_YEAR_OUT_OF_NETWORK, MAJOR_FIRST_YEAR_IN_NETWORK,
                        MAJOR_SECOND_YEAR_OUT_OF_NETWORK, MAJOR_SECOND_YEAR_IN_NETWORK,
                        MAJOR_THIRD_YEAR_OUT_OF_NETWORK, MAJOR_THIRD_YEAR_IN_NETWORK,
                        MAJOR_FOURTH_YEAR_OUT_OF_NETWORK, MAJOR_FOURTH_YEAR_IN_NETWORK),
                ImmutableList.of());

        LOGGER.info("Step 4");
        assertSoftly(softy -> {
            softy.assertThat(coInsuranceAssetList.getAsset(MAJOR_IN_NETWORK)).isAbsent();
            softy.assertThat(coInsuranceAssetList.getAsset(MAJOR_OUT_OF_NETWORK)).isAbsent();
            coInsuranceAssetList.getAsset(IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_NO);
            softy.assertThat(coInsuranceAssetList.getAsset(MAJOR_IN_NETWORK)).isPresent().isRequired();
            softy.assertThat(coInsuranceAssetList.getAsset(MAJOR_OUT_OF_NETWORK)).isPresent().isRequired();
        });

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

    private void setFieldsAndCheckErrorMessage(AssetList coInsuranceAssetList, String setValue,
                                               ImmutableMap<AssetDescriptor<ComboBox>, AssetDescriptor<ComboBox>> coInsurancePairs,
                                               String errorMessageTemplate) {
        assertSoftly(softly ->
                coInsurancePairs.forEach((key, value) -> {
                    // For each entry (xxx - Out of Network) compare that error no appears before set value
                    softly.assertThat(coInsuranceAssetList.getAsset(key)).hasNoWarning();
                    // Set value only for controls with name (xxx - Out of Network)
                    coInsuranceAssetList.getAsset(key).setValue(setValue);
                    // For each entry (xxx - In Network, xxx - Out of Network) compare error message with error message template
                    // Check error message for each control with name (xxx - Out of Network).
                    // (value) uses for check error message template, ex. "State Requirement: (key) must be equal to (value)."
                    softly.assertThat(coInsuranceAssetList.getAsset(key).getWarning().orElse(""))
                            .isEqualTo(String.format(errorMessageTemplate, key.getLabel(), value.getLabel()));
                }));
    }

    private void checkValidationErrorsOnPremiumSummaryTab(ImmutableMap<AssetDescriptor<ComboBox>, AssetDescriptor<ComboBox>> coInsurancePairs,
                                                          String errorMessageTemplate) {
        premiumSummaryTab.navigate();
        premiumSummaryTab.rate();
        assertSoftly(softly ->
                coInsurancePairs.forEach((key, value) -> {

                    // For each entry (xxx - In Network, xxx - Out of Network) compare error message with error message template
                    // Find row with error message that equal to message template
                    softly.assertThat(ErrorPage.tableError).hasMatchingRows(
                            ErrorPage.TableError.MESSAGE.getName(), String.format(errorMessageTemplate, key.getLabel(), value.getLabel()));
                }));
        Tab.buttonCancel.click();
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
    }

    private List<String> getRangeListWithPercent(int start, int stop, int step) {
        LinkedList<String> list = new LinkedList<>();
        for (int i=start; i<=stop; i=i+step) {
            list.add(i + "%");
        }
        return list;
    }

    private void checkCoInsuranceDisplayedAndHiddenControls(String numberOfGradedYears,
                                                            ImmutableList<AssetDescriptor<ComboBox>> displayedList,
                                                            ImmutableList<AssetDescriptor<ComboBox>> hiddenList) {
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CO_INSURANCE).getAsset(NUMBER_OF_GRADED_YEARS).setValue(numberOfGradedYears);
        assertSoftly(softly -> {
            displayedList.forEach(asset ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CO_INSURANCE).getAsset(asset)).isPresent().isRequired());
            hiddenList.forEach(asset ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CO_INSURANCE).getAsset(asset)).isAbsent());
        });
    }
}


