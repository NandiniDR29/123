package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
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
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalDeductibleMetaData.DEDUCTIBLE_EXPENSE_PERIOD;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalMaximumMetaData.MAXIMUM_EXPENSE_PERIOD;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteConfigureDentalDeductibleExpensePeriod extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private final static String DEDUCTIBLE_EXPENSE_PERIOD_WARNING = "Deductible Expense Period and Maximum Expense Period must match.";
    private final static String CALENDAR_YEAR = "Calendar Year";
    private final static String BENEFIT_YEAR = "Benefit Year";

    private static AssetList assetListDeductible;

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13805", component = POLICY_GROUPBENEFITS)
    public void testConfigureDentalDeductibleExpensePeriod() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), PremiumSummaryTab.class);
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());

        assetListDeductible = planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE);
        planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);

        planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(MAXIMUM_EXPENSE_PERIOD).setValue(BENEFIT_YEAR);
        assetListDeductible.getAsset(DEDUCTIBLE_EXPENSE_PERIOD).setValue(BENEFIT_YEAR);

        assertSoftly(softly -> {

            LOGGER.info("---=={Step 1-2}==---");
            assetListDeductible.getAsset(DEDUCTIBLE_EXPENSE_PERIOD).setValue(CALENDAR_YEAR);

            LOGGER.info("---=={Step 3}==---");
            Tab.buttonNext.click();
            softly.assertThat(assetListDeductible.getAsset(DEDUCTIBLE_EXPENSE_PERIOD)).hasWarningWithText(DEDUCTIBLE_EXPENSE_PERIOD_WARNING);

            LOGGER.info("---=={Step 4}==---");
            premiumSummaryTab.navigate();
            premiumSummaryTab.rate();
            softly.assertThat(ErrorPage.tableError).hasMatchingRows(ErrorPage.TableError.MESSAGE.getName(), DEDUCTIBLE_EXPENSE_PERIOD_WARNING);
            Tab.buttonCancel.click();

            LOGGER.info("---=={Step 5}==---");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());

            LOGGER.info("---=={Step 6}==---");
            planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(MAXIMUM_EXPENSE_PERIOD).setValue(CALENDAR_YEAR);

            LOGGER.info("---=={Step 7-8}==---");
            premiumSummaryTab.navigate();
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        });
    }
}