package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalDeductibleMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalMaximumMetaData.MAXIMUM_EXPENSE_PERIOD;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteConfigureDentalDeductibleAttributesPart1 extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private final static String NUMBER_OF_GRADED_YEARS_2 = "2";
    private final static String NUMBER_OF_GRADED_YEARS_3 = "3";
    private final static String NUMBER_OF_GRADED_YEARS_4 = "4";
    private static AssetList assetListDeductible;

    private static final ImmutableList<String> CALENDAR_LIST_VALUES = ImmutableList.of("Calendar Year", "Benefit Year");
    private static final ImmutableList<String> NUMBER_OF_GRADED_YEARS_VALUES = ImmutableList.of("2", "3", "4");
    private static final ImmutableList<AssetDescriptor<ComboBox>> DEDUCTIBLE_CONTROLS_LIST = ImmutableList.of(
            DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK,
            DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK, DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK);

    private static final ImmutableList<AssetDescriptor<ComboBox>> FAMILY_DEDUCTIBLE_CONTROLS_LIST = ImmutableList.of(
            FAMILY_DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, FAMILY_DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, FAMILY_DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, FAMILY_DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK,
            FAMILY_DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, FAMILY_DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, FAMILY_DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK, FAMILY_DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK);

    private static final ImmutableList<String> DEDUCTIBLE_LIST_VALUES = ImmutableList.of("$0", "$25", "$35", "$50", "$75", "$100", "$150", "$200", "$250", "$300");
    private static final ImmutableList<String> DEDUCTIBLE_LIST_VALUES_WITH_BLANK = ImmutableList.of("$0", "$25", "$35", "$50", "$75", "$100", "$150", "$200", "$250", "$300");
    private static final ImmutableList<String> FAMILY_DEDUCTIBLE_LIST_VALUES = ImmutableList.of("Unlimited", "1X", "2X", "3X");
    private static final ImmutableList<String> FAMILY_DEDUCTIBLE_LIST_VALUES_WITH_BLANK = ImmutableList.of("Unlimited", "1X", "2X", "3X");

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13573", component = POLICY_GROUPBENEFITS)
    public void testConfigureDentalDeductibleAttributes() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NV"),
                PlanDefinitionTab.class);
        selectFirstPlanFromDNMasterPolicyDefaultTestData();
        planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);
        assetListDeductible = planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE);

        assertSoftly(softly -> {
            LOGGER.info("---=={Step 1}==---");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(MAXIMUM_EXPENSE_PERIOD)).hasOptions(CALENDAR_LIST_VALUES);

            LOGGER.info("---=={Step 2}==---");
            softly.assertThat(assetListDeductible.getAsset(DEDUCTIBLE_EXPENSE_PERIOD)).hasOptions(CALENDAR_LIST_VALUES);

            LOGGER.info("---=={Step 3}==---");
            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_YES);
            softly.assertThat(assetListDeductible.getAsset(IS_IT_GRADED_DENTAL_DEDUCTIBLE)).isDisabled().hasValue(VALUE_NO);

            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);
            softly.assertThat(assetListDeductible.getAsset(IS_IT_GRADED_DENTAL_DEDUCTIBLE)).isEnabled().isRequired().hasValue(VALUE_NO);

            LOGGER.info("---=={Step 4}==---");
            assetListDeductible.getAsset(IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_YES);
            softly.assertThat(assetListDeductible.getAsset(NUMBER_OF_GRADED_YEARS)).isEnabled().isRequired().hasValue("3").hasOptions(NUMBER_OF_GRADED_YEARS_VALUES);

            assetListDeductible.getAsset(IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_NO);
            softly.assertThat(assetListDeductible.getAsset(NUMBER_OF_GRADED_YEARS)).isAbsent();

            LOGGER.info("---=={Step 5, 7}==---");
            assetListDeductible.getAsset(IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_YES);
            assetListDeductible.getAsset(NUMBER_OF_GRADED_YEARS).setValue(NUMBER_OF_GRADED_YEARS_4);

            LOGGER.info("Verify field for step 5");
            verifyDisplayedControls(DEDUCTIBLE_CONTROLS_LIST, softly);
            ImmutableList.of(DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK,
                    DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK).forEach(control ->
                    softly.assertThat(assetListDeductible.getAsset(control)).hasOptions(DEDUCTIBLE_LIST_VALUES));
            ImmutableList.of(DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK, DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK).forEach(control ->
                    softly.assertThat(assetListDeductible.getAsset(control)).hasOptions(DEDUCTIBLE_LIST_VALUES_WITH_BLANK));

            LOGGER.info("Verify field for step 7");
            verifyDisplayedControls(FAMILY_DEDUCTIBLE_CONTROLS_LIST, softly);
            ImmutableList.of(FAMILY_DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, FAMILY_DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, FAMILY_DEDUCTIBLE_SECOND_YEAR_IN_NETWORK,
                    FAMILY_DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK, FAMILY_DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, FAMILY_DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK).forEach(control ->
                    softly.assertThat(assetListDeductible.getAsset(control)).hasOptions(FAMILY_DEDUCTIBLE_LIST_VALUES));
            ImmutableList.of(FAMILY_DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK, FAMILY_DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK).forEach(control ->
                    softly.assertThat(assetListDeductible.getAsset(control)).hasOptions(FAMILY_DEDUCTIBLE_LIST_VALUES_WITH_BLANK));

            assetListDeductible.getAsset(IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_NO);

            LOGGER.info("Verify field for step 5");
            verifyNotDisplayedControls(DEDUCTIBLE_CONTROLS_LIST, softly);
            ImmutableList.of(DEDUCTIBLE_IN_NETWORK, DEDUCTIBLE_OUT_OF_NETWORK).forEach(control ->
                    softly.assertThat(assetListDeductible.getAsset(control)).isPresent().isEnabled().isRequired().hasOptions(DEDUCTIBLE_LIST_VALUES));

            LOGGER.info("Verify field for step 7");
            verifyNotDisplayedControls(FAMILY_DEDUCTIBLE_CONTROLS_LIST, softly);
            ImmutableList.of(FAMILY_DEDUCTIBLE_IN_NETWORK, FAMILY_DEDUCTIBLE_OUT_OF_NETWORK).forEach(control ->
                    softly.assertThat(assetListDeductible.getAsset(control)).isPresent().isRequired());

            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_YES);

            LOGGER.info("Verify field for step 5");
            softly.assertThat(assetListDeductible.getAsset(DEDUCTIBLE_EPO)).isEnabled().isRequired().hasValue("$50").hasOptions(DEDUCTIBLE_LIST_VALUES_WITH_BLANK);
            LOGGER.info("Verify field for step 7");
            softly.assertThat(assetListDeductible.getAsset(FAMILY_DEDUCTIBLE_EPO)).isEnabled().isRequired().hasValue("3X").hasOptions(FAMILY_DEDUCTIBLE_LIST_VALUES_WITH_BLANK);

            LOGGER.info("---=={Step 6, 8}==---");
            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);
            assetListDeductible.getAsset(IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_YES);
            assetListDeductible.getAsset(NUMBER_OF_GRADED_YEARS).setValue(NUMBER_OF_GRADED_YEARS_2);

            LOGGER.info("Verify field for step 6");
            verifyDisplayedControls(ImmutableList.of(DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK), softly);
            verifyNotDisplayedControls(ImmutableList.of(DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK, DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK), softly);
            LOGGER.info("Verify field for step 8");
            verifyDisplayedControls(ImmutableList.of(FAMILY_DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, FAMILY_DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, FAMILY_DEDUCTIBLE_SECOND_YEAR_IN_NETWORK,
                    FAMILY_DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK), softly);
            verifyNotDisplayedControls(ImmutableList.of(FAMILY_DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, FAMILY_DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, FAMILY_DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK,
                    FAMILY_DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK), softly);

            assetListDeductible.getAsset(NUMBER_OF_GRADED_YEARS).setValue(NUMBER_OF_GRADED_YEARS_3);
            LOGGER.info("Verify field for step 6");
            verifyDisplayedControls(ImmutableList.of(DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK,
                    DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK), softly);
            verifyNotDisplayedControls(ImmutableList.of(DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK, DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK), softly);

            LOGGER.info("Verify field for step 8");
            verifyDisplayedControls(ImmutableList.of(
                    FAMILY_DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, FAMILY_DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, FAMILY_DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, FAMILY_DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK,
                    FAMILY_DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, FAMILY_DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK), softly);
            verifyNotDisplayedControls(ImmutableList.of(FAMILY_DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK, FAMILY_DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK), softly);
        });
    }

    private void verifyDisplayedControls(ImmutableList<AssetDescriptor<ComboBox>> list, ETCSCoreSoftAssertions softly) {
        list.forEach(control -> softly.assertThat(assetListDeductible.getAsset(control)).isPresent().isEnabled().isRequired());
    }

    private void verifyNotDisplayedControls(ImmutableList<AssetDescriptor<ComboBox>> list, ETCSCoreSoftAssertions softly) {
        list.forEach(control -> softly.assertThat(assetListDeductible.getAsset(control)).isAbsent());
    }
}