package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.CaseProfileConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.QuotesSelectionActionTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.USER_10_LOGIN;
import static com.exigen.ren.common.enums.UsersConsts.USER_QA_QA_LOGIN;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.ValueConstants.*;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.CopayMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.FrequencyMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.PlanBenefitsMetadata.CONTACT_LENSES_ALLOWANCE_UP_TO;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.PlanBenefitsMetadata.FRAMES_ALLOWANCE_UP_TO;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.SpecialPlanFeaturesMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.RATE_GUARANTEE_MONTHS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigureBenefitSchedule extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    private static final ImmutableList<String> EXAM_MATERIALS_LIST = ImmutableList.of("$0 combined", "$0/$5", "$0/$10", "$0/$15", "$0/$20", "$0/$25", "$5 combined", "$5/$5", "$5/$10", "$5/$15", "$5/$20", "$5/$25", "$10 combined", "$10/$10", "$10/$15", "$10/$20", "$10/$25", "$15 combined", "$15/$15", "$15/$20", "$15/$25", "$20 combined", "$20/$20", "$20/$25", "$25 combined", "$25/$25");
    private static final ImmutableList<String> MEDICALLY_NECESSARY_CONTACT_LENSES_LIST = ImmutableList.of("$0", "$5", "$10", "$15", "$20", "$25", "$5 combined", "$5", "$10", "$15", "$20", "$25", "$10 combined", "$10", "$15", "$20", "$25", "$15 combined", "$15", "$20", "$25", "$20 combined", "$20", "$25", "$25 combined", "$25");
    private static final ImmutableList<String> EXAM_LENSES_FRAME_LIST = ImmutableList.of(EMPTY, "Plan A (12/24/24)", "Plan B (12/12/24)", "Plan C (12/12/12)", "Plan D (24/24/24)");
    private static final ImmutableList<String> CONTACTS_LIST = ImmutableList.of("12", "24");
    private static final String WARNING_MESSAGE = "Frame frequency does not match Rate Guarantee";
    private static final String PROGRESSIVE_LENSES_WARNING = "Proposal will require Underwriter approval because Master Quote contains Progressive Lenses in Full.";
    private static final String PROPOSAL_ERROR = "Proposal will require Underwriter approval because Master Quote contains Progressive Lenses in Full";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18943", "REN-18441", "REN-18859"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureBenefitScheduleWithUser1() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass());
        planDefinitionTab.selectDefaultPlan();
        assertSoftly(softly -> {
            LOGGER.info("Test REN-18943 Step 1, REN-18441 Step 1");
            ImmutableList.of(COPAY, PLAN_BENEFITS, FREQUENCY, SPECIAL_PLAN_FEATURES).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(control)).isPresent());

            LOGGER.info("Test REN-18943 Step 2, REN-18441 Step 2");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(PROGRESSIVE_LENSES_IN_FULL)).isPresent().hasValue(VALUE_NO);
            ImmutableList.of("Eyeglasses (Frames/Lenses)", "Wellness Exam", "Contact Lenses").forEach(labels ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(labels))).isAbsent());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(EXAM_MATERIALS)).isPresent().hasOptions(EXAM_MATERIALS_LIST);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(MEDICALLY_NECESSARY_CONTACT_LENSES)).isPresent().isDisabled();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(LENS_ENHANCEMENTS_STANDARD)).isPresent().isRequired().isDisabled().hasValue("$55");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(LENS_ENHANCEMENTS_PREMIUM)).isPresent().isRequired().isDisabled().hasValue("$95 - $105");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(LENS_ENHANCEMENTS_CUSTOM)).isPresent().isRequired().isDisabled().hasValue("$150 - $175");
            planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(PROGRESSIVE_LENSES_IN_FULL).setValue(VALUE_YES);
            ImmutableList.of(LENS_ENHANCEMENTS_STANDARD, LENS_ENHANCEMENTS_PREMIUM, LENS_ENHANCEMENTS_CUSTOM).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(control)).isAbsent());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(PROGRESSIVE_LENSES_IN_FULL)).hasWarningWithText(PROGRESSIVE_LENSES_WARNING);

            LOGGER.info("Test REN-18441 Step 3");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Lenses - Allowance up to"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Out of Network"))).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN_BENEFITS).getAsset(FRAMES_ALLOWANCE_UP_TO)).isPresent().hasOptions("$120", "$130", "$150", "$180", "$200");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN_BENEFITS).getAsset(CONTACT_LENSES_ALLOWANCE_UP_TO)).isPresent().hasOptions("Same as Frames");

            LOGGER.info("Test REN-18441 Step 4");
            ImmutableList.of("Exam", "Frame", "Lenses").forEach(control ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(control))).isAbsent());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(PLAN_LIMITATION)).isPresent().hasOptions("Customer can choose either Eyeglasses or Contacts");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(FREQUENCY_DEFINITION)).isPresent().isOptional().hasOptions(StringUtils.EMPTY, "Calendar Year", "Service Year");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME)).isPresent().isRequired().hasOptions(EXAM_LENSES_FRAME_LIST);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(CONTACTS)).isPresent().isRequired().hasOptions(CONTACTS_LIST);

            LOGGER.info("Test REN-18441 Step 5");
            planDefinitionTab.getAssetList().getAsset(SPECIAL_PLAN_FEATURES).isPresent();
            ImmutableList.of("General Definitions", "Adult Minimum Wage - Primary Insured", "Standard Child Age Limit", "Non-Standard Child Age Limit (e.g. F/T student)").forEach(control ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(control))).isAbsent());
            ImmutableList.of(PHOTOCHROMIC_LENSES_FACTOR, SCRATCH_COATING_FACTOR, SAFETY_GLASSES_FACTOR).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPECIAL_PLAN_FEATURES).getAsset(control)).isPresent().isOptional());

            LOGGER.info("Test REN-18859 Step 1");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS).setValue("12");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());

            LOGGER.info("Test REN-18859 Step 2");
            planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(PROGRESSIVE_LENSES_IN_FULL).setValue(VALUE_NO);
            planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(PROGRESSIVE_LENSES_IN_FULL).setValue(VALUE_YES);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(PROGRESSIVE_LENSES_IN_FULL)).hasWarningWithText(PROGRESSIVE_LENSES_WARNING);
            ImmutableList.of(LENS_ENHANCEMENTS_STANDARD, LENS_ENHANCEMENTS_PREMIUM, LENS_ENHANCEMENTS_CUSTOM).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(control)).isAbsent());
            LOGGER.info("Verify Medically Necessary Contact Lenses : available values are depend on the value of 'Exam / Materials'");
            EXAM_MATERIALS_LIST.forEach(value -> {
                        planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(EXAM_MATERIALS).setValue(value);
                        softly.assertThat(planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(MEDICALLY_NECESSARY_CONTACT_LENSES)).hasOptions(StringUtils.EMPTY, MEDICALLY_NECESSARY_CONTACT_LENSES_LIST.get(EXAM_MATERIALS_LIST.indexOf(value)));
                    }
            );

            LOGGER.info("Test REN-18859 Step 5");
            planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME).setValue("Plan C (12/12/12)");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME)).hasNoWarning();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(CONTACTS)).hasValue("12");
            planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME).setValue("Plan B (12/12/24)");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME)).hasWarningWithText(WARNING_MESSAGE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(CONTACTS)).hasValue("12");
            ImmutableList.of("Plan A (12/24/24)", "Plan D (24/24/24)").forEach(control -> {
                planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME).setValue(control);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME)).hasWarningWithText(WARNING_MESSAGE);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(CONTACTS)).hasValue("24");
            });

            LOGGER.info("Test REN-18859 Step 6");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS).setValue("24");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME)).hasNoWarning();
            ImmutableList.of("Plan A (12/24/24)", "Plan B (12/12/24)", "Plan D (24/24/24)").forEach(control -> {
                planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME).setValue(control);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME)).hasNoWarning();
            });
            planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME).setValue("Plan C (12/12/12)");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME)).hasWarningWithText(WARNING_MESSAGE);

            LOGGER.info("Test REN-18859 Step 8");
            groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass(), premiumSummaryTab.getClass(), true);
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
            ImmutableList.of("Plan B (12/12/24)", "Plan A (12/24/24)").forEach(control -> {
                groupVisionMasterPolicy.dataGather().start();
                NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
                planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME).setValue(control);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME)).hasNoWarning();
                premiumSummaryTab.navigate();
                premiumSummaryTab.submitTab();
                softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
            });

            LOGGER.info("Test REN-18859 Step 9");
            groupVisionMasterPolicy.dataGather().start();
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME).setValue("Plan C (12/12/12)");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME)).hasWarningWithText(WARNING_MESSAGE);
            premiumSummaryTab.navigate();
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
            groupVisionMasterPolicy.dataGather().start();
            policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS).setValue("12");
            premiumSummaryTab.navigate();
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            LOGGER.info("Test REN-18859 Step 10");
            ImmutableList.of("Plan A (12/24/24)", "Plan B (12/12/24)", "Plan D (24/24/24)").forEach(control -> {
                groupVisionMasterPolicy.dataGather().start();
                NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
                planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME).setValue(control);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(EXAM_LENSES_FRAME)).hasWarningWithText(WARNING_MESSAGE);
                softly.assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.DATA_GATHERING);
                premiumSummaryTab.navigate();
                premiumSummaryTab.submitTab();
                softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
            });

            LOGGER.info("Test REN-18859 Step 11");
            groupVisionMasterPolicy.propose().start();
            QuotesSelectionActionTab.selectQuote(ImmutableList.of(1));
            QuotesSelectionActionTab.textBoxProposalName.setValue("PreProposalName");
            Tab.buttonNext.click();
            softly.assertThat(ProposalActionTab.buttonOverrideRules).isEnabled();
            ProposalActionTab.buttonOverrideRules.click();
            softly.assertThat(tableErrorsList.getRow(1).getCell(TableConstants.OverrideErrorsTable.MESSAGE.getName())).hasValue("Proposal requires Underwriter approval because Master Quote contains Progress...");
            softly.assertThat(tableErrorsList.getRow(1).getCell(TableConstants.OverrideErrorsTable.OVERRIDE.getName()).controls.checkBoxes.getFirst()).isEnabled();
            tableErrorsList.getRow(1).getCell(TableConstants.OverrideErrorsTable.OVERRIDE.getName()).controls.checkBoxes.getFirst().setValue(true);
            tableErrorsList.getRow(1).getCell(TableConstants.OverrideErrorsTable.DURATION.getName()).controls.radioGroups.getFirst().setValue("Life");
            tableErrorsList.getRow(1).getCell(TableConstants.OverrideErrorsTable.REASON_FOR_OVERRIDE.getName()).controls.comboBoxes.getFirst().setValue("Customer Satisfaction");
            ProposalActionTab.buttonOverride.click();
            ProposalActionTab.buttonCalculatePremium.click();
            softly.assertThat(buttonGeneratePreProposal).isEnabled();
            softly.assertThat(buttonGenerateProposal).isEnabled();
            buttonGeneratePreProposal.click();
            Page.dialogConfirmation.buttonYes.click();
            assertThat(CaseProfileSummaryPage.tableProposal.getRow(QuotesSelectionActionTabMetaData.PROPOSAL_NAME.getLabel(), "PreProposalName")
                    .getCell(CaseProfileConstants.CaseProfileProposalTable.PROPOSAL_STATUS)).hasValue(CaseProfileConstants.ProposalStatus.PROPOSED_PREMIUM_CALCULATED);
            CaseProfileSummaryPage.tableProposal.getRow(QuotesSelectionActionTabMetaData.PROPOSAL_NAME.getLabel(), "PreProposalName")
                    .getCell(CaseProfileConstants.CaseProfileProposalTable.ACTION).controls.links.getFirst().click();
            Tab.buttonNext.click();
            buttonGenerateProposal.click();
            ProposalActionTab.dialogProposal.confirm();
            assertThat(CaseProfileSummaryPage.tableProposal.getRow(QuotesSelectionActionTabMetaData.PROPOSAL_NAME.getLabel(), "PreProposalName")
                    .getCell(CaseProfileConstants.CaseProfileProposalTable.PROPOSAL_STATUS)).hasValue(CaseProfileConstants.ProposalStatus.GENERATED);
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18944"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureBenefitScheduleWithUser2() {
        mainApp().open(USER_10_LOGIN, USER_QA_QA_LOGIN);
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass());
        planDefinitionTab.selectDefaultPlan();
        assertSoftly(softly -> {
            LOGGER.info("Test REN-18944 Step 2");
            planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(PROGRESSIVE_LENSES_IN_FULL).setValue(VALUE_YES);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(PROGRESSIVE_LENSES_IN_FULL)).hasWarningWithText(PROGRESSIVE_LENSES_WARNING);
            ImmutableList.of(LENS_ENHANCEMENTS_STANDARD, LENS_ENHANCEMENTS_PREMIUM, LENS_ENHANCEMENTS_CUSTOM).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(control)).isAbsent());

            LOGGER.info("Test REN-18944 Step 3");
            ImmutableList.of(PHOTOCHROMIC_LENSES_FACTOR, SCRATCH_COATING_FACTOR, SAFETY_GLASSES_FACTOR).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPECIAL_PLAN_FEATURES).getAsset(control)).isPresent().isDisabled());

            LOGGER.info("Test REN-18944 Step 4");
            groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass(), premiumSummaryTab.getClass(), true);
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            String quoteNum = PolicySummaryPage.labelPolicyNumber.getValue();

            LOGGER.info("Test REN-18944 Step 5");
            groupVisionMasterPolicy.propose().start();
            QuotesSelectionActionTab.selectQuote(ImmutableList.of(1));
            QuotesSelectionActionTab.textBoxProposalName.setValue("ProposalName");
            Tab.buttonNext.click();
            ProposalActionTab.buttonCalculatePremium.click();
            ProposalActionTab.buttonGeneratePreProposal.click();
            Page.dialogConfirmation.buttonYes.click();
            softly.assertThat(ErrorPage.tableError).hasRowsThatContain(0, ImmutableMap.of(ErrorPage.TableError.DESCRIPTION.getName(), PROPOSAL_ERROR));
            Tab.buttonBack.click();
            groupVisionMasterPolicy.propose().start();
            QuotesSelectionActionTab.selectQuote(ImmutableList.of(1));
            QuotesSelectionActionTab.textBoxProposalName.setValue("ProposalName");
            Tab.buttonNext.click();
            ProposalActionTab.buttonCalculatePremium.click();
            ProposalActionTab.buttonGenerateProposal.click();
            Page.dialogConfirmation.confirm();
            softly.assertThat(ErrorPage.tableError).hasRowsThatContain(0, ImmutableMap.of(ErrorPage.TableError.DESCRIPTION.getName(), PROPOSAL_ERROR));
            Tab.buttonBack.click();

            LOGGER.info("Test REN-18944 Step 6");
            MainPage.QuickSearch.search(quoteNum);
            groupVisionMasterPolicy.dataGather().start();
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(PROGRESSIVE_LENSES_IN_FULL).setValue(VALUE_NO);
            ImmutableList.of(LENS_ENHANCEMENTS_STANDARD, LENS_ENHANCEMENTS_PREMIUM, LENS_ENHANCEMENTS_CUSTOM).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(COPAY).getAsset(control)).isPresent());
            premiumSummaryTab.navigate();
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).valueContains(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            groupVisionMasterPolicy.propose().perform(getDefaultVSMasterPolicyData());
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

            LOGGER.info("Test REN-18944 Step 7");
            groupVisionMasterPolicy.acceptContract().perform(getDefaultVSMasterPolicyData());
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);
            groupVisionMasterPolicy.issue().perform(getDefaultVSMasterPolicyData());

            groupVisionMasterPolicy.endorse().start();
            groupVisionMasterPolicy.endorse().getWorkspace().fill(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
            Tab.buttonOk.click();
            Page.dialogConfirmation.confirm();
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            ImmutableList.of(PHOTOCHROMIC_LENSES_FACTOR, SCRATCH_COATING_FACTOR, SAFETY_GLASSES_FACTOR).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPECIAL_PLAN_FEATURES).getAsset(control)).isPresent().isDisabled());
        });
    }
}
