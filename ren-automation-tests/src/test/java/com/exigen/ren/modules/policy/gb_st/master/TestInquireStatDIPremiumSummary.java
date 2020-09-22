package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.CoveragesConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.ActionConstants.CHANGE;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.*;
import static com.exigen.ren.main.enums.TableConstants.FileIntakeManagement.INTAKE_PROFILE_NUMBER;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryCoveragesTable.FORMULA_RATE;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData.UPLOAD_FILE;
import static com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData.UploadFileDialog.FILE_NAME;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.ClassificationManagementTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.CENSUS_TYPE;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.UNDER_FIFTY_LIVES_WORKFLOW;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PremiumSummaryTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab.tablePlansAndCoverages;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PremiumSummaryTab.premiumSummaryCoveragesTable;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.labelPolicyStatus;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestInquireStatDIPremiumSummary extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    private static final String DOLLAR = "$";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23637"}, component = POLICY_GROUPBENEFITS)
    public void testPremiumSummaryRateForPercentOfTaxableWage() {
        LOGGER.info("REN-23637 Precondition");
        TestData td = statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NJ")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey(), PlanDefinitionTabMetaData.RATING.getLabel(), PlanDefinitionTabMetaData.RatingMetadata.RATE_BASIS.getLabel()), "Percent of Taxable Wage");

        initiateQuoteAndPerformAllVerifications(td, getDefaultSTMasterPolicyData()
                .adjust(classificationManagementMpTab.getMetaKey(), tdSpecific().getTestData("ClassificationManagementTab_NJ")), false);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23637"}, component = POLICY_GROUPBENEFITS)
    public void testPremiumSummaryRateForPerEmployeePerMonth() {
        LOGGER.info("REN-23637 Precondition");
        TestData td = statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NJ")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey(), PlanDefinitionTabMetaData.RATING.getLabel(), PlanDefinitionTabMetaData.RatingMetadata.RATE_BASIS.getLabel()), "Per Employee Per Month");

        initiateQuoteAndPerformAllVerifications(td, statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NJ"), true);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23637"}, component = POLICY_GROUPBENEFITS)
    public void testPremiumSummaryRateForPerPFLNY() {
        LOGGER.info("REN-23637 Precondition");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        initiateSTQuoteAndFillUpToTab(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY), PlanDefinitionTab.class, true);
        Tab.buttonNext.click();
        assertSoftly(softly -> {
            LOGGER.info("REN-23637 TC1 Step 1");
            softly.assertThat(NavigationPage.PolicyNavigation.isLeftMenuTabSelected(NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT.get())).isTrue();
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(INITIATED);

            LOGGER.info("REN-23637 TC1 Step 2");
            addClassificationGroupForCoverage(CoveragesConstants.GroupStatutoryCoverages.STAT_NY);
            addClassificationGroupForCoverage(CoveragesConstants.GroupStatutoryCoverages.PFL_NY);
            classificationManagementMpTab.submitTab();

            statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().getTab(PremiumSummaryTab.class).fillTab(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));
            premiumSummaryTab.rate();

            LOGGER.info("REN-23637 TC1 Step 3");
            softly.assertThat(premiumSummaryCoveragesTable.getRow(1).getCell(TableConstants.PremiumSummaryCoveragesTable.MANUAL_RATE.getName()).getValue()).startsWith(DOLLAR);
            softly.assertThat(premiumSummaryCoveragesTable.getRow(1).getCell(FORMULA_RATE.getName()).getValue()).startsWith(DOLLAR);

            LOGGER.info("REN-23637 TC1 Step 4");
            PremiumSummaryTab.buttonNext.click();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23820"}, component = POLICY_GROUPBENEFITS)
    public void testRatingCensusParameterForCensusTypeNone() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        caseProfile.update().perform(tdSpecific().getTestData("TestDataCaseUpdate").resolveLinks());

        initiateSTQuoteAndFillUpToTab(getDefaultSTMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestDataMaster").resolveLinks().resolveLinks()), PremiumSummaryTab.class, false);
        String quoteNumber = QuoteSummaryPage.getQuoteNumber();

        assertSoftly(softly -> {
            LOGGER.info("REN-23820 TC1 Step 2");
            ImmutableList.of(SELECT_RATING_CENSUS, APPLY, REMOVE).forEach(control ->
                    softly.assertThat(premiumSummaryTab.getAssetList().getAsset(control)).isAbsent());
            softly.assertThat(APPLIED_RATING_CENSUS).isAbsent();

            LOGGER.info("REN-23820 TC1 Step 3");
            softly.assertThat(DBService.get().getValue(
                    "SELECT pd.censusFile_ID, ps.policyStatusCd, ps.policyNumber " +
                            "FROM PolicySummary ps " +
                            "JOIN PolicyDetail pd on ps.policyDetail_id = pd.id " +
                            "WHERE ps.policyNumber = ?", quoteNumber)).isEmpty();
        });

        LOGGER.info("REN-23820 TC1 Step 4");
        policyInformationTab.navigateToTab();
        policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES_WORKFLOW).setValue(VALUE_NO);
        premiumSummaryTab.navigateToTab();
        premiumSummaryTab.submitTab();

        assertThat(labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
        statutoryDisabilityInsuranceMasterPolicy.propose().perform(getDefaultSTMasterPolicyData());
        assertThat(labelPolicyStatus).hasValue(PROPOSED);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23820"}, component = POLICY_GROUPBENEFITS)
    public void testRatingCensusParameterForCensus() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_AutoSubGroup")
                .adjust(tdSpecific().getTestData("TestDataCase").resolveLinks()), statutoryDisabilityInsuranceMasterPolicy.getType());
        caseProfile.update().perform(tdSpecific().getTestData("TestDataCaseUpdate").resolveLinks());

        String fileIntake1 = CaseProfileSummaryPage.tableFileIntakeManagement.getColumn(INTAKE_PROFILE_NUMBER).getCell(1).getValue();
        String fileIntake2 = CaseProfileSummaryPage.tableFileIntakeManagement.getColumn(INTAKE_PROFILE_NUMBER).getCell(2).getValue();

        initiateSTQuoteAndFillUpToTab(getDefaultSTMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestDataMaster").resolveLinks().resolveLinks())
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey(), CENSUS_TYPE.getLabel()), "Eligible"), PremiumSummaryTab.class, false);

        LOGGER.info("REN-23820 TC2 Steps 2, 4, 5");
        assertSoftly(softly -> {
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(SELECT_RATING_CENSUS)).isPresent().isEnabled().hasValue(EMPTY);
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(APPLY)).isPresent().isDisabled();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REMOVE)).isAbsent();
            softly.assertThat(APPLIED_RATING_CENSUS).isAbsent();
        });

        LOGGER.info("REN-23820 TC2 Step 6");
        String file1 = tdSpecific().getTestData("FileIntakeManagementTab1").getValue(FileIntakeManagementTabMetaData.INTAKE_PROFILE_NAME.getLabel());
        String file2 = tdSpecific().getTestData("FileIntakeManagementTab2").getValue(FileIntakeManagementTabMetaData.INTAKE_PROFILE_NAME.getLabel());

        String uploadedFileName1 = tdSpecific().getTestData("FileIntakeManagementTab1", UPLOAD_FILE.getLabel()).getValue(FILE_NAME.getLabel());
        String uploadedFileName2 = tdSpecific().getTestData("FileIntakeManagementTab2", UPLOAD_FILE.getLabel()).getValue(FILE_NAME.getLabel());

        String value1 = String.format("%s_%S :: %s", fileIntake1, file1, uploadedFileName1);
        String value2 = String.format("%s_%S :: %s", fileIntake2, file2, uploadedFileName2);

        assertSoftly(softly -> {
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(SELECT_RATING_CENSUS)).containsAllOptions(EMPTY, value1, value2);

            LOGGER.info("REN-23820 TC2 Step 7, 9, 10");
            premiumSummaryTab.getAssetList().getAsset(SELECT_RATING_CENSUS).setValue(value1);
            premiumSummaryTab.getAssetList().getAsset(APPLY).click();

            softly.assertThat(APPLIED_RATING_CENSUS).hasValue(value1);
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REMOVE)).isPresent().isEnabled();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(APPLY)).isDisabled();

            LOGGER.info("REN-23820 TC2 Step 8");
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(SELECT_RATING_CENSUS)).containsAllOptions(EMPTY, value2);

            LOGGER.info("REN-23820 TC2 Step 11");
            premiumSummaryTab.getAssetList().getAsset(REMOVE).click();
            softly.assertThat(APPLIED_RATING_CENSUS).isAbsent();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(SELECT_RATING_CENSUS)).containsAllOptions(value1, value2);
        });

        LOGGER.info("REN-23820 TC2 Step 13");
        premiumSummaryTab.getAssetList().getAsset(SELECT_RATING_CENSUS).setValue(value2);
        premiumSummaryTab.getAssetList().getAsset(APPLY).click();

        policyInformationTab.navigateToTab();
        policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES_WORKFLOW).setValue(VALUE_NO);
        premiumSummaryTab.navigateToTab();
        premiumSummaryTab.submitTab();

        assertThat(labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
    }

    private void initiateQuoteAndPerformAllVerifications(TestData tdPolicy, TestData policyTD, boolean startWithDollar) {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        initiateSTQuoteAndFillUpToTab(tdPolicy, PlanDefinitionTab.class, true);
        planDefinitionTab.submitTab();

        assertSoftly(softly -> {
            LOGGER.info("REN-23637 TC1 Step 1");
            softly.assertThat(NavigationPage.PolicyNavigation.isLeftMenuTabSelected(NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT.get())).isTrue();
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(INITIATED);

            LOGGER.info("REN-23637 TC1 Step 2");
            statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(policyTD, ClassificationManagementTab.class, PremiumSummaryTab.class, true);
            premiumSummaryTab.rate();

            LOGGER.info("REN-23637 TC1 Step 3");
            if (startWithDollar) {
                softly.assertThat(premiumSummaryCoveragesTable.getRow(1).getCell(TableConstants.PremiumSummaryCoveragesTable.MANUAL_RATE.getName()).getValue()).startsWith(DOLLAR);
                softly.assertThat(premiumSummaryCoveragesTable.getRow(1).getCell(FORMULA_RATE.getName()).getValue()).startsWith(DOLLAR);
            } else {
                softly.assertThat(premiumSummaryCoveragesTable.getRow(1).getCell(TableConstants.PremiumSummaryCoveragesTable.MANUAL_RATE.getName()).getValue()).doesNotStartWith(DOLLAR);
                softly.assertThat(premiumSummaryCoveragesTable.getRow(1).getCell(FORMULA_RATE.getName()).getValue()).doesNotStartWith(DOLLAR);
            }

            LOGGER.info("REN-23637 TC1 Step 4");
            PremiumSummaryTab.buttonNext.click();
            softly.assertThat(labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
        });
    }

    private void addClassificationGroupForCoverage(String coverageName) {
        tablePlansAndCoverages.getRowContains(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName(), coverageName).getCell(1).click();
        classificationManagementMpTab.getAssetList().getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        classificationManagementMpTab.getAssetList().getAsset(CLASSIFICATION_GROUP_NAME).setValue("1");
        tableClassificationSubGroupsAndRatingInfo.getRows().forEach(row -> {
            row.getCell(8).controls.links.get(CHANGE).click();
            classificationManagementMpTab.getAssetList().getAsset(NUMBER_OF_PARTICIPANTS).setValue("1");
            classificationManagementMpTab.getAssetList().getAsset(TOTAL_VOLUME).setValue("5");
        });
    }
}
