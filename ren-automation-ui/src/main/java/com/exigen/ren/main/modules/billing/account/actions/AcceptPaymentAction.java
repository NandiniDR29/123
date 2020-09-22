/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.actions;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.RadioButton;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.AcceptPaymentActionTab;
import org.openqa.selenium.By;

import java.util.List;
import java.util.Map;

public class AcceptPaymentAction implements Action {
    public static final TableExtended<PaymentMethods> paymentMethodsTable = new TableExtended<>("Payment Methods", By.xpath("//div[@id='paymentMethodListTable:j_id_2a_51']/div/table"));

    public static final TableExtended<SuspenseDetails> suspenseDetailsTable = new TableExtended<>("Suspense Details", By.xpath("//div[@id='paymentForm:suspenseItemList']/div/table"));

    private Workspace workspace = new Workspace.Builder()
            .registerTab(AcceptPaymentActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Accept Payment";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        new Link(By.linkText("Accept Payment")).click();
        return this;
    }

    public void start(TestData td) {
        new Link(By.linkText("Accept Payment")).click();
        AcceptPaymentActionTab.allocateExistingSuspense.fill(td);
        Tab.buttonCancel.click();
    }

    public Action perform(TestData td, String amount) {
        td.adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.AMOUNT.getLabel()), amount);

        //TODO(ymaliarevich): payment for two policies
        if(td.getValue(AcceptPaymentActionTab.class.getSimpleName(),
                AcceptPaymentActionTabMetaData.POLICY_TWO.getLabel()) != null) {
            td.adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(),
                    AcceptPaymentActionTabMetaData.POLICY_TWO.getLabel()), amount);
        }
        return perform(td);
    }

    public void perform(TestData td, String amount, List<String> allocations, String referenceNumber) {
        td.adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.AMOUNT.getLabel()), amount)
                .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.ALLOCATIONS.getLabel()),
                        allocations);
        perform(td);

        Table tableSuspendRemainingError = new Table(By.id("paymentForm:error_message"));
        if (tableSuspendRemainingError.isPresent() || getWorkspace().getTab(AcceptPaymentActionTab.class)
                .getAssetList().getAsset(AcceptPaymentActionTabMetaData.REFERENCE.getLabel()).getWarning().isPresent()) {
            new RadioButton(By.id("paymentForm:suspendRemainingAmount_radio:0")).setValue(true);
            new TextBox(By.id("paymentForm:cashReferenceNumber")).setValue(referenceNumber);
            submit();
        }
    }

    //Accept payment from Suspense
    public void perform(TestData td, List<String> allocations) {
        td.adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.ALLOCATIONS.getLabel()), allocations);
        perform(td);
    }

    //Manual Allocation to Billable items
    public void perform(TestData td, String amount, List<String> allocations, int tableAllocationsRowNumber,
                        int tableAllocationsIntoBGroupsRowNumber, Map<String, Currency> amountsByCoverage, String referenceNumber) {
        td.adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.AMOUNT.getLabel()), amount)
                .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.ALLOCATIONS.getLabel()),
                        allocations);
        perform(td);

        Table tableAllocations = new Table(By.xpath("//div[@id='paymentForm:invoicesDistributionsTable']/div/table"));
        if (tableAllocations.isPresent()) {

            tableAllocations.getRow(tableAllocationsRowNumber).getCell(BillingConstants.BillingAllocationsTable.INVOICE).controls.links.getFirst().click();
            new RadioButton(By.id("advAllocationForm:automaticDestination_radio:1")).setValue(true);
            Table tableAllocationsIntoBillingGroups = new Table(By.xpath("//div[@id='advAllocationForm:invoiceDistributionsTable_0']/div/table"));
            tableAllocationsIntoBillingGroups.getRow(tableAllocationsIntoBGroupsRowNumber).getCell(BillingConstants.BillingAllocationsIntoGroupsTable.BILLING_GROUP).controls.links.getFirst()
                    .click();

            fillManualAllocationsByCoverage(amountsByCoverage);

            submit();
            submit();
            submit();

            Table tableSuspendRemainingError = new Table(By.id("paymentForm:error_message"));
            if (tableSuspendRemainingError.isPresent()) {
                new RadioButton(By.id("paymentForm:suspendRemainingAmount_radio:0")).setValue(true);
                new TextBox(By.id("paymentForm:cashReferenceNumber")).setValue(referenceNumber);
                submit();
            }
        }
    }

    private void fillManualAllocationsByCoverage(Map<String, Currency> amountsByCoverage) {
        Table tablePaymentAllocation = new Table(By.xpath("//div[@id='advAllocationForm:invoice_items_info_table']/div/table"));
        for (Row row : tablePaymentAllocation.getRows()) {
            String coverageCode = row.getCell(BillingConstants.BillingPaymentAllocationTable.COVERAGE).getValue();
            Currency remainingDue = new Currency(row.getCell(BillingConstants.BillingPaymentAllocationTable.REMAINING_DUE).getValue());

            Currency amountToAllocate = amountsByCoverage.get(coverageCode);
            if (amountToAllocate == null || amountToAllocate.isZero()) {
                continue;
            }

            Currency allocatedAmount = remainingDue.min(amountToAllocate);
            row.getCell(BillingConstants.BillingPaymentAllocationTable.AMOUNT_PAID).controls.textBoxes.getFirst().setValue(allocatedAmount.toString());

            amountsByCoverage.put(coverageCode, amountToAllocate.subtract(allocatedAmount));

        }
    }

    @Override
    public Action perform(TestData td) {
        start();
        getWorkspace().fill(td);
        return submit();
    }

    @Override
    public Action submit() {
        AcceptPaymentActionTab.buttonOk.click();
        return this;
    }

    public enum PaymentMethods implements Named {
        PAYMENT_METHOD("Payment Method"),
        RECURRING_PAYMENTS_BILLING_ACCOUNTS("Recurring Payments: Billing Accounts"),
        EFFECTIVE_DATE("Effective Date"),
        EXPIRATION_DATE("Expiration Date"),
        ACTION("Action");

        private String name;

        PaymentMethods(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }


    public enum SuspenseDetails implements Named {
        CREATION_DATE("Creation Date"),
        PAYMENT_METHOD("Payment Method"),
        REFERENCE_NUMBER("Reference Number"),
        ADDITIONAL_INFORMATION("Additional Information"),
        CHECK_NUMBER("Check Number"),
        CHECK_DATE("Check Date"),
        CUSTOMER("Customer"),
        AMOUNT("Amount"),
        REMAINING_AMOUNT("Remaining Amount");

        private String name;

        SuspenseDetails(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
