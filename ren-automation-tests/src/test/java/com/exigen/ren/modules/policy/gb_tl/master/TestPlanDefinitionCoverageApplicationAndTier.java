/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN_PLUS_VOLUNTARY;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.ClassificationManagementTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.REQUIRED_PARTICIPATION;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab.changeCoverageTo;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPlanDefinitionCoverageApplicationAndTier extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-26677", component = POLICY_GROUPBENEFITS)
    public void testAccidentalDeathDismembermentInsuranceLossScheduleNewFields() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PlanDefinitionTab.class);

        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASIC_LIFE_PLAN));
        planDefinitionTab.addCoverage(BASIC_LIFE_PLAN, DEP_ADD);

        validateCoverageApplicationAndTier(BASIC_LIFE_PLAN, DEP_ADD);
        validateCoverageApplicationAndTier(BASIC_LIFE_PLAN_PLUS_VOLUNTARY, DEP_VOL_ADD);
        validateCoverageApplicationAndTier(BASIC_LIFE_PLAN_PLUS_VOLUNTARY, DEP_BTL);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-26679", component = POLICY_GROUPBENEFITS)
    public void testVerifyCoverageTierAndTierfields() {

        mainApp().open();
        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());

        LOGGER.info("Step 1");
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PlanDefinitionTab.class);
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASIC_LIFE_PLAN_PLUS_VOLUNTARY));

        ImmutableList.of(ADD, SP_VOL_BTL, DEP_VOL_BTL).forEach(PlanDefinitionTab::removeCoverage);

        ImmutableMap<String, String> planDefinitionMap = new ImmutableMap.Builder<String, String>()
                .put(BTL, "BTL")
                .put(VOL_BTL, "VOL_BTL")
                .put(VOL_ADD, "VOL_ADD")
                .put(DEP_BTL, "DEP_BTL")
                .put(DEP_VOL_ADD, "DEP_VOL_ADD")
                .build();

        planDefinitionMap.forEach((coverage, tdKey) -> {
            changeCoverageTo(coverage);
            planDefinitionTab.fillTab(tdSpecific().getTestData(tdKey));
        });

        planDefinitionTab.addCoverage(BASIC_LIFE_PLAN_PLUS_VOLUNTARY, SP_VOL_BTL);
        planDefinitionTab.getAssetList().getAsset(REQUIRED_PARTICIPATION).setValue("25%");
        planDefinitionTab.submitTab();

        LOGGER.info("Step 2");
        classificationManagementMpTab.fillTab(tdSpecific().getTestData("TestData_ClassificationManagementTab"));

        ImmutableList.of(DEP_BTL, DEP_VOL_ADD).forEach(coverage -> {
            ClassificationManagementTab.coveragesTable.getRow(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName(), coverage).getCell(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName()).click();
            assertThat(classificationManagementMpTab.getAssetList().getAsset(TIER)).isPresent().isRequired().hasValue("Spouse and/or Child");
        });

        classificationManagementMpTab.submitTab();
        premiumSummaryTab.fillTab(getDefaultTLMasterPolicyData()).submitTab();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Step 3");
        termLifeInsuranceMasterPolicy.propose().start().getWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), ProposalActionTab.class);
        assertThat(ProposalActionTab.getTableCoveragePremium(policyNumber, DEP_BTL).getRow(1).getCell("Tier")).hasValue("Spouse + Child");
        assertThat(ProposalActionTab.getTableCoveragePremium(policyNumber, DEP_VOL_ADD).getRow(1).getCell("Tier")).hasValue("Spouse + Child");
    }

    private void validateCoverageApplicationAndTier(String plan, String coverage) {
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(plan));
        changeCoverageTo(coverage);

        LOGGER.info("Steps 1, 2");
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_APPLICATION)).isPresent().isRequired().hasOptions("Spouse Only", "Child Only", "Spouse and Child");

        LOGGER.info("Step 3");
        TestData tdPlanDefinition = tdSpecific().getTestData("PlanDefinition_REN_26677");

        if (coverage.equals(DEP_BTL)) {
            tdPlanDefinition.mask(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName(), PlanDefinitionTabMetaData.ENHANCED_AD_D.getLabel())).resolveLinks();
        }

        planDefinitionTab.fillTab(tdPlanDefinition).submitTab();

        LOGGER.info("Step 4");
        classificationManagementMpTab.getAssetList().getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        classificationManagementMpTab.getAssetList().getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
        assertThat(classificationManagementMpTab.getAssetList().getAsset(TIER)).isPresent().isRequired().hasValue("Spouse and/or Child");
    }
}