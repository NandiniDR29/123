package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionEndorseNPBInfoActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.PlanDefinitionEndorseNPBInfoActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInfoEndorseNPBInfoActionTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.NameToDisplayOnMPDocumentsValues.MEMBER_COMPANY;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionEndorseNPBInfoActionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionEndorseNPBInfoActionTabMetaData.CoverageIncludedInPackageMetaData.*;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionEndorseNPBInfoActionTabMetaData.EligibilityMetadata.*;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInfoEndorseNPBInfoActionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.CENSUS_TYPE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestNPBEndorsePolicyInfoTabVerification extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-35688"}, component = POLICY_GROUPBENEFITS)
    public void testNPBEndorsePolicyInfoTabVerification() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithRelationshipTypes")
                .adjust(TestData.makeKeyPath(relationshipTab.getMetaKey() + "[1]", RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER.getLabel()), MEMBER_COMPANY).resolveLinks());
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        LOGGER.info("Scenario#1 Step#1 execution");
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData").resolveLinks()).resolveLinks()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", CENSUS_TYPE.getLabel()), "Eligible"));

        LOGGER.info("Scenario#1 Step#2, Scenario#2 Step#1 verification");
        longTermDisabilityMasterPolicy.endorseNPBInfo().start();
        longTermDisabilityMasterPolicy.endorseNPBInfo().getWorkspace()
                .fillUpTo(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT_NPB_INFO, TestDataKey.DEFAULT_TEST_DATA_KEY), PolicyInfoEndorseNPBInfoActionTab.class, false);

        AbstractContainer<?, ?> assetListPolicyInfo = longTermDisabilityMasterPolicy.endorseNPBInfo().getWorkspace().getTab(PolicyInfoEndorseNPBInfoActionTab.class).getAssetList();
        AbstractContainer<?, ?> assetListPlanDefinition = longTermDisabilityMasterPolicy.endorseNPBInfo().getWorkspace().getTab(PlanDefinitionEndorseNPBInfoActionTab.class).getAssetList();

        LOGGER.info("Scenario#1 Steps#3-6, 9-15, 18-21, 26-33 verification");
        CustomSoftAssertions.assertSoftly(softly -> {
            ImmutableList.of(
                    QUOTE_CREATION_DATE, QUOTE_EXPIRATION_DATE, UNDEWRITING_COMPANY, GROUP_IS_MEMBER_COMPANY,
                    POLICY_EFFECTIVE_DATE, CURRENT_POLICY_YEAR_START_DATE, NEXT_POLICY_YEAR_START_DATE, RATE_GUARANTEE_MONTHS,
                    NEXT_RENEWAL_EFFECTIVE_DATE, NEXT_RENEWAL_QUOTE_START_DATE, DELIVERY_MODEL, SITUS_STATE, ZIP_CODE, SMALL_GROUP,
                    PRIOR_CLAIMS_RETROACTIVE_EFFECTIVE_DATE, FIRST_TIME_BUYER, ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS, AGING_FREQUENCY)
                    .forEach(asset -> softly.assertThat(assetListPolicyInfo.getAsset(asset)).isPresent().isDisabled());
            softly.assertThat(assetListPolicyInfo.getAsset(PRIOR_CLAIMS_ALLOWED)).isPresent().isDisabled();
            softly.assertThat(assetListPolicyInfo.getAsset(ANNIVERSARY_DAY)).isPresent().isDisabled();


            LOGGER.info("Scenario#1 Steps#7, 8, 22");
            ImmutableList.of(MEMBER_COMPANY_NAME, NAME_TO_DISPLAY_ON_MP_DOCUMENTS, COUNTY_CODE)
                    .forEach(asset -> softly.assertThat(assetListPolicyInfo.getAsset(asset)).isPresent().isEnabled().isRequired());

            LOGGER.info("Scenario#1 Steps#23-25");
            ImmutableList.of(PRIOR_CARRIER_NAME_TEXT_BOX, PRIOR_CARRIER_POLICY_NUMBER, DEDUCTION_BY_PAY_PERIOD)
                    .forEach(asset -> softly.assertThat(assetListPolicyInfo.getAsset(asset)).isPresent().isEnabled());

            LOGGER.info("Scenario#1 Steps#16, 17");
            softly.assertThat(assetListPolicyInfo.getAsset(RENEWAL_FREQUENCY)).isAbsent();
            softly.assertThat(assetListPolicyInfo.getAsset(POLICY_TERM)).isAbsent();

            LOGGER.info("Scenario#1 Steps#34-36");
            ImmutableList.of("Sales Representative", "Underwriter", "Sales Support Associate", "Agency / Producer", "Agency Type",
                    "Independent Commissionable Producer (ICP)?", "Agent Sub Producer", "Primary Agency?", "Commission Split", "Cosmetic Surgery",
                    "Substance Abuse", "Active Duty In Armed Forces", "War", "Self-inflicted", "Riot", "Felony", "Intoxication")
                    .forEach(sectionName -> softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(sectionName))).isAbsent());
        });

        LOGGER.info("Scenario#2 Steps#3-5, 7-10");
        longTermDisabilityMasterPolicy.endorseNPBInfo().getWorkspace().getTab(PolicyInfoEndorseNPBInfoActionTab.class).submitTab();
        CustomSoftAssertions.assertSoftly(softly -> {

            LOGGER.info("Scenario#2 Steps#2");
            softly.assertThat(PlanDefinitionEndorseNPBInfoActionTab.planTable).isAbsent();
            ImmutableList.of(COVERAGE_NAME, PLAN, PlanDefinitionEndorseNPBInfoActionTabMetaData.CENSUS_TYPE,
                    REQUIRED_PARTICIPATION, ASSUMED_PARTICIPATION, TOTAL_NUMBER_ELIGIBLE_LIVES)
                    .forEach(asset -> softly.assertThat(assetListPlanDefinition.getAsset(asset)).isPresent().isDisabled());

            LOGGER.info("Scenario#2 Steps#11-18");
            ImmutableList.of(STD, LIFE, DENTAL, VISION, STD_ADMINISTERED)
                    .forEach(asset -> softly.assertThat(assetListPlanDefinition.getAsset(COVERAGE_INCLUDED_IN_PACKAGE).getAsset(asset)).isPresent().isDisabled());

            LOGGER.info("Scenario#2 Steps#19, 20");
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT)).isPresent().isDisabled();

            LOGGER.info("Scenario#2 Steps#6, 21, 22, 25");
            softly.assertThat(assetListPlanDefinition.getAsset(PLAN_NAME)).isPresent().isEnabled().isRequired();
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(ELIGIBILITY_WAITING_PERIOD_DEFINITION)).isPresent().isEnabled().isRequired();
            assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(ELIGIBILITY_WAITING_PERIOD_DEFINITION).setValue("None");
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES)).isPresent().isEnabled().isRequired();

            LOGGER.info("Scenario#2 Steps#23, 24");
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_MODE)).isAbsent();
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD)).isAbsent();

            LOGGER.info("Scenario#2 Steps#26-35");
            ImmutableList.of("Self-administered?", "Direct Bill", "Contribution Type", "Participant Contribution %", "Minimum Number of Participants", "Gross Up", "Taxability",
                    "Premium Paid-Post Tax", "Sponsor Payment Mode", "Member Payment Mode", "Open Enrollment Participation?", "Rate Basis", "Experience Claim Amount", "SIC Code",
                    "Credibility Factor", "Benefit Type", "Maximum Monthly Benefit Amount", "Benefit Percentage", "Minimum Monthly Benefit Percentage", "Minimum Monthly Benefit Amount",
                    "Maximum Benefit Period", "Elimination Period (days)", "Definition of Disability", "Test Definition", "Number of Months", "Residual", "Partial Disability Benefit",
                    "WIB Duration", "Offset % After WIB Duration", "Specialty Own Occupation", "Earning Definition", "Own Occupation Earnings Test", "Any Occupation Earnings Test",
                    "Pre-Existing Conditions", "Temporary Recovery Period During EP (days)", "Temporary Recovery Period After EP (months)", "Successive Period (Months)", "Estate Payable",
                    "Survivor Benefit Waiting Period", "Survivor - Family Income Benefit Type", "Pay Survivor Benefit Gross", "Social Security Integration Method", "Sick Leave", "PTO",
                    "Compulsory State Plans", "Termination/Severance", "Work Earnings", "Retirement Plan", "Automobile Liability", "Include PERS and STRS", "3rd Party Settlement",
                    "Individual Disability Plan", "Family & Medical Leave Act", "Layoff Leave of Absence", "Military Leave", "Military Leave Duration", "Prudent Person", "FICA Match",
                    "Rehabilitation Incentive Benefit", "Rehabilitation Incentive Benefit Max Amount", "Rehabilitation Incentive Benefit Duration", "Rehabilitation Incentive Benefit Threshold",
                    "Mental Illness Limitation", "Special Conditions Limitation", "Substance Abuse Limitation", "Self Reported Conditions Limitation", "Combined Limit", "Family Care Benefit",
                    "Terminal Illness Benefit", "Cost of Living Adjustment Benefit", "COBRA Premium Reimbursement Amount", "Recovery Income Benefit", "Recovery Income Protection Max",
                    "Presumptive Disability", "Catastrophic Disability Benefit", "Infectious Disease", "Child Education Benefit", "Student Loan Repayment Amount", "ERISA Option",
                    "Accumulation Period", "401K Contribution During Disability", "W2", "Mental Illness Event", "Substance Abuse Event", "Non-Verifiable Symptoms Event", "Special Conditions Event",
                    "Mandatory Rehabilitation", "Mandatory Wellness", "Reside Outside", "Guaranteed Issue Amount", "Revenue Protection Benefit Included", "Owner", "Shareholder",
                    "Sole proprietor", "Partner", "Director", "Manager", "Workplace Modification Benefit", "Workplace modification benefit %", "Workplace modification benefit Maximum")
                    .forEach(sectionName -> softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(sectionName))).isAbsent());
        });
    }
}
