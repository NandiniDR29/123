package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionEndorseNPBInfoActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.CaseInstallationEndorseNPDINfoActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PlanDefinitionEndorseNPBInfoActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PlanDefinitionIssueActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInfoEndorseNPBInfoActionTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.NameToDisplayOnMPDocumentsValues.MEMBER_COMPANY;
import static com.exigen.ren.main.enums.ValueConstants.NONE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.policy.common.metadata.master.CaseInstallationEndorseNPDINfoActionTabMetaData.DEFINITION_OF_LEGAL_SPOUSE;
import static com.exigen.ren.main.modules.policy.common.metadata.master.CaseInstallationEndorseNPDINfoActionTabMetaData.DefinitionOfLegalSpouseMetadata.*;
import static com.exigen.ren.main.modules.policy.common.metadata.master.CaseInstallationEndorseNPDINfoActionTabMetaData.INCLUDE_MEMBERS_ON_COBRA;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionEndorseNPBInfoActionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionEndorseNPBInfoActionTabMetaData.EligibilityMetadata.MINIMUM_HOURLY_REQUIREMENT;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInfoEndorseNPBInfoActionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.ELIGIBILITY;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetadata.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestNPBEndorseWhenGroupMemberCompanyIsYes extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-35844"}, component = POLICY_GROUPBENEFITS)
    public void testNPBEndorseWhenGroupMemberCompanyIsYes() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithRelationshipTypes")
                .adjust(TestData.makeKeyPath(relationshipTab.getMetaKey() + "[1]", RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER.getLabel()), MEMBER_COMPANY).resolveLinks());
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());

        groupVisionMasterPolicy.createPolicy(getDefaultVSMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestDataPolicy").resolveLinks()).resolveLinks()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.CENSUS_TYPE.getLabel()), "Eligible")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", ELIGIBILITY.getLabel(), MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK.getLabel()), "30")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", ELIGIBILITY.getLabel(), ELIGIBILITY_WAITING_PERIOD_DEFINITION.getLabel()), "Amount and Mode Only")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", ELIGIBILITY.getLabel(), WAITING_PERIOD_AMOUNT.getLabel()), "10")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", ELIGIBILITY.getLabel(), WAITING_PERIOD_MODE.getLabel()), "Days")
                .adjust(TestData.makeKeyPath(PlanDefinitionIssueActionTab.class.getSimpleName(), PlanDefinitionIssueActionTabMetaData.INCLUDE_RETIREES.getLabel()), VALUE_NO));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("Steps#1, 2 execution");
        groupVisionMasterPolicy.endorseNPBInfo().start();
        groupVisionMasterPolicy.endorseNPBInfo().getWorkspace()
                .fillUpTo(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT_NPB_INFO, TestDataKey.DEFAULT_TEST_DATA_KEY), PolicyInfoEndorseNPBInfoActionTab.class, false);

        CustomSoftAssertions.assertSoftly(softly -> {
            LOGGER.info("Steps#3.1-3.5, 3.8-3.15, 3.18-3.22, 3.24, 3.27-3.32, 3.34, 3.35 verification");

            AbstractContainer<?, ?> assetListPolicyInfo = groupVisionMasterPolicy.endorseNPBInfo().getWorkspace().getTab(PolicyInfoEndorseNPBInfoActionTab.class).getAssetList();
            ImmutableList.of(QUOTE_CREATION_DATE, QUOTE_EXPIRATION_DATE, ASO_PLAN, UNDEWRITING_COMPANY, GROUP_IS_MEMBER_COMPANY,
                    POLICY_EFFECTIVE_DATE, CURRENT_POLICY_YEAR_START_DATE, NEXT_POLICY_YEAR_START_DATE, RATE_GUARANTEE_MONTHS,
                    NEXT_RENEWAL_EFFECTIVE_DATE, NEXT_RENEWAL_QUOTE_START_DATE, CURRENCY, DELIVERY_MODEL, SITUS_STATE, ZIP_CODE, COUNTRY,
                    RENEWAL_NOTIFICATION_DAYS, TOTAL_NUMBER_OF_ELIGIBLE_LIVES, PRIOR_CLAIMS_RETROACTIVE_EFFECTIVE_DATE, ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS,
                    GROUP_IS_AN_ASSOCIATION, BLEND_DEMOGRAPHICS, AGING_FREQUENCY)
                    .forEach(asset -> softly.assertThat(assetListPolicyInfo.getAsset(asset)).isPresent().isDisabled());
            softly.assertThat(assetListPolicyInfo.getAsset(PRIOR_CLAIMS_ALLOWED)).isPresent().isDisabled();
            softly.assertThat(assetListPolicyInfo.getAsset(ANNIVERSARY_DAY)).isPresent().isDisabled();

            LOGGER.info("Steps#3.16, 3.17, 3.36-3.38 verification");
            ImmutableList.of("Sales Representative", "Underwriter", "Sales Support Associate", "Agency / Producer", "Agency Type",
                    "Independent Commissionable Producer (ICP)?", "Agent Sub Producer", "Primary Agency?", "Commission Split")
                    .forEach(sectionName -> softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(sectionName))).isAbsent());
            softly.assertThat(assetListPolicyInfo.getAsset(RENEWAL_FREQUENCY)).isAbsent();
            softly.assertThat(assetListPolicyInfo.getAsset(POLICY_TERM)).isAbsent();

            LOGGER.info("Steps#3.6, 3.7, 3.23, 3.33 verification");
            ImmutableList.of(MEMBER_COMPANY_NAME, NAME_TO_DISPLAY_ON_MP_DOCUMENTS, COUNTY_CODE, DEDUCTION_BY_PAY_PERIOD)
                    .forEach(asset -> softly.assertThat(assetListPolicyInfo.getAsset(asset)).isPresent().isEnabled().isRequired());

            LOGGER.info("Steps#3.25, 3.26 verification");
            softly.assertThat(assetListPolicyInfo.getAsset(PRIOR_CARRIER_NAME)).isPresent().isEnabled();

            LOGGER.info("Step#4 execution");
            groupVisionMasterPolicy.endorseNPBInfo().getWorkspace().getTab(PolicyInfoEndorseNPBInfoActionTab.class).submitTab();

            LOGGER.info("Steps#4.1-4.5 verification");
            AbstractContainer<?, ?> assetListCaseInstallation = groupVisionMasterPolicy.endorseNPBInfo().getWorkspace().getTab(CaseInstallationEndorseNPDINfoActionTab.class).getAssetList();
            ImmutableList.of(SPOUSE_LEGALLY_RECOGNIZED_POLICY_ISSUE_STATE, CIVIL_UNION_LEGALLY_RECOGNIZED_POLICY_ISSUE_STATE, INCLUDES_DOMESTIC_PARTNER)
                    .forEach(asset -> softly.assertThat(assetListCaseInstallation.getAsset(DEFINITION_OF_LEGAL_SPOUSE).getAsset(asset)).isPresent().isEnabled());
            softly.assertThat(assetListCaseInstallation.getAsset(INCLUDE_MEMBERS_ON_COBRA)).isPresent().isEnabled().isRequired();
        });

        LOGGER.info("Step#5 execution");
        groupVisionMasterPolicy.endorseNPBInfo().getWorkspace().getTab(CaseInstallationEndorseNPDINfoActionTab.class).submitTab();
        AbstractContainer<?, ?> assetListPlanDefinition = groupVisionMasterPolicy.endorseNPBInfo().getWorkspace().getTab(PlanDefinitionEndorseNPBInfoActionTab.class).getAssetList();
        CustomSoftAssertions.assertSoftly(softly -> {

            LOGGER.info("Step#5.1 verification");
            softly.assertThat(PlanDefinitionEndorseNPBInfoActionTab.planTable).isPresent();

            LOGGER.info("Steps#5.2, 5.5-7.7 verification");
            softly.assertThat(assetListPlanDefinition.getAsset(PLAN)).isPresent().isDisabled();
            softly.assertThat(assetListPlanDefinition.getAsset(COVERAGE_TIERS)).isPresent().isDisabled();
            softly.assertThat(assetListPlanDefinition.getAsset(CENSUS_TYPE)).isPresent().isDisabled();
            softly.assertThat(assetListPlanDefinition.getAsset(NETWORK)).isPresent().isDisabled();

            LOGGER.info("Steps#5.8, 5.9, 5.10, 5.16, 5.17 verification");
            ImmutableList.of(EligibilityMetadata.DOES_MIN_HOURLY_REQUIREMENT_APPLY_VS_DN, MINIMUM_HOURLY_REQUIREMENT,
                    EligibilityMetadata.DEPENDENT_MAXIMUM_AGE,
                    EligibilityMetadata.INCLUDE_DISABLED_DEPENDENTS)
                    .forEach(asset -> softly.assertThat(assetListPlanDefinition.getAsset(PlanDefinitionEndorseNPBInfoActionTabMetaData.ELIGIBILITY).getAsset(asset)).isPresent().isDisabled());

            LOGGER.info("Steps#5.3, 5.4 verification");
            softly.assertThat(assetListPlanDefinition.getAsset(PLAN_NAME)).isPresent().isEnabled().isRequired();
            softly.assertThat(assetListPlanDefinition.getAsset(VSP_DIVISION)).isPresent().isEnabled().isRequired();

            LOGGER.info("Steps#5.11, 5.15, 5.18, 5.19 execution");
            ImmutableList.of(EligibilityMetadata.ELIGIBILITY_WAITING_PERIOD_DEFINITION,
                    EligibilityMetadata.ELIGIBILITY_WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES,
                    EligibilityMetadata.ALLOW_MEMBER_AND_SPOUSE,
                    EligibilityMetadata.BENEFITS_END_ON)
                    .forEach(asset -> softly.assertThat(assetListPlanDefinition.getAsset(PlanDefinitionEndorseNPBInfoActionTabMetaData.ELIGIBILITY).getAsset(asset)).isPresent().isEnabled().isRequired());

            LOGGER.info("Steps#5.12 verification");
            assetListPlanDefinition.getAsset(PlanDefinitionEndorseNPBInfoActionTabMetaData.ELIGIBILITY)
                    .getAsset(EligibilityMetadata.ELIGIBILITY_WAITING_PERIOD_DEFINITION).setValue(NONE);

            LOGGER.info("Steps#5.13, 5.14 verification");
            ImmutableList.of(EligibilityMetadata.WAITING_PERIOD_AMOUNT,
                    EligibilityMetadata.WAITING_PERIOD_MODE)
                    .forEach(asset -> softly.assertThat(assetListPlanDefinition.getAsset(PlanDefinitionEndorseNPBInfoActionTabMetaData.ELIGIBILITY).getAsset(asset)).isAbsent());

            LOGGER.info("Steps#5.20-5.28 verification");
            ImmutableList.of("LTD", "LIFE", "DENTAL", "VISION", "Self-administered?", "Direct Bill", "Participant Contribution %", "Contacts - Up to",
                    "Minimum Number of Participants", "Gross Up", "Taxability", "Premium Paid-Post Tax", "Sponsor Payment Mode", "Member Payment Mode",
                    "Experience Claim Amount", "Credibility Factor", "SIC Code", "Exam/Materials", "Medically Necessary Contact Lenses", "Contacts",
                    "Progressive Lenses in Full", "Lens Enhancements - Standard", "Lens Enhancements - Premium", "Lens Enhancements - Custom",
                    "Frames - Allowance up to", "Contact Lenses - Allowance up to", "Plan Limitation", "Frequency Definition", "Exam/Lenses/Frame",
                    "Lenticular Lenses - Up to", "Photochromic Lenses Factor", "Scratch Coating Factor", "Safety Glasses Factor", "Exam - Up to", "Frame - Up to",
                    "Single Vision Lenses - Up to", "Lined Bifocal Lenses - Up to", "Lined Trifocal Lenses - Up to", "Progressive Lenses - Up to")
                    .forEach(sectionName -> softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(sectionName))).isAbsent());
        });
    }
}
