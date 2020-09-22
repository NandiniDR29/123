/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.actions;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.modules.billing.account.metadata.TransferPaymentBenefitsActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.TransferPaymentBenefitsActionTab;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import org.openqa.selenium.By;

public class TransferPaymentBenefitsAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(TransferPaymentBenefitsActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Transfer Payment";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    //FIXME(vmarkouski) - magic number
    @Override
    public Action start() {
        return start(1);
    }

    public Action start(int rowNumber) {
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(rowNumber).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.buttons.get("Transfer").click();
        return this;
    }

    public Action perform(TestData td, int rowNumber) {
        start(rowNumber);
        getWorkspace().fill(td);
        return submit();
    }

    public Action perform(TestData td, String policy, String transferAmount) {
        td.adjust(
                TestData.makeKeyPath(TransferPaymentBenefitsActionTab.class.getSimpleName(),
                        TransferPaymentBenefitsActionTabMetaData.BILLING_ACCOUNT.getLabel(),
                        TransferPaymentBenefitsActionTabMetaData.BillingAccountSingleSelector.POLICY_NUMBER.getLabel()),
                policy)
                .adjust(TestData.makeKeyPath(TransferPaymentBenefitsActionTab.class.getSimpleName(),
                        TransferPaymentBenefitsActionTabMetaData.TRANSFER_AMOUNT.getLabel()), transferAmount);
        return perform(td);
    }

    public void perform(int rowNumber, String billingAccountNumber, String transferAmount) {
        start(rowNumber);
        new TextBox(By.id("paymentForm:transferAmount")).setValue(transferAmount);
        transferPayment(billingAccountNumber);
    }

    public void perform(int rowNumber, String billingAccountNumber) {
        start(rowNumber);
        transferPayment(billingAccountNumber);
    }

    private void transferPayment(String billingAccountNumber) {
        new Button(By.id("paymentForm:showBillingAccountSearchPopupLnk")).click();
        new TextBox(By.id("searchBillingAccountsForm:accountNumber")).setValue(billingAccountNumber);
        new Button(By.id("searchBillingAccountsForm:searchBtn")).click();
        Table tableAcountSearchResults = new Table(By.xpath("//div[@id='searchBillingAccountsForm:accountSearchResults']//table"));
        tableAcountSearchResults.getRow(1).getCell(BillingConstants.BillingAccountsSearchResultTable.BILLING_ACCOUNT).controls.buttons.getFirst().click();
        TransferPaymentBenefitsActionTab.buttonOk.click();
    }

    @Override
    public Action submit() {
        new Button(By.xpath("//input[@id='paymentForm:okButton_footer']")).click();
        return this;
    }

}
