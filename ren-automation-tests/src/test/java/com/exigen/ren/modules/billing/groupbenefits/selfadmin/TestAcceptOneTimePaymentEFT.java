/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.tabs.DeclinePaymentActionTab;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.BamConstants.ACCEPT_PAYMENT_TYPE;
import static com.exigen.ren.main.enums.BamConstants.FINISHED;
import static com.exigen.ren.main.enums.BillingConstants.PaymentsAndOtherTransactionTypeGB.PAYMENT;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData.PAYMENT_METHOD;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAcceptOneTimePaymentEFT extends GroupBenefitsBillingBaseTest implements BillingAccountContext {
    private final static String PAYMENT_METHOD_EFT = "EFT";
    private final static String PAYMENT_METHOD_CREDIT_CARD = "Credit Card";

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20956", component = BILLING_GROUPBENEFITS)
    public void testAcceptOneTimePaymentEFTSelf() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicySelfAdmin();

        commonSteps();
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20725", component = BILLING_GROUPBENEFITS)
    public void testAcceptSavedPaymentMethodEFTFull() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicyFullAdmin();

        commonSteps();
    }

    private void commonSteps() {
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.generateFutureStatement().perform();
        billingAccount.generateFutureStatement().perform();

        assertSoftly(softly -> {
            LOGGER.info("---=={Step 1}==---");
            billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "Payment_Methods_Credit_EFT"), modalPremiumAmount.get().toString());

            LOGGER.info("---=={Step 4.3}==---");
            BillingAccountsListPage.verifyBamActivities(String.format(ACCEPT_PAYMENT_TYPE, PAYMENT_METHOD_EFT, new Currency(modalPremiumAmount.get().toString())), FINISHED);

            LOGGER.info("---=={Step 4.5}==---");
            BillingSummaryPage.tablePaymentsOtherTransactions.getRow(ImmutableMap.of(TableConstants.BillingPaymentsAndTransactionsGB.TYPE.getName(),
                    PAYMENT)).getCell(TableConstants.BillingPaymentsAndTransactionsGB.TYPE.getName()).controls.links.getFirst().click();
            softly.assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(PAYMENT_METHOD.getLabel()))).hasValue(PAYMENT_METHOD_EFT);
            Tab.buttonBack.click();

            LOGGER.info("---=={Step 4.6}==---");
            billingAccount.declinePayment().start(1);
            softly.assertThat(DeclinePaymentActionTab.tablePaymentDetails).with(DeclinePaymentActionTab.PaymentDetails.PAYMENT_METHOD, PAYMENT_METHOD_EFT).hasMatchingRows(1);
            Tab.buttonCancel.click();

            LOGGER.info("---=={Step 6}==---");
            billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "Payment_Methods_Credit_Card"), modalPremiumAmount.get().multiply(2).toString());

            LOGGER.info("---=={Step 6.3}==---");
            BillingAccountsListPage.verifyBamActivities(String.format(ACCEPT_PAYMENT_TYPE, PAYMENT_METHOD_CREDIT_CARD, new Currency(modalPremiumAmount.get().multiply(2).toString())), FINISHED);

            LOGGER.info("---=={Step 6.5}==---");
            BillingSummaryPage.tablePaymentsOtherTransactions.getRow(TableConstants.BillingPaymentsAndTransactionsGB.TYPE.getName(), PAYMENT).getCell(TableConstants.BillingPaymentsAndTransactionsGB.TYPE.getName()).controls.links.getFirst().click();
            softly.assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(PAYMENT_METHOD.getLabel()))).hasValue(PAYMENT_METHOD_CREDIT_CARD);
            Tab.buttonBack.click();

            LOGGER.info("---=={Step 4.6}==---");
            billingAccount.declinePayment().start(1);
            softly.assertThat(DeclinePaymentActionTab.tablePaymentDetails).with(DeclinePaymentActionTab.PaymentDetails.PAYMENT_METHOD, PAYMENT_METHOD_CREDIT_CARD).hasMatchingRows(1);
            Tab.buttonCancel.click();
        });
    }
}