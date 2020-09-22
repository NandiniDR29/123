package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.ClassificationManagementTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.ClassificationManagementTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteClassificationSubGroups extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    private static final String NUMBER_OF_PARTICIPANTS_VALUE = "0";
    private static final String TOTAL_VOLUME_VALUE = "0";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13215", component = POLICY_GROUPBENEFITS)
    public void testQuoteClassificationNoSubGroups() {
        preconditions();

        MultiAssetList classManagementTabAssetList = (MultiAssetList) classificationManagementMpTab.getAssetList();
        assertSoftly(softly -> {
            // For each row from table 'Plans And Coverages' we click on plan and do actions and assert fields
            ClassificationManagementTab.tablePlansAndCoverages.getRows().forEach(row -> {
                row.getCell(TableConstants.PlansAndCoverages.PLAN.getName()).click();
                classManagementTabAssetList.getAsset(ClassificationManagementTabMetaData.ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();

                LOGGER.info("REN-13215 Steps 1");
                classManagementTabAssetList.getAsset(ClassificationManagementTabMetaData.CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
                classManagementTabAssetList.getAsset(ClassificationManagementTabMetaData.USE_CLASSIFICATION_SUB_GROUPS).setValue(VALUE_NO);
                checkSubGroupsAndRatingInfoFields(softly, classManagementTabAssetList);
                Tab.buttonTopSave.click();
                checkGroupCoverageRelationshipsTable(softly);
            });
        });

        LOGGER.info("REN-13215 Steps 7, 8");
        Tab.buttonSaveAndExit.click();
        checkCalculateAndIssuePolicy();
    }

    private void preconditions() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile")
                        .adjust(classificationManagementTab.getMetaKey(), ImmutableList.of(caseProfile.getDefaultTestData("CaseProfile", "ClassificationManagementTab_1_WithAutoGroups"))),
                shortTermDisabilityMasterPolicy.getType());
        initQuoteWithFillUpToClassificationManagementTab(getDefaultSTDMasterPolicyData(),
                shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestDataPlanDefinitionTabWithThreePlans"));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13215", component = POLICY_GROUPBENEFITS)
    public void testQuoteClassificationSubGroups() {
        preconditions();
        MultiAssetList classManagementTabAssetList = (MultiAssetList) classificationManagementMpTab.getAssetList();

        assertSoftly(softly -> {
            // For each row from table 'Plans And Coverages' we click on plan and do actions and assert fields
            ClassificationManagementTab.tablePlansAndCoverages.getRows().forEach(row -> {
                row.getCell(TableConstants.PlansAndCoverages.PLAN.getName()).click();
                classManagementTabAssetList.getAsset(ClassificationManagementTabMetaData.ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
                classManagementTabAssetList.getAsset(ClassificationManagementTabMetaData.CLASSIFICATION_GROUP_NAME).setValueByIndex(1);

                LOGGER.info("REN-13215 Step 2, 3");
                classManagementTabAssetList.getAsset(ClassificationManagementTabMetaData.USE_CLASSIFICATION_SUB_GROUPS).setValue(VALUE_YES);
                softly.assertThat(classManagementTabAssetList.getAsset(UNI_TOBACCO)).isPresent().hasValue("");
                softly.assertThat(classManagementTabAssetList.getAsset(UNISEX)).isPresent().hasValue("");
                softly.assertThat(classManagementTabAssetList.getAsset(NUMBER_OF_PARTICIPANTS)).isAbsent();
                softly.assertThat(classManagementTabAssetList.getAsset(TOTAL_VOLUME)).isAbsent();
                softly.assertThat(classManagementTabAssetList.getAsset(RATE)).isAbsent();

                LOGGER.info("REN-12467 Step 4");
                classManagementTabAssetList.getAsset(UNI_TOBACCO).setValue("Yes");
                classManagementTabAssetList.getAsset(UNISEX).setValue("Yes");
                softly.assertThat(ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo).isPresent();

                LOGGER.info("REN-12467 Step 5, 6, 7");
                checkSubGroupsAndRatingInfoFields(softly, classManagementTabAssetList);
                Tab.buttonTopSave.click();
                checkGroupCoverageRelationshipsTable(softly);
            });
        });

        LOGGER.info("REN-12467 Step 7, 8");
        Tab.buttonSaveAndExit.click();
        checkCalculateAndIssuePolicy();
    }

    private void initQuoteWithFillUpToClassificationManagementTab(TestData tdInit, TestData tdFillUp) {
        shortTermDisabilityMasterPolicy.initiate(tdInit);
        shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(tdFillUp, classificationManagementMpTab.getClass());
    }

    private void checkSubGroupsAndRatingInfoFields(ETCSCoreSoftAssertions softly, MultiAssetList classManagmTabAssetList) {
        softly.assertThat(classManagmTabAssetList.getAsset(NUMBER_OF_PARTICIPANTS))
                .isPresent().isDisabled().isOptional().hasValue(NUMBER_OF_PARTICIPANTS_VALUE);
        softly.assertThat(classManagmTabAssetList.getAsset(TOTAL_VOLUME))
                .isPresent().isDisabled().isOptional().hasValue(new Currency(TOTAL_VOLUME_VALUE).toString());
    }

    private void checkGroupCoverageRelationshipsTable(ETCSCoreSoftAssertions softly) {
        softly.assertThat(ClassificationManagementTab.tableCoverageRelationships)
                .hasMatchingRows(1, ImmutableMap.of(
                        TableConstants.CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName(), NUMBER_OF_PARTICIPANTS_VALUE,
                        TableConstants.CoverageRelationships.TOTAL_VOLUME.getName(), new Currency(TOTAL_VOLUME_VALUE).toString()));
    }

    private void checkCalculateAndIssuePolicy() {
        shortTermDisabilityMasterPolicy.dataGather().start();
        premiumSummaryTab.navigate();
        premiumSummaryTab.fillTab(getDefaultSTDMasterPolicyData()).submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        shortTermDisabilityMasterPolicy.propose().perform(getDefaultSTDMasterPolicyData());
        shortTermDisabilityMasterPolicy.acceptContract().perform(getDefaultSTDMasterPolicyData());
        shortTermDisabilityMasterPolicy.issue().perform(getDefaultSTDMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}