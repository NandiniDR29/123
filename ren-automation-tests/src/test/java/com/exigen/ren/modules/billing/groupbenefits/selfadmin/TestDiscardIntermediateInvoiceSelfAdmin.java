/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.helpers.billing.groupbenefits.BillingHelperGB;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.STATUS;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE;
import static com.exigen.ren.main.enums.BillingConstants.BillingGBBillsAndStatmentsTable.ACTION;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.DISCARDED_ESTIMATED;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL_ESTIMATED;
import static com.exigen.ren.main.modules.billing.account.pages.AdjustPremiumPage.buttonCancelBackUp;
import static com.exigen.ren.main.modules.billing.account.pages.AdjustPremiumPage.labelAmountBilled;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDiscardIntermediateInvoiceSelfAdmin extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20396", component = BILLING_GROUPBENEFITS)
    public void testDiscardInvoiceIncreaseModalPremium() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicySelfAdmin();

        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();

        MainPage.QuickSearch.search(masterPolicyNumber.get());

        groupAccidentMasterPolicy.createEndorsement(groupAccidentMasterPolicy.getDefaultTestData("Endorsement", "TestData")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData("Endorsement", "TestData_WithOtherRateWithTwoCoverage").resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestDataWithExistingBA").resolveLinks()));

        initModalPremiumValues();

        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();

        BillingSummaryPage.openBillsStatementsPeriodView();
        BillingSummaryPage.tableBillsAndStatementsByPeriod.getRow(2).getCell(ACTION).controls.links.get("Edit").click();
        assertThat(labelAmountBilled).hasValue(modalPremiumAmount.get().toString());

        buttonCancelBackUp.click();
        Page.dialogConfirmation.confirm();

        Currency modalPremiumAdjustment = new Currency(modalPremiumAmount.get().subtract(modalPremiumAmount.get()));
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash"),
                modalPremiumAmount.get().add(modalPremiumAmount.get()).add(modalPremiumAdjustment).toString());

        billingAccount.discardBill().perform(new SimpleDataProvider(), 1);
        assertThat(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(STATUS)).hasValue(DISCARDED_ESTIMATED);

        billingAccount.regenerateBill().perform(new SimpleDataProvider(), 1);

        assertThat(BillingSummaryPage.tableBillsAndStatements.getRow(1))
                .hasCellWithValue(STATUS, PAID_IN_FULL_ESTIMATED)
                .hasCellWithValue(TOTAL_DUE, modalPremiumAmount.get().add(modalPremiumAdjustment).toString());
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20396", component = BILLING_GROUPBENEFITS)
    public void testDiscardInvoiceWithSuspense() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicySelfAdminData());

        masterPolicyNumber.set(PolicySummaryPage.labelPolicyNumber.getValue());
        billingAccountNumber.set(getBillingAccountNumber(masterPolicyNumber.get()));
        policyPremium.set(PolicySummaryPage.TransactionHistory.getEndingPremium().divide(PolicySummaryPage.getRenewalFrequency(masterPolicyNumber.get()))); // in according with REN-40770
        modalPremiumAmount.set(BillingHelperGB.calculateModalPremiumAmount(12, policyPremium.get()));

        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();

        MainPage.QuickSearch.search(masterPolicyNumber.get());
        groupAccidentMasterPolicy.createEndorsement(groupAccidentMasterPolicy.getDefaultTestData("Endorsement", "TestData")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData("Endorsement", "TestData_WithOtherRate").resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestDataWithExistingBA").resolveLinks()));

        initModalPremiumValues();

        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();

        BillingSummaryPage.openBillsStatementsPeriodView();
        BillingSummaryPage.tableBillsAndStatementsByPeriod.getRow(2).getCell(ACTION).controls.links.get("Edit").click();
        assertThat(labelAmountBilled).hasValue(modalPremiumAmount.get().toString());

        buttonCancelBackUp.click();
        Page.dialogConfirmation.confirm();

        Currency modalPremiumAdjustment = new Currency(modalPremiumAmount.get().subtract(modalPremiumAmount.get()));
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Payment_Over_Amount"),
                modalPremiumAmount.get().add(modalPremiumAmount.get()).add(modalPremiumAdjustment).add(new Currency(10)).toString());

        billingAccount.discardBill().perform(new SimpleDataProvider(), 1);
        assertThat(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(STATUS)).hasValue(DISCARDED_ESTIMATED);

        billingAccount.regenerateBill().perform(new SimpleDataProvider(), 1);
        assertThat(BillingSummaryPage.tableBillsAndStatements.getRow(1))
                .hasCellWithValue(STATUS, PAID_IN_FULL_ESTIMATED)
                .hasCellWithValue(TOTAL_DUE, modalPremiumAmount.get().add(modalPremiumAdjustment).toString());
    }
}
