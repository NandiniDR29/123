package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.CoveragesConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.PFL_NY;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NJ_STAT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NY_STAT;
import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.PERCENT_OF_TAXABLE_WAGE;
import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.PER_EMPLOYEE_PER_MONTH;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.modules.policy.common.actions.common.EndorseAction.startEndorsementForPolicy;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.ClassificationManagementTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetadata.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetadata.MAXIMUM_WEEKLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PlanDefinitionTab.removeCoverage;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyAttributesChangesVerification extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-33935", "REN-34443"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyAttributesChangesVerification() {
        LOGGER.info("General Preconditions");
        String employeeTier = "Employee";
        String employerTier = "Employer";

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        initiateSTQuoteAndFillUpToTab(getDefaultSTMasterPolicyData(), PlanDefinitionTab.class, true);
        planDefinitionTab.addCoverage(NY_STAT, PFL_NY);
        assertSoftly(softly -> {

            LOGGER.info("REN-33935 TC1 Step#1 verification");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(STATEWIDE_MAX_COVERED_PAYROLL)).hasValue("$72,860.84");//All values are default according to the spec.

            LOGGER.info("REN-33935 TC1 Step#2 verification");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(BENEFIT_PERCENTAGE)).hasValue("60%");

            LOGGER.info("REN-33935 TC1 Step#3 verification");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(MAXIMUM_WEEKLY_BENEFIT_AMOUNT)).hasValue("$840.70");

            LOGGER.info("REN-33935 TC2 Step#1 verification");
            removeCoverage(PFL_NY);
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(NJ_STAT);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(COVERAGE_NAME)).hasValue(CoveragesConstants.GroupStatutoryCoverages.STAT_NJ);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CONTRIBUTION_TYPE)).hasValue("Mandatory").hasOptions("Non-contributory", "Mandatory");

            LOGGER.info("REN-33935 TC2 Step#2 verification");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(ANNUAL_TAXABLE_WAGE_PER_PERSON)).hasValue("$134,900.00");

            LOGGER.info("REN-33935 TC2 Step#3 verification");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(EMPLOYER_MAXIMUM_ANNUAL_TAXABLE_WAGE_PER_PERSON)).isPresent().isDisabled().hasValue("$35,300.00");

            LOGGER.info("REN-33935 TC2 Step#4 verification");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(MAXIMUM_WEEKLY_BENEFIT_AMOUNT)).hasValue("$881.00");

            LOGGER.info("REN-33935 TC2 Step#5 verification");
            planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RATE_BASIS).setValue(PERCENT_OF_TAXABLE_WAGE);
            planDefinitionTab.submitTab();

            classificationManagementMpTab.getAssetList().getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
            classificationManagementMpTab.getAssetList().getAsset(CLASSIFICATION_GROUP_NAME).setValue("index=1");

            LOGGER.info("REN-33935 TC2 Step#6 verification");
            List<String> coverageTierColumnValues = ClassificationManagementTab.tablePlanTierAndRatingInfo.getColumn(TableConstants.PlanTierAndRatingSelection.COVERAGE_TIER.getName()).getValue();
            softly.assertThat(coverageTierColumnValues).isEqualTo(ImmutableList.of(employeeTier, employerTier));

            LOGGER.info("REN-33935 TC2 Step#7 verification");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Summarize Number of Participants?"))).isAbsent();

            LOGGER.info("REN-33935 TC2 Step#8, 9, 10 and REN-34443 Step#1 verification");
            setCoverageTierAndFieldsCheck(employeeTier, softly);

            LOGGER.info("REN-33935 TC2 Step#11, 12, 13 and REN-34443 Step#1 verification");
            setCoverageTierAndFieldsCheck(employerTier, softly);


            LOGGER.info("REN-34443 Step#3 verification");
            updateRateBasisAndSetClassificationGroup(PER_EMPLOYEE_PER_MONTH);
            perEmployeePerMonthVerification(softly);
        });

        premiumSummaryTab.navigateToTab();
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().getTab(PremiumSummaryTab.class).fillTab(getDefaultSTMasterPolicyData());
        premiumSummaryTab.submitTab();

        LOGGER.info("REN-34443 Step#5 verification");
        proposeAcceptContractIssueSTMasterPolicyWithDefaultTestData();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);

        LOGGER.info("REN-34443 Step#6 verification");
        startEndorsementForPolicy(GroupBenefitsMasterPolicyType.GB_ST, statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        updateRateBasisAndSetClassificationGroup(PERCENT_OF_TAXABLE_WAGE);
        assertSoftly(softly -> {
            setCoverageTierAndFieldsCheck(employeeTier, softly);
            setCoverageTierAndFieldsCheck(employerTier, softly);

            updateRateBasisAndSetClassificationGroup(PER_EMPLOYEE_PER_MONTH);
            perEmployeePerMonthVerification(softly);
        });
    }

    private void setCoverageTierAndFieldsCheck(String coverageTier, ETCSCoreSoftAssertions softly) {
        classificationManagementMpTab.updateExistingCoverage(coverageTier);
        softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(RATE)).isPresent().isOptional().hasValue("1.00000000");//should be updated in REN-40280
        softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(VOLUME)).isPresent().isRequired().hasValue("$0.00");//should be updated in REN-40280
        softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(NUMBER_OF_PARTICIPANTS)).isPresent().isOptional().hasValue("1");
    }

    private void updateRateBasisAndSetClassificationGroup(String rateBasis) {
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RATE_BASIS).setValue(rateBasis);
        planDefinitionTab.submitTab();

        classificationManagementMpTab.getAssetList().getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        classificationManagementMpTab.getAssetList().getAsset(CLASSIFICATION_GROUP_NAME).setValue("index=1");
    }

    private void perEmployeePerMonthVerification(ETCSCoreSoftAssertions softly) {
        softly.assertThat(ClassificationManagementTab.tablePlanTierAndRatingInfo).isAbsent();
        softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(VOLUME)).isAbsent();

        softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(RATE)).isPresent();
        softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(TOTAL_VOLUME)).isPresent();
        softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(NUMBER_OF_PARTICIPANTS)).isPresent();
        softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(USE_CLASSIFICATION_SUB_GROUPS)).isPresent();
    }
}
