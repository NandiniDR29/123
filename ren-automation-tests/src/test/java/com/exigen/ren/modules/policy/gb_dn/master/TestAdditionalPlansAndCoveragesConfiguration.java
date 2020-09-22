package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.USER_10_LOGIN;
import static com.exigen.ren.common.enums.UsersConsts.USER_QA_QA_LOGIN;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSelectionValues.*;
import static com.exigen.ren.main.enums.PolicyConstants.Situs_State_WA_Plans.*;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.tableErrorsList;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.CoInsuranceMetaData.IS_IT_GRADED_CO_INSURANCE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalDeductibleMetaData.DEDUCTIBLE_EXPENSE_PERIOD;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalDeductibleMetaData.IS_IT_GRADED_DENTAL_DEDUCTIBLE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalMaximumMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetaData.BENEFIT_PERIOD_BASED_ON_MASTER_POLICY;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.OrthodontiaMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.utils.CommonMethods.getRandomElementFromList;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAdditionalPlansAndCoveragesConfiguration extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static final ImmutableList WA_PLANS = ImmutableList.of(PLAN_WA2, PLAN_WA3, PLAN_WA4, PLAN_WA5, PLAN_WA6, PLAN_WA7, PLAN_WA8, PLAN_WA9);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-16439", "REN-16444", "REN-16445", "REN-16452", "REN-16547"}, component = POLICY_GROUPBENEFITS)
    public void testAdditionalPlansAndCoveragesConfiguration() {
        mainApp().open();
        LOGGER.info("REN-16439 Preconditions");

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData().adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), "WA")
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel())), PlanDefinitionTab.class);
        assertSoftly(softly -> {
            LOGGER.info("REN-16439 Step 1");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN).getAllValues())
                    .containsOnly(PLAN_WA1, PLAN_WA2, PLAN_WA3, PLAN_WA4, PLAN_WA5, PLAN_WA6, PLAN_WA7, PLAN_WA8, PLAN_WA9);

            LOGGER.info("REN-16547 Step 4");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(PLAN_WA1));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(MAXIMUM_ROLL_OVER)).isDisabled();

            LOGGER.info("REN-16547 Step 5");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of((String) getRandomElementFromList(WA_PLANS)));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(MAXIMUM_ROLL_OVER)).isDisabled();

            LOGGER.info("REN-16439 Step 2");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("51");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN).getAllValues())
                    .containsOnly(ALACARTE, BASEPOS, MAJEPOS, FLEX_PLUS, GRADUATED);
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(GRADUATED));

            LOGGER.info("REN-16444 Step 1 REN-16445 Step 13");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(BENEFIT_PERIOD_BASED_ON_MASTER_POLICY)).isRequired().isPresent().isDisabled().hasValue(VALUE_NO);

            LOGGER.info("REN-16445 Step 1");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(FLEX_PLUS));
            planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(MAXIMUM_EXPENSE_PERIOD).setValue("Benefit Year");
            planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE).getAsset(DEDUCTIBLE_EXPENSE_PERIOD).setValue("Benefit Year");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(BENEFIT_PERIOD_BASED_ON_MASTER_POLICY)).isRequired().isPresent().isDisabled().hasValue(VALUE_YES);

            LOGGER.info("REN-16445 Step 2");
            planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(MAXIMUM_EXPENSE_PERIOD).setValue("Calendar Year");
            planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE).getAsset(DEDUCTIBLE_EXPENSE_PERIOD).setValue("Calendar Year");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(BENEFIT_PERIOD_BASED_ON_MASTER_POLICY)).isAbsent();

            LOGGER.info("REN-16547 Step 2");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(IS_IT_GRADED_DENTAL_MAXIMUM)).isDisabled();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(IS_IT_GRADED_CO_INSURANCE)).isDisabled();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE).getAsset(IS_IT_GRADED_DENTAL_DEDUCTIBLE)).isDisabled();
            planDefinitionTab.getAssetList().getAsset(ORTHODONTIA).getAsset(ORTHO_COVERAGE).setValue(VALUE_YES);
            ImmutableList.of(LIFETIME_DEDUCTIBLE, IS_IT_GRADED_ORTHODONTIA, YEARLY_MAXIMUM).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(ORTHODONTIA).getAsset(control)).isDisabled());

            LOGGER.info("REN-16547 Step 3");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(MAXIMUM_ROLL_OVER)).isEnabled();

            LOGGER.info("REN-16445 Step 8 Precondition");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(ALACARTE));

            LOGGER.info("REN-16547 Step 1");
            ImmutableList.of(
                    planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(IS_IT_GRADED_DENTAL_MAXIMUM),
                    planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(MAXIMUM_ROLL_OVER),
                    planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(IS_IT_GRADED_CO_INSURANCE)
            ).forEach(control ->
                    softly.assertThat(control).isEnabled());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE).getAsset(IS_IT_GRADED_DENTAL_DEDUCTIBLE)).isEnabled();
            ImmutableList.of(LIFETIME_DEDUCTIBLE, IS_IT_GRADED_ORTHODONTIA, YEARLY_MAXIMUM).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(ORTHODONTIA).getAsset(LIFETIME_DEDUCTIBLE)).isEnabled());

            LOGGER.info("REN-16445 Step 8 AlaCarte");
            verifyBenefitPeriodBasedOnMasterPolicyField();

            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(ASO_PLAN).setValue(VALUE_YES);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());

            LOGGER.info("REN-16445 Step 8 ASO");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(ASO));
            verifyBenefitPeriodBasedOnMasterPolicyField();

            LOGGER.info("REN-16445 Step 8 ASOALC");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(ASOALC));
            verifyBenefitPeriodBasedOnMasterPolicyField();

            LOGGER.info("REN-16445 Step 9 Precondition");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(ASO_PLAN).setValue(VALUE_NO);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(ALACARTE));

            LOGGER.info("REN-16445 Step 9");
            planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(MAXIMUM_EXPENSE_PERIOD).setValue("Calendar Year");
            planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE).getAsset(DEDUCTIBLE_EXPENSE_PERIOD).setValue("Calendar Year");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(BENEFIT_PERIOD_BASED_ON_MASTER_POLICY)).isAbsent();

            LOGGER.info("REN-16452 Step 4 Login with User 2, repeat step 1");
            groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultDNMasterPolicyData(), PlanDefinitionTab.class, PremiumSummaryTab.class);
            premiumSummaryTab.submitTab();

            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
            LOGGER.info("REN-16452 Step 4 Login with User 2, repeat step 2");
            groupDentalMasterPolicy.propose().start();
            QuotesSelectionActionTab.selectQuote(ImmutableList.of(1));
            QuotesSelectionActionTab.textBoxProposalName.setValue("PreProposalName");
            Tab.buttonNext.click();
            softly.assertThat(ProposalActionTab.buttonOverrideRules).isEnabled();

            LOGGER.info("REN-16452 Step 4 Login with User 2, repeat step 3");
            ProposalActionTab.buttonOverrideRules.click();
            softly.assertThat(tableErrorsList.getRow(1).getCell(TableConstants.OverrideErrorsTable.MESSAGE.getName())).hasValue("Proposal with an A La Carte Plan requires Underwriter approval");
            softly.assertThat(tableErrorsList.getRow(1).getCell(TableConstants.OverrideErrorsTable.OVERRIDE.getName()).controls.checkBoxes.getFirst()).isEnabled();

            LOGGER.info("REN-16452 step 5");
            tableErrorsList.getRow(1).getCell(TableConstants.OverrideErrorsTable.OVERRIDE.getName()).controls.checkBoxes.getFirst().setValue(true);
            tableErrorsList.getRow(1).getCell(TableConstants.OverrideErrorsTable.DURATION.getName()).controls.radioGroups.getFirst().setValue("Life");
            tableErrorsList.getRow(1).getCell(TableConstants.OverrideErrorsTable.REASON_FOR_OVERRIDE.getName()).controls.comboBoxes.getFirst().setValue("Customer Satisfaction");
            ProposalActionTab.buttonOverride.click();
            Tab.buttonCancel.click();
            Page.dialogConfirmation.buttonYes.click();

            LOGGER.info("REN-16452 step 6,7 For Plan Grad");
            createQuoteAndCheckOverrideRules(GRADUATED, PLAN_GRAD_GRADUATED, 2);

            LOGGER.info("REN-16452 step 6,7 For Plan Flex");
            createQuoteAndCheckOverrideRules(FLEX_PLUS, PLAN_FLEX_PLUS, 3);

            LOGGER.info("REN-16452 step 6,7 For Plan MajePos");
            createQuoteAndCheckOverrideRules(MAJEPOS, PLAN_MAJEPOS, 4);

            LOGGER.info("REN-16452 step 6,7 For Plan BasePos");
            createQuoteAndCheckOverrideRules(BASEPOS, PLAN_BASEPOS, 5);
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-16452"}, component = POLICY_GROUPBENEFITS)
    public void testRulesVerificationForUserAuthLevelZero() {
        mainApp().reopen(USER_10_LOGIN, USER_QA_QA_LOGIN);

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        assertSoftly(softly -> {
            LOGGER.info("REN-16452 Step 1");
            groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData().mask(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.REQUIRED_PARTICIPATION_PCT.getLabel())));
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            LOGGER.info("REN-16452 Step 2");
            groupDentalMasterPolicy.propose().start();
            QuotesSelectionActionTab.selectQuote(ImmutableList.of(1));
            QuotesSelectionActionTab.textBoxProposalName.setValue("PreProposalName");

            Tab.buttonNext.click();
            softly.assertThat(ProposalActionTab.buttonOverrideRules).isEnabled();

            LOGGER.info("REN-16452 step 3");
            ProposalActionTab.buttonOverrideRules.click();
            softly.assertThat(tableErrorsList.getRow(1).getCell(TableConstants.OverrideErrorsTable.MESSAGE.getName())).hasValue("Proposal with an A La Carte Plan requires Underwriter approval");
            softly.assertThat(tableErrorsList.getRow(1).getCell(TableConstants.OverrideErrorsTable.OVERRIDE.getName()).controls.checkBoxes.getFirst()).isDisabled();
        });
    }

    private void createQuoteAndCheckOverrideRules(String Plan, String planKey, int selectQuoteIndex) {
        LOGGER.info("REN-16452 step 6 Create new Quote 2");
        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData().adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[0]", PLAN.getLabel()), Plan).adjust(TestData.makeKeyPath(ClassificationManagementTab.class.getSimpleName(), PLAN.getLabel()), planKey).mask(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[1]", PPO_EPO_PLAN.getLabel())));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("REN-16452 step 71 Create new Quote 2");
        groupDentalMasterPolicy.propose().start();
        QuotesSelectionActionTab.selectQuote(ImmutableList.of(selectQuoteIndex));
        QuotesSelectionActionTab.textBoxProposalName.setValue("PreProposalName");
        Tab.buttonNext.click();
        CustomAssertions.assertThat(ProposalActionTab.buttonOverrideRules).isDisabled();
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
    }

    private void verifyBenefitPeriodBasedOnMasterPolicyField() {
        planDefinitionTab.getAssetList().getAsset(DENTAL_MAXIMUM).getAsset(MAXIMUM_EXPENSE_PERIOD).setValue("Benefit Year");
        planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE).getAsset(DEDUCTIBLE_EXPENSE_PERIOD).setValue("Benefit Year");
        assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(BENEFIT_PERIOD_BASED_ON_MASTER_POLICY)).isRequired().isPresent().isEnabled().hasValue(VALUE_YES);
    }
}
