/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.billing.global.write_off_reasons.WriteOffReasonsContext;
import com.exigen.ren.admin.modules.billing.global.write_off_reasons.pages.WriteOffReasonsPage;
import com.exigen.ren.admin.modules.billing.rules.write_off.benefits.WriteOffBenefitsContext;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.metadata.ReverseWriteOffActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.ReverseWriteOffActionTab;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.ISSUED_ESTIMATED;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL_ESTIMATED;
import static com.exigen.ren.main.enums.BillingConstants.PaymentsAndAdjustmentsStatusGB.*;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestWriteOffReverseReasonConfiguration extends GroupBenefitsBillingBaseTest implements BillingAccountContext, WriteOffReasonsContext, WriteOffBenefitsContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-16600", component = BILLING_GROUPBENEFITS)
    public void testWriteOffReverseReasonConfigurationTC1() {

        adminApp().open();
        writeOffReason.navigate();
        assertSoftly(softly -> {
            softly.assertThat(WriteOffReasonsPage.tableWriteOffReasonsPage.getRow(ImmutableMap.of(
                    WriteOffReasonsPage.WriteOffReasons.DESCRIPTION.getName(), "Other",
                    WriteOffReasonsPage.WriteOffReasons.CATEGORY.getName(), "Reverse Premium Write-Off"))).isPresent();

            softly.assertThat(WriteOffReasonsPage.tableWriteOffReasonsPage.getRow(ImmutableMap.of(
                    WriteOffReasonsPage.WriteOffReasons.DESCRIPTION.getName(), "Reversed due to Invoice Adjustment",
                    WriteOffReasonsPage.WriteOffReasons.CATEGORY.getName(), "Reverse Premium Write-Off"))).isPresent();
        });
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-16600", component = BILLING_GROUPBENEFITS)
    public void testWriteOffReverseReasonConfigurationTC2_3() {

        mainApp().reopen();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicySelfAdmin();

        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.generateFutureStatement().perform();
        String firstInvoice = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusMonths(1));
        mainApp().reopen();
        navigateToBillingAccount(masterPolicyNumber.get());

        assertSoftly(softly -> {
            LOGGER.info("---=={Step 1}==---");
            billingAccount.otherTransactions().perform(billingAccount.getDefaultTestData("OtherTransactions", "TestData_Adjustment"));
            billingAccount.paymentsAdjustmentsAction(ActionConstants.BillingPendingTransactionAction.APPROVE);
            billingAccount.allocationReverse().start();
            LOGGER.info("---=={Step 2}==---");
            billingAccount.allocationReverse().getWorkspace().fillUpTo(billingAccount.getDefaultTestData("AllocationReverse", DEFAULT_TEST_DATA_KEY), ReverseWriteOffActionTab.class, true);
            softly.assertThat(reverseWriteOffActionTab.getAssetList().getAsset(ReverseWriteOffActionTabMetaData.REASON)).hasOptions("", "Reversed due to Invoice Adjustment", "Other");
            billingAccount.allocationReverse().submit();

            LOGGER.info("---=={Step 3}==---");
            softly.assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1)
                    .getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS.getName())).valueContains(REVERSE);
            softly.assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1)
                    .getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS.getName())).valueContains(PENDING);

            billingAccount.paymentsAdjustmentsAction(ActionConstants.BillingPendingTransactionAction.APPROVE);
            softly.assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1)
                    .getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS.getName())).valueContains(REVERSED);
            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1)
                    .getCell(TableConstants.BillingPaymentsAndTransactionsGB.TYPE.getName())).hasValue("Reverse Premium Write-Off");
            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1)
                    .getCell(TableConstants.BillingPaymentsAndTransactionsGB.REASON.getName())).hasValue("Reversed due to Invoice Adjustment");

            LOGGER.info("---=={Step 4}==---");
            billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash_Invoice"), firstCoverageModalPremium.get().toString());
            billingAccount.otherTransactions().perform(billingAccount.getDefaultTestData("OtherTransactions", "TestData_Adjustment"));

            LOGGER.info("---=={Step 5}==---");
            billingAccount.paymentsAdjustmentsAction(ActionConstants.BillingPendingTransactionAction.APPROVE);
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements.getRow(ImmutableMap.of(TableConstants.BillingBillsAndStatementsGB.INVOICE.getName(), firstInvoice))
                    .getCell(BillingConstants.BillingBillsAndStatmentsTable.STATUS)).hasValue(PAID_IN_FULL_ESTIMATED);

            LOGGER.info("---=={Step 6}==---");
            billingAccount.unallocatePayment().start(2);
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED_ESTIMATED).containsMatchingRow(1);
            softly.assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS.getName())).valueContains(REVERSED);
            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1)
                    .getCell(TableConstants.BillingPaymentsAndTransactionsGB.TYPE.getName())).hasValue("Reverse Premium Write-Off");
            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1)
                    .getCell(TableConstants.BillingPaymentsAndTransactionsGB.REASON.getName())).hasValue("Reversed due to Invoice Adjustment");
        });
    }



}
