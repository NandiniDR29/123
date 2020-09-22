package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ALACARTE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalDeductibleMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteConfigureDentalDeductibleRules extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private final static String BLANK_VALUE = "";
    private final static String LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK_WARNING_TEXT = "State Requirement: Lifetime Deductible - Out of Network must be equal to Lifetime Deductible - In Network.";
    private final static String LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK_ZERO_WARNING_TEXT = "State Requirement: If Lifetime Deductible - In Network = $0, then Lifetime Deductible - Out of Network must be less than or equal to $250";
    private final static String LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK_WARNING_TEXT_2X ="State Requirement: Lifetime Deductible - Out of Network must be less than or equal to 2x Lifetime Deductible - In Network.";
    private final static String LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK_WARNING_TEXT_3X = "State Requirement: Lifetime Deductible - Out of Network must be less than or equal to 3x Lifetime Deductible - In Network.";

    private static AssetList assetListDeductible;
    private static final ImmutableList<String> SITUS_STATE_VALUES = ImmutableList.of("TX", "LA", "GA", "MS");
    private static final ImmutableList<AssetDescriptor<ComboBox>> LIFETIME_DEDUCTIBLE_ASSET_DESCRIPTORS = ImmutableList.of(LIFETIME_DEDUCTIBLE_IN_NETWORK, LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13721", component = POLICY_GROUPBENEFITS)
    public void testConfigureDentalDeductibleRules() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NV"),
                PlanDefinitionTab.class);
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(ALACARTE));
        assetListDeductible = planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE);

        assertSoftly(softly -> {

            LOGGER.info("---=={Step 1}==---");
            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE).setValue(VALUE_YES);
            LIFETIME_DEDUCTIBLE_ASSET_DESCRIPTORS.forEach(control ->
                    softly.assertThat(assetListDeductible.getAsset(control)).isPresent().isEnabled().isRequired());

            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE).setValue(VALUE_NO);
            LIFETIME_DEDUCTIBLE_ASSET_DESCRIPTORS.forEach(control ->
                    softly.assertThat(assetListDeductible.getAsset(control)).isAbsent());

            LOGGER.info("---=={Step 2}==---");
            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_YES);
            softly.assertThat(assetListDeductible.getAsset(APPLY_DEDUCTIBLE_EPO)).isPresent().isEnabled();

            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);
            softly.assertThat(assetListDeductible.getAsset(APPLY_DEDUCTIBLE_EPO)).isAbsent();

            LOGGER.info("---=={Step 3}==---");
            SITUS_STATE_VALUES.forEach(value -> verifyStateRequirementFunctionalityForAttributesBySitusSite(value, softly));

            setSitusSiteAndReturnToPlanDefinitionTab("CA");
            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE).setValue(VALUE_YES);
            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_IN_NETWORK).setValue("$200");
            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK).setValue("$300");
            softly.assertThat(assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK)).hasNoWarning();

            LOGGER.info("---=={Step 4}==---");
            setSitusSiteAndReturnToPlanDefinitionTab("NC");
            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE).setValue(VALUE_NO);
            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE).setValue(VALUE_YES);
            LIFETIME_DEDUCTIBLE_ASSET_DESCRIPTORS.forEach(control ->
                    softly.assertThat(assetListDeductible.getAsset(control)).isPresent().isEnabled().isRequired());

            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_IN_NETWORK).setValue("$0");
            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK).setValue("$300");
            softly.assertThat(assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK)).hasWarningWithText(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK_ZERO_WARNING_TEXT);

            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK).setValue("$250");
            softly.assertThat(assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK)).hasNoWarning();

            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK).setValue("$0");
            softly.assertThat(assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK)).hasNoWarning();

            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_IN_NETWORK).setValue("$50");
            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK).setValue("$300");
            softly.assertThat(assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK)).hasWarningWithText(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK_WARNING_TEXT_2X);

            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK).setValue("$100");
            softly.assertThat(assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK)).hasNoWarning();

            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK).setValue("$25");
            softly.assertThat(assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK)).hasNoWarning();

            LOGGER.info("---=={Step 5}==---");
            setSitusSiteAndReturnToPlanDefinitionTab("OK");
            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE).setValue(VALUE_NO);
            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE).setValue(VALUE_YES);

            LIFETIME_DEDUCTIBLE_ASSET_DESCRIPTORS.forEach(control ->
                    softly.assertThat(assetListDeductible.getAsset(control)).isPresent().isEnabled().isRequired());

            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_IN_NETWORK).setValue("$0");
            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK).setValue("$300");
            softly.assertThat(assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK)).hasNoWarning();

            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_IN_NETWORK).setValue("$25");
            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK).setValue("$100");
            softly.assertThat(assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK)).hasWarningWithText(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK_WARNING_TEXT_3X);

            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK).setValue("$75");
            softly.assertThat(assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK)).hasNoWarning();

            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK).setValue("$25");
            softly.assertThat(assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK)).hasNoWarning();
        });
    }

    private void verifyStateRequirementFunctionalityForAttributesBySitusSite(String situsState, ETCSCoreSoftAssertions softly) {

        setSitusSiteAndReturnToPlanDefinitionTab(situsState);

        assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE).setValue(VALUE_YES);
        LIFETIME_DEDUCTIBLE_ASSET_DESCRIPTORS.forEach(control ->
                softly.assertThat(assetListDeductible.getAsset(control)).isPresent().isEnabled().isRequired());

        assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_IN_NETWORK).setValue("$200");
        assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK).setValue("$300");
        softly.assertThat(assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK)).hasWarningWithText(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK_WARNING_TEXT);

        assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK).setValue("$200");
        softly.assertThat(assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK)).hasNoWarning();
    }

    private void setSitusSiteAndReturnToPlanDefinitionTab(String situsState) {
        LOGGER.info(String.format("Set Situs State = %s", situsState));
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
        policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(situsState);
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(ALACARTE));
    }
}