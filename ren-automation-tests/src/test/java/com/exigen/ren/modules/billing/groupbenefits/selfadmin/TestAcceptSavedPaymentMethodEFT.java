/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.actions.AcceptPaymentAction;
import com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.AcceptPaymentActionTab;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.BamConstants.*;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL_ESTIMATED;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData.*;
import static com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData.BillingMailingAddress.STATE_PROVINCE_COMBOBOX;
import static com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData.PAYMENT_METHOD;
import static com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData.ManagePaymentMethods.*;
import static com.exigen.ren.main.modules.billing.account.metadata.RefundActionTabMetaData.ManagePaymentMethods.ADD_PAYMENT_METHOD;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAcceptSavedPaymentMethodEFT extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20725", component = BILLING_GROUPBENEFITS)
    public void testAcceptSavedPaymentMethodEFTSelf() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicySelfAdmin();

        commonSteps();

        assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.STATUS, PAID_IN_FULL_ESTIMATED).containsMatchingRows(1, 2);
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20725", component = BILLING_GROUPBENEFITS)
    public void testAcceptSavedPaymentMethodEFTFull() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicyFullAdmin();

        commonSteps();
        assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.STATUS, PAID_IN_FULL_ESTIMATED).hasMatchingRows(1);
        assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.STATUS, PAID_IN_FULL).hasMatchingRows(1);
    }

    private void commonSteps(){
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.generateFutureStatement().perform();
        billingAccount.generateFutureStatement().perform();

        assertSoftly(softly -> {
            LOGGER.info("---=={Step 1}==---");
            billingAccount.acceptPayment().start();

            Tab acceptPaymentActionTab = billingAccount.acceptPayment().getWorkspace().getTab(AcceptPaymentActionTab.class);
            softly.assertThat(acceptPaymentActionTab.getAssetList().getAsset(PAYMENT_METHOD)).hasOptions(
                    ImmutableList.of("", "--------One Time Payment Methods--------", "Cash", "Check", "EFT", "Credit Card"));

            LOGGER.info("---=={Step 2}==---");
            AssetList paymentMethodsAssetList = acceptPaymentActionTab.getAssetList().getAsset(MANAGE_PAYMENT_METHODS);
            paymentMethodsAssetList.getAsset(ADD_PAYMENT_METHOD).click();

            softly.assertThat(paymentMethodsAssetList.getAsset(ManagePaymentMethods.PAYMENT_METHOD)).hasOptions("Credit Card", "EFT", "ACH");
            paymentMethodsAssetList.getAsset(ManagePaymentMethods.PAYMENT_METHOD).setValue("EFT");
            paymentMethodsAssetList.getAsset(ManagePaymentMethods.NAME_TYPE).setValue("Individual");
            ImmutableList.of(TRANSIT_NUMBER, ManagePaymentMethods.ACCOUNT_NUMBER, ManagePaymentMethods.BANK_NAME, ManagePaymentMethods.NAME_TYPE, FIRST_NAME, LAST_NAME)
                    .forEach(control -> softly.assertThat(paymentMethodsAssetList.getAsset(control)).isPresent().isRequired());
            ImmutableList.of(ManagePaymentMethods.PAYMENT_METHOD, PAYMENT_METHOD_EFFECTIVE_DATE, PAYMENT_METHOD_EXPIRATION_DATE, PREFIX, MIDDLE_NAME, DOES_THE_BANK_ACCOUNT_BELONG_TO_THE_INSURED)
                    .forEach(control -> softly.assertThat(paymentMethodsAssetList.getAsset(control)).isPresent().isOptional());

            paymentMethodsAssetList.getAsset(ManagePaymentMethods.NAME_TYPE).setValue("Non-Individual");
            ImmutableList.of(ManagePaymentMethods.NAME_TYPE, ManagePaymentMethods.NAME).forEach(control -> softly.assertThat(paymentMethodsAssetList.getAsset(control)).isPresent().isRequired());
            paymentMethodsAssetList.getAsset(DOES_THE_BANK_ACCOUNT_BELONG_TO_THE_INSURED).setValue("No");

            paymentMethodsAssetList.getAsset(PAYORS_NAME_TYPE).setValue("Individual");
            ImmutableList.of(PAYORS_NAME_TYPE, PAYORS_FIRST_NAME, PAYORS_LAST_NAME)
                    .forEach(control -> softly.assertThat(paymentMethodsAssetList.getAsset(control)).isPresent().isRequired());
            ImmutableList.of(PAYORS_PREFIX, PAYORS_MIDDLE_NAME)
                    .forEach(control -> softly.assertThat(paymentMethodsAssetList.getAsset(control)).isPresent().isOptional());

            paymentMethodsAssetList.getAsset(PAYORS_NAME_TYPE).setValue("Non-Individual");
            ImmutableList.of(PAYORS_NAME_TYPE, PAYORS_NAME).forEach(control -> softly.assertThat(paymentMethodsAssetList.getAsset(control)).isPresent().isRequired());

            paymentMethodsAssetList.getAsset(BILLING_MAILING_ADDRESS).getAsset(BillingMailingAddress.COUNTRY).setValue("");
            ImmutableList.of(BillingMailingAddress.COUNTRY, BillingMailingAddress.ADDRESS_LINE_1, BillingMailingAddress.CITY)
                    .forEach(control -> softly.assertThat(paymentMethodsAssetList.getAsset(BILLING_MAILING_ADDRESS).getAsset(control)).isPresent().isRequired());
            ImmutableList.of(BillingMailingAddress.ZIP_POSTAL_CODE, BillingMailingAddress.ADDRESS_LINE_2, BillingMailingAddress.ADDRESS_LINE_3, BillingMailingAddress.STATE_PROVINCE)
                    .forEach(control -> softly.assertThat(paymentMethodsAssetList.getAsset(BILLING_MAILING_ADDRESS).getAsset(control)).isPresent().isOptional());

            paymentMethodsAssetList.getAsset(BILLING_MAILING_ADDRESS).getAsset(BillingMailingAddress.COUNTRY).setValue("United States");
            ImmutableList.of(BillingMailingAddress.COUNTRY, BillingMailingAddress.ADDRESS_LINE_1, BillingMailingAddress.CITY, BillingMailingAddress.ZIP_POSTAL_CODE, STATE_PROVINCE_COMBOBOX)
                    .forEach(control -> softly.assertThat(paymentMethodsAssetList.getAsset(BILLING_MAILING_ADDRESS).getAsset(control)).isPresent().isRequired());
            ImmutableList.of(BillingMailingAddress.ADDRESS_LINE_2, BillingMailingAddress.ADDRESS_LINE_3)
                    .forEach(control -> softly.assertThat(paymentMethodsAssetList.getAsset(BILLING_MAILING_ADDRESS).getAsset(control)).isPresent().isOptional());

            acceptPaymentActionTab.fillTab(billingAccount.getDefaultTestData("AcceptPayment", "Manage_Payment_Methods_EFT"));
            String newPaymentMethod = AcceptPaymentAction.paymentMethodsTable.getRow(1).getCell(AcceptPaymentAction.PaymentMethods.PAYMENT_METHOD.getName()).getValue();
            softly.assertThat(newPaymentMethod).isNotEqualTo("No records found.");
            paymentMethodsAssetList.getAsset(BACK).click();

            LOGGER.info("---=={Step 4}==---");
            assertThat(acceptPaymentActionTab.getAssetList().getAsset(PAYMENT_METHOD)).hasOptions(
                    ImmutableList.of("", "--------Saved Payment Methods--------", newPaymentMethod, "--------One Time Payment Methods--------", "Cash", "Check", "EFT", "Credit Card"));
            Tab.buttonCancel.click();

            LOGGER.info("---=={Step 5}==---");
            billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash_Invoice")
                            .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.PAYMENT_METHOD.getLabel()), newPaymentMethod)
                            .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.SUSPEND_REMAINING.getLabel()), "Yes"),
                    modalPremiumAmount.get().multiply(2).toString(),
                    Arrays.asList(modalPremiumAmount.get().toString(), "0"), "test");

            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions)
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.TYPE, BillingConstants.PaymentsAndOtherTransactionTypeGB.PAYMENT)
                    .hasMatchingRows(1);

            BillingAccountsListPage.verifyBamActivities(String.format(ACCEPT_PAYMENT_TYPE, newPaymentMethod.split(" ")[0], new Currency(modalPremiumAmount.get().multiply(2).toString())), FINISHED);
            billingAccount.navigateToBillingAccountList();
            BillingAccountsListPage.verifyBamActivities(String.format(ACCEPT_PAYMENT_TYPE_ADDED, newPaymentMethod), FINISHED);

            LOGGER.info("---=={Step 6}==---");
            navigateToBillingAccount(masterPolicyNumber.get());
            BillingSummaryPage.tablePaymentsOtherTransactions.getRow(ImmutableMap.of(TableConstants.BillingPaymentsAndTransactionsGB.TYPE.getName(),
                    BillingConstants.PaymentsAndOtherTransactionTypeGB.PAYMENT)).getCell(TableConstants.BillingPaymentsAndTransactionsGB.TYPE.getName()).controls.links.getFirst().click();
            softly.assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(PAYMENT_METHOD.getLabel()))).hasValue(newPaymentMethod);
            Tab.buttonBack.click();

            billingAccount.acceptPayment().start();
            billingAccount.acceptPayment().getWorkspace().fill(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash_Allocate_Existing_Suspense"));

            softly.assertThat(AcceptPaymentAction.suspenseDetailsTable.getRow(1).getCell(AcceptPaymentAction.SuspenseDetails.PAYMENT_METHOD.getName()))
                    .hasValue("EFT");
            billingAccount.acceptPayment().submit();

        });
    }
}