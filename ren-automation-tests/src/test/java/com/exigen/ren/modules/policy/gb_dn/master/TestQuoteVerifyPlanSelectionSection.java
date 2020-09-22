/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN_NAME;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteVerifyPlanSelectionSection extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private final static String PLAN_ALACARTE = "ALACARTE-A La Carte";
    private final static String PLAN_BASEPOS = "BASEPOS-Basic EPOS";
    private final static String PLAN_MAJEPOS = "MAJEPOS-Major EPOS";
    private final static String PLAN_FLEX_PLUS = "FLEX-Flex Plus";
    private final static String PLAN_GRAD_GRADUATED = "GRAD-Graduated";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-14964", component = POLICY_GROUPBENEFITS)
    public void testQuoteVerifyPlanSelectionSection() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "WA")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel()), "100"), PlanDefinitionTab.class);
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASEPOS, MAJEPOS, FLEX_PLUS, GRADUATED, ALACARTE));
        assertSoftly(softly -> {
            softly.assertThat(PlanDefinitionTab.planTable.getRow(TableConstants.CoverageDefinition.PLAN.getName(), PLAN_BASEPOS)).isPresent();
            softly.assertThat(PlanDefinitionTab.planTable.getRow(TableConstants.CoverageDefinition.PLAN.getName(), PLAN_MAJEPOS)).isPresent();
            softly.assertThat(PlanDefinitionTab.planTable.getRow(TableConstants.CoverageDefinition.PLAN.getName(), PLAN_FLEX_PLUS)).isPresent();
            softly.assertThat(PlanDefinitionTab.planTable.getRow(TableConstants.CoverageDefinition.PLAN.getName(), PLAN_GRAD_GRADUATED)).isPresent();
            softly.assertThat(PlanDefinitionTab.planTable.getRow(TableConstants.CoverageDefinition.PLAN.getName(), PLAN_ALACARTE)).isPresent();

            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT.get());

            softly.assertThat(ClassificationManagementTab.coveragesTable.getRow(ClassificationManagementTab.PlanSelection.PLAN.getName(), PLAN_BASEPOS)).isPresent();
            softly.assertThat(ClassificationManagementTab.coveragesTable.getRow(ClassificationManagementTab.PlanSelection.PLAN.getName(), PLAN_MAJEPOS)).isPresent();
            softly.assertThat(ClassificationManagementTab.coveragesTable.getRow(ClassificationManagementTab.PlanSelection.PLAN.getName(), PLAN_FLEX_PLUS)).isPresent();
            softly.assertThat(ClassificationManagementTab.coveragesTable.getRow(ClassificationManagementTab.PlanSelection.PLAN.getName(), PLAN_GRAD_GRADUATED)).isPresent();
            softly.assertThat(ClassificationManagementTab.coveragesTable.getRow(ClassificationManagementTab.PlanSelection.PLAN.getName(), PLAN_ALACARTE)).isPresent();

            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());

            PlanDefinitionTab.openAddedPlan(PLAN_BASEPOS);
            planDefinitionTab.getAssetList().getAsset(PLAN_NAME).setValue("Test1");
            String baseposPlanName = String.format("%s-%s", "BASEPOS", "Test1");
            softly.assertThat(PlanDefinitionTab.planTable.getRow(TableConstants.CoverageDefinition.PLAN.getName(), baseposPlanName)).isPresent();

            PlanDefinitionTab.openAddedPlan(PLAN_MAJEPOS);
            planDefinitionTab.getAssetList().getAsset(PLAN_NAME).setValue("Test2");
            String majeposPlanName = String.format("%s-%s", "MAJEPOS", "Test2");
            softly.assertThat(PlanDefinitionTab.planTable.getRow(TableConstants.CoverageDefinition.PLAN.getName(), majeposPlanName)).isPresent();

            PlanDefinitionTab.openAddedPlan(PLAN_FLEX_PLUS);
            planDefinitionTab.getAssetList().getAsset(PLAN_NAME).setValue("Test3");
            String flexPlanName = String.format("%s-%s", "FLEX", "Test3");
            softly.assertThat(PlanDefinitionTab.planTable.getRow(TableConstants.CoverageDefinition.PLAN.getName(), flexPlanName)).isPresent();

            PlanDefinitionTab.openAddedPlan(PLAN_GRAD_GRADUATED);
            planDefinitionTab.getAssetList().getAsset(PLAN_NAME).setValue("Test4");
            String gradPlanName = String.format("%s-%s", "GRAD", "Test4");
            softly.assertThat(PlanDefinitionTab.planTable.getRow(TableConstants.CoverageDefinition.PLAN.getName(), gradPlanName)).isPresent();

            PlanDefinitionTab.openAddedPlan(PLAN_ALACARTE);
            planDefinitionTab.getAssetList().getAsset(PLAN_NAME).setValue("Test5");
            String alacartePlanName = String.format("%s-%s", "ALACARTE", "Test5");
            softly.assertThat(PlanDefinitionTab.planTable.getRow(TableConstants.CoverageDefinition.PLAN.getName(), alacartePlanName)).isPresent();

            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT.get());
            softly.assertThat(ClassificationManagementTab.coveragesTable.getRow(ClassificationManagementTab.PlanSelection.PLAN.getName(), baseposPlanName)).isPresent();
            softly.assertThat(ClassificationManagementTab.coveragesTable.getRow(ClassificationManagementTab.PlanSelection.PLAN.getName(), majeposPlanName)).isPresent();
            softly.assertThat(ClassificationManagementTab.coveragesTable.getRow(ClassificationManagementTab.PlanSelection.PLAN.getName(), flexPlanName)).isPresent();
            softly.assertThat(ClassificationManagementTab.coveragesTable.getRow(ClassificationManagementTab.PlanSelection.PLAN.getName(), gradPlanName)).isPresent();
            softly.assertThat(ClassificationManagementTab.coveragesTable.getRow(ClassificationManagementTab.PlanSelection.PLAN.getName(), alacartePlanName)).isPresent();

            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            softly.assertThat(PlanDefinitionTab.planTable.getRow(TableConstants.CoverageDefinition.PLAN.getName(), baseposPlanName)).isPresent();
            softly.assertThat(PlanDefinitionTab.planTable.getRow(TableConstants.CoverageDefinition.PLAN.getName(), majeposPlanName)).isPresent();
            softly.assertThat(PlanDefinitionTab.planTable.getRow(TableConstants.CoverageDefinition.PLAN.getName(), flexPlanName)).isPresent();
            softly.assertThat(PlanDefinitionTab.planTable.getRow(TableConstants.CoverageDefinition.PLAN.getName(), gradPlanName)).isPresent();
            softly.assertThat(PlanDefinitionTab.planTable.getRow(TableConstants.CoverageDefinition.PLAN.getName(), alacartePlanName)).isPresent();
     });
    }
}
