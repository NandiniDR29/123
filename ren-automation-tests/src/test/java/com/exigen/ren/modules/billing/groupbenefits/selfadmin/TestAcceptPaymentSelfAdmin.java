/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL_ESTIMATED;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAcceptPaymentSelfAdmin extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    private Currency additionalModalPremiumAmount = new Currency("5");

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23870", component = BILLING_GROUPBENEFITS)
    public void testAcceptPaymentSelfAdmin() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicySelfAdmin();

        navigateToBillingAccount(masterPolicyNumber.get());

        LOGGER.info("TEST: Generate Future Statement(Invoice 1) for Policy # " + masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("TEST: Accept Payment of Invoice 1 for Policy #" + masterPolicyNumber.get());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData(
                "AcceptPayment", "TestData_Cash_Invoice"), modalPremiumAmount.get().toString());

        acceptedPaymentVerification();

        LOGGER.info("TEST: Generate Future Statement(Invoice 2) for Policy # " + masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("TEST: Generate Future Statement(Invoice 3) for Policy # " + masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("TEST: Generate Future Statement(Invoice 4) for Policy # " + masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("TEST: Accept Payment of Invoice 2 and first billable item of Invoice 3 for Policy # " + masterPolicyNumber.get());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData(
                "AcceptPayment", "TestData_Cash_Invoice"), modalPremiumAmount.get().add(firstCoverageModalPremium.get()).toString());

        verifyPaymentAcceptForSecondInvoice();

        LOGGER.info("TEST: Accept Payment of second billable item of Invoice 3 and Invoice 4 for Policy # " + masterPolicyNumber.get());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData(
                "AcceptPayment", "TestData_Payment_Over_Amount"), secondCoverageModalPremium.get().add(modalPremiumAmount.get()).add(additionalModalPremiumAmount).toString());

        verifyPaymentAcceptForOtherInvoices();
    }

    private void verifyPaymentAcceptForSecondInvoice() {
        assertSoftly(softly -> {
            softly.assertThat(BillingSummaryPage.tableBillingGeneralInformation)
                    .with(TableConstants.BillingGeneralInformationGB.TOTAL_PAID, modalPremiumAmount.get().add(modalPremiumAmount.get()).add(firstCoverageModalPremium.get()))
                    .containsMatchingRow(1);

            softly.assertThat(BillingSummaryPage.tableBillsAndStatements)
                    .with(TableConstants.BillingBillsAndStatementsGB.STATUS, PAID_IN_FULL_ESTIMATED)
                    .containsMatchingRow(3);

            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions)
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.POLICY_NUMBER, masterPolicyNumber.get())
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.TYPE, BillingConstants.PaymentsAndOtherTransactionTypeGB.PAYMENT)
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.AMOUNT, String.format("(%s)", modalPremiumAmount.get().add(firstCoverageModalPremium.get()).toString()))
                    .containsMatchingRow(1);
        });
    }

    private void verifyPaymentAcceptForOtherInvoices() {
        assertSoftly(softly -> {
            softly.assertThat(BillingSummaryPage.tableBillingGeneralInformation)
                    .with(TableConstants.BillingGeneralInformationGB.TOTAL_PAID, modalPremiumAmount.get().add(modalPremiumAmount.get()).add(modalPremiumAmount.get()).add(modalPremiumAmount.get()))
                    .containsMatchingRow(1);

            softly.assertThat(BillingSummaryPage.tableBillsAndStatements)
                    .with(TableConstants.BillingBillsAndStatementsGB.STATUS, PAID_IN_FULL_ESTIMATED)
                    .containsMatchingRows(1, 4);

            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions)
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.TYPE, BillingConstants.PaymentsAndOtherTransactionTypeGB.ACCOUNT_SUSPENSE)
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.AMOUNT, String.format("(%s)", additionalModalPremiumAmount.toString()))
                    .containsMatchingRow(1);

            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions)
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.POLICY_NUMBER, masterPolicyNumber.get())
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.TYPE, BillingConstants.PaymentsAndOtherTransactionTypeGB.PAYMENT)
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.AMOUNT, String.format("(%s)", modalPremiumAmount.get().add(secondCoverageModalPremium.get()).toString()))
                    .containsMatchingRow(2);
        });
    }
}
