package com.exigen.ren.modules.policy.gb_eap.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.EmployeeAssistanceProgramMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PlanDefinitionIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.tabs.PlanDefinitionIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.TableConstants.CoverageRelationships.CLASS_NAME;
import static com.exigen.ren.main.enums.TableConstants.CoverageRelationships.CLASS_NUMBER;
import static com.exigen.ren.main.enums.ValueConstants.*;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.ClassificationManagementTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PlanDefinitionTabMetaData.CoverageIncludedInPackageMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PolicyInformationTabMetaData.AssignedAgenciesMetaData.PRIMARY_AGENCY;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PolicyInformationTabMetaData.InternalTeamMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PremiumSummaryTabMetaData.RATE_SECTION;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PremiumSummaryTabMetaData.RateMetaData.RATE_REQUEST_DATE;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PremiumSummaryTabMetaData.RateMetaData.RATING_FORMULA;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.tabs.ClassificationManagementTab.tableCoverageRelationships;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.tabs.ClassificationManagementTab.tablePlansAndCoverages;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPresentsOfModifiedFieldsOnAllTabs extends BaseTest implements CustomerContext, CaseProfileContext, EmployeeAssistanceProgramMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_EAP, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-41499", "REN-42379", "REN-42558"}, component = POLICY_GROUPBENEFITS)
    public void testPresentsOfModifiedFieldsOnPolicyInformationAndPlanDefinitionTabs() {
        mainApp().open();
        customerNonIndividual.createViaUI(getDefaultCustomerNonIndividualTestData().adjust(tdSpecific().getTestData("Adjustment_Relationship").resolveLinks()));
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile"), employeeAssistanceProgramMasterPolicy.getType());
        TestData tdPolicy = getDefaultEAPMasterPolicyData().adjust(tdSpecific().getTestData("TestData_Policy").resolveLinks())
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), GROUP_IS_MEMBER_COMPANY.getLabel()), "Yes")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), MEMBER_COMPANY_NAME.getLabel()), "index=1");
        employeeAssistanceProgramMasterPolicy.initiate(tdPolicy);

        LOGGER.info("REN-41499 Step 5");
        assertThat(policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS)).hasValue(ValueConstants.EMPTY);

        LOGGER.info("REN-41499 Step 1");
        policyInformationTab.fillTab(tdPolicy);
        assertSoftly(softly -> {
            ImmutableList.of(QUOTE_CREATION_DATE, QUOTE_EXPIRATION_DATE, UNDEWRITING_COMPANY, GROUP_IS_MEMBER_COMPANY, MEMBER_COMPANY_NAME, POLICY_EFFECTIVE_DATE,
                    CURRENT_POLICY_YEAR_START_DATE, NEXT_POLICY_YEAR_START_DATE, RATE_GUARANTEE_MONTHS, NEXT_RENEWAL_EFFECTIVE_DATE, NEXT_RENEWAL_QUOTE_START_DATE,
                    DELIVERY_MODEL, COUNTRY, SITUS_STATE, ZIP_CODE, AGENCY_PRODUCER_COMBO, AGENT_SUB_PRODUCER).forEach(element -> softly.assertThat(policyInformationTab.getAssetList().getAsset(element)).isPresent());

            ImmutableList.of(SALES_REPRESENTATIVE, SALES_SUPPORT_ASSOCIATE, UNDERWRITER)
                    .forEach(element -> softly.assertThat(policyInformationTab.getAssetList().getAsset(INTERNAL_TEAM).getAsset(element)).isPresent());
            softly.assertThat(policyInformationTab.getAssetList().getAsset(ASSIGNED_AGENCIES).getAsset(PRIMARY_AGENCY)).isPresent();
        });

        LOGGER.info("REN-42379 Step 1");
        policyInformationTab.buttonNext.click();
        enrollmentTab.fillTab(tdPolicy);
        enrollmentTab.buttonNext.click();

        LOGGER.info("REN-42379 Step 4");
        planDefinitionTab.selectDefaultPlan();
        assertSoftly(softly -> {
            ImmutableList.of(COVERAGE_NAME, PLAN_NAME).forEach(element -> softly.assertThat(planDefinitionTab.getAssetList().getAsset(element)).isPresent());

            LOGGER.info("REN-42379 Step 5");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN)).hasValue(ImmutableList.of("EAP"));

            LOGGER.info("REN-42379 Step 6");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_NAME)).hasOptions(ValueConstants.EMPTY, "EAP");

            LOGGER.info("REN-42379 Step 7");
            ImmutableList.of(STD, LTD, LIFE, DENTAL, VISION)
                    .forEach(element -> softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_INCLUDED_IN_PACKAGE).getAsset(element)).isPresent());

            LOGGER.info("REN-42379 Step 8");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_INCLUDED_IN_PACKAGE).getAsset(STD)).isOptional().isEnabled().hasValue(false);

            LOGGER.info("REN-42379 Step 10");
            ImmutableList.of(SELF_ADMINISTERED, DIRECT_BILL, CONTRIBUTION_TYPE, PARTICIPANT_CONTRIBUTION, SPONSOR_PAYMENT_MODE)
                    .forEach(element -> softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(element)).isPresent());

            LOGGER.info("REN-42379 Step 11");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE)).isDisabled().hasValue("Non-contributory");

            LOGGER.info("REN-42379 Step 12");
            ImmutableList.of(WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEE, ELIGIBILITY_WAITING_PERIOD_DEFINITION)
                    .forEach(element -> softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(element)).isPresent());

            LOGGER.info("REN-42379 Step 14");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(ELIGIBILITY_WAITING_PERIOD_DEFINITION))
                    .hasOptions(ValueConstants.EMPTY, "On the Policy Effective Date (None)", "After 30 Days of Employment", "After 60 Days of Employment", "After 90 Days of Employment");

            LOGGER.info("REN-42379 Step 16");
            planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(ELIGIBILITY_WAITING_PERIOD_DEFINITION).setValue("After 30 Days of Employment");
            ImmutableList.of(WAITING_PERIOD_DEFINITION, WAITING_PERIOD_MODE_DEFINITION)
                    .forEach(element -> softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(element)).isPresent());

            LOGGER.info("REN-42379 Step 20, 21");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RatingMetaData.RATE_BASIS))
                    .isPresent().hasValue("Per Employee Per Month").hasOptions("Per Employee Per Month");

            LOGGER.info("REN-42379 Step 20, 22");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RatingMetaData.PLAN_TYPE))
                    .isPresent().isRequired().isEnabled().hasOptions(ValueConstants.EMPTY, "Telephonic benefits", "Face to Face benefits");

            LOGGER.info("REN-42379 Step 25, 26");
            ImmutableList.of("Census type", "Benefit Type")
                    .forEach(element -> softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(element))).isAbsent());
        });

        LOGGER.info("REN-41499 Step 6. REN-42379 Step 29");
        policyInformationTab.buttonNext.click();
        employeeAssistanceProgramMasterPolicy.getDefaultWorkspace().fillFromTo(tdPolicy, PlanDefinitionTab.class, PremiumSummaryTab.class, true);
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("REN-42558 Step 4");
        employeeAssistanceProgramMasterPolicy.propose().perform(tdPolicy);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        LOGGER.info("REN-42558 Step 5");
        employeeAssistanceProgramMasterPolicy.acceptContract().start();
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(planDefinitionTab.getTabName()))).isAbsent();
        Tab.buttonCancel.click();
        Page.dialogConfirmation.buttonYes.click();

        LOGGER.info("REN-42558 Step 6");
        employeeAssistanceProgramMasterPolicy.acceptContract().perform(tdPolicy);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        LOGGER.info("REN-42558 Step 7");
        employeeAssistanceProgramMasterPolicy.issue().start();
        NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
        assertSoftly(softly -> {
            ImmutableList.of(PlanDefinitionIssueActionTabMetaData.COVERAGE_NAME, PlanDefinitionIssueActionTabMetaData.PLAN, PlanDefinitionIssueActionTabMetaData.PLAN_NAME)
                    .forEach(element -> softly.assertThat(employeeAssistanceProgramMasterPolicy.issue().getWorkspace().getTab(PlanDefinitionIssueActionTab.class)
                            .getAssetList().getAsset(element)).hasValue("EAP"));
            AssetList eligibilityAsset = employeeAssistanceProgramMasterPolicy.issue()
                    .getWorkspace().getTab(PlanDefinitionIssueActionTab.class).getAssetList().getAsset(PlanDefinitionIssueActionTabMetaData.ELIGIBILITY);
            softly.assertThat(eligibilityAsset.getAsset(PlanDefinitionIssueActionTabMetaData.EligibilityMetaData.WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEE)).hasValue(VALUE_YES);
            softly.assertThat(eligibilityAsset.getAsset(PlanDefinitionIssueActionTabMetaData.EligibilityMetaData.ELIGIBILITY_WAITING_PERIOD_DEFINITION))
                    .hasValue("After 30 Days of Employment");
            softly.assertThat(eligibilityAsset.getAsset(PlanDefinitionIssueActionTabMetaData.EligibilityMetaData.WAITING_PERIOD_DEFINITION)).hasValue("100");
            softly.assertThat(eligibilityAsset.getAsset(PlanDefinitionIssueActionTabMetaData.EligibilityMetaData.WAITING_PERIOD_MODE_DEFINITION)).hasValue("Days");
        });
        LOGGER.info("REN-42558 Step 9");
        Tab.buttonCancel.click();
        Page.dialogConfirmation.buttonYes.click();
        employeeAssistanceProgramMasterPolicy.issue().perform(tdPolicy);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_EAP, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-41560", "REN-41579"}, component = POLICY_GROUPBENEFITS)
    public void testPresentsOfModifiedFieldsOnClassificationManagementAndPremiumSummaryTabs() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(employeeAssistanceProgramMasterPolicy.getType());
        initiateEAPQuoteAndFillToTab(getDefaultEAPMasterPolicyData(), ClassificationManagementTab.class, false);
        classificationManagementMpTab.getAssetList().getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();

        LOGGER.info("Step 1");
        assertSoftly(softly -> {
            ImmutableList.of(tableCoverageRelationships, tablePlansAndCoverages).forEach(element -> softly.assertThat(element).isPresent());

            LOGGER.info("Step 2");
            ImmutableList.of(CLASS_NUMBER, CLASS_NAME, TableConstants.CoverageRelationships.NUMBER_OF_PARTICIPANTS, TableConstants.CoverageRelationships.RATE)
                    .forEach(element -> softly.assertThat(classificationManagementMpTab.tableCoverageRelationships.getColumn(element.getName())).isPresent());
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Total Volume"))).isAbsent();

            LOGGER.info("Step 3");
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(USE_CLASSIFICATION_SUB_GROUPS)).isDisabled().hasValue(VALUE_NO);
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(NUMBER_OF_PARTICIPANTS)).isRequired().isEnabled().hasValue(EMPTY);
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(RATE)).isDisabled().hasValue("$1.00");
        });

        LOGGER.info("Step 6");
        classificationManagementMpTab.getAssetList().getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
        classificationManagementMpTab.getAssetList().getAsset(NUMBER_OF_PARTICIPANTS).setValue("0");
        classificationManagementMpTab.buttonNext.click();
        assertThat(premiumSummaryTab.buttonRate).isPresent();

        LOGGER.info("Step 7");
        classificationManagementMpTab.navigate();
        classificationManagementMpTab.getAssetList().getAsset(NUMBER_OF_PARTICIPANTS).setValue("10");
        classificationManagementMpTab.buttonNext.click();
        assertThat(premiumSummaryTab.buttonRate).isPresent();

        assertSoftly(softly -> {
            LOGGER.info("REN-41579 Step 1, 2");
            ImmutableList.of(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME, TableConstants.PremiumSummaryCoveragesTable.PLAN,
                    TableConstants.PremiumSummaryCoveragesTable.CONTRIBUTION_TYPE, TableConstants.PremiumSummaryCoveragesTable.PARTICIPANTS,
                    TableConstants.PremiumSummaryCoveragesTable.MANUAL_RATE, TableConstants.PremiumSummaryCoveragesTable.ANNUAL_PREMIUM)
                    .forEach(element -> softly.assertThat(premiumSummaryTab.premiumSummaryCoveragesTable.getHeader().getValue()).contains(element.getName()));
            ImmutableList.of(RATE_REQUEST_DATE, RATING_FORMULA)
                    .forEach(element -> softly.assertThat(premiumSummaryTab.getAssetList().getAsset(RATE_SECTION).getAsset(element)).isPresent());
            ImmutableList.of("Volume", "Formula Rate", "Experience Rate", "Experience Adjustment Factor", "Select Rating Census", "Experience Claim Amount", "Credibility Factor")
                    .forEach(element -> softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(element))).isAbsent());
        });

        LOGGER.info("Step 8. REN-41579 Step 3");
        premiumSummaryTab.buttonRate.click();
        classificationManagementMpTab.navigate();
        assertSoftly(softly -> {
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(USE_CLASSIFICATION_SUB_GROUPS)).isDisabled().hasValue(VALUE_NO);
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(NUMBER_OF_PARTICIPANTS)).isRequired().isEnabled().hasValue("10");
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(RATE)).isDisabled();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Total Volume"))).isAbsent();
        });

        LOGGER.info("Step 9");
        Tab.buttonSaveAndExit.click();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("Step 11");
        employeeAssistanceProgramMasterPolicy.propose().perform(getDefaultEAPMasterPolicyData());
        employeeAssistanceProgramMasterPolicy.acceptContract().perform(getDefaultEAPMasterPolicyData());
        employeeAssistanceProgramMasterPolicy.issue().perform(getDefaultEAPMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
    }
}
