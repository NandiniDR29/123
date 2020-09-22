/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.BamConstants;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.AcceptPaymentActionTab;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.ISSUED_ESTIMATED;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL_ESTIMATED;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAcceptPaymentFromSuspenseSelfAdmin extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23909", component = BILLING_GROUPBENEFITS)
    public void testAcceptPaymentFromSuspenseSelfAdmin() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicySelfAdmin();

        navigateToBillingAccount(masterPolicyNumber.get());

        LOGGER.info("TEST: Generate Future Statement(Invoice 1) for Policy # " + masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();
        String firstInvoice = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();

        LOGGER.info("TEST: Create Cash Payment in BA Suspense for first billable item of Invoice 1 for Policy #" + masterPolicyNumber.get());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("SuspendePayment", "TestData_Cash_Suspense"), firstCoverageModalPremium.get().toString());

        LOGGER.info("TEST: Create Check Payment in BA Suspense for Policy #" + masterPolicyNumber.get());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("SuspendePayment", "TestData_Check_Suspense"));

        LOGGER.info("Verify that system did not perform any changes related to invoice payments and Suspense amount");
        billingAccount.acceptPayment().start(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash_Allocate_Existing_Suspense"));

        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow("Description", BamConstants.ACCEPT_PAYMENT_CANCELED)).hasCellWithValue("Status", BamConstants.CANCELLED);

        LOGGER.info("TEST: Accept Payment from suspense for first billable item of Invoice 1 for Policy #" + masterPolicyNumber.get());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash_Allocate_Existing_Suspense"));

        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow("Description", String.format(BamConstants.ACCEPT_PAYMENT_FINISHED,
                firstCoverageModalPremium.get().toString(), firstInvoice))).hasCellWithValue("Status", BamConstants.FINISHED);

        verifyPartialSuspensePaymentForFirstInvoice();

        LOGGER.info("TEST: Generate Future Statement(Invoice 2) for Policy # " + masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();
        String secondInvoice = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();

        LOGGER.info("TEST: Generate Future Statement(Invoice 3) for Policy # " + masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();
        String thirdInvoice = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();

        LOGGER.info("TEST: Generate Future Statement(Invoice 4) for Policy # " + masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("TEST: Create Cash Payment in BA Suspense for second billable itme of Invoice 1 + Invoice 2 + Invoice 3 for Policy #" + masterPolicyNumber.get());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("SuspendePayment", "TestData_Cash_Suspense_For_Three_Invoices"),
                secondCoverageModalPremium.get().add(modalPremiumAmount.get()).add(modalPremiumAmount.get()).toString());

        LOGGER.info("TEST: Accept Payment from suspense for second billable item of Invoice 1 + Invoice 2 + Invoice 3 for Policy #" + masterPolicyNumber.get());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash_Allocate_Existing_Suspense"));

        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow("Description", String.format(BamConstants.ACCEPT_PAYMENT_FINISHED,
                modalPremiumAmount.get().toString(), thirdInvoice))).hasCellWithValue("Status", BamConstants.FINISHED);

        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow("Description", String.format(BamConstants.ACCEPT_PAYMENT_FINISHED,
                modalPremiumAmount.get().toString(), secondInvoice))).hasCellWithValue("Status", BamConstants.FINISHED);

        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow("Description", String.format(BamConstants.ACCEPT_PAYMENT_FINISHED,
                secondCoverageModalPremium.get().toString(), firstInvoice))).hasCellWithValue("Status", BamConstants.FINISHED);

        verifySuspensePayment();

        LOGGER.info("TEST: Accept Payment for Invoice 4 for Policy #" + masterPolicyNumber.get());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash_Invoice"), modalPremiumAmount.get().toString());

        billingAccount.acceptPayment().start();

        assertThat(AcceptPaymentActionTab.allocateExistingSuspense).isEnabled();

        AcceptPaymentActionTab.allocateExistingSuspense.setValue(true);

        assertThat(AcceptPaymentActionTab.tableAllocations.getRow(1).getCell(AcceptPaymentActionTabMetaData.INVOICE.getLabel())).hasValue("No records found.");
    }

    private void verifyPartialSuspensePaymentForFirstInvoice() {
        assertSoftly(softly -> {
            softly.assertThat(BillingSummaryPage.tableBillingGeneralInformation)
                    .with(TableConstants.BillingGeneralInformationGB.TOTAL_PAID, firstCoverageModalPremium.get().toString())
                    .with(TableConstants.BillingGeneralInformationGB.TOTAL_DUE, secondCoverageModalPremium.get().toString())
                    .containsMatchingRow(1);

            softly.assertThat(BillingSummaryPage.tableBillsAndStatements)
                    .with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED_ESTIMATED)
                    .containsMatchingRow(1);

            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions)
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.POLICY_NUMBER, masterPolicyNumber.get())
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.TYPE, BillingConstants.PaymentsAndOtherTransactionTypeGB.PAYMENT)
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.AMOUNT, String.format("(%s)", firstCoverageModalPremium.get().toString()))
                    .containsMatchingRow(1);
        });
    }

    private void verifySuspensePayment() {
        assertSoftly(softly -> {
            softly.assertThat(BillingSummaryPage.tableBillingGeneralInformation)
                    .with(TableConstants.BillingGeneralInformationGB.TOTAL_PAID, modalPremiumAmount.get().add(modalPremiumAmount.get()).add(modalPremiumAmount.get()).toString())
                    .with(TableConstants.BillingGeneralInformationGB.TOTAL_DUE, modalPremiumAmount.get().toString())
                    .containsMatchingRow(1);

            softly.assertThat(BillingSummaryPage.tableBillsAndStatements)
                    .with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED_ESTIMATED)
                    .containsMatchingRow(1);

            softly.assertThat(BillingSummaryPage.tableBillsAndStatements)
                    .with(TableConstants.BillingBillsAndStatementsGB.STATUS, PAID_IN_FULL_ESTIMATED)
                    .containsMatchingRows(2, 4);

            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions)
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.POLICY_NUMBER, masterPolicyNumber.get())
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.TYPE, BillingConstants.PaymentsAndOtherTransactionTypeGB.PAYMENT)
                    .containsMatchingRow(1);
        });
    }
}
