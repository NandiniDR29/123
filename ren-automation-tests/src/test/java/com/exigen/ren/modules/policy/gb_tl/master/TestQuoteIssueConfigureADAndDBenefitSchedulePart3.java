/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteIssueConfigureADAndDBenefitSchedulePart3 extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    private static AssetList spouseTrainingBenefit;
    private static AssetList adaptiveHomeVehicleBenefit;
    private static AssetList rehabilitationBenefitAmount;
    private static AssetList bereavementBenefit;

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-14846", "REN-14847", "REN-14848", "REN-14849"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteIssueConfigureADAndDBenefitSchedulePart3() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PlanDefinitionTab.class);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(BASIC_LIFE_PLAN));

        LOGGER.info("Add coverages");
        ImmutableList.of(DEP_ADD, VOL_BTL, VOL_ADD, DEP_VOL_ADD).forEach(coverage ->
                planDefinitionTab.addCoverage(BASIC_LIFE_PLAN, coverage));

        ImmutableList.of(ADD, DEP_ADD, VOL_ADD, DEP_VOL_ADD).forEach(coverage -> {
            LOGGER.info("---=={Step 1}==---");
            PlanDefinitionTab.changeCoverageTo(coverage);

            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME).setValue(coverage);

            stepsForSpouseTrainingBenefit();
            stepsForAdaptiveHomeVehicleBenefit();
            stepsForRehabilitationBenefitAmount();
            stepsForBereavementBenefit();
        });

        LOGGER.info("Add coverages");
        ImmutableList.of(SP_VOL_BTL, DEP_VOL_BTL).forEach(coverage ->
                planDefinitionTab.addCoverage(BASIC_LIFE_PLAN, coverage));
        ImmutableList.of(BTL, DEP_BTL, VOL_BTL, SP_VOL_BTL, DEP_VOL_BTL).forEach(coverage -> {

            LOGGER.info("---=={Step 7}==---");
            PlanDefinitionTab.changeCoverageTo(coverage);

            //REN-14846
            assertSoftly(softly -> softly.assertThat(spouseTrainingBenefit).isAbsent());

            //REN-14847
            assertSoftly(softly -> softly.assertThat(adaptiveHomeVehicleBenefit).isAbsent());

            //REN-14848
            assertSoftly(softly -> softly.assertThat(rehabilitationBenefitAmount).isAbsent());

            //REN-14849
            assertSoftly(softly -> softly.assertThat(bereavementBenefit).isAbsent());
        });
    }

    private void stepsForSpouseTrainingBenefit() {
        assertSoftly(softly -> {

            spouseTrainingBenefit = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.SPOUSE_TRAINING_BENEFIT);

            LOGGER.info("---=={Step 2 REN-14846}==---");
            planDefinitionTab.getAssetList().getAsset(ENHANCED_AD_D).setValue("No");
            softly.assertThat(spouseTrainingBenefit).isAbsent();

            LOGGER.info("---=={Step 3 REN-14846}==---");
            planDefinitionTab.getAssetList().getAsset(ENHANCED_AD_D).setValue("Yes");
            softly.assertThat(spouseTrainingBenefit).isPresent();

            LOGGER.info("---=={Step 4 REN-14846}==---");
            spouseTrainingBenefit.getAsset(SpouseTrainingBenefitMetaData.APPLY_BENEFIT).setValue(false);
            softly.assertThat(spouseTrainingBenefit.getAsset(SpouseTrainingBenefitMetaData.BENEFIT_PERCENTAGE)).isAbsent();
            softly.assertThat(spouseTrainingBenefit.getAsset(SpouseTrainingBenefitMetaData.MAXIMUM_BENEFIT_AMOUNT)).isAbsent();
            softly.assertThat(spouseTrainingBenefit.getAsset(SpouseTrainingBenefitMetaData.ENROLLED_WITH_MONTHS)).isAbsent();

            LOGGER.info("---=={Step 5 REN-14846}==---");
            spouseTrainingBenefit.getAsset(SpouseTrainingBenefitMetaData.APPLY_BENEFIT).setValue(true);
            softly.assertThat(spouseTrainingBenefit.getAsset(SpouseTrainingBenefitMetaData.BENEFIT_PERCENTAGE)).isPresent().isEnabled().isRequired().hasValue("5%");
            softly.assertThat(spouseTrainingBenefit.getAsset(SpouseTrainingBenefitMetaData.MAXIMUM_BENEFIT_AMOUNT)).isPresent().isEnabled().isRequired().hasValue("$5,000.00");
            softly.assertThat(spouseTrainingBenefit.getAsset(SpouseTrainingBenefitMetaData.ENROLLED_WITH_MONTHS)).isPresent().isEnabled().isRequired().hasValue("12 Months").hasOptions("12 Months", "24 Months");
        });
    }

    private void stepsForAdaptiveHomeVehicleBenefit() {
        assertSoftly(softly -> {

            LOGGER.info("---=={Step 2 REN-14847}==---");
            adaptiveHomeVehicleBenefit = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ADAPTIVE_HOME_VEHICLE_BENEFIT);
            softly.assertThat(adaptiveHomeVehicleBenefit).isPresent();

            LOGGER.info("---=={Step 3 REN-14847}==---");
            adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.APPLY_BENEFIT).setValue(false);
            softly.assertThat(adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.PERCENTAGE_AMOUNT)).isAbsent();
            softly.assertThat(adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.MAXIMUM_BENEFIT_AMOUNT)).isAbsent();
            softly.assertThat(adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.COVERED_LOSS_MUST_OCCUR_WITHIN_MONTHS)).isAbsent();

            LOGGER.info("---=={Step 4 REN-14847}==---");
            adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.APPLY_BENEFIT).setValue(true);
            softly.assertThat(adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.PERCENTAGE_AMOUNT)).isPresent().isEnabled().isRequired().hasValue("5%");
            softly.assertThat(adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.MAXIMUM_BENEFIT_AMOUNT)).isPresent().isEnabled().isRequired().hasValue("$5,000.00");
            softly.assertThat(adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.COVERED_LOSS_MUST_OCCUR_WITHIN_MONTHS)).isPresent().isEnabled().isRequired().hasValue("12");

            adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.PERCENTAGE_AMOUNT).setValue("");
            adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.MAXIMUM_BENEFIT_AMOUNT).setValue("");
            adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.COVERED_LOSS_MUST_OCCUR_WITHIN_MONTHS).setValue("");

            softly.assertThat(adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.PERCENTAGE_AMOUNT)).hasWarning();
            softly.assertThat(adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.MAXIMUM_BENEFIT_AMOUNT)).hasWarning();
            softly.assertThat(adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.COVERED_LOSS_MUST_OCCUR_WITHIN_MONTHS)).hasWarning();

            adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.PERCENTAGE_AMOUNT).setValue("5%");
            adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.MAXIMUM_BENEFIT_AMOUNT).setValue("$5,000.00");
            adaptiveHomeVehicleBenefit.getAsset(AdaptiveHomeVehicleBenefitMetaData.COVERED_LOSS_MUST_OCCUR_WITHIN_MONTHS).setValue("12");
        });
    }

    private void stepsForRehabilitationBenefitAmount() {
        assertSoftly(softly -> {

            LOGGER.info("---=={Step 2 REN-14848}==---");
            rehabilitationBenefitAmount = planDefinitionTab.getAssetList().getAsset(REHABILITATION_BENEFIT_AMOUNT);
            softly.assertThat(rehabilitationBenefitAmount).isPresent();

            LOGGER.info("---=={Step 3 REN-14848}==---");
            rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.APPLY_BENEFIT).setValue(false);
            softly.assertThat(rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.PERCENTAGE_AMOUNT)).isAbsent();
            softly.assertThat(rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.MAXIMUM_BENEFIT_AMOUNT)).isAbsent();
            softly.assertThat(rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.COVERED_LOSS_MUST_OCCUR_WITHIN_YEAR)).isAbsent();

            LOGGER.info("---=={Step 4 REN-14848}==---");
            rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.APPLY_BENEFIT).setValue(true);
            softly.assertThat(rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.PERCENTAGE_AMOUNT)).isPresent().isEnabled().isRequired().hasValue("5%");
            softly.assertThat(rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.MAXIMUM_BENEFIT_AMOUNT)).isPresent().isEnabled().isRequired().hasValue("$5,000.00");
            softly.assertThat(rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.COVERED_LOSS_MUST_OCCUR_WITHIN_YEAR)).isPresent().isEnabled().isRequired().hasValue("1");

            rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.PERCENTAGE_AMOUNT).setValue("");
            rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.MAXIMUM_BENEFIT_AMOUNT).setValue("");
            rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.COVERED_LOSS_MUST_OCCUR_WITHIN_YEAR).setValue("");

            softly.assertThat(rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.PERCENTAGE_AMOUNT)).hasWarning();
            softly.assertThat(rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.MAXIMUM_BENEFIT_AMOUNT)).hasWarning();
            softly.assertThat(rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.COVERED_LOSS_MUST_OCCUR_WITHIN_YEAR)).hasWarning();

            rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.PERCENTAGE_AMOUNT).setValue("5%");
            rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.MAXIMUM_BENEFIT_AMOUNT).setValue("$5,000.00");
            rehabilitationBenefitAmount.getAsset(RehabilitationBenefitAmountMetaData.COVERED_LOSS_MUST_OCCUR_WITHIN_YEAR).setValue("1");
        });
    }

    private void stepsForBereavementBenefit() {
        assertSoftly(softly -> {

            LOGGER.info("---=={Step 2 REN-14849}==---");
            bereavementBenefit = planDefinitionTab.getAssetList().getAsset(BEREAVEMENT_BENEFIT);
            softly.assertThat(bereavementBenefit).isPresent();

            LOGGER.info("---=={Step 3 REN-14849}==---");
            bereavementBenefit.getAsset(BereavementBenefitMetaData.APPLY_BENEFIT).setValue(false);
            softly.assertThat(bereavementBenefit.getAsset(BereavementBenefitMetaData.BENEFIT_AMOUNT_PER_SESSION)).isAbsent();
            softly.assertThat(bereavementBenefit.getAsset(BereavementBenefitMetaData.NUMBER_OF_SESSIONS)).isAbsent();
            softly.assertThat(bereavementBenefit.getAsset(BereavementBenefitMetaData.DURATION_YEARS)).isAbsent();

            LOGGER.info("---=={Step 4 REN-14849}==---");
            bereavementBenefit.getAsset(BereavementBenefitMetaData.APPLY_BENEFIT).setValue(true);

            softly.assertThat(bereavementBenefit.getAsset(BereavementBenefitMetaData.NUMBER_OF_SESSIONS)).isPresent().isEnabled().isRequired().hasValue("5");
            bereavementBenefit.getAsset(BereavementBenefitMetaData.NUMBER_OF_SESSIONS).setValue("");
            softly.assertThat(bereavementBenefit.getAsset(BereavementBenefitMetaData.NUMBER_OF_SESSIONS)).hasWarning();
            bereavementBenefit.getAsset(BereavementBenefitMetaData.NUMBER_OF_SESSIONS).setValue("5");

            softly.assertThat(bereavementBenefit.getAsset(BereavementBenefitMetaData.BENEFIT_AMOUNT_PER_SESSION))
                    .isPresent().isEnabled().isRequired().hasValue("$100").hasOptions("$50", "$100", "$150", "$200");
            softly.assertThat(bereavementBenefit.getAsset(BereavementBenefitMetaData.DURATION_YEARS)).isPresent().isEnabled().isRequired()
                    .hasValue("1 year").hasOptions("1 year", "2 years", "3 years");
        });
    }
}