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
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DENTAL_MAXIMUM;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalMaximumMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.PPO_EPO_PLAN;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteConfigureDentalMaximumAffectsRating extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private final static String NUMBER_OF_GRADED_YEARS_2 = "2";
    private final static String PLAN_MAXIMUM_900 = "$900";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-12417", component = POLICY_GROUPBENEFITS)
    public void testConfigureDentalMaximumAffectsRatingTC1() {

        TestData tdDataGatherTestData = getDefaultDNMasterPolicyData().adjust(tdSpecific().getTestData("TestData_TC1").resolveLinks());

        preconditions(tdDataGatherTestData);

        assertSoftly(softly -> {

            ImmutableMap<AssetDescriptor, Object> map = new ImmutableMap.Builder<AssetDescriptor, Object>()
                    .put(IS_IT_GRADED_DENTAL_MAXIMUM, VALUE_NO)
                    .put(NUMBER_OF_GRADED_YEARS, NUMBER_OF_GRADED_YEARS_2)
                    .put(PLAN_MAXIMUM_FIRST_YEAR_IN_NETWORK, PLAN_MAXIMUM_900)
                    .put(PLAN_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_900)
                    .put(PLAN_MAXIMUM_SECOND_YEAR_IN_NETWORK, PLAN_MAXIMUM_900)
                    .put(PLAN_MAXIMUM_SECOND_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_900)
                    .put(PLAN_MAXIMUM_THIRD_YEAR_IN_NETWORK, PLAN_MAXIMUM_900)
                    .put(PLAN_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_900)
                    .put(PLAN_MAXIMUM_FOURTH_YEAR_IN_NETWORK, PLAN_MAXIMUM_900)
                    .put(PLAN_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_900)
                    .put(MAXIMUM_EXTENDER, true)
                    .build();

            map.forEach((assetDescriptor, value) -> verifyQuoteStatusAfterUpdateValues(assetDescriptor, value, DATA_GATHERING, softly));
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-12417", component = POLICY_GROUPBENEFITS)
    public void testConfigureDentalMaximumAffectsRatingTC2() {

        TestData tdDataGatherTestData = getDefaultDNMasterPolicyData().adjust(tdSpecific().getTestData("TestData_TC2").resolveLinks());

        preconditions(tdDataGatherTestData);

        assertSoftly(softly -> {

            ImmutableMap<AssetDescriptor, Object> map = new ImmutableMap.Builder<AssetDescriptor, Object>()
                    .put(ROLL_OVER_THRESHOLD, PLAN_MAXIMUM_900)
                    .put(ROLL_OVER_BENEFIT, PLAN_MAXIMUM_900)
                    .put(ROLL_OVER_BENEFIT_LIMIT, PLAN_MAXIMUM_900)
                    .build();

            map.forEach((assetDescriptor, value) -> verifyQuoteStatusAfterUpdateValues(assetDescriptor, value, PREMIUM_CALCULATED, softly));

            verifyQuoteStatusAfterUpdateValues(MAXIMUM_ROLL_OVER, VALUE_NO, DATA_GATHERING, softly);
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-12417", component = POLICY_GROUPBENEFITS)
    public void testConfigureDentalMaximumAffectsRatingTC3() {

        TestData tdDataGatherTestData = getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName()+"[1]", PPO_EPO_PLAN.getLabel()), "Yes")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName()+"[1]", PLAN_MAX_EPO.getLabel()), "$1,000")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NV");

        preconditions(tdDataGatherTestData);

        assertSoftly(softly -> verifyQuoteStatusAfterUpdateValues(PLAN_MAX_EPO, "$2,000", DATA_GATHERING, softly));
    }

    private void preconditions(TestData td) {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createQuote(td);
    }

    private void verifyQuoteStatusAfterUpdateValues(AssetDescriptor assetDescriptor, Object value, String expectedStatus, ETCSCoreSoftAssertions softly) {

        groupDentalMasterPolicy.dataGather().start();
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        softly.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);

        planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(assetDescriptor).setValue(value);
        softly.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(expectedStatus);
        Tab.buttonTopCancel.click();
        Page.dialogConfirmation.confirm();
    }
}