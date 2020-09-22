/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.ADD;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.BTL;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN;
import static com.exigen.ren.main.enums.ValueConstants.NOT_INCLUDED;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.ScheduleOfContinuationProvisionMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestValidateFieldsOnScheduleOfContinuationProvisions extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    private static final String TWELVE_MONTHS = "12 Months";
    private static final String TWENTY_MONTHS = "24 Months";
    private static List<String> LAYOFF_VALUES = new ArrayList<>(Arrays.asList("4 weeks", "5 weeks", "6 weeks", "7 weeks", "8 weeks", "9 weeks", "10 weeks", "11 weeks", "12 weeks", NOT_INCLUDED));
    private final AssetList assetListScheduleOfContinuationProvision = planDefinitionTab.getAssetList().getAsset(SCHEDULE_OF_CONTINUATION_PROVISION);
    private final MultiAssetList planDefinitionTabAssetList = (MultiAssetList) planDefinitionTab.getAssetList();

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-25093", "REN-25094"}, component = POLICY_GROUPBENEFITS)
    public void testValidateFieldsPlanDefinitionTabPolicyInfoScreen() {

        preconditions(getDefaultTLMasterPolicyData(), BTL);

        LOGGER.info("TEST: REN-25093 Step 1");
        assetListScheduleOfContinuationProvision.getAsset(LAYOFF).setValue(NOT_INCLUDED);
        assertThat(assetListScheduleOfContinuationProvision.getAsset(LAYOFF_DURATION)).isAbsent();

        LOGGER.info("TEST: REN-25093 Steps 3-5");
        assetListScheduleOfContinuationProvision.getAsset(LAYOFF).setValue("4 weeks");
        assertThat(assetListScheduleOfContinuationProvision.getAsset(LAYOFF_DURATION)).isPresent().isRequired().isEnabled().hasValue(TWELVE_MONTHS).hasOptions(ImmutableList.of("12 Months", "24 Months"));

        LOGGER.info("TEST: REN-25093 Step 6");
        assetListScheduleOfContinuationProvision.getAsset(LAYOFF_DURATION).setValue(TWENTY_MONTHS);

        LOGGER.info("TEST: REN-25093 Steps 7-9");
        assertThat(assetListScheduleOfContinuationProvision.getAsset(MILITARY_LEAVE_DURATION)).isPresent().isRequired().isEnabled().hasValue(TWELVE_MONTHS).hasOptions(ImmutableList.of("12 Months", "24 Months"));

        LOGGER.info("TEST: REN-25093 Step 10");
        assetListScheduleOfContinuationProvision.getAsset(MILITARY_LEAVE_DURATION).setValue(TWENTY_MONTHS);

        LOGGER.info("TEST: REN-25093 Steps 11-12");
        assertThat(assetListScheduleOfContinuationProvision.getAsset(SABBATICAL_DURATION)).isPresent().isRequired().isEnabled().hasValue("");

        LOGGER.info("TEST: REN-25093 Step 21");
        assetListScheduleOfContinuationProvision.getAsset(SABBATICAL_DURATION).setValue("60");

        LOGGER.info("TEST: REN-25094 Step 1");
        assertThat(assetListScheduleOfContinuationProvision.getAsset(LAYOFF)).isRequired().hasValue("4 weeks").containsAllOptions(LAYOFF_VALUES);

        LOGGER.info("TEST: REN-25094 Step 2");
        assetListScheduleOfContinuationProvision.getAsset(LAYOFF).setValue("5 weeks");

        LOGGER.info("TEST: REN-25094 Step 3");
        LAYOFF_VALUES.remove(NOT_INCLUDED);
        assertThat(assetListScheduleOfContinuationProvision.getAsset(MILITARY_LEAVE)).isRequired().hasValue("4 weeks").containsAllOptions(LAYOFF_VALUES);

        LOGGER.info("TEST: REN-25094 Step 4");
        assetListScheduleOfContinuationProvision.getAsset(MILITARY_LEAVE).setValue("5 weeks");

        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultTLMasterPolicyData(), planDefinitionTab.getClass(), premiumSummaryTab.getClass(), true);
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
    }


    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-26281", component = POLICY_GROUPBENEFITS)
    public void testValidateMortgageBenAndHospitalInpStayBenSectionSitusStateNotNY() {

        TestData tdPolicy = getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "ME")
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel()));

        preconditions(tdPolicy, ADD);

        LOGGER.info("TEST: Step 1");
        assertThat(planDefinitionTabAssetList.getAsset(MORTGAGE_BENEFIT)).isPresent();

        LOGGER.info("TEST: Step 2");
        assertThat(planDefinitionTabAssetList.getAsset(HOSPITAL_INPATIENT_STAY_BENEFIT)).isPresent();
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-26281", component = POLICY_GROUPBENEFITS)
    public void testValidateMortgageBenAndHospitalInpStayBenSectionSitusStateNY() {

        preconditions(getDefaultTLMasterPolicyData(), ADD);

        LOGGER.info("TEST: Step 1");
        assertThat(planDefinitionTabAssetList.getAsset(MORTGAGE_BENEFIT)).isPresent(false);

        LOGGER.info("TEST: Step 2");
        assertThat(planDefinitionTabAssetList.getAsset(HOSPITAL_INPATIENT_STAY_BENEFIT)).isPresent();
    }

    private void preconditions(TestData tdPolicy, String coverage) {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(tdPolicy, PlanDefinitionTab.class);
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASIC_LIFE_PLAN));
        PlanDefinitionTab.changeCoverageTo(coverage);
    }
}