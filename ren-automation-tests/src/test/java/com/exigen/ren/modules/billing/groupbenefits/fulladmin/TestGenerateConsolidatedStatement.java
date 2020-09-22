package com.exigen.ren.modules.billing.groupbenefits.fulladmin;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.billing.account.tabs.ConsolidatedStatementDetailsActionTab;
import com.exigen.ren.main.modules.billing.account.tabs.GenerateConsolidatedStatementActionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.common.pages.Page.dialogConfirmation;
import static com.exigen.ren.main.enums.ActionConstants.BillingAction.GENERATE_CONSOLIDATED_STATEMENT;
import static com.exigen.ren.main.enums.BamConstants.*;
import static com.exigen.ren.main.enums.TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT_NAME;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.CREATE_NEW_BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.ConsolidatedStatementDetailsTabMetaData.*;
import static com.exigen.ren.main.modules.billing.account.metadata.ConsolidatedStatementDetailsTabMetaData.AddBillingAccountsMetaData.BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.tabs.ConsolidatedStatementDetailsActionTab.tableRelatedBillingAccounts;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CertificatePolicyTabMetaData.BILLING_LOCATION;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.CONTRIBUTION_TYPE;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;


public class TestGenerateConsolidatedStatement extends GroupBenefitsBillingBaseTest implements BillingAccountContext {


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36495", component = BILLING_GROUPBENEFITS)
    public void testGenerateConsolidatedStatementFull() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        caseProfile.create(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY), groupAccidentMasterPolicy.getType());

        //MP1 (Coverages - COV1, COV2, Payment Mode is 12, calendar Monthly).
        TestData tdMP1 = groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_TwoCoverages")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "TestDataWithCustomCalendar"));

        tdMP1.adjust(TestData.makeKeyPath(BillingAccountTab.class.getSimpleName()), ImmutableList.of(
                groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "BillingAccountTabWithCustomCalendar")
                        .adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), BillingAccountTabMetaData.BillingAccountGeneralOptions.BILLING_ACCOUNT_NAME.getLabel()), "BA1"),
                groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "BillingAccountTabWithDefaultCalendar")
                        .adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), BillingAccountTabMetaData.BillingAccountGeneralOptions.BILLING_ACCOUNT_NAME.getLabel()), "BA2")));

        groupAccidentMasterPolicy.createPolicy(tdMP1);

        groupAccidentCertificatePolicy.createPolicyViaUI(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getClass().getSimpleName(), BILLING_LOCATION.getLabel()), "LOC1")
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));
        PolicySummaryPage.linkMasterPolicy.click();

        groupAccidentCertificatePolicy.createPolicy(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "CoveragesTab_PlanEnhanced").resolveLinks())
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getClass().getSimpleName(), BILLING_LOCATION.getLabel()), "LOC2")
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()));
        PolicySummaryPage.linkMasterPolicy.click();

        //MP2 (Coverages - COV3, Payment Mode is 12, calendar Monthly).
        TestData tdMP2 = groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "TestDataWithCustomCalendar"))
                .adjust(TestData.makeKeyPath(BillingAccountTab.class.getSimpleName()), ImmutableList.of(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "BillingAccountTabWithCustomCalendar")
                        .adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), BillingAccountTabMetaData.BillingAccountGeneralOptions.BILLING_ACCOUNT_NAME.getLabel()), "BA3")));

        groupAccidentMasterPolicy.createPolicy(tdMP2);

        groupAccidentCertificatePolicy.createPolicyViaUI(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "CoveragesTab_PlanEnhanced").resolveLinks())
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getClass().getSimpleName(), BILLING_LOCATION.getLabel()), "LOC3")
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));
        PolicySummaryPage.linkMasterPolicy.click();

        commonSteps();
    }


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36495", component = BILLING_GROUPBENEFITS)
    public void testGenerateConsolidatedStatementSelf() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        caseProfile.create(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY), groupAccidentMasterPolicy.getType());

        //MP1 (Coverages - COV1, COV2, Payment Mode is 12, calendar Monthly).
        TestData tdMP1 = groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER_SELF_ADMIN, "TestData_WithTwoCoverages")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "TestDataWithCustomCalendar"));

        tdMP1.adjust(TestData.makeKeyPath(BillingAccountTab.class.getSimpleName()), ImmutableList.of(
                groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "BillingAccountTabWithCustomCalendar")
                        .adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), BillingAccountTabMetaData.BillingAccountGeneralOptions.BILLING_ACCOUNT_NAME.getLabel()), "BA1"),
                groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "BillingAccountTabWithDefaultCalendar")
                        .adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), BillingAccountTabMetaData.BillingAccountGeneralOptions.BILLING_ACCOUNT_NAME.getLabel()), "BA2")));

        tdMP1.getTestDataList(planDefinitionTab.getClass().getSimpleName()).get(1)
                .adjust(TestData.makeKeyPath(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Non-contributory");

        groupAccidentMasterPolicy.createPolicy(tdMP1);

        //MP2 (Coverages - COV3, Payment Mode is 12, calendar Monthly).
        TestData tdMP2 = groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER_SELF_ADMIN, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "TestDataWithCustomCalendar"))
                .adjust(TestData.makeKeyPath(BillingAccountTab.class.getSimpleName()), ImmutableList.of(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "BillingAccountTabWithCustomCalendar")
                        .adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), BillingAccountTabMetaData.BillingAccountGeneralOptions.BILLING_ACCOUNT_NAME.getLabel()), "BA3")));

        groupAccidentMasterPolicy.createPolicy(tdMP2);
        commonSteps();
    }

    private void commonSteps() {
        billingAccount.navigateToBillingAccountList();
        String ba1 = BillingAccountsListPage.tableBenefitAccounts.getRow(ImmutableMap.of(BILLING_ACCOUNT_NAME.getName(), "BA1")).getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).getValue();
        String ba2 = BillingAccountsListPage.tableBenefitAccounts.getRow(ImmutableMap.of(BILLING_ACCOUNT_NAME.getName(), "BA2")).getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).getValue();
        String ba3 = BillingAccountsListPage.tableBenefitAccounts.getRow(ImmutableMap.of(BILLING_ACCOUNT_NAME.getName(), "BA3")).getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).getValue();

        LOGGER.info("---=={Step 1}==---");
        assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(GENERATE_CONSOLIDATED_STATEMENT);

        LOGGER.info("---=={Step 2}==---");
        billingAccount.setUpConsolidatedStatement().start();
        AbstractContainer<?, ?> consolidatedStatement = billingAccount.setUpConsolidatedStatement().getWorkspace().getTab(ConsolidatedStatementDetailsActionTab.class).getAssetList();
        consolidatedStatement.getAsset(CONSOLIDATED_STATEMENT_ENABLED_FROM).setValueContains(ba1);
        consolidatedStatement.getAsset(ADD_BUTTOM).click();
        tableRelatedBillingAccounts.getRow(ImmutableMap.of(TableConstants.RelatedBillingAccounts.BILLING_ACCOUNT.getName(), ba2)).getCell(1).click();
        consolidatedStatement.getAsset(ADD_BILLING_ACCOUNTS).getAsset(BILLING_ACCOUNT).setValue(ba3);
        ConsolidatedStatementDetailsActionTab.buttonSearch.click();
        ConsolidatedStatementDetailsActionTab.tableSearchResults.getRow(ImmutableMap.of(TableConstants.RelatedBillingAccounts.BILLING_ACCOUNT.getName(), ba3)).getCell(1).click();
        ConsolidatedStatementDetailsActionTab.buttonSelect.click();
        billingAccount.setUpConsolidatedStatement().submit();

        LOGGER.info("---=={Step 3}==---");
        MainPage.QuickSearch.search(ba1);
        billingAccount.generateConsolidatedStatement().start();
        String currentSystemDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);

        assertSoftly(softly -> {
            assertThat(GenerateConsolidatedStatementActionTab.consolidateStatementConfirmDialog).hasValue(String.format("Consolidated Statement will be generated for a date %s and include the following Billing Accounts:", currentSystemDate));
            assertThat(GenerateConsolidatedStatementActionTab.consolidateStatementBillingAccounts).valueContains(ba2);
            assertThat(GenerateConsolidatedStatementActionTab.consolidateStatementBillingAccounts).valueContains(ba3);
        });

        dialogConfirmation.buttonCancel.click();
        assertThat(Efolder.isDocumentExist("Invoices and Bills", "BILLING_GROUP_CONSOLIDATED_BILL_LB.pdf")).withFailMessage("File found in folder").isFalse();

        LOGGER.info("---=={Step 4}==---");
        BillingAccountsListPage.verifyBamActivities(String.format(CONSOLIDATED_STATEMENT_BA_NUM_IS_INITIATED, ba1), CANCELLED);

        LOGGER.info("---=={Step 5}==---");
        billingAccount.generateConsolidatedStatement().perform();
        mainApp().reopen();
        MainPage.QuickSearch.search(ba1);
        assertThat(Efolder.isDocumentExistStartsContains("Invoices and Bills", "-Billing Statement", "pdf")).withFailMessage("File not found in folder").isTrue();

        LOGGER.info("---=={Step 6}==---");
        BillingAccountsListPage.verifyBamActivities(String.format(CONSOLIDATED_STATEMENT_BA_NUM_IS_INITIATED, ba1), FINISHED);
        BillingAccountsListPage.verifyBamActivities(String.format(CONSOLIDATED_STATEMENT_BA_NUM_IS_GENERATED, ba1), FINISHED);
    }
}