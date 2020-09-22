package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.ipb.eisa.utils.RetryService;
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
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ALACARTE;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ASOALC;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.ASO_PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestStateRestrictionRulesVerificationPart2 extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static final AssetList dentalMaxAssetList = planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM);
    private static final AssetList coInsuranceAssetList = planDefinitionTab.getAssetList().getAsset(CO_INSURANCE);
    private static final AssetList dentalDedAssetList = planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE);
    private static final AssetList orthodontiaAssetList = planDefinitionTab.getAssetList().getAsset(ORTHODONTIA);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20649"}, component = POLICY_GROUPBENEFITS)
    public void testStateRestrictionRulesVerificationPart2_TC1() {
        LOGGER.info("General Preconditions");
        policyInitiationNotASOWithSitusStateAndPlan("TX");

        LOGGER.info("REN-20649 Step#1 verification");
        dentalMaxAssetList.getAsset(PlanDefinitionTabMetaData.DentalMaximumMetaData.IS_IT_GRADED_DENTAL_MAXIMUM).setValue(VALUE_YES);
        dentalMaxAssetList.getAsset(DentalMaximumMetaData.NUMBER_OF_GRADED_YEARS).setValue("4");

        setSectionValuesAndCheckMessage(DentalMaximumMetaData.PLAN_MAXIMUM_FIRST_YEAR_IN_NETWORK, DentalMaximumMetaData.PLAN_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK, "$1,000", dentalMaxAssetList);
        setSectionValuesAndCheckMessage(DentalMaximumMetaData.PLAN_MAXIMUM_SECOND_YEAR_IN_NETWORK, DentalMaximumMetaData.PLAN_MAXIMUM_SECOND_YEAR_OUT_OF_NETWORK, "$1,000", dentalMaxAssetList);
        setSectionValuesAndCheckMessage(DentalMaximumMetaData.PLAN_MAXIMUM_THIRD_YEAR_IN_NETWORK, DentalMaximumMetaData.PLAN_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK, "$1,000", dentalMaxAssetList);
        setSectionValuesAndCheckMessage(DentalMaximumMetaData.PLAN_MAXIMUM_FOURTH_YEAR_IN_NETWORK, DentalMaximumMetaData.PLAN_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK, "$1,000", dentalMaxAssetList);

        LOGGER.info("REN-20649 Step#3 verification");
        dentalMaxAssetList.getAsset(PlanDefinitionTabMetaData.DentalMaximumMetaData.IS_IT_GRADED_DENTAL_MAXIMUM).setValue(VALUE_NO);
        setSectionValuesAndCheckMessage(DentalMaximumMetaData.PLAN_MAXIMUM_IN_NETWORK, DentalMaximumMetaData.PLAN_MAXIMUM_OUT_NETWORK, "$2,500", dentalMaxAssetList);

        LOGGER.info("REN-20649 Steps#4-8 verification");
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.PREVENTIVE_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_OUT_OF_NETWORK, "100%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.BASIC_IN_NETWORK, CoInsuranceMetaData.BASIC_OUT_OF_NETWORK, "90%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.MAJOR_IN_NETWORK, CoInsuranceMetaData.MAJOR_OUT_OF_NETWORK, "90%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.PROSTHODONTICS_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_OUT_OF_NETWORK, "90%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.RADIOGRAPHS_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_OUT_OF_NETWORK, "90%", coInsuranceAssetList);

        LOGGER.info("REN-20649 Step#10 verification");
        coInsuranceAssetList.getAsset(CoInsuranceMetaData.IS_IT_GRADED_CO_INSURANCE).setValue(VALUE_YES);
        coInsuranceAssetList.getAsset(CoInsuranceMetaData.NUMBER_OF_GRADED_YEARS).setValue("4");

        setSectionValuesAndCheckMessage(CoInsuranceMetaData.PREVENTIVE_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_FIRST_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.PREVENTIVE_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_SECOND_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.PREVENTIVE_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_THIRD_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.PREVENTIVE_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.PREVENTIVE_FOURTH_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);

        LOGGER.info("REN-20649 Step#11 verification");
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.BASIC_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.BASIC_FIRST_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.BASIC_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.BASIC_SECOND_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.BASIC_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.BASIC_THIRD_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.BASIC_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.BASIC_FOURTH_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);

        LOGGER.info("REN-20649 Step#12 verification");
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.MAJOR_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.MAJOR_FIRST_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.MAJOR_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.MAJOR_SECOND_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.MAJOR_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.MAJOR_THIRD_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.MAJOR_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.MAJOR_FOURTH_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);

        LOGGER.info("REN-20649 Step#13 verification");
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.PROSTHODONTICS_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_FIRST_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.PROSTHODONTICS_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_SECOND_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.PROSTHODONTICS_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_THIRD_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.PROSTHODONTICS_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.PROSTHODONTICS_FOURTH_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);

        LOGGER.info("REN-20649 Step#14 verification");
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.RADIOGRAPHS_FIRST_YEAR_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_FIRST_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.RADIOGRAPHS_SECOND_YEAR_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_SECOND_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.RADIOGRAPHS_THIRD_YEAR_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_THIRD_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);
        setSectionValuesAndCheckMessage(CoInsuranceMetaData.RADIOGRAPHS_FOURTH_YEAR_IN_NETWORK, CoInsuranceMetaData.RADIOGRAPHS_FOURTH_YEAR_OUT_OF_NETWORK, "40%", coInsuranceAssetList);

        LOGGER.info("REN-20649 Step#15 verification");
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_NO);
        setSectionValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_OUT_OF_NETWORK, "$50", dentalDedAssetList);

        LOGGER.info("REN-20649 Step#16 verification");
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE).setValue(VALUE_YES);
        setSectionValuesAndCheckMessage(DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE_IN_NETWORK, DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK, "$150", dentalDedAssetList);

        LOGGER.info("REN-20649 Step#18 verification");
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_YES);
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.NUMBER_OF_GRADED_YEARS).setValue("4");
        setSectionValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, "$50", dentalDedAssetList);
        setSectionValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK, "$50", dentalDedAssetList);
        setSectionValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, "$50", dentalDedAssetList);
        setSectionValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK, "$50", dentalDedAssetList);

        LOGGER.info("REN-20649 Step#19 verification");
        orthodontiaAssetList.getAsset(OrthodontiaMetaData.ORTHO_COVERAGE).setValue(VALUE_YES);
        setSectionValuesAndCheckMessage(OrthodontiaMetaData.CO_INSURANCE_IN_NETWORK, OrthodontiaMetaData.CO_INSURANCE_OUT_OF_NETWORK, "50%", orthodontiaAssetList);

        LOGGER.info("REN-20649 Step#20 verification");
        setSectionValuesAndCheckMessage(OrthodontiaMetaData.YEARLY_DEDUCTIBLE_IN_NETWORK, OrthodontiaMetaData.YEARLY_DEDUCTIBLE_OUT_OF_NETWORK, "$75", orthodontiaAssetList);

        LOGGER.info("REN-20649 Step#21 verification");
        orthodontiaAssetList.getAsset(OrthodontiaMetaData.YEARLY_MAXIMUM).setValue(VALUE_YES);
        setSectionValuesAndCheckMessage(OrthodontiaMetaData.YEARLY_MAXIMUM_IN_NETWORK, OrthodontiaMetaData.YEARLY_MAXIMUM_OUT_OF_NETWORK, "$2,000", orthodontiaAssetList);

        LOGGER.info("REN-20649 Step22 verification");
        setSectionValuesAndCheckMessage(OrthodontiaMetaData.LIFETIME_MAXIMUM_IN_NETWORK, OrthodontiaMetaData.LIFETIME_MAXIMUM_OUT_OF_NETWORK, "$2,000", orthodontiaAssetList);

        LOGGER.info("REN-20649 Step#24 verification");
        orthodontiaAssetList.getAsset(OrthodontiaMetaData.IS_IT_GRADED_ORTHODONTIA).setValue(VALUE_YES);
        orthodontiaAssetList.getAsset(OrthodontiaMetaData.NUMBER_OF_GRADED_YEARS).setValue("4");

        orthodontiaAssetList.getAsset(OrthodontiaMetaData.COINSURANCE_FIRST_YEAR_IN_NETWORK).setValue("50%");
        orthodontiaAssetList.getAsset(OrthodontiaMetaData.COINSURANCE_FIRST_YEAR_OUT_OF_NETWORK).setValue("50%");
        orthodontiaAssetList.getAsset(OrthodontiaMetaData.COINSURANCE_SECOND_YEAR_IN_NETWORK).setValue("50%");
        orthodontiaAssetList.getAsset(OrthodontiaMetaData.COINSURANCE_SECOND_YEAR_OUT_OF_NETWORK).setValue("50%");
        setSectionValuesAndCheckMessage(OrthodontiaMetaData.COINSURANCE_THIRD_YEAR_IN_NETWORK, OrthodontiaMetaData.COINSURANCE_THIRD_YEAR_OUT_OF_NETWORK, "50%", orthodontiaAssetList);
        setSectionValuesAndCheckMessage(OrthodontiaMetaData.COINSURANCE_FOURTH_YEAR_IN_NETWORK, OrthodontiaMetaData.COINSURANCE_FOURTH_YEAR_OUT_OF_NETWORK, "50%", orthodontiaAssetList);

        LOGGER.info("REN-20649 Step#25 verification");
        setSectionValuesAndCheckMessage(OrthodontiaMetaData.YEARLY_MAXIMUM_FIRST_YEAR_IN_NETWORK, OrthodontiaMetaData.YEARLY_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK, "$2,000", orthodontiaAssetList);
        setSectionValuesAndCheckMessage(OrthodontiaMetaData.YEARLY_MAXIMUM_SECOND_YEAR_IN_NETWORK, OrthodontiaMetaData.YEARLY_MAXIMUM_SECOND_YEAR_OUT_OF_NETWORK, "$2,000", orthodontiaAssetList);
        setSectionValuesAndCheckMessage(OrthodontiaMetaData.YEARLY_MAXIMUM_THIRD_YEAR_IN_NETWORK, OrthodontiaMetaData.YEARLY_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK, "$2,000", orthodontiaAssetList);
        setSectionValuesAndCheckMessage(OrthodontiaMetaData.YEARLY_MAXIMUM_FOURTH_YEAR_IN_NETWORK, OrthodontiaMetaData.YEARLY_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK, "$2,000", orthodontiaAssetList);

        LOGGER.info("REN-20649 Step#26 verification");
        setSectionValuesAndCheckMessage(OrthodontiaMetaData.LIFETIME_MAXIMUM_FIRST_YEAR_IN_NETWORK, OrthodontiaMetaData.LIFETIME_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK, "$1,000", orthodontiaAssetList);
        orthodontiaAssetList.getAsset(OrthodontiaMetaData.LIFETIME_MAXIMUM_SECOND_YEAR_IN_NETWORK).setValue("$1,000");
        orthodontiaAssetList.getAsset(OrthodontiaMetaData.LIFETIME_MAXIMUM_SECOND_YEAR_OUT_OF_NETWORK).setValue("$1,000");
        setSectionValuesAndCheckMessage(OrthodontiaMetaData.LIFETIME_MAXIMUM_THIRD_YEAR_IN_NETWORK, OrthodontiaMetaData.LIFETIME_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK, "$1,000", orthodontiaAssetList);
        setSectionValuesAndCheckMessage(OrthodontiaMetaData.LIFETIME_MAXIMUM_FOURTH_YEAR_IN_NETWORK, OrthodontiaMetaData.LIFETIME_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK, "$1,000", orthodontiaAssetList);

        LOGGER.info("REN-20649 Step#27 verification");
        orthodontiaAssetList.getAsset(OrthodontiaMetaData.LIFETIME_DEDUCTIBLE).setValue(VALUE_YES);
        setSectionValuesAndCheckMessage(OrthodontiaMetaData.LIFETIME_DEDUCTIBLE_IN_NETWORK, OrthodontiaMetaData.LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK, "$200", orthodontiaAssetList);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20650", "REN-20273"}, component = POLICY_GROUPBENEFITS)
    public void testStateRestrictionRulesVerificationPart2_TC2() {
        LOGGER.info("General Preconditions");
        policyInitiationNotASOWithSitusStateAndPlan("NC");
        LOGGER.info("REN-20650 Step#1 verification");
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_NO);
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_OUT_OF_NETWORK, "$100", "$150", "2x");

        LOGGER.info("REN-20650 Step#2 verification");
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE).setValue(VALUE_YES);
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE_IN_NETWORK, DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK, "$100", "$150", "2x");

        LOGGER.info("REN-20650 Step#3 verification");
        setDentalDedSectionAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_OUT_OF_NETWORK, "$250");

        LOGGER.info("REN-20650 Step#4 verification");
        setDentalDedSectionAndCheckMessage(DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE_IN_NETWORK, DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK, "$250");

        LOGGER.info("REN-20650 Step#6 verification");
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_YES);
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.NUMBER_OF_GRADED_YEARS).setValue("4");

        LOGGER.info("REN-20650 Step#7 verification");
        setDentalDedSectionAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, "$250");
        setDentalDedSectionAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK, "$250");
        setDentalDedSectionAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, "$250");
        setDentalDedSectionAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK, "$250");

        LOGGER.info("REN-20650 Step#9 verification");
        policyInformationTab.navigateToTab();
        RetryService.run(predicate -> policyInformationTab.getAssetList().getAsset(SITUS_STATE).isOptionPresent("OK"),
                () -> {
                    policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("OK");
                    return null;
                }, StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(ALACARTE));

        dentalDedAssetList.getAsset(DentalDeductibleMetaData.IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_NO);
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_OUT_OF_NETWORK, "$25", "$75", "3x");

        LOGGER.info("REN-20650 Step#10 verification");
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE).setValue(VALUE_YES);
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE_IN_NETWORK, DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK, "$25", "$75", "3x");

        LOGGER.info("REN-20650 Step#12 verification");
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_YES);
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.NUMBER_OF_GRADED_YEARS).setValue("4");
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, "$0", "$250", "3x");
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK, "$0", "$250", "3x");
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, "$0", "$250", "3x");
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK, "$0", "$250", "3x");

        LOGGER.info("REN-20273 Steps#1, 2 verification");
        changeSitusStateAndSetPlan("NC", "$75", "2x");

        LOGGER.info("REN-20273 Step#3 verification");
        setDentalDedSectionAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_OUT_OF_NETWORK, "$300");

        LOGGER.info("REN-20273 Step#4 verification");
        setDentalDedSectionAndCheckMessage(DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE_IN_NETWORK, DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK, "$300");

        LOGGER.info("REN-20273 Step#6 verification");
        deductibleSectionVerificationWithGradedYears("$75", "2x");

        LOGGER.info("REN-20273 Step#7 verification");
        setDentalDedSectionAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, "$300");
        setDentalDedSectionAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK, "$300");
        setDentalDedSectionAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, "$300");
        setDentalDedSectionAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK, "$300");

        LOGGER.info("REN-20273 Steps#9, 10 verification");
        changeSitusStateAndSetPlan("OK", "$100", "3x");

        LOGGER.info("REN-20273 Step#12 verification");
        deductibleSectionVerificationWithGradedYears("$100", "3x");
    }

    private void policyInitiationNotASOWithSitusStateAndPlan(String situsState) {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        initiateQuoteAndFillUpToTab(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASO_PLAN.getLabel()), VALUE_NO)
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), situsState), PlanDefinitionTab.class, false);
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(ALACARTE));
    }

    private void changeSitusStateAndSetPlan(String situsState, String amountOut, String value) {
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get(), Tab.doubleWaiter);
        policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(situsState);
        policyInformationTab.getAssetList().getAsset(ASO_PLAN).setValue(VALUE_YES);
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(ASOALC));

        dentalDedAssetList.getAsset(DentalDeductibleMetaData.IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_NO);
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_OUT_OF_NETWORK, "$25", amountOut, value);

        dentalDedAssetList.getAsset(DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE).setValue(VALUE_YES);
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE_IN_NETWORK, DentalDeductibleMetaData.LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK, "$25", amountOut, value);
    }

    private void setSectionValuesAndCheckMessage(AssetDescriptor<ComboBox> inNetwork, AssetDescriptor<ComboBox> outOfNetwork, String amount, AssetList assetlist) {
        assetlist.getAsset(inNetwork).setValue(amount);
        assetlist.getAsset(outOfNetwork).setValue(amount);
        assertThat(assetlist.getAsset(outOfNetwork)).hasNoWarning();
        assertThat(assetlist.getAsset(inNetwork)).hasNoWarning();
    }

    private void setDentalDedValuesAndCheckMessage(AssetDescriptor<ComboBox> inNetwork, AssetDescriptor<ComboBox> outOfNetwork, String amountIn, String amountOut, String value) {
        dentalDedAssetList.getAsset(inNetwork).setValue(amountIn);
        dentalDedAssetList.getAsset(outOfNetwork).setValue(amountOut);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR
                .format(String.format("State Requirement: %s must be less than or equal to %s %s.", outOfNetwork.getLabel(), value, inNetwork.getLabel())))).isAbsent();
    }

    private void setDentalDedSectionAndCheckMessage(AssetDescriptor<ComboBox> inNetwork, AssetDescriptor<ComboBox> outOfNetwork, String amountOut) {
        dentalDedAssetList.getAsset(inNetwork).setValue("$0");
        dentalDedAssetList.getAsset(outOfNetwork).setValue(amountOut);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR
                .format(String.format("State Requirement: If %s = $0, then %s must be less than or equal to $250.", outOfNetwork.getLabel(), inNetwork.getLabel())))).isAbsent();
    }

    private void deductibleSectionVerificationWithGradedYears(String amountOut, String value) {
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_YES);
        dentalDedAssetList.getAsset(DentalDeductibleMetaData.NUMBER_OF_GRADED_YEARS).setValue("4");
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_FIRST_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK, "$25", amountOut, value);
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_SECOND_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK, "$25", amountOut, value);
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_THIRD_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK, "$25", amountOut, value);
        setDentalDedValuesAndCheckMessage(DentalDeductibleMetaData.DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK, DentalDeductibleMetaData.DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK, "$25", amountOut, value);
    }
}
