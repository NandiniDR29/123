package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ALACARTE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.ASO_PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestStateRestrictionRulesVerificationPart3 extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static final AssetList coInsuranceAssetList = planDefinitionTab.getAssetList().getAsset(CO_INSURANCE);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20651"}, component = POLICY_GROUPBENEFITS)
    public void testStateRestrictionRulesVerificationPart3() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        initiateQuoteAndFillUpToTab(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASO_PLAN.getLabel()), VALUE_NO)
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NV"), PlanDefinitionTab.class, false);
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(ALACARTE));

        LOGGER.info("REN-20651 Steps#1-5, 7-11 verification");
        coInsuredSectionVerification("30%", "80%");

        LOGGER.info("REN-20651 Steps#15-19, 21-25 verification");
        changeSitusStateAndSetPlan("ME");
        coInsuredSectionVerification("20%", "90%");

        LOGGER.info("REN-20651 Steps#29-33, 35-39 verification");
        changeSitusStateAndSetPlan("KY");
        coInsuredSectionVerification("25%", "80%");

        LOGGER.info("REN-20651 Steps#42-46 verification");
        changeSitusStateAndSetPlan("CT");
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PREVENTIVE_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.BASIC_IN_NETWORK, CoInsuranceMetaData.BASIC_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.MAJOR_IN_NETWORK, CoInsuranceMetaData.MAJOR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PROSTHODONTICS_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.RADIOGRAPHS_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_OUT_OF_NETWORK);

        LOGGER.info("REN-20651 Step#48 verification");
        coInsuranceAssetList.getAsset(CoInsuranceMetaData.IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_YES);
        coInsuranceAssetList.getAsset(CoInsuranceMetaData.NUMBER_OF_GRADED_YEARS).setValue("4");
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PREVENTIVE_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_FIRST_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PREVENTIVE_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_SECOND_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PREVENTIVE_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_THIRD_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PREVENTIVE_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_FOURTH_YEAR_OUT_OF_NETWORK);

        LOGGER.info("REN-20651 Step#49 verification");
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.BASIC_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.BASIC_FIRST_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.BASIC_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.BASIC_SECOND_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.BASIC_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.BASIC_THIRD_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.BASIC_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.BASIC_FOURTH_YEAR_OUT_OF_NETWORK);

        LOGGER.info("REN-20651 Step#50 verification");
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.MAJOR_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.MAJOR_FIRST_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.MAJOR_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.MAJOR_SECOND_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.MAJOR_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.MAJOR_THIRD_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.MAJOR_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.MAJOR_FOURTH_YEAR_OUT_OF_NETWORK);

        LOGGER.info("REN-20651 Step#51 verification");
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PROSTHODONTICS_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_FIRST_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PROSTHODONTICS_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_SECOND_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PROSTHODONTICS_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_THIRD_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PROSTHODONTICS_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_FOURTH_YEAR_OUT_OF_NETWORK);

        LOGGER.info("REN-20651 Step#52 verification");
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.RADIOGRAPHS_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_FIRST_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.RADIOGRAPHS_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_SECOND_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.RADIOGRAPHS_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_THIRD_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.RADIOGRAPHS_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_FOURTH_YEAR_OUT_OF_NETWORK);
    }

    private void coInsuredSectionVerification(String percentageValue, String amountOut) {
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PREVENTIVE_IN_NETWORK, percentageValue, CoInsuranceMetaData.PREVENTIVE_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.BASIC_IN_NETWORK, percentageValue, CoInsuranceMetaData.BASIC_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.MAJOR_IN_NETWORK, percentageValue, CoInsuranceMetaData.MAJOR_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PROSTHODONTICS_IN_NETWORK, percentageValue, CoInsuranceMetaData.PROSTHODONTICS_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.RADIOGRAPHS_IN_NETWORK, percentageValue, CoInsuranceMetaData.RADIOGRAPHS_OUT_OF_NETWORK, amountOut);

        coInsuranceAssetList.getAsset(CoInsuranceMetaData.IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_YES);
        coInsuranceAssetList.getAsset(CoInsuranceMetaData.NUMBER_OF_GRADED_YEARS).setValue("4");
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PREVENTIVE_FIRST_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.PREVENTIVE_FIRST_YEAR_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PREVENTIVE_SECOND_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.PREVENTIVE_SECOND_YEAR_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PREVENTIVE_THIRD_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.PREVENTIVE_THIRD_YEAR_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PREVENTIVE_FOURTH_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.PREVENTIVE_FOURTH_YEAR_OUT_OF_NETWORK, amountOut);

        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.BASIC_FIRST_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.BASIC_FIRST_YEAR_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.BASIC_SECOND_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.BASIC_SECOND_YEAR_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.BASIC_THIRD_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.BASIC_THIRD_YEAR_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.BASIC_FOURTH_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.BASIC_FOURTH_YEAR_OUT_OF_NETWORK, amountOut);

        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.MAJOR_FIRST_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.MAJOR_FIRST_YEAR_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.MAJOR_SECOND_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.MAJOR_SECOND_YEAR_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.MAJOR_THIRD_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.MAJOR_THIRD_YEAR_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.MAJOR_FOURTH_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.MAJOR_FOURTH_YEAR_OUT_OF_NETWORK, amountOut);

        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PROSTHODONTICS_FIRST_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.PROSTHODONTICS_FIRST_YEAR_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PROSTHODONTICS_SECOND_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.PROSTHODONTICS_SECOND_YEAR_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PROSTHODONTICS_THIRD_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.PROSTHODONTICS_THIRD_YEAR_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PROSTHODONTICS_FOURTH_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.PROSTHODONTICS_FOURTH_YEAR_OUT_OF_NETWORK, amountOut);

        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.RADIOGRAPHS_FIRST_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.RADIOGRAPHS_FIRST_YEAR_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.RADIOGRAPHS_SECOND_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.RADIOGRAPHS_SECOND_YEAR_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.RADIOGRAPHS_THIRD_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.RADIOGRAPHS_THIRD_YEAR_OUT_OF_NETWORK, amountOut);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.RADIOGRAPHS_FOURTH_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.RADIOGRAPHS_FOURTH_YEAR_OUT_OF_NETWORK, amountOut);
    }

    private void setCoInsuranceValuesAndCheckMessageForDifferentStates(AssetDescriptor<ComboBox> inNetwork, String value, AssetDescriptor<ComboBox> outOfNetwork, String amountOut) {
        coInsuranceAssetList.getAsset(inNetwork).setValue("100%");
        coInsuranceAssetList.getAsset(outOfNetwork).setValue(amountOut);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR
                .format(String.format("State Requirement: %s must be within %s of %s.", outOfNetwork.getLabel(), value, inNetwork.getLabel())))).isAbsent();
    }

    private void changeSitusStateAndSetPlan(String situsState) {
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get(), Tab.doubleWaiter);
        policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(situsState);
        policyInformationTab.getAssetList().getAsset(ASO_PLAN).setValue(VALUE_NO);
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(ALACARTE));
    }

    private void setCoInsuranceValuesAndCheckMessageForCTStates(AssetDescriptor<ComboBox> inNetwork, AssetDescriptor<ComboBox> outOfNetwork) {
        coInsuranceAssetList.getAsset(inNetwork).setValue("0%");
        coInsuranceAssetList.getAsset(outOfNetwork).setValue("0%");
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR
                .format(String.format("State Requirement: If value for %s or %s is greater than 0, then both must be greater than 0.", outOfNetwork.getLabel(), inNetwork.getLabel())))).isAbsent();
    }
}
