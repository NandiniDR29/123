package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationBindActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationBindActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationIssueActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.EnrollmentTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.NameToDisplayOnMPDocumentsValues.*;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.*;
import static com.exigen.ren.main.enums.ValueConstants.*;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.MasterQuote.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyInformationTabNewAttributePropertiesVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    private static final AbstractContainer<?, ?> acceptContractAssetList = groupAccidentMasterPolicy.acceptContract().getWorkspace().getTab(PolicyInformationBindActionTab.class).getAssetList();
    private static final AbstractContainer<?, ?> issueAssetList = groupAccidentMasterPolicy.issue().getWorkspace().getTab(PolicyInformationIssueActionTab.class).getAssetList();

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-32684"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyInformationTabNewAttributePropertiesVerification_TC1() {
        LOGGER.info("General Preconditions");
        initiateACMasterPolicy();

        LOGGER.info("Scenario#1 Steps#1, 2 verification");
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY)).isRequired().isPresent().hasValue(VALUE_NO);

        LOGGER.info("Scenario#1 Steps#6, 7 verification");
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.SMALL_GROUP)).isRequired().isPresent().isEnabled().hasValue(VALUE_NO);

        LOGGER.info("Scenario#1 Step#8 verification");
        groupAccidentMasterPolicy.getDefaultWorkspace().fillFrom(getDefaultACMasterPolicyData(), PolicyInformationTab.class);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("Scenario#1 Step#9 verification");
        assertThat(PolicySummaryPage.tableGeneralInformation.getHeader())
                .doesNotHaveValue(ImmutableList.of(GROUP_IS_MEMBER_COMPANY.getName(), MEMBER_COMPANY_NAME.getName(), NAME_TO_DISPLAY_ON_MP_DOCUMENTS.getName(), SMALL_GROUP.getName()));

        LOGGER.info("Scenario#1 Steps#10, 11, 12 verification");
        proposeAndAcceptContractInitiate();

        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(acceptContractAssetList.getAsset(PolicyInformationBindActionTabMetaData.GROUP_IS_MEMBER_COMPANY)).isPresent().isDisabled().hasValue(VALUE_NO);
            softly.assertThat(acceptContractAssetList.getAsset(PolicyInformationBindActionTabMetaData.SMALL_GROUP)).isPresent().isDisabled().hasValue(VALUE_NO);
        });

        LOGGER.info("Scenario#1 Steps#14, 15 verification");
        completeAcceptContractAndIssueInitiate();

        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(issueAssetList.getAsset(PolicyInformationIssueActionTabMetaData.GROUP_IS_MEMBER_COMPANY)).isPresent().isDisabled().hasValue(VALUE_NO);
            softly.assertThat(issueAssetList.getAsset(PolicyInformationIssueActionTabMetaData.SMALL_GROUP)).isPresent().isDisabled().hasValue(VALUE_NO);
        });

        LOGGER.info("Scenario#1 Step#16 verification");
        groupAccidentMasterPolicy.issue().getWorkspace().fillFrom(getDefaultACMasterPolicyData(), PolicyInformationIssueActionTab.class);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-32684"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyInformationTabNewAttributePropertiesVerification_TC2() {
        LOGGER.info("General Preconditions");
        String relationship1 = tdSpecific().getValue("RelationshipTestData1", RelationshipTabMetaData.NAME_LEGAL.getLabel());
        String relationship2 = tdSpecific().getValue("RelationshipTestData2", RelationshipTabMetaData.NAME_LEGAL.getLabel());

        initiateACMasterPolicy();

        LOGGER.info("Scenario#2 Steps#1, 2, 3 verification");
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY).setValue(VALUE_YES);
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).isPresent().isRequired().hasValue(EMPTY);

        LOGGER.info("Scenario#2 Step#5 verification");
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME).getAllValues())
                .containsOnly(EMPTY, relationship1, relationship2);

        LOGGER.info("Scenario#2 Step#6 verification");
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isAbsent();

        LOGGER.info("Scenario#2 Step#8 verification");
        groupAccidentMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class).fillTab(getDefaultACMasterPolicyData());
        enrollmentTab.navigateToTab();
        groupAccidentMasterPolicy.getDefaultWorkspace().fillFrom(getDefaultACMasterPolicyData(), EnrollmentTab.class);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("Scenario#2 Step#10 verification");
        groupAccidentMasterPolicy.dataGather().start();
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME).setValue(relationship2);
        premiumSummaryTab.navigateToTab();
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("Scenario#2 Step#11 execution");
        proposeAndAcceptContractInitiate();

        CustomSoftAssertions.assertSoftly(softly -> {

            LOGGER.info("Scenario#2 Step#12 verification");
            softly.assertThat(acceptContractAssetList.getAsset(PolicyInformationBindActionTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isAbsent();

            LOGGER.info("Scenario#2 Step#13 verification");
            softly.assertThat(acceptContractAssetList.getAsset(PolicyInformationBindActionTabMetaData.GROUP_IS_MEMBER_COMPANY)).isPresent().isDisabled();
            softly.assertThat(acceptContractAssetList.getAsset(PolicyInformationBindActionTabMetaData.MEMBER_COMPANY_NAME)).isPresent().isDisabled();
            softly.assertThat(acceptContractAssetList.getAsset(PolicyInformationBindActionTabMetaData.SMALL_GROUP)).isPresent().isDisabled();
        });

        LOGGER.info("Scenario#2 Steps#14 verification");
        completeAcceptContractAndIssueInitiate();

        CustomSoftAssertions.assertSoftly(softly -> {
            LOGGER.info("Scenario#2 Steps#15, 16, 17 verification");
            softly.assertThat(issueAssetList.getAsset(PolicyInformationIssueActionTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS))
                    .isPresent().isRequired().isEnabled().hasValue(MEMBER_COMPANY).hasOptions(GROUP_SPONSOR, MEMBER_COMPANY);

            LOGGER.info("Scenario#2 Step#18 verification");
            softly.assertThat(issueAssetList.getAsset(PolicyInformationIssueActionTabMetaData.GROUP_IS_MEMBER_COMPANY)).isPresent().isDisabled();
            softly.assertThat(issueAssetList.getAsset(PolicyInformationIssueActionTabMetaData.MEMBER_COMPANY_NAME)).isPresent().isDisabled();
            softly.assertThat(issueAssetList.getAsset(PolicyInformationIssueActionTabMetaData.SMALL_GROUP)).isPresent().isDisabled();
        });

        LOGGER.info("Scenario#2 Step#19 verification");
        groupAccidentMasterPolicy.issue().getWorkspace().fillFrom(getDefaultACMasterPolicyData(), PolicyInformationIssueActionTab.class);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
    }

    private void initiateACMasterPolicy() {
        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithRelationshipTypes")
                .adjust(tdSpecific().getTestData("TestData").resolveLinks()).resolveLinks());
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
    }

    private void proposeAndAcceptContractInitiate() {
        groupAccidentMasterPolicy.propose().perform(getDefaultACMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PROPOSED);
        groupAccidentMasterPolicy.acceptContract().start();
    }

    private void completeAcceptContractAndIssueInitiate() {
        groupAccidentMasterPolicy.acceptContract().getWorkspace().fillFrom(getDefaultACMasterPolicyData(), PolicyInformationBindActionTab.class);
        PremiumSummaryBindActionTab.buttonNext.click();
        groupAccidentMasterPolicy.issue().start();
    }
}
