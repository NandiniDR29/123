package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.DATA_GATHERING;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DENTAL_DEDUCTIBLE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalDeductibleMetaData;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalDeductibleMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteConfigureDentalDeductibleAffectsRating extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {


    private final static String BENEFIT_YEAR = "Benefit Year";

    private static AssetList assetListDeductible = planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13714", component = POLICY_GROUPBENEFITS)
    public void testConfigureDentalDeductibleExpensePeriodTC1() {

        TestData tdDataGatherTestData = getDefaultDNMasterPolicyData().adjust(tdSpecific().getTestData("TestData_TC1").resolveLinks());

        preconditions(tdDataGatherTestData);

        assertSoftly(softly -> {

            ImmutableMap<AssetDescriptor, Object> map = new ImmutableMap.Builder<AssetDescriptor, Object>()
                    .put(DEDUCTIBLE_EXPENSE_PERIOD, BENEFIT_YEAR)
                    .put(IS_IT_GRADED_DENTAL_DEDUCTIBLE, VALUE_NO)
                    .put(DentalDeductibleMetaData.NUMBER_OF_GRADED_YEARS, "2")
                    .put(DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, "$50")
                    .put(DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, "$50")
                    .put(DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, "$50")
                    .put(DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK, "$50")
                    .put(DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, "$50")
                    .put(DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK, "$50")
                    .put(DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, "$50")
                    .put(DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK, "$50")
                    .put(FAMILY_DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, "2X")
                    .put(FAMILY_DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, "2X")
                    .put(FAMILY_DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, "2X")
                    .put(FAMILY_DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK, "2X")
                    .put(FAMILY_DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, "2X")
                    .put(FAMILY_DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK, "2X")
                    .put(FAMILY_DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, "2X")
                    .put(FAMILY_DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK, "2X")
                    .build();

            map.forEach((assetDescriptor, value) -> verifyQuoteStatusAfterUpdateValues(assetDescriptor, value, softly));
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13714", component = POLICY_GROUPBENEFITS)
    public void testConfigureDentalDeductibleExpensePeriodTC2() {

        TestData tdDataGatherTestData = getDefaultDNMasterPolicyData().adjust(tdSpecific().getTestData("TestData_TC2").resolveLinks()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NV"));

        preconditions(tdDataGatherTestData);

        assertSoftly(softly -> {

            ImmutableMap<AssetDescriptor, Object> map = new ImmutableMap.Builder<AssetDescriptor, Object>()
                    .put(DEDUCTIBLE_IN_NETWORK, "$50")
                    .put(DEDUCTIBLE_OUT_OF_NETWORK, "$50")
                    .put(FAMILY_DEDUCTIBLE_IN_NETWORK, "2X")
                    .put(FAMILY_DEDUCTIBLE_OUT_OF_NETWORK, "2X")
                    .put(DEDUCTIBLE_EPO, "$50")
                    .put(FAMILY_DEDUCTIBLE_EPO, "2X")
                    .build();

            map.forEach((assetDescriptor, value) -> verifyQuoteStatusAfterUpdateValues(assetDescriptor, value, softly));
        });
    }

    private void preconditions(TestData td) {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createQuote(td);
    }

    private void verifyQuoteStatusAfterUpdateValues(AssetDescriptor assetDescriptor, Object value, ETCSCoreSoftAssertions softly) {

        groupDentalMasterPolicy.dataGather().start();
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        softly.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);

        assetListDeductible.getAsset(assetDescriptor).setValue(value);
        softly.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(DATA_GATHERING);
        Tab.buttonTopCancel.click();
        Page.dialogConfirmation.confirm();
    }
}