package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.PLAN_NAME_DUBLICATE_ERROR_MESSAGE;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.PLAN_NAME_ERROR_MESSAGE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuotePlanNameAndCoverageTiers extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private final static String TEST_PLAN_NAME = "testPlanName";
    private final static String COVERAGE_TIERS_IS_REQUIRED_ERROR_MESSAGE = "'Coverage Tiers' is required";
    private final static StaticElement COVERAGE_TIERS_ERROR_BLOCK = new StaticElement(By.xpath("//span[@id='policyDataGatherForm:sedit_GroupCoverageDefinitionTiersViewOnlyComponent_coverageTiers_error']"));

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13458", component = POLICY_GROUPBENEFITS)
    public void testQuotePlanNameAndCoverageTiers() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), PlanDefinitionTab.class);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).fill(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "SelectTwoPlans"));
        Row row = PlanDefinitionTab.planTable.getRow(1);
        row.getCell(6).controls.links.getFirst().click();

        TextBox planNameAsset = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN_NAME);
        // Asserts for REN-13458/#1
        assertSoftly(softly ->
                softly.assertThat(planNameAsset)
                        .isRequired().isEnabled().hasValue(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).getValue().get(0)));

        // Asserts for REN-13458/#2
        assertSoftly(softly -> {
            planNameAsset.setValue(RandomStringUtils.randomAlphabetic(10));
            Tab.buttonNext.click();
            softly.assertThat(planNameAsset).hasNoWarning();

            planNameAsset.setValue(RandomStringUtils.randomAlphabetic(256));
            softly.assertThat(planNameAsset.getValue()).hasSize(255);
            Tab.buttonNext.click();
            softly.assertThat(planNameAsset).hasNoWarning();

            planNameAsset.setValue("");
            Tab.buttonNext.click();
            softly.assertThat(planNameAsset.getWarning().orElse("")).contains(PLAN_NAME_ERROR_MESSAGE);

            checkErrorsAfterRateForPremiumSummaryTab(softly, PLAN_NAME_ERROR_MESSAGE);

            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            // Set same plan name for each plan from table
            PlanDefinitionTab.planTable.getRows().forEach(tableRow -> {
                tableRow.getCell(6).controls.links.getFirst().click();
                planNameAsset.setValue(TEST_PLAN_NAME);
            });
            checkErrorsAfterRateForPremiumSummaryTab(softly, PLAN_NAME_DUBLICATE_ERROR_MESSAGE);
        });

        AdvancedSelector coverageTiersAsset = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_TIERS);
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        // Asserts for REN-13458/#3
        assertSoftly(softly -> {
            removeAllValuesFromAdvancedSelectorAsset(coverageTiersAsset);
            Tab.buttonNext.click();
            softly.assertThat(COVERAGE_TIERS_ERROR_BLOCK).isPresent().hasValue((COVERAGE_TIERS_IS_REQUIRED_ERROR_MESSAGE));
            checkErrorsAfterRateForPremiumSummaryTab(softly, COVERAGE_TIERS_IS_REQUIRED_ERROR_MESSAGE);
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13458", component = POLICY_GROUPBENEFITS)
    public void testQuoteCoverageTiersDefaultValues() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NJ"), PlanDefinitionTab.class);
        selectFirstPlanFromDNMasterPolicyDefaultTestData();

        AdvancedSelector coverageTiersAsset = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_TIERS);
        // REN-13458/#4
        assertSoftly(softly -> {
            softly.assertThat(coverageTiersAsset).hasValue(ImmutableList.of("Employee Only", "Employee + Spouse", "Employee + Child(ren)", "Employee + Family"));

            coverageTiersAsset.buttonOpenPopup.click();
            coverageTiersAsset.buttonSearch.click();
            softly.assertThat(coverageTiersAsset.listboxAvailableItems.getAllValues()).isEqualTo(ImmutableList.of("Employee + 1", "Composite tier"));
        });

        coverageTiersAsset.buttonCancel.click();
        groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultDNMasterPolicyData(), planDefinitionTab.getClass(), premiumSummaryTab.getClass(), true);
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        // REN-13458/#5
        groupDentalMasterPolicy.dataGather().start();
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        planDefinitionTab.getAssetList().fill(tdSpecific().getTestData("TestDataUpdate"));
        Tab.buttonSaveAndExit.click();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        // REN-13458/#6
        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.acceptContract().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.issue().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.createEndorsement(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestDataUpdate").resolveLinks())
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.EXISTENT_BILLING_ACCOUNT).resolveLinks()));
        PolicySummaryPage.TransactionHistory.selectTransactionsToCompare("Endorsement", "Issue");
        PolicySummaryPage.TransactionHistoryDifferences.expandDifferencesTable();
        assertThat(PolicySummaryPage.TransactionHistoryDifferences.tableDifferences).hasRowsThatContain("Description", PlanDefinitionTabMetaData.PLAN_NAME.getLabel());
    }


    private void checkErrorsAfterRateForPremiumSummaryTab(ETCSCoreSoftAssertions softly, String errorMessage) {
        premiumSummaryTab.navigate();
        premiumSummaryTab.rate();

        softly.assertThat(ErrorPage.tableError)
                .as(String.format("Rows with error message: '%s' doesn't exist", errorMessage))
                .hasRowsThatContain(ImmutableMap.of(ErrorPage.TableError.MESSAGE.getName(), errorMessage));
        Tab.buttonCancel.click();
    }

    // TODO Edit after resolve EISDEV-206537
    private void removeAllValuesFromAdvancedSelectorAsset(AdvancedSelector asset) {
        asset.buttonOpenPopup.click();
        asset.removeValue(asset.listboxSelectedItems.getAllValues());
        asset.buttonSave.click();
        Page.dialogConfirmation.confirm();
    }
}
