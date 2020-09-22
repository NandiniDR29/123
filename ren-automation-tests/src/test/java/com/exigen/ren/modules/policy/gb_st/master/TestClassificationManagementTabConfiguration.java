package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.PolicyConstants.SubGroups;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabSubGroup.ClassificationManagementTabSubGroupData;
import com.exigen.ren.main.modules.caseprofile.tabs.FileIntakeManagementTab;
import com.exigen.ren.main.modules.caseprofile.tabs.LocationManagementTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.*;
import static com.exigen.ren.main.enums.ActionConstants.CHANGE;
import static com.exigen.ren.main.enums.ActionConstants.REMOVE;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.*;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorTable.MESSAGE;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NY_STAT;
import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.*;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.TableConstants.ClassificationSubGroupsAndRatingColumns.CLASSIFICATION_SUB_GROUP_NAME;
import static com.exigen.ren.main.enums.TableConstants.ClassificationSubGroupsAndRatingColumns.CRITERIA;
import static com.exigen.ren.main.enums.TableConstants.PlansAndCoverages.COVERAGE_NAME;
import static com.exigen.ren.main.enums.ValueConstants.*;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabGroupData.CREATE_SUB_GROUPS_AUTO;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabSubGroup.ClassificationManagementTabSubGroupData.*;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabSubGroup.SUB_GROUP_DETAIS;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.GROUP_DETAILS;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.SUB_GROUP;
import static com.exigen.ren.main.modules.caseprofile.tabs.ClassificationManagementTab.tableClassificationSubGroups;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.ClassificationManagementTabMetaData.UNI_TOBACCO;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.ClassificationManagementTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.UNDER_FIFTY_LIVES;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab.tablePlansAndCoverages;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClassificationManagementTabConfiguration extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    private static final String NUMBER_OF_PARTICIPANTS_IS_REQUIRED = "'Number of Participants' is required";
    private static final String TOTAL_VOLUME_IS_REQUIRED = "'Total Volume' is required";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23530", "REN-23505", "REN-23783", "REN-23856", "REN-23855"}, component = POLICY_GROUPBENEFITS)
    public void testClassificationManagementTabConfigurationForStatNYAndEnhancedNYCoverage() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        TestData dataForPolicy = statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_NY")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName(), PlanDefinitionTabMetaData.COVERAGE_NAME.getLabel()), ENHANCED_NY)
                .adjust(TestData.makeKeyPath(classificationManagementMpTab.getMetaKey()), statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(DATA_GATHER, "ClassificationManagementTab_Enhanced_NY"))
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName(), RATING.getLabel(), NUMBER_OF_LIVES_MALE.getLabel()), "10")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName(), RATING.getLabel(), NUMBER_OF_LIVES_FEMALE.getLabel()), "15")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName(), RATING.getLabel(), USE_EXPERIENCE_RATING.getLabel()), VALUE_YES)
                .resolveLinks();
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(dataForPolicy, PremiumSummaryTab.class, true);

        AbstractContainer<?, ?> assetListForClassificationManagementTab =
                statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().getTab(ClassificationManagementTab.class).getAssetList();

        LOGGER.info("REN-23530 Step 1, 2. REN-23783 Step 1");
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        ClassificationManagementTab.tableClassificationGroupCoverageRelationships.getRow(1)
                .getCell(ClassificationManagementTab.tableClassificationGroupCoverageRelationships.getColumnsCount()).controls.links.get(REMOVE).click();
        Page.dialogConfirmation.confirm();
        assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        assetListForClassificationManagementTab.getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);

        assertSoftly(softly -> {
            tableClassificationSubGroupsAndRatingInfo.getRow(CLASSIFICATION_SUB_GROUP_NAME.getName(), SubGroups.MALE)
                    .getCell(8).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(assetListForClassificationManagementTab.getAsset(NUMBER_OF_PARTICIPANTS)).isRequired().hasValue("10");
            softly.assertThat(assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME)).hasValue(EMPTY);

            tableClassificationSubGroupsAndRatingInfo.getRow(CLASSIFICATION_SUB_GROUP_NAME.getName(), SubGroups.FEMALE)
                    .getCell(8).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(assetListForClassificationManagementTab.getAsset(NUMBER_OF_PARTICIPANTS)).isRequired().hasValue("15");
            softly.assertThat(assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME)).hasValue(EMPTY);
        });

        LOGGER.info("REN-23530 Step 3");
        NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_EXPERIENCE_RATING).setValue(VALUE_NO);
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        assertSoftly(softly -> {
            for (Row row : tableClassificationSubGroupsAndRatingInfo.getRows()) {
                row.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
                softly.assertThat(assetListForClassificationManagementTab.getAsset(NUMBER_OF_PARTICIPANTS)).hasValue(EMPTY);

                LOGGER.info("REN-23530 Step 4. REN-23855 Step 2");
                Tab.buttonNext.click();
                softly.assertThat(assetListForClassificationManagementTab.getAsset(NUMBER_OF_PARTICIPANTS)).hasWarningWithText(NUMBER_OF_PARTICIPANTS_IS_REQUIRED);
            }
        });


        LOGGER.info("REN-23783 Step 2, 3. REN-23855 Step 4, 5");
        ClassificationManagementTab.tableClassificationGroupCoverageRelationships.getRow(1)
                .getCell(ClassificationManagementTab.tableClassificationGroupCoverageRelationships.getColumnsCount()).controls.links.get(REMOVE).click();
        Page.dialogConfirmation.confirm();
        NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RATE_BASIS).setValue(PER_100_MONTHLY_COVERED_PAYROLL);
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        assetListForClassificationManagementTab.getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
        assertSoftly(softly -> {
            for (Row row : tableClassificationSubGroupsAndRatingInfo.getRows()) {
                row.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
                softly.assertThat(assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME)).isRequired().hasValue(EMPTY);

                Tab.buttonNext.click();
                softly.assertThat(assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME)).isRequired().hasWarningWithText(TOTAL_VOLUME_IS_REQUIRED);
            }
        });

        LOGGER.info("REN-23783 Step 5. REN-23856 Step 1, 2. REN-23855 Step 7");
        NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RATE_BASIS).setValue(PER_EMPLOYEE_PER_MONTH);
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        assetListForClassificationManagementTab.getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
        assertSoftly(softly -> {
            for (Row row : tableClassificationSubGroupsAndRatingInfo.getRows()) {
                row.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
                softly.assertThat(assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME)).isOptional();
                softly.assertThat(assetListForClassificationManagementTab.getAsset(RATE)).isDisabled().hasValue("$1.00");

                LOGGER.info("REN-23783 Step 6");
                assetListForClassificationManagementTab.getAsset(NUMBER_OF_PARTICIPANTS).setValue(EMPTY);
                assertThat(assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME)).hasNoWarning();
            }
        });


        LOGGER.info("REN-23856 Step 4");
        Tab.buttonSaveAndExit.click();
        statutoryDisabilityInsuranceMasterPolicy.dataGather().start();
        NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RATE_BASIS).setValue(PER_100_MONTHLY_COVERED_PAYROLL);
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        assetListForClassificationManagementTab.getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
        assertSoftly(softly -> {
            for (Row row : tableClassificationSubGroupsAndRatingInfo.getRows()) {
                row.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
                softly.assertThat(assetListForClassificationManagementTab.getAsset(RATE)).isDisabled().hasValue("1.00000000");
            }
        });

        assertSoftly(softly -> {
            LOGGER.info("REN-23505 Step 1");
            ClassificationManagementTab.tableClassificationGroupCoverageRelationships.getRow(1)
                    .getCell(ClassificationManagementTab.tableClassificationGroupCoverageRelationships.getColumnsCount()).controls.links.get(REMOVE).click();
            Page.dialogConfirmation.confirm();
            NavigationPage.toLeftMenuTab(POLICY_INFORMATION);
            policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES).setValue(VALUE_YES);
            NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
            assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
            assetListForClassificationManagementTab.getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
            softly.assertThat(assetListForClassificationManagementTab.getAsset(USE_CLASSIFICATION_SUB_GROUPS)).isPresent();
            softly.assertThat(assetListForClassificationManagementTab.getAsset(UNISEX)).isPresent();
            softly.assertThat(assetListForClassificationManagementTab.getAsset(UNI_TOBACCO)).isPresent();

        });

        LOGGER.info("REN-23783 Step 1");
        assertSoftly(softly -> {
            for (Row row : tableClassificationSubGroupsAndRatingInfo.getRows()) {
                row.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
                softly.assertThat(assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME)).hasValue(EMPTY);
            }
        });


        LOGGER.info("REN-23505 Step 2");
        NavigationPage.toLeftMenuTab(POLICY_INFORMATION);
        policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES).setValue(VALUE_NO);
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        assertThat(assetListForClassificationManagementTab.getAsset(USE_CLASSIFICATION_SUB_GROUPS))
                .isDisabled().hasValue(VALUE_YES);

        LOGGER.info("REN-23505 Step 3");
        NavigationPage.toLeftMenuTab(POLICY_INFORMATION);
        policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES).setValue(VALUE_YES);

        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RATE_BASIS).setValue("Per Employee Per Month");

        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        assertThat(assetListForClassificationManagementTab.getAsset(USE_CLASSIFICATION_SUB_GROUPS))
                .isDisabled().hasValue(VALUE_YES);

        assertSoftly(softly -> {
            LOGGER.info("REN-23505 Step 4");
            assetListForClassificationManagementTab.getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
            softly.assertThat(assetListForClassificationManagementTab.getAsset(UNISEX)).isDisabled().hasValue(VALUE_NO);

            LOGGER.info("REN-23505 Step 5");
            softly.assertThat(assetListForClassificationManagementTab.getAsset(UNI_TOBACCO)).isDisabled().hasValue(VALUE_YES);
        });

        LOGGER.info("REN-23505 Step 6. REN-23856 Step 5");
        tableClassificationSubGroupsAndRatingInfo.getRows().forEach(row -> {
            row.getCell(tableClassificationSubGroupsAndRatingInfo.getColumnsCount()).controls.links.get(CHANGE).click();
            assetListForClassificationManagementTab.getAsset(NUMBER_OF_PARTICIPANTS).setValue("24");
            assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME).setValue("1");
        });
        classificationManagementMpTab.submitTab();
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-23853", component = POLICY_GROUPBENEFITS)
    public void testFieldRateForStatNYAndEnhancedNYCoverage() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithTwoPlansAutoSubGroup"),
                statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_NY"));
        TestData dataForPolicy = getDefaultSTMasterPolicyData()
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName(), PlanDefinitionTabMetaData.COVERAGE_NAME.getLabel()), ENHANCED_NY)
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName(), RATING.getLabel(), RATE_BASIS.getLabel()), PER_EMPLOYEE_PER_MONTH)
                .resolveLinks();
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(dataForPolicy.adjust(TestData.makeKeyPath(classificationManagementMpTab.getMetaKey()),
                statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(DATA_GATHER, "ClassificationManagementTab_Enhanced_NY")), PremiumSummaryTab.class, true);

        LOGGER.info("REN-23853 Step 1, 2");
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        ClassificationManagementTab.tableClassificationGroupCoverageRelationships.getRow(1)
                .getCell(ClassificationManagementTab.tableClassificationGroupCoverageRelationships.getColumnsCount()).controls.links.get(REMOVE).click();
        Page.dialogConfirmation.confirm();
        AbstractContainer<?, ?> assetListForClassificationManagementTab =
                statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().getTab(ClassificationManagementTab.class).getAssetList();
        assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        assertThat(assetListForClassificationManagementTab.getAsset(RATE)).isDisabled().hasValue("$1.00");

        LOGGER.info("REN-23853 Step 4");
        Tab.buttonSaveAndExit.click();
        statutoryDisabilityInsuranceMasterPolicy.dataGather().start();
        NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RATE_BASIS).setValue(PER_100_MONTHLY_COVERED_PAYROLL);
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        assertThat(assetListForClassificationManagementTab.getAsset(RATE)).isDisabled().hasValue("1.00000000");

        LOGGER.info("REN-23853 Step 5");
        assetListForClassificationManagementTab.getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
        assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME).setValue("34");
        NavigationPage.toLeftMenuTab(PREMIUM_SUMMARY);
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23947", "REN-23949"}, component = POLICY_GROUPBENEFITS)
    public void testFieldsNumberOfParticipantsAndRateForForStatNJCoverage() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        TestData dataForPolicy = statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_NJ")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName(), RATING.getLabel(), NUMBER_OF_LIVES_MALE.getLabel()), "10")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName(), RATING.getLabel(), NUMBER_OF_LIVES_FEMALE.getLabel()), "15")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName(), RATING.getLabel(), USE_EXPERIENCE_RATING.getLabel()), VALUE_YES)
                .resolveLinks();
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(dataForPolicy, PremiumSummaryTab.class, true);

        LOGGER.info("REN-23947 Step 1, 2");
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        ClassificationManagementTab.tableClassificationGroupCoverageRelationships.getRow(1)
                .getCell(ClassificationManagementTab.tableClassificationGroupCoverageRelationships.getColumnsCount()).controls.links.get(REMOVE).click();
        Page.dialogConfirmation.confirm();
        AbstractContainer<?, ?> assetListForClassificationManagementTab =
                statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().getTab(ClassificationManagementTab.class).getAssetList();
        assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        assertThat(assetListForClassificationManagementTab.getAsset(NUMBER_OF_PARTICIPANTS)).isRequired().hasValue("25");

        LOGGER.info("REN-23947 Step 3");
        NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(USE_EXPERIENCE_RATING).setValue(VALUE_NO);
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        assertThat(assetListForClassificationManagementTab.getAsset(NUMBER_OF_PARTICIPANTS)).hasValue(EMPTY);

        LOGGER.info("REN-23947 Step 4");
        assetListForClassificationManagementTab.getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
        Tab.buttonNext.click();
        assertThat(assetListForClassificationManagementTab.getAsset(NUMBER_OF_PARTICIPANTS)).hasWarningWithText(NUMBER_OF_PARTICIPANTS_IS_REQUIRED);

        LOGGER.info("REN-23947 Step 5");
        assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME).setValue("1");
        NavigationPage.toLeftMenuTab(PREMIUM_SUMMARY);
        PremiumSummaryTab.buttonRate.click();
        assertThat(ErrorPage.tableError.getRow(MESSAGE, NUMBER_OF_PARTICIPANTS_IS_REQUIRED).getCell(MESSAGE)).isPresent();
        Tab.buttonCancel.click();

        LOGGER.info("REN-23949 Step 1, 2");
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        ClassificationManagementTab.tableClassificationGroupCoverageRelationships.getRow(1)
                .getCell(ClassificationManagementTab.tableClassificationGroupCoverageRelationships.getColumnsCount()).controls.links.get(REMOVE).click();
        Page.dialogConfirmation.confirm();
        NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RATE_BASIS).setValue(PER_EMPLOYEE_PER_MONTH);
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        assertThat(assetListForClassificationManagementTab.getAsset(RATE)).isDisabled().hasValue("$1.00");

        LOGGER.info("REN-23949 Step 4");
        Tab.buttonSaveAndExit.click();
        statutoryDisabilityInsuranceMasterPolicy.dataGather().start();
        NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RATE_BASIS).setValue(PERCENT_OF_TAXABLE_WAGE);
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        assertThat(assetListForClassificationManagementTab.getAsset(RATE)).isDisabled().hasValue("1.00000000");
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23948", "REN-23946"}, component = POLICY_GROUPBENEFITS)
    public void testTotalVolumeFieldForStatNJCoverage() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        TestData dataForPolicy = statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_NJ")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName(), RATING.getLabel(), RATE_BASIS.getLabel()), PER_EMPLOYEE_PER_MONTH)
                .resolveLinks();
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(dataForPolicy, classificationManagementMpTab.getClass());

        LOGGER.info("REN-23948 Step 1. REN-23946 Step 1");
        AbstractContainer<?, ?> assetListForClassificationManagementTab =
                statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().getTab(ClassificationManagementTab.class).getAssetList();
        assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        assetListForClassificationManagementTab.getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
        assertThat(assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME)).isOptional().hasValue(EMPTY);
        assertThat(assetListForClassificationManagementTab.getAsset(USE_CLASSIFICATION_SUB_GROUPS)).isDisabled().hasValue(VALUE_NO);

        LOGGER.info("REN-23948 Step 2");
        NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RATE_BASIS).setValue(PERCENT_OF_TAXABLE_WAGE);
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);

        assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        assetListForClassificationManagementTab.getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);

        ClassificationManagementTab.updateExistingCoverage("Employee");
        assertThat(classificationManagementMpTab.getAssetList().getAsset(VOLUME)).isRequired().hasValue("$0.00");

        ClassificationManagementTab.updateExistingCoverage("Employer");
        assertThat(classificationManagementMpTab.getAssetList().getAsset(VOLUME)).isRequired().hasValue("$0.00");

        LOGGER.info("REN-23948 Step 3");
        NavigationPage.toLeftMenuTab(PREMIUM_SUMMARY);
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().getTab(premiumSummaryTab.getClass()).fillTab(getDefaultSTMasterPolicyData());
        PremiumSummaryTab.buttonRate.click();
        NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RATE_BASIS).setValue(PER_EMPLOYEE_PER_MONTH);
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);

        assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        assetListForClassificationManagementTab.getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);

        assertThat(assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME)).isOptional().hasValue(EMPTY);

        LOGGER.info("REN-23948 Step 4");
        assetListForClassificationManagementTab.getAsset(NUMBER_OF_PARTICIPANTS).setValue("10");
        Tab.buttonNext.click();

        assertThat(premiumSummaryTab.isTabSelected()).isTrue();

        LOGGER.info("REN-23948 Step 5");
        PremiumSummaryTab.buttonRate.click();

        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23937", "REN-23944", "REN-23931"}, component = POLICY_GROUPBENEFITS)
    public void testClassificationManagementTabConfigurationForPFLAndNYCoverage() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTMasterPolicyData(), PremiumSummaryTab.class, true);

        NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
        planDefinitionTab.getAssetList().getAsset(ADD_COVERAGE).click();
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(NY_STAT);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME).setValue(PFL_NY);
        planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("2");
        planDefinitionTab.getAssetList().getAsset(MEMBER_PAYMENT_MODE).setValue(ImmutableList.of("12"));

        AbstractContainer<?, ?> assetListForClassificationManagementTab =
                statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().getTab(ClassificationManagementTab.class).getAssetList();

        LOGGER.info("REN-23937 Step 1. REN-23931 Step 1");
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        ClassificationManagementTab.tableClassificationGroupCoverageRelationships.getRow(1).getCell(
                ClassificationManagementTab.tableClassificationGroupCoverageRelationships.getColumnsCount()).controls.links.get(REMOVE).click();
        Page.dialogConfirmation.confirm();
        assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        assetListForClassificationManagementTab.getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
        assetListForClassificationManagementTab.getAsset(UNI_TOBACCO).setValue(VALUE_YES);
        assetListForClassificationManagementTab.getAsset(UNISEX).setValue(VALUE_NO);
        assertSoftly(softly -> {
            softly.assertThat(assetListForClassificationManagementTab.getAsset(USE_CLASSIFICATION_SUB_GROUPS)).isDisabled().hasValue(VALUE_YES);
            for (Row row : tableClassificationSubGroupsAndRatingInfo.getRows()) {
                row.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
                softly.assertThat(assetListForClassificationManagementTab.getAsset(NUMBER_OF_PARTICIPANTS)).isRequired().hasValue(EMPTY);
                softly.assertThat(assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME)).isRequired().hasValue(EMPTY);
            }
        });

        LOGGER.info("REN-23937 Step 2");
        assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME).setValue("12");
        Tab.buttonNext.click();
        assertThat(assetListForClassificationManagementTab.getAsset(NUMBER_OF_PARTICIPANTS)).hasWarningWithText(NUMBER_OF_PARTICIPANTS_IS_REQUIRED);

        LOGGER.info("REN-23937 Step 4");
        assetListForClassificationManagementTab.getAsset(NUMBER_OF_PARTICIPANTS).setValue("1");
        assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME).setValue(EMPTY);
        Tab.buttonNext.click();
        assertThat(assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME)).isRequired().hasWarningWithText(TOTAL_VOLUME_IS_REQUIRED);

        assertSoftly(softly -> {
            LOGGER.info("REN-23937 Step 6");
            assetListForClassificationManagementTab.getAsset(NUMBER_OF_PARTICIPANTS).setValue("24");
            assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME).setValue("12");
            softly.assertThat(assetListForClassificationManagementTab.getAsset(TOTAL_VOLUME)).hasNoWarning();
            softly.assertThat(assetListForClassificationManagementTab.getAsset(NUMBER_OF_PARTICIPANTS)).hasNoWarning();
        });

        LOGGER.info("REN-23944 Step 1, 2");
        tablePlansAndCoverages.getRow(COVERAGE_NAME.getName(), STAT_NY)
                .getCell(1).click();
        ClassificationManagementTab.tableClassificationGroupCoverageRelationships.getRow(1).getCell(
                ClassificationManagementTab.tableClassificationGroupCoverageRelationships.getColumnsCount()).controls.links.get(REMOVE).click();
        Page.dialogConfirmation.confirm();
        NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RATE_BASIS).setValue(PER_EMPLOYEE_PER_MONTH);
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        assetListForClassificationManagementTab.getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
        assertThat(assetListForClassificationManagementTab.getAsset(RATE)).isDisabled().hasValue("$1.00");

        LOGGER.info("REN-23944 Step 4");
        Tab.buttonSaveAndExit.click();
        statutoryDisabilityInsuranceMasterPolicy.dataGather().start();
        NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RATE_BASIS).setValue(PER_100_MONTHLY_COVERED_PAYROLL);
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        tablePlansAndCoverages.getRow(COVERAGE_NAME.getName(), STAT_NY).getCell(COVERAGE_NAME.getName()).click();
        assetListForClassificationManagementTab.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        assetListForClassificationManagementTab.getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
        assertThat(assetListForClassificationManagementTab.getAsset(RATE)).hasValue("1.00000000");

        for (Row row : ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo.getRows()) {
            row.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
        }

        tablePlansAndCoverages.getRow(COVERAGE_NAME.getName(), PFL_NY).getCell(COVERAGE_NAME.getName()).click();
        for (Row row : ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo.getRows()) {
            row.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
        }

        LOGGER.info("REN-23944 Step 5. REN-23931 Step 2");
        Tab.buttonNext.click();
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
    }


    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-23952", component = POLICY_GROUPBENEFITS)
    public void testSubGroupsForStatNYAndEnhancedNYCoverage() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.initiate();
        TestData testDataForCase = caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_AutoSubGroup")
                .adjust(TestData.makeKeyPath(productAndPlanManagementTab.getClass().getSimpleName(), "Product"), statutoryDisabilityInsuranceMasterPolicy.getType().getName())
                .adjust(TestData.makeKeyPath(classificationManagementTab.getClass().getSimpleName() + "[0]", "Group Details", "Create Sub-Groups Automatically?"), VALUE_NO)
                .resolveLinks();
        caseProfile.getDefaultWorkspace().fillUpTo(testDataForCase, classificationManagementTab.getClass(), true);

        assertSoftly(softly -> {
            LOGGER.info("REN-23952 Step 1");
            softly.assertThat(tableClassificationSubGroups).hasRows(0);

            LOGGER.info("REN-23952 Step 2");
            classificationManagementTab.getAssetList().getAsset(GROUP_DETAILS).getAsset(CREATE_SUB_GROUPS_AUTO).setValue(VALUE_YES);
            tableClassificationSubGroups.getRows().forEach(row -> {
                row.getCell(1).controls.links.getFirst().click();
                softly.assertThat(classificationManagementTab.getAssetList().getAsset(SUB_GROUP).getAsset(SUB_GROUP_DETAIS).getAsset(GENDER)).isDisabled();
                softly.assertThat(classificationManagementTab.getAssetList().getAsset(SUB_GROUP).getAsset(SUB_GROUP_DETAIS).getAsset(TOBACCO)).isDisabled();
                softly.assertThat(classificationManagementTab.getAssetList().getAsset(SUB_GROUP).getAsset(SUB_GROUP_DETAIS).getAsset(MIN_AGE)).isDisabled();
                softly.assertThat(classificationManagementTab.getAssetList().getAsset(SUB_GROUP).getAsset(SUB_GROUP_DETAIS).getAsset(MAX_AGE)).isDisabled();
                softly.assertThat(classificationManagementTab.getAssetList().getAsset(SUB_GROUP).getAsset(SUB_GROUP_DETAIS)
                        .getAsset(ClassificationManagementTabSubGroupData.UNI_TOBACCO)).isDisabled();
                softly.assertThat(classificationManagementTab.getAssetList().getAsset(SUB_GROUP).getAsset(SUB_GROUP_DETAIS).getAsset(UNI_SEX)).isDisabled();
            });
        });

        assertSoftly(softly -> {
            LOGGER.info("REN-23952 Step 3, 4");
            com.exigen.ren.main.modules.caseprofile.tabs.ClassificationManagementTab.buttonNext.click();
            caseProfile.getDefaultWorkspace().fillFromTo(testDataForCase, LocationManagementTab.class, FileIntakeManagementTab.class, true);
            Tab.buttonSaveAndFinalize.click();
            statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
            TestData dataForPolicy = getDefaultSTMasterPolicyData()
                    .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName(), PlanDefinitionTabMetaData.COVERAGE_NAME.getLabel()), ENHANCED_NY)
                    .resolveLinks();
            statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(dataForPolicy, PlanDefinitionTab.class);
            NavigationPage.toLeftMenuTab(POLICY_INFORMATION);
            policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES).setValue(VALUE_YES);
            NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);

            LOGGER.info("REN-23952 Step 5, 6");
            classificationManagementMpTab.getAssetList().getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
            classificationManagementMpTab.getAssetList().getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
            String classificationSubGroupColumnName = CLASSIFICATION_SUB_GROUP_NAME.getName();
            softly.assertThat(tableClassificationSubGroupsAndRatingInfo.getRow(classificationSubGroupColumnName, "Male")
                    .getCell(CRITERIA.getName())).hasValue("Gender = Male; Tobacco = N/A; Min Age = 1 Max Age = 98");
            softly.assertThat(tableClassificationSubGroupsAndRatingInfo.getRow(classificationSubGroupColumnName, "Female")
                    .getCell(CRITERIA.getName())).hasValue("Gender = Female; Tobacco = N/A; Min Age = 1 Max Age = 98");
            softly.assertThat(tableClassificationSubGroupsAndRatingInfo.getRow(classificationSubGroupColumnName, "Proprietor")
                    .getCell(CRITERIA.getName())).hasValue("Gender = N/A; Tobacco = N/A; Min Age = 99 Max Age =");
        });
    }
}
