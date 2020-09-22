/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.fulladmin;

import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.helpers.policy.groupbenefits.PolicyHelper;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.tabs.GenerateDraftBillActionTab;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.BamConstants.*;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.INVOICE;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDraftBillGenerationFullAdmin extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-15745", "REN-15739"}, component = BILLING_GROUPBENEFITS)
    public void testDraftBillGenerationFullAdmin() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicyFullAdmin();

        navigateToBillingAccount(masterPolicyNumber.get());

        LOGGER.info("---=={Step 1}==---");
        billingAccount.generateDraftBill().start();
        String[] parsedTextArray = GenerateDraftBillActionTab.draftBillPopupPanel.getValue().split(" ");
        String dueDate = parsedTextArray[3];
        String periodStart = parsedTextArray[6];
        String periodEnd = parsedTextArray[8];
        Page.dialogConfirmation.buttonCancel.click();
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(BILLING_DRAFT_BILL_INITIATE, dueDate, periodStart, periodEnd), CANCELLED);

        LOGGER.info("---=={Step 2}==---");
        billingAccount.generateDraftBill().perform(new SimpleDataProvider());
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(BILLING_DRAFT_BILL_GENERATE, dueDate, periodStart, periodEnd), FINISHED);
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(BILLING_DRAFT_BILL_INITIATE, dueDate, periodStart, periodEnd), FINISHED);

        LOGGER.info("---=={Step 3}==---");
        billingAccount.generateDraftBill().start();
        assertThat(GenerateDraftBillActionTab.draftBillPopupPanel).hasValue(
                String.format("Statement cannot be generated as there is already generated Draft Bill for period %s - %s.", periodStart, periodEnd));
        Page.dialogConfirmation.buttonCancel.click();
        navigateToBillingAccount(masterPolicyNumber.get());
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(BILLING_DRAFT_BILL_INITIATE, dueDate, periodStart, periodEnd), CANCELLED);

        LOGGER.info("---=={Step 4}==---");
        MainPage.QuickSearch.search(masterPolicyNumber.get());
        groupAccidentMasterPolicy.createEndorsement(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_Endorsement").resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestDataWithExistingBA").resolveLinks()));

        MainPage.QuickSearch.search(certificatePolicyNumber.get());
        groupAccidentCertificatePolicy.createEndorsement(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData("TestPolicyEndorsementFlat", DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY).resolveLinks()));

        PolicyHelper.executeCascadingTransactionJobs(masterPolicyNumber.get());
        navigateToBillingAccount(masterPolicyNumber.get());

        LOGGER.info("---=={Step 5}==---");
        billingAccount.generateDraftBill().perform(new SimpleDataProvider());
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(BILLING_DRAFT_BILL_GENERATE, dueDate, periodStart, periodEnd), FINISHED);
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(BILLING_DRAFT_BILL_INITIATE, dueDate, periodStart, periodEnd), FINISHED);

        LOGGER.info("---=={Step 6}==---");
        billingAccount.generateFutureStatement().perform();
        String invoiceNum = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(INVOICE_SUCCESSFULLY_GENERATED, invoiceNum, dueDate, String.format("%s - %s", periodStart, periodEnd)), FINISHED);
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(INVOICE_GENERATE, invoiceNum, dueDate, String.format("%s - %s", periodStart, periodEnd)), FINISHED);

        LOGGER.info("---=={Step 7}==---");
        billingAccount.generateDraftBill().start();
        String[] parsedTextArray2 = GenerateDraftBillActionTab.draftBillPopupPanel.getValue().split(" ");
        String dueDate2 = parsedTextArray2[3];
        String periodStart2 = parsedTextArray2[6];
        String periodEnd2 = parsedTextArray2[8];
        Page.dialogConfirmation.confirm();
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(BILLING_DRAFT_BILL_GENERATE, dueDate2, periodStart2, periodEnd2), FINISHED);
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(BILLING_DRAFT_BILL_INITIATE, dueDate2, periodStart2, periodEnd2), FINISHED);

        LOGGER.info("---=={Step 8}==---");
        billingAccount.generateFutureStatement().perform();
        String invoiceNum2 = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(INVOICE_SUCCESSFULLY_GENERATED, invoiceNum2, dueDate2, String.format("%s - %s", periodStart2, periodEnd2)), FINISHED);
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(INVOICE_GENERATE, invoiceNum2, dueDate2, String.format("%s - %s", periodStart2, periodEnd2)), FINISHED);

        billingAccount.generateDraftBill().start();
        String[] parsedTextArray3 = GenerateDraftBillActionTab.draftBillPopupPanel.getValue().split(" ");
        String dueDate3 = parsedTextArray3[3];
        String periodStart3 = parsedTextArray3[6];
        String periodEnd3 = parsedTextArray3[8];
        Page.dialogConfirmation.buttonCancel.click();

        billingAccount.generateFutureStatement().perform();
        String invoiceNum3 = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(INVOICE_SUCCESSFULLY_GENERATED, invoiceNum3, dueDate3, String.format("%s - %s", periodStart3, periodEnd3)), FINISHED);
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(INVOICE_GENERATE, invoiceNum3, dueDate3, String.format("%s - %s", periodStart3, periodEnd3)), FINISHED);

        LOGGER.info("---=={Step 9}==---");
        billingAccount.generateDraftBill().start();
        String[] parsedTextArray4 = GenerateDraftBillActionTab.draftBillPopupPanel.getValue().split(" ");
        String dueDate4 = parsedTextArray4[3];
        String periodStart4 = parsedTextArray4[6];
        String periodEnd4 = parsedTextArray4[8];
        Page.dialogConfirmation.confirm();

        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(BILLING_DRAFT_BILL_GENERATE, dueDate4, periodStart4, periodEnd4), FINISHED);
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(BILLING_DRAFT_BILL_INITIATE, dueDate4, periodStart4, periodEnd4), FINISHED);
    }
}
