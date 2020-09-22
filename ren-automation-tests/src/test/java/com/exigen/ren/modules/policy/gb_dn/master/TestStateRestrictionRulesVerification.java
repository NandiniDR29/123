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
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ASO;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ASOALC;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalMaximumMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.ASO_PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestStateRestrictionRulesVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static final AssetList dentalMaxAssetList = planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM);
    private static final AssetList coInsuranceAssetList = planDefinitionTab.getAssetList().getAsset(CO_INSURANCE);
    private static final AssetList dentalDedAssetList = planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE);
    private static final AssetList orthodontiaAssetList = planDefinitionTab.getAssetList().getAsset(ORTHODONTIA);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20251"}, component = POLICY_GROUPBENEFITS)
    public void testStateRestrictionRulesVerification_TC1() {
        LOGGER.info("General Preconditions");
        policyInitiationWithSitusStateAndPlan("TX", ASOALC);

        LOGGER.info("REN-20251 Step#1 verification");
        dentalMaxAssetList.getAsset(PlanDefinitionTabMetaData.DentalMaximumMetaData.IS_IT_GRADED_DENTAL_MAXIMUM).setValue(VALUE_YES);
        dentalMaxAssetList.getAsset(PlanDefinitionTabMetaData.DentalMaximumMetaData.NUMBER_OF_GRADED_YEARS).setValue("4");
        setDentalMaxValuesAndCheckMessage(PLAN_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_FIRST_YEAR_IN_NETWORK, "$1,000", "$1,500");
        setDentalMaxValuesAndCheckMessage(PLAN_MAXIMUM_SECOND_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_SECOND_YEAR_IN_NETWORK, "$1,000", "$1,500");
        setDentalMaxValuesAndCheckMessage(PLAN_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_THIRD_YEAR_IN_NETWORK, "$1,000", "$1,500");
        setDentalMaxValuesAndCheckMessage(PLAN_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK, PLAN_MAXIMUM_FOURTH_YEAR_IN_NETWORK, "$1,000", "$1,500");

        LOGGER.info("REN-20251 Step#3 verification");
        dentalMaxAssetList.getAsset(PlanDefinitionTabMetaData.DentalMaximumMetaData.IS_IT_GRADED_DENTAL_MAXIMUM).setValue(VALUE_NO);
        setDentalMaxValuesAndCheckMessage(PLAN_MAXIMUM_IN_NETWORK, PLAN_MAXIMUM_OUT_NETWORK, "$2,500", "$3,500");

        LOGGER.info("REN-20251 Steps#4-8 verification");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.PREVENTIVE_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_OUT_OF_NETWORK, "100%", "90%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.BASIC_IN_NETWORK, CoInsuranceMetaData.BASIC_OUT_OF_NETWORK, "80%", "90%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.MAJOR_IN_NETWORK, CoInsuranceMetaData.MAJOR_OUT_OF_NETWORK, "80%", "90%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.PROSTHODONTICS_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_OUT_OF_NETWORK, "80%", "90%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.RADIOGRAPHS_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_OUT_OF_NETWORK, "80%", "90%");

        LOGGER.info("REN-20251 Steps#10 verification");
        coInsuranceAssetList.getAsset(CoInsuranceMetaData.IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_YES);
        coInsuranceAssetList.getAsset(CoInsuranceMetaData.NUMBER_OF_GRADED_YEARS).setValue("4");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.PREVENTIVE_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_FIRST_YEAR_OUT_OF_NETWORK, "40%", "30%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.PREVENTIVE_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_SECOND_YEAR_OUT_OF_NETWORK, "40%", "30%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.PREVENTIVE_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_THIRD_YEAR_OUT_OF_NETWORK, "40%", "30%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.PREVENTIVE_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_FOURTH_YEAR_OUT_OF_NETWORK, "40%", "30%");

        LOGGER.info("REN-20251 Steps#11 verification");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.BASIC_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.BASIC_FIRST_YEAR_OUT_OF_NETWORK, "40%", "30%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.BASIC_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.BASIC_SECOND_YEAR_OUT_OF_NETWORK, "40%", "30%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.BASIC_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.BASIC_THIRD_YEAR_OUT_OF_NETWORK, "40%", "30%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.BASIC_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.BASIC_FOURTH_YEAR_OUT_OF_NETWORK, "40%", "30%");

        LOGGER.info("REN-20251 Steps#12 verification");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.MAJOR_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.MAJOR_FIRST_YEAR_OUT_OF_NETWORK, "40%", "30%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.MAJOR_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.MAJOR_SECOND_YEAR_OUT_OF_NETWORK, "40%", "30%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.MAJOR_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.MAJOR_THIRD_YEAR_OUT_OF_NETWORK, "40%", "30%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.MAJOR_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.MAJOR_FOURTH_YEAR_OUT_OF_NETWORK, "40%", "30%");

        LOGGER.info("REN-20251 Steps#13 verification");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.PROSTHODONTICS_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_FIRST_YEAR_OUT_OF_NETWORK, "40%", "30%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.PROSTHODONTICS_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_SECOND_YEAR_OUT_OF_NETWORK, "40%", "30%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.PROSTHODONTICS_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_THIRD_YEAR_OUT_OF_NETWORK, "40%", "30%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.PROSTHODONTICS_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_FOURTH_YEAR_OUT_OF_NETWORK, "40%", "30%");

        LOGGER.info("REN-20251 Steps#14 verification");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.RADIOGRAPHS_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_FIRST_YEAR_OUT_OF_NETWORK, "40%", "30%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.RADIOGRAPHS_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_SECOND_YEAR_OUT_OF_NETWORK, "40%", "30%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.RADIOGRAPHS_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_THIRD_YEAR_OUT_OF_NETWORK, "40%", "30%");
        setCoInsuranceValuesAndCheckMessage(CoInsuranceMetaData.RADIOGRAPHS_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_FOURTH_YEAR_OUT_OF_NETWORK, "40%", "30%");

        LOGGER.info("REN-20251 Steps#15 verification");
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_NO);
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_OUT_OF_NETWORK, "$50", "$75");

        LOGGER.info("REN-20251 Steps#16 verification");
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE).setValue(VALUE_YES);
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE_IN_NETWORK, DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK, "$100", "$150");

        LOGGER.info("REN-20251 Steps#18 verification");
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_YES);
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.NUMBER_OF_GRADED_YEARS).setValue("4");
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, "$50", "$75");
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK, "$50", "$75");
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, "$50", "$75");
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK, "$50", "$75");

        LOGGER.info("REN-20251 Steps#19 verification");
        orthodontiaAssetList.getAsset(OrthodontiaMetaData.ORTHO_COVERAGE).setValue(VALUE_YES);
        setOrthodontiaValuesAndCheckMessageForTXState(OrthodontiaMetaData.CO_INSURANCE_IN_NETWORK, OrthodontiaMetaData.CO_INSURANCE_OUT_OF_NETWORK, "50%", "60%");

        LOGGER.info("REN-20251 Steps#20 verification");
        setOrthodontiaValuesAndCheckMessageForTXState(OrthodontiaMetaData.YEARLY_DEDUCTIBLE_IN_NETWORK, OrthodontiaMetaData.YEARLY_DEDUCTIBLE_OUT_OF_NETWORK, "$75", "$100");

        LOGGER.info("REN-20251 Steps#21 verification");
        orthodontiaAssetList.getAsset(OrthodontiaMetaData.YEARLY_MAXIMUM).setValue(VALUE_YES);
        setOrthodontiaValuesAndCheckMessageForTXState(OrthodontiaMetaData.YEARLY_MAXIMUM_IN_NETWORK, OrthodontiaMetaData.YEARLY_MAXIMUM_OUT_OF_NETWORK, "$1,000", "$2,000");

        LOGGER.info("REN-20251 Steps#24 verification");
        orthodontiaAssetList.getAsset(OrthodontiaMetaData.IS_IT_GRADED_ORTHODONTIA).setValue(VALUE_YES);
        orthodontiaAssetList.getAsset(OrthodontiaMetaData.NUMBER_OF_GRADED_YEARS).setValue("4");
        setOrthodontiaValuesAndCheckMessageForTXState(OrthodontiaMetaData.COINSURANCE_FIRST_YEAR_IN_NETWORK, OrthodontiaMetaData.COINSURANCE_FIRST_YEAR_OUT_OF_NETWORK, "50%", "60%");
        setOrthodontiaValuesAndCheckMessageForTXState(OrthodontiaMetaData.COINSURANCE_SECOND_YEAR_IN_NETWORK, OrthodontiaMetaData.COINSURANCE_SECOND_YEAR_OUT_OF_NETWORK, "50%", "60%");
        setOrthodontiaValuesAndCheckMessageForTXState(OrthodontiaMetaData.COINSURANCE_THIRD_YEAR_IN_NETWORK, OrthodontiaMetaData.COINSURANCE_THIRD_YEAR_OUT_OF_NETWORK, "50%", "60%");
        setOrthodontiaValuesAndCheckMessageForTXState(OrthodontiaMetaData.COINSURANCE_FOURTH_YEAR_IN_NETWORK, OrthodontiaMetaData.COINSURANCE_FOURTH_YEAR_OUT_OF_NETWORK, "50%", "60%");

        LOGGER.info("REN-20251 Steps#25 verification");
        setOrthodontiaValuesAndCheckMessageForTXState(OrthodontiaMetaData.YEARLY_MAXIMUM_FIRST_YEAR_IN_NETWORK, OrthodontiaMetaData.YEARLY_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK, "$1,000", "$2,000");
        setOrthodontiaValuesAndCheckMessageForTXState(OrthodontiaMetaData.YEARLY_MAXIMUM_SECOND_YEAR_IN_NETWORK, OrthodontiaMetaData.YEARLY_MAXIMUM_SECOND_YEAR_OUT_OF_NETWORK, "$1,000", "$2,000");
        setOrthodontiaValuesAndCheckMessageForTXState(OrthodontiaMetaData.YEARLY_MAXIMUM_THIRD_YEAR_IN_NETWORK, OrthodontiaMetaData.YEARLY_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK, "$1,000", "$2,000");
        setOrthodontiaValuesAndCheckMessageForTXState(OrthodontiaMetaData.YEARLY_MAXIMUM_FOURTH_YEAR_IN_NETWORK, OrthodontiaMetaData.YEARLY_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK, "$1,000", "$2,000");

        LOGGER.info("REN-20251 Steps#26 verification");
        setOrthodontiaValuesAndCheckMessageForTXState(OrthodontiaMetaData.LIFETIME_MAXIMUM_FIRST_YEAR_IN_NETWORK, OrthodontiaMetaData.LIFETIME_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK, "$500", "$1,000");
        setOrthodontiaValuesAndCheckMessageForTXState(OrthodontiaMetaData.LIFETIME_MAXIMUM_SECOND_YEAR_IN_NETWORK, OrthodontiaMetaData.LIFETIME_MAXIMUM_SECOND_YEAR_OUT_OF_NETWORK, "$500", "$1,000");
        setOrthodontiaValuesAndCheckMessageForTXState(OrthodontiaMetaData.LIFETIME_MAXIMUM_THIRD_YEAR_IN_NETWORK, OrthodontiaMetaData.LIFETIME_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK, "$500", "$1,000");
        setOrthodontiaValuesAndCheckMessageForTXState(OrthodontiaMetaData.LIFETIME_MAXIMUM_FOURTH_YEAR_IN_NETWORK, OrthodontiaMetaData.LIFETIME_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK, "$500", "$1,000");

        LOGGER.info("REN-20251 Steps#27 verification");
        orthodontiaAssetList.getAsset(OrthodontiaMetaData.LIFETIME_DEDUCTIBLE).setValue(VALUE_YES);
        setOrthodontiaValuesAndCheckMessageForTXState(OrthodontiaMetaData.LIFETIME_DEDUCTIBLE_IN_NETWORK, OrthodontiaMetaData.LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK, "$100", "$200");
    }


    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20254", "REN-20272"}, component = POLICY_GROUPBENEFITS)
    public void testStateRestrictionRulesVerification_TC2() {
        LOGGER.info("General Preconditions");
        policyInitiationWithSitusStateAndPlan("NV", ASO);

        LOGGER.info("REN-20254 Step#1-5, 7-11 verification");
        coInsuredSectionVerification("30%");

        LOGGER.info("REN-20254 Steps#15-19, 21-25 verification");
        changeSitusStateAndSetPlan("ME");
        coInsuredSectionVerification("20%");

        LOGGER.info("REN-20254 Steps#29-33, 35-39 verification");
        changeSitusStateAndSetPlan("KY");
        coInsuredSectionVerification("25%");

        LOGGER.info("REN-20254 Steps#42-46 verification");
        changeSitusStateAndSetPlan("CT");
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PREVENTIVE_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.BASIC_IN_NETWORK, CoInsuranceMetaData.BASIC_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.MAJOR_IN_NETWORK, CoInsuranceMetaData.MAJOR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PROSTHODONTICS_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.RADIOGRAPHS_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_OUT_OF_NETWORK);

        LOGGER.info("REN-20254 Step#48 verification");
        coInsuranceAssetList.getAsset(CoInsuranceMetaData.IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_YES);
        coInsuranceAssetList.getAsset(CoInsuranceMetaData.NUMBER_OF_GRADED_YEARS).setValue("4");
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PREVENTIVE_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_FIRST_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PREVENTIVE_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_SECOND_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PREVENTIVE_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_THIRD_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PREVENTIVE_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_FOURTH_YEAR_OUT_OF_NETWORK);

        LOGGER.info("REN-20254 Step#49 verification");
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.BASIC_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.BASIC_FIRST_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.BASIC_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.BASIC_SECOND_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.BASIC_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.BASIC_THIRD_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.BASIC_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.BASIC_FOURTH_YEAR_OUT_OF_NETWORK);

        LOGGER.info("REN-20254 Step#50 verification");
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.MAJOR_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.MAJOR_FIRST_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.MAJOR_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.MAJOR_SECOND_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.MAJOR_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.MAJOR_THIRD_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.MAJOR_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.MAJOR_FOURTH_YEAR_OUT_OF_NETWORK);

        LOGGER.info("REN-20254 Step#51 verification");
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PROSTHODONTICS_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_FIRST_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PROSTHODONTICS_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_SECOND_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PROSTHODONTICS_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_THIRD_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.PROSTHODONTICS_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_FOURTH_YEAR_OUT_OF_NETWORK);

        LOGGER.info("REN-20254 Step#52 verification");
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.RADIOGRAPHS_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_FIRST_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.RADIOGRAPHS_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_SECOND_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.RADIOGRAPHS_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_THIRD_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForCTStates(CoInsuranceMetaData.RADIOGRAPHS_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_FOURTH_YEAR_OUT_OF_NETWORK);

        LOGGER.info("REN-20272 Steps#1-5 verification");
        changeSitusStateAndSetPlan("NV");
        planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_YES);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PREVENTIVE_IN_NETWORK_EPO, "30%", CoInsuranceMetaData.PREVENTIVE_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.BASIC_IN_NETWORK_EPO, "30%", CoInsuranceMetaData.BASIC_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.MAJOR_IN_NETWORK_EPO, "30%", CoInsuranceMetaData.MAJOR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PROSTHODONTICS_EPO, "30%", CoInsuranceMetaData.PROSTHODONTICS_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.RADIOGRAPHS_EPO, "30%", CoInsuranceMetaData.RADIOGRAPHS_OUT_OF_NETWORK);
    }

    private void policyInitiationWithSitusStateAndPlan(String situsState, String planName) {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        initiateQuoteAndFillUpToTab(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASO_PLAN.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), situsState), PlanDefinitionTab.class, false);
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(planName));
    }

    private void changeSitusStateAndSetPlan(String situsState) {
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get(), Tab.doubleWaiter);
        policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(situsState);
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(ASO));
    }

    private void setDentalMaxValuesAndCheckMessage(AssetDescriptor<ComboBox> inNetwork, AssetDescriptor<ComboBox> outOfNetwork, String amountIn, String amountOut) {
        dentalMaxAssetList.getAsset(inNetwork).setValue(amountIn);
        dentalMaxAssetList.getAsset(outOfNetwork).setValue(amountOut);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR
                .format(String.format("State Requirement: %s must be equal to %s.", outOfNetwork.getLabel(), inNetwork.getLabel())))).isAbsent();
    }

    private void setCoInsuranceValuesAndCheckMessage(AssetDescriptor<ComboBox> inNetwork, AssetDescriptor<ComboBox> outOfNetwork, String amountIn, String amountOut) {
        coInsuranceAssetList.getAsset(inNetwork).setValue(amountIn);
        coInsuranceAssetList.getAsset(outOfNetwork).setValue(amountOut);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR
                .format(String.format("State Requirement: %s must be equal to %s.", outOfNetwork.getLabel(), inNetwork.getLabel())))).isAbsent();
    }

    private void setDentalDedValuesAndCheckMessage(AssetDescriptor<ComboBox> inNetwork, AssetDescriptor<ComboBox> outOfNetwork, String amountIn, String amountOut) {
        dentalDedAssetList.getAsset(inNetwork).setValue(amountIn);
        dentalDedAssetList.getAsset(outOfNetwork).setValue(amountOut);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR
                .format(String.format("State Requirement: %s must be equal to %s.", outOfNetwork.getLabel(), inNetwork.getLabel())))).isAbsent();
    }

    private void setOrthodontiaValuesAndCheckMessageForTXState(AssetDescriptor<ComboBox> inNetwork, AssetDescriptor<ComboBox> outOfNetwork, String amountIn, String amountOut) {
        orthodontiaAssetList.getAsset(inNetwork).setValue(amountIn);
        orthodontiaAssetList.getAsset(outOfNetwork).setValue(amountOut);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR
                .format(String.format("State Requirement: %s must be equal to %s.", outOfNetwork.getLabel(), inNetwork.getLabel())))).isAbsent();
    }

    private void coInsuredSectionVerification(String percentageValue) {
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PREVENTIVE_IN_NETWORK, percentageValue, CoInsuranceMetaData.PREVENTIVE_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.BASIC_IN_NETWORK, percentageValue, CoInsuranceMetaData.BASIC_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.MAJOR_IN_NETWORK, percentageValue, CoInsuranceMetaData.MAJOR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PROSTHODONTICS_IN_NETWORK, percentageValue, CoInsuranceMetaData.PROSTHODONTICS_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.RADIOGRAPHS_IN_NETWORK, percentageValue, CoInsuranceMetaData.RADIOGRAPHS_OUT_OF_NETWORK);

        coInsuranceAssetList.getAsset(CoInsuranceMetaData.IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_YES);
        coInsuranceAssetList.getAsset(CoInsuranceMetaData.NUMBER_OF_GRADED_YEARS).setValue("4");
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PREVENTIVE_FIRST_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.PREVENTIVE_FIRST_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PREVENTIVE_SECOND_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.PREVENTIVE_SECOND_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PREVENTIVE_THIRD_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.PREVENTIVE_THIRD_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PREVENTIVE_FOURTH_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.PREVENTIVE_FOURTH_YEAR_OUT_OF_NETWORK);

        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.BASIC_FIRST_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.BASIC_FIRST_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.BASIC_SECOND_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.BASIC_SECOND_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.BASIC_THIRD_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.BASIC_THIRD_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.BASIC_FOURTH_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.BASIC_FOURTH_YEAR_OUT_OF_NETWORK);

        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.MAJOR_FIRST_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.MAJOR_FIRST_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.MAJOR_SECOND_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.MAJOR_SECOND_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.MAJOR_THIRD_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.MAJOR_THIRD_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.MAJOR_FOURTH_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.MAJOR_FOURTH_YEAR_OUT_OF_NETWORK);

        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PROSTHODONTICS_FIRST_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.PROSTHODONTICS_FIRST_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PROSTHODONTICS_SECOND_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.PROSTHODONTICS_SECOND_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PROSTHODONTICS_THIRD_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.PROSTHODONTICS_THIRD_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.PROSTHODONTICS_FOURTH_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.PROSTHODONTICS_FOURTH_YEAR_OUT_OF_NETWORK);

        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.RADIOGRAPHS_FIRST_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.RADIOGRAPHS_FIRST_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.RADIOGRAPHS_SECOND_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.RADIOGRAPHS_SECOND_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.RADIOGRAPHS_THIRD_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.RADIOGRAPHS_THIRD_YEAR_OUT_OF_NETWORK);
        setCoInsuranceValuesAndCheckMessageForDifferentStates(CoInsuranceMetaData.RADIOGRAPHS_FOURTH_YEAR_IN_NETWORK, percentageValue, CoInsuranceMetaData.RADIOGRAPHS_FOURTH_YEAR_OUT_OF_NETWORK);
    }

    private void setCoInsuranceValuesAndCheckMessageForDifferentStates(AssetDescriptor<ComboBox> inNetwork, String value, AssetDescriptor<ComboBox> outOfNetwork) {
        coInsuranceAssetList.getAsset(inNetwork).setValue("100%");
        coInsuranceAssetList.getAsset(outOfNetwork).setValue("45%");
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR
                .format(String.format("State Requirement: %s must be within %s of %s.", outOfNetwork.getLabel(), value, inNetwork.getLabel())))).isAbsent();
    }

    private void setCoInsuranceValuesAndCheckMessageForCTStates(AssetDescriptor<ComboBox> inNetwork, AssetDescriptor<ComboBox> outOfNetwork) {
        coInsuranceAssetList.getAsset(inNetwork).setValue("0%");
        coInsuranceAssetList.getAsset(outOfNetwork).setValue("45%");
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR
                .format(String.format("State Requirement: If value for %s or %s is greater than 0, then both must be greater than 0.", outOfNetwork.getLabel(), inNetwork.getLabel())))).isAbsent();
    }
}
