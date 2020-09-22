/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.metadata.PaymentsMaintenanceMetaData;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.AcceptPaymentActionTab;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.metadata.AddSuspenseActionTabMetaData;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.pages.AccountInformationPage;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.pages.ViewSuspensePage;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.tabs.AddSuspenseActionTab;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.tabs.InquirySuspenseActionTab;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.format.DateTimeFormatter;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL_ESTIMATED;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.billing.paymentsmaintenance.PaymentsMaintenanceContext.paymentsMaintenance;
import static com.exigen.ren.main.modules.billing.paymentsmaintenance.metadata.InquirySuspenseActionTabMetaData.SUSPENSE_REFERENCE;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestSuspenseReferenceSelfAdmin extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-24862", component = BILLING_GROUPBENEFITS)
    public void testDiscardBillSelfAdmin() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicySelfAdmin();
        String policyNumberMP1 = masterPolicyNumber.get();
        navigateToBillingAccount(policyNumberMP1);
        String numberBA1 = billingAccountNumber.get();
        String modalPrem =  modalPremiumAmount.get().toString();
        billingAccount.generateFutureStatement().perform();
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash_Invoice"), modalPremiumAmount.get().toString());
        assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.STATUS, PAID_IN_FULL_ESTIMATED).hasMatchingRows(1);

        createPolicyFullAdmin();
        String policyNumberMP2 = masterPolicyNumber.get();
        navigateToBillingAccount(policyNumberMP2);
        String numberBA2 = getBillingAccountNumber(policyNumberMP2);
        billingAccount.generateFutureStatement().perform();
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash_Invoice"), modalPremiumAmount.get().toString());
        assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.STATUS, PAID_IN_FULL).hasMatchingRows(1);
        navigateToBillingAccount(policyNumberMP1);

        LOGGER.info("---=={Step 1}==---");
        billingAccount.transferPaymentBenefits().perform(1, numberBA2,  modalPrem);

        paymentsMaintenance.viewSuspense().perform();

        String refNum = ViewSuspensePage.tableSuspense.getRow(ImmutableMap.of(ViewSuspensePage.SuspenseListTable.BILLING_ACCOUNTS.getName(), numberBA2))
                .getCell(ViewSuspensePage.SuspenseListTable.REFERENCE.getName()).getValue();
        checkReferenceFormat(refNum);
        assertThat(ViewSuspensePage.tableSuspense).with(ViewSuspensePage.SuspenseListTable.REFERENCE, refNum).hasMatchingRows(1);

        ViewSuspensePage.tableSuspense.getRow(ImmutableMap.of(ViewSuspensePage.SuspenseListTable.BILLING_ACCOUNTS.getName(), numberBA2))
                .getCell(ViewSuspensePage.SuspenseListTable.REFERENCE.getName()).controls.links.getFirst().click();

        assertSoftly(softly -> {
            softly.assertThat(paymentsMaintenance.viewSuspense().getWorkspace().getTab(InquirySuspenseActionTab.class).getAssetList().getAsset(SUSPENSE_REFERENCE).getValue()).isEqualTo(refNum);
            softly.assertThat(AccountInformationPage.tableAccountInformation.getRow(1).getCell(AccountInformationPage.AccountInformation.ASSOCIATED_AMOUNT.getName()).controls.textBoxes.getFirst().getValue())
                    .isEqualTo(modalPrem);
        });

        LOGGER.info("---=={Step 2}==---");
        navigateToBillingAccount(policyNumberMP1);

        paymentsMaintenance.addSuspense().start();
        String refNum2 =  paymentsMaintenance.addSuspense().getWorkspace().getTab(AddSuspenseActionTab.class)
                .getAssetList().getAsset(AddSuspenseActionTabMetaData.SUSPENSE_REFERENCE).getValue();
        checkReferenceFormat(refNum2);
        assertThat(refNum2).isNotEqualTo(refNum);
        String amount = new Currency("28763").toString();
        paymentsMaintenance.addSuspense().getWorkspace().getTab(AddSuspenseActionTab.class)
                .fillTab(paymentsMaintenance.getDefaultTestData("AddSuspense", "TestData_Payment Designation")
                        .adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.AddSuspenseActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.AddSuspenseActionTab.SUSPENSE_AMOUNT.getLabel()), amount));
        paymentsMaintenance.addSuspense().submit();

        paymentsMaintenance.viewSuspense().perform(paymentsMaintenance.getDefaultTestData("ViewSuspense", DEFAULT_TEST_DATA_KEY), refNum2);

        assertSoftly(softly -> {
            softly.assertThat(ViewSuspensePage.tableSuspense).with(ViewSuspensePage.SuspenseListTable.REFERENCE, refNum2).hasMatchingRows(1);

            softly.assertThat(ViewSuspensePage.tableSuspense)
                    .with(ViewSuspensePage.SuspenseListTable.REFERENCE, refNum2)
                    .with(ViewSuspensePage.SuspenseListTable.AMOUNT, amount).hasMatchingRows(1);
        });

        LOGGER.info("---=={Step 3}==---");
        Currency acceptPaymentAmount = new Currency(modalPrem).multiply(2);
        Currency suspenseAmount = new Currency(modalPrem);

        navigateToBillingAccount(policyNumberMP1);
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash")
                .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.AMOUNT.getLabel()), acceptPaymentAmount.toString()));
        billingAccount.acceptPayment().getWorkspace().getTab(AcceptPaymentActionTab.class).getAssetList().getAsset(AcceptPaymentActionTabMetaData.SUSPEND_REMAINING).setValue(VALUE_YES);
        String refNum3 =  billingAccount.acceptPayment().getWorkspace().getTab(AcceptPaymentActionTab.class).getAssetList().getAsset(AcceptPaymentActionTabMetaData.REFERENCE).getValue();
        checkReferenceFormat(refNum3);
        assertThat(refNum3).isNotIn(ImmutableList.of(refNum, refNum2));
        billingAccount.acceptPayment().submit();

        paymentsMaintenance.viewSuspense().perform(paymentsMaintenance.getDefaultTestData("ViewSuspense", DEFAULT_TEST_DATA_KEY), refNum3);

        assertSoftly(softly -> {
            softly.assertThat(ViewSuspensePage.tableSuspense).with(ViewSuspensePage.SuspenseListTable.REFERENCE, refNum3).hasMatchingRows(1);

            softly.assertThat(ViewSuspensePage.tableSuspense)
                    .with(ViewSuspensePage.SuspenseListTable.REFERENCE, refNum3)
                    .with(ViewSuspensePage.SuspenseListTable.AMOUNT, suspenseAmount.toString()).hasMatchingRows(1);
        });

        LOGGER.info("---=={Step 4}==---");
        navigateToBillingAccount(policyNumberMP1);
        billingAccount.unallocatePayment().start(2);

        paymentsMaintenance.viewSuspense().perform(paymentsMaintenance.getDefaultTestData("ViewSuspense", "TestDataAmount"), acceptPaymentAmount, acceptPaymentAmount);

        String refNum4 = ViewSuspensePage.tableSuspense.getRow(ImmutableMap.of(ViewSuspensePage.SuspenseListTable.BILLING_ACCOUNTS.getName(), numberBA1))
                .getCell(ViewSuspensePage.SuspenseListTable.REFERENCE.getName()).getValue();
        assertThat(refNum4).isEqualTo(refNum3);

        assertSoftly(softly -> {
            softly.assertThat(ViewSuspensePage.tableSuspense).with(ViewSuspensePage.SuspenseListTable.REFERENCE, refNum4).hasMatchingRows(1);
            softly.assertThat(ViewSuspensePage.tableSuspense)
                    .with(ViewSuspensePage.SuspenseListTable.REFERENCE, refNum4)
                    .with(ViewSuspensePage.SuspenseListTable.AMOUNT, acceptPaymentAmount.toString()).hasMatchingRows(1);
        });
    }

    private void checkReferenceFormat(String refNum){
        assertThat(refNum.split(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyMMdd")))[1].length()).isEqualTo(3);
   }


}
