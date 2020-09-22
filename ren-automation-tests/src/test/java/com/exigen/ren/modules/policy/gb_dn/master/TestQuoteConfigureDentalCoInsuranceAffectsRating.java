package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
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
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.CO_INSURANCE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.CoInsuranceMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteConfigureDentalCoInsuranceAffectsRating extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {


    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-12994", component = POLICY_GROUPBENEFITS)
    public void testConfigureDentalDeductibleExpensePeriodTC1() {

        TestData tdDataGatherTestData = getDefaultDNMasterPolicyData().adjust(tdSpecific().getTestData("TestData_TC1").resolveLinks());

        preconditions(tdDataGatherTestData);

        assertSoftly(softly -> {

            ImmutableMap<AssetDescriptor, Object> map = new ImmutableMap.Builder<AssetDescriptor, Object>()
                    .put(UC_PERCENTILE_LEVEL, "REN 70th")
                    .put(IS_IT_GRADED_CO_INSURANCE, VALUE_NO)
                    .put(NUMBER_OF_GRADED_YEARS, "2")
                    .put(PREVENTIVE_FIRST_YEAR_IN_NETWORK, "50%")
                    .put(PREVENTIVE_SECOND_YEAR_IN_NETWORK, "50%")
                    .put(PREVENTIVE_THIRD_YEAR_IN_NETWORK, "50%")
                    .put(PREVENTIVE_FOURTH_YEAR_IN_NETWORK, "50%")
                    .put(PREVENTIVE_FIRST_YEAR_OUT_OF_NETWORK, "50%")
                    .put(PREVENTIVE_SECOND_YEAR_OUT_OF_NETWORK, "50%")
                    .put(PREVENTIVE_THIRD_YEAR_OUT_OF_NETWORK, "50%")
                    .put(PREVENTIVE_FOURTH_YEAR_OUT_OF_NETWORK, "50%")
                    .build();

            map.forEach((assetDescriptor, value) -> verifyQuoteStatusAfterUpdateValues(assetDescriptor, value, softly));
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-12994", component = POLICY_GROUPBENEFITS)
    public void testConfigureDentalDeductibleExpensePeriodTC2() {

        TestData tdDataGatherTestData = getDefaultDNMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_TC2")
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NV").resolveLinks());

        preconditions(tdDataGatherTestData);

        assertSoftly(softly -> {

            ImmutableMap<AssetDescriptor, Object> map = new ImmutableMap.Builder<AssetDescriptor, Object>()
                    .put(PREVENTIVE_IN_NETWORK, "50%")
                    .put(PREVENTIVE_IN_NETWORK_EPO, "50%")
                    .put(PREVENTIVE_OUT_OF_NETWORK, "50%")
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

        planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(assetDescriptor).setValue(value);
        softly.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(DATA_GATHERING);
        Tab.buttonTopCancel.click();
        Page.dialogConfirmation.confirm();
    }
}