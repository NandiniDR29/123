/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.fulladmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.helpers.billing.groupbenefits.BillingHelperGB;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB;
import com.exigen.ren.main.enums.TableConstants.BillingBillsAndStatementsGB;
import com.exigen.ren.main.enums.TableConstants.BillingGeneralInformationGB;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.helpers.billing.BillingHelper.ZERO;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPaymentsUnallocateActionFullAdmin extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    private String secondInvoiceNumber, thirdInvoiceNumber, fourthInvoiceNumber;
    private String referenceNumber = "RN" + RandomStringUtils.random(4, false, true);
    private Currency paymentAmount = new Currency(320);
    private Currency secondPaymentAmount = new Currency(200);
    private Currency thirdPaymentAmount = new Currency(80);
    private Currency fourthPaymentAmount = new Currency(0);
    private Currency fifthPaymentAmount = new Currency(50);
    private Currency sixthPaymentAmount = new Currency(150);
    private Currency paymentAmountToBillableItem = new Currency(100);
    private Currency secondPaymentAmountToBillableItem = new Currency(0);

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-21702", component = BILLING_GROUPBENEFITS)
    public void testPaymentsUnallocateActionFullAdmin() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicyFullAdminTwoCoveragesTwoCertificates();

        navigateToBillingAccount(masterPolicyNumber.get());

        LOGGER.info("TEST: Generate Future Statement for Billing Account #" + getBillingAccountNumber(masterPolicyNumber.get()));
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("TEST: Generate Second Future Statement for Billing Account #" + getBillingAccountNumber(masterPolicyNumber.get()));
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("TEST: Generate Third Future Statement for Billing Account #" + getBillingAccountNumber(masterPolicyNumber.get()));
        billingAccount.generateFutureStatement().perform();

        generateFutureStatementVerificationForFullAdmin();

        thirdInvoiceNumber = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();
        secondInvoiceNumber = BillingSummaryPage.tableBillsAndStatements.getRow(2).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();

        LOGGER.info("TEST: Accept Payment for Invoices #" + thirdInvoiceNumber + ", " + secondInvoiceNumber);
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash"),
                paymentAmount.toString(), Arrays.asList(fourthPaymentAmount.toString(), fifthPaymentAmount.toString(), sixthPaymentAmount.toString()), referenceNumber);

        acceptPaymentVerification();

        billingAccount.unallocatePayment().start(2);

        unallocatePaymentVerification();

        LOGGER.info("TEST: Discard Invoice #" + thirdInvoiceNumber);
        billingAccount.discardBill().perform(new SimpleDataProvider());

        LOGGER.info("TEST: Regenerate Invoice #" + thirdInvoiceNumber);
        billingAccount.regenerateBill().perform(new SimpleDataProvider());

        fourthInvoiceNumber = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();

        LOGGER.info("TEST: Allocate Payment to Invoice #" + fourthInvoiceNumber);
        //Allocate Payment to Billable items
        Map<String, Currency> amountsByCoverage = new HashMap<>();
        amountsByCoverage.put("BA", paymentAmountToBillableItem);

        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData(
                "AcceptPayment", "TestData_Cash"), secondPaymentAmount.toString(),
                Arrays.asList(secondPaymentAmountToBillableItem.toString(), paymentAmountToBillableItem.toString()),
                2, 1, amountsByCoverage, referenceNumber);

        LOGGER.info("TEST: Allocate Payment to Invoice #" + fourthInvoiceNumber);
        //Allocate Payment to Billable items
        Map<String, Currency> secondAmountsByCoverage = new HashMap<>();
        secondAmountsByCoverage.put("BTL", paymentAmountToBillableItem);

        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData(
                "AcceptPayment", "TestData_Cash"), secondPaymentAmount.toString(),
                Arrays.asList(secondPaymentAmountToBillableItem.toString(),
                        paymentAmountToBillableItem.toString()), 2, 1,
                secondAmountsByCoverage,
                referenceNumber);

        acceptPaymentToBillableItemsVerification();

        billingAccount.unallocatePayment().start(2);

        LOGGER.info("TEST: Generate Forth Future Statement for Billing Account #" + getBillingAccountNumber(masterPolicyNumber.get()));
        billingAccount.generateFutureStatement().perform();

        generateFourthFutureStatementVerification();

        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData(
                "AcceptPayment", "TestData_Cash"), thirdPaymentAmount.toString());

        acceptFourthPaymentVerification();
    }

    protected void generateFutureStatementVerificationForFullAdmin() {

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.ISSUED)
                .with(BillingBillsAndStatementsGB.CURRENT_DUE, modalPremiumAmount.get())
                .with(BillingBillsAndStatementsGB.PRIOR_DUE, modalPremiumAmount.get().add(modalPremiumAmount.get()))
                .with(BillingBillsAndStatementsGB.TOTAL_DUE, modalPremiumAmount.get().multiply(BillingHelperGB.calculateBillAndStatements()))
                .with(BillingBillsAndStatementsGB.UNALLOCATED_INVOICE_PREMIUM, ZERO)
                .containsMatchingRow(1);

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.ISSUED)
                .with(BillingBillsAndStatementsGB.CURRENT_DUE, modalPremiumAmount.get())
                .with(BillingBillsAndStatementsGB.PRIOR_DUE, modalPremiumAmount.get())
                .with(BillingBillsAndStatementsGB.TOTAL_DUE, modalPremiumAmount.get().add(modalPremiumAmount.get()))
                .with(BillingBillsAndStatementsGB.UNALLOCATED_INVOICE_PREMIUM, ZERO)
                .containsMatchingRow(2);

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.ISSUED)
                .with(BillingBillsAndStatementsGB.CURRENT_DUE, modalPremiumAmount.get())
                .with(BillingBillsAndStatementsGB.PRIOR_DUE, ZERO)
                .with(BillingBillsAndStatementsGB.TOTAL_DUE, modalPremiumAmount.get())
                .with(BillingBillsAndStatementsGB.UNALLOCATED_INVOICE_PREMIUM, ZERO)
                .containsMatchingRow(3);

        // Billing General Information
        assertThat(BillingSummaryPage.tableBillingGeneralInformation)
                .with(BillingGeneralInformationGB.SUSPENSE_AMOUNT, ZERO)
                .with(BillingGeneralInformationGB.CURRENT_DUE, modalPremiumAmount.get())
                .with(BillingGeneralInformationGB.PRIOR_DUE, modalPremiumAmount.get().add(modalPremiumAmount.get()))
                .with(BillingGeneralInformationGB.TOTAL_DUE, modalPremiumAmount.get().multiply(BillingHelperGB.calculateBillAndStatements()))
                .with(BillingGeneralInformationGB.TOTAL_PAID, ZERO)
                .with(BillingGeneralInformationGB.UNALLOCATED_INVOICE_PREMIUM, ZERO)
                .containsMatchingRow(1);
    }

    protected void discardInvoiceVerification() {

        // Billing General Information
        assertThat(BillingSummaryPage.tableBillingGeneralInformation)
                .with(BillingGeneralInformationGB.SUSPENSE_AMOUNT, new Currency(paymentAmount))
                .with(BillingGeneralInformationGB.CURRENT_DUE, modalPremiumAmount.get())
                .with(BillingGeneralInformationGB.PRIOR_DUE, (modalPremiumAmount.get().add(modalPremiumAmount.get())))
                .with(BillingGeneralInformationGB.TOTAL_DUE, modalPremiumAmount.get().multiply(BillingHelperGB.calculateBillAndStatements()))
                .with(BillingGeneralInformationGB.TOTAL_PAID, ZERO)
                .with(BillingGeneralInformationGB.UNALLOCATED_INVOICE_PREMIUM, ZERO)
                .containsMatchingRow(1);
    }

    protected void unallocatePaymentVerification() {

        // Billing General Information
        assertThat(BillingSummaryPage.tableBillingGeneralInformation)
                .with(BillingGeneralInformationGB.SUSPENSE_AMOUNT, new Currency(paymentAmount))
                .with(BillingGeneralInformationGB.CURRENT_DUE, modalPremiumAmount.get())
                .with(BillingGeneralInformationGB.PRIOR_DUE, (modalPremiumAmount.get().add(modalPremiumAmount.get())))
                .with(BillingGeneralInformationGB.TOTAL_DUE, modalPremiumAmount.get().multiply(BillingHelperGB.calculateBillAndStatements()))
                .with(BillingGeneralInformationGB.TOTAL_PAID, ZERO)
                .with(BillingGeneralInformationGB.UNALLOCATED_INVOICE_PREMIUM, ZERO)
                .containsMatchingRow(1);
    }

    protected void acceptPaymentVerification() {

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.ISSUED)
                .with(BillingBillsAndStatementsGB.CURRENT_DUE, modalPremiumAmount.get())
                .with(BillingBillsAndStatementsGB.PRIOR_DUE, modalPremiumAmount.get().add(modalPremiumAmount.get()))
                .with(BillingBillsAndStatementsGB.TOTAL_DUE, modalPremiumAmount.get().multiply(BillingHelperGB.calculateBillAndStatements()))
                .with(BillingBillsAndStatementsGB.UNALLOCATED_INVOICE_PREMIUM, modalPremiumAmount.get().divide(4))
                .containsMatchingRow(1);

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.ISSUED)
                .with(BillingBillsAndStatementsGB.CURRENT_DUE, modalPremiumAmount.get())
                .with(BillingBillsAndStatementsGB.PRIOR_DUE, modalPremiumAmount.get())
                .with(BillingBillsAndStatementsGB.TOTAL_DUE, modalPremiumAmount.get().add(modalPremiumAmount.get()))
                .with(BillingBillsAndStatementsGB.UNALLOCATED_INVOICE_PREMIUM, modalPremiumAmount.get().divide(4))
                .containsMatchingRow(2);

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.ISSUED)
                .with(BillingBillsAndStatementsGB.CURRENT_DUE, modalPremiumAmount.get())
                .with(BillingBillsAndStatementsGB.PRIOR_DUE, ZERO)
                .with(BillingBillsAndStatementsGB.TOTAL_DUE, modalPremiumAmount.get())
                .with(BillingBillsAndStatementsGB.UNALLOCATED_INVOICE_PREMIUM, ZERO)
                .containsMatchingRow(3);

        // Billing General Information
        assertThat(BillingSummaryPage.tableBillingGeneralInformation)
                .with(BillingGeneralInformationGB.SUSPENSE_AMOUNT, new Currency(paymentAmount).subtract(modalPremiumAmount.get()))
                .with(BillingGeneralInformationGB.CURRENT_DUE, modalPremiumAmount.get().divide(4))
                .with(BillingGeneralInformationGB.PRIOR_DUE, (modalPremiumAmount.get().add(modalPremiumAmount.get())).subtract(modalPremiumAmount.get().divide(4)))
                .with(BillingGeneralInformationGB.TOTAL_DUE, modalPremiumAmount.get().add(modalPremiumAmount.get()))
                .with(BillingGeneralInformationGB.TOTAL_PAID, modalPremiumAmount.get())
                .with(BillingGeneralInformationGB.UNALLOCATED_INVOICE_PREMIUM, modalPremiumAmount.get().divide(2))
                .containsMatchingRow(1);
    }

    protected void acceptPaymentToBillableItemsVerification() {

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.PAID_IN_FULL)
                .containsMatchingRow(1);

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.DISCARDED)
                .containsMatchingRow(2);

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.ISSUED)
                .with(BillingBillsAndStatementsGB.UNALLOCATED_INVOICE_PREMIUM, modalPremiumAmount.get().divide(10))
                .containsMatchingRow(3);

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.PAID_IN_FULL)
                .containsMatchingRow(4);

        // Billing General Information
        assertThat(BillingSummaryPage.tableBillingGeneralInformation)
                .with(BillingGeneralInformationGB.SUSPENSE_AMOUNT, modalPremiumAmount.get())
                .with(BillingGeneralInformationGB.CURRENT_DUE, ZERO)
                .with(BillingGeneralInformationGB.PRIOR_DUE, (modalPremiumAmount.get().divide(2)).subtract(modalPremiumAmount.get().divide(10)))
                .with(BillingGeneralInformationGB.TOTAL_DUE, (modalPremiumAmount.get().divide(2)).subtract(modalPremiumAmount.get().divide(10)))
                .with(BillingGeneralInformationGB.TOTAL_PAID, new Currency(520))
                .with(BillingGeneralInformationGB.UNALLOCATED_INVOICE_PREMIUM, modalPremiumAmount.get().divide(10))
                .containsMatchingRow(1);
    }

    protected void generateFourthFutureStatementVerification() {

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.ISSUED)
                .with(BillingBillsAndStatementsGB.UNALLOCATED_INVOICE_PREMIUM, modalPremiumAmount.get().divide(10))
                .containsMatchingRow(1);

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.PAID_IN_FULL)
                .containsMatchingRow(2);

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.DISCARDED)
                .containsMatchingRow(3);

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.PAID_IN_FULL)
                .containsMatchingRow(4);

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.PAID_IN_FULL)
                .containsMatchingRow(5);

        // Billing General Information
        assertThat(BillingSummaryPage.tableBillingGeneralInformation)
                .with(BillingGeneralInformationGB.SUSPENSE_AMOUNT, ZERO)
                .with(BillingGeneralInformationGB.CURRENT_DUE, (modalPremiumAmount.get().divide(2)).subtract(modalPremiumAmount.get().divide(10)))
                .with(BillingGeneralInformationGB.PRIOR_DUE, ZERO)
                .with(BillingGeneralInformationGB.TOTAL_DUE, (modalPremiumAmount.get().divide(2)).subtract(modalPremiumAmount.get().divide(10)))
                .with(BillingGeneralInformationGB.TOTAL_PAID, new Currency(720))
                .with(BillingGeneralInformationGB.UNALLOCATED_INVOICE_PREMIUM, modalPremiumAmount.get().divide(10))
                .containsMatchingRow(1);
    }

    protected void acceptFourthPaymentVerification() {

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.PAID_IN_FULL)
                .containsMatchingRow(1);

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.PAID_IN_FULL)
                .containsMatchingRow(2);

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.DISCARDED)
                .containsMatchingRow(3);

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.PAID_IN_FULL)
                .containsMatchingRow(4);

        // Bills & Statements
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(BillingBillsAndStatementsGB.STATUS, BillsAndStatementsStatusGB.PAID_IN_FULL)
                .containsMatchingRow(5);

        // Billing General Information
        assertThat(BillingSummaryPage.tableBillingGeneralInformation)
                .with(BillingGeneralInformationGB.SUSPENSE_AMOUNT, ZERO)
                .with(BillingGeneralInformationGB.CURRENT_DUE, ZERO)
                .with(BillingGeneralInformationGB.PRIOR_DUE, ZERO)
                .with(BillingGeneralInformationGB.TOTAL_DUE, ZERO)
                .with(BillingGeneralInformationGB.TOTAL_PAID, (modalPremiumAmount.get().multiply(BillingHelperGB.calculateBillAndStatements())
                        .subtract(modalPremiumAmount.get())))
                .with(BillingGeneralInformationGB.UNALLOCATED_INVOICE_PREMIUM, ZERO)
                .containsMatchingRow(1);

    }
}
