/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.billing.account.tabs.ConsolidatedStatementDetailsActionTab;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.common.pages.Page.dialogConfirmation;
import static com.exigen.ren.main.enums.ActionConstants.REMOVE;
import static com.exigen.ren.main.enums.ActionConstants.UPDATE;
import static com.exigen.ren.main.enums.BamConstants.*;
import static com.exigen.ren.main.enums.CaseProfileConstants.ErrorMessages.NO_RECORD_FOUND;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.BILLING_ACCOUNT_NAME;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.CREATE_NEW_BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.ConsolidatedStatementDetailsTabMetaData.*;
import static com.exigen.ren.main.modules.billing.account.metadata.ConsolidatedStatementDetailsTabMetaData.AddBillingAccountsMetaData.BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.tabs.ConsolidatedStatementDetailsActionTab.*;
import static com.exigen.ren.main.modules.billing.account.tabs.ManageConsolidatedStatementsActionTab.tableManageConsolidatedStatements;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.CONTRIBUTION_TYPE;
import static com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage.labelNoRecordsMessage;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUpdateConsolidatedStatementSetting extends GroupBenefitsBillingBaseTest implements BillingAccountContext {


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36469", component = BILLING_GROUPBENEFITS)
    public void testGenerateConsolidatedStatementTC1() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        billingAccount.navigateToBillingAccount();
        assertThat(labelNoRecordsMessage).hasValue(NO_RECORD_FOUND);

        billingAccount.manageConsolidatedStatementsAction().start();
        assertThat(tableManageConsolidatedStatements.getRow(1).getCell(1)).hasValue(NO_RECORD_FOUND);

        buttonBack.click();
        assertThat(labelNoRecordsMessage).hasValue(NO_RECORD_FOUND);
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36469", component = BILLING_GROUPBENEFITS)
    public void testGenerateConsolidatedStatementTC2() {

        String ba1Name = "BA1";
        String ba2Name = "BA2";
        String ba3Name = "BA3";
        String ba4Name = "BA4";
        String ba5Name = "BA5";
        String ba6Name = "BA6";
        String ba7Name = "BA7";
        String ba8Name = "BA8";

        mainApp().open();
        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        LOGGER.info("Self administered policy MP1(cov1, cov2, cov3) is issued and assigned to BA1, BA2, BA3 (cov1 > BA1, cov2 > BA2, cov3 > BA3).");
        TestData td = groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER_SELF_ADMIN, "TestData_AllPlans")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY));

        td.adjust(TestData.makeKeyPath(BillingAccountTab.class.getSimpleName()), ImmutableList.of(
                tdSpecific().getTestData("BillingAccountTab").adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), BILLING_ACCOUNT_NAME.getLabel()), ba1Name),
                tdSpecific().getTestData("BillingAccountTab").adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), BILLING_ACCOUNT_NAME.getLabel()), ba2Name)));
        td.getTestDataList(planDefinitionTab.getClass().getSimpleName()).get(1)
                .adjust(TestData.makeKeyPath(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Non-contributory");

        groupAccidentMasterPolicy.createPolicy(td);
        groupAccidentMasterPolicy.setupBillingGroups().start();
        groupAccidentMasterPolicy.setupBillingGroups().getWorkspace().fill(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));

        LOGGER.info("Self administered policy MP2(cov4, cov5) is issued and assigned to BA4, BA5 (cov4 > BA4, cov5 > BA5).");
        TestData td2 = groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER_SELF_ADMIN, "TestData_AllPlans")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY));

        td2.adjust(TestData.makeKeyPath(BillingAccountTab.class.getSimpleName()), ImmutableList.of(
                tdSpecific().getTestData("BillingAccountTab").adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), BILLING_ACCOUNT_NAME.getLabel()), ba4Name),
                tdSpecific().getTestData("BillingAccountTab").adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), BILLING_ACCOUNT_NAME.getLabel()), ba5Name)));

        td2.getTestDataList(planDefinitionTab.getMetaKey()).get(1)
                .adjust(TestData.makeKeyPath(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Non-contributory");

        groupAccidentMasterPolicy.createPolicy(td2);

        groupAccidentMasterPolicy.setupBillingGroups().start();
        groupAccidentMasterPolicy.setupBillingGroups().getWorkspace().fill(tdSpecific().getTestData("TestData_For_BA6"));

        LOGGER.info("Full administered policy MP3(cov7) is issued and assigned to BA7 (cov7 > BA7).");
        TestData td3 = groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_TwoCoverages")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY));

        td3.adjust(TestData.makeKeyPath(BillingAccountTab.class.getSimpleName()), ImmutableList.of(
                tdSpecific().getTestData("BillingAccountTab").adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), BILLING_ACCOUNT_NAME.getLabel()), ba7Name),
                tdSpecific().getTestData("BillingAccountTab").adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), BILLING_ACCOUNT_NAME.getLabel()), ba8Name)));
        groupAccidentMasterPolicy.createPolicy(td3);

        LOGGER.info("---=={Step 1}==---");
        billingAccount.navigateToBillingAccount();

        assertThat(BillingAccountsListPage.tableBenefitAccounts).hasRows(8);
        String ba1 = BillingAccountsListPage.tableBenefitAccounts.getRow(ImmutableMap.of(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT_NAME.getName(), ba1Name)).getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).getValue();
        String ba2 = BillingAccountsListPage.tableBenefitAccounts.getRow(ImmutableMap.of(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT_NAME.getName(), ba2Name)).getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).getValue();
        String ba3 = BillingAccountsListPage.tableBenefitAccounts.getRow(ImmutableMap.of(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT_NAME.getName(), ba3Name)).getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).getValue();
        String ba4 = BillingAccountsListPage.tableBenefitAccounts.getRow(ImmutableMap.of(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT_NAME.getName(), ba4Name)).getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).getValue();
        String ba5 = BillingAccountsListPage.tableBenefitAccounts.getRow(ImmutableMap.of(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT_NAME.getName(), ba5Name)).getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).getValue();
        String ba6 = BillingAccountsListPage.tableBenefitAccounts.getRow(ImmutableMap.of(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT_NAME.getName(), ba6Name)).getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).getValue();
        String ba7 = BillingAccountsListPage.tableBenefitAccounts.getRow(ImmutableMap.of(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT_NAME.getName(), ba7Name)).getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).getValue();
        String ba8 = BillingAccountsListPage.tableBenefitAccounts.getRow(ImmutableMap.of(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT_NAME.getName(), ba8Name)).getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).getValue();

        LOGGER.info("BA1 has Consolidated statement setting (with included accounts: BA2, BA3, BA5).");
        billingAccount.setUpConsolidatedStatement().start();
        AbstractContainer<?, ?> consolidatedStatement = billingAccount.setUpConsolidatedStatement().getWorkspace().getTab(ConsolidatedStatementDetailsActionTab.class).getAssetList();
        consolidatedStatement.getAsset(CONSOLIDATED_STATEMENT_ENABLED_FROM).setValueContains(ba1);
        consolidatedStatement.getAsset(ADD_BUTTOM).click();
        tableRelatedBillingAccounts.getRow(ImmutableMap.of(TableConstants.RelatedBillingAccounts.BILLING_ACCOUNT.getName(), ba2)).getCell(1).click();
        tableRelatedBillingAccounts.getRow(ImmutableMap.of(TableConstants.RelatedBillingAccounts.BILLING_ACCOUNT.getName(), ba3)).getCell(1).click();
        consolidatedStatement.getAsset(ADD_BILLING_ACCOUNTS).getAsset(BILLING_ACCOUNT).setValue(ba5);
        buttonSearch.click();
        tableSearchResults.getRow(ImmutableMap.of(TableConstants.RelatedBillingAccounts.BILLING_ACCOUNT.getName(), ba5)).getCell(1).click();
        buttonSelect.click();
        billingAccount.setUpConsolidatedStatement().submit();

        LOGGER.info("BA4 has Consolidated statement setting (with included account: BA1).");
        billingAccount.setUpConsolidatedStatement().perform(billingAccount.getDefaultTestData("SetUpConsolidatedStatement", DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(ConsolidatedStatementDetailsActionTab.class.getSimpleName(), CONSOLIDATED_STATEMENT_ENABLED_FROM.getLabel()),  String.format("contains=%s", ba4))
                .adjust(TestData.makeKeyPath(ConsolidatedStatementDetailsActionTab.class.getSimpleName(), ADD_BILLING_ACCOUNTS.getLabel()),
                        billingAccount.getDefaultTestData("SetUpConsolidatedStatement", "BillingAccount").adjust(TestData.makeKeyPath(BILLING_ACCOUNT.getLabel()), ba1))
                .resolveLinks());

        LOGGER.info("BA7 has Consolidated statement setting (with included account: BA8).");
        billingAccount.setUpConsolidatedStatement().start();
        consolidatedStatement.getAsset(CONSOLIDATED_STATEMENT_ENABLED_FROM).setValueContains(ba7);
        consolidatedStatement.getAsset(ADD_BUTTOM).click();
        tableRelatedBillingAccounts.getRow(ImmutableMap.of(TableConstants.RelatedBillingAccounts.BILLING_ACCOUNT.getName(), ba8)).getCell(1).click();
        buttonSelect.click();
        billingAccount.setUpConsolidatedStatement().submit();

        LOGGER.info("---=={Step 1}==---");
        assertThat(BillingAccountsListPage.tableBenefitAccounts).hasRows(8);

        LOGGER.info("---=={Step 2}==---");
        billingAccount.manageConsolidatedStatementsAction().start();
        assertThat(tableManageConsolidatedStatements).hasRows(3);
        assertThat(tableManageConsolidatedStatements).with(TableConstants.IncludeBAInTheConsolidatedStatement.BILLING_ACCOUNT, ba1).hasMatchingRows(1);
        assertThat(tableManageConsolidatedStatements).with(TableConstants.IncludeBAInTheConsolidatedStatement.BILLING_ACCOUNT, ba4).hasMatchingRows(1);
        assertThat(tableManageConsolidatedStatements).with(TableConstants.IncludeBAInTheConsolidatedStatement.BILLING_ACCOUNT, ba7).hasMatchingRows(1);

        LOGGER.info("---=={Step 4}==---");
        tableManageConsolidatedStatements.getRow(ImmutableMap.of(TableConstants.IncludeBAInTheConsolidatedStatement.BILLING_ACCOUNT.getName(), ba7))
                .getCell(TableConstants.IncludeBAInTheConsolidatedStatement.ACTIONS.getName()).controls.links.get(REMOVE).click();
        dialogConfirmation.buttonCancel.click();
        billingAccount.manageConsolidatedStatementsAction().submit();

        LOGGER.info("---=={Step 5}==---");
        MainPage.QuickSearch.search(ba7);
        BillingAccountsListPage.verifyBamActivities(CONSOLIDATED_STATEMENT_REMOVED, CANCELLED);

        LOGGER.info("---=={Step 6}==---");
        billingAccount.navigateToBillingAccount();
        BillingAccountsListPage.verifyBamActivities(String.format(CONSOLIDATED_STATEMENT_REMOVED_BA, ba7), CANCELLED);

        LOGGER.info("---=={Step 7}==---");
        billingAccount.manageConsolidatedStatementsAction().start();
        tableManageConsolidatedStatements.getRow(ImmutableMap.of(TableConstants.IncludeBAInTheConsolidatedStatement.BILLING_ACCOUNT.getName(), ba7))
                .getCell(TableConstants.IncludeBAInTheConsolidatedStatement.ACTIONS.getName()).controls.links.get(REMOVE).click();
        dialogConfirmation.confirm();
        assertThat(tableManageConsolidatedStatements).hasRows(2);
        assertThat(tableManageConsolidatedStatements).with(TableConstants.IncludeBAInTheConsolidatedStatement.BILLING_ACCOUNT, ba1).hasMatchingRows(1);
        assertThat(tableManageConsolidatedStatements).with(TableConstants.IncludeBAInTheConsolidatedStatement.BILLING_ACCOUNT, ba4).hasMatchingRows(1);
        billingAccount.manageConsolidatedStatementsAction().submit();

        LOGGER.info("---=={Step 8}==---");
        MainPage.QuickSearch.search(ba7);
        BillingAccountsListPage.verifyBamActivities(CONSOLIDATED_STATEMENT_REMOVED, FINISHED);

        LOGGER.info("---=={Step 9}==---");
        billingAccount.navigateToBillingAccount();
        BillingAccountsListPage.verifyBamActivities(String.format(CONSOLIDATED_STATEMENT_REMOVED_BA, ba7), FINISHED);

        LOGGER.info("---=={Step 10}==---");
        billingAccount.manageConsolidatedStatementsAction().start();
        tableManageConsolidatedStatements.getRow(ImmutableMap.of(TableConstants.IncludeBAInTheConsolidatedStatement.BILLING_ACCOUNT.getName(), ba4))
                .getCell(TableConstants.IncludeBAInTheConsolidatedStatement.ACTIONS.getName()).controls.links.get(UPDATE).click();

        LOGGER.info("---=={Step 11}==---");
        tableIncludeBAInTheConsolidatedStatement.getRow(ImmutableMap.of(TableConstants.IncludeBAInTheConsolidatedStatement.BILLING_ACCOUNT.getName(), ba1))
                .getCell(TableConstants.IncludeBAInTheConsolidatedStatement.ACTIONS.getName()).controls.links.get(ActionConstants.REMOVE).click();
        dialogConfirmation.confirm();

        assertThat(buttonSetUpUpdate).isDisabled();
        assertThat(consolidatedStatement.getAsset(CONSOLIDATED_STATEMENT_ENABLED_FROM)).isDisabled();
        navButtonCancel.click();
        assertThat(tableManageConsolidatedStatements).hasRows(2);

        LOGGER.info("---=={Step 12}==---");
        MainPage.QuickSearch.search(ba4);
        BillingAccountsListPage.verifyBamActivities(CONSOLIDATED_STATEMENT_UPDATE, CANCELLED);

        LOGGER.info("---=={Step 13}==---");
        billingAccount.navigateToBillingAccount();
        BillingAccountsListPage.verifyBamActivities(String.format(CONSOLIDATED_STATEMENT_UPDATE_BA, ba4), CANCELLED);

        LOGGER.info("---=={Step 14}==---");
        billingAccount.manageConsolidatedStatementsAction().start();
        tableManageConsolidatedStatements.getRow(ImmutableMap.of(TableConstants.IncludeBAInTheConsolidatedStatement.BILLING_ACCOUNT.getName(), ba4))
                .getCell(TableConstants.IncludeBAInTheConsolidatedStatement.ACTIONS.getName()).controls.links.get(UPDATE).click();

        LOGGER.info("---=={Step 15}==---");
        tableIncludeBAInTheConsolidatedStatement.getRow(ImmutableMap.of(TableConstants.IncludeBAInTheConsolidatedStatement.BILLING_ACCOUNT.getName(), ba1))
                .getCell(TableConstants.IncludeBAInTheConsolidatedStatement.ACTIONS.getName()).controls.links.get(ActionConstants.REMOVE).click();
        dialogConfirmation.confirm();

        consolidatedStatement.getAsset(ADD_BUTTOM).click();
        tableRelatedBillingAccounts.getRow(ImmutableMap.of(TableConstants.RelatedBillingAccounts.BILLING_ACCOUNT.getName(), ba6)).getCell(1).click();
        consolidatedStatement.getAsset(ADD_BILLING_ACCOUNTS).getAsset(BILLING_ACCOUNT).setValue(ba8);
        buttonSearch.click();
        assertThat(tableSearchResults.getRow(1).getCell(1)).hasValue(NO_RECORD_FOUND);

        consolidatedStatement.getAsset(ADD_BILLING_ACCOUNTS).getAsset(BILLING_ACCOUNT).setValue(ba2);
        buttonSearch.click();
        tableSearchResults.getRow(ImmutableMap.of(TableConstants.RelatedBillingAccounts.BILLING_ACCOUNT.getName(), ba2)).getCell(1).click();
        buttonSelect.click();
        assertThat(tableIncludeBAInTheConsolidatedStatement).hasRows(2);
        buttonSetUpUpdate.click();

        LOGGER.info("---=={Step 16}==---");
        MainPage.QuickSearch.search(ba4);
        BillingAccountsListPage.verifyBamActivities(CONSOLIDATED_STATEMENT_UPDATE, FINISHED);

        LOGGER.info("---=={Step 17}==---");
        billingAccount.navigateToBillingAccount();
        BillingAccountsListPage.verifyBamActivities(String.format(CONSOLIDATED_STATEMENT_UPDATE_BA, ba4), FINISHED);
    }
}